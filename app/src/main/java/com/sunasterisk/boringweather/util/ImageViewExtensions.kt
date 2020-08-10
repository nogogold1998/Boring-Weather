package com.sunasterisk.boringweather.util

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.sunasterisk.boringweather.R

fun ImageView.load(url: String, requestBuilder: (RequestBuilder<Drawable>.() -> Unit)? = null) =
    Glide.with(context)
        .load(Uri.parse(url))
        .centerCrop()
        .error(R.drawable.ic_round_broken_image_24)
        .transition(DrawableTransitionOptions.withCrossFade())
        .apply { requestBuilder?.invoke(this) }
        .into(this)
