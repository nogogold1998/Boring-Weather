package com.sunasterisk.boringweather.util

import com.sunasterisk.boringweather.data.model.Weather
import com.sunasterisk.boringweather.util.WeatherGifUrls.clearNight
import com.sunasterisk.boringweather.util.WeatherGifUrls.cloud
import com.sunasterisk.boringweather.util.WeatherGifUrls.rain
import com.sunasterisk.boringweather.util.WeatherGifUrls.snow
import com.sunasterisk.boringweather.util.WeatherGifUrls.thunder

val Weather.gifUrl: String?
    get() = when (id) {
        in 200 until 300 -> thunder.random()
        in 300 until 400 -> null
        in 500 until 600 -> rain.random()
        in 600 until 700 -> snow.random()
        in 700 until 800 -> null
        800 -> icon?.lastOrNull()?.takeIf { it == 'n' }?.let { clearNight.random() }
        in 801 until 900 -> cloud.random()
        else -> null
    }

private object WeatherGifUrls {

    val thunder = arrayOf(
        "https://thumbs.gfycat.com/LimitedFittingGuillemot-max-1mb.gif",
        "https://i.pinimg.com/originals/38/81/58/38815856a338eba76821039793d3ce51.gif",
        "https://i2.wp.com/windowscustomization.com/wp-content/uploads/2018/12/Thunders.gif",
        "https://i.imgur.com/8qV7l.gif"
    )

    val rain = arrayOf(
        "https://data.whicdn.com/images/291832111/original.gif",
        "https://media3.giphy.com/media/l0Iy5fjHyedk9aDGU/giphy.gif",
        "https://media2.giphy.com/media/gRnSZSRzOJeG4/giphy.gif?cid=ecf05e47a35783c5c6cb0a68ada9f14278318223d52546c5&rid=giphy.gif",
        "https://media0.giphy.com/media/t7Qb8655Z1VfBGr5XB/giphy.gif?cid=ecf05e47e497ce7812c0ac41cfdb9c37cb682d1ad4156153&rid=giphy.gif"
    )

    val clearNight = arrayOf(
        "https://data.whicdn.com/images/221826294/original.gif",
        "https://i.gifer.com/1AZy.gif",
        "https://thumbs.gfycat.com/PreciousAgreeableLark-size_restricted.gif",
        "https://i.gifer.com/shP.gif"
    )

    val cloud = arrayOf(
        "https://i.gifer.com/ChB.gif",
        "https://64.media.tumblr.com/259647777ce730adeb8fe08aad6d8589/tumblr_nxdjfcoSmW1s08qivo1_400.gifv",
        "https://i.pinimg.com/originals/b6/7f/61/b67f61a1364ea22a050d701c7bf7858f.gif",
        "https://media2.giphy.com/media/HoUgegTjteXCw/giphy.gif"
    )

    val snow = arrayOf(
        "https://64.media.tumblr.com/8f8a1a48b69ae147d334edad25564096/tumblr_mvp1bqaBGt1swwqebo1_500.gif",
        "https://media1.giphy.com/media/BDucPOizdZ5AI/giphy.gif",
        "https://media2.giphy.com/media/dGGcyYfMEoC7S/giphy.gif?cid=ecf05e47fdcf22908f78a8c4f8a9bab53ad89fbedb97f27b&rid=giphy.gif",
        "https://media0.giphy.com/media/bnsWLCG5bEaiI/giphy.gif?cid=ecf05e47d72caf7c398fbd35f4d77913a3d0dd625e88e702&rid=giphy.gif"
    )
}
