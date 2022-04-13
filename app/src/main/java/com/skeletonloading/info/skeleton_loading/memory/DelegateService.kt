package com.skeletonloading.info.skeleton_loading.memory

import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import com.skeletonloading.info.skeleton_loading.SkeletonLoader
import com.skeletonloading.info.skeleton_loading.annotation.ExperimentalKoletonApi
import com.skeletonloading.info.skeleton_loading.skeleton.RecyclerViewSkeleton
import com.skeletonloading.info.skeleton_loading.skeleton.Skeleton
import com.skeletonloading.info.skeleton_loading.skeleton.TextViewSkeleton
import com.skeletonloading.info.skeleton_loading.skeleton.ViewSkeleton
import com.skeletonloading.info.skeleton_loading.target.ViewTarget
import com.skeletonloading.info.skeleton_loading.util.koletonManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred

@OptIn(ExperimentalKoletonApi::class)
internal class DelegateService(
    private val imageLoader: SkeletonLoader
) {

    fun createTargetDelegate(
        skeleton: Skeleton
    ): TargetDelegate {
        return ViewTargetDelegate(skeleton, skeleton.target)
    }

    @MainThread
    fun createSkeletonDelegate(
        skeleton: Skeleton,
        targetDelegate: TargetDelegate,
        lifecycle: Lifecycle,
        mainDispatcher: CoroutineDispatcher,
        deferred: Deferred<*>
    ): SkeletonDelegate? {
        val skeletonDelegate: SkeletonDelegate
        when (skeleton) {
            is ViewSkeleton, is RecyclerViewSkeleton, is TextViewSkeleton -> when (val target = skeleton.target) {
                is ViewTarget<*> -> {
                    skeletonDelegate = ViewTargetSkeletonDelegate(
                        imageLoader = imageLoader,
                        skeleton = skeleton,
                        target = targetDelegate,
                        lifecycle = lifecycle,
                        dispatcher = mainDispatcher,
                        job = deferred
                    )
                    lifecycle.addObserver(skeletonDelegate)
                    target.view.koletonManager.setCurrentSkeleton(skeletonDelegate)
                }
                else -> {
                    skeletonDelegate = BaseRequestDelegate(lifecycle, mainDispatcher, deferred)
                    lifecycle.addObserver(skeletonDelegate)
                }
            }
        }
        return skeletonDelegate
    }
}
