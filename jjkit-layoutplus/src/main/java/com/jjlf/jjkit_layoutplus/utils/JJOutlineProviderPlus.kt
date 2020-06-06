package com.jjlf.jjkit_layoutplus.utils

import android.graphics.Matrix
import android.graphics.Outline
import android.graphics.Path
import android.graphics.RectF
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.FloatRange
import androidx.annotation.RequiresApi
import androidx.core.graphics.toRect
import com.jjlf.jjkit_layoututils.JJPadding


import kotlin.math.min

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
internal class JJOutlineProviderPlus : ViewOutlineProvider() {
    private var mRadius = floatArrayOf(0f,0f,0f,0f,0f,0f,0f,0f)
    private var mScaleX = -1f
    private var mScaleY = -1f
    private var mOffsetY = 0f
    private var mOffsetX = 0f
    private var mAlpha = 0.99f
    private var mPath = Path()
    private val mRect = RectF()
    private var mShape = 0
    private var mIsPathClosure = false
    private var mSetupPath: ((RectF,Path)-> Unit)? = null
    private var mIsNewPath = false
    private var mPadding = JJPadding()


    companion object{
        const val ROUND_CIRCLE = 1
        const val CORNER_SMOOTH_VERY_SMALL = 2
        const val CORNER_SMOOTH_SMALL = 3
        const val CORNER_SMOOTH_MEDIUM = 4
        const val CORNER_SMOOTH_LARGE = 5
        const val CORNER_SMOOTH_XLARGE = 6
    }

    fun setScale(x: Float,y: Float):JJOutlineProviderPlus{
        mScaleX = x
        mScaleY = y
        return this
    }

    fun setShape(type: Int):JJOutlineProviderPlus{
        mIsNewPath = false
        mIsPathClosure = false
        mShape = type
        return this
    }

    fun setOffset(x: Float,y: Float):JJOutlineProviderPlus{
        mOffsetX = x
        mOffsetY = y
        return this
    }


    fun setAlpha(@FloatRange(from = 0.0, to = 1.0)alpha: Float): JJOutlineProviderPlus{
        mAlpha = if(alpha == 1f) 0.99f else alpha
        return this
    }

    fun setRadius(radius: Float): JJOutlineProviderPlus{
        mRadius = floatArrayOf(radius,radius,radius,radius,radius,radius,radius,radius)
        mIsNewPath = false
        mIsPathClosure = false
        mShape = 0
        return this
    }

    fun setRadius(radius: FloatArray) : JJOutlineProviderPlus{
        mRadius = radius
        mIsNewPath = false
        mIsPathClosure = false
        mShape = 0
        return this
    }

    fun setRadius(topLeft: Float,topRight:Float, bottomRight:Float,bottomLeft:Float) : JJOutlineProviderPlus{
        mRadius = floatArrayOf(topLeft,topLeft,topRight,topRight,bottomRight,bottomRight,bottomLeft,bottomLeft)
        mIsNewPath = false
        mIsPathClosure = false
        mShape = 0
        return this
    }

    fun setConvexPath(path:Path):JJOutlineProviderPlus{
        mIsNewPath = true
        mIsPathClosure = false
        mShape = 0
        mPath = path
        return this
    }

    fun setConvexPath(closure:(RectF,Path)->Unit):JJOutlineProviderPlus{
        mIsNewPath = true
        mIsPathClosure = true
        mSetupPath = closure
        mShape = 0
        return this
    }

    fun diposePath():JJOutlineProviderPlus{
        mIsNewPath = false
        mSetupPath = null
        mIsPathClosure = false
        mPath.reset()
        return this
    }


    fun setPadding(padding: JJPadding): JJOutlineProviderPlus {
        mPadding = padding
        return this
    }


    override fun getOutline(view: View?, outline: Outline?) {
        val h = view?.height ?: 0
        val w = view?.width ?: 0
        mRect.set(0f,0f,w.toFloat(),h.toFloat())
        outline?.alpha = mAlpha

        if(mRect.width() > 0f && mRect.height()> 0f) {

            setupRect()

            val isClipToOutline = view?.clipToOutline ?: false
            if(isClipToOutline){
                setupRadiusForShape()
                outline?.setRoundRect(mRect.toRect(),mRadius.first())
            }else {
                //just elevation

                if(mIsPathClosure  && mIsNewPath){
                    mPath.reset()
                    mSetupPath?.invoke(mRect, mPath)
                }
                if (!mIsNewPath){
                    mPath.reset()
                    setupRadiusForShape()
                    mPath.addRoundRect(mRect, mRadius, Path.Direction.CW)
                }

                outline?.setConvexPath(mPath)
            }
        }

    }

  private var mMatrix = Matrix()
    private fun setupRect(){
        mRect.paddingP(mPadding)
        mRect.scaleP(mScaleX, mScaleY,mMatrix)
        mRect.offset(mOffsetX, mOffsetY)
    }

    private fun setupRadiusForShape(){
        when(mShape){
            ROUND_CIRCLE -> {
                val radius = min(mRect.height(),mRect.width()) / 2f
                for (i in 0..7){
                    mRadius[i] = radius
                }
            }
            2 -> {
                val radius = min(mRect.height(),mRect.width()) * 0.03f
                for (i in 0..7){
                    mRadius[i] = radius
                }
            }
            3 -> {
                val radius = min(mRect.height(),mRect.width()) * 0.05f
                for (i in 0..7){
                    mRadius[i] = radius
                }
            }
            4 -> {
                val radius = min(mRect.height(),mRect.width()) * 0.1f
                for (i in 0..7){
                    mRadius[i] = radius
                }
            }

            5 -> {
                val radius = min(mRect.height(),mRect.width()) * 0.15f
                for (i in 0..7){
                    mRadius[i] = radius
                }
            }

            6 -> {
                val radius = min(mRect.height(),mRect.width()) * 0.2f
                for (i in 0..7){
                    mRadius[i] = radius
                }
            }


            else -> Log.v("JJOutlineProvider","Custom Radius")
        }
    }
}


