package com.jjlf.jjkit_layoutplus

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
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
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.AppBarLayout
import com.jjlf.jjkit_layoututils.JJLayout
import com.jjlf.jjkit_layoututils.JJMargin
import com.jjlf.jjkit_layoututils.JJPadding
import com.jjlf.jjkit_layoututils.JJScreen

@SuppressLint("ResourceType")
class JJProgressViewFS: ConstraintLayout {

    //region custom

    private lateinit var mProgressView : ProgressBar
    private fun setupViews(context: Context,attrs: AttributeSet?){
        val t = context.obtainStyledAttributes(attrs,R.styleable.JJProgressViewFS,0,0)
        val size = t.getDimensionPixelSize(R.styleable.JJProgressViewFS_progressSize,JJScreen.point(160))
        val color = t.getColor(R.styleable.JJProgressViewFS_colorOnSurface,Color.BLACK)
        t.recycle()

        mProgressView = ProgressBar(context)
        mProgressView.id = View.generateViewId()
        addView(mProgressView)

        JJLayout.clSetView(mProgressView)
            .clCenterInParent()
            .clWidth(size)
            .clHeight(size)
            .clDisposeView()

        isClickable = true
        outlineProvider = null
        setProgressColor(color)
        if(background == null) setBackgroundColor(Color.WHITE)

    }

    fun setProgressVisibility(mode: Int) :JJProgressViewFS {
        val csp = ConstraintSet()
        csp.clone(this)
        csp.setVisibility(mProgressView.id,mode)
        csp.applyTo(this)
        return this
    }

    fun setProgressSize(size: Int) :JJProgressViewFS{
        val csp = ConstraintSet()
        csp.clone(this)
        csp.constrainHeight(mProgressView.id,size)
        csp.constrainWidth(mProgressView.id,size)
        csp.applyTo(this)
        return this
    }

    fun setProgressColor(color: Int) :JJProgressViewFS{
        mProgressView.indeterminateTintList = ColorStateList.valueOf(color)
        return this
    }

    fun setProgressColorRes(resId: Int) :JJProgressViewFS{
        val color = ContextCompat.getColor(context,resId)
        mProgressView.indeterminateTintList = ColorStateList.valueOf(color)
        return this
    }

    fun setBackgroundColorRes(resId: Int): JJProgressViewFS {
        val color = ContextCompat.getColor(context,resId)
        setBackgroundColor(color)
        return this
    }

    fun getProgress(): ProgressBar {
        return mProgressView
    }

    //endregion

    //region init

