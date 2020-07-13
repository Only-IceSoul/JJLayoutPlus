package com.jjlf.jjkit_layoutplus.utils

import android.view.animation.Interpolator

internal class JJInterpolatorPlus(private val ease: Int) : Interpolator {


    override fun getInterpolation(input: Float): Float {
       return JJInterpolatorProviderPlus.get(ease, input)
    }

     fun getEase(): Int {
        return ease
    }
}