package com.sunasterisk.boringweather.util

enum class FeelsLikeEmoji(val unicode: String) {
    COLD("\uD83E\uDD76"),
    NEUTRAL("\uD83D\uDE10"),
    SMILE("\uD83D\uDE42"),
    SWEAT("\uD83D\uDE30"),
    HOT("\uD83E\uDD75"),
    KNOCKED_OUT("\uD83D\uDE35");

    override fun toString() = unicode
}