    private var mSupportLandScape = false
    private var mIgnoreCl = false
    constructor(context: Context) : this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        setupInitConstraint()
        setupLayout(attrs)
        setupViews(context,attrs)
    }

    private var mClMargin = JJMargin()
    private var mCllMargin = JJMargin()

    @SuppressLint("CustomViewStyleable")
    private fun setupLayout(attrs: AttributeSet?){
        val a = context.obtainStyledAttributes(attrs,
            R.styleable.jjlayoutplus, 0, 0)
        mIgnoreCl = a.getBoolean(R.styleable.jjlayoutplus_layout_ignoreCl,false)
        mConfigurationChanged = a.getBoolean(R.styleable.jjlayoutplus_support_configuration_changed,false)
        mSupportLandScape = a.getBoolean(R.styleable.jjlayoutplus_support_landscape,false)

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

        clMargins(mClMargin)
        cllMargins(mCllMargin)

    }
    private fun setupAndroidBase(attrs: AttributeSet?){
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
            R.styleable.jjlayoutplus_lpHeightPercentScreenWidth -> {
                mlpHeight = JJScreen.percentWidth(a.getFloat(R.styleable.jjlayoutplus_lpHeightPercentScreenWidth,0f))
            }
            R.styleable.jjlayoutplus_lpHeightPercentScreenHeight -> {
                mlpHeight = JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_lpHeightPercentScreenHeight,0f))
            }
            R.styleable.jjlayoutplus_lpWidthPercentScreenWidth -> {
                mlpWidth = JJScreen.percentWidth( a.getFloat(R.styleable.jjlayoutplus_lpWidthPercentScreenWidth,0f))
            }
            R.styleable.jjlayoutplus_lpWidthPercentScreenHeight -> {
                mlpWidth = JJScreen.percentHeight( a.getFloat(R.styleable.jjlayoutplus_lpWidthPercentScreenHeight,0f))
            }
            R.styleable.jjlayoutplus_lpHeightResponsive -> {
                mlpHeight =  responsiveSizeDimension(a, R.styleable.jjlayoutplus_lpHeightResponsive)
            }
            R.styleable.jjlayoutplus_lpWidthResponsive -> {
                mlpWidth =  responsiveSizeDimension(a, R.styleable.jjlayoutplus_lpWidthResponsive)
            }
            R.styleable.jjlayoutplus_lpHeightResponsivePercentScreenHeight -> {
                mlpHeight = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_lpHeightResponsivePercentScreenHeight)
            }
            R.styleable.jjlayoutplus_lpWidthResponsivePercentScreenHeight -> {
                mlpWidth = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_lpWidthResponsivePercentScreenHeight)
            }
            R.styleable.jjlayoutplus_lpHeightResponsivePercentScreenWidth -> {
                mlpHeight = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_lpHeightResponsivePercentScreenWidth)
            }
            R.styleable.jjlayoutplus_lpWidthResponsivePercentScreenWidth -> {
                mlpWidth = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_lpWidthResponsivePercentScreenWidth)
            }
        }
    }
    private fun setupMarginLp(a: TypedArray, index: Int){
        when(a.getIndex(index)){
            R.styleable.jjlayoutplus_lpMarginTopPerScHeight -> {
                mlpMargins.top = JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_lpMarginTopPerScHeight,0f))
            }
            R.styleable.jjlayoutplus_lpMarginLeftPerScHeight -> {
                mlpMargins.left = JJScreen.percentHeight( a.getFloat(R.styleable.jjlayoutplus_lpMarginLeftPerScHeight,0f))
            }
            R.styleable.jjlayoutplus_lpMarginRightPerScHeight -> {
                mlpMargins.right = JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_lpMarginRightPerScHeight,0f))
            }
            R.styleable.jjlayoutplus_lpMarginBottomPerScHeight -> {
                mlpMargins.bottom = JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_lpMarginBottomPerScHeight,0f))
            }
            R.styleable.jjlayoutplus_lpMarginTopPerScWidth -> {
                mlpMargins.top = JJScreen.percentWidth(a.getFloat(R.styleable.jjlayoutplus_lpMarginTopPerScWidth,0f))
            }
            R.styleable.jjlayoutplus_lpMarginLeftPerScWidth -> {
                mlpMargins.left = JJScreen.percentWidth( a.getFloat(R.styleable.jjlayoutplus_lpMarginLeftPerScWidth,0f))
            }
            R.styleable.jjlayoutplus_lpMarginRightPerScWidth->{
                mlpMargins.right = JJScreen.percentWidth(a.getFloat(R.styleable.jjlayoutplus_lpMarginRightPerScWidth,0f))
            }
            R.styleable.jjlayoutplus_lpMarginBottomPerScWidth -> {
                mlpMargins.bottom = JJScreen.percentWidth(a.getFloat(R.styleable.jjlayoutplus_lpMarginBottomPerScWidth,0f))
            }
            R.styleable.jjlayoutplus_lpMarginTopResponsive -> {
                mlpMargins.top = responsiveSizeDimension(a, R.styleable.jjlayoutplus_lpMarginTopResponsive)
            }
            R.styleable.jjlayoutplus_lpMarginLeftResponsive ->{
                mlpMargins.left =  responsiveSizeDimension(a, R.styleable.jjlayoutplus_lpMarginLeftResponsive)
            }
            R.styleable.jjlayoutplus_lpMarginRightResponsive -> {
                mlpMargins.right =   responsiveSizeDimension(a, R.styleable.jjlayoutplus_lpMarginRightResponsive)
            }
            R.styleable.jjlayoutplus_lpMarginBottomResponsive -> {
                mlpMargins.bottom =  responsiveSizeDimension(a, R.styleable.jjlayoutplus_lpMarginBottomResponsive)
            }
            R.styleable.jjlayoutplus_lpMarginTopResPerScWidth -> {
                mlpMargins.top  = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_lpMarginTopResPerScWidth)
            }
            R.styleable.jjlayoutplus_lpMarginLeftResPerScWidth -> {
                mlpMargins.left = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_lpMarginLeftResPerScWidth)
            }
            R.styleable.jjlayoutplus_lpMarginRightResPerScWidth -> {
                mlpMargins.right =  responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_lpMarginRightResPerScWidth)
            }
            R.styleable.jjlayoutplus_lpMarginBottomResPerScWidth -> {
                mlpMargins.bottom = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_lpMarginBottomResPerScWidth)
            }
            R.styleable.jjlayoutplus_lpMarginTopResPerScHeight ->{
                mlpMargins.top = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_lpMarginTopResPerScHeight)
            }
            R.styleable.jjlayoutplus_lpMarginLeftResPerScHeight ->{
                mlpMargins.left = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_lpMarginLeftResPerScHeight)
            }
            R.styleable.jjlayoutplus_lpMarginRightResPerScHeight ->{
                mlpMargins.right = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_lpMarginRightResPerScHeight)
            }
            R.styleable.jjlayoutplus_lpMarginBottomResPerScHeight ->{
                mlpMargins.bottom = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_lpMarginBottomResPerScHeight)
            }
            R.styleable.jjlayoutplus_lpMarginPercentScHeight -> {
                mlpMargins = JJMargin.all(JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_lpMarginPercentScHeight,0f)))
            }
            R.styleable.jjlayoutplus_lpMarginPercentScWidth -> {
                mlpMargins = JJMargin.all(JJScreen.percentWidth( a.getFloat(R.styleable.jjlayoutplus_lpMarginPercentScWidth,0f)))
            }
            R.styleable.jjlayoutplus_lpMarginResponsive -> {
                mlpMargins = JJMargin.all(responsiveSizeDimension(a, R.styleable.jjlayoutplus_lpMarginResponsive))
            }
            R.styleable.jjlayoutplus_lpMarginResPerScHeight -> {
                mlpMargins = JJMargin.all(responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_lpMarginResPerScHeight))
            }
            R.styleable.jjlayoutplus_lpMarginResPerScWidth -> {
                mlpMargins =  JJMargin.all(responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_lpMarginResPerScWidth))
            }
            R.styleable.jjlayoutplus_lpMarginVerticalPerScHeight -> {
                val mar = JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_lpMarginVerticalPerScHeight,0f))
                mlpMargins.top = mar ; mlpMargins.bottom = mar
            }
            R.styleable.jjlayoutplus_lpMarginVerticalPerScWidth ->{
                val mar = JJScreen.percentWidth(a.getFloat(R.styleable.jjlayoutplus_lpMarginVerticalPerScWidth,0f))
                mlpMargins.top = mar ; mlpMargins.bottom = mar
            }
            R.styleable.jjlayoutplus_lpMarginVerticalResponsive -> {
                val mar = responsiveSizeDimension(a, R.styleable.jjlayoutplus_lpMarginVerticalResponsive)
                mlpMargins.top = mar ; mlpMargins.bottom = mar
            }
            R.styleable.jjlayoutplus_lpMarginVerticalResPerScWidth -> {
                val mar = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_lpMarginVerticalResPerScWidth )
                mlpMargins.top = mar ; mlpMargins.bottom = mar
            }
            R.styleable.jjlayoutplus_lpMarginVerticalResPerScHeight -> {
                val mar = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_lpMarginVerticalResPerScHeight)
                mlpMargins.top = mar ; mlpMargins.bottom = mar
            }
            R.styleable.jjlayoutplus_lpMarginHorizontalPerScHeight -> {
                val mar = JJScreen.percentHeight( a.getFloat(R.styleable.jjlayoutplus_lpMarginHorizontalPerScHeight,0f))
                mlpMargins.left = mar ; mlpMargins.right = mar
            }
            R.styleable.jjlayoutplus_lpMarginHorizontalPerScWidth -> {
                val mar = JJScreen.percentWidth(a.getFloat(R.styleable.jjlayoutplus_lpMarginHorizontalPerScWidth,0f))
                mlpMargins.left = mar ; mlpMargins.right = mar
            }
            R.styleable.jjlayoutplus_lpMarginHorizontalResponsive -> {
                val mar = responsiveSizeDimension(a, R.styleable.jjlayoutplus_lpMarginHorizontalResponsive)
                mlpMargins.left = mar ; mlpMargins.right = mar
            }
            R.styleable.jjlayoutplus_lpMarginHorizontalResPerScWidth -> {
                val mar = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_lpMarginHorizontalResPerScWidth)
                mlpMargins.left = mar ; mlpMargins.right = mar
            }
            R.styleable.jjlayoutplus_lpMarginHorizontalResPerScHeight -> {
                val mar = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_lpMarginHorizontalResPerScHeight)
                mlpMargins.left = mar ; mlpMargins.right = mar
            }
        }

    }
    private fun setupPaddingLp(a: TypedArray, index:Int){
        when(a.getIndex(index)){
            R.styleable.jjlayoutplus_lpPaddingTopPerScHeight -> {
                mlpPadding.top = JJScreen.percentHeight( a.getFloat(R.styleable.jjlayoutplus_lpPaddingTopPerScHeight,0f))
            }
            R.styleable.jjlayoutplus_lpPaddingLeftPerScHeight -> {
                mlpPadding.left = JJScreen.percentHeight( a.getFloat(R.styleable.jjlayoutplus_lpPaddingLeftPerScHeight,0f))
            }
            R.styleable.jjlayoutplus_lpPaddingRightPerScHeight -> {
                mlpPadding.right = JJScreen.percentHeight( a.getFloat(R.styleable.jjlayoutplus_lpPaddingRightPerScHeight,0f))
            }
            R.styleable.jjlayoutplus_lpPaddingBottomPerScHeight -> {
                mlpPadding.bottom = JJScreen.percentHeight( a.getFloat(R.styleable.jjlayoutplus_lpPaddingBottomPerScHeight,0f))
            }
            R.styleable.jjlayoutplus_lpPaddingTopPerScWidth -> {
                mlpPadding.top = JJScreen.percentWidth( a.getFloat(R.styleable.jjlayoutplus_lpPaddingTopPerScWidth,0f))
            }
            R.styleable.jjlayoutplus_lpPaddingLeftPerScWidth -> {
                mlpPadding.left = JJScreen.percentWidth( a.getFloat(R.styleable.jjlayoutplus_lpPaddingLeftPerScWidth,0f))
            }
            R.styleable.jjlayoutplus_lpPaddingRightPerScWidth -> {
                mlpPadding.right = JJScreen.percentWidth( a.getFloat(R.styleable.jjlayoutplus_lpPaddingRightPerScWidth,0f))
            }
            R.styleable.jjlayoutplus_lpPaddingBottomPerScWidth -> {
                mlpPadding.bottom = JJScreen.percentWidth( a.getFloat(R.styleable.jjlayoutplus_lpPaddingBottomPerScWidth,0f))
            }
            R.styleable.jjlayoutplus_lpPaddingTopResponsive -> {
                mlpPadding.top = responsiveSizeDimension(a, R.styleable.jjlayoutplus_lpPaddingTopResponsive)
            }
            R.styleable.jjlayoutplus_lpPaddingLeftResponsive -> {
                mlpPadding.left = responsiveSizeDimension(a, R.styleable.jjlayoutplus_lpPaddingLeftResponsive)
            }
            R.styleable.jjlayoutplus_lpPaddingRightResponsive -> {
                mlpPadding.right = responsiveSizeDimension(a, R.styleable.jjlayoutplus_lpPaddingRightResponsive)
            }
            R.styleable.jjlayoutplus_lpPaddingBottomResponsive -> {
                mlpPadding.bottom = responsiveSizeDimension(a, R.styleable.jjlayoutplus_lpPaddingBottomResponsive)
            }
            R.styleable.jjlayoutplus_lpPaddingTopResPerScWidth -> {
                mlpPadding.top = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_lpPaddingTopResPerScWidth )
            }
            R.styleable.jjlayoutplus_lpPaddingLeftResPerScWidth -> {
                mlpPadding.left = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_lpPaddingLeftResPerScWidth )
            }
            R.styleable.jjlayoutplus_lpPaddingRightResPerScWidth -> {
                mlpPadding.right = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_lpPaddingRightResPerScWidth )
            }
            R.styleable.jjlayoutplus_lpPaddingBottomResPerScWidth -> {
                mlpPadding.bottom = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_lpPaddingBottomResPerScWidth )
            }

            R.styleable.jjlayoutplus_lpPaddingTopResPerScHeight -> {
                mlpPadding.top = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_lpPaddingTopResPerScHeight )
            }
            R.styleable.jjlayoutplus_lpPaddingLeftResPerScHeight -> {
                mlpPadding.left = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_lpPaddingLeftResPerScHeight )
            }
            R.styleable.jjlayoutplus_lpPaddingRightResPerScHeight -> {
                mlpPadding.right = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_lpPaddingRightResPerScHeight )
            }
            R.styleable.jjlayoutplus_lpPaddingBottomResPerScHeight -> {
                mlpPadding.bottom = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_lpPaddingBottomResPerScHeight )
            }
            R.styleable.jjlayoutplus_lpPaddingPercentScHeight -> {
                mlpPadding = JJPadding.all(JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_lpPaddingPercentScHeight,0f)))
            }
            R.styleable.jjlayoutplus_lpPaddingPercentScWidth -> {
                mlpPadding = JJPadding.all(JJScreen.percentWidth(a.getFloat(R.styleable.jjlayoutplus_lpPaddingPercentScWidth,0f)))
            }
            R.styleable.jjlayoutplus_lpPaddingResponsive -> {
                mlpPadding = JJPadding.all(responsiveSizeDimension(a, R.styleable.jjlayoutplus_lpPaddingResponsive))
            }
            R.styleable.jjlayoutplus_lpPaddingResPerScHeight -> {
                mlpPadding = JJPadding.all(responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_lpPaddingResPerScHeight))
            }
            R.styleable.jjlayoutplus_lpPaddingResPerScWidth -> {
                mlpPadding = JJPadding.all(responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_lpPaddingResPerScWidth))
            }
            R.styleable.jjlayoutplus_lpPaddingVerticalPerScHeight -> {
                val mar = JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_lpPaddingVerticalPerScHeight,0f))
                mlpPadding.top = mar ; mlpPadding.bottom = mar
            }
            R.styleable.jjlayoutplus_lpPaddingVerticalPerScWidth -> {
                val mar = JJScreen.percentWidth(a.getFloat(R.styleable.jjlayoutplus_lpPaddingVerticalPerScWidth,0f))
                mlpPadding.top = mar ; mlpPadding.bottom = mar
            }
            R.styleable.jjlayoutplus_lpPaddingVerticalResponsive -> {
                val mar = responsiveSizeDimension(a, R.styleable.jjlayoutplus_lpPaddingVerticalResponsive)
                mlpPadding.top = mar ; mlpPadding.bottom = mar
            }
            R.styleable.jjlayoutplus_lpPaddingVerticalResPerScWidth -> {
                val mar = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_lpPaddingVerticalResPerScWidth)
                mlpPadding.top = mar ; mlpPadding.bottom = mar
            }
            R.styleable.jjlayoutplus_lpPaddingVerticalResPerScHeight -> {
                val mar = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_lpPaddingVerticalResPerScHeight)
                mlpPadding.top = mar ; mlpPadding.bottom = mar
            }
            R.styleable.jjlayoutplus_lpPaddingHorizontalPerScHeight -> {
                val mar = JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_lpPaddingHorizontalPerScHeight,0f))
                mlpPadding.left = mar ; mlpPadding.right = mar
            }
            R.styleable.jjlayoutplus_lpPaddingHorizontalPerScWidth -> {
                val mar = JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_lpPaddingHorizontalPerScWidth,0f))
                mlpPadding.left = mar ; mlpPadding.right = mar
            }
            R.styleable.jjlayoutplus_lpPaddingHorizontalResponsive -> {
                val mar = responsiveSizeDimension(a, R.styleable.jjlayoutplus_lpPaddingHorizontalResponsive)
                mlpPadding.left = mar ; mlpPadding.right = mar
            }
            R.styleable.jjlayoutplus_lpPaddingHorizontalResPerScWidth ->{
                val mar = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_lpPaddingHorizontalResPerScWidth)
                mlpPadding.left = mar ; mlpPadding.right = mar
            }
            R.styleable.jjlayoutplus_lpPaddingHorizontalResPerScHeight ->{
                val mar = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_lpPaddingHorizontalResPerScHeight)
                mlpPadding.left = mar ; mlpPadding.right = mar
            }

        }

    }

    private fun setupSizeCl(a: TypedArray, index:Int){
        when(a.getIndex(index)){
            R.styleable.jjlayoutplus_clHeightPercent -> {
                clPercentHeight( a.getFloat(R.styleable.jjlayoutplus_clHeightPercent,0f))
            }
            R.styleable.jjlayoutplus_clWidthPercent -> {
                clPercentWidth( a.getFloat(R.styleable.jjlayoutplus_clWidthPercent,0f))
            }
            R.styleable.jjlayoutplus_clHeightPercentScreenWidth -> {
                clHeight(JJScreen.percentWidth(a.getFloat(R.styleable.jjlayoutplus_clHeightPercentScreenWidth,0f)))
            }
            R.styleable.jjlayoutplus_clWidthPercentScreenWidth -> {
                clWidth(JJScreen.percentWidth(a.getFloat(R.styleable.jjlayoutplus_clWidthPercentScreenWidth,0f)))
            }

            R.styleable.jjlayoutplus_clHeightPercentScreenHeight -> {
                clHeight(JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_clHeightPercentScreenHeight,0f)))
            }
            R.styleable.jjlayoutplus_clWidthPercentScreenHeight -> {
                clWidth(JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_clWidthPercentScreenHeight,0f)))
            }
            R.styleable.jjlayoutplus_clHeightResponsive -> {
                clHeight(responsiveSizeDimension(a, R.styleable.jjlayoutplus_clHeightResponsive))
            }
            R.styleable.jjlayoutplus_clWidthResponsive -> {
                clWidth(responsiveSizeDimension(a, R.styleable.jjlayoutplus_clWidthResponsive))
            }
            R.styleable.jjlayoutplus_clHeightResponsivePercentScreenHeight ->{
                clHeight(responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_clHeightResponsivePercentScreenHeight))
            }
            R.styleable.jjlayoutplus_clWidthResponsivePercentScreenHeight ->{
                clWidth(responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_clWidthResponsivePercentScreenHeight))
            }

            R.styleable.jjlayoutplus_clHeightResponsivePercentScreenWidth ->{
                clHeight(responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_clHeightResponsivePercentScreenWidth))
            }
            R.styleable.jjlayoutplus_clWidthResponsivePercentScreenWidth ->{
                clWidth(responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_clWidthResponsivePercentScreenWidth))
            }
        }




    }
    private fun setupAnchorsCl(a: TypedArray, index:Int){
        when(a.getIndex(index)){
            R.styleable.jjlayoutplus_clFillParent -> {
                if(a.getBoolean(R.styleable.jjlayoutplus_clFillParent,false)) clFillParent()
            }
            R.styleable.jjlayoutplus_clFillParentHorizontally -> {
                if(a.getBoolean(R.styleable.jjlayoutplus_clFillParentHorizontally,false)) clFillParentHorizontally()
            }
            R.styleable.jjlayoutplus_clFillParentVertically -> {
                if(a.getBoolean(R.styleable.jjlayoutplus_clFillParentVertically,false)) clFillParentVertically()
            }
            R.styleable.jjlayoutplus_clCenterInParent -> {
                if(a.getBoolean(R.styleable.jjlayoutplus_clCenterInParent,false)) clCenterInParent()
            }
            R.styleable.jjlayoutplus_clCenterInParentHorizontally -> {
                if(a.getBoolean(R.styleable.jjlayoutplus_clCenterInParentHorizontally,false)) clCenterInParentHorizontally()
            }
            R.styleable.jjlayoutplus_clCenterInParentVertically -> {
                if(a.getBoolean(R.styleable.jjlayoutplus_clCenterInParentVertically,false)) clCenterInParentVertically()
            }
            R.styleable.jjlayoutplus_clCenterInParentTopVertically -> {
                if(a.getBoolean(R.styleable.jjlayoutplus_clCenterInParentTopVertically,false))  clCenterInParentTopVertically()
            }
            R.styleable.jjlayoutplus_clCenterInParentBottomVertically -> {
                if(a.getBoolean(R.styleable.jjlayoutplus_clCenterInParentBottomVertically,false)) clCenterInParentBottomVertically()
            }
            R.styleable.jjlayoutplus_clCenterInParentStartHorizontally -> {
                if(a.getBoolean(R.styleable.jjlayoutplus_clCenterInParentStartHorizontally,false)) clCenterInParentStartHorizontally()
            }
            R.styleable.jjlayoutplus_clCenterInParentEndHorizontally -> {
                if(a.getBoolean(R.styleable.jjlayoutplus_clCenterInParentEndHorizontally,false)) clCenterInParentEndHorizontally()
            }

            R.styleable.jjlayoutplus_clCenterInTopVerticallyOf -> {
                clCenterInTopVertically(a.getResourceId(
                    R.styleable.jjlayoutplus_clCenterInTopVerticallyOf,
                    View.NO_ID))
            }
            R.styleable.jjlayoutplus_clCenterInBottomVerticallyOf -> {
                clCenterInBottomVertically(a.getResourceId(
                    R.styleable.jjlayoutplus_clCenterInBottomVerticallyOf,
                    View.NO_ID))
            }
            R.styleable.jjlayoutplus_clCenterInStartHorizontallyOf -> {
                clCenterInStartHorizontally(a.getResourceId(
                    R.styleable.jjlayoutplus_clCenterInStartHorizontallyOf,
                    View.NO_ID))
            }
            R.styleable.jjlayoutplus_clCenterInEndHorizontallyOf -> {
                clCenterInEndHorizontally(a.getResourceId(
                    R.styleable.jjlayoutplus_clCenterInEndHorizontallyOf,
                    View.NO_ID))
            }
            R.styleable.jjlayoutplus_clCenterVerticallyOf -> {
                clCenterVerticallyOf(a.getResourceId(
                    R.styleable.jjlayoutplus_clCenterVerticallyOf,
                    View.NO_ID))
            }
            R.styleable.jjlayoutplus_clCenterHorizontallyOf -> {
                clCenterHorizontallyOf(a.getResourceId(
                    R.styleable.jjlayoutplus_clCenterHorizontallyOf,
                    View.NO_ID))
            }
            R.styleable.jjlayoutplus_clVerticalBias -> {
                clVerticalBias(a.getFloat(R.styleable.jjlayoutplus_clVerticalBias,0.5f))
            }
            R.styleable.jjlayoutplus_clHorizontalBias -> {
                clHorizontalBias( a.getFloat(R.styleable.jjlayoutplus_clHorizontalBias,0.5f))
            }
            R.styleable.jjlayoutplus_clStartToStartParent -> {
                if(a.getBoolean(R.styleable.jjlayoutplus_clStartToStartParent,false)) clStartToStartParent()
            }
            R.styleable.jjlayoutplus_clStartToEndParent -> {
                if(a.getBoolean(R.styleable.jjlayoutplus_clStartToEndParent,false)) clStartToEndParent()
            }
            R.styleable.jjlayoutplus_clEndToEndParent -> {
                if(a.getBoolean(R.styleable.jjlayoutplus_clEndToEndParent,false)) clEndToEndParent()
            }
            R.styleable.jjlayoutplus_clEndToStartParent -> {
                if(a.getBoolean(R.styleable.jjlayoutplus_clEndToStartParent,false)) clEndToStartParent()
            }
            R.styleable.jjlayoutplus_clTopToTopParent -> {
                if(a.getBoolean(R.styleable.jjlayoutplus_clTopToTopParent,false)) clTopToTopParent()
            }
            R.styleable.jjlayoutplus_clTopToBottomParent -> {
                if(a.getBoolean(R.styleable.jjlayoutplus_clTopToBottomParent,false)) clTopToBottomParent()
            }
            R.styleable.jjlayoutplus_clBottomToBottomParent -> {
                if(a.getBoolean(R.styleable.jjlayoutplus_clBottomToBottomParent,false)) clBottomToBottomParent()
            }
            R.styleable.jjlayoutplus_clBottomToTopParent -> {
                if(a.getBoolean(R.styleable.jjlayoutplus_clBottomToTopParent,false)) clBottomToTopParent()
            }

            R.styleable.jjlayoutplus_clStartToStartOf -> {
                clStartToStart(a.getResourceId(R.styleable.jjlayoutplus_clStartToStartOf, View.NO_ID))
            }
            R.styleable.jjlayoutplus_clStartToEndOf -> {
                clStartToEnd(a.getResourceId(R.styleable.jjlayoutplus_clStartToEndOf, View.NO_ID))
            }
            R.styleable.jjlayoutplus_clEndToEndOf -> {
                clEndToEnd(a.getResourceId(R.styleable.jjlayoutplus_clEndToEndOf, View.NO_ID))
            }
            R.styleable.jjlayoutplus_clEndToStartOf -> {
                clEndToStart(a.getResourceId(R.styleable.jjlayoutplus_clEndToStartOf, View.NO_ID))
            }
            R.styleable.jjlayoutplus_clTopToTopOf -> {
                clTopToTop(a.getResourceId(R.styleable.jjlayoutplus_clTopToTopOf, View.NO_ID))
            }
            R.styleable.jjlayoutplus_clTopToBottomOf -> {
                clTopToBottom(a.getResourceId(R.styleable.jjlayoutplus_clTopToBottomOf, View.NO_ID))
            }
            R.styleable.jjlayoutplus_clBottomToBottomOf -> {
                clBottomToBottom(a.getResourceId(R.styleable.jjlayoutplus_clBottomToBottomOf, View.NO_ID))
            }
            R.styleable.jjlayoutplus_clBottomToTopOf -> {
                clBottomToTop(a.getResourceId(R.styleable.jjlayoutplus_clBottomToTopOf, View.NO_ID))
            }

        }
    }
    private fun setupMarginCl(a: TypedArray, index:Int){
        when(a.getIndex(index)){
            R.styleable.jjlayoutplus_clMarginEnd ->{
                mClMargin.right = a.getDimension(R.styleable.jjlayoutplus_clMarginEnd,0f).toInt()
            }
            R.styleable.jjlayoutplus_clMarginStart ->{
                mClMargin.left = a.getDimension(R.styleable.jjlayoutplus_clMarginStart,0f).toInt()
            }
            R.styleable.jjlayoutplus_clMarginTop ->{
                mClMargin.top = a.getDimension(R.styleable.jjlayoutplus_clMarginTop,0f).toInt()
            }
            R.styleable.jjlayoutplus_clMarginBottom ->{
                mClMargin.bottom = a.getDimension(R.styleable.jjlayoutplus_clMarginBottom,0f).toInt()
            }

            R.styleable.jjlayoutplus_clMarginEndPercentScreenHeight -> {
                mClMargin.right = JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_clMarginEndPercentScreenHeight,0f))
            }
            R.styleable.jjlayoutplus_clMarginStartPercentScreenHeight -> {
                mClMargin.left = JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_clMarginStartPercentScreenHeight,0f))
            }
            R.styleable.jjlayoutplus_clMarginTopPercentScreenHeight -> {
                mClMargin.top = JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_clMarginTopPercentScreenHeight,0f))
            }
            R.styleable.jjlayoutplus_clMarginBottomPercentScreenHeight -> {
                mClMargin.bottom = JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_clMarginBottomPercentScreenHeight,0f))
            }

            R.styleable.jjlayoutplus_clMarginEndPercentScreenWidth -> {
                mClMargin.right = JJScreen.percentWidth(a.getFloat(R.styleable.jjlayoutplus_clMarginEndPercentScreenWidth,0f))
            }
            R.styleable.jjlayoutplus_clMarginStartPercentScreenWidth -> {
                mClMargin.left = JJScreen.percentWidth(a.getFloat(R.styleable.jjlayoutplus_clMarginStartPercentScreenWidth,0f))
            }
            R.styleable.jjlayoutplus_clMarginTopPercentScreenWidth -> {
                mClMargin.top = JJScreen.percentWidth(a.getFloat(R.styleable.jjlayoutplus_clMarginTopPercentScreenWidth,0f))
            }
            R.styleable.jjlayoutplus_clMarginBottomPercentScreenWidth -> {
                mClMargin.bottom = JJScreen.percentWidth(a.getFloat(R.styleable.jjlayoutplus_clMarginBottomPercentScreenWidth,0f))
            }
            R.styleable.jjlayoutplus_clMargin -> {
                mClMargin = JJMargin.all(a.getDimension(R.styleable.jjlayoutplus_clMargin,0f).toInt())
            }
            R.styleable.jjlayoutplus_clMarginPerScHeight -> {
                mClMargin = JJMargin.all(JJScreen.percentHeight( a.getFloat(R.styleable.jjlayoutplus_clMarginPerScHeight,0f)))
            }
            R.styleable.jjlayoutplus_clMarginPerScWidth -> {
                mClMargin = JJMargin.all(JJScreen.percentWidth( a.getFloat(R.styleable.jjlayoutplus_clMarginPerScWidth,0f)))
            }
            R.styleable.jjlayoutplus_clMarginResponsive -> {
                mClMargin = JJMargin.all(responsiveSizeDimension(a, R.styleable.jjlayoutplus_clMarginResponsive))
            }
            R.styleable.jjlayoutplus_clMarginResPerScHeight -> {
                mClMargin = JJMargin.all(responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_clMarginResPerScHeight))
            }
            R.styleable.jjlayoutplus_clMarginResPerScWidth -> {
                mClMargin = JJMargin.all(responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_clMarginResPerScWidth))
            }
            R.styleable.jjlayoutplus_clMarginEndResponsive -> {
                mClMargin.right = responsiveSizeDimension(a, R.styleable.jjlayoutplus_clMarginEndResponsive)
            }
            R.styleable.jjlayoutplus_clMarginStartResponsive -> {
                mClMargin.left = responsiveSizeDimension(a, R.styleable.jjlayoutplus_clMarginStartResponsive)
            }
            R.styleable.jjlayoutplus_clMarginTopResponsive -> {
                mClMargin.top = responsiveSizeDimension(a, R.styleable.jjlayoutplus_clMarginTopResponsive)
            }
            R.styleable.jjlayoutplus_clMarginBottomResponsive -> {
                mClMargin.bottom = responsiveSizeDimension(a, R.styleable.jjlayoutplus_clMarginBottomResponsive)
            }

            R.styleable.jjlayoutplus_clMarginEndResPerScHeight -> {
                mClMargin.right = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_clMarginEndResPerScHeight)
            }
            R.styleable.jjlayoutplus_clMarginStartResPerScHeight -> {
                mClMargin.left = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_clMarginStartResPerScHeight)
            }
            R.styleable.jjlayoutplus_clMarginTopResPerScHeight -> {
                mClMargin.top = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_clMarginTopResPerScHeight)
            }
            R.styleable.jjlayoutplus_clMarginBottomResPerScHeight -> {
                mClMargin.bottom = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_clMarginBottomResPerScHeight)
            }

            R.styleable.jjlayoutplus_clMarginEndResPerScWidth -> {
                mClMargin.right = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_clMarginEndResPerScWidth)
            }
            R.styleable.jjlayoutplus_clMarginStartResPerScWidth -> {
                mClMargin.left = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_clMarginStartResPerScWidth)
            }
            R.styleable.jjlayoutplus_clMarginTopResPerScWidth -> {
                mClMargin.top = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_clMarginTopResPerScWidth)
            }
            R.styleable.jjlayoutplus_clMarginBottomResPerScWidth -> {
                mClMargin.bottom = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_clMarginBottomResPerScWidth)
            }
            R.styleable.jjlayoutplus_clMarginVertical -> {
                val mar = a.getDimension(R.styleable.jjlayoutplus_clMarginVertical,0f).toInt()
                mClMargin.top = mar ; mClMargin.bottom = mar
            }
            R.styleable.jjlayoutplus_clMarginVerticalPerScHeight -> {
                val mar = JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_clMarginVerticalPerScHeight,0f))
                mClMargin.top = mar ; mClMargin.bottom = mar
            }
            R.styleable.jjlayoutplus_clMarginVerticalPerScWidth -> {
                val mar = JJScreen.percentWidth(a.getFloat(R.styleable.jjlayoutplus_clMarginVerticalPerScWidth,0f))
                mClMargin.top = mar ; mClMargin.bottom = mar
            }
            R.styleable.jjlayoutplus_clMarginVerticalResponsive -> {
                val mar = responsiveSizeDimension(a, R.styleable.jjlayoutplus_clMarginVerticalResponsive)
                mClMargin.top = mar ; mClMargin.bottom = mar
            }
            R.styleable.jjlayoutplus_clMarginVerticalResPerScHeight -> {
                val mar = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_clMarginVerticalResPerScHeight)
                mClMargin.top = mar ; mClMargin.bottom = mar
            }
            R.styleable.jjlayoutplus_clMarginVerticalResPerScWidth -> {
                val mar = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_clMarginVerticalResPerScWidth)
                mClMargin.top = mar ; mClMargin.bottom = mar
            }

            R.styleable.jjlayoutplus_clMarginHorizontal -> {
                val mar = a.getDimension(R.styleable.jjlayoutplus_clMarginHorizontal,0f).toInt()
                mClMargin.left = mar ; mClMargin.right = mar
            }
            R.styleable.jjlayoutplus_clMarginHorizontalPerScHeight -> {
                val mar = JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_clMarginHorizontalPerScHeight,0f))
                mClMargin.left = mar ; mClMargin.right = mar
            }
            R.styleable.jjlayoutplus_clMarginHorizontalPerScWidth -> {
                val mar = JJScreen.percentWidth(a.getFloat(R.styleable.jjlayoutplus_clMarginHorizontalPerScWidth,0f))
                mClMargin.left = mar ; mClMargin.right = mar
            }
            R.styleable.jjlayoutplus_clMarginHorizontalResponsive -> {
                val mar = responsiveSizeDimension(a, R.styleable.jjlayoutplus_clMarginHorizontalResponsive)
                mClMargin.left = mar ; mClMargin.right = mar
            }
            R.styleable.jjlayoutplus_clMarginHorizontalResPerScHeight -> {
                val mar = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_clMarginHorizontalResPerScHeight)
                mClMargin.left = mar ; mClMargin.right = mar
            }
            R.styleable.jjlayoutplus_clMarginHorizontalResPerScWidth -> {
                val mar = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_clMarginHorizontalResPerScWidth)
                mClMargin.left = mar ; mClMargin.right = mar
            }

        }
    }

    private fun setupMarginLpl(a: TypedArray, index:Int) {
        when (a.getIndex(index)) {
            R.styleable.jjlayoutplus_lplMargin -> {
                mlsMargins =
                    JJMargin.all(a.getDimension(R.styleable.jjlayoutplus_lplMargin, 0f).toInt())
            }
            R.styleable.jjlayoutplus_lplMarginVertical -> {
                val mar = a.getDimension(R.styleable.jjlayoutplus_lplMarginVertical, 0f).toInt()
                mlsMargins.top = mar; mlsMargins.bottom = mar
            }
            R.styleable.jjlayoutplus_lplMarginHorizontal -> {
                val mar =
                    a.getDimension(R.styleable.jjlayoutplus_lplMarginHorizontal, 0f).toInt()
                mlsMargins.left = mar; mlsMargins.right = mar
            }

            R.styleable.jjlayoutplus_lplMarginStart -> {
                mlsMargins.left =
                    a.getDimension(R.styleable.jjlayoutplus_lplMarginStart, 0f).toInt()
            }
            R.styleable.jjlayoutplus_lplMarginEnd -> {
                mlsMargins.right =
                    a.getDimension(R.styleable.jjlayoutplus_lplMarginEnd, 0f).toInt()
            }
            R.styleable.jjlayoutplus_lplMarginBottom -> {
                mlsMargins.bottom =
                    a.getDimension(R.styleable.jjlayoutplus_lplMarginBottom, 0f).toInt()
            }
            R.styleable.jjlayoutplus_lplMarginTop -> {
                mlsMargins.top =
                    a.getDimension(R.styleable.jjlayoutplus_lplMarginTop, 0f).toInt()
            }

            R.styleable.jjlayoutplus_lplMarginLeftPerScHeight -> {
                mlsMargins.left = JJScreen.percentHeight(
                    a.getFloat(
                        R.styleable.jjlayoutplus_lplMarginLeftPerScHeight,
                        0f
                    )
                )
            }
            R.styleable.jjlayoutplus_lplMarginRightPerScHeight -> {
                mlsMargins.right = JJScreen.percentHeight(
                    a.getFloat(
                        R.styleable.jjlayoutplus_lplMarginRightPerScHeight,
                        0f
                    )
                )
            }
            R.styleable.jjlayoutplus_lplMarginBottomPerScHeight -> {
                mlsMargins.bottom = JJScreen.percentHeight(
                    a.getFloat(
                        R.styleable.jjlayoutplus_lplMarginBottomPerScHeight,
                        0f
                    )
                )
            }
            R.styleable.jjlayoutplus_lplMarginTopPerScHeight -> {
                mlsMargins.top = JJScreen.percentHeight(
                    a.getFloat(
                        R.styleable.jjlayoutplus_lplMarginTopPerScHeight,
                        0f
                    )
                )
            }

            R.styleable.jjlayoutplus_lplMarginLeftPerScWidth -> {
                mlsMargins.left = JJScreen.percentWidth(
                    a.getFloat(
                        R.styleable.jjlayoutplus_lplMarginLeftPerScWidth,
                        0f
                    )
                )
            }
            R.styleable.jjlayoutplus_lplMarginRightPerScWidth -> {
                mlsMargins.right = JJScreen.percentWidth(
                    a.getFloat(
                        R.styleable.jjlayoutplus_lplMarginRightPerScWidth,
                        0f
                    )
                )
            }
            R.styleable.jjlayoutplus_lplMarginBottomPerScWidth -> {
                mlsMargins.bottom = JJScreen.percentWidth(
                    a.getFloat(
                        R.styleable.jjlayoutplus_lplMarginBottomPerScWidth,
                        0f
                    )
                )
            }
            R.styleable.jjlayoutplus_lplMarginTopPerScWidth -> {
                mlsMargins.top = JJScreen.percentWidth(
                    a.getFloat(
                        R.styleable.jjlayoutplus_lplMarginTopPerScWidth,
                        0f
                    )
                )
            }

            R.styleable.jjlayoutplus_lplMarginTopResponsive -> {
                mlsMargins.top =
                    responsiveSizeDimension(a, R.styleable.jjlayoutplus_lplMarginTopResponsive)
            }
            R.styleable.jjlayoutplus_lplMarginLeftResponsive -> {
                mlsMargins.left =
                    responsiveSizeDimension(a, R.styleable.jjlayoutplus_lplMarginLeftResponsive)
            }
            R.styleable.jjlayoutplus_lplMarginRightResponsive -> {
                mlsMargins.right = responsiveSizeDimension(
                    a,
                    R.styleable.jjlayoutplus_lplMarginRightResponsive
                )
            }
            R.styleable.jjlayoutplus_lplMarginBottomResponsive -> {
                mlsMargins.bottom = responsiveSizeDimension(
                    a,
                    R.styleable.jjlayoutplus_lplMarginBottomResponsive
                )
            }

            R.styleable.jjlayoutplus_lplMarginTopResPerScWidth -> {
                mlsMargins.top = responsiveSizePercentScreenWidth(
                    a,
                    R.styleable.jjlayoutplus_lplMarginTopResPerScWidth
                )
            }
            R.styleable.jjlayoutplus_lplMarginLeftResPerScWidth -> {
                mlsMargins.left = responsiveSizePercentScreenWidth(
                    a,
                    R.styleable.jjlayoutplus_lplMarginLeftResPerScWidth
                )
            }
            R.styleable.jjlayoutplus_lplMarginRightResPerScWidth -> {
                mlsMargins.right = responsiveSizePercentScreenWidth(
                    a,
                    R.styleable.jjlayoutplus_lplMarginRightResPerScWidth
                )
            }
            R.styleable.jjlayoutplus_lplMarginBottomResPerScWidth -> {
                mlsMargins.bottom = responsiveSizePercentScreenWidth(
                    a,
                    R.styleable.jjlayoutplus_lplMarginBottomResPerScWidth
                )
            }

            R.styleable.jjlayoutplus_lplMarginTopResPerScHeight -> {
                mlsMargins.top = responsiveSizePercentScreenHeight(
                    a,
                    R.styleable.jjlayoutplus_lplMarginTopResPerScHeight
                )
            }
            R.styleable.jjlayoutplus_lplMarginLeftResPerScHeight -> {
                mlsMargins.left = responsiveSizePercentScreenHeight(
                    a,
                    R.styleable.jjlayoutplus_lplMarginLeftResPerScHeight
                )
            }
            R.styleable.jjlayoutplus_lplMarginRightResPerScHeight -> {
                mlsMargins.right = responsiveSizePercentScreenHeight(
                    a,
                    R.styleable.jjlayoutplus_lplMarginRightResPerScHeight
                )
            }
            R.styleable.jjlayoutplus_lplMarginBottomResPerScHeight -> {
                mlsMargins.bottom = responsiveSizePercentScreenHeight(
                    a,
                    R.styleable.jjlayoutplus_lplMarginBottomResPerScHeight
                )
            }
            R.styleable.jjlayoutplus_lplMarginPercentScHeight -> {
                mlsMargins = JJMargin.all(
                    JJScreen.percentHeight(
                        a.getFloat(
                            R.styleable.jjlayoutplus_lplMarginPercentScHeight,
                            0f
                        )
                    )
                )
            }
            R.styleable.jjlayoutplus_lplMarginPercentScWidth -> {
                mlsMargins = JJMargin.all(
                    JJScreen.percentWidth(
                        a.getFloat(
                            R.styleable.jjlayoutplus_lplMarginPercentScWidth,
                            0f
                        )
                    )
                )
            }
            R.styleable.jjlayoutplus_lplMarginResponsive -> {
                mlsMargins = JJMargin.all(
                    responsiveSizeDimension(
                        a,
                        R.styleable.jjlayoutplus_lplMarginResponsive
                    )
                )
            }
            R.styleable.jjlayoutplus_lplMarginResPerScHeight -> {
                mlsMargins = JJMargin.all(
                    responsiveSizePercentScreenHeight(
                        a,
                        R.styleable.jjlayoutplus_lplMarginResPerScHeight
                    )
                )
            }
            R.styleable.jjlayoutplus_lplMarginResPerScWidth -> {
                mlsMargins = JJMargin.all(
                    responsiveSizePercentScreenWidth(
                        a,
                        R.styleable.jjlayoutplus_lplMarginResPerScWidth
                    )
                )
            }

            R.styleable.jjlayoutplus_lplMarginVerticalPerScHeight -> {
                val mar = JJScreen.percentHeight(
                    a.getFloat(
                        R.styleable.jjlayoutplus_lplMarginVerticalPerScHeight,
                        0f
                    )
                )
                mlsMargins.top = mar; mlsMargins.bottom = mar
            }
            R.styleable.jjlayoutplus_lplMarginVerticalPerScWidth -> {
                val mar = JJScreen.percentWidth(
                    a.getFloat(
                        R.styleable.jjlayoutplus_lplMarginVerticalPerScWidth,
                        0f
                    )
                )
                mlsMargins.top = mar; mlsMargins.bottom = mar
            }
            R.styleable.jjlayoutplus_lplMarginVerticalResponsive -> {
                val mar = responsiveSizeDimension(
                    a,
                    R.styleable.jjlayoutplus_lplMarginVerticalResponsive
                )
                mlsMargins.top = mar; mlsMargins.bottom = mar
            }
            R.styleable.jjlayoutplus_lplMarginVerticalResPerScWidth -> {
                val mar = responsiveSizePercentScreenWidth(
                    a,
                    R.styleable.jjlayoutplus_lplMarginVerticalResPerScWidth
                )
                mlsMargins.top = mar; mlsMargins.bottom = mar
            }
            R.styleable.jjlayoutplus_lplMarginVerticalResPerScHeight -> {
                val mar = responsiveSizePercentScreenHeight(
                    a,
                    R.styleable.jjlayoutplus_lplMarginVerticalResPerScHeight
                )
                mlsMargins.top = mar; mlsMargins.bottom = mar
            }


            R.styleable.jjlayoutplus_lplMarginHorizontalPerScHeight -> {
                val mar = JJScreen.percentHeight(
                    a.getFloat(
                        R.styleable.jjlayoutplus_lplMarginHorizontalPerScHeight,
                        0f
                    )
                )
                mlsMargins.left = mar; mlsMargins.right = mar
            }
            R.styleable.jjlayoutplus_lplMarginHorizontalPerScWidth -> {
                val mar = JJScreen.percentWidth(
                    a.getFloat(
                        R.styleable.jjlayoutplus_lplMarginHorizontalPerScWidth,
                        0f
                    )
                )
                mlsMargins.left = mar; mlsMargins.right = mar
            }
            R.styleable.jjlayoutplus_lplMarginHorizontalResponsive -> {
                val mar = responsiveSizeDimension(
                    a,
                    R.styleable.jjlayoutplus_lplMarginHorizontalResponsive
                )
                mlsMargins.left = mar; mlsMargins.right = mar
            }
            R.styleable.jjlayoutplus_lplMarginHorizontalResPerScWidth -> {
                val mar = responsiveSizePercentScreenWidth(
                    a,
                    R.styleable.jjlayoutplus_lplMarginHorizontalResPerScWidth
                )
                mlsMargins.left = mar; mlsMargins.right = mar
            }
            R.styleable.jjlayoutplus_lplMarginHorizontalResPerScHeight -> {
                val mar = responsiveSizePercentScreenHeight(
                    a,
                    R.styleable.jjlayoutplus_lplMarginHorizontalResPerScHeight
                )
                mlsMargins.left = mar; mlsMargins.right = mar
            }

        }
    }
    private fun setupPaddingLpl(a: TypedArray, index: Int){
        when(a.getIndex(index)){
            R.styleable.jjlayoutplus_lplPadding -> {
                mlsPadding = JJPadding.all( a.getDimension(R.styleable.jjlayoutplus_lplPadding,0f).toInt())
            }
            R.styleable.jjlayoutplus_lplPaddingVertical -> {
                val mar = a.getDimension(R.styleable.jjlayoutplus_lplPaddingVertical,0f).toInt()
                mlsPadding.top = mar ; mlsPadding.bottom = mar
            }
            R.styleable.jjlayoutplus_lplPaddingHorizontal -> {
                val mar = a.getDimension(R.styleable.jjlayoutplus_lplPaddingHorizontal,0f).toInt()
                mlsPadding.left = mar ; mlsPadding.right = mar
            }
            R.styleable.jjlayoutplus_lplPaddingStart -> {
                mlsPadding.left = a.getDimension(R.styleable.jjlayoutplus_lplPaddingStart,0f).toInt()
            }
            R.styleable.jjlayoutplus_lplPaddingEnd -> {
                mlsPadding.right = a.getDimension(R.styleable.jjlayoutplus_lplPaddingEnd,0f).toInt()
            }
            R.styleable.jjlayoutplus_lplPaddingTop -> {
                mlsPadding.top = a.getDimension(R.styleable.jjlayoutplus_lplPaddingTop,0f).toInt()
            }
            R.styleable.jjlayoutplus_lplPaddingBottom -> {
                mlsPadding.bottom = a.getDimension(R.styleable.jjlayoutplus_lplPaddingBottom,0f).toInt()
            }

            R.styleable.jjlayoutplus_lplPaddingTopPerScHeight -> {
                mlsPadding.top = JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_lplPaddingTopPerScHeight,0f))
            }
            R.styleable.jjlayoutplus_lplPaddingLeftPerScHeight -> {
                mlsPadding.left = JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_lplPaddingLeftPerScHeight,0f))
            }
            R.styleable.jjlayoutplus_lplPaddingRightPerScHeight -> {
                mlsPadding.right = JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_lplPaddingRightPerScHeight,0f))
            }
            R.styleable.jjlayoutplus_lplPaddingBottomPerScHeight -> {
                mlsPadding.bottom = JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_lplPaddingBottomPerScHeight,0f))
            }

            R.styleable.jjlayoutplus_lplPaddingTopPerScWidth -> {
                mlsPadding.top = JJScreen.percentWidth(a.getFloat(R.styleable.jjlayoutplus_lplPaddingTopPerScWidth,0f))
            }
            R.styleable.jjlayoutplus_lplPaddingLeftPerScWidth -> {
                mlsPadding.left = JJScreen.percentWidth(a.getFloat(R.styleable.jjlayoutplus_lplPaddingLeftPerScWidth,0f))
            }
            R.styleable.jjlayoutplus_lplPaddingRightPerScWidth -> {
                mlsPadding.right = JJScreen.percentWidth(a.getFloat(R.styleable.jjlayoutplus_lplPaddingRightPerScWidth,0f))
            }
            R.styleable.jjlayoutplus_lplPaddingBottomPerScWidth -> {
                mlsPadding.bottom = JJScreen.percentWidth(a.getFloat(R.styleable.jjlayoutplus_lplPaddingBottomPerScWidth,0f))
            }

            R.styleable.jjlayoutplus_lplPaddingTopResponsive -> {
                mlsPadding.top = responsiveSizeDimension(a, R.styleable.jjlayoutplus_lplPaddingTopResponsive)
            }
            R.styleable.jjlayoutplus_lplPaddingLeftResponsive -> {
                mlsPadding.left = responsiveSizeDimension(a, R.styleable.jjlayoutplus_lplPaddingLeftResponsive)
            }
            R.styleable.jjlayoutplus_lplPaddingRightResponsive -> {
                mlsPadding.right = responsiveSizeDimension(a, R.styleable.jjlayoutplus_lplPaddingRightResponsive)
            }
            R.styleable.jjlayoutplus_lplPaddingBottomResponsive -> {
                mlsPadding.bottom = responsiveSizeDimension(a, R.styleable.jjlayoutplus_lplPaddingBottomResponsive)
            }

            R.styleable.jjlayoutplus_lplPaddingTopResPerScWidth -> {
                mlsPadding.top = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_lplPaddingTopResPerScWidth)
            }
            R.styleable.jjlayoutplus_lplPaddingLeftResPerScWidth -> {
                mlsPadding.left = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_lplPaddingLeftResPerScWidth)
            }
            R.styleable.jjlayoutplus_lplPaddingRightResPerScWidth -> {
                mlsPadding.right = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_lplPaddingRightResPerScWidth)
            }
            R.styleable.jjlayoutplus_lplPaddingBottomResPerScWidth -> {
                mlsPadding.bottom = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_lplPaddingBottomResPerScWidth)
            }

            R.styleable.jjlayoutplus_lplPaddingTopResPerScHeight -> {
                mlsPadding.top = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_lplPaddingTopResPerScHeight)
            }
            R.styleable.jjlayoutplus_lplPaddingLeftResPerScHeight -> {
                mlsPadding.left = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_lplPaddingLeftResPerScHeight)
            }
            R.styleable.jjlayoutplus_lplPaddingRightResPerScHeight -> {
                mlsPadding.right = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_lplPaddingRightResPerScHeight)
            }
            R.styleable.jjlayoutplus_lplPaddingBottomResPerScHeight -> {
                mlsPadding.bottom = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_lplPaddingBottomResPerScHeight)
            }
            R.styleable.jjlayoutplus_lplPaddingPercentScHeight->{
                mlsPadding = JJPadding.all(JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_lplPaddingPercentScHeight,0f)))
            }
            R.styleable.jjlayoutplus_lplPaddingPercentScWidth->{
                mlsPadding = JJPadding.all(JJScreen.percentWidth(a.getFloat(R.styleable.jjlayoutplus_lplPaddingPercentScWidth,0f)))
            }
            R.styleable.jjlayoutplus_lplPaddingResponsive->{
                mlsPadding = JJPadding.all(responsiveSizeDimension(a, R.styleable.jjlayoutplus_lplPaddingResponsive))
            }
            R.styleable.jjlayoutplus_lplPaddingResPerScHeight->{
                mlsPadding = JJPadding.all(responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_lplPaddingResPerScHeight))
            }
            R.styleable.jjlayoutplus_lplPaddingResPerScWidth->{
                mlsPadding = JJPadding.all(responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_lplPaddingResPerScWidth))
            }

            R.styleable.jjlayoutplus_lplPaddingVerticalPerScHeight -> {
                val mar = JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_lplPaddingVerticalPerScHeight,0f))
                mlsPadding.top = mar ; mlsPadding.bottom = mar
            }
            R.styleable.jjlayoutplus_lplPaddingVerticalPerScWidth -> {
                val mar = JJScreen.percentWidth(a.getFloat(R.styleable.jjlayoutplus_lplPaddingVerticalPerScWidth,0f))
                mlsPadding.top = mar ; mlsPadding.bottom = mar
            }
            R.styleable.jjlayoutplus_lplPaddingVerticalResponsive -> {
                val mar = responsiveSizeDimension(a, R.styleable.jjlayoutplus_lplPaddingVerticalResponsive)
                mlsPadding.top = mar ; mlsPadding.bottom = mar
            }
            R.styleable.jjlayoutplus_lplPaddingVerticalResPerScWidth -> {
                val mar = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_lplPaddingVerticalResPerScWidth)
                mlsPadding.top = mar ; mlsPadding.bottom = mar
            }
            R.styleable.jjlayoutplus_lplPaddingVerticalResPerScHeight -> {
                val mar = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_lplPaddingVerticalResPerScHeight)
                mlsPadding.top = mar ; mlsPadding.bottom = mar
            }

            R.styleable.jjlayoutplus_lplPaddingHorizontalPerScHeight -> {
                val mar = JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_lplPaddingHorizontalPerScHeight,0f))
                mlsPadding.left = mar ; mlsPadding.right = mar
            }
            R.styleable.jjlayoutplus_lplPaddingHorizontalPerScWidth -> {
                val mar = JJScreen.percentWidth(a.getFloat(R.styleable.jjlayoutplus_lplPaddingHorizontalPerScWidth,0f))
                mlsPadding.left = mar ; mlsPadding.right = mar
            }
            R.styleable.jjlayoutplus_lplPaddingHorizontalResponsive -> {
                val mar = responsiveSizeDimension(a, R.styleable.jjlayoutplus_lplPaddingHorizontalResponsive)
                mlsPadding.left = mar ; mlsPadding.right = mar
            }
            R.styleable.jjlayoutplus_lplPaddingHorizontalResPerScWidth -> {
                val mar = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_lplPaddingHorizontalResPerScWidth)
                mlsPadding.left = mar ; mlsPadding.right = mar
            }
            R.styleable.jjlayoutplus_lplPaddingHorizontalResPerScHeight -> {
                val mar = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_lplPaddingHorizontalResPerScHeight)
                mlsPadding.left = mar ; mlsPadding.right = mar
            }
        }
    }
    private fun setupSizeLpl(a: TypedArray, index:Int){
        when (a.getIndex(index)) {
            R.styleable.jjlayoutplus_layout_height_landscape -> {
                mlsHeight = a.getLayoutDimension(R.styleable.jjlayoutplus_layout_height_landscape,0)
            }
            R.styleable.jjlayoutplus_layout_width_landscape -> {
                mlsWidth = a.getLayoutDimension(R.styleable.jjlayoutplus_layout_width_landscape,0)
            }
            R.styleable.jjlayoutplus_lplHeightPercentScreenWidth -> {
                mlsHeight = JJScreen.percentWidth( a.getFloat(R.styleable.jjlayoutplus_lplHeightPercentScreenWidth,0f))
            }
            R.styleable.jjlayoutplus_lplWidthPercentScreenWidth -> {
                mlsWidth = JJScreen.percentWidth( a.getFloat(R.styleable.jjlayoutplus_lplWidthPercentScreenWidth,0f))
            }

            R.styleable.jjlayoutplus_lplHeightPercentScreenHeight -> {
                mlsHeight = JJScreen.percentHeight( a.getFloat(R.styleable.jjlayoutplus_lplHeightPercentScreenHeight,0f))
            }
            R.styleable.jjlayoutplus_lplWidthPercentScreenHeight -> {
                mlsWidth = JJScreen.percentHeight( a.getFloat(R.styleable.jjlayoutplus_lplWidthPercentScreenHeight,0f))
            }
            R.styleable.jjlayoutplus_lplHeightResponsive -> {
                mlsHeight = responsiveSizeDimension(a, R.styleable.jjlayoutplus_lplHeightResponsive)
            }
            R.styleable.jjlayoutplus_lplWidthResponsive -> {
                mlsWidth = responsiveSizeDimension(a, R.styleable.jjlayoutplus_lplWidthResponsive)
            }
            R.styleable.jjlayoutplus_lplHeightResponsivePercentScreenHeight -> {
                mlsHeight = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_lplHeightResponsivePercentScreenHeight)
            }
            R.styleable.jjlayoutplus_lplWidthResponsivePercentScreenHeight -> {
                mlsWidth = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_lplWidthResponsivePercentScreenHeight)
            }
            R.styleable.jjlayoutplus_lplHeightResponsivePercentScreenWidth -> {
                mlsHeight = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_lplHeightResponsivePercentScreenWidth)
            }
            R.styleable.jjlayoutplus_lplWidthResponsivePercentScreenWidth -> {
                mlsWidth = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_lplWidthResponsivePercentScreenWidth)
            }

        }
    }

    private fun setupMarginCll(a: TypedArray, index:Int){
        when (a.getIndex(index)) {
            R.styleable.jjlayoutplus_cllMarginEnd -> {
                mCllMargin.right = a.getDimension(R.styleable.jjlayoutplus_cllMarginEnd,0f).toInt()
            }
            R.styleable.jjlayoutplus_cllMarginStart -> {
                mCllMargin.left = a.getDimension(R.styleable.jjlayoutplus_cllMarginStart,0f).toInt()
            }
            R.styleable.jjlayoutplus_cllMarginTop -> {
                mCllMargin.top = a.getDimension(R.styleable.jjlayoutplus_cllMarginTop,0f).toInt()
            }
            R.styleable.jjlayoutplus_cllMarginBottom -> {
                mCllMargin.bottom = a.getDimension(R.styleable.jjlayoutplus_cllMarginBottom,0f).toInt()
            }

            R.styleable.jjlayoutplus_cllMarginEndPercentScreenHeight -> {
                mCllMargin.right = JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_cllMarginEndPercentScreenHeight,0f))
            }
            R.styleable.jjlayoutplus_cllMarginStartPercentScreenHeight -> {
                mCllMargin.left = JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_cllMarginStartPercentScreenHeight,0f))
            }
            R.styleable.jjlayoutplus_cllMarginTopPercentScreenHeight -> {
                mCllMargin.top = JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_cllMarginTopPercentScreenHeight,0f))
            }
            R.styleable.jjlayoutplus_cllMarginBottomPercentScreenHeight -> {
                mCllMargin.bottom = JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_cllMarginBottomPercentScreenHeight,0f))
            }

            R.styleable.jjlayoutplus_cllMarginEndPercentScreenWidth -> {
                mCllMargin.right = JJScreen.percentWidth(a.getFloat(R.styleable.jjlayoutplus_cllMarginEndPercentScreenWidth,0f))
            }
            R.styleable.jjlayoutplus_cllMarginStartPercentScreenWidth -> {
                mCllMargin.left = JJScreen.percentWidth(a.getFloat(R.styleable.jjlayoutplus_cllMarginStartPercentScreenWidth,0f))
            }
            R.styleable.jjlayoutplus_cllMarginTopPercentScreenWidth -> {
                mCllMargin.top = JJScreen.percentWidth(a.getFloat(R.styleable.jjlayoutplus_cllMarginTopPercentScreenWidth,0f))
            }
            R.styleable.jjlayoutplus_cllMarginBottomPercentScreenWidth -> {
                mCllMargin.bottom = JJScreen.percentWidth(a.getFloat(R.styleable.jjlayoutplus_cllMarginBottomPercentScreenWidth,0f))
            }

            R.styleable.jjlayoutplus_cllMargin -> {
                mCllMargin = JJMargin.all(a.getDimension(R.styleable.jjlayoutplus_cllMargin,0f).toInt())
            }
            R.styleable.jjlayoutplus_cllMarginPerScHeight -> {
                mCllMargin = JJMargin.all(JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_cllMarginPerScHeight,0f)))
            }
            R.styleable.jjlayoutplus_cllMarginPerScWidth -> {
                mCllMargin = JJMargin.all(JJScreen.percentWidth(a.getFloat(R.styleable.jjlayoutplus_cllMarginPerScWidth,0f)))
            }
            R.styleable.jjlayoutplus_cllMarginResponsive -> {
                mCllMargin = JJMargin.all(responsiveSizeDimension(a, R.styleable.jjlayoutplus_cllMarginResponsive))
            }
            R.styleable.jjlayoutplus_cllMarginResPerScHeight -> {
                mCllMargin = JJMargin.all(responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_cllMarginResPerScHeight))
            }
            R.styleable.jjlayoutplus_cllMarginResPerScWidth -> {
                mCllMargin = JJMargin.all(responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_cllMarginResPerScWidth))
            }
            R.styleable.jjlayoutplus_cllMarginEndResponsive ->{
                mCllMargin.right = responsiveSizeDimension(a, R.styleable.jjlayoutplus_cllMarginEndResponsive)
            }
            R.styleable.jjlayoutplus_cllMarginStartResponsive ->{
                mCllMargin.left = responsiveSizeDimension(a, R.styleable.jjlayoutplus_cllMarginStartResponsive)
            }
            R.styleable.jjlayoutplus_cllMarginTopResponsive ->{
                mCllMargin.top = responsiveSizeDimension(a, R.styleable.jjlayoutplus_cllMarginTopResponsive)
            }
            R.styleable.jjlayoutplus_cllMarginBottomResponsive ->{
                mCllMargin.bottom = responsiveSizeDimension(a, R.styleable.jjlayoutplus_cllMarginBottomResponsive)
            }

            R.styleable.jjlayoutplus_cllMarginEndResPerScHeight ->{
                mCllMargin.right = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_cllMarginEndResPerScHeight)
            }
            R.styleable.jjlayoutplus_cllMarginStartResPerScHeight ->{
                mCllMargin.left = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_cllMarginStartResPerScHeight)
            }
            R.styleable.jjlayoutplus_cllMarginTopResPerScHeight ->{
                mCllMargin.top = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_cllMarginTopResPerScHeight)
            }
            R.styleable.jjlayoutplus_cllMarginBottomResPerScHeight ->{
                mCllMargin.bottom = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_cllMarginBottomResPerScHeight)
            }

            R.styleable.jjlayoutplus_cllMarginEndResPerScWidth ->{
                mCllMargin.right = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_cllMarginEndResPerScWidth)
            }
            R.styleable.jjlayoutplus_cllMarginStartResPerScWidth ->{
                mCllMargin.left = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_cllMarginStartResPerScWidth)
            }
            R.styleable.jjlayoutplus_cllMarginTopResPerScWidth ->{
                mCllMargin.top = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_cllMarginTopResPerScWidth)
            }
            R.styleable.jjlayoutplus_cllMarginBottomResPerScWidth ->{
                mCllMargin.bottom = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_cllMarginBottomResPerScWidth)
            }

            R.styleable.jjlayoutplus_cllMarginVertical->{
                val mar = a.getDimension(R.styleable.jjlayoutplus_cllMarginVertical,0f).toInt()
                mCllMargin.top = mar ; mCllMargin.bottom = mar
            }
            R.styleable.jjlayoutplus_cllMarginVerticalPerScHeight->{
                val mar = JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_cllMarginVerticalPerScHeight,0f))
                mCllMargin.top = mar ; mCllMargin.bottom = mar
            }
            R.styleable.jjlayoutplus_cllMarginVerticalPerScWidth->{
                val mar = JJScreen.percentWidth(a.getFloat(R.styleable.jjlayoutplus_cllMarginVerticalPerScWidth,0f))
                mCllMargin.top = mar ; mCllMargin.bottom = mar
            }
            R.styleable.jjlayoutplus_cllMarginVerticalResponsive->{
                val mar = responsiveSizeDimension(a, R.styleable.jjlayoutplus_cllMarginVerticalResponsive)
                mCllMargin.top = mar ; mCllMargin.bottom = mar
            }
            R.styleable.jjlayoutplus_cllMarginVerticalResPerScHeight->{
                val mar = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_cllMarginVerticalResPerScHeight)
                mCllMargin.top = mar ; mCllMargin.bottom = mar
            }
            R.styleable.jjlayoutplus_cllMarginVerticalResPerScWidth->{
                val mar = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_cllMarginVerticalResPerScWidth)
                mCllMargin.top = mar ; mCllMargin.bottom = mar
            }

            R.styleable.jjlayoutplus_cllMarginHorizontal->{
                val mar = a.getDimension(R.styleable.jjlayoutplus_cllMarginHorizontal,0f).toInt()
                mCllMargin.top = mar ; mCllMargin.bottom = mar
            }
            R.styleable.jjlayoutplus_cllMarginHorizontalPerScHeight->{
                val mar = JJScreen.percentHeight(a.getFloat(R.styleable.jjlayoutplus_cllMarginHorizontalPerScHeight,0f))
                mCllMargin.left = mar ; mCllMargin.right = mar
            }
            R.styleable.jjlayoutplus_cllMarginHorizontalPerScWidth->{
                val mar = JJScreen.percentWidth(a.getFloat(R.styleable.jjlayoutplus_cllMarginHorizontalPerScWidth,0f))
                mCllMargin.left = mar ; mCllMargin.right = mar
            }
            R.styleable.jjlayoutplus_cllMarginHorizontalResponsive->{
                val mar = responsiveSizeDimension(a, R.styleable.jjlayoutplus_cllMarginHorizontalResponsive)
                mCllMargin.left = mar ; mCllMargin.right = mar
            }
            R.styleable.jjlayoutplus_cllMarginHorizontalResPerScHeight->{
                val mar = responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_cllMarginHorizontalResPerScHeight)
                mCllMargin.left = mar ; mCllMargin.right = mar
            }
            R.styleable.jjlayoutplus_cllMarginHorizontalResPerScWidth->{
                val mar = responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_cllMarginHorizontalResPerScWidth)
                mCllMargin.left = mar ; mCllMargin.right = mar
            }
        }
    }
    private fun setupSizeCll(a: TypedArray, index:Int){
        when (a.getIndex(index)) {
            R.styleable.jjlayoutplus_layout_height_landscape->{
                val value = a.getLayoutDimension(R.styleable.jjlayoutplus_layout_height_landscape,0)
                if(value > 0 || value == -2 ) cllHeight(value)
            }
            R.styleable.jjlayoutplus_layout_width_landscape->{
                val value = a.getLayoutDimension(R.styleable.jjlayoutplus_layout_width_landscape,0)
                if(value > 0 || value == -2 ) cllWidth(value)
            }
            R.styleable.jjlayoutplus_cllHeightPercent -> {
                cllPercentHeight( a.getFloat(R.styleable.jjlayoutplus_cllHeightPercent,0f))
            }
            R.styleable.jjlayoutplus_cllWidthPercent -> {
                cllPercentWidth( a.getFloat(R.styleable.jjlayoutplus_cllWidthPercent,0f))
            }
            R.styleable.jjlayoutplus_cllHeightPercentScreenWidth -> {
                cllHeight(JJScreen.percentWidth( a.getFloat(R.styleable.jjlayoutplus_cllHeightPercentScreenWidth,0f)))
            }
            R.styleable.jjlayoutplus_cllWidthPercentScreenWidth -> {
                cllWidth(JJScreen.percentWidth( a.getFloat(R.styleable.jjlayoutplus_cllWidthPercentScreenWidth,0f)))
            }
            R.styleable.jjlayoutplus_cllHeightPercentScreenHeight -> {
                cllHeight(JJScreen.percentHeight( a.getFloat(R.styleable.jjlayoutplus_cllHeightPercentScreenHeight,0f)))
            }
            R.styleable.jjlayoutplus_cllWidthPercentScreenHeight -> {
                cllWidth(JJScreen.percentHeight( a.getFloat(R.styleable.jjlayoutplus_cllWidthPercentScreenHeight,0f)))
            }
            R.styleable.jjlayoutplus_cllHeightResponsive -> {
                cllHeight(responsiveSizeDimension(a, R.styleable.jjlayoutplus_cllHeightResponsive))
            }
            R.styleable.jjlayoutplus_cllWidthResponsive -> {
                cllWidth(responsiveSizeDimension(a, R.styleable.jjlayoutplus_cllWidthResponsive))
            }

            R.styleable.jjlayoutplus_cllHeightResponsivePercentScreenHeight -> {
                cllHeight(responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_cllHeightResponsivePercentScreenHeight))
            }
            R.styleable.jjlayoutplus_cllWidthResponsivePercentScreenHeight -> {
                cllWidth(responsiveSizePercentScreenHeight(a, R.styleable.jjlayoutplus_cllWidthResponsivePercentScreenHeight))
            }
            R.styleable.jjlayoutplus_cllWidthResponsivePercentScreenWidth -> {
                cllHeight(responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_cllWidthResponsivePercentScreenWidth))
            }
            R.styleable.jjlayoutplus_cllHeightResponsivePercentScreenWidth -> {
                cllWidth(responsiveSizePercentScreenWidth(a, R.styleable.jjlayoutplus_cllHeightResponsivePercentScreenWidth))
            }
        }

    }
    private fun setupAnchorsCll(a: TypedArray, index:Int){
        when (a.getIndex(index)) {
            R.styleable.jjlayoutplus_cllFillParent ->{
                if(a.getBoolean(R.styleable.jjlayoutplus_cllFillParent,false)) cllFillParent()
            }
            R.styleable.jjlayoutplus_cllFillParentHorizontally ->{
                if(a.getBoolean(R.styleable.jjlayoutplus_cllFillParentHorizontally,false)) cllFillParentHorizontally()
            }
            R.styleable.jjlayoutplus_cllFillParentVertically ->{
                if(a.getBoolean(R.styleable.jjlayoutplus_cllFillParentVertically,false)) cllFillParentVertically()
            }
            R.styleable.jjlayoutplus_cllCenterInParent ->{
                if(a.getBoolean(R.styleable.jjlayoutplus_cllCenterInParent,false)) cllCenterInParent()
            }
            R.styleable.jjlayoutplus_cllCenterInParentHorizontally ->{
                if(a.getBoolean(R.styleable.jjlayoutplus_cllCenterInParentHorizontally,false)) cllCenterInParentHorizontally()
            }
            R.styleable.jjlayoutplus_cllCenterInParentVertically ->{
                if(a.getBoolean(R.styleable.jjlayoutplus_cllCenterInParentVertically,false)) cllCenterInParentVertically()
            }
            R.styleable.jjlayoutplus_cllCenterInParentTopVertically ->{
                if(a.getBoolean(R.styleable.jjlayoutplus_cllCenterInParentTopVertically,false)) cllCenterInParentTopVertically()
            }
            R.styleable.jjlayoutplus_cllCenterInParentBottomVertically ->{
                if(a.getBoolean(R.styleable.jjlayoutplus_cllCenterInParentBottomVertically,false)) cllCenterInParentBottomVertically()
            }
            R.styleable.jjlayoutplus_cllCenterInParentStartHorizontally ->{
                if(a.getBoolean(R.styleable.jjlayoutplus_cllCenterInParentStartHorizontally,false)) cllCenterInParentStartHorizontally()
            }
            R.styleable.jjlayoutplus_cllCenterInParentEndHorizontally ->{
                if(a.getBoolean(R.styleable.jjlayoutplus_cllCenterInParentEndHorizontally,false)) cllCenterInParentEndHorizontally()
            }
            R.styleable.jjlayoutplus_cllCenterInTopVerticallyOf ->{
                cllCenterInTopVertically(a.getResourceId(
                    R.styleable.jjlayoutplus_cllCenterInTopVerticallyOf,
                    View.NO_ID))
            }
            R.styleable.jjlayoutplus_cllCenterInBottomVerticallyOf ->{
                cllCenterInBottomVertically(a.getResourceId(
                    R.styleable.jjlayoutplus_cllCenterInBottomVerticallyOf,
                    View.NO_ID))
            }
            R.styleable.jjlayoutplus_cllCenterInStartHorizontallyOf ->{
                cllCenterInStartHorizontally(a.getResourceId(
                    R.styleable.jjlayoutplus_cllCenterInStartHorizontallyOf,
                    View.NO_ID))
            }
            R.styleable.jjlayoutplus_cllCenterInEndHorizontallyOf ->{
                cllCenterInEndHorizontally(a.getResourceId(
                    R.styleable.jjlayoutplus_cllCenterInEndHorizontallyOf,
                    View.NO_ID))
            }
            R.styleable.jjlayoutplus_cllCenterVerticallyOf ->{
                cllCenterVerticallyOf(a.getResourceId(
                    R.styleable.jjlayoutplus_cllCenterVerticallyOf,
                    View.NO_ID))
            }
            R.styleable.jjlayoutplus_cllCenterHorizontallyOf ->{
                cllCenterHorizontallyOf(a.getResourceId(
                    R.styleable.jjlayoutplus_cllCenterHorizontallyOf,
                    View.NO_ID))
            }
            R.styleable.jjlayoutplus_cllVerticalBias -> {
                cllVerticalBias(a.getFloat(R.styleable.jjlayoutplus_cllVerticalBias,0.5f))
            }
            R.styleable.jjlayoutplus_cllHorizontalBias -> {
                cllHorizontalBias(a.getFloat(R.styleable.jjlayoutplus_cllHorizontalBias,0.5f))
            }

            R.styleable.jjlayoutplus_cllStartToStartParent ->{
                if(a.getBoolean(R.styleable.jjlayoutplus_cllStartToStartParent,false)) cllStartToStartParent()
            }
            R.styleable.jjlayoutplus_cllStartToEndParent ->{
                if(a.getBoolean(R.styleable.jjlayoutplus_cllStartToEndParent,false)) cllStartToEndParent()
            }
            R.styleable.jjlayoutplus_cllEndToEndParent ->{
                if(a.getBoolean(R.styleable.jjlayoutplus_cllEndToEndParent,false)) cllEndToEndParent()
            }
            R.styleable.jjlayoutplus_cllEndToStartParent ->{
                if(a.getBoolean(R.styleable.jjlayoutplus_cllEndToStartParent,false)) cllEndToStartParent()
            }
            R.styleable.jjlayoutplus_cllTopToTopParent ->{
                if(a.getBoolean(R.styleable.jjlayoutplus_cllTopToTopParent,false)) cllTopToTopParent()
            }
            R.styleable.jjlayoutplus_cllTopToBottomParent ->{
                if(a.getBoolean(R.styleable.jjlayoutplus_cllTopToBottomParent,false)) cllTopToBottomParent()
            }
            R.styleable.jjlayoutplus_cllBottomToBottomParent ->{
                if(a.getBoolean(R.styleable.jjlayoutplus_cllBottomToBottomParent,false)) cllBottomToBottomParent()
            }
            R.styleable.jjlayoutplus_cllBottomToTopParent ->{
                if(a.getBoolean(R.styleable.jjlayoutplus_cllBottomToTopParent,false)) cllBottomToTopParent()
            }

            R.styleable.jjlayoutplus_cllStartToStartOf -> {
                cllStartToStart(a.getResourceId(R.styleable.jjlayoutplus_cllStartToStartOf, View.NO_ID))
            }
            R.styleable.jjlayoutplus_cllStartToEndOf -> {
                cllStartToEnd(a.getResourceId(R.styleable.jjlayoutplus_cllStartToEndOf, View.NO_ID))
            }
            R.styleable.jjlayoutplus_cllEndToEndOf -> {
                cllEndToEnd(a.getResourceId(R.styleable.jjlayoutplus_cllEndToEndOf, View.NO_ID))
            }
            R.styleable.jjlayoutplus_cllEndToStartOf -> {
                cllEndToStart(a.getResourceId(R.styleable.jjlayoutplus_cllEndToStartOf, View.NO_ID))
            }
            R.styleable.jjlayoutplus_cllTopToTopOf -> {
                cllTopToTop(a.getResourceId(R.styleable.jjlayoutplus_cllTopToTopOf, View.NO_ID))
            }
            R.styleable.jjlayoutplus_cllTopToBottomOf -> {
                cllTopToBottom(a.getResourceId(R.styleable.jjlayoutplus_cllTopToBottomOf, View.NO_ID))
            }
            R.styleable.jjlayoutplus_cllBottomToBottomOf -> {
                cllBottomToBottom(a.getResourceId(R.styleable.jjlayoutplus_cllBottomToBottomOf, View.NO_ID))
            }
            R.styleable.jjlayoutplus_cllBottomToTopOf -> {
                cllBottomToTop(a.getResourceId(R.styleable.jjlayoutplus_cllBottomToTopOf, View.NO_ID))
            }

        }

    }

    private fun setupInitConstraint(){
        mConstraintSet.constrainWidth(id,0)
        mConstraintSet.constrainHeight(id,0)
        mConstraintSetLandScape.constrainWidth(id,0)
        mConstraintSetLandScape.constrainHeight(id,0)
        mConstraintSet.setVisibilityMode(id,ConstraintSet.VISIBILITY_MODE_IGNORE)
        mConstraintSetLandScape.setVisibilityMode(id,ConstraintSet.VISIBILITY_MODE_IGNORE)
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

    fun setSupportLandScape(support:Boolean) : JJProgressViewFS {
        mSupportLandScape = support
        return this
    }

    fun setSupportConfigurationChanged(support:Boolean) : JJProgressViewFS {
        mConfigurationChanged = support
        return this
    }

    fun addViews(vararg views: View): JJProgressViewFS {
        for (v in views) {
            addView(v)
        }
        return this
    }



    private var mIdentifier = 0
    fun setIdentifier(value: Int): JJProgressViewFS {
        mIdentifier = value
        return this
    }

    fun getIdentifier():Int{
        return mIdentifier
    }

    private var mState = 0
    fun setState(state: Int): JJProgressViewFS {
        mState = state
        return this
    }

    fun getState():Int{
        return mState
    }

    private var mAttribute = ""
    fun setAttribute(string:String): JJProgressViewFS {
        mAttribute = string
        return this
    }

    fun getAttribute(): String {
        return mAttribute
    }

    fun setPadding(padding: JJPadding): JJProgressViewFS {
        mlpPadding = padding
        setPaddingRelative(padding.left,padding.top,padding.right,padding.bottom)
        return this
    }

    fun setOnClickListenerR(listener: (view: View) -> Unit): JJProgressViewFS {
        setOnClickListener(listener)
        return this
    }

    fun setOnFocusChangeListenerR(listener: View.OnFocusChangeListener): JJProgressViewFS {
        onFocusChangeListener = listener
        return this
    }


    fun setIsFocusable(boolean: Boolean): JJProgressViewFS {
        isFocusable = boolean
        return this
    }

    fun setOnTouchListenerR(listener: View.OnTouchListener): JJProgressViewFS {
        setOnTouchListener(listener)
        return this
    }

    fun setFitsSystemWindowsR(boolean: Boolean): JJProgressViewFS {
        fitsSystemWindows = boolean
        return this
    }

    fun setBackgroundColorR(color: Int): JJProgressViewFS {
        setBackgroundColor(color)
        return this
    }

    fun setBackgroundR(drawable: Drawable?): JJProgressViewFS {
        background = drawable
        return this
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setOutlineProviderR(provider: ViewOutlineProvider): JJProgressViewFS {
        outlineProvider = provider
        return this
    }

    fun setFullScreen(): JJProgressViewFS {
        systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        return this
    }

    fun setIsFocusableInTouchMode(boolean: Boolean): JJProgressViewFS {
        isFocusableInTouchMode = boolean
        return this
    }



    fun setVisibilityR(type: Int): JJProgressViewFS {
        visibility = type
        return this
    }

    fun setMinHeightR(h:Int): JJProgressViewFS {
        minHeight = h
        return this
    }

    fun setMinWidthR(w:Int): JJProgressViewFS {
        minWidth = w
        return this
    }

    fun setMinimumHeightR(h:Int): JJProgressViewFS {
        minimumHeight = h
        return this
    }

    fun setMinimumWidthR(w:Int): JJProgressViewFS {
        minimumWidth = w
        return this
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setClipToOutlineR(boolean: Boolean) : JJProgressViewFS {
        clipToOutline = boolean
        return this
    }
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun setClipBoundsR(bounds: Rect) : JJProgressViewFS {
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


    fun setClipChildrenToPath(path: Path): JJProgressViewFS {
        mPathClipChildren = path
        mIsPathClosureClipChildren = false
        mIsClipInPathChildren = true
        mIsClipChildrenEnabled = true
        mIsClipOutPathChildren = false
        return this
    }

    fun setClipAllToPath(path: Path): JJProgressViewFS {
        mPathClipAll = path
        mIsPathClosureClipAll = false
        mIsClipInPathAll = true
        mIsClipAllEnabled = true
        mIsClipOutPathAll = false
        return this
    }


    fun setClipOutChildrenToPath(path: Path): JJProgressViewFS {
        mPathClipChildren = path
        mIsPathClosureClipChildren = false
        mIsClipOutPathChildren = true
        mIsClipChildrenEnabled = true
        mIsClipInPathChildren = false
        return this
    }


    fun setClipOutAllToPath(path: Path): JJProgressViewFS {
        mPathClipAll = path
        mIsPathClosureClipAll = false
        mIsClipOutPathAll = true
        mIsClipAllEnabled = true
        mIsClipInPathAll = false
        return this
    }

    fun setClipChildrenToPath(closure:(RectF, Path)->Unit): JJProgressViewFS {
        mIsClipInPathChildren = true
        mIsPathClosureClipChildren = true
        mIsClipOutPathChildren = false
        mIsClipChildrenEnabled = true
        mClosurePathClipChildren = closure
        return this
    }

    fun setClipAllToPath(closure:(RectF, Path, JJPadding)->Unit): JJProgressViewFS {
        mIsClipInPathAll = true
        mIsPathClosureClipAll = true
        mIsClipOutPathAll = false
        mIsClipAllEnabled = true
        mClosurePathClipAll = closure
        return this
    }

    fun setClipOutChildrenToPath(closure:(RectF, Path)->Unit): JJProgressViewFS {
        mIsClipInPathChildren = false
        mIsPathClosureClipChildren = true
        mIsClipOutPathChildren = true
        mIsClipChildrenEnabled = true
        mClosurePathClipChildren = closure
        return this
    }

    fun setClipOutAllToPath(closure:(RectF, Path, JJPadding)->Unit): JJProgressViewFS {
        mIsClipInPathAll = false
        mIsPathClosureClipAll = true
        mIsClipOutPathAll = true
        mIsClipAllEnabled = true
        mClosurePathClipAll = closure
        return this
    }

    fun disposeClipPathChildren(): JJProgressViewFS {
        mIsClipOutPathChildren = false
        mIsPathClosureClipChildren = false
        mIsClipChildrenEnabled = false
        mIsClipInPathChildren = false
        mClosurePathClipChildren = null
        return  this
    }
    fun disposeClipPathAll(): JJProgressViewFS {
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

    //region layout params

    fun lpWidth(w: Int) : JJProgressViewFS {
        mlpWidth = w
        return this
    }
    fun lpHeight(h: Int) : JJProgressViewFS {
        mlpHeight = h
        return this
    }
    fun lpPadding(pad: JJPadding) : JJProgressViewFS {
        mlpPadding = pad
        return this
    }

    fun lpMargin(mar: JJMargin) : JJProgressViewFS {
        mlpMargins = mar
        return this
    }

    //endregion

    //region layout params landscape

    fun lplWidth(w: Int) : JJProgressViewFS {
        mlsWidth = w
        return this
    }
    fun lplHeight(h: Int) : JJProgressViewFS {
        mlsHeight = h
        return this
    }
    fun lplPadding(pad: JJPadding) : JJProgressViewFS {
        mlsPadding = pad
        return this
    }

    fun lplMargin(mar: JJMargin) : JJProgressViewFS {
        mlsMargins = mar
        return this
    }

    //endregion

    //region CoordinatorLayout params

    private fun setupCol() {
        val a = layoutParams as?  CoordinatorLayout.LayoutParams
        layoutParams = a
    }

    fun colGravity(gravity: Int): JJProgressViewFS {
        setupCol()
        (layoutParams as?  CoordinatorLayout.LayoutParams)?.gravity = gravity
        return this
    }

    fun colBehavior(behavior: AppBarLayout.Behavior){
        setupCol()
        (layoutParams as?  CoordinatorLayout.LayoutParams)?.behavior = behavior
    }

    //endregion

    //region AppBarLayout Params
    private  fun setupAblp(){
        val a = layoutParams as? AppBarLayout.LayoutParams
        layoutParams = a
    }

    fun ablScrollFlags(flags: Int) : JJProgressViewFS {
        setupAblp()
        (layoutParams as? AppBarLayout.LayoutParams)?.scrollFlags = flags
        return this
    }

    fun ablScrollInterpolator(interpolator: Interpolator) : JJProgressViewFS {
        setupAblp()
        (layoutParams as? AppBarLayout.LayoutParams)?.scrollInterpolator = interpolator
        return this
    }

    //endregion

    //region RelativeLayout Params

    private fun setupRlp(){
        val a = layoutParams as? RelativeLayout.LayoutParams
        layoutParams = a
    }

    fun rlAbove(viewId: Int): JJProgressViewFS {
        setupRlp()
        (layoutParams as? RelativeLayout.LayoutParams)?.addRule(RelativeLayout.ABOVE,viewId)
        return this
    }

    fun rlBelow(viewId: Int): JJProgressViewFS {
        setupRlp()
        (layoutParams as? RelativeLayout.LayoutParams)?.addRule(RelativeLayout.BELOW,viewId)
        return this
    }

    fun rlAlignParentBottom(value : Boolean = true): JJProgressViewFS {
        setupRlp()
        val data = if(value) 1 else 0
        (layoutParams as? RelativeLayout.LayoutParams)?.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,data)
        return this
    }

    fun rlAlignParentTop(value : Boolean = true): JJProgressViewFS {
        setupRlp()
        val data = if(value) 1 else 0
        (layoutParams as? RelativeLayout.LayoutParams)?.addRule(RelativeLayout.ALIGN_PARENT_TOP,data)
        return this
    }

    fun rlAlignParentStart(value : Boolean = true): JJProgressViewFS {
        setupRlp()
        val data = if(value) 1 else 0
        (layoutParams as? RelativeLayout.LayoutParams)?.addRule(RelativeLayout.ALIGN_PARENT_START,data)
        return this
    }

    fun rlAlignParentEnd(value : Boolean = true): JJProgressViewFS {
        setupRlp()
        val data = if(value) 1 else 0
        (layoutParams as? RelativeLayout.LayoutParams)?.addRule(RelativeLayout.ALIGN_PARENT_END,data)
        return this
    }

    fun rlAlignParentLeft(value : Boolean = true): JJProgressViewFS {
        setupRlp()
        val data = if(value) 1 else 0
        (layoutParams as? RelativeLayout.LayoutParams)?.addRule(RelativeLayout.ALIGN_PARENT_LEFT,data)
        return this
    }

    fun rlAlignParentRight(value : Boolean = true): JJProgressViewFS {
        setupRlp()
        val data = if(value) 1 else 0
        (layoutParams as? RelativeLayout.LayoutParams)?.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,data)
        return this
    }

    fun rlAlignEnd(viewId: Int): JJProgressViewFS {
        setupRlp()
        (layoutParams as? RelativeLayout.LayoutParams)?.addRule(RelativeLayout.ALIGN_END,viewId)
        return this
    }

    fun rlAlignStart(viewId: Int): JJProgressViewFS {
        setupRlp()
        (layoutParams as? RelativeLayout.LayoutParams)?.addRule(RelativeLayout.ALIGN_START,viewId)
        return this
    }

    fun rlAlignTop(viewId: Int): JJProgressViewFS {
        setupRlp()
        (layoutParams as? RelativeLayout.LayoutParams)?.addRule(RelativeLayout.ALIGN_TOP,viewId)
        return this
    }

    fun rlAlignBottom(viewId: Int): JJProgressViewFS {
        setupRlp()
        (layoutParams as? RelativeLayout.LayoutParams)?.addRule(RelativeLayout.ALIGN_BOTTOM,viewId)
        return this
    }


    fun rlAlignLeft(viewId: Int): JJProgressViewFS {
        setupRlp()
        (layoutParams as? RelativeLayout.LayoutParams)?.addRule(RelativeLayout.ALIGN_LEFT,viewId)
        return this
    }

    fun rlAlignRight(viewId: Int): JJProgressViewFS {
        setupRlp()
        (layoutParams as? RelativeLayout.LayoutParams)?.addRule(RelativeLayout.ALIGN_RIGHT,viewId)
        return this
    }

    fun rlRightToLeft(viewId: Int): JJProgressViewFS {
        setupRlp()
        (layoutParams as? RelativeLayout.LayoutParams)?.addRule(RelativeLayout.LEFT_OF,viewId)
        return this
    }

    fun rlLeftToRight(viewId: Int): JJProgressViewFS {
        setupRlp()
        (layoutParams as? RelativeLayout.LayoutParams)?.addRule(RelativeLayout.RIGHT_OF,viewId)
        return this
    }

    fun rlStartToEnd(viewId: Int): JJProgressViewFS {
        setupRlp()
        (layoutParams as? RelativeLayout.LayoutParams)?.addRule(RelativeLayout.END_OF,viewId)
        return this
    }

    fun rlEndToStart(viewId: Int): JJProgressViewFS {
        setupRlp()
        (layoutParams as? RelativeLayout.LayoutParams)?.addRule(RelativeLayout.START_OF,viewId)
        return this
    }

    fun rlCenterInParent(value:Boolean = true): JJProgressViewFS {
        setupRlp()
        val data = if(value) 1 else 0
        (layoutParams as? RelativeLayout.LayoutParams)?.addRule(RelativeLayout.CENTER_IN_PARENT,data)
        return this
    }

    fun rlCenterInParentVertically(value:Boolean = true): JJProgressViewFS {
        setupRlp()
        val data = if(value) 1 else 0
        (layoutParams as? RelativeLayout.LayoutParams)?.addRule(RelativeLayout.CENTER_VERTICAL,data)
        return this
    }

    fun rlCenterInParentHorizontally(value:Boolean = true): JJProgressViewFS {
        setupRlp()
        val data = if(value) 1 else 0
        (layoutParams as? RelativeLayout.LayoutParams)?.addRule(RelativeLayout.CENTER_HORIZONTAL,data)
        return this
    }

    fun rlAlignBaseline(viewId: Int): JJProgressViewFS {
        setupRlp()
        (layoutParams as? RelativeLayout.LayoutParams)?.addRule(RelativeLayout.ALIGN_BASELINE,viewId)
        return this
    }


    //endregion

    //region LinearLayout Params
    private fun setupLlp() {
        val a = layoutParams as? LinearLayout.LayoutParams
        layoutParams = a
    }
    fun llWeight(w: Float): JJProgressViewFS {
        setupLlp()
        (layoutParams as? LinearLayout.LayoutParams)?.weight = w
        return this
    }
    fun llGravity(gravity: Int): JJProgressViewFS {
        setupLlp()
        (layoutParams as? LinearLayout.LayoutParams)?.gravity = gravity
        return this
    }

    //endregion

    //region MotionLayout Params

    private var mMotionConstraintSet: ConstraintSet? = null


    fun mlVisibilityMode(visibility: Int): JJProgressViewFS {
        mMotionConstraintSet?.setVisibilityMode(id, visibility)
        return this
    }

    fun mlVerticalBias(float: Float): JJProgressViewFS {
        mMotionConstraintSet?.setVerticalBias(id,float)
        return this
    }
    fun mlHorizontalBias(float: Float): JJProgressViewFS {
        mMotionConstraintSet?.setHorizontalBias(id,float)
        return this
    }

    fun mlCenterHorizontallyOf(viewId: Int, marginStart: Int = 0, marginEnd: Int = 0): JJProgressViewFS {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.START, viewId, ConstraintSet.START, marginStart)
        mMotionConstraintSet?.connect(this.id, ConstraintSet.END, viewId, ConstraintSet.END, marginEnd)
        mMotionConstraintSet?.setHorizontalBias(viewId,0.5f)
        return this
    }
    fun mlCenterVerticallyOf(viewId: Int,marginTop: Int = 0, marginBottom: Int = 0): JJProgressViewFS {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.TOP, viewId, ConstraintSet.TOP, marginTop)
        mMotionConstraintSet?.connect(this.id, ConstraintSet.BOTTOM, viewId, ConstraintSet.BOTTOM, marginBottom)
        mMotionConstraintSet?.setVerticalBias(viewId,0.5f)
        return this
    }

    fun mlMargins(margins: JJMargin) : JJProgressViewFS {
        mMotionConstraintSet?.setMargin(id, ConstraintSet.TOP,margins.top)
        mMotionConstraintSet?.setMargin(id, ConstraintSet.BOTTOM,margins.bottom)
        mMotionConstraintSet?.setMargin(id, ConstraintSet.END,margins.right)
        mMotionConstraintSet?.setMargin(id, ConstraintSet.START,margins.left)
        return this
    }


    fun mlFloatCustomAttribute(attrName: String, value: Float): JJProgressViewFS {
        mMotionConstraintSet?.setFloatValue(id,attrName,value)
        return this
    }

    fun mlIntCustomAttribute(attrName: String, value: Int): JJProgressViewFS {
        mMotionConstraintSet?.setIntValue(id,attrName,value)
        return this
    }

    fun mlColorCustomAttribute(attrName: String, value: Int): JJProgressViewFS {
        mMotionConstraintSet?.setColorValue(id,attrName,value)
        return this
    }

    fun mlStringCustomAttribute(attrName: String, value: String): JJProgressViewFS {
        mMotionConstraintSet?.setStringValue(id,attrName,value)
        return this
    }

    fun mlRotation(float: Float): JJProgressViewFS {
        mMotionConstraintSet?.setRotation(id,float)
        return this
    }

    fun mlRotationX(float: Float): JJProgressViewFS {
        mMotionConstraintSet?.setRotationX(id,float)
        return this
    }

    fun mlRotationY(float: Float): JJProgressViewFS {
        mMotionConstraintSet?.setRotationY(id,float)
        return this
    }

    fun mlTranslation(x: Float,y: Float): JJProgressViewFS {
        mMotionConstraintSet?.setTranslation(id,x,y)
        return this
    }
    fun mlTranslationX(x: Float): JJProgressViewFS {
        mMotionConstraintSet?.setTranslationX(id,x)
        return this
    }

    fun mlTranslationY(y: Float): JJProgressViewFS {
        mMotionConstraintSet?.setTranslationY(id,y)
        return this
    }

    fun mlTranslationZ(z: Float): JJProgressViewFS {
        mMotionConstraintSet?.setTranslationZ(id,z)
        return this
    }

    fun mlTransformPivot(x: Float, y: Float): JJProgressViewFS {
        mMotionConstraintSet?.setTransformPivot(id,x,y)
        return this
    }

    fun mlTransformPivotX(x: Float): JJProgressViewFS {
        mMotionConstraintSet?.setTransformPivotX(id,x)
        return this
    }

    fun mlTransformPivotY(y: Float): JJProgressViewFS {
        mMotionConstraintSet?.setTransformPivotY(id,y)
        return this
    }

    fun mlScaleX(x: Float): JJProgressViewFS {
        mMotionConstraintSet?.setScaleX(id,x)
        return this
    }

    fun mlScaleY(y: Float): JJProgressViewFS {
        mMotionConstraintSet?.setScaleY(id,y)
        return this
    }

    fun mlDimensionRatio(ratio: String): JJProgressViewFS {
        mMotionConstraintSet?.setDimensionRatio(id,ratio)
        return this
    }

    fun mlAlpha(alpha: Float): JJProgressViewFS {
        mMotionConstraintSet?.setAlpha(id,alpha)
        return this
    }



    fun mlTopToTop(viewId: Int, margin: Int = 0): JJProgressViewFS {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.TOP, viewId, ConstraintSet.TOP, margin)
        return this
    }

    fun mlTopToTopParent(margin: Int = 0): JJProgressViewFS {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin)
        return this
    }


    fun mlTopToBottomOf(viewId: Int, margin: Int = 0): JJProgressViewFS {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.TOP, viewId, ConstraintSet.BOTTOM, margin)
        return this
    }

    fun mlTopToBottomParent(margin: Int = 0): JJProgressViewFS {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin)
        return this
    }

    fun mlBottomToTopOf(viewId: Int, margin: Int = 0): JJProgressViewFS {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.BOTTOM, viewId, ConstraintSet.TOP, margin)

        return this
    }

    fun mlBottomToTopParent(margin: Int = 0): JJProgressViewFS {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin)

        return this
    }

    fun mlBottomToBottomOf(viewId: Int, margin: Int = 0): JJProgressViewFS {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.BOTTOM, viewId, ConstraintSet.BOTTOM, margin)

        return this
    }

    fun mlBottomToBottomParent(margin: Int = 0): JJProgressViewFS {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin)

        return this
    }

    fun mlStartToStartOf(viewId: Int, margin: Int = 0): JJProgressViewFS {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.START, viewId, ConstraintSet.START, margin)

        return this
    }

    fun mlStartToStartParent(margin: Int = 0): JJProgressViewFS {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margin)

        return this
    }

    fun mlStartToEndOf(viewId: Int, margin: Int = 0): JJProgressViewFS {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.START, viewId, ConstraintSet.END, margin)

        return this
    }

    fun mlStartToEndParent(margin: Int = 0): JJProgressViewFS {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.END, margin)

        return this
    }

    fun mlEndToEndOf(viewId: Int, margin: Int= 0): JJProgressViewFS {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.END, viewId, ConstraintSet.END, margin)

        return this
    }

    fun mlEndToEndParent(margin: Int = 0): JJProgressViewFS {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, margin)

        return this
    }


    fun mlEndToStartOf(viewId: Int, margin: Int = 0): JJProgressViewFS {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.END, viewId, ConstraintSet.START, margin)
        return this
    }

    fun mlEndToStartParent(margin: Int = 0): JJProgressViewFS {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.START, margin)
        return this
    }


    fun mlWidth(width: Int): JJProgressViewFS {
        mMotionConstraintSet?.constrainWidth(id, width)
        return this
    }

    fun mlHeight(height: Int): JJProgressViewFS {
        mMotionConstraintSet?.constrainHeight(id, height)
        return this
    }

    fun mlPercentWidth(width: Float): JJProgressViewFS {
        mMotionConstraintSet?.constrainPercentWidth(id, width)
        return this
    }

    fun mlPercentHeight(height: Float): JJProgressViewFS {
        mMotionConstraintSet?.constrainPercentHeight(id, height)
        return this
    }

    fun mlCenterInParent(): JJProgressViewFS {
        mMotionConstraintSet?.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mMotionConstraintSet?.setVerticalBias(id, 0.5f)
        mMotionConstraintSet?.setHorizontalBias(id, 0.5f)

        return this
    }

    fun mlCenterInParent(verticalBias: Float, horizontalBias: Float, margin: JJMargin): JJProgressViewFS {
        mMotionConstraintSet?.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margin.left)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, margin.right)
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin.top)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin.bottom)
        mMotionConstraintSet?.setVerticalBias(id, verticalBias)
        mMotionConstraintSet?.setHorizontalBias(id, horizontalBias)
        return this
    }

    fun mlCenterInParentVertically(): JJProgressViewFS {
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mMotionConstraintSet?.setVerticalBias(id, 0.5f)

        return this
    }

    fun mlCenterInParentHorizontally(): JJProgressViewFS {
        mMotionConstraintSet?.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mMotionConstraintSet?.setHorizontalBias(id, 0.5f)
        return this
    }

    fun mlCenterInParentVertically(bias: Float, topMargin: Int, bottomMargin: Int): JJProgressViewFS {
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, bottomMargin)
        mMotionConstraintSet?.setVerticalBias(id, bias)
        return this
    }

    fun mlCenterInParentHorizontally(bias: Float, startMargin: Int, endtMargin: Int): JJProgressViewFS {
        mMotionConstraintSet?.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, startMargin)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, endtMargin)
        mMotionConstraintSet?.setHorizontalBias(id, bias)
        return this
    }


    fun mlCenterInParentTopVertically(): JJProgressViewFS {
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mMotionConstraintSet?.setVerticalBias(id, 0.5f)
        return this
    }


    fun mlCenterInParentBottomVertically(): JJProgressViewFS {
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mMotionConstraintSet?.setVerticalBias(id, 0.5f)
        return this
    }

    fun mlCenterInParentStartHorizontally(): JJProgressViewFS {
        mMotionConstraintSet?.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mMotionConstraintSet?.setHorizontalBias(id, 0.5f)
        return this
    }

    fun mlCenterInParentEndHorizontally(): JJProgressViewFS {
        mMotionConstraintSet?.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mMotionConstraintSet?.setHorizontalBias(id, 0.5f)
        return this
    }

    fun mlCenterInTopVerticallyOf(viewId: Int): JJProgressViewFS {
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, viewId, ConstraintSet.TOP, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, viewId, ConstraintSet.TOP, 0)
        mMotionConstraintSet?.setVerticalBias(id, 0.5f)
        return this
    }


    fun mlCenterInBottomVerticallyOf(viewId: Int): JJProgressViewFS {
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, viewId, ConstraintSet.BOTTOM, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, viewId, ConstraintSet.BOTTOM, 0)
        mMotionConstraintSet?.setVerticalBias(id, 0.5f)
        return this
    }

    fun mlCenterInStartHorizontallyOf(viewId: Int): JJProgressViewFS {
        mMotionConstraintSet?.connect(id, ConstraintSet.START, viewId, ConstraintSet.START, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, viewId, ConstraintSet.START, 0)
        mMotionConstraintSet?.setHorizontalBias(id, 0.5f)
        return this
    }

    fun mlCenterInEndHorizontallyOf(viewId: Int): JJProgressViewFS {
        mMotionConstraintSet?.connect(id, ConstraintSet.START, viewId, ConstraintSet.END, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, viewId, ConstraintSet.END, 0)
        mMotionConstraintSet?.setHorizontalBias(id, 0.5f)
        return this
    }

    fun mlCenterVertically(topId: Int, topSide: Int, topMargin: Int, bottomId: Int, bottomSide: Int, bottomMargin: Int, bias: Float): JJProgressViewFS {
        mMotionConstraintSet?.centerVertically(id, topId, topSide, topMargin, bottomId, bottomSide, bottomMargin, bias)
        return this
    }

    fun mlCenterHorizontally(startId: Int, startSide: Int, startMargin: Int, endId: Int, endSide: Int, endMargin: Int, bias: Float): JJProgressViewFS {
        mMotionConstraintSet?.centerHorizontally(id, startId, startSide, startMargin, endId, endSide, endMargin, bias)
        return this
    }


    fun mlFillParent(): JJProgressViewFS {
        mMotionConstraintSet?.constrainWidth(id,0)
        mMotionConstraintSet?.constrainHeight(id,0)
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        return this
    }

    fun mlFillParent(margin: JJMargin): JJProgressViewFS {
        mMotionConstraintSet?.constrainWidth(id,0)
        mMotionConstraintSet?.constrainHeight(id,0)
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin.top)
        mMotionConstraintSet?.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margin.left)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, margin.right)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin.bottom)
        return this
    }

    fun mlFillParentHorizontally(): JJProgressViewFS {
        mMotionConstraintSet?.constrainWidth(id,0)
        mMotionConstraintSet?.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        return this
    }

    fun mlFillParentVertically(): JJProgressViewFS {
        mMotionConstraintSet?.constrainHeight(id,0)
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        return this
    }

    fun mlFillParentHorizontally(startMargin: Int, endMargin: Int): JJProgressViewFS {
        mMotionConstraintSet?.constrainWidth(id,0)
        mMotionConstraintSet?.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, startMargin)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, endMargin)
        return this
    }

    fun mlFillParentVertically(topMargin: Int, bottomMargin: Int): JJProgressViewFS {
        mMotionConstraintSet?.constrainHeight(id,0)
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, bottomMargin)
        return this
    }

    fun mlVisibility(visibility: Int): JJProgressViewFS {
        mMotionConstraintSet?.setVisibility(id, visibility)
        return this
    }

    fun mlElevation(elevation: Float): JJProgressViewFS {
        mMotionConstraintSet?.setElevation(id, elevation)
        return this
    }

    fun mlApply(): JJProgressViewFS {
        mMotionConstraintSet?.applyTo(parent as ConstraintLayout)
        return this
    }

    fun mlSetConstraint(cs : ConstraintSet?): JJProgressViewFS {
        mMotionConstraintSet = cs
        return this
    }

    fun mlDisposeConstraint(): JJProgressViewFS {
        mMotionConstraintSet = null
        return this
    }

    //endregion

    //region ConstraintLayout Params
    protected val mConstraintSet = ConstraintSet()


    fun clFloatCustomAttribute(attrName: String, value: Float): JJProgressViewFS {
        mConstraintSet.setFloatValue(id,attrName,value)
        return this
    }

    fun clIntCustomAttribute(attrName: String, value: Int): JJProgressViewFS {
        mConstraintSet.setIntValue(id,attrName,value)
        return this
    }

    fun clColorCustomAttribute(attrName: String, value: Int): JJProgressViewFS {
        mConstraintSet.setColorValue(id,attrName,value)
        return this
    }

    fun clStringCustomAttribute(attrName: String, value: String): JJProgressViewFS {
        mConstraintSet.setStringValue(id,attrName,value)
        return this
    }

    fun clRotation(float: Float): JJProgressViewFS {
        mConstraintSet.setRotation(id,float)
        return this
    }

    fun clRotationX(float: Float): JJProgressViewFS {
        mConstraintSet.setRotationX(id,float)
        return this
    }

    fun clRotationY(float: Float): JJProgressViewFS {
        mConstraintSet.setRotationY(id,float)
        return this
    }

    fun clTranslation(x: Float,y: Float): JJProgressViewFS {
        mConstraintSet.setTranslation(id,x,y)
        return this
    }
    fun clTranslationX(x: Float): JJProgressViewFS {
        mConstraintSet.setTranslationX(id,x)
        return this
    }

    fun clTranslationY(y: Float): JJProgressViewFS {
        mConstraintSet.setTranslationY(id,y)
        return this
    }

    fun clTranslationZ(z: Float): JJProgressViewFS {
        mConstraintSet.setTranslationZ(id,z)
        return this
    }

    fun clTransformPivot(x: Float, y: Float): JJProgressViewFS {
        mConstraintSet.setTransformPivot(id,x,y)
        return this
    }

    fun clTransformPivotX(x: Float): JJProgressViewFS {
        mConstraintSet.setTransformPivotX(id,x)
        return this
    }

    fun clTransformPivotY(y: Float): JJProgressViewFS {
        mConstraintSet.setTransformPivotY(id,y)
        return this
    }

    fun clScaleX(x: Float): JJProgressViewFS {
        mConstraintSet.setScaleX(id,x)
        return this
    }

    fun clScaleY(y: Float): JJProgressViewFS {
        mConstraintSet.setScaleY(id,y)
        return this
    }

    fun clDimensionRatio(ratio: String): JJProgressViewFS {
        mConstraintSet.setDimensionRatio(id,ratio)
        return this
    }

    fun clAlpha(alpha: Float): JJProgressViewFS {
        mConstraintSet.setAlpha(id,alpha)
        return this
    }


    fun clApply(): JJProgressViewFS {
        mConstraintSet.applyTo(parent as ConstraintLayout)
        return this
    }

    fun clVisibilityMode(visibility: Int): JJProgressViewFS {
        mConstraintSet.setVisibilityMode(id, visibility)
        return this
    }

    fun clVerticalBias(float: Float): JJProgressViewFS {
        mConstraintSet.setVerticalBias(id,float)
        return this
    }
    fun clHorizontalBias(float: Float): JJProgressViewFS {
        mConstraintSet.setHorizontalBias(id,float)
        return this
    }

    fun clCenterHorizontallyOf(viewId: Int, marginStart: Int = 0, marginEnd: Int = 0): JJProgressViewFS {
        mConstraintSet.connect(this.id, ConstraintSet.START, viewId, ConstraintSet.START, marginStart)
        mConstraintSet.connect(this.id, ConstraintSet.END, viewId, ConstraintSet.END, marginEnd)
        mConstraintSet.setHorizontalBias(id,0.5f)
        return this
    }
    fun clCenterVerticallyOf(viewId: Int,marginTop: Int = 0, marginBottom: Int = 0): JJProgressViewFS {
        mConstraintSet.connect(this.id, ConstraintSet.TOP, viewId, ConstraintSet.TOP, marginTop)
        mConstraintSet.connect(this.id, ConstraintSet.BOTTOM, viewId, ConstraintSet.BOTTOM, marginBottom)
        mConstraintSet.setVerticalBias(id,0.5f)
        return this
    }

    fun clMargins(margins: JJMargin) : JJProgressViewFS {
        mConstraintSet.setMargin(id, ConstraintSet.TOP,margins.top)
        mConstraintSet.setMargin(id, ConstraintSet.BOTTOM,margins.bottom)
        mConstraintSet.setMargin(id, ConstraintSet.END,margins.right)
        mConstraintSet.setMargin(id, ConstraintSet.START,margins.left)
        return this
    }


    fun clTopToTop(viewId: Int, margin: Int = 0): JJProgressViewFS {
        mConstraintSet.connect(this.id, ConstraintSet.TOP, viewId, ConstraintSet.TOP, margin)
        return this
    }

    fun clTopToTopParent(margin: Int = 0): JJProgressViewFS {
        mConstraintSet.connect(this.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin)
        return this
    }


    fun clTopToBottom(viewId: Int, margin: Int = 0): JJProgressViewFS {
        mConstraintSet.connect(this.id, ConstraintSet.TOP, viewId, ConstraintSet.BOTTOM, margin)
        return this
    }

    fun clTopToBottomParent(margin: Int = 0): JJProgressViewFS {
        mConstraintSet.connect(this.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin)
        return this
    }

    fun clBottomToTop(viewId: Int, margin: Int = 0): JJProgressViewFS {
        mConstraintSet.connect(this.id, ConstraintSet.BOTTOM, viewId, ConstraintSet.TOP, margin)
        return this
    }

    fun clBottomToTopParent(margin: Int = 0): JJProgressViewFS {
        mConstraintSet.connect(this.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin)
        return this
    }

    fun clBottomToBottom(viewId: Int, margin: Int = 0): JJProgressViewFS {
        mConstraintSet.connect(this.id, ConstraintSet.BOTTOM, viewId, ConstraintSet.BOTTOM, margin)
        return this
    }

    fun clBottomToBottomParent(margin: Int = 0): JJProgressViewFS {
        mConstraintSet.connect(this.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin)
        return this
    }

    fun clStartToStart(viewId: Int, margin: Int = 0): JJProgressViewFS {
        mConstraintSet.connect(this.id, ConstraintSet.START, viewId, ConstraintSet.START, margin)
        return this
    }

    fun clStartToStartParent(margin: Int = 0): JJProgressViewFS {
        mConstraintSet.connect(this.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margin)
        return this
    }

    fun clStartToEnd(viewId: Int, margin: Int = 0): JJProgressViewFS {
        mConstraintSet.connect(this.id, ConstraintSet.START, viewId, ConstraintSet.END, margin)
        return this
    }

    fun clStartToEndParent(margin: Int = 0): JJProgressViewFS {
        mConstraintSet.connect(this.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.END, margin)
        return this
    }

    fun clEndToEnd(viewId: Int, margin: Int = 0): JJProgressViewFS {
        mConstraintSet.connect(this.id, ConstraintSet.END, viewId, ConstraintSet.END, margin)
        return this
    }

    fun clEndToEndParent(margin: Int = 0): JJProgressViewFS {
        mConstraintSet.connect(this.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, margin)
        return this
    }


    fun clEndToStart(viewId: Int, margin: Int = 0): JJProgressViewFS {
        mConstraintSet.connect(this.id, ConstraintSet.END, viewId, ConstraintSet.START, margin)
        return this
    }

    fun clEndToStartParent(margin: Int = 0): JJProgressViewFS {
        mConstraintSet.connect(this.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.START, margin)
        return this
    }


    fun clWidth(width: Int): JJProgressViewFS {
        mConstraintSet.constrainWidth(id, width)
        return this
    }

    fun clHeight(height: Int): JJProgressViewFS {
        mConstraintSet.constrainHeight(id, height)
        return this
    }

    fun clPercentWidth(width: Float): JJProgressViewFS {
        mConstraintSet.constrainPercentWidth(id, width)
        return this
    }

    fun clPercentHeight(height: Float): JJProgressViewFS {
        mConstraintSet.constrainPercentHeight(id, height)
        return this
    }

    fun clCenterInParent(): JJProgressViewFS {
        mConstraintSet.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSet.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mConstraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mConstraintSet.setVerticalBias(id, 0.5f)
        mConstraintSet.setHorizontalBias(id, 0.5f)
        return this
    }

    fun clCenterInParent(verticalBias: Float, horizontalBias: Float, margin: JJMargin): JJProgressViewFS {
        mConstraintSet.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margin.left)
        mConstraintSet.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, margin.right)
        mConstraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin.top)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin.bottom)
        mConstraintSet.setVerticalBias(id, verticalBias)
        mConstraintSet.setHorizontalBias(id, horizontalBias)
        return this
    }

    fun clCenterInParentVertically(): JJProgressViewFS {
        mConstraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mConstraintSet.setVerticalBias(id, 0.5f)
        return this
    }

    fun clCenterInParentHorizontally(): JJProgressViewFS {
        mConstraintSet.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSet.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mConstraintSet.setHorizontalBias(id, 0.5f)
        return this
    }

    fun clCenterInParentVertically(bias: Float, topMargin: Int, bottomMargin: Int): JJProgressViewFS {
        mConstraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, bottomMargin)
        mConstraintSet.setVerticalBias(id, bias)
        return this
    }

    fun clCenterInParentHorizontally(bias: Float, startMargin: Int, endtMargin: Int): JJProgressViewFS {
        mConstraintSet.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, startMargin)
        mConstraintSet.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, endtMargin)
        mConstraintSet.setHorizontalBias(id, bias)
        return this
    }


    fun clCenterInParentTopVertically(): JJProgressViewFS {
        mConstraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSet.setVerticalBias(id, 0.5f)
        return this
    }


    fun clCenterInParentBottomVertically(): JJProgressViewFS {
        mConstraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mConstraintSet.setVerticalBias(id, 0.5f)
        return this
    }

    fun clCenterInParentStartHorizontally(): JJProgressViewFS {
        mConstraintSet.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSet.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSet.setHorizontalBias(id, 0.5f)
        return this
    }

    fun clCenterInParentEndHorizontally(): JJProgressViewFS {
        mConstraintSet.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mConstraintSet.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mConstraintSet.setHorizontalBias(id, 0.5f)
        return this
    }

    fun clCenterInTopVertically(topId: Int): JJProgressViewFS {
        mConstraintSet.connect(id, ConstraintSet.TOP, topId, ConstraintSet.TOP, 0)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, topId, ConstraintSet.TOP, 0)
        mConstraintSet.setVerticalBias(id, 0.5f)
        return this
    }


    fun clCenterInBottomVertically(bottomId: Int): JJProgressViewFS {
        mConstraintSet.connect(id, ConstraintSet.TOP, bottomId, ConstraintSet.BOTTOM, 0)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, bottomId, ConstraintSet.BOTTOM, 0)
        mConstraintSet.setVerticalBias(id, 0.5f)
        return this
    }

    fun clCenterInStartHorizontally(startId: Int): JJProgressViewFS {
        mConstraintSet.connect(id, ConstraintSet.START, startId, ConstraintSet.START, 0)
        mConstraintSet.connect(id, ConstraintSet.END, startId, ConstraintSet.START, 0)
        mConstraintSet.setHorizontalBias(id, 0.5f)
        return this
    }

    fun clCenterInEndHorizontally(endId: Int): JJProgressViewFS {
        mConstraintSet.connect(id, ConstraintSet.START, endId, ConstraintSet.END, 0)
        mConstraintSet.connect(id, ConstraintSet.END, endId, ConstraintSet.END, 0)
        mConstraintSet.setHorizontalBias(id, 0.5f)
        return this
    }

    fun clCenterVertically(topId: Int, topSide: Int, topMargin: Int, bottomId: Int, bottomSide: Int, bottomMargin: Int, bias: Float): JJProgressViewFS {
        mConstraintSet.centerVertically(id, topId, topSide, topMargin, bottomId, bottomSide, bottomMargin, bias)
        return this
    }

    fun clCenterHorizontally(startId: Int, startSide: Int, startMargin: Int, endId: Int, endSide: Int, endMargin: Int, bias: Float): JJProgressViewFS {
        mConstraintSet.centerHorizontally(id, startId, startSide, startMargin, endId, endSide, endMargin, bias)
        return this
    }


    fun clFillParent(): JJProgressViewFS {
        mConstraintSet.constrainWidth(id,0)
        mConstraintSet.constrainHeight(id,0)
        mConstraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSet.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSet.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        return this
    }

    fun clFillParent(margin: JJMargin): JJProgressViewFS {
        mConstraintSet.constrainWidth(id,0)
        mConstraintSet.constrainHeight(id,0)
        mConstraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin.top)
        mConstraintSet.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margin.left)
        mConstraintSet.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, margin.right)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin.bottom)
        return this
    }

    fun clFillParentHorizontally(): JJProgressViewFS {
        mConstraintSet.constrainWidth(id,0)
        mConstraintSet.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSet.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        return this
    }

    fun clFillParentVertically(): JJProgressViewFS {
        mConstraintSet.constrainHeight(id,0)
        mConstraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        return this
    }

    fun clFillParentHorizontally(startMargin: Int, endMargin: Int): JJProgressViewFS {
        mConstraintSet.constrainWidth(id,0)
        mConstraintSet.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, startMargin)
        mConstraintSet.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, endMargin)
        return this
    }

    fun clFillParentVertically(topMargin: Int, bottomMargin: Int): JJProgressViewFS {
        mConstraintSet.constrainHeight(id,0)
        mConstraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, bottomMargin)
        return this
    }

    fun clVisibility(visibility: Int): JJProgressViewFS {
        mConstraintSet.setVisibility(id, visibility)
        return this
    }



    fun clElevation(elevation: Float): JJProgressViewFS {
        mConstraintSet.setElevation(id, elevation)

        return this
    }

    fun clConstraintSet() : ConstraintSet {
        return mConstraintSet
    }

    fun clMinWidth(w:Int): JJProgressViewFS {
        mConstraintSet.constrainMinWidth(id,w)
        return this
    }

    fun clMinHeight(h:Int): JJProgressViewFS {
        mConstraintSet.constrainMinHeight(id,h)
        return this
    }

    fun clMaxWidth(w:Int): JJProgressViewFS {
        mConstraintSet.constrainMaxWidth(id,w)
        return this
    }

    fun clMaxHeight(h:Int): JJProgressViewFS {
        mConstraintSet.constrainMaxHeight(id,h)
        return this
    }






    //endregion

    //region ConstraintLayout LandScape Params
    protected val mConstraintSetLandScape = ConstraintSet()

    fun cllApply(): JJProgressViewFS {
        mConstraintSetLandScape.applyTo(parent as ConstraintLayout)
        return this
    }


    fun cllFloatCustomAttribute(attrName: String, value: Float): JJProgressViewFS {
        mConstraintSet.setFloatValue(id,attrName,value)
        return this
    }

    fun cllIntCustomAttribute(attrName: String, value: Int): JJProgressViewFS {
        mConstraintSet.setIntValue(id,attrName,value)
        return this
    }

    fun cllColorCustomAttribute(attrName: String, value: Int): JJProgressViewFS {
        mConstraintSet.setColorValue(id,attrName,value)
        return this
    }

    fun cllStringCustomAttribute(attrName: String, value: String): JJProgressViewFS {
        mConstraintSet.setStringValue(id,attrName,value)
        return this
    }

    fun cllRotation(float: Float): JJProgressViewFS {
        mConstraintSet.setRotation(id,float)
        return this
    }

    fun cllRotationX(float: Float): JJProgressViewFS {
        mConstraintSet.setRotationX(id,float)
        return this
    }

    fun cllRotationY(float: Float): JJProgressViewFS {
        mConstraintSet.setRotationY(id,float)
        return this
    }

    fun cllTranslation(x: Float,y: Float): JJProgressViewFS {
        mConstraintSet.setTranslation(id,x,y)
        return this
    }
    fun cllTranslationX(x: Float): JJProgressViewFS {
        mConstraintSet.setTranslationX(id,x)
        return this
    }

    fun cllTranslationY(y: Float): JJProgressViewFS {
        mConstraintSet.setTranslationY(id,y)
        return this
    }

    fun cllTranslationZ(z: Float): JJProgressViewFS {
        mConstraintSet.setTranslationZ(id,z)
        return this
    }

    fun cllTransformPivot(x: Float, y: Float): JJProgressViewFS {
        mConstraintSet.setTransformPivot(id,x,y)
        return this
    }

    fun cllTransformPivotX(x: Float): JJProgressViewFS {
        mConstraintSet.setTransformPivotX(id,x)
        return this
    }

    fun cllTransformPivotY(y: Float): JJProgressViewFS {
        mConstraintSet.setTransformPivotY(id,y)
        return this
    }

    fun cllScaleX(x: Float): JJProgressViewFS {
        mConstraintSet.setScaleX(id,x)
        return this
    }

    fun cllScaleY(y: Float): JJProgressViewFS {
        mConstraintSet.setScaleY(id,y)
        return this
    }

    fun cllDimensionRatio(ratio: String): JJProgressViewFS {
        mConstraintSet.setDimensionRatio(id,ratio)
        return this
    }

    fun cllAlpha(alpha: Float): JJProgressViewFS {
        mConstraintSet.setAlpha(id,alpha)
        return this
    }


    fun cllVisibilityMode(visibility: Int): JJProgressViewFS {
        mConstraintSetLandScape.setVisibilityMode(id, visibility)
        return this
    }

    fun cllVerticalBias(float: Float): JJProgressViewFS {
        mConstraintSetLandScape.setVerticalBias(id,float)
        return this
    }
    fun cllHorizontalBias(float: Float): JJProgressViewFS {
        mConstraintSetLandScape.setHorizontalBias(id,float)
        return this
    }

    fun cllCenterHorizontallyOf(viewId: Int, marginStart: Int = 0, marginEnd: Int = 0): JJProgressViewFS {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.START, viewId, ConstraintSet.START, marginStart)
        mConstraintSetLandScape.connect(this.id, ConstraintSet.END, viewId, ConstraintSet.END, marginEnd)
        mConstraintSetLandScape.setHorizontalBias(id,0.5f)
        return this
    }
    fun cllCenterVerticallyOf(viewId: Int,marginTop: Int = 0, marginBottom: Int = 0): JJProgressViewFS {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.TOP, viewId, ConstraintSet.TOP, marginTop)
        mConstraintSetLandScape.connect(this.id, ConstraintSet.BOTTOM, viewId, ConstraintSet.BOTTOM, marginBottom)
        mConstraintSetLandScape.setVerticalBias(id,0.5f)
        return this
    }

    fun cllMargins(margins: JJMargin) : JJProgressViewFS {
        mConstraintSetLandScape.setMargin(id, ConstraintSet.TOP,margins.top)
        mConstraintSetLandScape.setMargin(id, ConstraintSet.BOTTOM,margins.bottom)
        mConstraintSetLandScape.setMargin(id, ConstraintSet.END,margins.right)
        mConstraintSetLandScape.setMargin(id, ConstraintSet.START,margins.left)
        return this
    }


    fun cllTopToTop(viewId: Int, margin: Int = 0): JJProgressViewFS {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.TOP, viewId, ConstraintSet.TOP, margin)
        return this
    }

    fun cllTopToTopParent(margin: Int = 0): JJProgressViewFS {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin)
        return this
    }


    fun cllTopToBottom(viewId: Int, margin: Int = 0): JJProgressViewFS {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.TOP, viewId, ConstraintSet.BOTTOM, margin)
        return this
    }

    fun cllTopToBottomParent(margin: Int = 0): JJProgressViewFS {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin)
        return this
    }

    fun cllBottomToTop(viewId: Int, margin: Int = 0): JJProgressViewFS {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.BOTTOM, viewId, ConstraintSet.TOP, margin)
        return this
    }

    fun cllBottomToTopParent(margin: Int = 0): JJProgressViewFS {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin)
        return this
    }

    fun cllBottomToBottom(viewId: Int, margin: Int = 0): JJProgressViewFS {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.BOTTOM, viewId, ConstraintSet.BOTTOM, margin)
        return this
    }

    fun cllBottomToBottomParent(margin: Int = 0): JJProgressViewFS {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin)
        return this
    }

    fun cllStartToStart(viewId: Int, margin: Int = 0): JJProgressViewFS {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.START, viewId, ConstraintSet.START, margin)
        return this
    }

    fun cllStartToStartParent(margin: Int = 0): JJProgressViewFS {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margin)
        return this
    }

    fun cllStartToEnd(viewId: Int, margin: Int = 0): JJProgressViewFS {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.START, viewId, ConstraintSet.END, margin)
        return this
    }

    fun cllStartToEndParent(margin: Int = 0): JJProgressViewFS {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.END, margin)
        return this
    }

    fun cllEndToEnd(viewId: Int, margin: Int = 0): JJProgressViewFS {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.END, viewId, ConstraintSet.END, margin)
        return this
    }

    fun cllEndToEndParent(margin: Int = 0): JJProgressViewFS {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, margin)
        return this
    }


    fun cllEndToStart(viewId: Int, margin: Int = 0): JJProgressViewFS {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.END, viewId, ConstraintSet.START, margin)
        return this
    }

    fun cllEndToStartParent(margin: Int = 0): JJProgressViewFS {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.START, margin)
        return this
    }


    fun cllWidth(width: Int): JJProgressViewFS {
        mConstraintSetLandScape.constrainWidth(id, width)
        return this
    }

    fun cllHeight(height: Int): JJProgressViewFS {
        mConstraintSetLandScape.constrainHeight(id, height)
        return this
    }

    fun cllPercentWidth(width: Float): JJProgressViewFS {
        mConstraintSetLandScape.constrainPercentWidth(id, width)
        return this
    }

    fun cllPercentHeight(height: Float): JJProgressViewFS {
        mConstraintSetLandScape.constrainPercentHeight(id, height)
        return this
    }

    fun cllCenterInParent(): JJProgressViewFS {
        mConstraintSetLandScape.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mConstraintSetLandScape.setVerticalBias(id, 0.5f)
        mConstraintSetLandScape.setHorizontalBias(id, 0.5f)
        return this
    }

    fun cllCenterInParent(verticalBias: Float, horizontalBias: Float, margin: JJMargin): JJProgressViewFS {
        mConstraintSetLandScape.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margin.left)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, margin.right)
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin.top)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin.bottom)
        mConstraintSetLandScape.setVerticalBias(id, verticalBias)
        mConstraintSetLandScape.setHorizontalBias(id, horizontalBias)
        return this
    }

    fun cllCenterInParentVertically(): JJProgressViewFS {
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mConstraintSetLandScape.setVerticalBias(id, 0.5f)
        return this
    }

    fun cllCenterInParentHorizontally(): JJProgressViewFS {
        mConstraintSetLandScape.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mConstraintSetLandScape.setHorizontalBias(id, 0.5f)
        return this
    }

    fun cllCenterInParentVertically(bias: Float, topMargin: Int, bottomMargin: Int): JJProgressViewFS {
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, bottomMargin)
        mConstraintSetLandScape.setVerticalBias(id, bias)
        return this
    }

    fun cllCenterInParentHorizontally(bias: Float, startMargin: Int, endtMargin: Int): JJProgressViewFS {
        mConstraintSetLandScape.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, startMargin)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, endtMargin)
        mConstraintSetLandScape.setHorizontalBias(id, bias)
        return this
    }


    fun cllCenterInParentTopVertically(): JJProgressViewFS {
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSetLandScape.setVerticalBias(id, 0.5f)
        return this
    }


    fun cllCenterInParentBottomVertically(): JJProgressViewFS {
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mConstraintSetLandScape.setVerticalBias(id, 0.5f)
        return this
    }

    fun cllCenterInParentStartHorizontally(): JJProgressViewFS {
        mConstraintSetLandScape.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSetLandScape.setHorizontalBias(id, 0.5f)
        return this
    }

    fun cllCenterInParentEndHorizontally(): JJProgressViewFS {
        mConstraintSetLandScape.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mConstraintSetLandScape.setHorizontalBias(id, 0.5f)
        return this
    }

    fun cllCenterInTopVertically(topId: Int): JJProgressViewFS {
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, topId, ConstraintSet.TOP, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, topId, ConstraintSet.TOP, 0)
        mConstraintSetLandScape.setVerticalBias(id, 0.5f)
        return this
    }


    fun cllCenterInBottomVertically(bottomId: Int): JJProgressViewFS {
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, bottomId, ConstraintSet.BOTTOM, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, bottomId, ConstraintSet.BOTTOM, 0)
        mConstraintSetLandScape.setVerticalBias(id, 0.5f)
        return this
    }

    fun cllCenterInStartHorizontally(startId: Int): JJProgressViewFS {
        mConstraintSetLandScape.connect(id, ConstraintSet.START, startId, ConstraintSet.START, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, startId, ConstraintSet.START, 0)
        mConstraintSetLandScape.setHorizontalBias(id, 0.5f)
        return this
    }

    fun cllCenterInEndHorizontally(endId: Int): JJProgressViewFS {
        mConstraintSetLandScape.connect(id, ConstraintSet.START, endId, ConstraintSet.END, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, endId, ConstraintSet.END, 0)
        mConstraintSetLandScape.setHorizontalBias(id, 0.5f)
        return this
    }

    fun cllCenterVertically(topId: Int, topSide: Int, topMargin: Int, bottomId: Int, bottomSide: Int, bottomMargin: Int, bias: Float): JJProgressViewFS {
        mConstraintSetLandScape.centerVertically(id, topId, topSide, topMargin, bottomId, bottomSide, bottomMargin, bias)
        return this
    }

    fun cllCenterHorizontally(startId: Int, startSide: Int, startMargin: Int, endId: Int, endSide: Int, endMargin: Int, bias: Float): JJProgressViewFS {
        mConstraintSetLandScape.centerHorizontally(id, startId, startSide, startMargin, endId, endSide, endMargin, bias)
        return this
    }


    fun cllFillParent(): JJProgressViewFS {
        mConstraintSetLandScape.constrainWidth(id,0)
        mConstraintSetLandScape.constrainHeight(id,0)
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        return this
    }

    fun cllFillParent(margin: JJMargin): JJProgressViewFS {
        mConstraintSetLandScape.constrainWidth(id,0)
        mConstraintSetLandScape.constrainHeight(id,0)
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin.top)
        mConstraintSetLandScape.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margin.left)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, margin.right)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin.bottom)
        return this
    }

    fun cllFillParentHorizontally(): JJProgressViewFS {
        mConstraintSetLandScape.constrainWidth(id,0)
        mConstraintSetLandScape.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        return this
    }

    fun cllFillParentVertically(): JJProgressViewFS {
        mConstraintSetLandScape.constrainHeight(id,0)
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        return this
    }

    fun cllFillParentHorizontally(startMargin: Int, endMargin: Int): JJProgressViewFS {
        mConstraintSetLandScape.constrainWidth(id,0)
        mConstraintSetLandScape.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, startMargin)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, endMargin)
        return this
    }

    fun cllFillParentVertically(topMargin: Int, bottomMargin: Int): JJProgressViewFS {
        mConstraintSetLandScape.constrainHeight(id,0)
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, bottomMargin)
        return this
    }

    fun cllVisibility(visibility: Int): JJProgressViewFS {
        mConstraintSetLandScape.setVisibility(id, visibility)
        return this
    }



    fun cllElevation(elevation: Float): JJProgressViewFS {
        mConstraintSetLandScape.setElevation(id, elevation)

        return this
    }

    fun cllConstraintSet() : ConstraintSet {
        return mConstraintSetLandScape
    }

    fun cllMinWidth(w:Int): JJProgressViewFS {
        mConstraintSetLandScape.constrainMinWidth(id,w)
        return this
    }

    fun cllMinHeight(h:Int): JJProgressViewFS {
        mConstraintSetLandScape.constrainMinHeight(id,h)
        return this
    }

    fun cllMaxWidth(w:Int): JJProgressViewFS {
        mConstraintSetLandScape.constrainMaxWidth(id,w)
        return this
    }

    fun cllMaxHeight(h:Int): JJProgressViewFS {
        mConstraintSetLandScape.constrainMaxHeight(id,h)
        return this
    }

    //endregion



}