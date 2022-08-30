package com.suri.weather.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

class Utils {
    companion object {
        @JvmStatic
        @BindingAdapter("loadImage")
        fun loadImage(view: ImageView, url: String?) {
            if (!url.isNullOrEmpty()) {
                Glide.with(view.context).load("https:$url").into(view)
            }
        }

        @JvmStatic
        @BindingAdapter("setFloat")
        fun setFloat(view: TextView, text: Double) {
            view.text = "${text.toFloat()}"
        }

        @JvmStatic
        @BindingAdapter("setTime")
        fun setTime(view: TextView, text: String?) {
            if(text!=null)
                view.text = text.split(' ')[1]
            else view.text="TIME"
        }
    }
}