package com.skeletonloading.info


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.skeletonloading.info.databinding.ActivityMainBinding
import com.skeletonloading.info.skeleton_loading.api.hideSkeleton
import com.skeletonloading.info.skeleton_loading.api.loadSkeleton

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    binding = DataBindingUtil.setContentView(this,R.layout.activity_main)



        binding.apply {


            /** button Loading **/
            btnLoading.setOnClickListener {
                loading()
            }

            /** button done **/
            btnDone.setOnClickListener {
                done()
            }


        }
    }



    private fun loading(){
        binding.recyclerView.loadSkeleton(R.layout.item_view)
    }


    private fun done(){
        binding.recyclerView.hideSkeleton()
    }
}