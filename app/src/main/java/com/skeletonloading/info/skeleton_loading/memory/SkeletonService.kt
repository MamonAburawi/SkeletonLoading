package com.skeletonloading.info.skeleton_loading.memory

import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import com.skeletonloading.info.skeleton_loading.lifecycle.GlobalLifecycle
import com.skeletonloading.info.skeleton_loading.lifecycle.LifecycleCoroutineDispatcher
import com.skeletonloading.info.skeleton_loading.skeleton.RecyclerViewSkeleton
import com.skeletonloading.info.skeleton_loading.skeleton.Skeleton
import com.skeletonloading.info.skeleton_loading.skeleton.TextViewSkeleton
import com.skeletonloading.info.skeleton_loading.skeleton.ViewSkeleton
import com.skeletonloading.info.skeleton_loading.target.Target
import com.skeletonloading.info.skeleton_loading.target.ViewTarget
import com.skeletonloading.info.skeleton_loading.util.getLifecycle
import com.skeletonloading.info.skeleton_loading.util.isNotNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal class SkeletonService {

    @MainThread
    fun lifecycleInfo(skeleton: Skeleton): LifecycleInfo {
        when (skeleton) {
            is ViewSkeleton, is RecyclerViewSkeleton, is TextViewSkeleton -> {
                val lifecycle = skeleton.getLifecycle()
                return if (lifecycle != null) {
                    val mainDispatcher = LifecycleCoroutineDispatcher
                        .createUnlessStarted(Dispatchers.Main.immediate, lifecycle)
                    LifecycleInfo(lifecycle, mainDispatcher)
                } else {
                    LifecycleInfo.GLOBAL
                }
            }
        }
    }

    private fun Skeleton.getLifecycle(): Lifecycle? {
        return when {
            lifecycle.isNotNull() -> lifecycle
            this is ViewSkeleton || this is RecyclerViewSkeleton -> target?.getLifecycle()
            else -> context.getLifecycle()
        }
    }

    private fun Target.getLifecycle(): Lifecycle? {
        return (this as? ViewTarget<*>)?.view?.context?.getLifecycle()
    }

    data class LifecycleInfo(
        val lifecycle: Lifecycle,
        val mainDispatcher: CoroutineDispatcher
    ) {

        companion object {
            val GLOBAL = LifecycleInfo(
                lifecycle = GlobalLifecycle,
                mainDispatcher = Dispatchers.Main.immediate
            )
        }
    }
}