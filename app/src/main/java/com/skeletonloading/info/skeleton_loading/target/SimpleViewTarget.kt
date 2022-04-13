package com.skeletonloading.info.skeleton_loading.target

import android.view.View
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.skeletonloading.info.skeleton_loading.custom.KoletonView

/** A [Target] that handles setting skeleton on a [View]. */
open class SimpleViewTarget(override val view: View) : ViewTarget<View>, DefaultLifecycleObserver {

    override fun onStart() = Unit

    /** Show the [skeleton] view */
    override fun onSuccess(skeleton: KoletonView) = skeleton.showSkeleton()

    override fun onError() = Unit

    override fun onStart(owner: LifecycleOwner) {}

    override fun onStop(owner: LifecycleOwner) {}
}