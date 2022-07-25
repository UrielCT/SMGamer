package com.example.socialmediagamer.model.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.socialmediagamer.model.models.SliderItem
import com.smarteist.autoimageslider.SliderViewAdapter
import com.squareup.picasso.Picasso


class SliderAdapter(val context: Context,val mSliderItems:MutableList<SliderItem>) : SliderViewAdapter<SliderAdapter.SliderAdapterVH>() {



    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        return SliderAdapterVH(LayoutInflater.from(context).inflate(com.example.socialmediagamer.R.layout.slider_item,null))


    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {
        val sliderItem: SliderItem = mSliderItems[position]
        if (sliderItem.imageUrl !=  null){
            if (sliderItem.imageUrl.isNotEmpty()){
                Picasso.get().load(sliderItem.imageUrl).fit().into(viewHolder.imageViewSlider)
            }
        }


    }

    override fun getCount(): Int  =  mSliderItems.size


    inner class SliderAdapterVH(itemView: View) : ViewHolder(itemView) {
        //var itemView: View
        var imageViewSlider: ImageView

        init {
            imageViewSlider = itemView.findViewById((com.example.socialmediagamer.R.id.im_slider))
            //this.itemView = itemView
        }
    }

}