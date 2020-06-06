package com.jjlf.jjkit_layoutplus.utils

import android.graphics.Matrix
import android.graphics.RectF
import androidx.annotation.FloatRange
import androidx.core.graphics.transform
import com.jjlf.jjkit_layoututils.JJPadding

internal fun RectF.scaleP(
    @FloatRange(from = 0.0, to = 1000.0) scaleX: Float,
    @FloatRange(from = 0.0, to = 1000.0) scaleY: Float, ma: Matrix
) {
    ma.postScale(scaleX,scaleY,centerX(),centerY())
    transform(ma)
}

internal fun RectF.paddingP(padding: JJPadding){
    left += padding.left.toFloat()
    right -= padding.right.toFloat()
    top += padding.top.toFloat()
    bottom -= padding.bottom.toFloat()
}