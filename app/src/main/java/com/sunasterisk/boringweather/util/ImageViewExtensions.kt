package com.sunasterisk.boringweather.util

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.sunasterisk.boringweather.R

fun ImageView.load(url: String) = Glide.with(context)
    .load(Uri.parse(url))
    .centerCrop()
    .error(R.drawable.ic_round_broken_image_24)
    .into(this)
