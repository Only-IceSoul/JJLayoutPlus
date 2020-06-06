package com.jjlf.jjkit_layoutplus

import android.content.Context
import android.content.res.Configuration
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.animation.Interpolator
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.updateMarginsRelative
import com.google.android.material.appbar.AppBarLayout
import com.jjlf.jjkit_layoutplus.JJImageCategoryCircle
import com.jjlf.jjkit_layoutplus.R
import com.jjlf.jjkit_layoututils.JJMargin
import com.jjlf.jjkit_layoututils.JJPadding
import com.jjlf.jjkit_layoututils.JJScreen

class JJImageCategoryCircle : ConstraintLayout {
    
    private lateinit var mImageView: AppCompatImageView
    private lateinit var mTextView : AppCompatTextView
    private fun setupViews(context: Context,attrs: AttributeSet?){
        
    }


    //region init

    constructor(context: Context) : super(context) {
        this.id = View.generateViewId()
        setupInitConstraint()
        setupViews(context,null)
    }

    private var mSupportLandScape = false
    private var mIgnoreCl = false
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        setupInitConstraint()
        setupLayout(attrs)
        setupViews(context,attrs)
    }


    private fun setupLayout(attrs: AttributeSet){
        val a = context.obtainStyledAttributes(attrs,
            R.styleable.JJImageCategoryCircle, 0, 0)
        mIgnoreCl = a.getBoolean(R.styleable.JJImageCategoryCircle_layout_ignoreCl,false)
        mConfigurationChanged = a.getBoolean(R.styleable.JJImageCategoryCircle_support_configuration_changed,false)
        mSupportLandScape = a.getBoolean(R.styleable.JJImageCategoryCircle_support_landscape,false)

        setupAndroidBase(attrs)

        for (i in 0 until a.indexCount) {
            //region standard portrait
            setupMarginLp(a, i)
            setupPaddingLp(a,i)
            setupSizeLp(a,i)
            //endregion
            //region constraint Layout portrait
            setupSizeCl(a,i)
            setupAnchorsCl(a, i)
            setupMarginCl(a,i)
            //endregion

            //region standard landscape
            setupMarginLpl(a,i)
            setupPaddingLpl(a,i)
            setupSizeLpl(a,i)
            //endregion
            //region constraint Layout landscape
            setupSizeCll(a,i)
            setupAnchorsCll(a, i)
            setupMarginCll(a,i)
            //endregion
        }
        a.recycle()

    }
    private fun setupAndroidBase(attrs: AttributeSet){
        val attrsArray = intArrayOf(
            android.R.attr.id,
            android.R.attr.layout_width, // 1
            android.R.attr.layout_height // 2
        )
        val ba = context.obtainStyledAttributes(attrs,
            attrsArray, 0, 0)

        val attrWidth = ba.getLayoutDimension(1, 0)
        val attrHeight = ba.getLayoutDimension(2, 0)

        if(attrWidth > 0 || attrWidth == -2) clWidth(attrWidth)
        if(attrHeight > 0 || attrHeight == -2) clHeight(attrHeight)

        mlpHeight = attrHeight
        mlpWidth = attrWidth

        val attrId = ba.getResourceId(0, View.NO_ID)
        if(attrId == View.NO_ID) id = View.generateViewId()

        ba.recycle()

    }

    private fun setupSizeLp(a: TypedArray, index:Int){
        when(a.getIndex(index)){
            R.styleable.JJImageCategoryCircle_lpHeightPercentScreenWidth -> {
                mlpHeight = JJScreen.percentWidth(a.getFloat(R.styleable.JJImageCategoryCircle_lpHeightPercentScreenWidth,0f))
            }
            R.styleable.JJImageCategoryCircle_lpHeightPercentScreenHeight -> {
                mlpHeight = JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_lpHeightPercentScreenHeight,0f))
            }
            R.styleable.JJImageCategoryCircle_lpWidthPercentScreenWidth -> {
                mlpWidth = JJScreen.percentWidth( a.getFloat(R.styleable.JJImageCategoryCircle_lpWidthPercentScreenWidth,0f))
            }
            R.styleable.JJImageCategoryCircle_lpWidthPercentScreenHeight -> {
                mlpWidth = JJScreen.percentHeight( a.getFloat(R.styleable.JJImageCategoryCircle_lpWidthPercentScreenHeight,0f))
            }
            R.styleable.JJImageCategoryCircle_lpHeightResponsive -> {
                mlpHeight =  responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_lpHeightResponsive)
            }
            R.styleable.JJImageCategoryCircle_lpWidthResponsive -> {
                mlpWidth =  responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_lpWidthResponsive)
            }
            R.styleable.JJImageCategoryCircle_lpHeightResponsivePercentScreenHeight -> {
                mlpHeight = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_lpHeightResponsivePercentScreenHeight)
            }
            R.styleable.JJImageCategoryCircle_lpWidthResponsivePercentScreenHeight -> {
                mlpWidth = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_lpWidthResponsivePercentScreenHeight)
            }
            R.styleable.JJImageCategoryCircle_lpHeightResponsivePercentScreenWidth -> {
                mlpHeight = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_lpHeightResponsivePercentScreenWidth)
            }
            R.styleable.JJImageCategoryCircle_lpWidthResponsivePercentScreenWidth -> {
                mlpWidth = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_lpWidthResponsivePercentScreenWidth)
            }
        }
    }
    private fun setupMarginLp(a: TypedArray, index: Int){
        when(a.getIndex(index)){
            R.styleable.JJImageCategoryCircle_lpMarginTopPerScHeight -> {
                mlpMargins.top = JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_lpMarginTopPerScHeight,0f))
            }
            R.styleable.JJImageCategoryCircle_lpMarginLeftPerScHeight -> {
                mlpMargins.left = JJScreen.percentHeight( a.getFloat(R.styleable.JJImageCategoryCircle_lpMarginLeftPerScHeight,0f))
            }
            R.styleable.JJImageCategoryCircle_lpMarginRightPerScHeight -> {
                mlpMargins.right = JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_lpMarginRightPerScHeight,0f))
            }
            R.styleable.JJImageCategoryCircle_lpMarginBottomPerScHeight -> {
                mlpMargins.bottom = JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_lpMarginBottomPerScHeight,0f))
            }
            R.styleable.JJImageCategoryCircle_lpMarginTopPerScWidth -> {
                mlpMargins.top = JJScreen.percentWidth(a.getFloat(R.styleable.JJImageCategoryCircle_lpMarginTopPerScWidth,0f))
            }
            R.styleable.JJImageCategoryCircle_lpMarginLeftPerScWidth -> {
                mlpMargins.left = JJScreen.percentWidth( a.getFloat(R.styleable.JJImageCategoryCircle_lpMarginLeftPerScWidth,0f))
            }
            R.styleable.JJImageCategoryCircle_lpMarginRightPerScWidth->{
                mlpMargins.right = JJScreen.percentWidth(a.getFloat(R.styleable.JJImageCategoryCircle_lpMarginRightPerScWidth,0f))
            }
            R.styleable.JJImageCategoryCircle_lpMarginBottomPerScWidth -> {
                mlpMargins.bottom = JJScreen.percentWidth(a.getFloat(R.styleable.JJImageCategoryCircle_lpMarginBottomPerScWidth,0f))
            }
            R.styleable.JJImageCategoryCircle_lpMarginTopResponsive -> {
                mlpMargins.top = responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_lpMarginTopResponsive)
            }
            R.styleable.JJImageCategoryCircle_lpMarginLeftResponsive ->{
                mlpMargins.left =  responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_lpMarginLeftResponsive)
            }
            R.styleable.JJImageCategoryCircle_lpMarginRightResponsive -> {
                mlpMargins.right =   responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_lpMarginRightResponsive)
            }
            R.styleable.JJImageCategoryCircle_lpMarginBottomResponsive -> {
                mlpMargins.bottom =  responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_lpMarginBottomResponsive)
            }
            R.styleable.JJImageCategoryCircle_lpMarginTopResPerScWidth -> {
                mlpMargins.top  = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_lpMarginTopResPerScWidth)
            }
            R.styleable.JJImageCategoryCircle_lpMarginLeftResPerScWidth -> {
                mlpMargins.left = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_lpMarginLeftResPerScWidth)
            }
            R.styleable.JJImageCategoryCircle_lpMarginRightResPerScWidth -> {
                mlpMargins.right =  responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_lpMarginRightResPerScWidth)
            }
            R.styleable.JJImageCategoryCircle_lpMarginBottomResPerScWidth -> {
                mlpMargins.bottom = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_lpMarginBottomResPerScWidth)
            }
            R.styleable.JJImageCategoryCircle_lpMarginTopResPerScHeight ->{
                mlpMargins.top = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_lpMarginTopResPerScHeight)
            }
            R.styleable.JJImageCategoryCircle_lpMarginLeftResPerScHeight ->{
                mlpMargins.left = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_lpMarginLeftResPerScHeight)
            }
            R.styleable.JJImageCategoryCircle_lpMarginRightResPerScHeight ->{
                mlpMargins.right = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_lpMarginRightResPerScHeight)
            }
            R.styleable.JJImageCategoryCircle_lpMarginBottomResPerScHeight ->{
                mlpMargins.bottom = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_lpMarginBottomResPerScHeight)
            }
            R.styleable.JJImageCategoryCircle_lpMarginPercentScHeight -> {
                mlpMargins = JJMargin.all(JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_lpMarginPercentScHeight,0f)))
            }
            R.styleable.JJImageCategoryCircle_lpMarginPercentScWidth -> {
                mlpMargins = JJMargin.all(JJScreen.percentWidth( a.getFloat(R.styleable.JJImageCategoryCircle_lpMarginPercentScWidth,0f)))
            }
            R.styleable.JJImageCategoryCircle_lpMarginResponsive -> {
                mlpMargins = JJMargin.all(responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_lpMarginResponsive))
            }
            R.styleable.JJImageCategoryCircle_lpMarginResPerScHeight -> {
                mlpMargins = JJMargin.all(responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_lpMarginResPerScHeight))
            }
            R.styleable.JJImageCategoryCircle_lpMarginResPerScWidth -> {
                mlpMargins =  JJMargin.all(responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_lpMarginResPerScWidth))
            }
            R.styleable.JJImageCategoryCircle_lpMarginVerticalPerScHeight -> {
                val mar = JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_lpMarginVerticalPerScHeight,0f))
                mlpMargins.top = mar ; mlpMargins.bottom = mar
            }
            R.styleable.JJImageCategoryCircle_lpMarginVerticalPerScWidth ->{
                val mar = JJScreen.percentWidth(a.getFloat(R.styleable.JJImageCategoryCircle_lpMarginVerticalPerScWidth,0f))
                mlpMargins.top = mar ; mlpMargins.bottom = mar
            }
            R.styleable.JJImageCategoryCircle_lpMarginVerticalResponsive -> {
                val mar = responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_lpMarginVerticalResponsive)
                mlpMargins.top = mar ; mlpMargins.bottom = mar
            }
            R.styleable.JJImageCategoryCircle_lpMarginVerticalResPerScWidth -> {
                val mar = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_lpMarginVerticalResPerScWidth )
                mlpMargins.top = mar ; mlpMargins.bottom = mar
            }
            R.styleable.JJImageCategoryCircle_lpMarginVerticalResPerScHeight -> {
                val mar = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_lpMarginVerticalResPerScHeight)
                mlpMargins.top = mar ; mlpMargins.bottom = mar
            }
            R.styleable.JJImageCategoryCircle_lpMarginHorizontalPerScHeight -> {
                val mar = JJScreen.percentHeight( a.getFloat(R.styleable.JJImageCategoryCircle_lpMarginHorizontalPerScHeight,0f))
                mlpMargins.left = mar ; mlpMargins.right = mar
            }
            R.styleable.JJImageCategoryCircle_lpMarginHorizontalPerScWidth -> {
                val mar = JJScreen.percentWidth(a.getFloat(R.styleable.JJImageCategoryCircle_lpMarginHorizontalPerScWidth,0f))
                mlpMargins.left = mar ; mlpMargins.right = mar
            }
            R.styleable.JJImageCategoryCircle_lpMarginHorizontalResponsive -> {
                val mar = responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_lpMarginHorizontalResponsive)
                mlpMargins.left = mar ; mlpMargins.right = mar
            }
            R.styleable.JJImageCategoryCircle_lpMarginHorizontalResPerScWidth -> {
                val mar = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_lpMarginHorizontalResPerScWidth)
                mlpMargins.left = mar ; mlpMargins.right = mar
            }
            R.styleable.JJImageCategoryCircle_lpMarginHorizontalResPerScHeight -> {
                val mar = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_lpMarginHorizontalResPerScHeight)
                mlpMargins.left = mar ; mlpMargins.right = mar
            }
        }

    }
    private fun setupPaddingLp(a: TypedArray, index:Int){
        when(a.getIndex(index)){
            R.styleable.JJImageCategoryCircle_lpPaddingTopPerScHeight -> {
                mlpPadding.top = JJScreen.percentHeight( a.getFloat(R.styleable.JJImageCategoryCircle_lpPaddingTopPerScHeight,0f))
            }
            R.styleable.JJImageCategoryCircle_lpPaddingLeftPerScHeight -> {
                mlpPadding.left = JJScreen.percentHeight( a.getFloat(R.styleable.JJImageCategoryCircle_lpPaddingLeftPerScHeight,0f))
            }
            R.styleable.JJImageCategoryCircle_lpPaddingRightPerScHeight -> {
                mlpPadding.right = JJScreen.percentHeight( a.getFloat(R.styleable.JJImageCategoryCircle_lpPaddingRightPerScHeight,0f))
            }
            R.styleable.JJImageCategoryCircle_lpPaddingBottomPerScHeight -> {
                mlpPadding.bottom = JJScreen.percentHeight( a.getFloat(R.styleable.JJImageCategoryCircle_lpPaddingBottomPerScHeight,0f))
            }
            R.styleable.JJImageCategoryCircle_lpPaddingTopPerScWidth -> {
                mlpPadding.top = JJScreen.percentWidth( a.getFloat(R.styleable.JJImageCategoryCircle_lpPaddingTopPerScWidth,0f))
            }
            R.styleable.JJImageCategoryCircle_lpPaddingLeftPerScWidth -> {
                mlpPadding.left = JJScreen.percentWidth( a.getFloat(R.styleable.JJImageCategoryCircle_lpPaddingLeftPerScWidth,0f))
            }
            R.styleable.JJImageCategoryCircle_lpPaddingRightPerScWidth -> {
                mlpPadding.right = JJScreen.percentWidth( a.getFloat(R.styleable.JJImageCategoryCircle_lpPaddingRightPerScWidth,0f))
            }
            R.styleable.JJImageCategoryCircle_lpPaddingBottomPerScWidth -> {
                mlpPadding.bottom = JJScreen.percentWidth( a.getFloat(R.styleable.JJImageCategoryCircle_lpPaddingBottomPerScWidth,0f))
            }
            R.styleable.JJImageCategoryCircle_lpPaddingTopResponsive -> {
                mlpPadding.top = responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_lpPaddingTopResponsive)
            }
            R.styleable.JJImageCategoryCircle_lpPaddingLeftResponsive -> {
                mlpPadding.left = responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_lpPaddingLeftResponsive)
            }
            R.styleable.JJImageCategoryCircle_lpPaddingRightResponsive -> {
                mlpPadding.right = responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_lpPaddingRightResponsive)
            }
            R.styleable.JJImageCategoryCircle_lpPaddingBottomResponsive -> {
                mlpPadding.bottom = responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_lpPaddingBottomResponsive)
            }
            R.styleable.JJImageCategoryCircle_lpPaddingTopResPerScWidth -> {
                mlpPadding.top = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_lpPaddingTopResPerScWidth )
            }
            R.styleable.JJImageCategoryCircle_lpPaddingLeftResPerScWidth -> {
                mlpPadding.left = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_lpPaddingLeftResPerScWidth )
            }
            R.styleable.JJImageCategoryCircle_lpPaddingRightResPerScWidth -> {
                mlpPadding.right = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_lpPaddingRightResPerScWidth )
            }
            R.styleable.JJImageCategoryCircle_lpPaddingBottomResPerScWidth -> {
                mlpPadding.bottom = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_lpPaddingBottomResPerScWidth )
            }

            R.styleable.JJImageCategoryCircle_lpPaddingTopResPerScHeight -> {
                mlpPadding.top = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_lpPaddingTopResPerScHeight )
            }
            R.styleable.JJImageCategoryCircle_lpPaddingLeftResPerScHeight -> {
                mlpPadding.left = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_lpPaddingLeftResPerScHeight )
            }
            R.styleable.JJImageCategoryCircle_lpPaddingRightResPerScHeight -> {
                mlpPadding.right = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_lpPaddingRightResPerScHeight )
            }
            R.styleable.JJImageCategoryCircle_lpPaddingBottomResPerScHeight -> {
                mlpPadding.bottom = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_lpPaddingBottomResPerScHeight )
            }
            R.styleable.JJImageCategoryCircle_lpPaddingPercentScHeight -> {
                mlpPadding = JJPadding.all(JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_lpPaddingPercentScHeight,0f)))
            }
            R.styleable.JJImageCategoryCircle_lpPaddingPercentScWidth -> {
                mlpPadding = JJPadding.all(JJScreen.percentWidth(a.getFloat(R.styleable.JJImageCategoryCircle_lpPaddingPercentScWidth,0f)))
            }
            R.styleable.JJImageCategoryCircle_lpPaddingResponsive -> {
                mlpPadding = JJPadding.all(responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_lpPaddingResponsive))
            }
            R.styleable.JJImageCategoryCircle_lpPaddingResPerScHeight -> {
                mlpPadding = JJPadding.all(responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_lpPaddingResPerScHeight))
            }
            R.styleable.JJImageCategoryCircle_lpPaddingResPerScWidth -> {
                mlpPadding = JJPadding.all(responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_lpPaddingResPerScWidth))
            }
            R.styleable.JJImageCategoryCircle_lpPaddingVerticalPerScHeight -> {
                val mar = JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_lpPaddingVerticalPerScHeight,0f))
                mlpPadding.top = mar ; mlpPadding.bottom = mar
            }
            R.styleable.JJImageCategoryCircle_lpPaddingVerticalPerScWidth -> {
                val mar = JJScreen.percentWidth(a.getFloat(R.styleable.JJImageCategoryCircle_lpPaddingVerticalPerScWidth,0f))
                mlpPadding.top = mar ; mlpPadding.bottom = mar
            }
            R.styleable.JJImageCategoryCircle_lpPaddingVerticalResponsive -> {
                val mar = responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_lpPaddingVerticalResponsive)
                mlpPadding.top = mar ; mlpPadding.bottom = mar
            }
            R.styleable.JJImageCategoryCircle_lpPaddingVerticalResPerScWidth -> {
                val mar = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_lpPaddingVerticalResPerScWidth)
                mlpPadding.top = mar ; mlpPadding.bottom = mar
            }
            R.styleable.JJImageCategoryCircle_lpPaddingVerticalResPerScHeight -> {
                val mar = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_lpPaddingVerticalResPerScHeight)
                mlpPadding.top = mar ; mlpPadding.bottom = mar
            }
            R.styleable.JJImageCategoryCircle_lpPaddingHorizontalPerScHeight -> {
                val mar = JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_lpPaddingHorizontalPerScHeight,0f))
                mlpPadding.left = mar ; mlpPadding.right = mar
            }
            R.styleable.JJImageCategoryCircle_lpPaddingHorizontalPerScWidth -> {
                val mar = JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_lpPaddingHorizontalPerScWidth,0f))
                mlpPadding.left = mar ; mlpPadding.right = mar
            }
            R.styleable.JJImageCategoryCircle_lpPaddingHorizontalResponsive -> {
                val mar = responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_lpPaddingHorizontalResponsive)
                mlpPadding.left = mar ; mlpPadding.right = mar
            }
            R.styleable.JJImageCategoryCircle_lpPaddingHorizontalResPerScWidth ->{
                val mar = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_lpPaddingHorizontalResPerScWidth)
                mlpPadding.left = mar ; mlpPadding.right = mar
            }
            R.styleable.JJImageCategoryCircle_lpPaddingHorizontalResPerScHeight ->{
                val mar = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_lpPaddingHorizontalResPerScHeight)
                mlpPadding.left = mar ; mlpPadding.right = mar
            }

        }

    }

    private fun setupSizeCl(a: TypedArray, index:Int){
        when(a.getIndex(index)){
            R.styleable.JJImageCategoryCircle_clHeightPercent -> {
                clPercentHeight( a.getFloat(R.styleable.JJImageCategoryCircle_clHeightPercent,0f))
            }
            R.styleable.JJImageCategoryCircle_clWidthPercent -> {
                clPercentWidth( a.getFloat(R.styleable.JJImageCategoryCircle_clWidthPercent,0f))
            }
            R.styleable.JJImageCategoryCircle_clHeightPercentScreenWidth -> {
                clHeight(JJScreen.percentWidth(a.getFloat(R.styleable.JJImageCategoryCircle_clHeightPercentScreenWidth,0f)))
            }
            R.styleable.JJImageCategoryCircle_clWidthPercentScreenWidth -> {
                clWidth(JJScreen.percentWidth(a.getFloat(R.styleable.JJImageCategoryCircle_clWidthPercentScreenWidth,0f)))
            }

            R.styleable.JJImageCategoryCircle_clHeightPercentScreenHeight -> {
                clHeight(JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_clHeightPercentScreenHeight,0f)))
            }
            R.styleable.JJImageCategoryCircle_clWidthPercentScreenHeight -> {
                clWidth(JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_clWidthPercentScreenHeight,0f)))
            }
            R.styleable.JJImageCategoryCircle_clHeightResponsive -> {
                clHeight(responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_clHeightResponsive))
            }
            R.styleable.JJImageCategoryCircle_clWidthResponsive -> {
                clWidth(responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_clWidthResponsive))
            }
            R.styleable.JJImageCategoryCircle_clHeightResponsivePercentScreenHeight ->{
                clHeight(responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_clHeightResponsivePercentScreenHeight))
            }
            R.styleable.JJImageCategoryCircle_clWidthResponsivePercentScreenHeight ->{
                clWidth(responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_clWidthResponsivePercentScreenHeight))
            }

            R.styleable.JJImageCategoryCircle_clHeightResponsivePercentScreenWidth ->{
                clHeight(responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_clHeightResponsivePercentScreenWidth))
            }
            R.styleable.JJImageCategoryCircle_clWidthResponsivePercentScreenWidth ->{
                clWidth(responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_clWidthResponsivePercentScreenWidth))
            }
        }




    }
    private fun setupAnchorsCl(a: TypedArray, index:Int){
        when(a.getIndex(index)){
            R.styleable.JJImageCategoryCircle_clFillParent -> {
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_clFillParent,false)) clFillParent()
            }
            R.styleable.JJImageCategoryCircle_clFillParentHorizontally -> {
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_clFillParentHorizontally,false)) clFillParentHorizontally()
            }
            R.styleable.JJImageCategoryCircle_clFillParentVertically -> {
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_clFillParentVertically,false)) clFillParentVertically()
            }
            R.styleable.JJImageCategoryCircle_clCenterInParent -> {
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_clCenterInParent,false)) clCenterInParent()
            }
            R.styleable.JJImageCategoryCircle_clCenterInParentHorizontally -> {
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_clCenterInParentHorizontally,false)) clCenterInParentHorizontally()
            }
            R.styleable.JJImageCategoryCircle_clCenterInParentVertically -> {
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_clCenterInParentVertically,false)) clCenterInParentVertically()
            }
            R.styleable.JJImageCategoryCircle_clCenterInParentTopVertically -> {
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_clCenterInParentTopVertically,false))  clCenterInParentTopVertically()
            }
            R.styleable.JJImageCategoryCircle_clCenterInParentBottomVertically -> {
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_clCenterInParentBottomVertically,false)) clCenterInParentBottomVertically()
            }
            R.styleable.JJImageCategoryCircle_clCenterInParentStartHorizontally -> {
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_clCenterInParentStartHorizontally,false)) clCenterInParentStartHorizontally()
            }
            R.styleable.JJImageCategoryCircle_clCenterInParentEndHorizontally -> {
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_clCenterInParentEndHorizontally,false)) clCenterInParentEndHorizontally()
            }

            R.styleable.JJImageCategoryCircle_clCenterInTopVerticallyOf -> {
                clCenterInTopVertically(a.getResourceId(
                    R.styleable.JJImageCategoryCircle_clCenterInTopVerticallyOf,
                    View.NO_ID))
            }
            R.styleable.JJImageCategoryCircle_clCenterInBottomVerticallyOf -> {
                clCenterInBottomVertically(a.getResourceId(
                    R.styleable.JJImageCategoryCircle_clCenterInBottomVerticallyOf,
                    View.NO_ID))
            }
            R.styleable.JJImageCategoryCircle_clCenterInStartHorizontallyOf -> {
                clCenterInStartHorizontally(a.getResourceId(
                    R.styleable.JJImageCategoryCircle_clCenterInStartHorizontallyOf,
                    View.NO_ID))
            }
            R.styleable.JJImageCategoryCircle_clCenterInEndHorizontallyOf -> {
                clCenterInEndHorizontally(a.getResourceId(
                    R.styleable.JJImageCategoryCircle_clCenterInEndHorizontallyOf,
                    View.NO_ID))
            }
            R.styleable.JJImageCategoryCircle_clCenterVerticallyOf -> {
                clCenterVerticallyOf(a.getResourceId(
                    R.styleable.JJImageCategoryCircle_clCenterVerticallyOf,
                    View.NO_ID))
            }
            R.styleable.JJImageCategoryCircle_clCenterHorizontallyOf -> {
                clCenterHorizontallyOf(a.getResourceId(
                    R.styleable.JJImageCategoryCircle_clCenterHorizontallyOf,
                    View.NO_ID))
            }
            R.styleable.JJImageCategoryCircle_clVerticalBias -> {
                clVerticalBias(a.getFloat(R.styleable.JJImageCategoryCircle_clVerticalBias,0.5f))
            }
            R.styleable.JJImageCategoryCircle_clHorizontalBias -> {
                clHorizontalBias( a.getFloat(R.styleable.JJImageCategoryCircle_clHorizontalBias,0.5f))
            }
            R.styleable.JJImageCategoryCircle_clStartToStartParent -> {
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_clStartToStartParent,false)) clStartToStartParent()
            }
            R.styleable.JJImageCategoryCircle_clStartToEndParent -> {
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_clStartToEndParent,false)) clStartToEndParent()
            }
            R.styleable.JJImageCategoryCircle_clEndToEndParent -> {
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_clEndToEndParent,false)) clEndToEndParent()
            }
            R.styleable.JJImageCategoryCircle_clEndToStartParent -> {
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_clEndToStartParent,false)) clEndToStartParent()
            }
            R.styleable.JJImageCategoryCircle_clTopToTopParent -> {
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_clTopToTopParent,false)) clTopToTopParent()
            }
            R.styleable.JJImageCategoryCircle_clTopToBottomParent -> {
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_clTopToBottomParent,false)) clTopToBottomParent()
            }
            R.styleable.JJImageCategoryCircle_clBottomToBottomParent -> {
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_clBottomToBottomParent,false)) clBottomToBottomParent()
            }
            R.styleable.JJImageCategoryCircle_clBottomToTopParent -> {
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_clBottomToTopParent,false)) clBottomToTopParent()
            }

            R.styleable.JJImageCategoryCircle_clStartToStartOf -> {
                clStartToStart(a.getResourceId(R.styleable.JJImageCategoryCircle_clStartToStartOf, View.NO_ID))
            }
            R.styleable.JJImageCategoryCircle_clStartToEndOf -> {
                clStartToEnd(a.getResourceId(R.styleable.JJImageCategoryCircle_clStartToEndOf, View.NO_ID))
            }
            R.styleable.JJImageCategoryCircle_clEndToEndOf -> {
                clEndToEnd(a.getResourceId(R.styleable.JJImageCategoryCircle_clEndToEndOf, View.NO_ID))
            }
            R.styleable.JJImageCategoryCircle_clEndToStartOf -> {
                clEndToStart(a.getResourceId(R.styleable.JJImageCategoryCircle_clEndToStartOf, View.NO_ID))
            }
            R.styleable.JJImageCategoryCircle_clTopToTopOf -> {
                clTopToTop(a.getResourceId(R.styleable.JJImageCategoryCircle_clTopToTopOf, View.NO_ID))
            }
            R.styleable.JJImageCategoryCircle_clTopToBottomOf -> {
                clTopToBottom(a.getResourceId(R.styleable.JJImageCategoryCircle_clTopToBottomOf, View.NO_ID))
            }
            R.styleable.JJImageCategoryCircle_clBottomToBottomOf -> {
                clBottomToBottom(a.getResourceId(R.styleable.JJImageCategoryCircle_clBottomToBottomOf, View.NO_ID))
            }
            R.styleable.JJImageCategoryCircle_clBottomToTopOf -> {
                clBottomToTop(a.getResourceId(R.styleable.JJImageCategoryCircle_clBottomToTopOf, View.NO_ID))
            }

        }
    }
    private fun setupMarginCl(a: TypedArray, index:Int){
        var margins = JJMargin()
        when(a.getIndex(index)){
            R.styleable.JJImageCategoryCircle_clMarginEnd ->{
                margins.right = a.getDimension(R.styleable.JJImageCategoryCircle_clMarginEnd,0f).toInt()
            }
            R.styleable.JJImageCategoryCircle_clMarginStart ->{
                margins.left = a.getDimension(R.styleable.JJImageCategoryCircle_clMarginStart,0f).toInt()
            }
            R.styleable.JJImageCategoryCircle_clMarginTop ->{
                margins.top = a.getDimension(R.styleable.JJImageCategoryCircle_clMarginTop,0f).toInt()
            }
            R.styleable.JJImageCategoryCircle_clMarginBottom ->{
                margins.bottom = a.getDimension(R.styleable.JJImageCategoryCircle_clMarginBottom,0f).toInt()
            }

            R.styleable.JJImageCategoryCircle_clMarginEndPercentScreenHeight -> {
                margins.right = JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_clMarginEndPercentScreenHeight,0f))
            }
            R.styleable.JJImageCategoryCircle_clMarginStartPercentScreenHeight -> {
                margins.left = JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_clMarginStartPercentScreenHeight,0f))
            }
            R.styleable.JJImageCategoryCircle_clMarginTopPercentScreenHeight -> {
                margins.top = JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_clMarginTopPercentScreenHeight,0f))
            }
            R.styleable.JJImageCategoryCircle_clMarginBottomPercentScreenHeight -> {
                margins.bottom = JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_clMarginBottomPercentScreenHeight,0f))
            }

            R.styleable.JJImageCategoryCircle_clMarginEndPercentScreenWidth -> {
                margins.right = JJScreen.percentWidth(a.getFloat(R.styleable.JJImageCategoryCircle_clMarginEndPercentScreenWidth,0f))
            }
            R.styleable.JJImageCategoryCircle_clMarginStartPercentScreenWidth -> {
                margins.left = JJScreen.percentWidth(a.getFloat(R.styleable.JJImageCategoryCircle_clMarginStartPercentScreenWidth,0f))
            }
            R.styleable.JJImageCategoryCircle_clMarginTopPercentScreenWidth -> {
                margins.top = JJScreen.percentWidth(a.getFloat(R.styleable.JJImageCategoryCircle_clMarginTopPercentScreenWidth,0f))
            }
            R.styleable.JJImageCategoryCircle_clMarginBottomPercentScreenWidth -> {
                margins.bottom = JJScreen.percentWidth(a.getFloat(R.styleable.JJImageCategoryCircle_clMarginBottomPercentScreenWidth,0f))
            }
            R.styleable.JJImageCategoryCircle_clMargin -> {
                margins = JJMargin.all(a.getDimension(R.styleable.JJImageCategoryCircle_clMargin,0f).toInt())
            }
            R.styleable.JJImageCategoryCircle_clMarginPerScHeight -> {
                margins = JJMargin.all(JJScreen.percentHeight( a.getFloat(R.styleable.JJImageCategoryCircle_clMarginPerScHeight,0f)))
            }
            R.styleable.JJImageCategoryCircle_clMarginPerScWidth -> {
                margins = JJMargin.all(JJScreen.percentWidth( a.getFloat(R.styleable.JJImageCategoryCircle_clMarginPerScWidth,0f)))
            }
            R.styleable.JJImageCategoryCircle_clMarginResponsive -> {
                margins = JJMargin.all(responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_clMarginResponsive))
            }
            R.styleable.JJImageCategoryCircle_clMarginResPerScHeight -> {
                margins = JJMargin.all(responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_clMarginResPerScHeight))
            }
            R.styleable.JJImageCategoryCircle_clMarginResPerScWidth -> {
                margins = JJMargin.all(responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_clMarginResPerScWidth))
            }
            R.styleable.JJImageCategoryCircle_clMarginEndResponsive -> {
                margins.right = responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_clMarginEndResponsive)
            }
            R.styleable.JJImageCategoryCircle_clMarginStartResponsive -> {
                margins.left = responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_clMarginStartResponsive)
            }
            R.styleable.JJImageCategoryCircle_clMarginTopResponsive -> {
                margins.top = responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_clMarginTopResponsive)
            }
            R.styleable.JJImageCategoryCircle_clMarginBottomResponsive -> {
                margins.bottom = responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_clMarginBottomResponsive)
            }

            R.styleable.JJImageCategoryCircle_clMarginEndResPerScHeight -> {
                margins.right = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_clMarginEndResPerScHeight)
            }
            R.styleable.JJImageCategoryCircle_clMarginStartResPerScHeight -> {
                margins.left = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_clMarginStartResPerScHeight)
            }
            R.styleable.JJImageCategoryCircle_clMarginTopResPerScHeight -> {
                margins.top = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_clMarginTopResPerScHeight)
            }
            R.styleable.JJImageCategoryCircle_clMarginBottomResPerScHeight -> {
                margins.bottom = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_clMarginBottomResPerScHeight)
            }

            R.styleable.JJImageCategoryCircle_clMarginEndResPerScWidth -> {
                margins.right = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_clMarginEndResPerScWidth)
            }
            R.styleable.JJImageCategoryCircle_clMarginStartResPerScWidth -> {
                margins.left = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_clMarginStartResPerScWidth)
            }
            R.styleable.JJImageCategoryCircle_clMarginTopResPerScWidth -> {
                margins.top = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_clMarginTopResPerScWidth)
            }
            R.styleable.JJImageCategoryCircle_clMarginBottomResPerScWidth -> {
                margins.bottom = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_clMarginBottomResPerScWidth)
            }
            R.styleable.JJImageCategoryCircle_clMarginVertical -> {
                val mar = a.getDimension(R.styleable.JJImageCategoryCircle_clMarginVertical,0f).toInt()
                margins.top = mar ; margins.bottom = mar
            }
            R.styleable.JJImageCategoryCircle_clMarginVerticalPerScHeight -> {
                val mar = JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_clMarginVerticalPerScHeight,0f))
                margins.top = mar ; margins.bottom = mar
            }
            R.styleable.JJImageCategoryCircle_clMarginVerticalPerScWidth -> {
                val mar = JJScreen.percentWidth(a.getFloat(R.styleable.JJImageCategoryCircle_clMarginVerticalPerScWidth,0f))
                margins.top = mar ; margins.bottom = mar
            }
            R.styleable.JJImageCategoryCircle_clMarginVerticalResponsive -> {
                val mar = responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_clMarginVerticalResponsive)
                margins.top = mar ; margins.bottom = mar
            }
            R.styleable.JJImageCategoryCircle_clMarginVerticalResPerScHeight -> {
                val mar = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_clMarginVerticalResPerScHeight)
                margins.top = mar ; margins.bottom = mar
            }
            R.styleable.JJImageCategoryCircle_clMarginVerticalResPerScWidth -> {
                val mar = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_clMarginVerticalResPerScWidth)
                margins.top = mar ; margins.bottom = mar
            }

            R.styleable.JJImageCategoryCircle_clMarginHorizontal -> {
                val mar = a.getDimension(R.styleable.JJImageCategoryCircle_clMarginHorizontal,0f).toInt()
                margins.left = mar ; margins.right = mar
            }
            R.styleable.JJImageCategoryCircle_clMarginHorizontalPerScHeight -> {
                val mar = JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_clMarginHorizontalPerScHeight,0f))
                margins.left = mar ; margins.right = mar
            }
            R.styleable.JJImageCategoryCircle_clMarginHorizontalPerScWidth -> {
                val mar = JJScreen.percentWidth(a.getFloat(R.styleable.JJImageCategoryCircle_clMarginHorizontalPerScWidth,0f))
                margins.left = mar ; margins.right = mar
            }
            R.styleable.JJImageCategoryCircle_clMarginHorizontalResponsive -> {
                val mar = responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_clMarginHorizontalResponsive)
                margins.left = mar ; margins.right = mar
            }
            R.styleable.JJImageCategoryCircle_clMarginHorizontalResPerScHeight -> {
                val mar = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_clMarginHorizontalResPerScHeight)
                margins.left = mar ; margins.right = mar
            }
            R.styleable.JJImageCategoryCircle_clMarginHorizontalResPerScWidth -> {
                val mar = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_clMarginHorizontalResPerScWidth)
                margins.left = mar ; margins.right = mar
            }

        }
        clMargins(margins)
    }

    private fun setupMarginLpl(a: TypedArray, index:Int) {
        when (a.getIndex(index)) {
            R.styleable.JJImageCategoryCircle_lplMargin -> {
                mlsMargins =
                    JJMargin.all(a.getDimension(R.styleable.JJImageCategoryCircle_lplMargin, 0f).toInt())
            }
            R.styleable.JJImageCategoryCircle_lplMarginVertical -> {
                val mar = a.getDimension(R.styleable.JJImageCategoryCircle_lplMarginVertical, 0f).toInt()
                mlsMargins.top = mar; mlsMargins.bottom = mar
            }
            R.styleable.JJImageCategoryCircle_lplMarginHorizontal -> {
                val mar =
                    a.getDimension(R.styleable.JJImageCategoryCircle_lplMarginHorizontal, 0f).toInt()
                mlsMargins.left = mar; mlsMargins.right = mar
            }

            R.styleable.JJImageCategoryCircle_lplMarginStart -> {
                mlsMargins.left =
                    a.getDimension(R.styleable.JJImageCategoryCircle_lplMarginStart, 0f).toInt()
            }
            R.styleable.JJImageCategoryCircle_lplMarginEnd -> {
                mlsMargins.right =
                    a.getDimension(R.styleable.JJImageCategoryCircle_lplMarginEnd, 0f).toInt()
            }
            R.styleable.JJImageCategoryCircle_lplMarginBottom -> {
                mlsMargins.bottom =
                    a.getDimension(R.styleable.JJImageCategoryCircle_lplMarginBottom, 0f).toInt()
            }
            R.styleable.JJImageCategoryCircle_lplMarginTop -> {
                mlsMargins.top =
                    a.getDimension(R.styleable.JJImageCategoryCircle_lplMarginTop, 0f).toInt()
            }

            R.styleable.JJImageCategoryCircle_lplMarginLeftPerScHeight -> {
                mlsMargins.left = JJScreen.percentHeight(
                    a.getFloat(
                        R.styleable.JJImageCategoryCircle_lplMarginLeftPerScHeight,
                        0f
                    )
                )
            }
            R.styleable.JJImageCategoryCircle_lplMarginRightPerScHeight -> {
                mlsMargins.right = JJScreen.percentHeight(
                    a.getFloat(
                        R.styleable.JJImageCategoryCircle_lplMarginRightPerScHeight,
                        0f
                    )
                )
            }
            R.styleable.JJImageCategoryCircle_lplMarginBottomPerScHeight -> {
                mlsMargins.bottom = JJScreen.percentHeight(
                    a.getFloat(
                        R.styleable.JJImageCategoryCircle_lplMarginBottomPerScHeight,
                        0f
                    )
                )
            }
            R.styleable.JJImageCategoryCircle_lplMarginTopPerScHeight -> {
                mlsMargins.top = JJScreen.percentHeight(
                    a.getFloat(
                        R.styleable.JJImageCategoryCircle_lplMarginTopPerScHeight,
                        0f
                    )
                )
            }

            R.styleable.JJImageCategoryCircle_lplMarginLeftPerScWidth -> {
                mlsMargins.left = JJScreen.percentWidth(
                    a.getFloat(
                        R.styleable.JJImageCategoryCircle_lplMarginLeftPerScWidth,
                        0f
                    )
                )
            }
            R.styleable.JJImageCategoryCircle_lplMarginRightPerScWidth -> {
                mlsMargins.right = JJScreen.percentWidth(
                    a.getFloat(
                        R.styleable.JJImageCategoryCircle_lplMarginRightPerScWidth,
                        0f
                    )
                )
            }
            R.styleable.JJImageCategoryCircle_lplMarginBottomPerScWidth -> {
                mlsMargins.bottom = JJScreen.percentWidth(
                    a.getFloat(
                        R.styleable.JJImageCategoryCircle_lplMarginBottomPerScWidth,
                        0f
                    )
                )
            }
            R.styleable.JJImageCategoryCircle_lplMarginTopPerScWidth -> {
                mlsMargins.top = JJScreen.percentWidth(
                    a.getFloat(
                        R.styleable.JJImageCategoryCircle_lplMarginTopPerScWidth,
                        0f
                    )
                )
            }

            R.styleable.JJImageCategoryCircle_lplMarginTopResponsive -> {
                mlsMargins.top =
                    responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_lplMarginTopResponsive)
            }
            R.styleable.JJImageCategoryCircle_lplMarginLeftResponsive -> {
                mlsMargins.left =
                    responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_lplMarginLeftResponsive)
            }
            R.styleable.JJImageCategoryCircle_lplMarginRightResponsive -> {
                mlsMargins.right = responsiveSizeDimension(
                    a,
                    R.styleable.JJImageCategoryCircle_lplMarginRightResponsive
                )
            }
            R.styleable.JJImageCategoryCircle_lplMarginBottomResponsive -> {
                mlsMargins.bottom = responsiveSizeDimension(
                    a,
                    R.styleable.JJImageCategoryCircle_lplMarginBottomResponsive
                )
            }

            R.styleable.JJImageCategoryCircle_lplMarginTopResPerScWidth -> {
                mlsMargins.top = responsiveSizePercentScreenWidth(
                    a,
                    R.styleable.JJImageCategoryCircle_lplMarginTopResPerScWidth
                )
            }
            R.styleable.JJImageCategoryCircle_lplMarginLeftResPerScWidth -> {
                mlsMargins.left = responsiveSizePercentScreenWidth(
                    a,
                    R.styleable.JJImageCategoryCircle_lplMarginLeftResPerScWidth
                )
            }
            R.styleable.JJImageCategoryCircle_lplMarginRightResPerScWidth -> {
                mlsMargins.right = responsiveSizePercentScreenWidth(
                    a,
                    R.styleable.JJImageCategoryCircle_lplMarginRightResPerScWidth
                )
            }
            R.styleable.JJImageCategoryCircle_lplMarginBottomResPerScWidth -> {
                mlsMargins.bottom = responsiveSizePercentScreenWidth(
                    a,
                    R.styleable.JJImageCategoryCircle_lplMarginBottomResPerScWidth
                )
            }

            R.styleable.JJImageCategoryCircle_lplMarginTopResPerScHeight -> {
                mlsMargins.top = responsiveSizePercentScreenHeight(
                    a,
                    R.styleable.JJImageCategoryCircle_lplMarginTopResPerScHeight
                )
            }
            R.styleable.JJImageCategoryCircle_lplMarginLeftResPerScHeight -> {
                mlsMargins.left = responsiveSizePercentScreenHeight(
                    a,
                    R.styleable.JJImageCategoryCircle_lplMarginLeftResPerScHeight
                )
            }
            R.styleable.JJImageCategoryCircle_lplMarginRightResPerScHeight -> {
                mlsMargins.right = responsiveSizePercentScreenHeight(
                    a,
                    R.styleable.JJImageCategoryCircle_lplMarginRightResPerScHeight
                )
            }
            R.styleable.JJImageCategoryCircle_lplMarginBottomResPerScHeight -> {
                mlsMargins.bottom = responsiveSizePercentScreenHeight(
                    a,
                    R.styleable.JJImageCategoryCircle_lplMarginBottomResPerScHeight
                )
            }
            R.styleable.JJImageCategoryCircle_lplMarginPercentScHeight -> {
                mlsMargins = JJMargin.all(
                    JJScreen.percentHeight(
                        a.getFloat(
                            R.styleable.JJImageCategoryCircle_lplMarginPercentScHeight,
                            0f
                        )
                    )
                )
            }
            R.styleable.JJImageCategoryCircle_lplMarginPercentScWidth -> {
                mlsMargins = JJMargin.all(
                    JJScreen.percentWidth(
                        a.getFloat(
                            R.styleable.JJImageCategoryCircle_lplMarginPercentScWidth,
                            0f
                        )
                    )
                )
            }
            R.styleable.JJImageCategoryCircle_lplMarginResponsive -> {
                mlsMargins = JJMargin.all(
                    responsiveSizeDimension(
                        a,
                        R.styleable.JJImageCategoryCircle_lplMarginResponsive
                    )
                )
            }
            R.styleable.JJImageCategoryCircle_lplMarginResPerScHeight -> {
                mlsMargins = JJMargin.all(
                    responsiveSizePercentScreenHeight(
                        a,
                        R.styleable.JJImageCategoryCircle_lplMarginResPerScHeight
                    )
                )
            }
            R.styleable.JJImageCategoryCircle_lplMarginResPerScWidth -> {
                mlsMargins = JJMargin.all(
                    responsiveSizePercentScreenWidth(
                        a,
                        R.styleable.JJImageCategoryCircle_lplMarginResPerScWidth
                    )
                )
            }

            R.styleable.JJImageCategoryCircle_lplMarginVerticalPerScHeight -> {
                val mar = JJScreen.percentHeight(
                    a.getFloat(
                        R.styleable.JJImageCategoryCircle_lplMarginVerticalPerScHeight,
                        0f
                    )
                )
                mlsMargins.top = mar; mlsMargins.bottom = mar
            }
            R.styleable.JJImageCategoryCircle_lplMarginVerticalPerScWidth -> {
                val mar = JJScreen.percentWidth(
                    a.getFloat(
                        R.styleable.JJImageCategoryCircle_lplMarginVerticalPerScWidth,
                        0f
                    )
                )
                mlsMargins.top = mar; mlsMargins.bottom = mar
            }
            R.styleable.JJImageCategoryCircle_lplMarginVerticalResponsive -> {
                val mar = responsiveSizeDimension(
                    a,
                    R.styleable.JJImageCategoryCircle_lplMarginVerticalResponsive
                )
                mlsMargins.top = mar; mlsMargins.bottom = mar
            }
            R.styleable.JJImageCategoryCircle_lplMarginVerticalResPerScWidth -> {
                val mar = responsiveSizePercentScreenWidth(
                    a,
                    R.styleable.JJImageCategoryCircle_lplMarginVerticalResPerScWidth
                )
                mlsMargins.top = mar; mlsMargins.bottom = mar
            }
            R.styleable.JJImageCategoryCircle_lplMarginVerticalResPerScHeight -> {
                val mar = responsiveSizePercentScreenHeight(
                    a,
                    R.styleable.JJImageCategoryCircle_lplMarginVerticalResPerScHeight
                )
                mlsMargins.top = mar; mlsMargins.bottom = mar
            }


            R.styleable.JJImageCategoryCircle_lplMarginHorizontalPerScHeight -> {
                val mar = JJScreen.percentHeight(
                    a.getFloat(
                        R.styleable.JJImageCategoryCircle_lplMarginHorizontalPerScHeight,
                        0f
                    )
                )
                mlsMargins.left = mar; mlsMargins.right = mar
            }
            R.styleable.JJImageCategoryCircle_lplMarginHorizontalPerScWidth -> {
                val mar = JJScreen.percentWidth(
                    a.getFloat(
                        R.styleable.JJImageCategoryCircle_lplMarginHorizontalPerScWidth,
                        0f
                    )
                )
                mlsMargins.left = mar; mlsMargins.right = mar
            }
            R.styleable.JJImageCategoryCircle_lplMarginHorizontalResponsive -> {
                val mar = responsiveSizeDimension(
                    a,
                    R.styleable.JJImageCategoryCircle_lplMarginHorizontalResponsive
                )
                mlsMargins.left = mar; mlsMargins.right = mar
            }
            R.styleable.JJImageCategoryCircle_lplMarginHorizontalResPerScWidth -> {
                val mar = responsiveSizePercentScreenWidth(
                    a,
                    R.styleable.JJImageCategoryCircle_lplMarginHorizontalResPerScWidth
                )
                mlsMargins.left = mar; mlsMargins.right = mar
            }
            R.styleable.JJImageCategoryCircle_lplMarginHorizontalResPerScHeight -> {
                val mar = responsiveSizePercentScreenHeight(
                    a,
                    R.styleable.JJImageCategoryCircle_lplMarginHorizontalResPerScHeight
                )
                mlsMargins.left = mar; mlsMargins.right = mar
            }

        }
    }
    private fun setupPaddingLpl(a: TypedArray, index: Int){
        when(a.getIndex(index)){
            R.styleable.JJImageCategoryCircle_lplPadding -> {
                mlsPadding = JJPadding.all( a.getDimension(R.styleable.JJImageCategoryCircle_lplPadding,0f).toInt())
            }
            R.styleable.JJImageCategoryCircle_lplPaddingVertical -> {
                val mar = a.getDimension(R.styleable.JJImageCategoryCircle_lplPaddingVertical,0f).toInt()
                mlsPadding.top = mar ; mlsPadding.bottom = mar
            }
            R.styleable.JJImageCategoryCircle_lplPaddingHorizontal -> {
                val mar = a.getDimension(R.styleable.JJImageCategoryCircle_lplPaddingHorizontal,0f).toInt()
                mlsPadding.left = mar ; mlsPadding.right = mar
            }
            R.styleable.JJImageCategoryCircle_lplPaddingStart -> {
                mlsPadding.left = a.getDimension(R.styleable.JJImageCategoryCircle_lplPaddingStart,0f).toInt()
            }
            R.styleable.JJImageCategoryCircle_lplPaddingEnd -> {
                mlsPadding.right = a.getDimension(R.styleable.JJImageCategoryCircle_lplPaddingEnd,0f).toInt()
            }
            R.styleable.JJImageCategoryCircle_lplPaddingTop -> {
                mlsPadding.top = a.getDimension(R.styleable.JJImageCategoryCircle_lplPaddingTop,0f).toInt()
            }
            R.styleable.JJImageCategoryCircle_lplPaddingBottom -> {
                mlsPadding.bottom = a.getDimension(R.styleable.JJImageCategoryCircle_lplPaddingBottom,0f).toInt()
            }

            R.styleable.JJImageCategoryCircle_lplPaddingTopPerScHeight -> {
                mlsPadding.top = JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_lplPaddingTopPerScHeight,0f))
            }
            R.styleable.JJImageCategoryCircle_lplPaddingLeftPerScHeight -> {
                mlsPadding.left = JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_lplPaddingLeftPerScHeight,0f))
            }
            R.styleable.JJImageCategoryCircle_lplPaddingRightPerScHeight -> {
                mlsPadding.right = JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_lplPaddingRightPerScHeight,0f))
            }
            R.styleable.JJImageCategoryCircle_lplPaddingBottomPerScHeight -> {
                mlsPadding.bottom = JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_lplPaddingBottomPerScHeight,0f))
            }

            R.styleable.JJImageCategoryCircle_lplPaddingTopPerScWidth -> {
                mlsPadding.top = JJScreen.percentWidth(a.getFloat(R.styleable.JJImageCategoryCircle_lplPaddingTopPerScWidth,0f))
            }
            R.styleable.JJImageCategoryCircle_lplPaddingLeftPerScWidth -> {
                mlsPadding.left = JJScreen.percentWidth(a.getFloat(R.styleable.JJImageCategoryCircle_lplPaddingLeftPerScWidth,0f))
            }
            R.styleable.JJImageCategoryCircle_lplPaddingRightPerScWidth -> {
                mlsPadding.right = JJScreen.percentWidth(a.getFloat(R.styleable.JJImageCategoryCircle_lplPaddingRightPerScWidth,0f))
            }
            R.styleable.JJImageCategoryCircle_lplPaddingBottomPerScWidth -> {
                mlsPadding.bottom = JJScreen.percentWidth(a.getFloat(R.styleable.JJImageCategoryCircle_lplPaddingBottomPerScWidth,0f))
            }

            R.styleable.JJImageCategoryCircle_lplPaddingTopResponsive -> {
                mlsPadding.top = responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_lplPaddingTopResponsive)
            }
            R.styleable.JJImageCategoryCircle_lplPaddingLeftResponsive -> {
                mlsPadding.left = responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_lplPaddingLeftResponsive)
            }
            R.styleable.JJImageCategoryCircle_lplPaddingRightResponsive -> {
                mlsPadding.right = responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_lplPaddingRightResponsive)
            }
            R.styleable.JJImageCategoryCircle_lplPaddingBottomResponsive -> {
                mlsPadding.bottom = responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_lplPaddingBottomResponsive)
            }

            R.styleable.JJImageCategoryCircle_lplPaddingTopResPerScWidth -> {
                mlsPadding.top = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_lplPaddingTopResPerScWidth)
            }
            R.styleable.JJImageCategoryCircle_lplPaddingLeftResPerScWidth -> {
                mlsPadding.left = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_lplPaddingLeftResPerScWidth)
            }
            R.styleable.JJImageCategoryCircle_lplPaddingRightResPerScWidth -> {
                mlsPadding.right = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_lplPaddingRightResPerScWidth)
            }
            R.styleable.JJImageCategoryCircle_lplPaddingBottomResPerScWidth -> {
                mlsPadding.bottom = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_lplPaddingBottomResPerScWidth)
            }

            R.styleable.JJImageCategoryCircle_lplPaddingTopResPerScHeight -> {
                mlsPadding.top = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_lplPaddingTopResPerScHeight)
            }
            R.styleable.JJImageCategoryCircle_lplPaddingLeftResPerScHeight -> {
                mlsPadding.left = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_lplPaddingLeftResPerScHeight)
            }
            R.styleable.JJImageCategoryCircle_lplPaddingRightResPerScHeight -> {
                mlsPadding.right = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_lplPaddingRightResPerScHeight)
            }
            R.styleable.JJImageCategoryCircle_lplPaddingBottomResPerScHeight -> {
                mlsPadding.bottom = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_lplPaddingBottomResPerScHeight)
            }
            R.styleable.JJImageCategoryCircle_lplPaddingPercentScHeight->{
                mlsPadding = JJPadding.all(JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_lplPaddingPercentScHeight,0f)))
            }
            R.styleable.JJImageCategoryCircle_lplPaddingPercentScWidth->{
                mlsPadding = JJPadding.all(JJScreen.percentWidth(a.getFloat(R.styleable.JJImageCategoryCircle_lplPaddingPercentScWidth,0f)))
            }
            R.styleable.JJImageCategoryCircle_lplPaddingResponsive->{
                mlsPadding = JJPadding.all(responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_lplPaddingResponsive))
            }
            R.styleable.JJImageCategoryCircle_lplPaddingResPerScHeight->{
                mlsPadding = JJPadding.all(responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_lplPaddingResPerScHeight))
            }
            R.styleable.JJImageCategoryCircle_lplPaddingResPerScWidth->{
                mlsPadding = JJPadding.all(responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_lplPaddingResPerScWidth))
            }

            R.styleable.JJImageCategoryCircle_lplPaddingVerticalPerScHeight -> {
                val mar = JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_lplPaddingVerticalPerScHeight,0f))
                mlsPadding.top = mar ; mlsPadding.bottom = mar
            }
            R.styleable.JJImageCategoryCircle_lplPaddingVerticalPerScWidth -> {
                val mar = JJScreen.percentWidth(a.getFloat(R.styleable.JJImageCategoryCircle_lplPaddingVerticalPerScWidth,0f))
                mlsPadding.top = mar ; mlsPadding.bottom = mar
            }
            R.styleable.JJImageCategoryCircle_lplPaddingVerticalResponsive -> {
                val mar = responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_lplPaddingVerticalResponsive)
                mlsPadding.top = mar ; mlsPadding.bottom = mar
            }
            R.styleable.JJImageCategoryCircle_lplPaddingVerticalResPerScWidth -> {
                val mar = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_lplPaddingVerticalResPerScWidth)
                mlsPadding.top = mar ; mlsPadding.bottom = mar
            }
            R.styleable.JJImageCategoryCircle_lplPaddingVerticalResPerScHeight -> {
                val mar = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_lplPaddingVerticalResPerScHeight)
                mlsPadding.top = mar ; mlsPadding.bottom = mar
            }

            R.styleable.JJImageCategoryCircle_lplPaddingHorizontalPerScHeight -> {
                val mar = JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_lplPaddingHorizontalPerScHeight,0f))
                mlsPadding.left = mar ; mlsPadding.right = mar
            }
            R.styleable.JJImageCategoryCircle_lplPaddingHorizontalPerScWidth -> {
                val mar = JJScreen.percentWidth(a.getFloat(R.styleable.JJImageCategoryCircle_lplPaddingHorizontalPerScWidth,0f))
                mlsPadding.left = mar ; mlsPadding.right = mar
            }
            R.styleable.JJImageCategoryCircle_lplPaddingHorizontalResponsive -> {
                val mar = responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_lplPaddingHorizontalResponsive)
                mlsPadding.left = mar ; mlsPadding.right = mar
            }
            R.styleable.JJImageCategoryCircle_lplPaddingHorizontalResPerScWidth -> {
                val mar = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_lplPaddingHorizontalResPerScWidth)
                mlsPadding.left = mar ; mlsPadding.right = mar
            }
            R.styleable.JJImageCategoryCircle_lplPaddingHorizontalResPerScHeight -> {
                val mar = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_lplPaddingHorizontalResPerScHeight)
                mlsPadding.left = mar ; mlsPadding.right = mar
            }
        }
    }
    private fun setupSizeLpl(a: TypedArray, index:Int){
        when (a.getIndex(index)) {
            R.styleable.JJImageCategoryCircle_layout_height_landscape -> {
                mlsHeight = a.getLayoutDimension(R.styleable.JJImageCategoryCircle_layout_height_landscape,0)
            }
            R.styleable.JJImageCategoryCircle_layout_width_landscape -> {
                mlsWidth = a.getLayoutDimension(R.styleable.JJImageCategoryCircle_layout_width_landscape,0)
            }
            R.styleable.JJImageCategoryCircle_lplHeightPercentScreenWidth -> {
                mlsHeight = JJScreen.percentWidth( a.getFloat(R.styleable.JJImageCategoryCircle_lplHeightPercentScreenWidth,0f))
            }
            R.styleable.JJImageCategoryCircle_lplWidthPercentScreenWidth -> {
                mlsWidth = JJScreen.percentWidth( a.getFloat(R.styleable.JJImageCategoryCircle_lplWidthPercentScreenWidth,0f))
            }

            R.styleable.JJImageCategoryCircle_lplHeightPercentScreenHeight -> {
                mlsHeight = JJScreen.percentHeight( a.getFloat(R.styleable.JJImageCategoryCircle_lplHeightPercentScreenHeight,0f))
            }
            R.styleable.JJImageCategoryCircle_lplWidthPercentScreenHeight -> {
                mlsWidth = JJScreen.percentHeight( a.getFloat(R.styleable.JJImageCategoryCircle_lplWidthPercentScreenHeight,0f))
            }
            R.styleable.JJImageCategoryCircle_lplHeightResponsive -> {
                mlsHeight = responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_lplHeightResponsive)
            }
            R.styleable.JJImageCategoryCircle_lplWidthResponsive -> {
                mlsWidth = responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_lplWidthResponsive)
            }
            R.styleable.JJImageCategoryCircle_lplHeightResponsivePercentScreenHeight -> {
                mlsHeight = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_lplHeightResponsivePercentScreenHeight)
            }
            R.styleable.JJImageCategoryCircle_lplWidthResponsivePercentScreenHeight -> {
                mlsWidth = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_lplWidthResponsivePercentScreenHeight)
            }
            R.styleable.JJImageCategoryCircle_lplHeightResponsivePercentScreenWidth -> {
                mlsHeight = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_lplHeightResponsivePercentScreenWidth)
            }
            R.styleable.JJImageCategoryCircle_lplWidthResponsivePercentScreenWidth -> {
                mlsWidth = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_lplWidthResponsivePercentScreenWidth)
            }

        }
    }

    private fun setupMarginCll(a: TypedArray, index:Int){
        var lsMargins = JJMargin()
        when (a.getIndex(index)) {
            R.styleable.JJImageCategoryCircle_cllMarginEnd -> {
                lsMargins.right = a.getDimension(R.styleable.JJImageCategoryCircle_cllMarginEnd,0f).toInt()
            }
            R.styleable.JJImageCategoryCircle_cllMarginStart -> {
                lsMargins.left = a.getDimension(R.styleable.JJImageCategoryCircle_cllMarginStart,0f).toInt()
            }
            R.styleable.JJImageCategoryCircle_cllMarginTop -> {
                lsMargins.top = a.getDimension(R.styleable.JJImageCategoryCircle_cllMarginTop,0f).toInt()
            }
            R.styleable.JJImageCategoryCircle_cllMarginBottom -> {
                lsMargins.bottom = a.getDimension(R.styleable.JJImageCategoryCircle_cllMarginBottom,0f).toInt()
            }

            R.styleable.JJImageCategoryCircle_cllMarginEndPercentScreenHeight -> {
                lsMargins.right = JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_cllMarginEndPercentScreenHeight,0f))
            }
            R.styleable.JJImageCategoryCircle_cllMarginStartPercentScreenHeight -> {
                lsMargins.left = JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_cllMarginStartPercentScreenHeight,0f))
            }
            R.styleable.JJImageCategoryCircle_cllMarginTopPercentScreenHeight -> {
                lsMargins.top = JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_cllMarginTopPercentScreenHeight,0f))
            }
            R.styleable.JJImageCategoryCircle_cllMarginBottomPercentScreenHeight -> {
                lsMargins.bottom = JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_cllMarginBottomPercentScreenHeight,0f))
            }

            R.styleable.JJImageCategoryCircle_cllMarginEndPercentScreenWidth -> {
                lsMargins.right = JJScreen.percentWidth(a.getFloat(R.styleable.JJImageCategoryCircle_cllMarginEndPercentScreenWidth,0f))
            }
            R.styleable.JJImageCategoryCircle_cllMarginStartPercentScreenWidth -> {
                lsMargins.left = JJScreen.percentWidth(a.getFloat(R.styleable.JJImageCategoryCircle_cllMarginStartPercentScreenWidth,0f))
            }
            R.styleable.JJImageCategoryCircle_cllMarginTopPercentScreenWidth -> {
                lsMargins.top = JJScreen.percentWidth(a.getFloat(R.styleable.JJImageCategoryCircle_cllMarginTopPercentScreenWidth,0f))
            }
            R.styleable.JJImageCategoryCircle_cllMarginBottomPercentScreenWidth -> {
                lsMargins.bottom = JJScreen.percentWidth(a.getFloat(R.styleable.JJImageCategoryCircle_cllMarginBottomPercentScreenWidth,0f))
            }

            R.styleable.JJImageCategoryCircle_cllMargin -> {
                lsMargins = JJMargin.all(a.getDimension(R.styleable.JJImageCategoryCircle_cllMargin,0f).toInt())
            }
            R.styleable.JJImageCategoryCircle_cllMarginPerScHeight -> {
                lsMargins = JJMargin.all(JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_cllMarginPerScHeight,0f)))
            }
            R.styleable.JJImageCategoryCircle_cllMarginPerScWidth -> {
                lsMargins = JJMargin.all(JJScreen.percentWidth(a.getFloat(R.styleable.JJImageCategoryCircle_cllMarginPerScWidth,0f)))
            }
            R.styleable.JJImageCategoryCircle_cllMarginResponsive -> {
                lsMargins = JJMargin.all(responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_cllMarginResponsive))
            }
            R.styleable.JJImageCategoryCircle_cllMarginResPerScHeight -> {
                lsMargins = JJMargin.all(responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_cllMarginResPerScHeight))
            }
            R.styleable.JJImageCategoryCircle_cllMarginResPerScWidth -> {
                lsMargins = JJMargin.all(responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_cllMarginResPerScWidth))
            }
            R.styleable.JJImageCategoryCircle_cllMarginEndResponsive ->{
                lsMargins.right = responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_cllMarginEndResponsive)
            }
            R.styleable.JJImageCategoryCircle_cllMarginStartResponsive ->{
                lsMargins.left = responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_cllMarginStartResponsive)
            }
            R.styleable.JJImageCategoryCircle_cllMarginTopResponsive ->{
                lsMargins.top = responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_cllMarginTopResponsive)
            }
            R.styleable.JJImageCategoryCircle_cllMarginBottomResponsive ->{
                lsMargins.bottom = responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_cllMarginBottomResponsive)
            }

            R.styleable.JJImageCategoryCircle_cllMarginEndResPerScHeight ->{
                lsMargins.right = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_cllMarginEndResPerScHeight)
            }
            R.styleable.JJImageCategoryCircle_cllMarginStartResPerScHeight ->{
                lsMargins.left = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_cllMarginStartResPerScHeight)
            }
            R.styleable.JJImageCategoryCircle_cllMarginTopResPerScHeight ->{
                lsMargins.top = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_cllMarginTopResPerScHeight)
            }
            R.styleable.JJImageCategoryCircle_cllMarginBottomResPerScHeight ->{
                lsMargins.bottom = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_cllMarginBottomResPerScHeight)
            }

            R.styleable.JJImageCategoryCircle_cllMarginEndResPerScWidth ->{
                lsMargins.right = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_cllMarginEndResPerScWidth)
            }
            R.styleable.JJImageCategoryCircle_cllMarginStartResPerScWidth ->{
                lsMargins.left = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_cllMarginStartResPerScWidth)
            }
            R.styleable.JJImageCategoryCircle_cllMarginTopResPerScWidth ->{
                lsMargins.top = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_cllMarginTopResPerScWidth)
            }
            R.styleable.JJImageCategoryCircle_cllMarginBottomResPerScWidth ->{
                lsMargins.bottom = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_cllMarginBottomResPerScWidth)
            }

            R.styleable.JJImageCategoryCircle_cllMarginVertical->{
                val mar = a.getDimension(R.styleable.JJImageCategoryCircle_cllMarginVertical,0f).toInt()
                lsMargins.top = mar ; lsMargins.bottom = mar
            }
            R.styleable.JJImageCategoryCircle_cllMarginVerticalPerScHeight->{
                val mar = JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_cllMarginVerticalPerScHeight,0f))
                lsMargins.top = mar ; lsMargins.bottom = mar
            }
            R.styleable.JJImageCategoryCircle_cllMarginVerticalPerScWidth->{
                val mar = JJScreen.percentWidth(a.getFloat(R.styleable.JJImageCategoryCircle_cllMarginVerticalPerScWidth,0f))
                lsMargins.top = mar ; lsMargins.bottom = mar
            }
            R.styleable.JJImageCategoryCircle_cllMarginVerticalResponsive->{
                val mar = responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_cllMarginVerticalResponsive)
                lsMargins.top = mar ; lsMargins.bottom = mar
            }
            R.styleable.JJImageCategoryCircle_cllMarginVerticalResPerScHeight->{
                val mar = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_cllMarginVerticalResPerScHeight)
                lsMargins.top = mar ; lsMargins.bottom = mar
            }
            R.styleable.JJImageCategoryCircle_cllMarginVerticalResPerScWidth->{
                val mar = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_cllMarginVerticalResPerScWidth)
                lsMargins.top = mar ; lsMargins.bottom = mar
            }

            R.styleable.JJImageCategoryCircle_cllMarginHorizontal->{
                val mar = a.getDimension(R.styleable.JJImageCategoryCircle_cllMarginHorizontal,0f).toInt()
                lsMargins.top = mar ; lsMargins.bottom = mar
            }
            R.styleable.JJImageCategoryCircle_cllMarginHorizontalPerScHeight->{
                val mar = JJScreen.percentHeight(a.getFloat(R.styleable.JJImageCategoryCircle_cllMarginHorizontalPerScHeight,0f))
                lsMargins.left = mar ; lsMargins.right = mar
            }
            R.styleable.JJImageCategoryCircle_cllMarginHorizontalPerScWidth->{
                val mar = JJScreen.percentWidth(a.getFloat(R.styleable.JJImageCategoryCircle_cllMarginHorizontalPerScWidth,0f))
                lsMargins.left = mar ; lsMargins.right = mar
            }
            R.styleable.JJImageCategoryCircle_cllMarginHorizontalResponsive->{
                val mar = responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_cllMarginHorizontalResponsive)
                lsMargins.left = mar ; lsMargins.right = mar
            }
            R.styleable.JJImageCategoryCircle_cllMarginHorizontalResPerScHeight->{
                val mar = responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_cllMarginHorizontalResPerScHeight)
                lsMargins.left = mar ; lsMargins.right = mar
            }
            R.styleable.JJImageCategoryCircle_cllMarginHorizontalResPerScWidth->{
                val mar = responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_cllMarginHorizontalResPerScWidth)
                lsMargins.left = mar ; lsMargins.right = mar
            }
        }
        cllMargins(lsMargins)
    }
    private fun setupSizeCll(a: TypedArray, index:Int){
        when (a.getIndex(index)) {
            R.styleable.JJImageCategoryCircle_layout_height_landscape->{
                val value = a.getLayoutDimension(R.styleable.JJImageCategoryCircle_layout_height_landscape,0)
                if(value > 0 || value == -2 ) cllHeight(value)
            }
            R.styleable.JJImageCategoryCircle_layout_width_landscape->{
                val value = a.getLayoutDimension(R.styleable.JJImageCategoryCircle_layout_width_landscape,0)
                if(value > 0 || value == -2 ) cllWidth(value)
            }
            R.styleable.JJImageCategoryCircle_cllHeightPercent -> {
                cllPercentHeight( a.getFloat(R.styleable.JJImageCategoryCircle_cllHeightPercent,0f))
            }
            R.styleable.JJImageCategoryCircle_cllWidthPercent -> {
                cllPercentWidth( a.getFloat(R.styleable.JJImageCategoryCircle_cllWidthPercent,0f))
            }
            R.styleable.JJImageCategoryCircle_cllHeightPercentScreenWidth -> {
                cllHeight(JJScreen.percentWidth( a.getFloat(R.styleable.JJImageCategoryCircle_cllHeightPercentScreenWidth,0f)))
            }
            R.styleable.JJImageCategoryCircle_cllWidthPercentScreenWidth -> {
                cllWidth(JJScreen.percentWidth( a.getFloat(R.styleable.JJImageCategoryCircle_cllWidthPercentScreenWidth,0f)))
            }
            R.styleable.JJImageCategoryCircle_cllHeightPercentScreenHeight -> {
                cllHeight(JJScreen.percentHeight( a.getFloat(R.styleable.JJImageCategoryCircle_cllHeightPercentScreenHeight,0f)))
            }
            R.styleable.JJImageCategoryCircle_cllWidthPercentScreenHeight -> {
                cllWidth(JJScreen.percentHeight( a.getFloat(R.styleable.JJImageCategoryCircle_cllWidthPercentScreenHeight,0f)))
            }
            R.styleable.JJImageCategoryCircle_cllHeightResponsive -> {
                cllHeight(responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_cllHeightResponsive))
            }
            R.styleable.JJImageCategoryCircle_cllWidthResponsive -> {
                cllWidth(responsiveSizeDimension(a, R.styleable.JJImageCategoryCircle_cllWidthResponsive))
            }

            R.styleable.JJImageCategoryCircle_cllHeightResponsivePercentScreenHeight -> {
                cllHeight(responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_cllHeightResponsivePercentScreenHeight))
            }
            R.styleable.JJImageCategoryCircle_cllWidthResponsivePercentScreenHeight -> {
                cllWidth(responsiveSizePercentScreenHeight(a, R.styleable.JJImageCategoryCircle_cllWidthResponsivePercentScreenHeight))
            }
            R.styleable.JJImageCategoryCircle_cllWidthResponsivePercentScreenWidth -> {
                cllHeight(responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_cllWidthResponsivePercentScreenWidth))
            }
            R.styleable.JJImageCategoryCircle_cllHeightResponsivePercentScreenWidth -> {
                cllWidth(responsiveSizePercentScreenWidth(a, R.styleable.JJImageCategoryCircle_cllHeightResponsivePercentScreenWidth))
            }
        }

    }
    private fun setupAnchorsCll(a: TypedArray, index:Int){
        when (a.getIndex(index)) {
            R.styleable.JJImageCategoryCircle_cllFillParent ->{
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_cllFillParent,false)) cllFillParent()
            }
            R.styleable.JJImageCategoryCircle_cllFillParentHorizontally ->{
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_cllFillParentHorizontally,false)) cllFillParentHorizontally()
            }
            R.styleable.JJImageCategoryCircle_cllFillParentVertically ->{
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_cllFillParentVertically,false)) cllFillParentVertically()
            }
            R.styleable.JJImageCategoryCircle_cllCenterInParent ->{
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_cllCenterInParent,false)) cllCenterInParent()
            }
            R.styleable.JJImageCategoryCircle_cllCenterInParentHorizontally ->{
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_cllCenterInParentHorizontally,false)) cllCenterInParentHorizontally()
            }
            R.styleable.JJImageCategoryCircle_cllCenterInParentVertically ->{
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_cllCenterInParentVertically,false)) cllCenterInParentVertically()
            }
            R.styleable.JJImageCategoryCircle_cllCenterInParentTopVertically ->{
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_cllCenterInParentTopVertically,false)) cllCenterInParentTopVertically()
            }
            R.styleable.JJImageCategoryCircle_cllCenterInParentBottomVertically ->{
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_cllCenterInParentBottomVertically,false)) cllCenterInParentBottomVertically()
            }
            R.styleable.JJImageCategoryCircle_cllCenterInParentStartHorizontally ->{
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_cllCenterInParentStartHorizontally,false)) cllCenterInParentStartHorizontally()
            }
            R.styleable.JJImageCategoryCircle_cllCenterInParentEndHorizontally ->{
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_cllCenterInParentEndHorizontally,false)) cllCenterInParentEndHorizontally()
            }
            R.styleable.JJImageCategoryCircle_cllCenterInTopVerticallyOf ->{
                cllCenterInTopVertically(a.getResourceId(
                    R.styleable.JJImageCategoryCircle_cllCenterInTopVerticallyOf,
                    View.NO_ID))
            }
            R.styleable.JJImageCategoryCircle_cllCenterInBottomVerticallyOf ->{
                cllCenterInBottomVertically(a.getResourceId(
                    R.styleable.JJImageCategoryCircle_cllCenterInBottomVerticallyOf,
                    View.NO_ID))
            }
            R.styleable.JJImageCategoryCircle_cllCenterInStartHorizontallyOf ->{
                cllCenterInStartHorizontally(a.getResourceId(
                    R.styleable.JJImageCategoryCircle_cllCenterInStartHorizontallyOf,
                    View.NO_ID))
            }
            R.styleable.JJImageCategoryCircle_cllCenterInEndHorizontallyOf ->{
                cllCenterInEndHorizontally(a.getResourceId(
                    R.styleable.JJImageCategoryCircle_cllCenterInEndHorizontallyOf,
                    View.NO_ID))
            }
            R.styleable.JJImageCategoryCircle_cllCenterVerticallyOf ->{
                cllCenterVerticallyOf(a.getResourceId(
                    R.styleable.JJImageCategoryCircle_cllCenterVerticallyOf,
                    View.NO_ID))
            }
            R.styleable.JJImageCategoryCircle_cllCenterHorizontallyOf ->{
                cllCenterHorizontallyOf(a.getResourceId(
                    R.styleable.JJImageCategoryCircle_cllCenterHorizontallyOf,
                    View.NO_ID))
            }
            R.styleable.JJImageCategoryCircle_cllVerticalBias -> {
                cllVerticalBias(a.getFloat(R.styleable.JJImageCategoryCircle_cllVerticalBias,0.5f))
            }
            R.styleable.JJImageCategoryCircle_cllHorizontalBias -> {
                cllHorizontalBias(a.getFloat(R.styleable.JJImageCategoryCircle_cllHorizontalBias,0.5f))
            }

            R.styleable.JJImageCategoryCircle_cllStartToStartParent ->{
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_cllStartToStartParent,false)) cllStartToStartParent()
            }
            R.styleable.JJImageCategoryCircle_cllStartToEndParent ->{
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_cllStartToEndParent,false)) cllStartToEndParent()
            }
            R.styleable.JJImageCategoryCircle_cllEndToEndParent ->{
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_cllEndToEndParent,false)) cllEndToEndParent()
            }
            R.styleable.JJImageCategoryCircle_cllEndToStartParent ->{
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_cllEndToStartParent,false)) cllEndToStartParent()
            }
            R.styleable.JJImageCategoryCircle_cllTopToTopParent ->{
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_cllTopToTopParent,false)) cllTopToTopParent()
            }
            R.styleable.JJImageCategoryCircle_cllTopToBottomParent ->{
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_cllTopToBottomParent,false)) cllTopToBottomParent()
            }
            R.styleable.JJImageCategoryCircle_cllBottomToBottomParent ->{
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_cllBottomToBottomParent,false)) cllBottomToBottomParent()
            }
            R.styleable.JJImageCategoryCircle_cllBottomToTopParent ->{
                if(a.getBoolean(R.styleable.JJImageCategoryCircle_cllBottomToTopParent,false)) cllBottomToTopParent()
            }

            R.styleable.JJImageCategoryCircle_cllStartToStartOf -> {
                cllStartToStart(a.getResourceId(R.styleable.JJImageCategoryCircle_cllStartToStartOf, View.NO_ID))
            }
            R.styleable.JJImageCategoryCircle_cllStartToEndOf -> {
                cllStartToEnd(a.getResourceId(R.styleable.JJImageCategoryCircle_cllStartToEndOf, View.NO_ID))
            }
            R.styleable.JJImageCategoryCircle_cllEndToEndOf -> {
                cllEndToEnd(a.getResourceId(R.styleable.JJImageCategoryCircle_cllEndToEndOf, View.NO_ID))
            }
            R.styleable.JJImageCategoryCircle_cllEndToStartOf -> {
                cllEndToStart(a.getResourceId(R.styleable.JJImageCategoryCircle_cllEndToStartOf, View.NO_ID))
            }
            R.styleable.JJImageCategoryCircle_cllTopToTopOf -> {
                cllTopToTop(a.getResourceId(R.styleable.JJImageCategoryCircle_cllTopToTopOf, View.NO_ID))
            }
            R.styleable.JJImageCategoryCircle_cllTopToBottomOf -> {
                cllTopToBottom(a.getResourceId(R.styleable.JJImageCategoryCircle_cllTopToBottomOf, View.NO_ID))
            }
            R.styleable.JJImageCategoryCircle_cllBottomToBottomOf -> {
                cllBottomToBottom(a.getResourceId(R.styleable.JJImageCategoryCircle_cllBottomToBottomOf, View.NO_ID))
            }
            R.styleable.JJImageCategoryCircle_cllBottomToTopOf -> {
                cllBottomToTop(a.getResourceId(R.styleable.JJImageCategoryCircle_cllBottomToTopOf, View.NO_ID))
            }

        }

    }

    private fun setupInitConstraint(){
        mConstraintSet.constrainWidth(id,0)
        mConstraintSet.constrainHeight(id,0)
        mConstraintSetLandScape.constrainWidth(id,0)
        mConstraintSetLandScape.constrainHeight(id,0)
    }
    private fun responsiveSizeDimension(a: TypedArray, style:Int) : Int {
        val t = resources.obtainTypedArray(a.getResourceId(style,
            View.NO_ID))
        val re = JJScreen.responsiveSize(t.getDimension(0, 0f).toInt(),
            t.getDimension(1, 0f).toInt(),
            t.getDimension(2, 0f).toInt(),
            t.getDimension(3, 0f).toInt())
        t.recycle()
        return re
    }
    private fun responsiveSizePercentScreenWidth(a: TypedArray, style:Int) : Int {
        val t = resources.obtainTypedArray(a.getResourceId(style,
            View.NO_ID))
        val re = JJScreen.responsiveSizePercentScreenWidth(t.getFloat(0, 0f),
            t.getFloat(1, 0f),
            t.getFloat(2, 0f),
            t.getFloat(3, 0f))
        t.recycle()
        return re
    }
    private fun responsiveSizePercentScreenHeight(a: TypedArray, style:Int) : Int {
        val t = resources.obtainTypedArray(a.getResourceId(style,
            View.NO_ID))
        val re = JJScreen.responsiveSizePercentScreenHeight(t.getFloat(0, 0f),
            t.getFloat(1, 0f),
            t.getFloat(2, 0f),
            t.getFloat(3, 0f))
        t.recycle()
        return re
    }


    var mInit = true
    private var mlsHeight = 0
    private var mlsWidth = 0
    private var mlsMargins = JJMargin()
    private var mlsPadding = JJPadding()
    private var mConfigurationChanged = false
    private var mlpHeight = 0
    private var mlpWidth = 0
    private var mlpMargins = JJMargin()
    private var mlpPadding = JJPadding()
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val isLandScale = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        if(mInit){
            if(isLandScale && mSupportLandScape) applyLayoutParamsLandScape() else applyLayoutParamsPortrait()
            mInit = false
        }
    }
    private fun applyLayoutParamsPortrait(){
        val csParent = parent as? ConstraintLayout
        val mlParent = parent as? MotionLayout
        when {
            mlParent != null -> Log.e("JJKIT","PARENT MOTION LAYOUT")
            csParent != null -> {
                if(!mIgnoreCl){
                    clApply()
                }
            }
            else -> {
                layoutParams.height = mlpHeight
                layoutParams.width = mlpWidth
                val margin = layoutParams as? ViewGroup.MarginLayoutParams
                margin?.topMargin = mlpMargins.top
                margin?.marginStart =  mlpMargins.left
                margin?.marginEnd =  mlpMargins.right
                margin?.bottomMargin =  mlpMargins.bottom

            }
        }

        var pl = paddingLeft
        var pr = paddingRight
        if (paddingStart > 0) pl = paddingStart
        if (paddingEnd > 0) pr = paddingEnd

        if(mlpPadding.top <= 0 && paddingTop > 0) mlpPadding.top = paddingTop
        if(mlpPadding.bottom <= 0 && paddingBottom > 0) mlpPadding.bottom = paddingBottom
        if(mlpPadding.left <= 0 && pl > 0) mlpPadding.left = pl
        if(mlpPadding.right <= 0 && pr > 0) mlpPadding.right = pr

        setPaddingRelative(mlpPadding.left,mlpPadding.top,mlpPadding.right,mlpPadding.bottom)
    }
    private fun applyLayoutParamsLandScape(){
        val csParent = parent as? ConstraintLayout
        val mlParent = parent as? MotionLayout
        when {
            mlParent != null -> Log.e("JJKIT", "PARENT MOTION LAYOUT")
            csParent != null -> {
                if (!mIgnoreCl) {
                    cllApply()
                }
            }
            else -> {
                layoutParams.height = mlsHeight
                layoutParams.width = mlsWidth
                val margin = layoutParams as? ViewGroup.MarginLayoutParams
                margin?.topMargin = mlsMargins.top
                margin?.marginStart = mlsMargins.left
                margin?.marginEnd = mlsMargins.right
                margin?.bottomMargin = mlsMargins.bottom
            }
        }
        setPaddingRelative(mlsPadding.left,mlsPadding.top,mlsPadding.right,mlsPadding.bottom)
    }
    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        if(mConfigurationChanged){
            if (newConfig?.orientation == Configuration.ORIENTATION_LANDSCAPE && mSupportLandScape) {
                applyLayoutParamsLandScape()
            } else  {
                applyLayoutParamsPortrait()
            }
        }
    }

    //endregion

    //region method set get

    fun setSupportLandScape(support:Boolean) : JJImageCategoryCircle {
        mSupportLandScape = support
        return this
    }

    fun setSupportConfigurationChanged(support:Boolean) : JJImageCategoryCircle {
        mConfigurationChanged = support
        return this
    }

    fun addViews(vararg views: View): JJImageCategoryCircle {
        for (v in views) {
            addView(v)
        }
        return this
    }



    private var mIdentifier = 0
    fun setIdentifier(value: Int): JJImageCategoryCircle {
        mIdentifier = value
        return this
    }

    fun getIdentifier():Int{
        return mIdentifier
    }

    private var mState = 0
    fun setState(state: Int): JJImageCategoryCircle {
        mState = state
        return this
    }

    fun getState():Int{
        return mState
    }

    private var mAttribute = ""
    fun setAttribute(string:String): JJImageCategoryCircle {
        mAttribute = string
        return this
    }

    fun getAttribute(): String {
        return mAttribute
    }

    fun setPadding(padding: JJPadding): JJImageCategoryCircle {
        mlpPadding = padding
        setPaddingRelative(padding.left,padding.top,padding.right,padding.bottom)
        return this
    }

    fun setOnClickListenerR(listener: (view: View) -> Unit): JJImageCategoryCircle {
        setOnClickListener(listener)
        return this
    }

    fun setOnFocusChangeListenerR(listener: View.OnFocusChangeListener): JJImageCategoryCircle {
        onFocusChangeListener = listener
        return this
    }


    fun setIsFocusable(boolean: Boolean): JJImageCategoryCircle {
        isFocusable = boolean
        return this
    }

    fun setOnTouchListenerR(listener: View.OnTouchListener): JJImageCategoryCircle {
        setOnTouchListener(listener)
        return this
    }

    fun setFitsSystemWindowsR(boolean: Boolean): JJImageCategoryCircle {
        fitsSystemWindows = boolean
        return this
    }

    fun setBackgroundColorR(color: Int): JJImageCategoryCircle {
        setBackgroundColor(color)
        return this
    }

    fun setBackgroundR(drawable: Drawable?): JJImageCategoryCircle {
        background = drawable
        return this
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setOutlineProviderR(provider: ViewOutlineProvider): JJImageCategoryCircle {
        outlineProvider = provider
        return this
    }

    fun setFullScreen(): JJImageCategoryCircle {
        systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        return this
    }

    fun setIsFocusableInTouchMode(boolean: Boolean): JJImageCategoryCircle {
        isFocusableInTouchMode = boolean
        return this
    }



    fun setVisibilityR(type: Int): JJImageCategoryCircle {
        visibility = type
        return this
    }

    fun setMinHeightR(h:Int): JJImageCategoryCircle {
        minHeight = h
        return this
    }

    fun setMinWidthR(w:Int): JJImageCategoryCircle {
        minWidth = w
        return this
    }

    fun setMinimumHeightR(h:Int): JJImageCategoryCircle {
        minimumHeight = h
        return this
    }

    fun setMinimumWidthR(w:Int): JJImageCategoryCircle {
        minimumWidth = w
        return this
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setClipToOutlineR(boolean: Boolean) : JJImageCategoryCircle {
        clipToOutline = boolean
        return this
    }
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun setClipBoundsR(bounds: Rect) : JJImageCategoryCircle {
        clipBounds = bounds
        return this
    }

    fun getPadding() : JJPadding {
        var pl = paddingLeft
        var pr = paddingRight
        if(paddingStart > 0) pl = paddingStart
        if(paddingEnd > 0) pr = paddingEnd
        return JJPadding(pl,paddingTop,pr,paddingBottom)
    }


    fun setClipChildrenToPath(path: Path): JJImageCategoryCircle {
        mPathClipChildren = path
        mIsPathClosureClipChildren = false
        mIsClipInPathChildren = true
        mIsClipChildrenEnabled = true
        mIsClipOutPathChildren = false
        return this
    }

    fun setClipAllToPath(path: Path): JJImageCategoryCircle {
        mPathClipAll = path
        mIsPathClosureClipAll = false
        mIsClipInPathAll = true
        mIsClipAllEnabled = true
        mIsClipOutPathAll = false
        return this
    }


    fun setClipOutChildrenToPath(path: Path): JJImageCategoryCircle {
        mPathClipChildren = path
        mIsPathClosureClipChildren = false
        mIsClipOutPathChildren = true
        mIsClipChildrenEnabled = true
        mIsClipInPathChildren = false
        return this
    }


    fun setClipOutAllToPath(path: Path): JJImageCategoryCircle {
        mPathClipAll = path
        mIsPathClosureClipAll = false
        mIsClipOutPathAll = true
        mIsClipAllEnabled = true
        mIsClipInPathAll = false
        return this
    }

    fun setClipChildrenToPath(closure:(RectF, Path)->Unit): JJImageCategoryCircle {
        mIsClipInPathChildren = true
        mIsPathClosureClipChildren = true
        mIsClipOutPathChildren = false
        mIsClipChildrenEnabled = true
        mClosurePathClipChildren = closure
        return this
    }

    fun setClipAllToPath(closure:(RectF, Path, JJPadding)->Unit): JJImageCategoryCircle {
        mIsClipInPathAll = true
        mIsPathClosureClipAll = true
        mIsClipOutPathAll = false
        mIsClipAllEnabled = true
        mClosurePathClipAll = closure
        return this
    }

    fun setClipOutChildrenToPath(closure:(RectF, Path)->Unit): JJImageCategoryCircle {
        mIsClipInPathChildren = false
        mIsPathClosureClipChildren = true
        mIsClipOutPathChildren = true
        mIsClipChildrenEnabled = true
        mClosurePathClipChildren = closure
        return this
    }

    fun setClipOutAllToPath(closure:(RectF, Path, JJPadding)->Unit): JJImageCategoryCircle {
        mIsClipInPathAll = false
        mIsPathClosureClipAll = true
        mIsClipOutPathAll = true
        mIsClipAllEnabled = true
        mClosurePathClipAll = closure
        return this
    }

    fun disposeClipPathChildren(): JJImageCategoryCircle {
        mIsClipOutPathChildren = false
        mIsPathClosureClipChildren = false
        mIsClipChildrenEnabled = false
        mIsClipInPathChildren = false
        mClosurePathClipChildren = null
        return  this
    }
    fun disposeClipPathAll(): JJImageCategoryCircle {
        mIsClipOutPathAll = false
        mIsPathClosureClipAll = false
        mIsClipAllEnabled = false
        mIsClipInPathAll = false
        mClosurePathClipAll = null
        return  this
    }

    //endregion

    //region Override
    private var mIsClipChildrenEnabled = false
    private var mPathClipChildren = Path()
    private var mIsClipInPathChildren = false
    private var mIsClipOutPathChildren = false
    private var mIsPathClosureClipChildren = false
    private var mClosurePathClipChildren : ((RectF, Path)->Unit)? = null
    private var mRectClip = RectF()
    @Suppress("DEPRECATION")
    override fun onDraw(canvas: Canvas?) {

        if(mIsClipChildrenEnabled) {

            mRectClip.setEmpty()
            mRectClip.right = width.toFloat()
            mRectClip.bottom = height.toFloat()
            mRectClip.left += mlpPadding.left.toFloat()
            mRectClip.right -= mlpPadding.right.toFloat()
            mRectClip.top += mlpPadding.top.toFloat()
            mRectClip.bottom -= mlpPadding.bottom.toFloat()

            canvas?.save()
            if (mIsClipInPathChildren) {
                if (mIsPathClosureClipChildren) {
                    mPathClipChildren.reset()
                    mClosurePathClipChildren?.invoke(mRectClip, mPathClipChildren)
                }
                canvas?.clipPath(mPathClipChildren)
            }
            if (mIsClipOutPathChildren) {
                canvas?.restore()
                canvas?.save()
                if (mIsPathClosureClipChildren) {
                    mPathClipChildren.reset()
                    mClosurePathClipChildren?.invoke(mRectClip, mPathClipChildren)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) canvas?.clipOutPath(mPathClipChildren)
                else canvas?.clipPath(mPathClipChildren, Region.Op.DIFFERENCE)
            }

        }
        super.onDraw(canvas)

    }


    private var mIsClipAllEnabled = false
    private var mPathClipAll = Path()
    private var mIsClipInPathAll = false
    private var mIsClipOutPathAll = false
    private var mIsPathClosureClipAll = false
    private var mClosurePathClipAll : ((RectF, Path, JJPadding)->Unit)? = null
    @Suppress("DEPRECATION")
    override fun draw(canvas: Canvas) {
        //clip
        if(mIsClipAllEnabled) {

            mRectClip.setEmpty()

            mRectClip.right = width.toFloat()
            mRectClip.bottom = height.toFloat()

            canvas.save()
            if (mIsClipInPathAll) {
                if (mIsPathClosureClipAll) {
                    mPathClipAll.reset()
                    mClosurePathClipAll?.invoke(mRectClip, mPathClipAll,mlpPadding)
                }
                canvas.clipPath(mPathClipAll)
            }
            if (mIsClipOutPathAll) {
                canvas.restore()
                canvas.save()
                if (mIsPathClosureClipAll) {
                    mPathClipAll.reset()
                    mClosurePathClipAll?.invoke(mRectClip, mPathClipAll,mlpPadding)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) canvas.clipOutPath(mPathClipAll)
                else canvas.clipPath(mPathClipAll, Region.Op.DIFFERENCE)
            }

        }
        //all and riple
        super.draw(canvas)

    }


    //endregion

    //region CoordinatorLayout params

    private var mCol: CoordinatorLayout.LayoutParams? = null
    private fun setupCol() {
        if (mCol == null) {
            mCol = layoutParams as?  CoordinatorLayout.LayoutParams
            layoutParams = mCol
        }
    }

    fun colWidth(width: Int): JJImageCategoryCircle {
        setupCol()
        mCol!!.width = width
        return this
    }

    fun colHeight(height: Int): JJImageCategoryCircle {
        setupCol()
        mCol!!.height = height
        return this
    }

    fun colGravity(gravity: Int): JJImageCategoryCircle {
        setupCol()
        mCol!!.gravity = gravity
        return this
    }

    fun colBehavior(behavior: AppBarLayout.Behavior){
        setupCol()
        mCol!!.behavior = behavior
    }

    //endregion

    //region AppBarLayout Params
    private var ablp : AppBarLayout.LayoutParams? = null
    private  fun setupAblp(){
        if(ablp == null) {
            ablp = layoutParams as? AppBarLayout.LayoutParams
            layoutParams = ablp
        }
    }

    fun ablWidth(width: Int): JJImageCategoryCircle {
        setupAblp()
        ablp!!.width = width
        return this
    }

    fun ablHeight(height: Int): JJImageCategoryCircle {
        setupAblp()
        ablp!!.height = height
        return this
    }

    fun ablScrollFlags(flags: Int) : JJImageCategoryCircle {
        setupAblp()
        ablp!!.scrollFlags = flags
        return this
    }

    fun ablScrollInterpolator(interpolator: Interpolator) : JJImageCategoryCircle {
        setupAblp()
        ablp!!.scrollInterpolator = interpolator
        return this
    }

    fun ablMargins(margins: JJMargin): JJImageCategoryCircle {
        setupAblp()
        ablp!!.updateMarginsRelative(margins.left,margins.top,margins.right,margins.bottom)
        return this
    }

    //endregion

    //region RelativeLayout Params

    private var mRlp: RelativeLayout.LayoutParams? = null

    private fun setupRlp(){
        if(mRlp == null) {
            mRlp = layoutParams as? RelativeLayout.LayoutParams
            layoutParams = mRlp
        }
    }

    fun rlWidth(width: Int): JJImageCategoryCircle {
        setupRlp()
        mRlp!!.width = width
        return this
    }

    fun rlHeight(height: Int): JJImageCategoryCircle {
        setupRlp()
        mRlp!!.height = height
        return this
    }

    fun rlAbove(viewId: Int): JJImageCategoryCircle {
        setupRlp()
        mRlp!!.addRule(RelativeLayout.ABOVE,viewId)
        return this
    }

    fun rlBelow(viewId: Int): JJImageCategoryCircle {
        setupRlp()
        mRlp!!.addRule(RelativeLayout.BELOW,viewId)
        return this
    }

    fun rlAlignParentBottom(value : Boolean = true): JJImageCategoryCircle {
        setupRlp()
        val data = if(value) 1 else 0
        mRlp!!.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,data)
        return this
    }

    fun rlAlignParentTop(value : Boolean = true): JJImageCategoryCircle {
        setupRlp()
        val data = if(value) 1 else 0
        mRlp!!.addRule(RelativeLayout.ALIGN_PARENT_TOP,data)
        return this
    }

    fun rlAlignParentStart(value : Boolean = true): JJImageCategoryCircle {
        setupRlp()
        val data = if(value) 1 else 0
        mRlp!!.addRule(RelativeLayout.ALIGN_PARENT_START,data)
        return this
    }

    fun rlAlignParentEnd(value : Boolean = true): JJImageCategoryCircle {
        setupRlp()
        val data = if(value) 1 else 0
        mRlp!!.addRule(RelativeLayout.ALIGN_PARENT_END,data)
        return this
    }

    fun rlAlignParentLeft(value : Boolean = true): JJImageCategoryCircle {
        setupRlp()
        val data = if(value) 1 else 0
        mRlp!!.addRule(RelativeLayout.ALIGN_PARENT_LEFT,data)
        return this
    }

    fun rlAlignParentRight(value : Boolean = true): JJImageCategoryCircle {
        setupRlp()
        val data = if(value) 1 else 0
        mRlp!!.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,data)
        return this
    }

    fun rlAlignEnd(viewId: Int): JJImageCategoryCircle {
        setupRlp()
        mRlp!!.addRule(RelativeLayout.ALIGN_END,viewId)
        return this
    }

    fun rlAlignStart(viewId: Int): JJImageCategoryCircle {
        setupRlp()
        mRlp!!.addRule(RelativeLayout.ALIGN_START,viewId)
        return this
    }

    fun rlAlignTop(viewId: Int): JJImageCategoryCircle {
        setupRlp()
        mRlp!!.addRule(RelativeLayout.ALIGN_TOP,viewId)
        return this
    }

    fun rlAlignBottom(viewId: Int): JJImageCategoryCircle {
        setupRlp()
        mRlp!!.addRule(RelativeLayout.ALIGN_BOTTOM,viewId)
        return this
    }


    fun rlAlignLeft(viewId: Int): JJImageCategoryCircle {
        setupRlp()
        mRlp!!.addRule(RelativeLayout.ALIGN_LEFT,viewId)
        return this
    }

    fun rlAlignRight(viewId: Int): JJImageCategoryCircle {
        setupRlp()
        mRlp!!.addRule(RelativeLayout.ALIGN_RIGHT,viewId)
        return this
    }

    fun rlRightToLeft(viewId: Int): JJImageCategoryCircle {
        setupRlp()
        mRlp!!.addRule(RelativeLayout.LEFT_OF,viewId)
        return this
    }

    fun rlLeftToRight(viewId: Int): JJImageCategoryCircle {
        setupRlp()
        mRlp!!.addRule(RelativeLayout.RIGHT_OF,viewId)
        return this
    }

    fun rlStartToEnd(viewId: Int): JJImageCategoryCircle {
        setupRlp()
        mRlp!!.addRule(RelativeLayout.END_OF,viewId)
        return this
    }

    fun rlEndToStart(viewId: Int): JJImageCategoryCircle {
        setupRlp()
        mRlp!!.addRule(RelativeLayout.START_OF,viewId)
        return this
    }

    fun rlCenterInParent(value:Boolean = true): JJImageCategoryCircle {
        setupRlp()
        val data = if(value) 1 else 0
        mRlp!!.addRule(RelativeLayout.CENTER_IN_PARENT,data)
        return this
    }

    fun rlCenterInParentVertically(value:Boolean = true): JJImageCategoryCircle {
        setupRlp()
        val data = if(value) 1 else 0
        mRlp!!.addRule(RelativeLayout.CENTER_VERTICAL,data)
        return this
    }

    fun rlCenterInParentHorizontally(value:Boolean = true): JJImageCategoryCircle {
        setupRlp()
        val data = if(value) 1 else 0
        mRlp!!.addRule(RelativeLayout.CENTER_HORIZONTAL,data)
        return this
    }

    fun rlAlignBaseline(viewId: Int): JJImageCategoryCircle {
        setupRlp()
        mRlp!!.addRule(RelativeLayout.ALIGN_BASELINE,viewId)
        return this
    }

    fun rlMargins(margins: JJMargin): JJImageCategoryCircle {
        setupRlp()
        mRlp!!.setMargins(margins.left,margins.top,margins.right,margins.bottom)
        return this
    }

    //endregion

    //region MotionLayout Params

    private var mMotionConstraintSet: ConstraintSet? = null


    fun mlVisibilityMode(visibility: Int): JJImageCategoryCircle {
        mMotionConstraintSet?.setVisibilityMode(id, visibility)
        return this
    }

    fun mlVerticalBias(float: Float): JJImageCategoryCircle {
        mMotionConstraintSet?.setVerticalBias(id,float)
        return this
    }
    fun mlHorizontalBias(float: Float): JJImageCategoryCircle {
        mMotionConstraintSet?.setHorizontalBias(id,float)
        return this
    }

    fun mlCenterHorizontallyOf(viewId: Int, marginStart: Int = 0, marginEnd: Int = 0): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.START, viewId, ConstraintSet.START, marginStart)
        mMotionConstraintSet?.connect(this.id, ConstraintSet.END, viewId, ConstraintSet.END, marginEnd)
        mMotionConstraintSet?.setHorizontalBias(viewId,0.5f)
        return this
    }
    fun mlCenterVerticallyOf(viewId: Int,marginTop: Int = 0, marginBottom: Int = 0): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.TOP, viewId, ConstraintSet.TOP, marginTop)
        mMotionConstraintSet?.connect(this.id, ConstraintSet.BOTTOM, viewId, ConstraintSet.BOTTOM, marginBottom)
        mMotionConstraintSet?.setVerticalBias(viewId,0.5f)
        return this
    }

    fun mlMargins(margins: JJMargin) : JJImageCategoryCircle {
        mMotionConstraintSet?.setMargin(id, ConstraintSet.TOP,margins.top)
        mMotionConstraintSet?.setMargin(id, ConstraintSet.BOTTOM,margins.bottom)
        mMotionConstraintSet?.setMargin(id, ConstraintSet.END,margins.right)
        mMotionConstraintSet?.setMargin(id, ConstraintSet.START,margins.left)
        return this
    }


    fun mlFloatCustomAttribute(attrName: String, value: Float): JJImageCategoryCircle {
        mMotionConstraintSet?.setFloatValue(id,attrName,value)
        return this
    }

    fun mlIntCustomAttribute(attrName: String, value: Int): JJImageCategoryCircle {
        mMotionConstraintSet?.setIntValue(id,attrName,value)
        return this
    }

    fun mlColorCustomAttribute(attrName: String, value: Int): JJImageCategoryCircle {
        mMotionConstraintSet?.setColorValue(id,attrName,value)
        return this
    }

    fun mlStringCustomAttribute(attrName: String, value: String): JJImageCategoryCircle {
        mMotionConstraintSet?.setStringValue(id,attrName,value)
        return this
    }

    fun mlRotation(float: Float): JJImageCategoryCircle {
        mMotionConstraintSet?.setRotation(id,float)
        return this
    }

    fun mlRotationX(float: Float): JJImageCategoryCircle {
        mMotionConstraintSet?.setRotationX(id,float)
        return this
    }

    fun mlRotationY(float: Float): JJImageCategoryCircle {
        mMotionConstraintSet?.setRotationY(id,float)
        return this
    }

    fun mlTranslation(x: Float,y: Float): JJImageCategoryCircle {
        mMotionConstraintSet?.setTranslation(id,x,y)
        return this
    }
    fun mlTranslationX(x: Float): JJImageCategoryCircle {
        mMotionConstraintSet?.setTranslationX(id,x)
        return this
    }

    fun mlTranslationY(y: Float): JJImageCategoryCircle {
        mMotionConstraintSet?.setTranslationY(id,y)
        return this
    }

    fun mlTranslationZ(z: Float): JJImageCategoryCircle {
        mMotionConstraintSet?.setTranslationZ(id,z)
        return this
    }

    fun mlTransformPivot(x: Float, y: Float): JJImageCategoryCircle {
        mMotionConstraintSet?.setTransformPivot(id,x,y)
        return this
    }

    fun mlTransformPivotX(x: Float): JJImageCategoryCircle {
        mMotionConstraintSet?.setTransformPivotX(id,x)
        return this
    }

    fun mlTransformPivotY(y: Float): JJImageCategoryCircle {
        mMotionConstraintSet?.setTransformPivotY(id,y)
        return this
    }

    fun mlScaleX(x: Float): JJImageCategoryCircle {
        mMotionConstraintSet?.setScaleX(id,x)
        return this
    }

    fun mlScaleY(y: Float): JJImageCategoryCircle {
        mMotionConstraintSet?.setScaleY(id,y)
        return this
    }

    fun mlDimensionRatio(ratio: String): JJImageCategoryCircle {
        mMotionConstraintSet?.setDimensionRatio(id,ratio)
        return this
    }

    fun mlAlpha(alpha: Float): JJImageCategoryCircle {
        mMotionConstraintSet?.setAlpha(id,alpha)
        return this
    }



    fun mlTopToTop(viewId: Int, margin: Int = 0): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.TOP, viewId, ConstraintSet.TOP, margin)
        return this
    }

    fun mlTopToTopParent(margin: Int = 0): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin)
        return this
    }


    fun mlTopToBottomOf(viewId: Int, margin: Int = 0): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.TOP, viewId, ConstraintSet.BOTTOM, margin)
        return this
    }

    fun mlTopToBottomParent(margin: Int = 0): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin)
        return this
    }

    fun mlBottomToTopOf(viewId: Int, margin: Int = 0): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.BOTTOM, viewId, ConstraintSet.TOP, margin)

        return this
    }

    fun mlBottomToTopParent(margin: Int = 0): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin)

        return this
    }

    fun mlBottomToBottomOf(viewId: Int, margin: Int = 0): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.BOTTOM, viewId, ConstraintSet.BOTTOM, margin)

        return this
    }

    fun mlBottomToBottomParent(margin: Int = 0): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin)

        return this
    }

    fun mlStartToStartOf(viewId: Int, margin: Int = 0): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.START, viewId, ConstraintSet.START, margin)

        return this
    }

    fun mlStartToStartParent(margin: Int = 0): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margin)

        return this
    }

    fun mlStartToEndOf(viewId: Int, margin: Int = 0): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.START, viewId, ConstraintSet.END, margin)

        return this
    }

    fun mlStartToEndParent(margin: Int = 0): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.END, margin)

        return this
    }

    fun mlEndToEndOf(viewId: Int, margin: Int= 0): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.END, viewId, ConstraintSet.END, margin)

        return this
    }

    fun mlEndToEndParent(margin: Int = 0): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, margin)

        return this
    }


    fun mlEndToStartOf(viewId: Int, margin: Int = 0): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.END, viewId, ConstraintSet.START, margin)
        return this
    }

    fun mlEndToStartParent(margin: Int = 0): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.START, margin)
        return this
    }


    fun mlWidth(width: Int): JJImageCategoryCircle {
        mMotionConstraintSet?.constrainWidth(id, width)
        return this
    }

    fun mlHeight(height: Int): JJImageCategoryCircle {
        mMotionConstraintSet?.constrainHeight(id, height)
        return this
    }

    fun mlPercentWidth(width: Float): JJImageCategoryCircle {
        mMotionConstraintSet?.constrainPercentWidth(id, width)
        return this
    }

    fun mlPercentHeight(height: Float): JJImageCategoryCircle {
        mMotionConstraintSet?.constrainPercentHeight(id, height)
        return this
    }

    fun mlCenterInParent(): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mMotionConstraintSet?.setVerticalBias(id, 0.5f)
        mMotionConstraintSet?.setHorizontalBias(id, 0.5f)

        return this
    }

    fun mlCenterInParent(verticalBias: Float, horizontalBias: Float, margin: JJMargin): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margin.left)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, margin.right)
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin.top)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin.bottom)
        mMotionConstraintSet?.setVerticalBias(id, verticalBias)
        mMotionConstraintSet?.setHorizontalBias(id, horizontalBias)
        return this
    }

    fun mlCenterInParentVertically(): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mMotionConstraintSet?.setVerticalBias(id, 0.5f)

        return this
    }

    fun mlCenterInParentHorizontally(): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mMotionConstraintSet?.setHorizontalBias(id, 0.5f)
        return this
    }

    fun mlCenterInParentVertically(bias: Float, topMargin: Int, bottomMargin: Int): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, bottomMargin)
        mMotionConstraintSet?.setVerticalBias(id, bias)
        return this
    }

    fun mlCenterInParentHorizontally(bias: Float, startMargin: Int, endtMargin: Int): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, startMargin)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, endtMargin)
        mMotionConstraintSet?.setHorizontalBias(id, bias)
        return this
    }


    fun mlCenterInParentTopVertically(): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mMotionConstraintSet?.setVerticalBias(id, 0.5f)
        return this
    }


    fun mlCenterInParentBottomVertically(): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mMotionConstraintSet?.setVerticalBias(id, 0.5f)
        return this
    }

    fun mlCenterInParentStartHorizontally(): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mMotionConstraintSet?.setHorizontalBias(id, 0.5f)
        return this
    }

    fun mlCenterInParentEndHorizontally(): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mMotionConstraintSet?.setHorizontalBias(id, 0.5f)
        return this
    }

    fun mlCenterInTopVerticallyOf(viewId: Int): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, viewId, ConstraintSet.TOP, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, viewId, ConstraintSet.TOP, 0)
        mMotionConstraintSet?.setVerticalBias(id, 0.5f)
        return this
    }


    fun mlCenterInBottomVerticallyOf(viewId: Int): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, viewId, ConstraintSet.BOTTOM, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, viewId, ConstraintSet.BOTTOM, 0)
        mMotionConstraintSet?.setVerticalBias(id, 0.5f)
        return this
    }

    fun mlCenterInStartHorizontallyOf(viewId: Int): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(id, ConstraintSet.START, viewId, ConstraintSet.START, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, viewId, ConstraintSet.START, 0)
        mMotionConstraintSet?.setHorizontalBias(id, 0.5f)
        return this
    }

    fun mlCenterInEndHorizontallyOf(viewId: Int): JJImageCategoryCircle {
        mMotionConstraintSet?.connect(id, ConstraintSet.START, viewId, ConstraintSet.END, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, viewId, ConstraintSet.END, 0)
        mMotionConstraintSet?.setHorizontalBias(id, 0.5f)
        return this
    }

    fun mlCenterVertically(topId: Int, topSide: Int, topMargin: Int, bottomId: Int, bottomSide: Int, bottomMargin: Int, bias: Float): JJImageCategoryCircle {
        mMotionConstraintSet?.centerVertically(id, topId, topSide, topMargin, bottomId, bottomSide, bottomMargin, bias)
        return this
    }

    fun mlCenterHorizontally(startId: Int, startSide: Int, startMargin: Int, endId: Int, endSide: Int, endMargin: Int, bias: Float): JJImageCategoryCircle {
        mMotionConstraintSet?.centerHorizontally(id, startId, startSide, startMargin, endId, endSide, endMargin, bias)
        return this
    }


    fun mlFillParent(): JJImageCategoryCircle {
        mMotionConstraintSet?.constrainWidth(id,0)
        mMotionConstraintSet?.constrainHeight(id,0)
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        return this
    }

    fun mlFillParent(margin: JJMargin): JJImageCategoryCircle {
        mMotionConstraintSet?.constrainWidth(id,0)
        mMotionConstraintSet?.constrainHeight(id,0)
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin.top)
        mMotionConstraintSet?.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margin.left)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, margin.right)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin.bottom)
        return this
    }

    fun mlFillParentHorizontally(): JJImageCategoryCircle {
        mMotionConstraintSet?.constrainWidth(id,0)
        mMotionConstraintSet?.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        return this
    }

    fun mlFillParentVertically(): JJImageCategoryCircle {
        mMotionConstraintSet?.constrainHeight(id,0)
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        return this
    }

    fun mlFillParentHorizontally(startMargin: Int, endMargin: Int): JJImageCategoryCircle {
        mMotionConstraintSet?.constrainWidth(id,0)
        mMotionConstraintSet?.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, startMargin)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, endMargin)
        return this
    }

    fun mlFillParentVertically(topMargin: Int, bottomMargin: Int): JJImageCategoryCircle {
        mMotionConstraintSet?.constrainHeight(id,0)
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, bottomMargin)
        return this
    }

    fun mlVisibility(visibility: Int): JJImageCategoryCircle {
        mMotionConstraintSet?.setVisibility(id, visibility)
        return this
    }

    fun mlElevation(elevation: Float): JJImageCategoryCircle {
        mMotionConstraintSet?.setElevation(id, elevation)
        return this
    }

    fun mlApply(): JJImageCategoryCircle {
        mMotionConstraintSet?.applyTo(parent as ConstraintLayout)
        return this
    }

    fun mlSetConstraint(cs : ConstraintSet?): JJImageCategoryCircle {
        mMotionConstraintSet = cs
        return this
    }

    fun mlDisposeConstraint(): JJImageCategoryCircle {
        mMotionConstraintSet = null
        return this
    }

    //endregion

    //region ConstraintLayout Params
    protected val mConstraintSet = ConstraintSet()


    fun clFloatCustomAttribute(attrName: String, value: Float): JJImageCategoryCircle {
        mConstraintSet.setFloatValue(id,attrName,value)
        return this
    }

    fun clIntCustomAttribute(attrName: String, value: Int): JJImageCategoryCircle {
        mConstraintSet.setIntValue(id,attrName,value)
        return this
    }

    fun clColorCustomAttribute(attrName: String, value: Int): JJImageCategoryCircle {
        mConstraintSet.setColorValue(id,attrName,value)
        return this
    }

    fun clStringCustomAttribute(attrName: String, value: String): JJImageCategoryCircle {
        mConstraintSet.setStringValue(id,attrName,value)
        return this
    }

    fun clRotation(float: Float): JJImageCategoryCircle {
        mConstraintSet.setRotation(id,float)
        return this
    }

    fun clRotationX(float: Float): JJImageCategoryCircle {
        mConstraintSet.setRotationX(id,float)
        return this
    }

    fun clRotationY(float: Float): JJImageCategoryCircle {
        mConstraintSet.setRotationY(id,float)
        return this
    }

    fun clTranslation(x: Float,y: Float): JJImageCategoryCircle {
        mConstraintSet.setTranslation(id,x,y)
        return this
    }
    fun clTranslationX(x: Float): JJImageCategoryCircle {
        mConstraintSet.setTranslationX(id,x)
        return this
    }

    fun clTranslationY(y: Float): JJImageCategoryCircle {
        mConstraintSet.setTranslationY(id,y)
        return this
    }

    fun clTranslationZ(z: Float): JJImageCategoryCircle {
        mConstraintSet.setTranslationZ(id,z)
        return this
    }

    fun clTransformPivot(x: Float, y: Float): JJImageCategoryCircle {
        mConstraintSet.setTransformPivot(id,x,y)
        return this
    }

    fun clTransformPivotX(x: Float): JJImageCategoryCircle {
        mConstraintSet.setTransformPivotX(id,x)
        return this
    }

    fun clTransformPivotY(y: Float): JJImageCategoryCircle {
        mConstraintSet.setTransformPivotY(id,y)
        return this
    }

    fun clScaleX(x: Float): JJImageCategoryCircle {
        mConstraintSet.setScaleX(id,x)
        return this
    }

    fun clScaleY(y: Float): JJImageCategoryCircle {
        mConstraintSet.setScaleY(id,y)
        return this
    }

    fun clDimensionRatio(ratio: String): JJImageCategoryCircle {
        mConstraintSet.setDimensionRatio(id,ratio)
        return this
    }

    fun clAlpha(alpha: Float): JJImageCategoryCircle {
        mConstraintSet.setAlpha(id,alpha)
        return this
    }


    fun clApply(): JJImageCategoryCircle {
        mConstraintSet.applyTo(parent as ConstraintLayout)
        return this
    }

    fun clVisibilityMode(visibility: Int): JJImageCategoryCircle {
        mConstraintSet.setVisibilityMode(id, visibility)
        return this
    }

    fun clVerticalBias(float: Float): JJImageCategoryCircle {
        mConstraintSet.setVerticalBias(id,float)
        return this
    }
    fun clHorizontalBias(float: Float): JJImageCategoryCircle {
        mConstraintSet.setHorizontalBias(id,float)
        return this
    }

    fun clCenterHorizontallyOf(viewId: Int, marginStart: Int = 0, marginEnd: Int = 0): JJImageCategoryCircle {
        mConstraintSet.connect(this.id, ConstraintSet.START, viewId, ConstraintSet.START, marginStart)
        mConstraintSet.connect(this.id, ConstraintSet.END, viewId, ConstraintSet.END, marginEnd)
        mConstraintSet.setHorizontalBias(id,0.5f)
        return this
    }
    fun clCenterVerticallyOf(viewId: Int,marginTop: Int = 0, marginBottom: Int = 0): JJImageCategoryCircle {
        mConstraintSet.connect(this.id, ConstraintSet.TOP, viewId, ConstraintSet.TOP, marginTop)
        mConstraintSet.connect(this.id, ConstraintSet.BOTTOM, viewId, ConstraintSet.BOTTOM, marginBottom)
        mConstraintSet.setVerticalBias(id,0.5f)
        return this
    }

    fun clMargins(margins: JJMargin) : JJImageCategoryCircle {
        mConstraintSet.setMargin(id, ConstraintSet.TOP,margins.top)
        mConstraintSet.setMargin(id, ConstraintSet.BOTTOM,margins.bottom)
        mConstraintSet.setMargin(id, ConstraintSet.END,margins.right)
        mConstraintSet.setMargin(id, ConstraintSet.START,margins.left)
        return this
    }


    fun clTopToTop(viewId: Int, margin: Int = 0): JJImageCategoryCircle {
        mConstraintSet.connect(this.id, ConstraintSet.TOP, viewId, ConstraintSet.TOP, margin)
        return this
    }

    fun clTopToTopParent(margin: Int = 0): JJImageCategoryCircle {
        mConstraintSet.connect(this.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin)
        return this
    }


    fun clTopToBottom(viewId: Int, margin: Int = 0): JJImageCategoryCircle {
        mConstraintSet.connect(this.id, ConstraintSet.TOP, viewId, ConstraintSet.BOTTOM, margin)
        return this
    }

    fun clTopToBottomParent(margin: Int = 0): JJImageCategoryCircle {
        mConstraintSet.connect(this.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin)
        return this
    }

    fun clBottomToTop(viewId: Int, margin: Int = 0): JJImageCategoryCircle {
        mConstraintSet.connect(this.id, ConstraintSet.BOTTOM, viewId, ConstraintSet.TOP, margin)
        return this
    }

    fun clBottomToTopParent(margin: Int = 0): JJImageCategoryCircle {
        mConstraintSet.connect(this.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin)
        return this
    }

    fun clBottomToBottom(viewId: Int, margin: Int = 0): JJImageCategoryCircle {
        mConstraintSet.connect(this.id, ConstraintSet.BOTTOM, viewId, ConstraintSet.BOTTOM, margin)
        return this
    }

    fun clBottomToBottomParent(margin: Int = 0): JJImageCategoryCircle {
        mConstraintSet.connect(this.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin)
        return this
    }

    fun clStartToStart(viewId: Int, margin: Int = 0): JJImageCategoryCircle {
        mConstraintSet.connect(this.id, ConstraintSet.START, viewId, ConstraintSet.START, margin)
        return this
    }

    fun clStartToStartParent(margin: Int = 0): JJImageCategoryCircle {
        mConstraintSet.connect(this.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margin)
        return this
    }

    fun clStartToEnd(viewId: Int, margin: Int = 0): JJImageCategoryCircle {
        mConstraintSet.connect(this.id, ConstraintSet.START, viewId, ConstraintSet.END, margin)
        return this
    }

    fun clStartToEndParent(margin: Int = 0): JJImageCategoryCircle {
        mConstraintSet.connect(this.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.END, margin)
        return this
    }

    fun clEndToEnd(viewId: Int, margin: Int = 0): JJImageCategoryCircle {
        mConstraintSet.connect(this.id, ConstraintSet.END, viewId, ConstraintSet.END, margin)
        return this
    }

    fun clEndToEndParent(margin: Int = 0): JJImageCategoryCircle {
        mConstraintSet.connect(this.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, margin)
        return this
    }


    fun clEndToStart(viewId: Int, margin: Int = 0): JJImageCategoryCircle {
        mConstraintSet.connect(this.id, ConstraintSet.END, viewId, ConstraintSet.START, margin)
        return this
    }

    fun clEndToStartParent(margin: Int = 0): JJImageCategoryCircle {
        mConstraintSet.connect(this.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.START, margin)
        return this
    }


    fun clWidth(width: Int): JJImageCategoryCircle {
        mConstraintSet.constrainWidth(id, width)
        return this
    }

    fun clHeight(height: Int): JJImageCategoryCircle {
        mConstraintSet.constrainHeight(id, height)
        return this
    }

    fun clPercentWidth(width: Float): JJImageCategoryCircle {
        mConstraintSet.constrainPercentWidth(id, width)
        return this
    }

    fun clPercentHeight(height: Float): JJImageCategoryCircle {
        mConstraintSet.constrainPercentHeight(id, height)
        return this
    }

    fun clCenterInParent(): JJImageCategoryCircle {
        mConstraintSet.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSet.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mConstraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mConstraintSet.setVerticalBias(id, 0.5f)
        mConstraintSet.setHorizontalBias(id, 0.5f)
        return this
    }

    fun clCenterInParent(verticalBias: Float, horizontalBias: Float, margin: JJMargin): JJImageCategoryCircle {
        mConstraintSet.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margin.left)
        mConstraintSet.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, margin.right)
        mConstraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin.top)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin.bottom)
        mConstraintSet.setVerticalBias(id, verticalBias)
        mConstraintSet.setHorizontalBias(id, horizontalBias)
        return this
    }

    fun clCenterInParentVertically(): JJImageCategoryCircle {
        mConstraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mConstraintSet.setVerticalBias(id, 0.5f)
        return this
    }

    fun clCenterInParentHorizontally(): JJImageCategoryCircle {
        mConstraintSet.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSet.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mConstraintSet.setHorizontalBias(id, 0.5f)
        return this
    }

    fun clCenterInParentVertically(bias: Float, topMargin: Int, bottomMargin: Int): JJImageCategoryCircle {
        mConstraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, bottomMargin)
        mConstraintSet.setVerticalBias(id, bias)
        return this
    }

    fun clCenterInParentHorizontally(bias: Float, startMargin: Int, endtMargin: Int): JJImageCategoryCircle {
        mConstraintSet.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, startMargin)
        mConstraintSet.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, endtMargin)
        mConstraintSet.setHorizontalBias(id, bias)
        return this
    }


    fun clCenterInParentTopVertically(): JJImageCategoryCircle {
        mConstraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSet.setVerticalBias(id, 0.5f)
        return this
    }


    fun clCenterInParentBottomVertically(): JJImageCategoryCircle {
        mConstraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mConstraintSet.setVerticalBias(id, 0.5f)
        return this
    }

    fun clCenterInParentStartHorizontally(): JJImageCategoryCircle {
        mConstraintSet.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSet.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSet.setHorizontalBias(id, 0.5f)
        return this
    }

    fun clCenterInParentEndHorizontally(): JJImageCategoryCircle {
        mConstraintSet.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mConstraintSet.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mConstraintSet.setHorizontalBias(id, 0.5f)
        return this
    }

    fun clCenterInTopVertically(topId: Int): JJImageCategoryCircle {
        mConstraintSet.connect(id, ConstraintSet.TOP, topId, ConstraintSet.TOP, 0)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, topId, ConstraintSet.TOP, 0)
        mConstraintSet.setVerticalBias(id, 0.5f)
        return this
    }


    fun clCenterInBottomVertically(bottomId: Int): JJImageCategoryCircle {
        mConstraintSet.connect(id, ConstraintSet.TOP, bottomId, ConstraintSet.BOTTOM, 0)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, bottomId, ConstraintSet.BOTTOM, 0)
        mConstraintSet.setVerticalBias(id, 0.5f)
        return this
    }

    fun clCenterInStartHorizontally(startId: Int): JJImageCategoryCircle {
        mConstraintSet.connect(id, ConstraintSet.START, startId, ConstraintSet.START, 0)
        mConstraintSet.connect(id, ConstraintSet.END, startId, ConstraintSet.START, 0)
        mConstraintSet.setHorizontalBias(id, 0.5f)
        return this
    }

    fun clCenterInEndHorizontally(endId: Int): JJImageCategoryCircle {
        mConstraintSet.connect(id, ConstraintSet.START, endId, ConstraintSet.END, 0)
        mConstraintSet.connect(id, ConstraintSet.END, endId, ConstraintSet.END, 0)
        mConstraintSet.setHorizontalBias(id, 0.5f)
        return this
    }

    fun clCenterVertically(topId: Int, topSide: Int, topMargin: Int, bottomId: Int, bottomSide: Int, bottomMargin: Int, bias: Float): JJImageCategoryCircle {
        mConstraintSet.centerVertically(id, topId, topSide, topMargin, bottomId, bottomSide, bottomMargin, bias)
        return this
    }

    fun clCenterHorizontally(startId: Int, startSide: Int, startMargin: Int, endId: Int, endSide: Int, endMargin: Int, bias: Float): JJImageCategoryCircle {
        mConstraintSet.centerHorizontally(id, startId, startSide, startMargin, endId, endSide, endMargin, bias)
        return this
    }


    fun clFillParent(): JJImageCategoryCircle {
        mConstraintSet.constrainWidth(id,0)
        mConstraintSet.constrainHeight(id,0)
        mConstraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSet.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSet.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        return this
    }

    fun clFillParent(margin: JJMargin): JJImageCategoryCircle {
        mConstraintSet.constrainWidth(id,0)
        mConstraintSet.constrainHeight(id,0)
        mConstraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin.top)
        mConstraintSet.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margin.left)
        mConstraintSet.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, margin.right)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin.bottom)
        return this
    }

    fun clFillParentHorizontally(): JJImageCategoryCircle {
        mConstraintSet.constrainWidth(id,0)
        mConstraintSet.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSet.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        return this
    }

    fun clFillParentVertically(): JJImageCategoryCircle {
        mConstraintSet.constrainHeight(id,0)
        mConstraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        return this
    }

    fun clFillParentHorizontally(startMargin: Int, endMargin: Int): JJImageCategoryCircle {
        mConstraintSet.constrainWidth(id,0)
        mConstraintSet.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, startMargin)
        mConstraintSet.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, endMargin)
        return this
    }

    fun clFillParentVertically(topMargin: Int, bottomMargin: Int): JJImageCategoryCircle {
        mConstraintSet.constrainHeight(id,0)
        mConstraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, bottomMargin)
        return this
    }

    fun clVisibility(visibility: Int): JJImageCategoryCircle {
        mConstraintSet.setVisibility(id, visibility)
        return this
    }



    fun clElevation(elevation: Float): JJImageCategoryCircle {
        mConstraintSet.setElevation(id, elevation)

        return this
    }

    fun clConstraintSet() : ConstraintSet {
        return mConstraintSet
    }

    fun clMinWidth(w:Int): JJImageCategoryCircle {
        mConstraintSet.constrainMinWidth(id,w)
        return this
    }

    fun clMinHeight(h:Int): JJImageCategoryCircle {
        mConstraintSet.constrainMinHeight(id,h)
        return this
    }

    fun clMaxWidth(w:Int): JJImageCategoryCircle {
        mConstraintSet.constrainMaxWidth(id,w)
        return this
    }

    fun clMaxHeight(h:Int): JJImageCategoryCircle {
        mConstraintSet.constrainMaxHeight(id,h)
        return this
    }






    //endregion

    //region ConstraintLayout LandScape Params
    protected val mConstraintSetLandScape = ConstraintSet()

    fun cllApply(): JJImageCategoryCircle {
        mConstraintSetLandScape.applyTo(parent as ConstraintLayout)
        return this
    }


    fun cllFloatCustomAttribute(attrName: String, value: Float): JJImageCategoryCircle {
        mConstraintSet.setFloatValue(id,attrName,value)
        return this
    }

    fun cllIntCustomAttribute(attrName: String, value: Int): JJImageCategoryCircle {
        mConstraintSet.setIntValue(id,attrName,value)
        return this
    }

    fun cllColorCustomAttribute(attrName: String, value: Int): JJImageCategoryCircle {
        mConstraintSet.setColorValue(id,attrName,value)
        return this
    }

    fun cllStringCustomAttribute(attrName: String, value: String): JJImageCategoryCircle {
        mConstraintSet.setStringValue(id,attrName,value)
        return this
    }

    fun cllRotation(float: Float): JJImageCategoryCircle {
        mConstraintSet.setRotation(id,float)
        return this
    }

    fun cllRotationX(float: Float): JJImageCategoryCircle {
        mConstraintSet.setRotationX(id,float)
        return this
    }

    fun cllRotationY(float: Float): JJImageCategoryCircle {
        mConstraintSet.setRotationY(id,float)
        return this
    }

    fun cllTranslation(x: Float,y: Float): JJImageCategoryCircle {
        mConstraintSet.setTranslation(id,x,y)
        return this
    }
    fun cllTranslationX(x: Float): JJImageCategoryCircle {
        mConstraintSet.setTranslationX(id,x)
        return this
    }

    fun cllTranslationY(y: Float): JJImageCategoryCircle {
        mConstraintSet.setTranslationY(id,y)
        return this
    }

    fun cllTranslationZ(z: Float): JJImageCategoryCircle {
        mConstraintSet.setTranslationZ(id,z)
        return this
    }

    fun cllTransformPivot(x: Float, y: Float): JJImageCategoryCircle {
        mConstraintSet.setTransformPivot(id,x,y)
        return this
    }

    fun cllTransformPivotX(x: Float): JJImageCategoryCircle {
        mConstraintSet.setTransformPivotX(id,x)
        return this
    }

    fun cllTransformPivotY(y: Float): JJImageCategoryCircle {
        mConstraintSet.setTransformPivotY(id,y)
        return this
    }

    fun cllScaleX(x: Float): JJImageCategoryCircle {
        mConstraintSet.setScaleX(id,x)
        return this
    }

    fun cllScaleY(y: Float): JJImageCategoryCircle {
        mConstraintSet.setScaleY(id,y)
        return this
    }

    fun cllDimensionRatio(ratio: String): JJImageCategoryCircle {
        mConstraintSet.setDimensionRatio(id,ratio)
        return this
    }

    fun cllAlpha(alpha: Float): JJImageCategoryCircle {
        mConstraintSet.setAlpha(id,alpha)
        return this
    }


    fun cllVisibilityMode(visibility: Int): JJImageCategoryCircle {
        mConstraintSetLandScape.setVisibilityMode(id, visibility)
        return this
    }

    fun cllVerticalBias(float: Float): JJImageCategoryCircle {
        mConstraintSetLandScape.setVerticalBias(id,float)
        return this
    }
    fun cllHorizontalBias(float: Float): JJImageCategoryCircle {
        mConstraintSetLandScape.setHorizontalBias(id,float)
        return this
    }

    fun cllCenterHorizontallyOf(viewId: Int, marginStart: Int = 0, marginEnd: Int = 0): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.START, viewId, ConstraintSet.START, marginStart)
        mConstraintSetLandScape.connect(this.id, ConstraintSet.END, viewId, ConstraintSet.END, marginEnd)
        mConstraintSetLandScape.setHorizontalBias(id,0.5f)
        return this
    }
    fun cllCenterVerticallyOf(viewId: Int,marginTop: Int = 0, marginBottom: Int = 0): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.TOP, viewId, ConstraintSet.TOP, marginTop)
        mConstraintSetLandScape.connect(this.id, ConstraintSet.BOTTOM, viewId, ConstraintSet.BOTTOM, marginBottom)
        mConstraintSetLandScape.setVerticalBias(id,0.5f)
        return this
    }

    fun cllMargins(margins: JJMargin) : JJImageCategoryCircle {
        mConstraintSetLandScape.setMargin(id, ConstraintSet.TOP,margins.top)
        mConstraintSetLandScape.setMargin(id, ConstraintSet.BOTTOM,margins.bottom)
        mConstraintSetLandScape.setMargin(id, ConstraintSet.END,margins.right)
        mConstraintSetLandScape.setMargin(id, ConstraintSet.START,margins.left)
        return this
    }


    fun cllTopToTop(viewId: Int, margin: Int = 0): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.TOP, viewId, ConstraintSet.TOP, margin)
        return this
    }

    fun cllTopToTopParent(margin: Int = 0): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin)
        return this
    }


    fun cllTopToBottom(viewId: Int, margin: Int = 0): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.TOP, viewId, ConstraintSet.BOTTOM, margin)
        return this
    }

    fun cllTopToBottomParent(margin: Int = 0): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin)
        return this
    }

    fun cllBottomToTop(viewId: Int, margin: Int = 0): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.BOTTOM, viewId, ConstraintSet.TOP, margin)
        return this
    }

    fun cllBottomToTopParent(margin: Int = 0): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin)
        return this
    }

    fun cllBottomToBottom(viewId: Int, margin: Int = 0): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.BOTTOM, viewId, ConstraintSet.BOTTOM, margin)
        return this
    }

    fun cllBottomToBottomParent(margin: Int = 0): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin)
        return this
    }

    fun cllStartToStart(viewId: Int, margin: Int = 0): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.START, viewId, ConstraintSet.START, margin)
        return this
    }

    fun cllStartToStartParent(margin: Int = 0): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margin)
        return this
    }

    fun cllStartToEnd(viewId: Int, margin: Int = 0): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.START, viewId, ConstraintSet.END, margin)
        return this
    }

    fun cllStartToEndParent(margin: Int = 0): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.END, margin)
        return this
    }

    fun cllEndToEnd(viewId: Int, margin: Int = 0): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.END, viewId, ConstraintSet.END, margin)
        return this
    }

    fun cllEndToEndParent(margin: Int = 0): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, margin)
        return this
    }


    fun cllEndToStart(viewId: Int, margin: Int = 0): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.END, viewId, ConstraintSet.START, margin)
        return this
    }

    fun cllEndToStartParent(margin: Int = 0): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.START, margin)
        return this
    }


    fun cllWidth(width: Int): JJImageCategoryCircle {
        mConstraintSetLandScape.constrainWidth(id, width)
        return this
    }

    fun cllHeight(height: Int): JJImageCategoryCircle {
        mConstraintSetLandScape.constrainHeight(id, height)
        return this
    }

    fun cllPercentWidth(width: Float): JJImageCategoryCircle {
        mConstraintSetLandScape.constrainPercentWidth(id, width)
        return this
    }

    fun cllPercentHeight(height: Float): JJImageCategoryCircle {
        mConstraintSetLandScape.constrainPercentHeight(id, height)
        return this
    }

    fun cllCenterInParent(): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mConstraintSetLandScape.setVerticalBias(id, 0.5f)
        mConstraintSetLandScape.setHorizontalBias(id, 0.5f)
        return this
    }

    fun cllCenterInParent(verticalBias: Float, horizontalBias: Float, margin: JJMargin): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margin.left)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, margin.right)
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin.top)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin.bottom)
        mConstraintSetLandScape.setVerticalBias(id, verticalBias)
        mConstraintSetLandScape.setHorizontalBias(id, horizontalBias)
        return this
    }

    fun cllCenterInParentVertically(): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mConstraintSetLandScape.setVerticalBias(id, 0.5f)
        return this
    }

    fun cllCenterInParentHorizontally(): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mConstraintSetLandScape.setHorizontalBias(id, 0.5f)
        return this
    }

    fun cllCenterInParentVertically(bias: Float, topMargin: Int, bottomMargin: Int): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, bottomMargin)
        mConstraintSetLandScape.setVerticalBias(id, bias)
        return this
    }

    fun cllCenterInParentHorizontally(bias: Float, startMargin: Int, endtMargin: Int): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, startMargin)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, endtMargin)
        mConstraintSetLandScape.setHorizontalBias(id, bias)
        return this
    }


    fun cllCenterInParentTopVertically(): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSetLandScape.setVerticalBias(id, 0.5f)
        return this
    }


    fun cllCenterInParentBottomVertically(): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mConstraintSetLandScape.setVerticalBias(id, 0.5f)
        return this
    }

    fun cllCenterInParentStartHorizontally(): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSetLandScape.setHorizontalBias(id, 0.5f)
        return this
    }

    fun cllCenterInParentEndHorizontally(): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mConstraintSetLandScape.setHorizontalBias(id, 0.5f)
        return this
    }

    fun cllCenterInTopVertically(topId: Int): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, topId, ConstraintSet.TOP, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, topId, ConstraintSet.TOP, 0)
        mConstraintSetLandScape.setVerticalBias(id, 0.5f)
        return this
    }


    fun cllCenterInBottomVertically(bottomId: Int): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, bottomId, ConstraintSet.BOTTOM, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, bottomId, ConstraintSet.BOTTOM, 0)
        mConstraintSetLandScape.setVerticalBias(id, 0.5f)
        return this
    }

    fun cllCenterInStartHorizontally(startId: Int): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(id, ConstraintSet.START, startId, ConstraintSet.START, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, startId, ConstraintSet.START, 0)
        mConstraintSetLandScape.setHorizontalBias(id, 0.5f)
        return this
    }

    fun cllCenterInEndHorizontally(endId: Int): JJImageCategoryCircle {
        mConstraintSetLandScape.connect(id, ConstraintSet.START, endId, ConstraintSet.END, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, endId, ConstraintSet.END, 0)
        mConstraintSetLandScape.setHorizontalBias(id, 0.5f)
        return this
    }

    fun cllCenterVertically(topId: Int, topSide: Int, topMargin: Int, bottomId: Int, bottomSide: Int, bottomMargin: Int, bias: Float): JJImageCategoryCircle {
        mConstraintSetLandScape.centerVertically(id, topId, topSide, topMargin, bottomId, bottomSide, bottomMargin, bias)
        return this
    }

    fun cllCenterHorizontally(startId: Int, startSide: Int, startMargin: Int, endId: Int, endSide: Int, endMargin: Int, bias: Float): JJImageCategoryCircle {
        mConstraintSetLandScape.centerHorizontally(id, startId, startSide, startMargin, endId, endSide, endMargin, bias)
        return this
    }


    fun cllFillParent(): JJImageCategoryCircle {
        mConstraintSetLandScape.constrainWidth(id,0)
        mConstraintSetLandScape.constrainHeight(id,0)
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        return this
    }

    fun cllFillParent(margin: JJMargin): JJImageCategoryCircle {
        mConstraintSetLandScape.constrainWidth(id,0)
        mConstraintSetLandScape.constrainHeight(id,0)
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin.top)
        mConstraintSetLandScape.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margin.left)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, margin.right)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin.bottom)
        return this
    }

    fun cllFillParentHorizontally(): JJImageCategoryCircle {
        mConstraintSetLandScape.constrainWidth(id,0)
        mConstraintSetLandScape.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        return this
    }

    fun cllFillParentVertically(): JJImageCategoryCircle {
        mConstraintSetLandScape.constrainHeight(id,0)
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        return this
    }

    fun cllFillParentHorizontally(startMargin: Int, endMargin: Int): JJImageCategoryCircle {
        mConstraintSetLandScape.constrainWidth(id,0)
        mConstraintSetLandScape.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, startMargin)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, endMargin)
        return this
    }

    fun cllFillParentVertically(topMargin: Int, bottomMargin: Int): JJImageCategoryCircle {
        mConstraintSetLandScape.constrainHeight(id,0)
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, bottomMargin)
        return this
    }

    fun cllVisibility(visibility: Int): JJImageCategoryCircle {
        mConstraintSetLandScape.setVisibility(id, visibility)
        return this
    }



    fun cllElevation(elevation: Float): JJImageCategoryCircle {
        mConstraintSetLandScape.setElevation(id, elevation)

        return this
    }

    fun cllConstraintSet() : ConstraintSet {
        return mConstraintSetLandScape
    }

    fun cllMinWidth(w:Int): JJImageCategoryCircle {
        mConstraintSetLandScape.constrainMinWidth(id,w)
        return this
    }

    fun cllMinHeight(h:Int): JJImageCategoryCircle {
        mConstraintSetLandScape.constrainMinHeight(id,h)
        return this
    }

    fun cllMaxWidth(w:Int): JJImageCategoryCircle {
        mConstraintSetLandScape.constrainMaxWidth(id,w)
        return this
    }

    fun cllMaxHeight(h:Int): JJImageCategoryCircle {
        mConstraintSetLandScape.constrainMaxHeight(id,h)
        return this
    }






