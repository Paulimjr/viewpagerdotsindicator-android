package com.viewpagerindicator

import android.content.Context
import android.graphics.Color
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.tbuonomo.viewpagerdotsindicator.OnPageChangeListenerHelper

abstract class BaseDotsIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        const val DEFAULT_POINT_COLOR = Color.CYAN
    }

    /** #############
     *
     *
     *  ATE AQUI OK!
     *
     *
     *
     *
     * */

    enum class Type(
        val defaultSize: Float,
        val defaultSpacing: Float,
        val styleableId: IntArray,
        val dotsColorId: Int,
        val dotsSizeId: Int,
        val dotsSpacingId: Int,
        val dotsCornerRadiusId: Int
    ) {
        DEFAULT(
            16f,
            8f,
            R.styleable.SpringDotsIndicator,
            R.styleable.SpringDotsIndicator_dotsColor,
            R.styleable.SpringDotsIndicator_dotsSize,
            R.styleable.SpringDotsIndicator_dotsSpacing,
            R.styleable.SpringDotsIndicator_dotsCornerRadius
        ),
        SPRING(
            16f,
            4f,
            R.styleable.DotsIndicator,
            R.styleable.DotsIndicator_dotsColor,
            R.styleable.DotsIndicator_dotsSize,
            R.styleable.DotsIndicator_dotsSpacing,
            R.styleable.DotsIndicator_dotsCornerRadius
        ),
        WORM(
            16f,
            4f,
            R.styleable.WormDotsIndicator,
            R.styleable.WormDotsIndicator_dotsColor,
            R.styleable.WormDotsIndicator_dotsSize,
            R.styleable.WormDotsIndicator_dotsSpacing,
            R.styleable.WormDotsIndicator_dotsCornerRadius
        )
    }

    @JvmField
    protected val dots = ArrayList<ImageView>()

    var dotsClickable: Boolean = true
    var dotsColor: Int = DEFAULT_POINT_COLOR
        set(value) {
            field = value
            refreshDotsColors()
        }

    protected var dotsSize = dpToPxF(type.defaultSize)
    protected var dotsCornerRadius = dotsSize / 2f
    protected var dotsSpacing = dpToPxF(type.defaultSpacing)

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, type.styleableId)

            dotsColor = a.getColor(type.dotsColorId, DEFAULT_POINT_COLOR)
            dotsSize = a.getDimension(type.dotsSizeId, dotsSize)
            dotsCornerRadius = a.getDimension(type.dotsCornerRadiusId, dotsCornerRadius)
            dotsSpacing = a.getDimension(type.dotsSpacingId, dotsSpacing)

            a.recycle()
        }
    }

    var pager: Pager? = null

    var fragmentNumber: Int = 0

    var currentFragment: Int = 0

    interface Pager {
        val isNotEmpty: Boolean
        val currentItem: Int
        val isEmpty: Boolean
        val count: Int

        fun setCurrentItem(item: Int, smoothScroll: Boolean)
        // fun removeOnPageChangeListener()
        // fun addOnPageChangeListener(onPageChangeListenerHelper: OnPageChangeListenerHelper)
    }

    fun addOnPageChangeListener(onPageChangeListenerHelper: OnPageChangeListenerHelper) {

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        refreshDots()
    }

    private fun refreshDotsCount() {
        /* if (dots.size < pager!!.count) {
           addDots(pager!!.count - dots.size)
         } else if (dots.size > pager!!.count) {
           removeDots(dots.size - pager!!.count)
         }*/
        if (dots.size < fragmentNumber) {
            addDots(fragmentNumber - dots.size)
        } else if (dots.size > fragmentNumber) {
            removeDots(dots.size - fragmentNumber)
        }
    }

    protected fun refreshDotsColors() {
        for (i in dots.indices) {
            refreshDotColor(i)
        }
    }

    protected fun dpToPx(dp: Int): Int {
        return (context.resources.displayMetrics.density * dp).toInt()
    }

    protected fun dpToPxF(dp: Float): Float {
        return context.resources.displayMetrics.density * dp
    }

    protected fun addDots(count: Int) {
        for (i in 0 until count) {
            addDot(i)
        }
    }

    private fun removeDots(count: Int) {
        for (i in 0 until count) {
            removeDot(i)
        }
    }

    protected fun refreshDots() {
        /*if (pager == null) {
          return
        }*/
        post {
            // Check if we need to refresh the dots count
            refreshDotsCount()
            refreshDotsColors()
            refreshDotsSize()
            refreshOnPageChangedListener()
        }
    }

    private fun refreshOnPageChangedListener() {
        val onPageChangeListenerHelper = buildOnPageChangedListener()
        //onPageChangeListenerHelper
        // pager!!.addOnPageChangeListener(onPageChangeListenerHelper)
        onPageChangeListenerHelper.onPageScrolled(currentFragment, 0f)
    }

    private fun refreshDotsSize() {

        for (i in 0 until this.currentFragment) {
            dots[i].setWidth(dotsSize.toInt())

        }


    }

    // ABSTRACT METHODS AND FIELDS

    abstract fun refreshDotColor(index: Int)
    abstract fun addDot(index: Int)
    abstract fun removeDot(index: Int)
    abstract fun buildOnPageChangedListener(): OnPageChangeListenerHelper
    abstract val type: Type

    // PUBLIC METHODS

    @Deprecated("Use setDotsColors() instead")
    fun setPointsColor(color: Int) {
        dotsColor = color
        refreshDotsColors()
    }

    fun setSelectedDot(index: Int) {

    }

    fun initializeDots(nrScreens: Int, value: Int) {
        fragmentNumber = nrScreens
        currentFragment = value
        object : Pager {


            override val isNotEmpty: Boolean
                get() = fragmentNumber.toString().isNotEmpty()
            override val currentItem: Int
                get() = currentFragment as Int
            override val isEmpty: Boolean
                get() = fragmentNumber.toString().isEmpty()
            override val count: Int
                get() = nrScreens

            override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
                currentFragment
            }


            /*  override fun removeOnPageChangeListener() {

              }*/

            /* override fun removeOnPageChangeListener() {
               onPageChangeListener?.let { viewPager.removeOnPageChangeListener(it) }
             }*/

            /*override fun addOnPageChangeListener(onPageChangeListenerHelper:
                                                 OnPageChangeListenerHelper) {
              onPageChangeListener = object : OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float,
                                            positionOffsetPixels: Int) {
                  onPageChangeListenerHelper.onPageScrolled(position, positionOffset)
                }

                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageSelected(position: Int) {
                }
              }
              viewPager.addOnPageChangeListener(onPageChangeListener!!)
            }*/
        }

        refreshDots()
    }


    // EXTENSIONS

    fun View.setWidth(width: Int) {
        layoutParams.apply {
            this.width = width
            requestLayout()
        }
    }

    fun <T> ArrayList<T>.isInBounds(index: Int) = index in 0 until size

    fun Context.getThemePrimaryColor(): Int {
        val value = TypedValue()
        this.theme.resolveAttribute(R.attr.colorPrimary, value, true)
        return value.data
    }

    // protected val ViewPager.isNotEmpty: Boolean get() = adapter!!.count > 0
    protected val ViewPager2.isNotEmpty: Boolean get() = adapter!!.itemCount > 0

    protected val ViewPager?.isEmpty: Boolean
        get() = this != null && this.adapter != null &&
                adapter!!.count == 0

    protected val ViewPager2?.isEmpty: Boolean
        get() = this != null && this.adapter != null &&
                adapter!!.itemCount == 0

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1 && layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            layoutDirection = View.LAYOUT_DIRECTION_LTR
            rotation = 180f
            requestLayout()
        }
    }
}