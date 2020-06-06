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
import android.view.ViewOutlineProvider
import android.view.animation.Interpolator
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.updateMarginsRelative
import com.google.android.material.appbar.AppBarLayout
import com.jjlf.jjkit_layoutplus.utils.JJColorDrawablePlus
import com.jjlf.jjkit_layoututils.JJLayout
import com.jjlf.jjkit_layoututils.JJMargin
import com.jjlf.jjkit_layoututils.JJPadding
import com.jjlf.jjkit_layoututils.JJScreen

@SuppressLint("ResourceType")
open class JJSearchBarStatic : ConstraintLayout {

    //region custom
    private fun setupViews(context: Context,attrs: AttributeSet?){

        mSearchBar = ConstraintLayout(context)
        val ci = ContextThemeWrapper(context,R.style.defSearchBarClickImage)
        val ct = ContextThemeWrapper(context,R.style.defSearchBarClickText)
        mImageView = AppCompatImageView(ci,attrs)
        mTextView = AppCompatTextView(ct,attrs)
        mSearchBar.id = View.generateViewId()
        mImageView.id = View.generateViewId()
        mTextView.id = View.generateViewId()

        addView(mSearchBar)
        mSearchBar.addView(mImageView)
        mSearchBar.addView(mTextView)
        val marginHorizontal = JJScreen.percentWidth(0.04f)

        JJLayout.clSetView(mSearchBar)
            .clCenterInParent()
            .clPercentWidth(0.8f)
            .clHeight(JJScreen.percentHeight(0.065f))
            .clMinWidth(JJScreen.percentWidth(0.8f))

            .clSetView(mImageView)
            .clHeight(JJScreen.percentHeight(0.04f))
            .clWidth(JJScreen.percentHeight(0.04f))
            .clCenterInParentVertically()
            .clStartToStarParent(JJScreen.percentHeight(0.033f))

            .clSetView(mTextView)
            .clWidth(0)
            .clHeight(0)
            .clStartToEndOf(mImageView.id,marginHorizontal)
            .clEndToEndParent(marginHorizontal)
            .clFillParentVertically()

            .clDisposeView()

        val attrsArray = intArrayOf(
            R.attr.colorSurface,
            R.attr.colorOnSurface
        )
        val ba = context.obtainStyledAttributes(attrs,
            attrsArray, 0, 0)
        val surface = ba.getColor(0,Color.parseColor("#F6F8F7"))
        val onSurface = ba.getColor(1,Color.parseColor("#A9B0B6"))
        ba.recycle()

        mSearchBar.background = JJColorDrawablePlus().setFillColor(surface)
            .setShape(JJColorDrawablePlus.ROUND_CIRCLE)
        mImageView.background = null
        mImageView.imageTintList = ColorStateList.valueOf(onSurface)
        mTextView.background = null

    }


    fun setOnSearchClickListener(listener: (view: View) -> Unit): JJSearchBarStatic{
        mSearchBar.setOnClickListener(listener)
        return this
    }

    fun setTextSize(size:Float) : JJSearchBarStatic{
        mTextView.textSize = size
        return this
    }
    fun setTextColor(color:Int) : JJSearchBarStatic{
        mTextView.setTextColor(color)
        return this
    }
    fun setTypeFace(typeface: Typeface) : JJSearchBarStatic{
        mTextView.typeface = typeface
        return this
    }
     fun setTextViewAlignment(alignment:Int) : JJSearchBarStatic{
        mTextView.textAlignment = alignment
        return this
    }
    fun setTextGravity(gravity: Int): JJSearchBarStatic{
        mTextView.gravity = gravity
        return this
    }
    fun setColorSurface(color: Int): JJSearchBarStatic{
       val dr = (mSearchBar.background as JJColorDrawablePlus).setFillColor(color)
        dr.invalidateSelf()
        return this
    }
    fun setColorOnSurface(color: Int): JJSearchBarStatic{
        mImageView.imageTintList = ColorStateList.valueOf(color)
        return this
    }

    //endregion

    //region init

