package com.jjlf.jjkit_layoutplus.utils

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.Typeface.MONOSPACE
import android.os.Build
import android.os.LocaleList
import android.text.Layout
import android.text.method.TransformationMethod
import android.util.AttributeSet
import android.util.SparseIntArray
import android.view.View
import android.view.View.TEXT_ALIGNMENT_TEXT_START
import android.widget.TextView
import androidx.annotation.RequiresApi
import org.w3c.dom.Text
import kotlin.math.min

class JJTextAttributes {
    //region variables
    var  mTextColorHighlight = 0
    var  mTextColor : ColorStateList? = null
    var  mTextColorHint : ColorStateList? = null
    var  mTextColorLink : ColorStateList? = null
    var  mTextSize = -1
    var  mTextLocales: LocaleList? = null
    var  mFontFamily:String? = null
    var  mFontTypeface: Typeface? = null
    var  mFontFamilyExplicit = false;
    var  mTypefaceIndex = -1
    var  mTextStyle = 0
    var  mFontWeight = -1
    var  mAllCaps = false
    var  mShadowColor = 0
    var  mShadowDx = 0f
    var  mShadowDy = 0f
    var  mShadowRadius = 0f
    var  mHasElegant = false
    var  mElegant = false
    var  mHasFallbackLineSpacing = false
    var  mFallbackLineSpacing = false
    var  mHasLetterSpacing = false
    var  mLetterSpacing = 0f
    var  mFontFeatureSettings:String? = null
    var  mFontVariationSettings:String? = null

    var mHint:String? = null
    var mGravity:Int? = null
    var mTextAlignment :Int? = null


    //endregion

    //region set


