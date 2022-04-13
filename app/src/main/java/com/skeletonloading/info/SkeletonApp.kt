package com.skeletonloading.info

import android.app.Application
import com.facebook.shimmer.Shimmer
import com.skeletonloading.info.skeleton_loading.SkeletonLoader
import com.skeletonloading.info.skeleton_loading.SkeletonLoaderFactory
import com.skeletonloading.info.skeleton_loading.util.ITEM_COUNT

//class SkeletonApp : Application(), SkeletonLoaderFactory {
//
//    override fun newSkeletonLoader(): SkeletonLoader {
//        return SkeletonLoader.Builder(this)
//            .color(R.color.colorSkeleton)
//            .cornerRadius(resources.getDimension(R.dimen.default_corner_radius))
//            .lineSpacing(resources.getDimension(R.dimen.default_line_spacing))
//            .itemCount(ITEM_COUNT)
//            .shimmer(true)
//            .shimmer(getCustomShimmer())
//            .build()
//    }
//
//    private fun getCustomShimmer(): Shimmer {
//        return Shimmer.AlphaHighlightBuilder()
//            .setDuration(1000)
//            .setBaseAlpha(0.5f)
//            .setHighlightAlpha(0.9f)
//            .setWidthRatio(1f)
//            .setHeightRatio(1f)
//            .setDropoff(1f)
//            .build()
//    }
//}