//endregion

    //region LinearLayout Params

    private var mLlp: LinearLayout.LayoutParams? = null
    private fun setupLlp() {
        if (mLlp == null) {
            mLlp = layoutParams as? LinearLayout.LayoutParams
            layoutParams = mLlp
        }
    }

    fun llWidth(width: Int): JJImageCategoryCircle {
        setupLlp()
        mLlp!!.width = width
        return this
    }

    fun llHeight(height: Int): JJImageCategoryCircle {
        setupLlp()
        mLlp!!.height = height
        return this
    }

    fun llWeight(weigth: Float): JJImageCategoryCircle {
        setupLlp()
        mLlp!!.weight = weigth
        return this
    }

    fun llGravity(gravity: Int): JJImageCategoryCircle {
        setupLlp()
        mLlp!!.gravity = gravity
        return this
    }

    fun llTopMargin(m : Int): JJImageCategoryCircle {
        setupLlp()
        mLlp!!.topMargin = m
        return this
    }

    fun llBottomMargin(m : Int): JJImageCategoryCircle {
        setupLlp()
        mLlp!!.bottomMargin = m
        return this
    }

    fun llStartMargin(m : Int): JJImageCategoryCircle {
        setupLlp()
        mLlp!!.marginStart = m
        return this
    }

    fun llEndMargin(m : Int): JJImageCategoryCircle {
        setupLlp()
        mLlp!!.marginEnd = m
        return this
    }

    fun llLeftMargin(m : Int): JJImageCategoryCircle {
        setupLlp()
        mLlp!!.leftMargin = m
        return this
    }

    fun llRightMargin(m : Int): JJImageCategoryCircle {
        setupLlp()
        mLlp!!.rightMargin = m
        return this
    }


    fun llMargins( margins : JJMargin): JJImageCategoryCircle {
        setupLlp()
        mLlp!!.setMargins(margins.left,margins.top,margins.right,margins.bottom)
        return this
    }

    //endregion

}