    fun setHint(t: TypedArray, styleableId:Int){
        mHint = t.getString(styleableId)
    }
    fun setHintTextColor(t: TypedArray, styleableId:Int){
        mTextColorHint = t.getColorStateList(styleableId)
    }
    fun setFontFamily(context:Context, t: TypedArray, styleableId:Int){
        if (!context.isRestricted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                mFontTypeface = t.getFont(styleableId )
            } catch (e :Exception ) {
                // Expected if it is not a font resource.
            }
        }
        if ( mFontTypeface == null) {
            mFontFamily = t.getString(styleableId)

        }
        mFontFamilyExplicit = true
    }

    fun setTypeFace(t: TypedArray, styleableId:Int){
        mTypefaceIndex = t.getInt(styleableId, mTypefaceIndex)
        if ( mTypefaceIndex != -1 && !mFontFamilyExplicit) {
            mFontFamily = null;
        }
    }

    fun setTextStyle(t: TypedArray, styleableId:Int){
        mTextStyle = t.getInt(styleableId, mTextStyle)
    }

    fun setGravity(t: TypedArray, styleableId:Int){
        mGravity = t.getInt(styleableId, -1)
    }

    fun setTextAlignment(t: TypedArray, styleableId:Int){
        mTextAlignment = t.getInt(styleableId,0)
    }
    fun setTextSize(t: TypedArray, styleableId:Int){
        mTextSize = t.getDimensionPixelSize(styleableId, -1)
    }
    fun setTextColor(t: TypedArray, styleableId:Int){
        mTextColor = t.getColorStateList(styleableId)
    }

    fun setTextColorHighlight(t: TypedArray, styleableId:Int){
        mTextColorHighlight = t.getColor(styleableId, mTextColorHighlight)
    }
    fun setTextColorLink(t: TypedArray, styleableId:Int){
        mTextColorLink = t.getColorStateList(styleableId)
    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun setTextLocale(t: TypedArray, styleableId:Int){
        val localeString = t.getString(styleableId)
        if (localeString != null) {
            val localeList = LocaleList.forLanguageTags(localeString);
            if (!localeList.isEmpty) {
                mTextLocales = localeList
            }
        }
    }

    fun setFontWeight(t: TypedArray, styleableId:Int){
        mFontWeight = t.getInt(styleableId, mFontWeight)
    }
    fun setAllCaps(t: TypedArray, styleableId:Int){
        mAllCaps = t.getBoolean(styleableId, mAllCaps)
    }
    fun setShadowColor(t: TypedArray, styleableId:Int){
        mShadowColor = t.getInt(styleableId, mShadowColor)
    }
    fun setShadowDx(t: TypedArray, styleableId:Int){
        mShadowDx = t.getFloat(styleableId, mShadowDx)
    }
    fun setShadowDy(t: TypedArray, styleableId:Int){
        mShadowDy = t.getFloat(styleableId, mShadowDy)
    }
    fun setShadowRadius(t: TypedArray, styleableId:Int){
        mShadowRadius = t.getFloat(styleableId, mShadowRadius)
    }
    fun setElegant(t: TypedArray, styleableId:Int){
        mHasElegant = true
        mElegant = t.getBoolean(styleableId, mElegant)
    }
    fun setFallbackLineSpacing(t: TypedArray, styleableId:Int){
        mHasFallbackLineSpacing = true;
        mFallbackLineSpacing = t.getBoolean(styleableId, mFallbackLineSpacing)
    }
    fun setLetterSpacing(t: TypedArray, styleableId:Int){
        mHasLetterSpacing = true;
        mLetterSpacing = t.getFloat(styleableId, mLetterSpacing)
    }
    fun setFontFeatureSettings(t: TypedArray, styleableId:Int){
        mFontFeatureSettings = t.getString(styleableId)
    }
    fun setFontVariationSettings(t: TypedArray, styleableId:Int){
        mFontVariationSettings = t.getString(styleableId)
    }


    //endregion


    fun  apply(context: Context,v:TextView) {
        if(mHint != null){
            v.hint = mHint
        }
        if(mGravity != null){
            v.gravity = mGravity!!
        }
        if(mTextAlignment != null){
            v.textAlignment = mTextAlignment!!
        }
        if (mTextColor != null) {
            v.setTextColor(mTextColor!!)
        }

        if (mTextColorHint != null) {
            v.setHintTextColor(mTextColorHint);
        }

        if (mTextColorLink != null) {
            v.setLinkTextColor(mTextColorLink);
        }

        if (mTextColorHighlight != 0) {
            v.highlightColor = mTextColorHighlight;
        }

        if (mTextSize != -1) {
            if (mTextSize.toFloat() != v.paint.textSize) {
                v.paint.textSize = mTextSize.toFloat()
            }
        }

        if (mTextLocales != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            v.textLocales = mTextLocales!!
        }

        if (mTypefaceIndex != -1 && !mFontFamilyExplicit) {
            mFontFamily = null;
        }

        setTypefaceFromAttrs(v,mFontTypeface, mFontFamily,
            mTypefaceIndex, mTextStyle, mFontWeight)

        if (mShadowColor != 0) {
            v.setShadowLayer(mShadowRadius, mShadowDx, mShadowDy,
                mShadowColor);
        }

        if (mAllCaps) {
            v.transformationMethod = object: TransformationMethod {
                override fun onFocusChanged(view: View?, sourceText: CharSequence?, focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {}
                override fun getTransformation(source: CharSequence?, view: View?): CharSequence? {
                    return source?.toString()?.toUpperCase(v.context.resources.configuration.locale)
                }
            }
        }
        if (mHasElegant) {
            v.isElegantTextHeight = mElegant
        }

        if (mHasFallbackLineSpacing && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            v.isFallbackLineSpacing = mFallbackLineSpacing;
        }

        if (mHasLetterSpacing) {
            v.letterSpacing = mLetterSpacing.toFloat()
        }

        if (mFontFeatureSettings != null) {
            v.fontFeatureSettings = mFontFeatureSettings
        }

        if (mFontVariationSettings != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
            v.fontVariationSettings = mFontVariationSettings;
        }
    }

    private fun setTypefaceFromAttrs( v:TextView,typeface: Typeface?,   familyName:String?, typefaceIndex:Int, style:Int,
                                      weight:Int) {
        if (typeface == null && familyName != null) {
            // Lookup normal Typeface from system font map.
            val  normalTypeface = Typeface.create(familyName, Typeface.NORMAL)
            resolveStyleAndSetTypeface(v,normalTypeface, style, weight);
        } else if (typeface != null) {
            resolveStyleAndSetTypeface(v,typeface, style, weight)
        } else {  // both typeface and familyName is null.
            when (typefaceIndex) {
                1 -> resolveStyleAndSetTypeface(v,Typeface.SANS_SERIF, style, weight)
                2 -> resolveStyleAndSetTypeface(v,Typeface.SERIF, style, weight)
                3 ->  resolveStyleAndSetTypeface(v,MONOSPACE, style, weight)
                else -> resolveStyleAndSetTypeface(v,null, style, weight)

            }
        }
    }

    private fun resolveStyleAndSetTypeface( v:TextView,typeface: Typeface?, style: Int,
                                            weight:Int) {
        var w = weight
        if (w >= 0) {
            w = min(1000,w)
            val  italic = (style and Typeface.ITALIC) != 0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                v.typeface = Typeface.create(typeface, w, italic)
            }else{
                v.setTypeface(typeface, style)
            }
        } else {
            v.setTypeface(typeface, style)
        }
    }


    private fun getArrayTextAttributes(): IntArray{
        return intArrayOf(
            android.R.attr.hint,
            android.R.attr.textColor,
            android.R.attr.textSize,
            android.R.attr.textAlignment,
            android.R.attr.gravity,
            android.R.attr.fontFamily,
            android.R.attr.typeface,
            android.R.attr.textStyle
        )
    }

    companion object{
        val values =  SparseIntArray().apply {
            put(android.R.attr.hint, 0)
            put(android.R.attr.textColor, 1)
            put(android.R.attr.textSize, 2)
            put(android.R.attr.textAlignment, 3)
            put(android.R.attr.gravity, 4)
            put(android.R.attr.fontFamily, 5)
            put(android.R.attr.typeface, 6)
            put(android.R.attr.textStyle, 7)
        }

    }



}