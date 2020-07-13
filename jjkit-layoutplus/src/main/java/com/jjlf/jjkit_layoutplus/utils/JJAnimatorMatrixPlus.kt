package com.jjlf.jjkit_layoutplus.utils

import android.graphics.Matrix
import android.view.animation.Animation
import android.view.animation.Interpolator
import android.view.animation.Transformation
import android.widget.ImageView
import java.lang.ref.WeakReference


internal class JJAnimatorMatrixPlus(target: ImageView? = null) : Animation() , Animation.AnimationListener {

    private var mImageView : WeakReference<ImageView?> = WeakReference(target)
    private var mStartImageMatrix = FloatArray(9)
    private var mEndImageMatrix = FloatArray(9)
    private var mAnimMatrix = FloatArray(9)

    init {
        duration = 300
        fillAfter = true
        interpolator = JJInterpolatorPlus(JJInterpolatorProviderPlus.EASE_OUT_EXPO)
        setAnimationListener(this)
    }

    fun ssInterpolator(inter: Interpolator): JJAnimatorMatrixPlus {
        interpolator = inter
        return this
    }

    fun ssDuration(durationMillis: Long) :JJAnimatorMatrixPlus{
        duration = durationMillis
        return this
    }

    fun ssTarget(target: ImageView) :JJAnimatorMatrixPlus {
        mImageView = WeakReference(target)
        return this
    }

    fun ssStartState(imageMatrix: Matrix) {
        reset()
        imageMatrix.getValues(mStartImageMatrix)
    }

    fun ssEndState(imageMatrix: Matrix) {
        imageMatrix.getValues(mEndImageMatrix)
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {

        for (i in mAnimMatrix.indices) {
            mAnimMatrix[i] =
                mStartImageMatrix[i] + (mEndImageMatrix[i] - mStartImageMatrix[i]) * interpolatedTime
        }
        val m = mImageView.get()?.imageMatrix
        m?.setValues(mAnimMatrix)
        mImageView.get()?.imageMatrix = m
        mImageView.get()?.invalidate()
    }

    override fun onAnimationEnd(animation: Animation?) {
        mImageView.get()?.clearAnimation()
    }

    override fun onAnimationRepeat(animation: Animation?) {}
    override fun onAnimationStart(animation: Animation?) {}


}