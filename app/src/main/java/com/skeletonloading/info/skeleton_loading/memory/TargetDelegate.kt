@file:OptIn(ExperimentalKoletonApi::class)

package com.skeletonloading.info.skeleton_loading.memory

import androidx.annotation.MainThread
import com.skeletonloading.info.skeleton_loading.annotation.ExperimentalKoletonApi
import com.skeletonloading.info.skeleton_loading.custom.KoletonView
import com.skeletonloading.info.skeleton_loading.skeleton.Skeleton
import com.skeletonloading.info.skeleton_loading.target.Target

internal sealed class TargetDelegate {

    @MainThread
    open fun start() {}

    @MainThread
    open fun success(skeleton: KoletonView) {}

    @MainThread
    open fun error() {}

    @MainThread
    open fun clear() {}
}

internal class ViewTargetDelegate(
    private val skeleton: Skeleton,
    private val target: Target?
) : TargetDelegate() {

    override fun start() {
        target?.onStart()
    }

    override fun success(skeleton: KoletonView) {
        target?.onSuccess(skeleton)
    }

    override fun error() {
        target?.onError()
    }

    override fun clear() {}
}