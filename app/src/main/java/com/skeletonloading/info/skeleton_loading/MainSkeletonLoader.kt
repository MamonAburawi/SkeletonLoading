package com.skeletonloading.info.skeleton_loading

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.LifecycleObserver
import com.facebook.shimmer.ShimmerFrameLayout
import com.skeletonloading.info.skeleton_loading.annotation.ExperimentalKoletonApi
import com.skeletonloading.info.skeleton_loading.custom.*
import com.skeletonloading.info.skeleton_loading.custom.RecyclerKoletonView
import com.skeletonloading.info.skeleton_loading.memory.DelegateService
import com.skeletonloading.info.skeleton_loading.memory.SkeletonService
import com.skeletonloading.info.skeleton_loading.skeleton.RecyclerViewSkeleton
import com.skeletonloading.info.skeleton_loading.skeleton.Skeleton
import com.skeletonloading.info.skeleton_loading.skeleton.TextViewSkeleton
import com.skeletonloading.info.skeleton_loading.skeleton.ViewSkeleton
import com.skeletonloading.info.skeleton_loading.target.RecyclerViewTarget
import com.skeletonloading.info.skeleton_loading.target.SimpleViewTarget
import com.skeletonloading.info.skeleton_loading.target.TextViewTarget
import com.skeletonloading.info.skeleton_loading.target.ViewTarget
import com.skeletonloading.info.skeleton_loading.util.*
import com.skeletonloading.info.skeleton_loading.util.generateRecyclerKoletonView
import com.skeletonloading.info.skeleton_loading.util.generateSimpleKoletonView
import com.skeletonloading.info.skeleton_loading.util.generateTextKoletonView
import com.skeletonloading.info.skeleton_loading.util.getParentViewGroup
import kotlinx.coroutines.*

@OptIn(ExperimentalKoletonApi::class)
internal class MainSkeletonLoader(
    private val context: Context,
    override val defaults: DefaultSkeletonOptions
) : SkeletonLoader {

    companion object {
        private const val TAG = "MainSkeletonLoader"
    }

    private val loaderScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, throwable.message.orEmpty())
    }

    private val delegateService = DelegateService(this)

    override fun load(skeleton: Skeleton) {
        val job = loaderScope.launch(exceptionHandler) { loadInternal(skeleton) }
        val target = skeleton.target as? ViewTarget<*>
        target?.view?.koletonManager?.setCurrentSkeletonJob(job)
    }

    override fun generate(skeleton: Skeleton): KoletonView {
        return generateKoletonView(skeleton)
    }

    private suspend fun loadInternal(skeleton: Skeleton) =
        withContext(Dispatchers.Main.immediate) {
            val (lifecycle, mainDispatcher) = SkeletonService().lifecycleInfo(skeleton)
            val targetDelegate = delegateService.createTargetDelegate(skeleton)
            val deferred = async(mainDispatcher, CoroutineStart.LAZY) {
                val target = skeleton.target
                if (target is ViewTarget<*> && target is LifecycleObserver) {
                    lifecycle.addObserver(target)
                    with(target.view) {
                        if (parent !is KoletonView && isMeasured()) {
                            val koletonView = generateKoletonView(skeleton)
                            koletonManager.setCurrentKoletonView(koletonView)
                            targetDelegate.success(koletonView)
                        }
                    }
                }
            }
            val skeletonDelegate = delegateService.createSkeletonDelegate(
                skeleton,
                targetDelegate,
                lifecycle,
                mainDispatcher,
                deferred
            )
            deferred.invokeOnCompletion { throwable ->
                loaderScope.launch(Dispatchers.Main.immediate) { skeletonDelegate?.onComplete() }
            }
            deferred.await()
        }

    override fun hide(view: View, koletonView: KoletonView) {
        koletonView.hideSkeleton()
        val skeletonView = koletonView as ShimmerFrameLayout
        val originalParams = skeletonView.layoutParams
        val originalParent = skeletonView.getParentViewGroup()
        skeletonView.removeView(view)
        originalParent.removeView(skeletonView)
        view.cloneTranslations(skeletonView)
        originalParent.addView(view, originalParams)
    }

    private fun generateKoletonView(skeleton: Skeleton): KoletonView {
        return when (skeleton) {
            is RecyclerViewSkeleton -> generateRecyclerView(skeleton)
            is ViewSkeleton -> generateSimpleView(skeleton)
            is TextViewSkeleton -> generateTextView(skeleton)
        }
    }

    private fun generateTextView(skeleton: TextViewSkeleton) = with(skeleton) {
        return@with if (target is TextViewTarget) {
            val attributes = TextViewAttributes(
                    view = target.view,
                    color = color ?: defaults.color,
                    cornerRadius = cornerRadius ?: defaults.cornerRadius,
                    isShimmerEnabled = isShimmerEnabled ?: defaults.isShimmerEnabled,
                    shimmer = shimmer ?: defaults.shimmer,
                    lineSpacing = lineSpacing ?: defaults.lineSpacing,
                    length = length
            )
            target.view.generateTextKoletonView(attributes)
        } else {
            TextKoletonView(context)
        }
    }

    private fun generateRecyclerView(skeleton: RecyclerViewSkeleton) = with(skeleton) {
        return@with if (target is RecyclerViewTarget) {
            val attributes = RecyclerViewAttributes(
                view = target.view,
                color = color ?: defaults.color,
                cornerRadius = cornerRadius ?: defaults.cornerRadius,
                isShimmerEnabled = isShimmerEnabled ?: defaults.isShimmerEnabled,
                shimmer = shimmer ?: defaults.shimmer,
                lineSpacing = lineSpacing ?: defaults.lineSpacing,
                itemLayout = itemLayoutResId,
                itemCount = itemCount ?: defaults.itemCount
            )
            target.view.generateRecyclerKoletonView(attributes)
        } else {
            RecyclerKoletonView(context)
        }
    }

    private fun generateSimpleView(skeleton: ViewSkeleton) = with(skeleton) {
        return@with if (target is SimpleViewTarget) {
            val attributes = SimpleViewAttributes(
                color = color ?: defaults.color,
                cornerRadius = cornerRadius ?: defaults.cornerRadius,
                isShimmerEnabled = isShimmerEnabled ?: defaults.isShimmerEnabled,
                shimmer = shimmer ?: defaults.shimmer,
                lineSpacing = lineSpacing ?: defaults.lineSpacing
            )
            target.view.generateSimpleKoletonView(attributes)
        } else {
            SimpleKoletonView(context)
        }
    }
}