    private lateinit var mSearchBar: ConstraintLayout
    private lateinit var mImageView: AppCompatImageView
    private lateinit var mTextView : AppCompatTextView


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
            R.styleable.JJSearchBarStatic, 0, 0)
        mIgnoreCl = a.getBoolean(R.styleable.JJSearchBarStatic_layout_ignoreCl,false)
        mConfigurationChanged = a.getBoolean(R.styleable.JJSearchBarStatic_support_configuration_changed,false)
        mSupportLandScape = a.getBoolean(R.styleable.JJSearchBarStatic_support_landscape,false)

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

    private fun setupSizeLp(a:TypedArray,index:Int){
        when(a.getIndex(index)){
            R.styleable.JJSearchBarStatic_lpHeightPercentScreenWidth -> {
                mlpHeight = JJScreen.percentWidth(a.getFloat(R.styleable.JJSearchBarStatic_lpHeightPercentScreenWidth,0f))
            }
            R.styleable.JJSearchBarStatic_lpHeightPercentScreenHeight -> {
                mlpHeight = JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_lpHeightPercentScreenHeight,0f))
            }
            R.styleable.JJSearchBarStatic_lpWidthPercentScreenWidth -> {
                mlpWidth = JJScreen.percentWidth( a.getFloat(R.styleable.JJSearchBarStatic_lpWidthPercentScreenWidth,0f))
            }
            R.styleable.JJSearchBarStatic_lpWidthPercentScreenHeight -> {
                mlpWidth = JJScreen.percentHeight( a.getFloat(R.styleable.JJSearchBarStatic_lpWidthPercentScreenHeight,0f))
            }
            R.styleable.JJSearchBarStatic_lpHeightResponsive -> {
                mlpHeight =  responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_lpHeightResponsive)
            }
            R.styleable.JJSearchBarStatic_lpWidthResponsive -> {
                mlpWidth =  responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_lpWidthResponsive)
            }
            R.styleable.JJSearchBarStatic_lpHeightResponsivePercentScreenHeight -> {
                mlpHeight = responsiveSizePercentScreenHeight(a, R.styleable.JJSearchBarStatic_lpHeightResponsivePercentScreenHeight)
            }
            R.styleable.JJSearchBarStatic_lpWidthResponsivePercentScreenHeight -> {
                mlpWidth = responsiveSizePercentScreenHeight(a, R.styleable.JJSearchBarStatic_lpWidthResponsivePercentScreenHeight)
            }
            R.styleable.JJSearchBarStatic_lpHeightResponsivePercentScreenWidth -> {
                mlpHeight = responsiveSizePercentScreenWidth(a, R.styleable.JJSearchBarStatic_lpHeightResponsivePercentScreenWidth)
            }
            R.styleable.JJSearchBarStatic_lpWidthResponsivePercentScreenWidth -> {
                mlpWidth = responsiveSizePercentScreenWidth(a, R.styleable.JJSearchBarStatic_lpWidthResponsivePercentScreenWidth)
            }
        }
    }
    private fun setupMarginLp(a:TypedArray,index: Int){
        when(a.getIndex(index)){
            R.styleable.JJSearchBarStatic_lpMarginTopPerScHeight -> {
                mlpMargins.top = JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_lpMarginTopPerScHeight,0f))
            }
            R.styleable.JJSearchBarStatic_lpMarginLeftPerScHeight -> {
                mlpMargins.left = JJScreen.percentHeight( a.getFloat(R.styleable.JJSearchBarStatic_lpMarginLeftPerScHeight,0f))
            }
            R.styleable.JJSearchBarStatic_lpMarginRightPerScHeight -> {
                mlpMargins.right = JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_lpMarginRightPerScHeight,0f))
            }
            R.styleable.JJSearchBarStatic_lpMarginBottomPerScHeight -> {
                mlpMargins.bottom = JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_lpMarginBottomPerScHeight,0f))
            }
            R.styleable.JJSearchBarStatic_lpMarginTopPerScWidth -> {
                mlpMargins.top = JJScreen.percentWidth(a.getFloat(R.styleable.JJSearchBarStatic_lpMarginTopPerScWidth,0f))
            }
            R.styleable.JJSearchBarStatic_lpMarginLeftPerScWidth -> {
                mlpMargins.left = JJScreen.percentWidth( a.getFloat(R.styleable.JJSearchBarStatic_lpMarginLeftPerScWidth,0f))
            }
            R.styleable.JJSearchBarStatic_lpMarginRightPerScWidth->{
                mlpMargins.right = JJScreen.percentWidth(a.getFloat(R.styleable.JJSearchBarStatic_lpMarginRightPerScWidth,0f))
            }
            R.styleable.JJSearchBarStatic_lpMarginBottomPerScWidth -> {
                mlpMargins.bottom = JJScreen.percentWidth(a.getFloat(R.styleable.JJSearchBarStatic_lpMarginBottomPerScWidth,0f))
            }
            R.styleable.JJSearchBarStatic_lpMarginTopResponsive -> {
                mlpMargins.top = responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_lpMarginTopResponsive)
            }
            R.styleable.JJSearchBarStatic_lpMarginLeftResponsive ->{
                mlpMargins.left =  responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_lpMarginLeftResponsive)
            }
            R.styleable.JJSearchBarStatic_lpMarginRightResponsive -> {
                mlpMargins.right =   responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_lpMarginRightResponsive)
            }
            R.styleable.JJSearchBarStatic_lpMarginBottomResponsive -> {
                mlpMargins.bottom =  responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_lpMarginBottomResponsive)
            }
            R.styleable.JJSearchBarStatic_lpMarginTopResPerScWidth -> {
                mlpMargins.top  = responsiveSizePercentScreenWidth(a, R.styleable.JJSearchBarStatic_lpMarginTopResPerScWidth)
            }
            R.styleable.JJSearchBarStatic_lpMarginLeftResPerScWidth -> {
                mlpMargins.left = responsiveSizePercentScreenWidth(a, R.styleable.JJSearchBarStatic_lpMarginLeftResPerScWidth)
            }
            R.styleable.JJSearchBarStatic_lpMarginRightResPerScWidth -> {
                mlpMargins.right =  responsiveSizePercentScreenWidth(a, R.styleable.JJSearchBarStatic_lpMarginRightResPerScWidth)
            }
            R.styleable.JJSearchBarStatic_lpMarginBottomResPerScWidth -> {
                mlpMargins.bottom = responsiveSizePercentScreenWidth(a, R.styleable.JJSearchBarStatic_lpMarginBottomResPerScWidth)
            }
            R.styleable.JJSearchBarStatic_lpMarginTopResPerScHeight ->{
                mlpMargins.top = responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_lpMarginTopResPerScHeight)
            }
            R.styleable.JJSearchBarStatic_lpMarginLeftResPerScHeight ->{
                mlpMargins.left = responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_lpMarginLeftResPerScHeight)
            }
            R.styleable.JJSearchBarStatic_lpMarginRightResPerScHeight ->{
                mlpMargins.right = responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_lpMarginRightResPerScHeight)
            }
            R.styleable.JJSearchBarStatic_lpMarginBottomResPerScHeight ->{
                mlpMargins.bottom = responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_lpMarginBottomResPerScHeight)
            }
            R.styleable.JJSearchBarStatic_lpMarginPercentScHeight -> {
                mlpMargins = JJMargin.all(JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_lpMarginPercentScHeight,0f)))
            }
            R.styleable.JJSearchBarStatic_lpMarginPercentScWidth -> {
                mlpMargins = JJMargin.all(JJScreen.percentWidth( a.getFloat(R.styleable.JJSearchBarStatic_lpMarginPercentScWidth,0f)))
            }
            R.styleable.JJSearchBarStatic_lpMarginResponsive -> {
                mlpMargins = JJMargin.all(responsiveSizeDimension(a, R.styleable.JJSearchBarStatic_lpMarginResponsive))
            }
            R.styleable.JJSearchBarStatic_lpMarginResPerScHeight -> {
                mlpMargins = JJMargin.all(responsiveSizePercentScreenHeight(a, R.styleable.JJSearchBarStatic_lpMarginResPerScHeight))
            }
            R.styleable.JJSearchBarStatic_lpMarginResPerScWidth -> {
                mlpMargins =  JJMargin.all(responsiveSizePercentScreenWidth(a, R.styleable.JJSearchBarStatic_lpMarginResPerScWidth))
            }
            R.styleable.JJSearchBarStatic_lpMarginVerticalPerScHeight -> {
                val mar = JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_lpMarginVerticalPerScHeight,0f))
                mlpMargins.top = mar ; mlpMargins.bottom = mar
            }
            R.styleable.JJSearchBarStatic_lpMarginVerticalPerScWidth ->{
                val mar = JJScreen.percentWidth(a.getFloat(R.styleable.JJSearchBarStatic_lpMarginVerticalPerScWidth,0f))
                mlpMargins.top = mar ; mlpMargins.bottom = mar
            }
            R.styleable.JJSearchBarStatic_lpMarginVerticalResponsive -> {
                val mar = responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_lpMarginVerticalResponsive)
                mlpMargins.top = mar ; mlpMargins.bottom = mar
            }
            R.styleable.JJSearchBarStatic_lpMarginVerticalResPerScWidth -> {
                val mar = responsiveSizePercentScreenWidth(a,R.styleable.JJSearchBarStatic_lpMarginVerticalResPerScWidth )
                mlpMargins.top = mar ; mlpMargins.bottom = mar
            }
            R.styleable.JJSearchBarStatic_lpMarginVerticalResPerScHeight -> {
                val mar = responsiveSizePercentScreenHeight(a, R.styleable.JJSearchBarStatic_lpMarginVerticalResPerScHeight)
                mlpMargins.top = mar ; mlpMargins.bottom = mar
            }
            R.styleable.JJSearchBarStatic_lpMarginHorizontalPerScHeight -> {
                val mar = JJScreen.percentHeight( a.getFloat(R.styleable.JJSearchBarStatic_lpMarginHorizontalPerScHeight,0f))
                mlpMargins.left = mar ; mlpMargins.right = mar
            }
            R.styleable.JJSearchBarStatic_lpMarginHorizontalPerScWidth -> {
                val mar = JJScreen.percentWidth(a.getFloat(R.styleable.JJSearchBarStatic_lpMarginHorizontalPerScWidth,0f))
                mlpMargins.left = mar ; mlpMargins.right = mar
            }
            R.styleable.JJSearchBarStatic_lpMarginHorizontalResponsive -> {
                val mar = responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_lpMarginHorizontalResponsive)
                mlpMargins.left = mar ; mlpMargins.right = mar
            }
            R.styleable.JJSearchBarStatic_lpMarginHorizontalResPerScWidth -> {
                val mar = responsiveSizePercentScreenWidth(a,R.styleable.JJSearchBarStatic_lpMarginHorizontalResPerScWidth)
                mlpMargins.left = mar ; mlpMargins.right = mar
            }
            R.styleable.JJSearchBarStatic_lpMarginHorizontalResPerScHeight -> {
                val mar = responsiveSizePercentScreenHeight(a, R.styleable.JJSearchBarStatic_lpMarginHorizontalResPerScHeight)
                mlpMargins.left = mar ; mlpMargins.right = mar
            }
        }

    }
    private fun setupPaddingLp(a:TypedArray,index:Int){
        when(a.getIndex(index)){
            R.styleable.JJSearchBarStatic_lpPaddingTopPerScHeight -> {
                mlpPadding.top = JJScreen.percentHeight( a.getFloat(R.styleable.JJSearchBarStatic_lpPaddingTopPerScHeight,0f))
            }
            R.styleable.JJSearchBarStatic_lpPaddingLeftPerScHeight -> {
                mlpPadding.left = JJScreen.percentHeight( a.getFloat(R.styleable.JJSearchBarStatic_lpPaddingLeftPerScHeight,0f))
            }
            R.styleable.JJSearchBarStatic_lpPaddingRightPerScHeight -> {
                mlpPadding.right = JJScreen.percentHeight( a.getFloat(R.styleable.JJSearchBarStatic_lpPaddingRightPerScHeight,0f))
            }
            R.styleable.JJSearchBarStatic_lpPaddingBottomPerScHeight -> {
                mlpPadding.bottom = JJScreen.percentHeight( a.getFloat(R.styleable.JJSearchBarStatic_lpPaddingBottomPerScHeight,0f))
            }
            R.styleable.JJSearchBarStatic_lpPaddingTopPerScWidth -> {
                mlpPadding.top = JJScreen.percentWidth( a.getFloat(R.styleable.JJSearchBarStatic_lpPaddingTopPerScWidth,0f))
            }
            R.styleable.JJSearchBarStatic_lpPaddingLeftPerScWidth -> {
                mlpPadding.left = JJScreen.percentWidth( a.getFloat(R.styleable.JJSearchBarStatic_lpPaddingLeftPerScWidth,0f))
            }
            R.styleable.JJSearchBarStatic_lpPaddingRightPerScWidth -> {
                mlpPadding.right = JJScreen.percentWidth( a.getFloat(R.styleable.JJSearchBarStatic_lpPaddingRightPerScWidth,0f))
            }
            R.styleable.JJSearchBarStatic_lpPaddingBottomPerScWidth -> {
                mlpPadding.bottom = JJScreen.percentWidth( a.getFloat(R.styleable.JJSearchBarStatic_lpPaddingBottomPerScWidth,0f))
            }
            R.styleable.JJSearchBarStatic_lpPaddingTopResponsive -> {
                mlpPadding.top = responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_lpPaddingTopResponsive)
            }
            R.styleable.JJSearchBarStatic_lpPaddingLeftResponsive -> {
                mlpPadding.left = responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_lpPaddingLeftResponsive)
            }
            R.styleable.JJSearchBarStatic_lpPaddingRightResponsive -> {
                mlpPadding.right = responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_lpPaddingRightResponsive)
            }
            R.styleable.JJSearchBarStatic_lpPaddingBottomResponsive -> {
                mlpPadding.bottom = responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_lpPaddingBottomResponsive)
            }
            R.styleable.JJSearchBarStatic_lpPaddingTopResPerScWidth -> {
                mlpPadding.top = responsiveSizePercentScreenWidth(a,R.styleable.JJSearchBarStatic_lpPaddingTopResPerScWidth )
            }
            R.styleable.JJSearchBarStatic_lpPaddingLeftResPerScWidth -> {
                mlpPadding.left = responsiveSizePercentScreenWidth(a,R.styleable.JJSearchBarStatic_lpPaddingLeftResPerScWidth )
            }
            R.styleable.JJSearchBarStatic_lpPaddingRightResPerScWidth -> {
                mlpPadding.right = responsiveSizePercentScreenWidth(a,R.styleable.JJSearchBarStatic_lpPaddingRightResPerScWidth )
            }
            R.styleable.JJSearchBarStatic_lpPaddingBottomResPerScWidth -> {
                mlpPadding.bottom = responsiveSizePercentScreenWidth(a,R.styleable.JJSearchBarStatic_lpPaddingBottomResPerScWidth )
            }

            R.styleable.JJSearchBarStatic_lpPaddingTopResPerScHeight -> {
                mlpPadding.top = responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_lpPaddingTopResPerScHeight )
            }
            R.styleable.JJSearchBarStatic_lpPaddingLeftResPerScHeight -> {
                mlpPadding.left = responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_lpPaddingLeftResPerScHeight )
            }
            R.styleable.JJSearchBarStatic_lpPaddingRightResPerScHeight -> {
                mlpPadding.right = responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_lpPaddingRightResPerScHeight )
            }
            R.styleable.JJSearchBarStatic_lpPaddingBottomResPerScHeight -> {
                mlpPadding.bottom = responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_lpPaddingBottomResPerScHeight )
            }
            R.styleable.JJSearchBarStatic_lpPaddingPercentScHeight -> {
                mlpPadding = JJPadding.all(JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_lpPaddingPercentScHeight,0f)))
            }
            R.styleable.JJSearchBarStatic_lpPaddingPercentScWidth -> {
                mlpPadding = JJPadding.all(JJScreen.percentWidth(a.getFloat(R.styleable.JJSearchBarStatic_lpPaddingPercentScWidth,0f)))
            }
            R.styleable.JJSearchBarStatic_lpPaddingResponsive -> {
                mlpPadding = JJPadding.all(responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_lpPaddingResponsive))
            }
            R.styleable.JJSearchBarStatic_lpPaddingResPerScHeight -> {
                mlpPadding = JJPadding.all(responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_lpPaddingResPerScHeight))
            }
            R.styleable.JJSearchBarStatic_lpPaddingResPerScWidth -> {
                mlpPadding = JJPadding.all(responsiveSizePercentScreenWidth(a,R.styleable.JJSearchBarStatic_lpPaddingResPerScWidth))
            }
            R.styleable.JJSearchBarStatic_lpPaddingVerticalPerScHeight -> {
                val mar = JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_lpPaddingVerticalPerScHeight,0f))
                mlpPadding.top = mar ; mlpPadding.bottom = mar
            }
            R.styleable.JJSearchBarStatic_lpPaddingVerticalPerScWidth -> {
                val mar = JJScreen.percentWidth(a.getFloat(R.styleable.JJSearchBarStatic_lpPaddingVerticalPerScWidth,0f))
                mlpPadding.top = mar ; mlpPadding.bottom = mar
            }
            R.styleable.JJSearchBarStatic_lpPaddingVerticalResponsive -> {
                val mar = responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_lpPaddingVerticalResponsive)
                mlpPadding.top = mar ; mlpPadding.bottom = mar
            }
            R.styleable.JJSearchBarStatic_lpPaddingVerticalResPerScWidth -> {
                val mar = responsiveSizePercentScreenWidth(a,R.styleable.JJSearchBarStatic_lpPaddingVerticalResPerScWidth)
                mlpPadding.top = mar ; mlpPadding.bottom = mar
            }
            R.styleable.JJSearchBarStatic_lpPaddingVerticalResPerScHeight -> {
                val mar = responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_lpPaddingVerticalResPerScHeight)
                mlpPadding.top = mar ; mlpPadding.bottom = mar
            }
            R.styleable.JJSearchBarStatic_lpPaddingHorizontalPerScHeight -> {
                val mar = JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_lpPaddingHorizontalPerScHeight,0f))
                mlpPadding.left = mar ; mlpPadding.right = mar
            }
            R.styleable.JJSearchBarStatic_lpPaddingHorizontalPerScWidth -> {
                val mar = JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_lpPaddingHorizontalPerScWidth,0f))
                mlpPadding.left = mar ; mlpPadding.right = mar
            }
            R.styleable.JJSearchBarStatic_lpPaddingHorizontalResponsive -> {
                val mar = responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_lpPaddingHorizontalResponsive)
                mlpPadding.left = mar ; mlpPadding.right = mar
            }
            R.styleable.JJSearchBarStatic_lpPaddingHorizontalResPerScWidth ->{
                val mar = responsiveSizePercentScreenWidth(a,R.styleable.JJSearchBarStatic_lpPaddingHorizontalResPerScWidth)
                mlpPadding.left = mar ; mlpPadding.right = mar
            }
            R.styleable.JJSearchBarStatic_lpPaddingHorizontalResPerScHeight ->{
                val mar = responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_lpPaddingHorizontalResPerScHeight)
                mlpPadding.left = mar ; mlpPadding.right = mar
            }

        }

    }

    private fun setupSizeCl(a:TypedArray,index:Int){
        when(a.getIndex(index)){
            R.styleable.JJSearchBarStatic_clHeightPercent -> {
                clPercentHeight( a.getFloat(R.styleable.JJSearchBarStatic_clHeightPercent,0f))
            }
            R.styleable.JJSearchBarStatic_clWidthPercent -> {
                clPercentWidth( a.getFloat(R.styleable.JJSearchBarStatic_clWidthPercent,0f))
            }
            R.styleable.JJSearchBarStatic_clHeightPercentScreenWidth -> {
                clHeight(JJScreen.percentWidth(a.getFloat(R.styleable.JJSearchBarStatic_clHeightPercentScreenWidth,0f)))
            }
            R.styleable.JJSearchBarStatic_clWidthPercentScreenWidth -> {
                clWidth(JJScreen.percentWidth(a.getFloat(R.styleable.JJSearchBarStatic_clWidthPercentScreenWidth,0f)))
            }

            R.styleable.JJSearchBarStatic_clHeightPercentScreenHeight -> {
                clHeight(JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_clHeightPercentScreenHeight,0f)))
            }
            R.styleable.JJSearchBarStatic_clWidthPercentScreenHeight -> {
                clWidth(JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_clWidthPercentScreenHeight,0f)))
            }
            R.styleable.JJSearchBarStatic_clHeightResponsive -> {
                clHeight(responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_clHeightResponsive))
            }
            R.styleable.JJSearchBarStatic_clWidthResponsive -> {
                clWidth(responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_clWidthResponsive))
            }
            R.styleable.JJSearchBarStatic_clHeightResponsivePercentScreenHeight ->{
                clHeight(responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_clHeightResponsivePercentScreenHeight))
            }
            R.styleable.JJSearchBarStatic_clWidthResponsivePercentScreenHeight ->{
                clWidth(responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_clWidthResponsivePercentScreenHeight))
            }

            R.styleable.JJSearchBarStatic_clHeightResponsivePercentScreenWidth ->{
                clHeight(responsiveSizePercentScreenWidth(a,R.styleable.JJSearchBarStatic_clHeightResponsivePercentScreenWidth))
            }
            R.styleable.JJSearchBarStatic_clWidthResponsivePercentScreenWidth ->{
                clWidth(responsiveSizePercentScreenWidth(a,R.styleable.JJSearchBarStatic_clWidthResponsivePercentScreenWidth))
            }
        }




    }
    private fun setupAnchorsCl(a: TypedArray,index:Int){
        when(a.getIndex(index)){
            R.styleable.JJSearchBarStatic_clFillParent -> {
                if(a.getBoolean(R.styleable.JJSearchBarStatic_clFillParent,false)) clFillParent()
            }
            R.styleable.JJSearchBarStatic_clFillParentHorizontally -> {
                if(a.getBoolean(R.styleable.JJSearchBarStatic_clFillParentHorizontally,false)) clFillParentHorizontally()
            }
            R.styleable.JJSearchBarStatic_clFillParentVertically -> {
                if(a.getBoolean(R.styleable.JJSearchBarStatic_clFillParentVertically,false)) clFillParentVertically()
            }
            R.styleable.JJSearchBarStatic_clCenterInParent -> {
                if(a.getBoolean(R.styleable.JJSearchBarStatic_clCenterInParent,false)) clCenterInParent()
            }
            R.styleable.JJSearchBarStatic_clCenterInParentHorizontally -> {
                if(a.getBoolean(R.styleable.JJSearchBarStatic_clCenterInParentHorizontally,false)) clCenterInParentHorizontally()
            }
            R.styleable.JJSearchBarStatic_clCenterInParentVertically -> {
                if(a.getBoolean(R.styleable.JJSearchBarStatic_clCenterInParentVertically,false)) clCenterInParentVertically()
            }
            R.styleable.JJSearchBarStatic_clCenterInParentTopVertically -> {
                if(a.getBoolean(R.styleable.JJSearchBarStatic_clCenterInParentTopVertically,false))  clCenterInParentTopVertically()
            }
            R.styleable.JJSearchBarStatic_clCenterInParentBottomVertically -> {
                if(a.getBoolean(R.styleable.JJSearchBarStatic_clCenterInParentBottomVertically,false)) clCenterInParentBottomVertically()
            }
            R.styleable.JJSearchBarStatic_clCenterInParentStartHorizontally -> {
                if(a.getBoolean(R.styleable.JJSearchBarStatic_clCenterInParentStartHorizontally,false)) clCenterInParentStartHorizontally()
            }
            R.styleable.JJSearchBarStatic_clCenterInParentEndHorizontally -> {
                if(a.getBoolean(R.styleable.JJSearchBarStatic_clCenterInParentEndHorizontally,false)) clCenterInParentEndHorizontally()
            }

            R.styleable.JJSearchBarStatic_clCenterInTopVerticallyOf -> {
                clCenterInTopVertically(a.getResourceId(R.styleable.JJSearchBarStatic_clCenterInTopVerticallyOf,
                    View.NO_ID))
            }
            R.styleable.JJSearchBarStatic_clCenterInBottomVerticallyOf -> {
                clCenterInBottomVertically(a.getResourceId(R.styleable.JJSearchBarStatic_clCenterInBottomVerticallyOf,
                    View.NO_ID))
            }
            R.styleable.JJSearchBarStatic_clCenterInStartHorizontallyOf -> {
                clCenterInStartHorizontally(a.getResourceId(R.styleable.JJSearchBarStatic_clCenterInStartHorizontallyOf,
                    View.NO_ID))
            }
            R.styleable.JJSearchBarStatic_clCenterInEndHorizontallyOf -> {
                clCenterInEndHorizontally(a.getResourceId(R.styleable.JJSearchBarStatic_clCenterInEndHorizontallyOf,
                    View.NO_ID))
            }
            R.styleable.JJSearchBarStatic_clCenterVerticallyOf -> {
                clCenterVerticallyOf(a.getResourceId(R.styleable.JJSearchBarStatic_clCenterVerticallyOf,
                    View.NO_ID))
            }
            R.styleable.JJSearchBarStatic_clCenterHorizontallyOf -> {
                clCenterHorizontallyOf(a.getResourceId(R.styleable.JJSearchBarStatic_clCenterHorizontallyOf,
                    View.NO_ID))
            }
            R.styleable.JJSearchBarStatic_clVerticalBias -> {
                clVerticalBias(a.getFloat(R.styleable.JJSearchBarStatic_clVerticalBias,0.5f))
            }
            R.styleable.JJSearchBarStatic_clHorizontalBias -> {
                clHorizontalBias( a.getFloat(R.styleable.JJSearchBarStatic_clHorizontalBias,0.5f))
            }
            R.styleable.JJSearchBarStatic_clStartToStartParent -> {
                if(a.getBoolean(R.styleable.JJSearchBarStatic_clStartToStartParent,false)) clStartToStartParent()
            }
            R.styleable.JJSearchBarStatic_clStartToEndParent -> {
                if(a.getBoolean(R.styleable.JJSearchBarStatic_clStartToEndParent,false)) clStartToEndParent()
            }
            R.styleable.JJSearchBarStatic_clEndToEndParent -> {
                if(a.getBoolean(R.styleable.JJSearchBarStatic_clEndToEndParent,false)) clEndToEndParent()
            }
            R.styleable.JJSearchBarStatic_clEndToStartParent -> {
                if(a.getBoolean(R.styleable.JJSearchBarStatic_clEndToStartParent,false)) clEndToStartParent()
            }
            R.styleable.JJSearchBarStatic_clTopToTopParent -> {
                if(a.getBoolean(R.styleable.JJSearchBarStatic_clTopToTopParent,false)) clTopToTopParent()
            }
            R.styleable.JJSearchBarStatic_clTopToBottomParent -> {
                if(a.getBoolean(R.styleable.JJSearchBarStatic_clTopToBottomParent,false)) clTopToBottomParent()
            }
            R.styleable.JJSearchBarStatic_clBottomToBottomParent -> {
                if(a.getBoolean(R.styleable.JJSearchBarStatic_clBottomToBottomParent,false)) clBottomToBottomParent()
            }
            R.styleable.JJSearchBarStatic_clBottomToTopParent -> {
                if(a.getBoolean(R.styleable.JJSearchBarStatic_clBottomToTopParent,false)) clBottomToTopParent()
            }

            R.styleable.JJSearchBarStatic_clStartToStartOf -> {
                clStartToStart(a.getResourceId(R.styleable.JJSearchBarStatic_clStartToStartOf, View.NO_ID))
            }
            R.styleable.JJSearchBarStatic_clStartToEndOf -> {
                clStartToEnd(a.getResourceId(R.styleable.JJSearchBarStatic_clStartToEndOf, View.NO_ID))
            }
            R.styleable.JJSearchBarStatic_clEndToEndOf -> {
                clEndToEnd(a.getResourceId(R.styleable.JJSearchBarStatic_clEndToEndOf, View.NO_ID))
            }
            R.styleable.JJSearchBarStatic_clEndToStartOf -> {
                clEndToStart(a.getResourceId(R.styleable.JJSearchBarStatic_clEndToStartOf, View.NO_ID))
            }
            R.styleable.JJSearchBarStatic_clTopToTopOf -> {
                clTopToTop(a.getResourceId(R.styleable.JJSearchBarStatic_clTopToTopOf, View.NO_ID))
            }
            R.styleable.JJSearchBarStatic_clTopToBottomOf -> {
                clTopToBottom(a.getResourceId(R.styleable.JJSearchBarStatic_clTopToBottomOf, View.NO_ID))
            }
            R.styleable.JJSearchBarStatic_clBottomToBottomOf -> {
                clBottomToBottom(a.getResourceId(R.styleable.JJSearchBarStatic_clBottomToBottomOf, View.NO_ID))
            }
            R.styleable.JJSearchBarStatic_clBottomToTopOf -> {
                clBottomToTop(a.getResourceId(R.styleable.JJSearchBarStatic_clBottomToTopOf, View.NO_ID))
            }

        }
    }
    private fun setupMarginCl(a: TypedArray,index:Int){
        var margins = JJMargin()
        when(a.getIndex(index)){
            R.styleable.JJSearchBarStatic_clMarginEnd ->{
                margins.right = a.getDimension(R.styleable.JJSearchBarStatic_clMarginEnd,0f).toInt()
            }
            R.styleable.JJSearchBarStatic_clMarginStart ->{
                margins.left = a.getDimension(R.styleable.JJSearchBarStatic_clMarginStart,0f).toInt()
            }
            R.styleable.JJSearchBarStatic_clMarginTop ->{
                margins.top = a.getDimension(R.styleable.JJSearchBarStatic_clMarginTop,0f).toInt()
            }
            R.styleable.JJSearchBarStatic_clMarginBottom ->{
                margins.bottom = a.getDimension(R.styleable.JJSearchBarStatic_clMarginBottom,0f).toInt()
            }

            R.styleable.JJSearchBarStatic_clMarginEndPercentScreenHeight -> {
                margins.right = JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_clMarginEndPercentScreenHeight,0f))
            }
            R.styleable.JJSearchBarStatic_clMarginStartPercentScreenHeight -> {
                margins.left = JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_clMarginStartPercentScreenHeight,0f))
            }
            R.styleable.JJSearchBarStatic_clMarginTopPercentScreenHeight -> {
                margins.top = JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_clMarginTopPercentScreenHeight,0f))
            }
            R.styleable.JJSearchBarStatic_clMarginBottomPercentScreenHeight -> {
                margins.bottom = JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_clMarginBottomPercentScreenHeight,0f))
            }

            R.styleable.JJSearchBarStatic_clMarginEndPercentScreenWidth -> {
                margins.right = JJScreen.percentWidth(a.getFloat(R.styleable.JJSearchBarStatic_clMarginEndPercentScreenWidth,0f))
            }
            R.styleable.JJSearchBarStatic_clMarginStartPercentScreenWidth -> {
                margins.left = JJScreen.percentWidth(a.getFloat(R.styleable.JJSearchBarStatic_clMarginStartPercentScreenWidth,0f))
            }
            R.styleable.JJSearchBarStatic_clMarginTopPercentScreenWidth -> {
                margins.top = JJScreen.percentWidth(a.getFloat(R.styleable.JJSearchBarStatic_clMarginTopPercentScreenWidth,0f))
            }
            R.styleable.JJSearchBarStatic_clMarginBottomPercentScreenWidth -> {
                margins.bottom = JJScreen.percentWidth(a.getFloat(R.styleable.JJSearchBarStatic_clMarginBottomPercentScreenWidth,0f))
            }
            R.styleable.JJSearchBarStatic_clMargin -> {
                margins = JJMargin.all(a.getDimension(R.styleable.JJSearchBarStatic_clMargin,0f).toInt())
            }
            R.styleable.JJSearchBarStatic_clMarginPerScHeight -> {
                margins = JJMargin.all(JJScreen.percentHeight( a.getFloat(R.styleable.JJSearchBarStatic_clMarginPerScHeight,0f)))
            }
            R.styleable.JJSearchBarStatic_clMarginPerScWidth -> {
                margins = JJMargin.all(JJScreen.percentWidth( a.getFloat(R.styleable.JJSearchBarStatic_clMarginPerScWidth,0f)))
            }
            R.styleable.JJSearchBarStatic_clMarginResponsive -> {
                margins = JJMargin.all(responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_clMarginResponsive))
            }
            R.styleable.JJSearchBarStatic_clMarginResPerScHeight -> {
                margins = JJMargin.all(responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_clMarginResPerScHeight))
            }
            R.styleable.JJSearchBarStatic_clMarginResPerScWidth -> {
                margins = JJMargin.all(responsiveSizePercentScreenWidth(a,R.styleable.JJSearchBarStatic_clMarginResPerScWidth))
            }
            R.styleable.JJSearchBarStatic_clMarginEndResponsive -> {
                margins.right = responsiveSizeDimension(a, R.styleable.JJSearchBarStatic_clMarginEndResponsive)
            }
            R.styleable.JJSearchBarStatic_clMarginStartResponsive -> {
                margins.left = responsiveSizeDimension(a, R.styleable.JJSearchBarStatic_clMarginStartResponsive)
            }
            R.styleable.JJSearchBarStatic_clMarginTopResponsive -> {
                margins.top = responsiveSizeDimension(a, R.styleable.JJSearchBarStatic_clMarginTopResponsive)
            }
            R.styleable.JJSearchBarStatic_clMarginBottomResponsive -> {
                margins.bottom = responsiveSizeDimension(a, R.styleable.JJSearchBarStatic_clMarginBottomResponsive)
            }

            R.styleable.JJSearchBarStatic_clMarginEndResPerScHeight -> {
                margins.right = responsiveSizePercentScreenHeight(a, R.styleable.JJSearchBarStatic_clMarginEndResPerScHeight)
            }
            R.styleable.JJSearchBarStatic_clMarginStartResPerScHeight -> {
                margins.left = responsiveSizePercentScreenHeight(a, R.styleable.JJSearchBarStatic_clMarginStartResPerScHeight)
            }
            R.styleable.JJSearchBarStatic_clMarginTopResPerScHeight -> {
                margins.top = responsiveSizePercentScreenHeight(a, R.styleable.JJSearchBarStatic_clMarginTopResPerScHeight)
            }
            R.styleable.JJSearchBarStatic_clMarginBottomResPerScHeight -> {
                margins.bottom = responsiveSizePercentScreenHeight(a, R.styleable.JJSearchBarStatic_clMarginBottomResPerScHeight)
            }

            R.styleable.JJSearchBarStatic_clMarginEndResPerScWidth -> {
                margins.right = responsiveSizePercentScreenWidth(a, R.styleable.JJSearchBarStatic_clMarginEndResPerScWidth)
            }
            R.styleable.JJSearchBarStatic_clMarginStartResPerScWidth -> {
                margins.left = responsiveSizePercentScreenWidth(a, R.styleable.JJSearchBarStatic_clMarginStartResPerScWidth)
            }
            R.styleable.JJSearchBarStatic_clMarginTopResPerScWidth -> {
                margins.top = responsiveSizePercentScreenWidth(a, R.styleable.JJSearchBarStatic_clMarginTopResPerScWidth)
            }
            R.styleable.JJSearchBarStatic_clMarginBottomResPerScWidth -> {
                margins.bottom = responsiveSizePercentScreenWidth(a, R.styleable.JJSearchBarStatic_clMarginBottomResPerScWidth)
            }
            R.styleable.JJSearchBarStatic_clMarginVertical -> {
                val mar = a.getDimension(R.styleable.JJSearchBarStatic_clMarginVertical,0f).toInt()
                margins.top = mar ; margins.bottom = mar
            }
            R.styleable.JJSearchBarStatic_clMarginVerticalPerScHeight -> {
                val mar = JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_clMarginVerticalPerScHeight,0f))
                margins.top = mar ; margins.bottom = mar
            }
            R.styleable.JJSearchBarStatic_clMarginVerticalPerScWidth -> {
                val mar = JJScreen.percentWidth(a.getFloat(R.styleable.JJSearchBarStatic_clMarginVerticalPerScWidth,0f))
                margins.top = mar ; margins.bottom = mar
            }
            R.styleable.JJSearchBarStatic_clMarginVerticalResponsive -> {
                val mar = responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_clMarginVerticalResponsive)
                margins.top = mar ; margins.bottom = mar
            }
            R.styleable.JJSearchBarStatic_clMarginVerticalResPerScHeight -> {
                val mar = responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_clMarginVerticalResPerScHeight)
                margins.top = mar ; margins.bottom = mar
            }
            R.styleable.JJSearchBarStatic_clMarginVerticalResPerScWidth -> {
                val mar = responsiveSizePercentScreenWidth(a,R.styleable.JJSearchBarStatic_clMarginVerticalResPerScWidth)
                margins.top = mar ; margins.bottom = mar
            }

            R.styleable.JJSearchBarStatic_clMarginHorizontal -> {
                val mar = a.getDimension(R.styleable.JJSearchBarStatic_clMarginHorizontal,0f).toInt()
                margins.left = mar ; margins.right = mar
            }
            R.styleable.JJSearchBarStatic_clMarginHorizontalPerScHeight -> {
                val mar = JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_clMarginHorizontalPerScHeight,0f))
                margins.left = mar ; margins.right = mar
            }
            R.styleable.JJSearchBarStatic_clMarginHorizontalPerScWidth -> {
                val mar = JJScreen.percentWidth(a.getFloat(R.styleable.JJSearchBarStatic_clMarginHorizontalPerScWidth,0f))
                margins.left = mar ; margins.right = mar
            }
            R.styleable.JJSearchBarStatic_clMarginHorizontalResponsive -> {
                val mar = responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_clMarginHorizontalResponsive)
                margins.left = mar ; margins.right = mar
            }
            R.styleable.JJSearchBarStatic_clMarginHorizontalResPerScHeight -> {
                val mar = responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_clMarginHorizontalResPerScHeight)
                margins.left = mar ; margins.right = mar
            }
            R.styleable.JJSearchBarStatic_clMarginHorizontalResPerScWidth -> {
                val mar = responsiveSizePercentScreenWidth(a,R.styleable.JJSearchBarStatic_clMarginHorizontalResPerScWidth)
                margins.left = mar ; margins.right = mar
            }

        }
        clMargins(margins)
    }

    private fun setupMarginLpl(a: TypedArray,index:Int) {
        when (a.getIndex(index)) {
            R.styleable.JJSearchBarStatic_lplMargin -> {
                mlsMargins =
                    JJMargin.all(a.getDimension(R.styleable.JJSearchBarStatic_lplMargin, 0f).toInt())
            }
            R.styleable.JJSearchBarStatic_lplMarginVertical -> {
                val mar = a.getDimension(R.styleable.JJSearchBarStatic_lplMarginVertical, 0f).toInt()
                mlsMargins.top = mar; mlsMargins.bottom = mar
            }
            R.styleable.JJSearchBarStatic_lplMarginHorizontal -> {
                val mar =
                    a.getDimension(R.styleable.JJSearchBarStatic_lplMarginHorizontal, 0f).toInt()
                mlsMargins.left = mar; mlsMargins.right = mar
            }

            R.styleable.JJSearchBarStatic_lplMarginStart -> {
                mlsMargins.left =
                    a.getDimension(R.styleable.JJSearchBarStatic_lplMarginStart, 0f).toInt()
            }
            R.styleable.JJSearchBarStatic_lplMarginEnd -> {
                mlsMargins.right =
                    a.getDimension(R.styleable.JJSearchBarStatic_lplMarginEnd, 0f).toInt()
            }
            R.styleable.JJSearchBarStatic_lplMarginBottom -> {
                mlsMargins.bottom =
                    a.getDimension(R.styleable.JJSearchBarStatic_lplMarginBottom, 0f).toInt()
            }
            R.styleable.JJSearchBarStatic_lplMarginTop -> {
                mlsMargins.top =
                    a.getDimension(R.styleable.JJSearchBarStatic_lplMarginTop, 0f).toInt()
            }

            R.styleable.JJSearchBarStatic_lplMarginLeftPerScHeight -> {
                mlsMargins.left = JJScreen.percentHeight(
                    a.getFloat(
                        R.styleable.JJSearchBarStatic_lplMarginLeftPerScHeight,
                        0f
                    )
                )
            }
            R.styleable.JJSearchBarStatic_lplMarginRightPerScHeight -> {
                mlsMargins.right = JJScreen.percentHeight(
                    a.getFloat(
                        R.styleable.JJSearchBarStatic_lplMarginRightPerScHeight,
                        0f
                    )
                )
            }
            R.styleable.JJSearchBarStatic_lplMarginBottomPerScHeight -> {
                mlsMargins.bottom = JJScreen.percentHeight(
                    a.getFloat(
                        R.styleable.JJSearchBarStatic_lplMarginBottomPerScHeight,
                        0f
                    )
                )
            }
            R.styleable.JJSearchBarStatic_lplMarginTopPerScHeight -> {
                mlsMargins.top = JJScreen.percentHeight(
                    a.getFloat(
                        R.styleable.JJSearchBarStatic_lplMarginTopPerScHeight,
                        0f
                    )
                )
            }

            R.styleable.JJSearchBarStatic_lplMarginLeftPerScWidth -> {
                mlsMargins.left = JJScreen.percentWidth(
                    a.getFloat(
                        R.styleable.JJSearchBarStatic_lplMarginLeftPerScWidth,
                        0f
                    )
                )
            }
            R.styleable.JJSearchBarStatic_lplMarginRightPerScWidth -> {
                mlsMargins.right = JJScreen.percentWidth(
                    a.getFloat(
                        R.styleable.JJSearchBarStatic_lplMarginRightPerScWidth,
                        0f
                    )
                )
            }
            R.styleable.JJSearchBarStatic_lplMarginBottomPerScWidth -> {
                mlsMargins.bottom = JJScreen.percentWidth(
                    a.getFloat(
                        R.styleable.JJSearchBarStatic_lplMarginBottomPerScWidth,
                        0f
                    )
                )
            }
            R.styleable.JJSearchBarStatic_lplMarginTopPerScWidth -> {
                mlsMargins.top = JJScreen.percentWidth(
                    a.getFloat(
                        R.styleable.JJSearchBarStatic_lplMarginTopPerScWidth,
                        0f
                    )
                )
            }

            R.styleable.JJSearchBarStatic_lplMarginTopResponsive -> {
                mlsMargins.top =
                    responsiveSizeDimension(a, R.styleable.JJSearchBarStatic_lplMarginTopResponsive)
            }
            R.styleable.JJSearchBarStatic_lplMarginLeftResponsive -> {
                mlsMargins.left =
                    responsiveSizeDimension(a, R.styleable.JJSearchBarStatic_lplMarginLeftResponsive)
            }
            R.styleable.JJSearchBarStatic_lplMarginRightResponsive -> {
                mlsMargins.right = responsiveSizeDimension(
                    a,
                    R.styleable.JJSearchBarStatic_lplMarginRightResponsive
                )
            }
            R.styleable.JJSearchBarStatic_lplMarginBottomResponsive -> {
                mlsMargins.bottom = responsiveSizeDimension(
                    a,
                    R.styleable.JJSearchBarStatic_lplMarginBottomResponsive
                )
            }

            R.styleable.JJSearchBarStatic_lplMarginTopResPerScWidth -> {
                mlsMargins.top = responsiveSizePercentScreenWidth(
                    a,
                    R.styleable.JJSearchBarStatic_lplMarginTopResPerScWidth
                )
            }
            R.styleable.JJSearchBarStatic_lplMarginLeftResPerScWidth -> {
                mlsMargins.left = responsiveSizePercentScreenWidth(
                    a,
                    R.styleable.JJSearchBarStatic_lplMarginLeftResPerScWidth
                )
            }
            R.styleable.JJSearchBarStatic_lplMarginRightResPerScWidth -> {
                mlsMargins.right = responsiveSizePercentScreenWidth(
                    a,
                    R.styleable.JJSearchBarStatic_lplMarginRightResPerScWidth
                )
            }
            R.styleable.JJSearchBarStatic_lplMarginBottomResPerScWidth -> {
                mlsMargins.bottom = responsiveSizePercentScreenWidth(
                    a,
                    R.styleable.JJSearchBarStatic_lplMarginBottomResPerScWidth
                )
            }

            R.styleable.JJSearchBarStatic_lplMarginTopResPerScHeight -> {
                mlsMargins.top = responsiveSizePercentScreenHeight(
                    a,
                    R.styleable.JJSearchBarStatic_lplMarginTopResPerScHeight
                )
            }
            R.styleable.JJSearchBarStatic_lplMarginLeftResPerScHeight -> {
                mlsMargins.left = responsiveSizePercentScreenHeight(
                    a,
                    R.styleable.JJSearchBarStatic_lplMarginLeftResPerScHeight
                )
            }
            R.styleable.JJSearchBarStatic_lplMarginRightResPerScHeight -> {
                mlsMargins.right = responsiveSizePercentScreenHeight(
                    a,
                    R.styleable.JJSearchBarStatic_lplMarginRightResPerScHeight
                )
            }
            R.styleable.JJSearchBarStatic_lplMarginBottomResPerScHeight -> {
                mlsMargins.bottom = responsiveSizePercentScreenHeight(
                    a,
                    R.styleable.JJSearchBarStatic_lplMarginBottomResPerScHeight
                )
            }
            R.styleable.JJSearchBarStatic_lplMarginPercentScHeight -> {
                mlsMargins = JJMargin.all(
                    JJScreen.percentHeight(
                        a.getFloat(
                            R.styleable.JJSearchBarStatic_lplMarginPercentScHeight,
                            0f
                        )
                    )
                )
            }
            R.styleable.JJSearchBarStatic_lplMarginPercentScWidth -> {
                mlsMargins = JJMargin.all(
                    JJScreen.percentWidth(
                        a.getFloat(
                            R.styleable.JJSearchBarStatic_lplMarginPercentScWidth,
                            0f
                        )
                    )
                )
            }
            R.styleable.JJSearchBarStatic_lplMarginResponsive -> {
                mlsMargins = JJMargin.all(
                    responsiveSizeDimension(
                        a,
                        R.styleable.JJSearchBarStatic_lplMarginResponsive
                    )
                )
            }
            R.styleable.JJSearchBarStatic_lplMarginResPerScHeight -> {
                mlsMargins = JJMargin.all(
                    responsiveSizePercentScreenHeight(
                        a,
                        R.styleable.JJSearchBarStatic_lplMarginResPerScHeight
                    )
                )
            }
            R.styleable.JJSearchBarStatic_lplMarginResPerScWidth -> {
                mlsMargins = JJMargin.all(
                    responsiveSizePercentScreenWidth(
                        a,
                        R.styleable.JJSearchBarStatic_lplMarginResPerScWidth
                    )
                )
            }

            R.styleable.JJSearchBarStatic_lplMarginVerticalPerScHeight -> {
                val mar = JJScreen.percentHeight(
                    a.getFloat(
                        R.styleable.JJSearchBarStatic_lplMarginVerticalPerScHeight,
                        0f
                    )
                )
                mlsMargins.top = mar; mlsMargins.bottom = mar
            }
            R.styleable.JJSearchBarStatic_lplMarginVerticalPerScWidth -> {
                val mar = JJScreen.percentWidth(
                    a.getFloat(
                        R.styleable.JJSearchBarStatic_lplMarginVerticalPerScWidth,
                        0f
                    )
                )
                mlsMargins.top = mar; mlsMargins.bottom = mar
            }
            R.styleable.JJSearchBarStatic_lplMarginVerticalResponsive -> {
                val mar = responsiveSizeDimension(
                    a,
                    R.styleable.JJSearchBarStatic_lplMarginVerticalResponsive
                )
                mlsMargins.top = mar; mlsMargins.bottom = mar
            }
            R.styleable.JJSearchBarStatic_lplMarginVerticalResPerScWidth -> {
                val mar = responsiveSizePercentScreenWidth(
                    a,
                    R.styleable.JJSearchBarStatic_lplMarginVerticalResPerScWidth
                )
                mlsMargins.top = mar; mlsMargins.bottom = mar
            }
            R.styleable.JJSearchBarStatic_lplMarginVerticalResPerScHeight -> {
                val mar = responsiveSizePercentScreenHeight(
                    a,
                    R.styleable.JJSearchBarStatic_lplMarginVerticalResPerScHeight
                )
                mlsMargins.top = mar; mlsMargins.bottom = mar
            }


            R.styleable.JJSearchBarStatic_lplMarginHorizontalPerScHeight -> {
                val mar = JJScreen.percentHeight(
                    a.getFloat(
                        R.styleable.JJSearchBarStatic_lplMarginHorizontalPerScHeight,
                        0f
                    )
                )
                mlsMargins.left = mar; mlsMargins.right = mar
            }
            R.styleable.JJSearchBarStatic_lplMarginHorizontalPerScWidth -> {
                val mar = JJScreen.percentWidth(
                    a.getFloat(
                        R.styleable.JJSearchBarStatic_lplMarginHorizontalPerScWidth,
                        0f
                    )
                )
                mlsMargins.left = mar; mlsMargins.right = mar
            }
            R.styleable.JJSearchBarStatic_lplMarginHorizontalResponsive -> {
                val mar = responsiveSizeDimension(
                    a,
                    R.styleable.JJSearchBarStatic_lplMarginHorizontalResponsive
                )
                mlsMargins.left = mar; mlsMargins.right = mar
            }
            R.styleable.JJSearchBarStatic_lplMarginHorizontalResPerScWidth -> {
                val mar = responsiveSizePercentScreenWidth(
                    a,
                    R.styleable.JJSearchBarStatic_lplMarginHorizontalResPerScWidth
                )
                mlsMargins.left = mar; mlsMargins.right = mar
            }
            R.styleable.JJSearchBarStatic_lplMarginHorizontalResPerScHeight -> {
                val mar = responsiveSizePercentScreenHeight(
                    a,
                    R.styleable.JJSearchBarStatic_lplMarginHorizontalResPerScHeight
                )
                mlsMargins.left = mar; mlsMargins.right = mar
            }

        }
    }
    private fun setupPaddingLpl(a: TypedArray,index: Int){
        when(a.getIndex(index)){
            R.styleable.JJSearchBarStatic_lplPadding -> {
                mlsPadding = JJPadding.all( a.getDimension(R.styleable.JJSearchBarStatic_lplPadding,0f).toInt())
            }
            R.styleable.JJSearchBarStatic_lplPaddingVertical -> {
               val mar = a.getDimension(R.styleable.JJSearchBarStatic_lplPaddingVertical,0f).toInt()
                mlsPadding.top = mar ; mlsPadding.bottom = mar
            }
            R.styleable.JJSearchBarStatic_lplPaddingHorizontal -> {
                val mar = a.getDimension(R.styleable.JJSearchBarStatic_lplPaddingHorizontal,0f).toInt()
                mlsPadding.left = mar ; mlsPadding.right = mar
            }
            R.styleable.JJSearchBarStatic_lplPaddingStart -> {
                mlsPadding.left = a.getDimension(R.styleable.JJSearchBarStatic_lplPaddingStart,0f).toInt()
            }
            R.styleable.JJSearchBarStatic_lplPaddingEnd -> {
                mlsPadding.right = a.getDimension(R.styleable.JJSearchBarStatic_lplPaddingEnd,0f).toInt()
            }
            R.styleable.JJSearchBarStatic_lplPaddingTop -> {
                mlsPadding.top = a.getDimension(R.styleable.JJSearchBarStatic_lplPaddingTop,0f).toInt()
            }
            R.styleable.JJSearchBarStatic_lplPaddingBottom -> {
                mlsPadding.bottom = a.getDimension(R.styleable.JJSearchBarStatic_lplPaddingBottom,0f).toInt()
            }

            R.styleable.JJSearchBarStatic_lplPaddingTopPerScHeight -> {
                mlsPadding.top = JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_lplPaddingTopPerScHeight,0f))
            }
            R.styleable.JJSearchBarStatic_lplPaddingLeftPerScHeight -> {
                mlsPadding.left = JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_lplPaddingLeftPerScHeight,0f))
            }
            R.styleable.JJSearchBarStatic_lplPaddingRightPerScHeight -> {
                mlsPadding.right = JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_lplPaddingRightPerScHeight,0f))
            }
            R.styleable.JJSearchBarStatic_lplPaddingBottomPerScHeight -> {
                mlsPadding.bottom = JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_lplPaddingBottomPerScHeight,0f))
            }

            R.styleable.JJSearchBarStatic_lplPaddingTopPerScWidth -> {
                mlsPadding.top = JJScreen.percentWidth(a.getFloat(R.styleable.JJSearchBarStatic_lplPaddingTopPerScWidth,0f))
            }
            R.styleable.JJSearchBarStatic_lplPaddingLeftPerScWidth -> {
                mlsPadding.left = JJScreen.percentWidth(a.getFloat(R.styleable.JJSearchBarStatic_lplPaddingLeftPerScWidth,0f))
            }
            R.styleable.JJSearchBarStatic_lplPaddingRightPerScWidth -> {
                mlsPadding.right = JJScreen.percentWidth(a.getFloat(R.styleable.JJSearchBarStatic_lplPaddingRightPerScWidth,0f))
            }
            R.styleable.JJSearchBarStatic_lplPaddingBottomPerScWidth -> {
                mlsPadding.bottom = JJScreen.percentWidth(a.getFloat(R.styleable.JJSearchBarStatic_lplPaddingBottomPerScWidth,0f))
            }

            R.styleable.JJSearchBarStatic_lplPaddingTopResponsive -> {
                mlsPadding.top = responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_lplPaddingTopResponsive)
            }
            R.styleable.JJSearchBarStatic_lplPaddingLeftResponsive -> {
                mlsPadding.left = responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_lplPaddingLeftResponsive)
            }
            R.styleable.JJSearchBarStatic_lplPaddingRightResponsive -> {
                mlsPadding.right = responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_lplPaddingRightResponsive)
            }
            R.styleable.JJSearchBarStatic_lplPaddingBottomResponsive -> {
                mlsPadding.bottom = responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_lplPaddingBottomResponsive)
            }

            R.styleable.JJSearchBarStatic_lplPaddingTopResPerScWidth -> {
                mlsPadding.top = responsiveSizePercentScreenWidth(a,R.styleable.JJSearchBarStatic_lplPaddingTopResPerScWidth)
            }
            R.styleable.JJSearchBarStatic_lplPaddingLeftResPerScWidth -> {
                mlsPadding.left = responsiveSizePercentScreenWidth(a,R.styleable.JJSearchBarStatic_lplPaddingLeftResPerScWidth)
            }
            R.styleable.JJSearchBarStatic_lplPaddingRightResPerScWidth -> {
                mlsPadding.right = responsiveSizePercentScreenWidth(a,R.styleable.JJSearchBarStatic_lplPaddingRightResPerScWidth)
            }
            R.styleable.JJSearchBarStatic_lplPaddingBottomResPerScWidth -> {
                mlsPadding.bottom = responsiveSizePercentScreenWidth(a,R.styleable.JJSearchBarStatic_lplPaddingBottomResPerScWidth)
            }

            R.styleable.JJSearchBarStatic_lplPaddingTopResPerScHeight -> {
                mlsPadding.top = responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_lplPaddingTopResPerScHeight)
            }
            R.styleable.JJSearchBarStatic_lplPaddingLeftResPerScHeight -> {
                mlsPadding.left = responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_lplPaddingLeftResPerScHeight)
            }
            R.styleable.JJSearchBarStatic_lplPaddingRightResPerScHeight -> {
                mlsPadding.right = responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_lplPaddingRightResPerScHeight)
            }
            R.styleable.JJSearchBarStatic_lplPaddingBottomResPerScHeight -> {
                mlsPadding.bottom = responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_lplPaddingBottomResPerScHeight)
            }
            R.styleable.JJSearchBarStatic_lplPaddingPercentScHeight->{
                mlsPadding = JJPadding.all(JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_lplPaddingPercentScHeight,0f)))
            }
            R.styleable.JJSearchBarStatic_lplPaddingPercentScWidth->{
                mlsPadding = JJPadding.all(JJScreen.percentWidth(a.getFloat(R.styleable.JJSearchBarStatic_lplPaddingPercentScWidth,0f)))
            }
            R.styleable.JJSearchBarStatic_lplPaddingResponsive->{
                mlsPadding = JJPadding.all(responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_lplPaddingResponsive))
            }
            R.styleable.JJSearchBarStatic_lplPaddingResPerScHeight->{
                mlsPadding = JJPadding.all(responsiveSizePercentScreenHeight(a, R.styleable.JJSearchBarStatic_lplPaddingResPerScHeight))
            }
            R.styleable.JJSearchBarStatic_lplPaddingResPerScWidth->{
                mlsPadding = JJPadding.all(responsiveSizePercentScreenWidth(a, R.styleable.JJSearchBarStatic_lplPaddingResPerScWidth))
            }

            R.styleable.JJSearchBarStatic_lplPaddingVerticalPerScHeight -> {
                val mar = JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_lplPaddingVerticalPerScHeight,0f))
                mlsPadding.top = mar ; mlsPadding.bottom = mar
            }
            R.styleable.JJSearchBarStatic_lplPaddingVerticalPerScWidth -> {
                val mar = JJScreen.percentWidth(a.getFloat(R.styleable.JJSearchBarStatic_lplPaddingVerticalPerScWidth,0f))
                mlsPadding.top = mar ; mlsPadding.bottom = mar
            }
            R.styleable.JJSearchBarStatic_lplPaddingVerticalResponsive -> {
                val mar = responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_lplPaddingVerticalResponsive)
                mlsPadding.top = mar ; mlsPadding.bottom = mar
            }
            R.styleable.JJSearchBarStatic_lplPaddingVerticalResPerScWidth -> {
                val mar = responsiveSizePercentScreenWidth(a,R.styleable.JJSearchBarStatic_lplPaddingVerticalResPerScWidth)
                mlsPadding.top = mar ; mlsPadding.bottom = mar
            }
            R.styleable.JJSearchBarStatic_lplPaddingVerticalResPerScHeight -> {
                val mar = responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_lplPaddingVerticalResPerScHeight)
                mlsPadding.top = mar ; mlsPadding.bottom = mar
            }

            R.styleable.JJSearchBarStatic_lplPaddingHorizontalPerScHeight -> {
                val mar = JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_lplPaddingHorizontalPerScHeight,0f))
                mlsPadding.left = mar ; mlsPadding.right = mar
            }
            R.styleable.JJSearchBarStatic_lplPaddingHorizontalPerScWidth -> {
                val mar = JJScreen.percentWidth(a.getFloat(R.styleable.JJSearchBarStatic_lplPaddingHorizontalPerScWidth,0f))
                mlsPadding.left = mar ; mlsPadding.right = mar
            }
            R.styleable.JJSearchBarStatic_lplPaddingHorizontalResponsive -> {
                val mar = responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_lplPaddingHorizontalResponsive)
                mlsPadding.left = mar ; mlsPadding.right = mar
            }
            R.styleable.JJSearchBarStatic_lplPaddingHorizontalResPerScWidth -> {
                val mar = responsiveSizePercentScreenWidth(a,R.styleable.JJSearchBarStatic_lplPaddingHorizontalResPerScWidth)
                mlsPadding.left = mar ; mlsPadding.right = mar
            }
            R.styleable.JJSearchBarStatic_lplPaddingHorizontalResPerScHeight -> {
                val mar = responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_lplPaddingHorizontalResPerScHeight)
                mlsPadding.left = mar ; mlsPadding.right = mar
            }
        }
    }
    private fun setupSizeLpl(a: TypedArray,index:Int){
        when (a.getIndex(index)) {
            R.styleable.JJSearchBarStatic_layout_height_landscape -> {
                mlsHeight = a.getLayoutDimension(R.styleable.JJSearchBarStatic_layout_height_landscape,0)
            }
            R.styleable.JJSearchBarStatic_layout_width_landscape -> {
                mlsWidth = a.getLayoutDimension(R.styleable.JJSearchBarStatic_layout_width_landscape,0)
            }
            R.styleable.JJSearchBarStatic_lplHeightPercentScreenWidth -> {
                mlsHeight = JJScreen.percentWidth( a.getFloat(R.styleable.JJSearchBarStatic_lplHeightPercentScreenWidth,0f))
            }
            R.styleable.JJSearchBarStatic_lplWidthPercentScreenWidth -> {
                mlsWidth = JJScreen.percentWidth( a.getFloat(R.styleable.JJSearchBarStatic_lplWidthPercentScreenWidth,0f))
            }

            R.styleable.JJSearchBarStatic_lplHeightPercentScreenHeight -> {
                mlsHeight = JJScreen.percentHeight( a.getFloat(R.styleable.JJSearchBarStatic_lplHeightPercentScreenHeight,0f))
            }
            R.styleable.JJSearchBarStatic_lplWidthPercentScreenHeight -> {
                mlsWidth = JJScreen.percentHeight( a.getFloat(R.styleable.JJSearchBarStatic_lplWidthPercentScreenHeight,0f))
            }
            R.styleable.JJSearchBarStatic_lplHeightResponsive -> {
                mlsHeight = responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_lplHeightResponsive)
            }
            R.styleable.JJSearchBarStatic_lplWidthResponsive -> {
                mlsWidth = responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_lplWidthResponsive)
            }
            R.styleable.JJSearchBarStatic_lplHeightResponsivePercentScreenHeight -> {
                mlsHeight = responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_lplHeightResponsivePercentScreenHeight)
            }
            R.styleable.JJSearchBarStatic_lplWidthResponsivePercentScreenHeight -> {
                mlsWidth = responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_lplWidthResponsivePercentScreenHeight)
            }
            R.styleable.JJSearchBarStatic_lplHeightResponsivePercentScreenWidth -> {
                mlsHeight = responsiveSizePercentScreenWidth(a,R.styleable.JJSearchBarStatic_lplHeightResponsivePercentScreenWidth)
            }
            R.styleable.JJSearchBarStatic_lplWidthResponsivePercentScreenWidth -> {
                mlsWidth = responsiveSizePercentScreenWidth(a,R.styleable.JJSearchBarStatic_lplWidthResponsivePercentScreenWidth)
            }

        }
    }

    private fun setupMarginCll(a: TypedArray,index:Int){
        var lsMargins = JJMargin()
        when (a.getIndex(index)) {
            R.styleable.JJSearchBarStatic_cllMarginEnd -> {
                lsMargins.right = a.getDimension(R.styleable.JJSearchBarStatic_cllMarginEnd,0f).toInt()
            }
            R.styleable.JJSearchBarStatic_cllMarginStart -> {
                lsMargins.left = a.getDimension(R.styleable.JJSearchBarStatic_cllMarginStart,0f).toInt()
            }
            R.styleable.JJSearchBarStatic_cllMarginTop -> {
                lsMargins.top = a.getDimension(R.styleable.JJSearchBarStatic_cllMarginTop,0f).toInt()
            }
            R.styleable.JJSearchBarStatic_cllMarginBottom -> {
                lsMargins.bottom = a.getDimension(R.styleable.JJSearchBarStatic_cllMarginBottom,0f).toInt()
            }

            R.styleable.JJSearchBarStatic_cllMarginEndPercentScreenHeight -> {
                lsMargins.right = JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_cllMarginEndPercentScreenHeight,0f))
            }
            R.styleable.JJSearchBarStatic_cllMarginStartPercentScreenHeight -> {
                lsMargins.left = JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_cllMarginStartPercentScreenHeight,0f))
            }
            R.styleable.JJSearchBarStatic_cllMarginTopPercentScreenHeight -> {
                lsMargins.top = JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_cllMarginTopPercentScreenHeight,0f))
            }
            R.styleable.JJSearchBarStatic_cllMarginBottomPercentScreenHeight -> {
                lsMargins.bottom = JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_cllMarginBottomPercentScreenHeight,0f))
            }

            R.styleable.JJSearchBarStatic_cllMarginEndPercentScreenWidth -> {
                lsMargins.right = JJScreen.percentWidth(a.getFloat(R.styleable.JJSearchBarStatic_cllMarginEndPercentScreenWidth,0f))
            }
            R.styleable.JJSearchBarStatic_cllMarginStartPercentScreenWidth -> {
                lsMargins.left = JJScreen.percentWidth(a.getFloat(R.styleable.JJSearchBarStatic_cllMarginStartPercentScreenWidth,0f))
            }
            R.styleable.JJSearchBarStatic_cllMarginTopPercentScreenWidth -> {
                lsMargins.top = JJScreen.percentWidth(a.getFloat(R.styleable.JJSearchBarStatic_cllMarginTopPercentScreenWidth,0f))
            }
            R.styleable.JJSearchBarStatic_cllMarginBottomPercentScreenWidth -> {
                lsMargins.bottom = JJScreen.percentWidth(a.getFloat(R.styleable.JJSearchBarStatic_cllMarginBottomPercentScreenWidth,0f))
            }

            R.styleable.JJSearchBarStatic_cllMargin -> {
                lsMargins = JJMargin.all(a.getDimension(R.styleable.JJSearchBarStatic_cllMargin,0f).toInt())
            }
            R.styleable.JJSearchBarStatic_cllMarginPerScHeight -> {
                lsMargins = JJMargin.all(JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_cllMarginPerScHeight,0f)))
            }
            R.styleable.JJSearchBarStatic_cllMarginPerScWidth -> {
                lsMargins = JJMargin.all(JJScreen.percentWidth(a.getFloat(R.styleable.JJSearchBarStatic_cllMarginPerScWidth,0f)))
            }
            R.styleable.JJSearchBarStatic_cllMarginResponsive -> {
                lsMargins = JJMargin.all(responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_cllMarginResponsive))
            }
            R.styleable.JJSearchBarStatic_cllMarginResPerScHeight -> {
                lsMargins = JJMargin.all(responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_cllMarginResPerScHeight))
            }
            R.styleable.JJSearchBarStatic_cllMarginResPerScWidth -> {
                lsMargins = JJMargin.all(responsiveSizePercentScreenWidth(a,R.styleable.JJSearchBarStatic_cllMarginResPerScWidth))
            }
            R.styleable.JJSearchBarStatic_cllMarginEndResponsive ->{
                lsMargins.right = responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_cllMarginEndResponsive)
            }
            R.styleable.JJSearchBarStatic_cllMarginStartResponsive ->{
                lsMargins.left = responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_cllMarginStartResponsive)
            }
            R.styleable.JJSearchBarStatic_cllMarginTopResponsive ->{
                lsMargins.top = responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_cllMarginTopResponsive)
            }
            R.styleable.JJSearchBarStatic_cllMarginBottomResponsive ->{
                lsMargins.bottom = responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_cllMarginBottomResponsive)
            }

            R.styleable.JJSearchBarStatic_cllMarginEndResPerScHeight ->{
                lsMargins.right = responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_cllMarginEndResPerScHeight)
            }
            R.styleable.JJSearchBarStatic_cllMarginStartResPerScHeight ->{
                lsMargins.left = responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_cllMarginStartResPerScHeight)
            }
            R.styleable.JJSearchBarStatic_cllMarginTopResPerScHeight ->{
                lsMargins.top = responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_cllMarginTopResPerScHeight)
            }
            R.styleable.JJSearchBarStatic_cllMarginBottomResPerScHeight ->{
                lsMargins.bottom = responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_cllMarginBottomResPerScHeight)
            }

            R.styleable.JJSearchBarStatic_cllMarginEndResPerScWidth ->{
                lsMargins.right = responsiveSizePercentScreenWidth(a,R.styleable.JJSearchBarStatic_cllMarginEndResPerScWidth)
            }
            R.styleable.JJSearchBarStatic_cllMarginStartResPerScWidth ->{
                lsMargins.left = responsiveSizePercentScreenWidth(a,R.styleable.JJSearchBarStatic_cllMarginStartResPerScWidth)
            }
            R.styleable.JJSearchBarStatic_cllMarginTopResPerScWidth ->{
                lsMargins.top = responsiveSizePercentScreenWidth(a,R.styleable.JJSearchBarStatic_cllMarginTopResPerScWidth)
            }
            R.styleable.JJSearchBarStatic_cllMarginBottomResPerScWidth ->{
                lsMargins.bottom = responsiveSizePercentScreenWidth(a,R.styleable.JJSearchBarStatic_cllMarginBottomResPerScWidth)
            }

            R.styleable.JJSearchBarStatic_cllMarginVertical->{
                val mar = a.getDimension(R.styleable.JJSearchBarStatic_cllMarginVertical,0f).toInt()
                lsMargins.top = mar ; lsMargins.bottom = mar
            }
            R.styleable.JJSearchBarStatic_cllMarginVerticalPerScHeight->{
                val mar =JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_cllMarginVerticalPerScHeight,0f))
                lsMargins.top = mar ; lsMargins.bottom = mar
            }
            R.styleable.JJSearchBarStatic_cllMarginVerticalPerScWidth->{
                val mar = JJScreen.percentWidth(a.getFloat(R.styleable.JJSearchBarStatic_cllMarginVerticalPerScWidth,0f))
                lsMargins.top = mar ; lsMargins.bottom = mar
            }
            R.styleable.JJSearchBarStatic_cllMarginVerticalResponsive->{
                val mar = responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_cllMarginVerticalResponsive)
                lsMargins.top = mar ; lsMargins.bottom = mar
            }
            R.styleable.JJSearchBarStatic_cllMarginVerticalResPerScHeight->{
                val mar = responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_cllMarginVerticalResPerScHeight)
                lsMargins.top = mar ; lsMargins.bottom = mar
            }
            R.styleable.JJSearchBarStatic_cllMarginVerticalResPerScWidth->{
                val mar = responsiveSizePercentScreenWidth(a,R.styleable.JJSearchBarStatic_cllMarginVerticalResPerScWidth)
                lsMargins.top = mar ; lsMargins.bottom = mar
            }

            R.styleable.JJSearchBarStatic_cllMarginHorizontal->{
                val mar = a.getDimension(R.styleable.JJSearchBarStatic_cllMarginHorizontal,0f).toInt()
                lsMargins.top = mar ; lsMargins.bottom = mar
            }
            R.styleable.JJSearchBarStatic_cllMarginHorizontalPerScHeight->{
                val mar =JJScreen.percentHeight(a.getFloat(R.styleable.JJSearchBarStatic_cllMarginHorizontalPerScHeight,0f))
                lsMargins.left = mar ; lsMargins.right = mar
            }
            R.styleable.JJSearchBarStatic_cllMarginHorizontalPerScWidth->{
                val mar = JJScreen.percentWidth(a.getFloat(R.styleable.JJSearchBarStatic_cllMarginHorizontalPerScWidth,0f))
                lsMargins.left = mar ; lsMargins.right = mar
            }
            R.styleable.JJSearchBarStatic_cllMarginHorizontalResponsive->{
                val mar = responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_cllMarginHorizontalResponsive)
                lsMargins.left = mar ; lsMargins.right = mar
            }
            R.styleable.JJSearchBarStatic_cllMarginHorizontalResPerScHeight->{
                val mar = responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_cllMarginHorizontalResPerScHeight)
                lsMargins.left = mar ; lsMargins.right = mar
            }
            R.styleable.JJSearchBarStatic_cllMarginHorizontalResPerScWidth->{
                val mar = responsiveSizePercentScreenWidth(a,R.styleable.JJSearchBarStatic_cllMarginHorizontalResPerScWidth)
                lsMargins.left = mar ; lsMargins.right = mar
            }
        }
        cllMargins(lsMargins)
    }
    private fun setupSizeCll(a: TypedArray,index:Int){
        when (a.getIndex(index)) {
            R.styleable.JJSearchBarStatic_layout_height_landscape->{
                val value = a.getLayoutDimension(R.styleable.JJSearchBarStatic_layout_height_landscape,0)
                if(value > 0 || value == -2 ) cllHeight(value)
            }
            R.styleable.JJSearchBarStatic_layout_width_landscape->{
                val value = a.getLayoutDimension(R.styleable.JJSearchBarStatic_layout_width_landscape,0)
                if(value > 0 || value == -2 ) cllWidth(value)
            }
            R.styleable.JJSearchBarStatic_cllHeightPercent -> {
                cllPercentHeight( a.getFloat(R.styleable.JJSearchBarStatic_cllHeightPercent,0f))
            }
            R.styleable.JJSearchBarStatic_cllWidthPercent -> {
                cllPercentWidth( a.getFloat(R.styleable.JJSearchBarStatic_cllWidthPercent,0f))
            }
            R.styleable.JJSearchBarStatic_cllHeightPercentScreenWidth -> {
                cllHeight(JJScreen.percentWidth( a.getFloat(R.styleable.JJSearchBarStatic_cllHeightPercentScreenWidth,0f)))
            }
            R.styleable.JJSearchBarStatic_cllWidthPercentScreenWidth -> {
                cllWidth(JJScreen.percentWidth( a.getFloat(R.styleable.JJSearchBarStatic_cllWidthPercentScreenWidth,0f)))
            }
            R.styleable.JJSearchBarStatic_cllHeightPercentScreenHeight -> {
                cllHeight(JJScreen.percentHeight( a.getFloat(R.styleable.JJSearchBarStatic_cllHeightPercentScreenHeight,0f)))
            }
            R.styleable.JJSearchBarStatic_cllWidthPercentScreenHeight -> {
                cllWidth(JJScreen.percentHeight( a.getFloat(R.styleable.JJSearchBarStatic_cllWidthPercentScreenHeight,0f)))
            }
            R.styleable.JJSearchBarStatic_cllHeightResponsive -> {
                cllHeight(responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_cllHeightResponsive))
            }
            R.styleable.JJSearchBarStatic_cllWidthResponsive -> {
                cllWidth(responsiveSizeDimension(a,R.styleable.JJSearchBarStatic_cllWidthResponsive))
            }

            R.styleable.JJSearchBarStatic_cllHeightResponsivePercentScreenHeight -> {
                cllHeight(responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_cllHeightResponsivePercentScreenHeight))
            }
            R.styleable.JJSearchBarStatic_cllWidthResponsivePercentScreenHeight -> {
                cllWidth(responsiveSizePercentScreenHeight(a,R.styleable.JJSearchBarStatic_cllWidthResponsivePercentScreenHeight))
            }
            R.styleable.JJSearchBarStatic_cllWidthResponsivePercentScreenWidth -> {
                cllHeight(responsiveSizePercentScreenWidth(a,R.styleable.JJSearchBarStatic_cllWidthResponsivePercentScreenWidth))
            }
            R.styleable.JJSearchBarStatic_cllHeightResponsivePercentScreenWidth -> {
                cllWidth(responsiveSizePercentScreenWidth(a,R.styleable.JJSearchBarStatic_cllHeightResponsivePercentScreenWidth))
            }
        }

    }
    private fun setupAnchorsCll(a: TypedArray,index:Int){
        when (a.getIndex(index)) {
            R.styleable.JJSearchBarStatic_cllFillParent ->{
                if(a.getBoolean(R.styleable.JJSearchBarStatic_cllFillParent,false)) cllFillParent()
            }
            R.styleable.JJSearchBarStatic_cllFillParentHorizontally ->{
                if(a.getBoolean(R.styleable.JJSearchBarStatic_cllFillParentHorizontally,false)) cllFillParentHorizontally()
            }
            R.styleable.JJSearchBarStatic_cllFillParentVertically ->{
                if(a.getBoolean(R.styleable.JJSearchBarStatic_cllFillParentVertically,false)) cllFillParentVertically()
            }
            R.styleable.JJSearchBarStatic_cllCenterInParent ->{
                if(a.getBoolean(R.styleable.JJSearchBarStatic_cllCenterInParent,false)) cllCenterInParent()
            }
            R.styleable.JJSearchBarStatic_cllCenterInParentHorizontally ->{
                if(a.getBoolean(R.styleable.JJSearchBarStatic_cllCenterInParentHorizontally,false)) cllCenterInParentHorizontally()
            }
            R.styleable.JJSearchBarStatic_cllCenterInParentVertically ->{
                if(a.getBoolean(R.styleable.JJSearchBarStatic_cllCenterInParentVertically,false)) cllCenterInParentVertically()
            }
            R.styleable.JJSearchBarStatic_cllCenterInParentTopVertically ->{
                if(a.getBoolean(R.styleable.JJSearchBarStatic_cllCenterInParentTopVertically,false)) cllCenterInParentTopVertically()
            }
            R.styleable.JJSearchBarStatic_cllCenterInParentBottomVertically ->{
                if(a.getBoolean(R.styleable.JJSearchBarStatic_cllCenterInParentBottomVertically,false)) cllCenterInParentBottomVertically()
            }
            R.styleable.JJSearchBarStatic_cllCenterInParentStartHorizontally ->{
                if(a.getBoolean(R.styleable.JJSearchBarStatic_cllCenterInParentStartHorizontally,false)) cllCenterInParentStartHorizontally()
            }
            R.styleable.JJSearchBarStatic_cllCenterInParentEndHorizontally ->{
                if(a.getBoolean(R.styleable.JJSearchBarStatic_cllCenterInParentEndHorizontally,false)) cllCenterInParentEndHorizontally()
            }
            R.styleable.JJSearchBarStatic_cllCenterInTopVerticallyOf ->{
                cllCenterInTopVertically(a.getResourceId(R.styleable.JJSearchBarStatic_cllCenterInTopVerticallyOf,
                    View.NO_ID))
            }
            R.styleable.JJSearchBarStatic_cllCenterInBottomVerticallyOf ->{
                cllCenterInBottomVertically(a.getResourceId(R.styleable.JJSearchBarStatic_cllCenterInBottomVerticallyOf,
                    View.NO_ID))
            }
            R.styleable.JJSearchBarStatic_cllCenterInStartHorizontallyOf ->{
                cllCenterInStartHorizontally(a.getResourceId(R.styleable.JJSearchBarStatic_cllCenterInStartHorizontallyOf,
                    View.NO_ID))
            }
            R.styleable.JJSearchBarStatic_cllCenterInEndHorizontallyOf ->{
                cllCenterInEndHorizontally(a.getResourceId(R.styleable.JJSearchBarStatic_cllCenterInEndHorizontallyOf,
                    View.NO_ID))
            }
            R.styleable.JJSearchBarStatic_cllCenterVerticallyOf ->{
                cllCenterVerticallyOf(a.getResourceId(R.styleable.JJSearchBarStatic_cllCenterVerticallyOf,
                    View.NO_ID))
            }
            R.styleable.JJSearchBarStatic_cllCenterHorizontallyOf ->{
                cllCenterHorizontallyOf(a.getResourceId(R.styleable.JJSearchBarStatic_cllCenterHorizontallyOf,
                    View.NO_ID))
            }
            R.styleable.JJSearchBarStatic_cllVerticalBias -> {
                cllVerticalBias(a.getFloat(R.styleable.JJSearchBarStatic_cllVerticalBias,0.5f))
            }
            R.styleable.JJSearchBarStatic_cllHorizontalBias -> {
                cllHorizontalBias(a.getFloat(R.styleable.JJSearchBarStatic_cllHorizontalBias,0.5f))
            }

            R.styleable.JJSearchBarStatic_cllStartToStartParent ->{
                if(a.getBoolean(R.styleable.JJSearchBarStatic_cllStartToStartParent,false)) cllStartToStartParent()
            }
            R.styleable.JJSearchBarStatic_cllStartToEndParent ->{
                if(a.getBoolean(R.styleable.JJSearchBarStatic_cllStartToEndParent,false)) cllStartToEndParent()
            }
            R.styleable.JJSearchBarStatic_cllEndToEndParent ->{
                if(a.getBoolean(R.styleable.JJSearchBarStatic_cllEndToEndParent,false)) cllEndToEndParent()
            }
            R.styleable.JJSearchBarStatic_cllEndToStartParent ->{
                if(a.getBoolean(R.styleable.JJSearchBarStatic_cllEndToStartParent,false)) cllEndToStartParent()
            }
            R.styleable.JJSearchBarStatic_cllTopToTopParent ->{
                if(a.getBoolean(R.styleable.JJSearchBarStatic_cllTopToTopParent,false)) cllTopToTopParent()
            }
            R.styleable.JJSearchBarStatic_cllTopToBottomParent ->{
                if(a.getBoolean(R.styleable.JJSearchBarStatic_cllTopToBottomParent,false)) cllTopToBottomParent()
            }
            R.styleable.JJSearchBarStatic_cllBottomToBottomParent ->{
                if(a.getBoolean(R.styleable.JJSearchBarStatic_cllBottomToBottomParent,false)) cllBottomToBottomParent()
            }
            R.styleable.JJSearchBarStatic_cllBottomToTopParent ->{
                if(a.getBoolean(R.styleable.JJSearchBarStatic_cllBottomToTopParent,false)) cllBottomToTopParent()
            }

            R.styleable.JJSearchBarStatic_cllStartToStartOf -> {
                cllStartToStart(a.getResourceId(R.styleable.JJSearchBarStatic_cllStartToStartOf, View.NO_ID))
            }
            R.styleable.JJSearchBarStatic_cllStartToEndOf -> {
                cllStartToEnd(a.getResourceId(R.styleable.JJSearchBarStatic_cllStartToEndOf, View.NO_ID))
            }
            R.styleable.JJSearchBarStatic_cllEndToEndOf -> {
                cllEndToEnd(a.getResourceId(R.styleable.JJSearchBarStatic_cllEndToEndOf, View.NO_ID))
            }
            R.styleable.JJSearchBarStatic_cllEndToStartOf -> {
                cllEndToStart(a.getResourceId(R.styleable.JJSearchBarStatic_cllEndToStartOf, View.NO_ID))
            }
            R.styleable.JJSearchBarStatic_cllTopToTopOf -> {
                cllTopToTop(a.getResourceId(R.styleable.JJSearchBarStatic_cllTopToTopOf, View.NO_ID))
            }
            R.styleable.JJSearchBarStatic_cllTopToBottomOf -> {
                cllTopToBottom(a.getResourceId(R.styleable.JJSearchBarStatic_cllTopToBottomOf, View.NO_ID))
            }
            R.styleable.JJSearchBarStatic_cllBottomToBottomOf -> {
                cllBottomToBottom(a.getResourceId(R.styleable.JJSearchBarStatic_cllBottomToBottomOf, View.NO_ID))
            }
            R.styleable.JJSearchBarStatic_cllBottomToTopOf -> {
                cllBottomToTop(a.getResourceId(R.styleable.JJSearchBarStatic_cllBottomToTopOf, View.NO_ID))
            }

        }

    }

    private fun setupInitConstraint(){
        mConstraintSet.constrainWidth(id,0)
        mConstraintSet.constrainHeight(id,0)
        mConstraintSetLandScape.constrainWidth(id,0)
        mConstraintSetLandScape.constrainHeight(id,0)
    }
    private fun responsiveSizeDimension(a: TypedArray,style:Int) : Int {
        val t = resources.obtainTypedArray(a.getResourceId(style,
            View.NO_ID))
        val re = JJScreen.responsiveSize(t.getDimension(0, 0f).toInt(),
            t.getDimension(1, 0f).toInt(),
            t.getDimension(2, 0f).toInt(),
            t.getDimension(3, 0f).toInt())
        t.recycle()
        return re
    }
    private fun responsiveSizePercentScreenWidth(a: TypedArray,style:Int) : Int {
        val t = resources.obtainTypedArray(a.getResourceId(style,
            View.NO_ID))
        val re = JJScreen.responsiveSizePercentScreenWidth(t.getFloat(0, 0f),
            t.getFloat(1, 0f),
            t.getFloat(2, 0f),
            t.getFloat(3, 0f))
        t.recycle()
        return re
    }
    private fun responsiveSizePercentScreenHeight(a: TypedArray,style:Int) : Int {
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
                val margin = layoutParams as? MarginLayoutParams
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
                val margin = layoutParams as? MarginLayoutParams
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

    fun setSupportLandScape(support:Boolean) : JJSearchBarStatic {
        mSupportLandScape = support
        return this
    }

    fun setSupportConfigurationChanged(support:Boolean) : JJSearchBarStatic {
        mConfigurationChanged = support
        return this
    }

    fun addViews(vararg views: View): JJSearchBarStatic {
        for (v in views) {
            addView(v)
        }
        return this
    }



    private var mIdentifier = 0
    fun setIdentifier(value: Int): JJSearchBarStatic {
        mIdentifier = value
        return this
    }

    fun getIdentifier():Int{
        return mIdentifier
    }

    private var mState = 0
    fun setState(state: Int): JJSearchBarStatic {
        mState = state
        return this
    }

    fun getState():Int{
        return mState
    }

    private var mAttribute = ""
    fun setAttribute(string:String): JJSearchBarStatic {
        mAttribute = string
        return this
    }

    fun getAttribute(): String {
        return mAttribute
    }

    fun setPadding(padding: JJPadding): JJSearchBarStatic {
        mlpPadding = padding
        setPaddingRelative(padding.left,padding.top,padding.right,padding.bottom)
        return this
    }

    fun setOnClickListenerR(listener: (view: View) -> Unit): JJSearchBarStatic {
        setOnClickListener(listener)
        return this
    }

    fun setOnFocusChangeListenerR(listener: OnFocusChangeListener): JJSearchBarStatic {
        onFocusChangeListener = listener
        return this
    }


    fun setIsFocusable(boolean: Boolean): JJSearchBarStatic {
        isFocusable = boolean
        return this
    }

    fun setOnTouchListenerR(listener: OnTouchListener): JJSearchBarStatic {
        setOnTouchListener(listener)
        return this
    }

    fun setFitsSystemWindowsR(boolean: Boolean): JJSearchBarStatic {
        fitsSystemWindows = boolean
        return this
    }

    fun setBackgroundColorR(color: Int): JJSearchBarStatic {
        setBackgroundColor(color)
        return this
    }

    fun setBackgroundR(drawable: Drawable?): JJSearchBarStatic {
        background = drawable
        return this
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setOutlineProviderR(provider: ViewOutlineProvider): JJSearchBarStatic {
        outlineProvider = provider
        return this
    }

    fun setFullScreen(): JJSearchBarStatic {
        systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        return this
    }

    fun setIsFocusableInTouchMode(boolean: Boolean): JJSearchBarStatic {
        isFocusableInTouchMode = boolean
        return this
    }



    fun setVisibilityR(type: Int): JJSearchBarStatic {
        visibility = type
        return this
    }

    fun setMinHeightR(h:Int): JJSearchBarStatic {
        minHeight = h
        return this
    }

    fun setMinWidthR(w:Int): JJSearchBarStatic {
        minWidth = w
        return this
    }

    fun setMinimumHeightR(h:Int): JJSearchBarStatic {
        minimumHeight = h
        return this
    }

    fun setMinimumWidthR(w:Int): JJSearchBarStatic {
        minimumWidth = w
        return this
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setClipToOutlineR(boolean: Boolean) : JJSearchBarStatic {
        clipToOutline = boolean
        return this
    }
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun setClipBoundsR(bounds: Rect) : JJSearchBarStatic {
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


    fun setClipChildrenToPath(path: Path): JJSearchBarStatic {
        mPathClipChildren = path
        mIsPathClosureClipChildren = false
        mIsClipInPathChildren = true
        mIsClipChildrenEnabled = true
        mIsClipOutPathChildren = false
        return this
    }

    fun setClipAllToPath(path: Path): JJSearchBarStatic {
        mPathClipAll = path
        mIsPathClosureClipAll = false
        mIsClipInPathAll = true
        mIsClipAllEnabled = true
        mIsClipOutPathAll = false
        return this
    }


    fun setClipOutChildrenToPath(path: Path): JJSearchBarStatic {
        mPathClipChildren = path
        mIsPathClosureClipChildren = false
        mIsClipOutPathChildren = true
        mIsClipChildrenEnabled = true
        mIsClipInPathChildren = false
        return this
    }


    fun setClipOutAllToPath(path: Path): JJSearchBarStatic {
        mPathClipAll = path
        mIsPathClosureClipAll = false
        mIsClipOutPathAll = true
        mIsClipAllEnabled = true
        mIsClipInPathAll = false
        return this
    }

    fun setClipChildrenToPath(closure:(RectF, Path)->Unit): JJSearchBarStatic {
        mIsClipInPathChildren = true
        mIsPathClosureClipChildren = true
        mIsClipOutPathChildren = false
        mIsClipChildrenEnabled = true
        mClosurePathClipChildren = closure
        return this
    }

    fun setClipAllToPath(closure:(RectF, Path, JJPadding)->Unit): JJSearchBarStatic {
        mIsClipInPathAll = true
        mIsPathClosureClipAll = true
        mIsClipOutPathAll = false
        mIsClipAllEnabled = true
        mClosurePathClipAll = closure
        return this
    }

    fun setClipOutChildrenToPath(closure:(RectF, Path)->Unit): JJSearchBarStatic {
        mIsClipInPathChildren = false
        mIsPathClosureClipChildren = true
        mIsClipOutPathChildren = true
        mIsClipChildrenEnabled = true
        mClosurePathClipChildren = closure
        return this
    }

    fun setClipOutAllToPath(closure:(RectF, Path, JJPadding)->Unit): JJSearchBarStatic {
        mIsClipInPathAll = false
        mIsPathClosureClipAll = true
        mIsClipOutPathAll = true
        mIsClipAllEnabled = true
        mClosurePathClipAll = closure
        return this
    }

    fun disposeClipPathChildren(): JJSearchBarStatic {
        mIsClipOutPathChildren = false
        mIsPathClosureClipChildren = false
        mIsClipChildrenEnabled = false
        mIsClipInPathChildren = false
        mClosurePathClipChildren = null
        return  this
    }
    fun disposeClipPathAll(): JJSearchBarStatic {
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

    fun colWidth(width: Int): JJSearchBarStatic {
        setupCol()
        mCol!!.width = width
        return this
    }

    fun colHeight(height: Int): JJSearchBarStatic {
        setupCol()
        mCol!!.height = height
        return this
    }

    fun colGravity(gravity: Int): JJSearchBarStatic {
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

    fun ablWidth(width: Int): JJSearchBarStatic {
        setupAblp()
        ablp!!.width = width
        return this
    }

    fun ablHeight(height: Int): JJSearchBarStatic {
        setupAblp()
        ablp!!.height = height
        return this
    }

    fun ablScrollFlags(flags: Int) : JJSearchBarStatic {
        setupAblp()
        ablp!!.scrollFlags = flags
        return this
    }

    fun ablScrollInterpolator(interpolator: Interpolator) : JJSearchBarStatic {
        setupAblp()
        ablp!!.scrollInterpolator = interpolator
        return this
    }

    fun ablMargins(margins: JJMargin): JJSearchBarStatic {
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

    fun rlWidth(width: Int): JJSearchBarStatic {
        setupRlp()
        mRlp!!.width = width
        return this
    }

    fun rlHeight(height: Int): JJSearchBarStatic {
        setupRlp()
        mRlp!!.height = height
        return this
    }

    fun rlAbove(viewId: Int): JJSearchBarStatic {
        setupRlp()
        mRlp!!.addRule(RelativeLayout.ABOVE,viewId)
        return this
    }

    fun rlBelow(viewId: Int): JJSearchBarStatic {
        setupRlp()
        mRlp!!.addRule(RelativeLayout.BELOW,viewId)
        return this
    }

    fun rlAlignParentBottom(value : Boolean = true): JJSearchBarStatic {
        setupRlp()
        val data = if(value) 1 else 0
        mRlp!!.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,data)
        return this
    }

    fun rlAlignParentTop(value : Boolean = true): JJSearchBarStatic {
        setupRlp()
        val data = if(value) 1 else 0
        mRlp!!.addRule(RelativeLayout.ALIGN_PARENT_TOP,data)
        return this
    }

    fun rlAlignParentStart(value : Boolean = true): JJSearchBarStatic {
        setupRlp()
        val data = if(value) 1 else 0
        mRlp!!.addRule(RelativeLayout.ALIGN_PARENT_START,data)
        return this
    }

    fun rlAlignParentEnd(value : Boolean = true): JJSearchBarStatic {
        setupRlp()
        val data = if(value) 1 else 0
        mRlp!!.addRule(RelativeLayout.ALIGN_PARENT_END,data)
        return this
    }

    fun rlAlignParentLeft(value : Boolean = true): JJSearchBarStatic {
        setupRlp()
        val data = if(value) 1 else 0
        mRlp!!.addRule(RelativeLayout.ALIGN_PARENT_LEFT,data)
        return this
    }

    fun rlAlignParentRight(value : Boolean = true): JJSearchBarStatic {
        setupRlp()
        val data = if(value) 1 else 0
        mRlp!!.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,data)
        return this
    }

    fun rlAlignEnd(viewId: Int): JJSearchBarStatic {
        setupRlp()
        mRlp!!.addRule(RelativeLayout.ALIGN_END,viewId)
        return this
    }

    fun rlAlignStart(viewId: Int): JJSearchBarStatic {
        setupRlp()
        mRlp!!.addRule(RelativeLayout.ALIGN_START,viewId)
        return this
    }

    fun rlAlignTop(viewId: Int): JJSearchBarStatic {
        setupRlp()
        mRlp!!.addRule(RelativeLayout.ALIGN_TOP,viewId)
        return this
    }

    fun rlAlignBottom(viewId: Int): JJSearchBarStatic {
        setupRlp()
        mRlp!!.addRule(RelativeLayout.ALIGN_BOTTOM,viewId)
        return this
    }


    fun rlAlignLeft(viewId: Int): JJSearchBarStatic {
        setupRlp()
        mRlp!!.addRule(RelativeLayout.ALIGN_LEFT,viewId)
        return this
    }

    fun rlAlignRight(viewId: Int): JJSearchBarStatic {
        setupRlp()
        mRlp!!.addRule(RelativeLayout.ALIGN_RIGHT,viewId)
        return this
    }

    fun rlRightToLeft(viewId: Int): JJSearchBarStatic {
        setupRlp()
        mRlp!!.addRule(RelativeLayout.LEFT_OF,viewId)
        return this
    }

    fun rlLeftToRight(viewId: Int): JJSearchBarStatic {
        setupRlp()
        mRlp!!.addRule(RelativeLayout.RIGHT_OF,viewId)
        return this
    }

    fun rlStartToEnd(viewId: Int): JJSearchBarStatic {
        setupRlp()
        mRlp!!.addRule(RelativeLayout.END_OF,viewId)
        return this
    }

    fun rlEndToStart(viewId: Int): JJSearchBarStatic {
        setupRlp()
        mRlp!!.addRule(RelativeLayout.START_OF,viewId)
        return this
    }

    fun rlCenterInParent(value:Boolean = true): JJSearchBarStatic {
        setupRlp()
        val data = if(value) 1 else 0
        mRlp!!.addRule(RelativeLayout.CENTER_IN_PARENT,data)
        return this
    }

    fun rlCenterInParentVertically(value:Boolean = true): JJSearchBarStatic {
        setupRlp()
        val data = if(value) 1 else 0
        mRlp!!.addRule(RelativeLayout.CENTER_VERTICAL,data)
        return this
    }

    fun rlCenterInParentHorizontally(value:Boolean = true): JJSearchBarStatic {
        setupRlp()
        val data = if(value) 1 else 0
        mRlp!!.addRule(RelativeLayout.CENTER_HORIZONTAL,data)
        return this
    }

    fun rlAlignBaseline(viewId: Int): JJSearchBarStatic {
        setupRlp()
        mRlp!!.addRule(RelativeLayout.ALIGN_BASELINE,viewId)
        return this
    }

    fun rlMargins(margins: JJMargin): JJSearchBarStatic {
        setupRlp()
        mRlp!!.setMargins(margins.left,margins.top,margins.right,margins.bottom)
        return this
    }

    //endregion

    //region MotionLayout Params

    private var mMotionConstraintSet: ConstraintSet? = null


    fun mlVisibilityMode(visibility: Int): JJSearchBarStatic {
        mMotionConstraintSet?.setVisibilityMode(id, visibility)
        return this
    }

    fun mlVerticalBias(float: Float): JJSearchBarStatic {
        mMotionConstraintSet?.setVerticalBias(id,float)
        return this
    }
    fun mlHorizontalBias(float: Float): JJSearchBarStatic {
        mMotionConstraintSet?.setHorizontalBias(id,float)
        return this
    }

    fun mlCenterHorizontallyOf(viewId: Int, marginStart: Int = 0, marginEnd: Int = 0): JJSearchBarStatic {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.START, viewId, ConstraintSet.START, marginStart)
        mMotionConstraintSet?.connect(this.id, ConstraintSet.END, viewId, ConstraintSet.END, marginEnd)
        mMotionConstraintSet?.setHorizontalBias(viewId,0.5f)
        return this
    }
    fun mlCenterVerticallyOf(viewId: Int,marginTop: Int = 0, marginBottom: Int = 0): JJSearchBarStatic {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.TOP, viewId, ConstraintSet.TOP, marginTop)
        mMotionConstraintSet?.connect(this.id, ConstraintSet.BOTTOM, viewId, ConstraintSet.BOTTOM, marginBottom)
        mMotionConstraintSet?.setVerticalBias(viewId,0.5f)
        return this
    }

    fun mlMargins(margins: JJMargin) : JJSearchBarStatic {
        mMotionConstraintSet?.setMargin(id, ConstraintSet.TOP,margins.top)
        mMotionConstraintSet?.setMargin(id, ConstraintSet.BOTTOM,margins.bottom)
        mMotionConstraintSet?.setMargin(id, ConstraintSet.END,margins.right)
        mMotionConstraintSet?.setMargin(id, ConstraintSet.START,margins.left)
        return this
    }


    fun mlFloatCustomAttribute(attrName: String, value: Float): JJSearchBarStatic {
        mMotionConstraintSet?.setFloatValue(id,attrName,value)
        return this
    }

    fun mlIntCustomAttribute(attrName: String, value: Int): JJSearchBarStatic {
        mMotionConstraintSet?.setIntValue(id,attrName,value)
        return this
    }

    fun mlColorCustomAttribute(attrName: String, value: Int): JJSearchBarStatic {
        mMotionConstraintSet?.setColorValue(id,attrName,value)
        return this
    }

    fun mlStringCustomAttribute(attrName: String, value: String): JJSearchBarStatic {
        mMotionConstraintSet?.setStringValue(id,attrName,value)
        return this
    }

    fun mlRotation(float: Float): JJSearchBarStatic {
        mMotionConstraintSet?.setRotation(id,float)
        return this
    }

    fun mlRotationX(float: Float): JJSearchBarStatic {
        mMotionConstraintSet?.setRotationX(id,float)
        return this
    }

    fun mlRotationY(float: Float): JJSearchBarStatic {
        mMotionConstraintSet?.setRotationY(id,float)
        return this
    }

    fun mlTranslation(x: Float,y: Float): JJSearchBarStatic {
        mMotionConstraintSet?.setTranslation(id,x,y)
        return this
    }
    fun mlTranslationX(x: Float): JJSearchBarStatic {
        mMotionConstraintSet?.setTranslationX(id,x)
        return this
    }

    fun mlTranslationY(y: Float): JJSearchBarStatic {
        mMotionConstraintSet?.setTranslationY(id,y)
        return this
    }

    fun mlTranslationZ(z: Float): JJSearchBarStatic {
        mMotionConstraintSet?.setTranslationZ(id,z)
        return this
    }

    fun mlTransformPivot(x: Float, y: Float): JJSearchBarStatic {
        mMotionConstraintSet?.setTransformPivot(id,x,y)
        return this
    }

    fun mlTransformPivotX(x: Float): JJSearchBarStatic {
        mMotionConstraintSet?.setTransformPivotX(id,x)
        return this
    }

    fun mlTransformPivotY(y: Float): JJSearchBarStatic {
        mMotionConstraintSet?.setTransformPivotY(id,y)
        return this
    }

    fun mlScaleX(x: Float): JJSearchBarStatic {
        mMotionConstraintSet?.setScaleX(id,x)
        return this
    }

    fun mlScaleY(y: Float): JJSearchBarStatic {
        mMotionConstraintSet?.setScaleY(id,y)
        return this
    }

    fun mlDimensionRatio(ratio: String): JJSearchBarStatic {
        mMotionConstraintSet?.setDimensionRatio(id,ratio)
        return this
    }

    fun mlAlpha(alpha: Float): JJSearchBarStatic {
        mMotionConstraintSet?.setAlpha(id,alpha)
        return this
    }



    fun mlTopToTop(viewId: Int, margin: Int = 0): JJSearchBarStatic {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.TOP, viewId, ConstraintSet.TOP, margin)
        return this
    }

    fun mlTopToTopParent(margin: Int = 0): JJSearchBarStatic {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin)
        return this
    }


    fun mlTopToBottomOf(viewId: Int, margin: Int = 0): JJSearchBarStatic {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.TOP, viewId, ConstraintSet.BOTTOM, margin)
        return this
    }

    fun mlTopToBottomParent(margin: Int = 0): JJSearchBarStatic {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin)
        return this
    }

    fun mlBottomToTopOf(viewId: Int, margin: Int = 0): JJSearchBarStatic {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.BOTTOM, viewId, ConstraintSet.TOP, margin)

        return this
    }

    fun mlBottomToTopParent(margin: Int = 0): JJSearchBarStatic {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin)

        return this
    }

    fun mlBottomToBottomOf(viewId: Int, margin: Int = 0): JJSearchBarStatic {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.BOTTOM, viewId, ConstraintSet.BOTTOM, margin)

        return this
    }

    fun mlBottomToBottomParent(margin: Int = 0): JJSearchBarStatic {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin)

        return this
    }

    fun mlStartToStartOf(viewId: Int, margin: Int = 0): JJSearchBarStatic {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.START, viewId, ConstraintSet.START, margin)

        return this
    }

    fun mlStartToStartParent(margin: Int = 0): JJSearchBarStatic {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margin)

        return this
    }

    fun mlStartToEndOf(viewId: Int, margin: Int = 0): JJSearchBarStatic {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.START, viewId, ConstraintSet.END, margin)

        return this
    }

    fun mlStartToEndParent(margin: Int = 0): JJSearchBarStatic {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.END, margin)

        return this
    }

    fun mlEndToEndOf(viewId: Int, margin: Int= 0): JJSearchBarStatic {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.END, viewId, ConstraintSet.END, margin)

        return this
    }

    fun mlEndToEndParent(margin: Int = 0): JJSearchBarStatic {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, margin)

        return this
    }


    fun mlEndToStartOf(viewId: Int, margin: Int = 0): JJSearchBarStatic {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.END, viewId, ConstraintSet.START, margin)
        return this
    }

    fun mlEndToStartParent(margin: Int = 0): JJSearchBarStatic {
        mMotionConstraintSet?.connect(this.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.START, margin)
        return this
    }


    fun mlWidth(width: Int): JJSearchBarStatic {
        mMotionConstraintSet?.constrainWidth(id, width)
        return this
    }

    fun mlHeight(height: Int): JJSearchBarStatic {
        mMotionConstraintSet?.constrainHeight(id, height)
        return this
    }

    fun mlPercentWidth(width: Float): JJSearchBarStatic {
        mMotionConstraintSet?.constrainPercentWidth(id, width)
        return this
    }

    fun mlPercentHeight(height: Float): JJSearchBarStatic {
        mMotionConstraintSet?.constrainPercentHeight(id, height)
        return this
    }

    fun mlCenterInParent(): JJSearchBarStatic {
        mMotionConstraintSet?.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mMotionConstraintSet?.setVerticalBias(id, 0.5f)
        mMotionConstraintSet?.setHorizontalBias(id, 0.5f)

        return this
    }

    fun mlCenterInParent(verticalBias: Float, horizontalBias: Float, margin: JJMargin): JJSearchBarStatic {
        mMotionConstraintSet?.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margin.left)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, margin.right)
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin.top)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin.bottom)
        mMotionConstraintSet?.setVerticalBias(id, verticalBias)
        mMotionConstraintSet?.setHorizontalBias(id, horizontalBias)
        return this
    }

    fun mlCenterInParentVertically(): JJSearchBarStatic {
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mMotionConstraintSet?.setVerticalBias(id, 0.5f)

        return this
    }

    fun mlCenterInParentHorizontally(): JJSearchBarStatic {
        mMotionConstraintSet?.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mMotionConstraintSet?.setHorizontalBias(id, 0.5f)
        return this
    }

    fun mlCenterInParentVertically(bias: Float, topMargin: Int, bottomMargin: Int): JJSearchBarStatic {
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, bottomMargin)
        mMotionConstraintSet?.setVerticalBias(id, bias)
        return this
    }

    fun mlCenterInParentHorizontally(bias: Float, startMargin: Int, endtMargin: Int): JJSearchBarStatic {
        mMotionConstraintSet?.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, startMargin)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, endtMargin)
        mMotionConstraintSet?.setHorizontalBias(id, bias)
        return this
    }


    fun mlCenterInParentTopVertically(): JJSearchBarStatic {
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mMotionConstraintSet?.setVerticalBias(id, 0.5f)
        return this
    }


    fun mlCenterInParentBottomVertically(): JJSearchBarStatic {
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mMotionConstraintSet?.setVerticalBias(id, 0.5f)
        return this
    }

    fun mlCenterInParentStartHorizontally(): JJSearchBarStatic {
        mMotionConstraintSet?.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mMotionConstraintSet?.setHorizontalBias(id, 0.5f)
        return this
    }

    fun mlCenterInParentEndHorizontally(): JJSearchBarStatic {
        mMotionConstraintSet?.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mMotionConstraintSet?.setHorizontalBias(id, 0.5f)
        return this
    }

    fun mlCenterInTopVerticallyOf(viewId: Int): JJSearchBarStatic {
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, viewId, ConstraintSet.TOP, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, viewId, ConstraintSet.TOP, 0)
        mMotionConstraintSet?.setVerticalBias(id, 0.5f)
        return this
    }


    fun mlCenterInBottomVerticallyOf(viewId: Int): JJSearchBarStatic {
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, viewId, ConstraintSet.BOTTOM, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, viewId, ConstraintSet.BOTTOM, 0)
        mMotionConstraintSet?.setVerticalBias(id, 0.5f)
        return this
    }

    fun mlCenterInStartHorizontallyOf(viewId: Int): JJSearchBarStatic {
        mMotionConstraintSet?.connect(id, ConstraintSet.START, viewId, ConstraintSet.START, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, viewId, ConstraintSet.START, 0)
        mMotionConstraintSet?.setHorizontalBias(id, 0.5f)
        return this
    }

    fun mlCenterInEndHorizontallyOf(viewId: Int): JJSearchBarStatic {
        mMotionConstraintSet?.connect(id, ConstraintSet.START, viewId, ConstraintSet.END, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, viewId, ConstraintSet.END, 0)
        mMotionConstraintSet?.setHorizontalBias(id, 0.5f)
        return this
    }

    fun mlCenterVertically(topId: Int, topSide: Int, topMargin: Int, bottomId: Int, bottomSide: Int, bottomMargin: Int, bias: Float): JJSearchBarStatic {
        mMotionConstraintSet?.centerVertically(id, topId, topSide, topMargin, bottomId, bottomSide, bottomMargin, bias)
        return this
    }

    fun mlCenterHorizontally(startId: Int, startSide: Int, startMargin: Int, endId: Int, endSide: Int, endMargin: Int, bias: Float): JJSearchBarStatic {
        mMotionConstraintSet?.centerHorizontally(id, startId, startSide, startMargin, endId, endSide, endMargin, bias)
        return this
    }


    fun mlFillParent(): JJSearchBarStatic {
        mMotionConstraintSet?.constrainWidth(id,0)
        mMotionConstraintSet?.constrainHeight(id,0)
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        return this
    }

    fun mlFillParent(margin: JJMargin): JJSearchBarStatic {
        mMotionConstraintSet?.constrainWidth(id,0)
        mMotionConstraintSet?.constrainHeight(id,0)
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin.top)
        mMotionConstraintSet?.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margin.left)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, margin.right)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin.bottom)
        return this
    }

    fun mlFillParentHorizontally(): JJSearchBarStatic {
        mMotionConstraintSet?.constrainWidth(id,0)
        mMotionConstraintSet?.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        return this
    }

    fun mlFillParentVertically(): JJSearchBarStatic {
        mMotionConstraintSet?.constrainHeight(id,0)
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        return this
    }

    fun mlFillParentHorizontally(startMargin: Int, endMargin: Int): JJSearchBarStatic {
        mMotionConstraintSet?.constrainWidth(id,0)
        mMotionConstraintSet?.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, startMargin)
        mMotionConstraintSet?.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, endMargin)
        return this
    }

    fun mlFillParentVertically(topMargin: Int, bottomMargin: Int): JJSearchBarStatic {
        mMotionConstraintSet?.constrainHeight(id,0)
        mMotionConstraintSet?.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin)
        mMotionConstraintSet?.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, bottomMargin)
        return this
    }

    fun mlVisibility(visibility: Int): JJSearchBarStatic {
        mMotionConstraintSet?.setVisibility(id, visibility)
        return this
    }

    fun mlElevation(elevation: Float): JJSearchBarStatic {
        mMotionConstraintSet?.setElevation(id, elevation)
        return this
    }

    fun mlApply(): JJSearchBarStatic {
        mMotionConstraintSet?.applyTo(parent as ConstraintLayout)
        return this
    }

    fun mlSetConstraint(cs : ConstraintSet?): JJSearchBarStatic {
        mMotionConstraintSet = cs
        return this
    }

    fun mlDisposeConstraint(): JJSearchBarStatic {
        mMotionConstraintSet = null
        return this
    }

    //endregion

    //region ConstraintLayout Params
    protected val mConstraintSet = ConstraintSet()


    fun clFloatCustomAttribute(attrName: String, value: Float): JJSearchBarStatic {
        mConstraintSet.setFloatValue(id,attrName,value)
        return this
    }

    fun clIntCustomAttribute(attrName: String, value: Int): JJSearchBarStatic {
        mConstraintSet.setIntValue(id,attrName,value)
        return this
    }

    fun clColorCustomAttribute(attrName: String, value: Int): JJSearchBarStatic {
        mConstraintSet.setColorValue(id,attrName,value)
        return this
    }

    fun clStringCustomAttribute(attrName: String, value: String): JJSearchBarStatic {
        mConstraintSet.setStringValue(id,attrName,value)
        return this
    }

    fun clRotation(float: Float): JJSearchBarStatic {
        mConstraintSet.setRotation(id,float)
        return this
    }

    fun clRotationX(float: Float): JJSearchBarStatic {
        mConstraintSet.setRotationX(id,float)
        return this
    }

    fun clRotationY(float: Float): JJSearchBarStatic {
        mConstraintSet.setRotationY(id,float)
        return this
    }

    fun clTranslation(x: Float,y: Float): JJSearchBarStatic {
        mConstraintSet.setTranslation(id,x,y)
        return this
    }
    fun clTranslationX(x: Float): JJSearchBarStatic {
        mConstraintSet.setTranslationX(id,x)
        return this
    }

    fun clTranslationY(y: Float): JJSearchBarStatic {
        mConstraintSet.setTranslationY(id,y)
        return this
    }

    fun clTranslationZ(z: Float): JJSearchBarStatic {
        mConstraintSet.setTranslationZ(id,z)
        return this
    }

    fun clTransformPivot(x: Float, y: Float): JJSearchBarStatic {
        mConstraintSet.setTransformPivot(id,x,y)
        return this
    }

    fun clTransformPivotX(x: Float): JJSearchBarStatic {
        mConstraintSet.setTransformPivotX(id,x)
        return this
    }

    fun clTransformPivotY(y: Float): JJSearchBarStatic {
        mConstraintSet.setTransformPivotY(id,y)
        return this
    }

    fun clScaleX(x: Float): JJSearchBarStatic {
        mConstraintSet.setScaleX(id,x)
        return this
    }

    fun clScaleY(y: Float): JJSearchBarStatic {
        mConstraintSet.setScaleY(id,y)
        return this
    }

    fun clDimensionRatio(ratio: String): JJSearchBarStatic {
        mConstraintSet.setDimensionRatio(id,ratio)
        return this
    }

    fun clAlpha(alpha: Float): JJSearchBarStatic {
        mConstraintSet.setAlpha(id,alpha)
        return this
    }


    fun clApply(): JJSearchBarStatic {
        mConstraintSet.applyTo(parent as ConstraintLayout)
        return this
    }

    fun clVisibilityMode(visibility: Int): JJSearchBarStatic {
        mConstraintSet.setVisibilityMode(id, visibility)
        return this
    }

    fun clVerticalBias(float: Float): JJSearchBarStatic {
        mConstraintSet.setVerticalBias(id,float)
        return this
    }
    fun clHorizontalBias(float: Float): JJSearchBarStatic {
        mConstraintSet.setHorizontalBias(id,float)
        return this
    }

    fun clCenterHorizontallyOf(viewId: Int, marginStart: Int = 0, marginEnd: Int = 0): JJSearchBarStatic {
        mConstraintSet.connect(this.id, ConstraintSet.START, viewId, ConstraintSet.START, marginStart)
        mConstraintSet.connect(this.id, ConstraintSet.END, viewId, ConstraintSet.END, marginEnd)
        mConstraintSet.setHorizontalBias(id,0.5f)
        return this
    }
    fun clCenterVerticallyOf(viewId: Int,marginTop: Int = 0, marginBottom: Int = 0): JJSearchBarStatic {
        mConstraintSet.connect(this.id, ConstraintSet.TOP, viewId, ConstraintSet.TOP, marginTop)
        mConstraintSet.connect(this.id, ConstraintSet.BOTTOM, viewId, ConstraintSet.BOTTOM, marginBottom)
        mConstraintSet.setVerticalBias(id,0.5f)
        return this
    }

    fun clMargins(margins: JJMargin) : JJSearchBarStatic {
        mConstraintSet.setMargin(id, ConstraintSet.TOP,margins.top)
        mConstraintSet.setMargin(id, ConstraintSet.BOTTOM,margins.bottom)
        mConstraintSet.setMargin(id, ConstraintSet.END,margins.right)
        mConstraintSet.setMargin(id, ConstraintSet.START,margins.left)
        return this
    }


    fun clTopToTop(viewId: Int, margin: Int = 0): JJSearchBarStatic {
        mConstraintSet.connect(this.id, ConstraintSet.TOP, viewId, ConstraintSet.TOP, margin)
        return this
    }

    fun clTopToTopParent(margin: Int = 0): JJSearchBarStatic {
        mConstraintSet.connect(this.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin)
        return this
    }


    fun clTopToBottom(viewId: Int, margin: Int = 0): JJSearchBarStatic {
        mConstraintSet.connect(this.id, ConstraintSet.TOP, viewId, ConstraintSet.BOTTOM, margin)
        return this
    }

    fun clTopToBottomParent(margin: Int = 0): JJSearchBarStatic {
        mConstraintSet.connect(this.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin)
        return this
    }

    fun clBottomToTop(viewId: Int, margin: Int = 0): JJSearchBarStatic {
        mConstraintSet.connect(this.id, ConstraintSet.BOTTOM, viewId, ConstraintSet.TOP, margin)
        return this
    }

    fun clBottomToTopParent(margin: Int = 0): JJSearchBarStatic {
        mConstraintSet.connect(this.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin)
        return this
    }

    fun clBottomToBottom(viewId: Int, margin: Int = 0): JJSearchBarStatic {
        mConstraintSet.connect(this.id, ConstraintSet.BOTTOM, viewId, ConstraintSet.BOTTOM, margin)
        return this
    }

    fun clBottomToBottomParent(margin: Int = 0): JJSearchBarStatic {
        mConstraintSet.connect(this.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin)
        return this
    }

    fun clStartToStart(viewId: Int, margin: Int = 0): JJSearchBarStatic {
        mConstraintSet.connect(this.id, ConstraintSet.START, viewId, ConstraintSet.START, margin)
        return this
    }

    fun clStartToStartParent(margin: Int = 0): JJSearchBarStatic {
        mConstraintSet.connect(this.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margin)
        return this
    }

    fun clStartToEnd(viewId: Int, margin: Int = 0): JJSearchBarStatic {
        mConstraintSet.connect(this.id, ConstraintSet.START, viewId, ConstraintSet.END, margin)
        return this
    }

    fun clStartToEndParent(margin: Int = 0): JJSearchBarStatic {
        mConstraintSet.connect(this.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.END, margin)
        return this
    }

    fun clEndToEnd(viewId: Int, margin: Int = 0): JJSearchBarStatic {
        mConstraintSet.connect(this.id, ConstraintSet.END, viewId, ConstraintSet.END, margin)
        return this
    }

    fun clEndToEndParent(margin: Int = 0): JJSearchBarStatic {
        mConstraintSet.connect(this.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, margin)
        return this
    }


    fun clEndToStart(viewId: Int, margin: Int = 0): JJSearchBarStatic {
        mConstraintSet.connect(this.id, ConstraintSet.END, viewId, ConstraintSet.START, margin)
        return this
    }

    fun clEndToStartParent(margin: Int = 0): JJSearchBarStatic {
        mConstraintSet.connect(this.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.START, margin)
        return this
    }


    fun clWidth(width: Int): JJSearchBarStatic {
        mConstraintSet.constrainWidth(id, width)
        return this
    }

    fun clHeight(height: Int): JJSearchBarStatic {
        mConstraintSet.constrainHeight(id, height)
        return this
    }

    fun clPercentWidth(width: Float): JJSearchBarStatic {
        mConstraintSet.constrainPercentWidth(id, width)
        return this
    }

    fun clPercentHeight(height: Float): JJSearchBarStatic {
        mConstraintSet.constrainPercentHeight(id, height)
        return this
    }

    fun clCenterInParent(): JJSearchBarStatic {
        mConstraintSet.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSet.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mConstraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mConstraintSet.setVerticalBias(id, 0.5f)
        mConstraintSet.setHorizontalBias(id, 0.5f)
        return this
    }

    fun clCenterInParent(verticalBias: Float, horizontalBias: Float, margin: JJMargin): JJSearchBarStatic {
        mConstraintSet.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margin.left)
        mConstraintSet.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, margin.right)
        mConstraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin.top)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin.bottom)
        mConstraintSet.setVerticalBias(id, verticalBias)
        mConstraintSet.setHorizontalBias(id, horizontalBias)
        return this
    }

    fun clCenterInParentVertically(): JJSearchBarStatic {
        mConstraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mConstraintSet.setVerticalBias(id, 0.5f)
        return this
    }

    fun clCenterInParentHorizontally(): JJSearchBarStatic {
        mConstraintSet.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSet.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mConstraintSet.setHorizontalBias(id, 0.5f)
        return this
    }

    fun clCenterInParentVertically(bias: Float, topMargin: Int, bottomMargin: Int): JJSearchBarStatic {
        mConstraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, bottomMargin)
        mConstraintSet.setVerticalBias(id, bias)
        return this
    }

    fun clCenterInParentHorizontally(bias: Float, startMargin: Int, endtMargin: Int): JJSearchBarStatic {
        mConstraintSet.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, startMargin)
        mConstraintSet.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, endtMargin)
        mConstraintSet.setHorizontalBias(id, bias)
        return this
    }


    fun clCenterInParentTopVertically(): JJSearchBarStatic {
        mConstraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSet.setVerticalBias(id, 0.5f)
        return this
    }


    fun clCenterInParentBottomVertically(): JJSearchBarStatic {
        mConstraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mConstraintSet.setVerticalBias(id, 0.5f)
        return this
    }

    fun clCenterInParentStartHorizontally(): JJSearchBarStatic {
        mConstraintSet.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSet.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSet.setHorizontalBias(id, 0.5f)
        return this
    }

    fun clCenterInParentEndHorizontally(): JJSearchBarStatic {
        mConstraintSet.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mConstraintSet.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mConstraintSet.setHorizontalBias(id, 0.5f)
        return this
    }

    fun clCenterInTopVertically(topId: Int): JJSearchBarStatic {
        mConstraintSet.connect(id, ConstraintSet.TOP, topId, ConstraintSet.TOP, 0)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, topId, ConstraintSet.TOP, 0)
        mConstraintSet.setVerticalBias(id, 0.5f)
        return this
    }


    fun clCenterInBottomVertically(bottomId: Int): JJSearchBarStatic {
        mConstraintSet.connect(id, ConstraintSet.TOP, bottomId, ConstraintSet.BOTTOM, 0)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, bottomId, ConstraintSet.BOTTOM, 0)
        mConstraintSet.setVerticalBias(id, 0.5f)
        return this
    }

    fun clCenterInStartHorizontally(startId: Int): JJSearchBarStatic {
        mConstraintSet.connect(id, ConstraintSet.START, startId, ConstraintSet.START, 0)
        mConstraintSet.connect(id, ConstraintSet.END, startId, ConstraintSet.START, 0)
        mConstraintSet.setHorizontalBias(id, 0.5f)
        return this
    }

    fun clCenterInEndHorizontally(endId: Int): JJSearchBarStatic {
        mConstraintSet.connect(id, ConstraintSet.START, endId, ConstraintSet.END, 0)
        mConstraintSet.connect(id, ConstraintSet.END, endId, ConstraintSet.END, 0)
        mConstraintSet.setHorizontalBias(id, 0.5f)
        return this
    }

    fun clCenterVertically(topId: Int, topSide: Int, topMargin: Int, bottomId: Int, bottomSide: Int, bottomMargin: Int, bias: Float): JJSearchBarStatic {
        mConstraintSet.centerVertically(id, topId, topSide, topMargin, bottomId, bottomSide, bottomMargin, bias)
        return this
    }

    fun clCenterHorizontally(startId: Int, startSide: Int, startMargin: Int, endId: Int, endSide: Int, endMargin: Int, bias: Float): JJSearchBarStatic {
        mConstraintSet.centerHorizontally(id, startId, startSide, startMargin, endId, endSide, endMargin, bias)
        return this
    }


    fun clFillParent(): JJSearchBarStatic {
        mConstraintSet.constrainWidth(id,0)
        mConstraintSet.constrainHeight(id,0)
        mConstraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSet.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSet.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        return this
    }

    fun clFillParent(margin: JJMargin): JJSearchBarStatic {
        mConstraintSet.constrainWidth(id,0)
        mConstraintSet.constrainHeight(id,0)
        mConstraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin.top)
        mConstraintSet.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margin.left)
        mConstraintSet.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, margin.right)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin.bottom)
        return this
    }

    fun clFillParentHorizontally(): JJSearchBarStatic {
        mConstraintSet.constrainWidth(id,0)
        mConstraintSet.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSet.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        return this
    }

    fun clFillParentVertically(): JJSearchBarStatic {
        mConstraintSet.constrainHeight(id,0)
        mConstraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        return this
    }

    fun clFillParentHorizontally(startMargin: Int, endMargin: Int): JJSearchBarStatic {
        mConstraintSet.constrainWidth(id,0)
        mConstraintSet.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, startMargin)
        mConstraintSet.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, endMargin)
        return this
    }

    fun clFillParentVertically(topMargin: Int, bottomMargin: Int): JJSearchBarStatic {
        mConstraintSet.constrainHeight(id,0)
        mConstraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin)
        mConstraintSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, bottomMargin)
        return this
    }

    fun clVisibility(visibility: Int): JJSearchBarStatic {
        mConstraintSet.setVisibility(id, visibility)
        return this
    }



    fun clElevation(elevation: Float): JJSearchBarStatic {
        mConstraintSet.setElevation(id, elevation)

        return this
    }

    fun clConstraintSet() : ConstraintSet {
        return mConstraintSet
    }

    fun clMinWidth(w:Int): JJSearchBarStatic {
        mConstraintSet.constrainMinWidth(id,w)
        return this
    }

    fun clMinHeight(h:Int): JJSearchBarStatic {
        mConstraintSet.constrainMinHeight(id,h)
        return this
    }

    fun clMaxWidth(w:Int): JJSearchBarStatic {
        mConstraintSet.constrainMaxWidth(id,w)
        return this
    }

    fun clMaxHeight(h:Int): JJSearchBarStatic {
        mConstraintSet.constrainMaxHeight(id,h)
        return this
    }






    //endregion

    //region ConstraintLayout LandScape Params
    protected val mConstraintSetLandScape = ConstraintSet()

    fun cllApply(): JJSearchBarStatic {
        mConstraintSetLandScape.applyTo(parent as ConstraintLayout)
        return this
    }


    fun cllFloatCustomAttribute(attrName: String, value: Float): JJSearchBarStatic {
        mConstraintSet.setFloatValue(id,attrName,value)
        return this
    }

    fun cllIntCustomAttribute(attrName: String, value: Int): JJSearchBarStatic {
        mConstraintSet.setIntValue(id,attrName,value)
        return this
    }

    fun cllColorCustomAttribute(attrName: String, value: Int): JJSearchBarStatic {
        mConstraintSet.setColorValue(id,attrName,value)
        return this
    }

    fun cllStringCustomAttribute(attrName: String, value: String): JJSearchBarStatic {
        mConstraintSet.setStringValue(id,attrName,value)
        return this
    }

    fun cllRotation(float: Float): JJSearchBarStatic {
        mConstraintSet.setRotation(id,float)
        return this
    }

    fun cllRotationX(float: Float): JJSearchBarStatic {
        mConstraintSet.setRotationX(id,float)
        return this
    }

    fun cllRotationY(float: Float): JJSearchBarStatic {
        mConstraintSet.setRotationY(id,float)
        return this
    }

    fun cllTranslation(x: Float,y: Float): JJSearchBarStatic {
        mConstraintSet.setTranslation(id,x,y)
        return this
    }
    fun cllTranslationX(x: Float): JJSearchBarStatic {
        mConstraintSet.setTranslationX(id,x)
        return this
    }

    fun cllTranslationY(y: Float): JJSearchBarStatic {
        mConstraintSet.setTranslationY(id,y)
        return this
    }

    fun cllTranslationZ(z: Float): JJSearchBarStatic {
        mConstraintSet.setTranslationZ(id,z)
        return this
    }

    fun cllTransformPivot(x: Float, y: Float): JJSearchBarStatic {
        mConstraintSet.setTransformPivot(id,x,y)
        return this
    }

    fun cllTransformPivotX(x: Float): JJSearchBarStatic {
        mConstraintSet.setTransformPivotX(id,x)
        return this
    }

    fun cllTransformPivotY(y: Float): JJSearchBarStatic {
        mConstraintSet.setTransformPivotY(id,y)
        return this
    }

    fun cllScaleX(x: Float): JJSearchBarStatic {
        mConstraintSet.setScaleX(id,x)
        return this
    }

    fun cllScaleY(y: Float): JJSearchBarStatic {
        mConstraintSet.setScaleY(id,y)
        return this
    }

    fun cllDimensionRatio(ratio: String): JJSearchBarStatic {
        mConstraintSet.setDimensionRatio(id,ratio)
        return this
    }

    fun cllAlpha(alpha: Float): JJSearchBarStatic {
        mConstraintSet.setAlpha(id,alpha)
        return this
    }


    fun cllVisibilityMode(visibility: Int): JJSearchBarStatic {
        mConstraintSetLandScape.setVisibilityMode(id, visibility)
        return this
    }

    fun cllVerticalBias(float: Float): JJSearchBarStatic {
        mConstraintSetLandScape.setVerticalBias(id,float)
        return this
    }
    fun cllHorizontalBias(float: Float): JJSearchBarStatic {
        mConstraintSetLandScape.setHorizontalBias(id,float)
        return this
    }

    fun cllCenterHorizontallyOf(viewId: Int, marginStart: Int = 0, marginEnd: Int = 0): JJSearchBarStatic {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.START, viewId, ConstraintSet.START, marginStart)
        mConstraintSetLandScape.connect(this.id, ConstraintSet.END, viewId, ConstraintSet.END, marginEnd)
        mConstraintSetLandScape.setHorizontalBias(id,0.5f)
        return this
    }
    fun cllCenterVerticallyOf(viewId: Int,marginTop: Int = 0, marginBottom: Int = 0): JJSearchBarStatic {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.TOP, viewId, ConstraintSet.TOP, marginTop)
        mConstraintSetLandScape.connect(this.id, ConstraintSet.BOTTOM, viewId, ConstraintSet.BOTTOM, marginBottom)
        mConstraintSetLandScape.setVerticalBias(id,0.5f)
        return this
    }

    fun cllMargins(margins: JJMargin) : JJSearchBarStatic {
        mConstraintSetLandScape.setMargin(id, ConstraintSet.TOP,margins.top)
        mConstraintSetLandScape.setMargin(id, ConstraintSet.BOTTOM,margins.bottom)
        mConstraintSetLandScape.setMargin(id, ConstraintSet.END,margins.right)
        mConstraintSetLandScape.setMargin(id, ConstraintSet.START,margins.left)
        return this
    }


    fun cllTopToTop(viewId: Int, margin: Int = 0): JJSearchBarStatic {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.TOP, viewId, ConstraintSet.TOP, margin)
        return this
    }

    fun cllTopToTopParent(margin: Int = 0): JJSearchBarStatic {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin)
        return this
    }


    fun cllTopToBottom(viewId: Int, margin: Int = 0): JJSearchBarStatic {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.TOP, viewId, ConstraintSet.BOTTOM, margin)
        return this
    }

    fun cllTopToBottomParent(margin: Int = 0): JJSearchBarStatic {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin)
        return this
    }

    fun cllBottomToTop(viewId: Int, margin: Int = 0): JJSearchBarStatic {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.BOTTOM, viewId, ConstraintSet.TOP, margin)
        return this
    }

    fun cllBottomToTopParent(margin: Int = 0): JJSearchBarStatic {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin)
        return this
    }

    fun cllBottomToBottom(viewId: Int, margin: Int = 0): JJSearchBarStatic {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.BOTTOM, viewId, ConstraintSet.BOTTOM, margin)
        return this
    }

    fun cllBottomToBottomParent(margin: Int = 0): JJSearchBarStatic {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin)
        return this
    }

    fun cllStartToStart(viewId: Int, margin: Int = 0): JJSearchBarStatic {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.START, viewId, ConstraintSet.START, margin)
        return this
    }

    fun cllStartToStartParent(margin: Int = 0): JJSearchBarStatic {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margin)
        return this
    }

    fun cllStartToEnd(viewId: Int, margin: Int = 0): JJSearchBarStatic {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.START, viewId, ConstraintSet.END, margin)
        return this
    }

    fun cllStartToEndParent(margin: Int = 0): JJSearchBarStatic {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.END, margin)
        return this
    }

    fun cllEndToEnd(viewId: Int, margin: Int = 0): JJSearchBarStatic {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.END, viewId, ConstraintSet.END, margin)
        return this
    }

    fun cllEndToEndParent(margin: Int = 0): JJSearchBarStatic {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, margin)
        return this
    }


    fun cllEndToStart(viewId: Int, margin: Int = 0): JJSearchBarStatic {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.END, viewId, ConstraintSet.START, margin)
        return this
    }

    fun cllEndToStartParent(margin: Int = 0): JJSearchBarStatic {
        mConstraintSetLandScape.connect(this.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.START, margin)
        return this
    }


    fun cllWidth(width: Int): JJSearchBarStatic {
        mConstraintSetLandScape.constrainWidth(id, width)
        return this
    }

    fun cllHeight(height: Int): JJSearchBarStatic {
        mConstraintSetLandScape.constrainHeight(id, height)
        return this
    }

    fun cllPercentWidth(width: Float): JJSearchBarStatic {
        mConstraintSetLandScape.constrainPercentWidth(id, width)
        return this
    }

    fun cllPercentHeight(height: Float): JJSearchBarStatic {
        mConstraintSetLandScape.constrainPercentHeight(id, height)
        return this
    }

    fun cllCenterInParent(): JJSearchBarStatic {
        mConstraintSetLandScape.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mConstraintSetLandScape.setVerticalBias(id, 0.5f)
        mConstraintSetLandScape.setHorizontalBias(id, 0.5f)
        return this
    }

    fun cllCenterInParent(verticalBias: Float, horizontalBias: Float, margin: JJMargin): JJSearchBarStatic {
        mConstraintSetLandScape.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margin.left)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, margin.right)
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin.top)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin.bottom)
        mConstraintSetLandScape.setVerticalBias(id, verticalBias)
        mConstraintSetLandScape.setHorizontalBias(id, horizontalBias)
        return this
    }

    fun cllCenterInParentVertically(): JJSearchBarStatic {
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mConstraintSetLandScape.setVerticalBias(id, 0.5f)
        return this
    }

    fun cllCenterInParentHorizontally(): JJSearchBarStatic {
        mConstraintSetLandScape.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mConstraintSetLandScape.setHorizontalBias(id, 0.5f)
        return this
    }

    fun cllCenterInParentVertically(bias: Float, topMargin: Int, bottomMargin: Int): JJSearchBarStatic {
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, bottomMargin)
        mConstraintSetLandScape.setVerticalBias(id, bias)
        return this
    }

    fun cllCenterInParentHorizontally(bias: Float, startMargin: Int, endtMargin: Int): JJSearchBarStatic {
        mConstraintSetLandScape.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, startMargin)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, endtMargin)
        mConstraintSetLandScape.setHorizontalBias(id, bias)
        return this
    }


    fun cllCenterInParentTopVertically(): JJSearchBarStatic {
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSetLandScape.setVerticalBias(id, 0.5f)
        return this
    }


    fun cllCenterInParentBottomVertically(): JJSearchBarStatic {
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        mConstraintSetLandScape.setVerticalBias(id, 0.5f)
        return this
    }

    fun cllCenterInParentStartHorizontally(): JJSearchBarStatic {
        mConstraintSetLandScape.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSetLandScape.setHorizontalBias(id, 0.5f)
        return this
    }

    fun cllCenterInParentEndHorizontally(): JJSearchBarStatic {
        mConstraintSetLandScape.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mConstraintSetLandScape.setHorizontalBias(id, 0.5f)
        return this
    }

    fun cllCenterInTopVertically(topId: Int): JJSearchBarStatic {
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, topId, ConstraintSet.TOP, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, topId, ConstraintSet.TOP, 0)
        mConstraintSetLandScape.setVerticalBias(id, 0.5f)
        return this
    }


    fun cllCenterInBottomVertically(bottomId: Int): JJSearchBarStatic {
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, bottomId, ConstraintSet.BOTTOM, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, bottomId, ConstraintSet.BOTTOM, 0)
        mConstraintSetLandScape.setVerticalBias(id, 0.5f)
        return this
    }

    fun cllCenterInStartHorizontally(startId: Int): JJSearchBarStatic {
        mConstraintSetLandScape.connect(id, ConstraintSet.START, startId, ConstraintSet.START, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, startId, ConstraintSet.START, 0)
        mConstraintSetLandScape.setHorizontalBias(id, 0.5f)
        return this
    }

    fun cllCenterInEndHorizontally(endId: Int): JJSearchBarStatic {
        mConstraintSetLandScape.connect(id, ConstraintSet.START, endId, ConstraintSet.END, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, endId, ConstraintSet.END, 0)
        mConstraintSetLandScape.setHorizontalBias(id, 0.5f)
        return this
    }

    fun cllCenterVertically(topId: Int, topSide: Int, topMargin: Int, bottomId: Int, bottomSide: Int, bottomMargin: Int, bias: Float): JJSearchBarStatic {
        mConstraintSetLandScape.centerVertically(id, topId, topSide, topMargin, bottomId, bottomSide, bottomMargin, bias)
        return this
    }

    fun cllCenterHorizontally(startId: Int, startSide: Int, startMargin: Int, endId: Int, endSide: Int, endMargin: Int, bias: Float): JJSearchBarStatic {
        mConstraintSetLandScape.centerHorizontally(id, startId, startSide, startMargin, endId, endSide, endMargin, bias)
        return this
    }


    fun cllFillParent(): JJSearchBarStatic {
        mConstraintSetLandScape.constrainWidth(id,0)
        mConstraintSetLandScape.constrainHeight(id,0)
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        return this
    }

    fun cllFillParent(margin: JJMargin): JJSearchBarStatic {
        mConstraintSetLandScape.constrainWidth(id,0)
        mConstraintSetLandScape.constrainHeight(id,0)
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin.top)
        mConstraintSetLandScape.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margin.left)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, margin.right)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin.bottom)
        return this
    }

    fun cllFillParentHorizontally(): JJSearchBarStatic {
        mConstraintSetLandScape.constrainWidth(id,0)
        mConstraintSetLandScape.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        return this
    }

    fun cllFillParentVertically(): JJSearchBarStatic {
        mConstraintSetLandScape.constrainHeight(id,0)
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        return this
    }

    fun cllFillParentHorizontally(startMargin: Int, endMargin: Int): JJSearchBarStatic {
        mConstraintSetLandScape.constrainWidth(id,0)
        mConstraintSetLandScape.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, startMargin)
        mConstraintSetLandScape.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, endMargin)
        return this
    }

    fun cllFillParentVertically(topMargin: Int, bottomMargin: Int): JJSearchBarStatic {
        mConstraintSetLandScape.constrainHeight(id,0)
        mConstraintSetLandScape.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin)
        mConstraintSetLandScape.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, bottomMargin)
        return this
    }

    fun cllVisibility(visibility: Int): JJSearchBarStatic {
        mConstraintSetLandScape.setVisibility(id, visibility)
        return this
    }



    fun cllElevation(elevation: Float): JJSearchBarStatic {
        mConstraintSetLandScape.setElevation(id, elevation)

        return this
    }

    fun cllConstraintSet() : ConstraintSet {
        return mConstraintSetLandScape
    }

    fun cllMinWidth(w:Int): JJSearchBarStatic {
        mConstraintSetLandScape.constrainMinWidth(id,w)
        return this
    }

    fun cllMinHeight(h:Int): JJSearchBarStatic {
        mConstraintSetLandScape.constrainMinHeight(id,h)
        return this
    }

    fun cllMaxWidth(w:Int): JJSearchBarStatic {
        mConstraintSetLandScape.constrainMaxWidth(id,w)
        return this
    }

    fun cllMaxHeight(h:Int): JJSearchBarStatic {
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

    fun llWidth(width: Int): JJSearchBarStatic {
        setupLlp()
        mLlp!!.width = width
        return this
    }

    fun llHeight(height: Int): JJSearchBarStatic {
        setupLlp()
        mLlp!!.height = height
        return this
    }

    fun llWeight(weigth: Float): JJSearchBarStatic {
        setupLlp()
        mLlp!!.weight = weigth
        return this
    }

    fun llGravity(gravity: Int): JJSearchBarStatic {
        setupLlp()
        mLlp!!.gravity = gravity
        return this
    }

    fun llTopMargin(m : Int): JJSearchBarStatic {
        setupLlp()
        mLlp!!.topMargin = m
        return this
    }

    fun llBottomMargin(m : Int): JJSearchBarStatic {
        setupLlp()
        mLlp!!.bottomMargin = m
        return this
    }

    fun llStartMargin(m : Int): JJSearchBarStatic {
        setupLlp()
        mLlp!!.marginStart = m
        return this
    }

    fun llEndMargin(m : Int): JJSearchBarStatic {
        setupLlp()
        mLlp!!.marginEnd = m
        return this
    }

    fun llLeftMargin(m : Int): JJSearchBarStatic {
        setupLlp()
        mLlp!!.leftMargin = m
        return this
    }

    fun llRightMargin(m : Int): JJSearchBarStatic {
        setupLlp()
        mLlp!!.rightMargin = m
        return this
    }


    fun llMargins( margins : JJMargin): JJSearchBarStatic {
        setupLlp()
        mLlp!!.setMargins(margins.left,margins.top,margins.right,margins.bottom)
        return this
    }

    //endregion


}