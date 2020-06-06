package com.jjlf.jjkit_layoutplus.utils

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class JJRippleDrawablePlus(color: ColorStateList,
                           content: Drawable? = null, mask: Drawable? = null
) : RippleDrawable(color, content, mask) {

    companion object{
        fun roundRect(colorBg: Int, radius: Float = 0f, colorPressed: Int = -1 , colorRipple: Int = -1, strokeW: Float = 0f, colorStroke: Int = Color.BLACK): RippleDrawable {

            val colorRip = if(colorRipple == -1) Color.parseColor("#80CCCCCC") else colorRipple
            val colorPre = if(colorPressed == -1 )  Color.parseColor("#80CCCCCC") else colorPressed

            val colorState = ColorStateList(arrayOf(intArrayOf(android.R.attr.state_pressed) ,intArrayOf(-android.R.attr.state_pressed)), intArrayOf(colorPre,colorRip))
            val bg = GradientDrawable()
            bg.shape = GradientDrawable.RECTANGLE
            bg.color = ColorStateList.valueOf(colorBg)
            bg.cornerRadii = floatArrayOf(radius,radius,radius,radius,radius,radius,radius,radius)


            if(strokeW > 0) {
                bg.setStroke(strokeW.toInt(),colorStroke)
            }

            val rect = RoundRectShape(floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius), null, null)
            val shape = ShapeDrawable(rect)

            return RippleDrawable(
                colorState,
                bg,
                shape
            )
        }

        fun withDrawable(drawable: Drawable,colorPressed: Int = 0 , colorRipple: Int = Color.parseColor("#80CCCCCC")): RippleDrawable{
            val colorRip = if(colorRipple == -1) Color.parseColor("#80CCCCCC") else colorRipple
            val colorPre = if(colorPressed == -1 )  Color.parseColor("#80CCCCCC") else colorPressed

            val colorState = ColorStateList(arrayOf(intArrayOf(android.R.attr.state_pressed) ,intArrayOf(-android.R.attr.state_pressed)), intArrayOf(colorPre,colorRip))

            return RippleDrawable(
                colorState,
                drawable,
                null
            )
        }


        fun withDrawableAndShape(drawable: Drawable,shape:Drawable,colorPressed: Int = 0 , colorRipple: Int = Color.parseColor("#80CCCCCC")): RippleDrawable{
            val colorRip = if(colorRipple == -1) Color.parseColor("#80CCCCCC") else colorRipple
            val colorPre = if(colorPressed == -1 )  Color.parseColor("#80CCCCCC") else colorPressed
            val colorState = ColorStateList(arrayOf(intArrayOf(android.R.attr.state_pressed) ,intArrayOf(-android.R.attr.state_pressed)), intArrayOf(colorPre,colorRip))

            return RippleDrawable(
                colorState,
                drawable,
                shape
            )
        }

    }


}