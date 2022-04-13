package com.skeletonloading.info.skeleton_loading.target

import android.widget.TextView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.skeletonloading.info.skeleton_loading.custom.KoletonView

/** A [Target] that handles setting skeleton on a [TextView]. */
open class TextViewTarget(override val view: TextView) : ViewTarget<TextView>, DefaultLifecycleObserver {

    override fun onStart() = Unit

    /** Show the [skeleton] view */
    override fun onSuccess(skeleton: KoletonView) = skeleton.showSkeleton()

    override fun onError() = Unit

    override fun onStart(owner: LifecycleOwner) {}

    override fun onStop(owner: LifecycleOwner) {}
}