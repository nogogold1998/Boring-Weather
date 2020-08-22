package com.sunasterisk.boringweather.ui.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.sunasterisk.boringweather.util.blur
import com.sunasterisk.boringweather.util.load

@BindingAdapter("imageUrl", "blurRadius", "blurSampling", requireAll = false)
fun ImageView.loadImage(url: String?, blurRadius: Int?, blurSampling: Int?) {
    if (url != null) {
        this.load(url) {
            if (blurRadius != null && blurSampling != null) {
                blur(blurRadius, blurSampling)
            }
        }
    }
}
