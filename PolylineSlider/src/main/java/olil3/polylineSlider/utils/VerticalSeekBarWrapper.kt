/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package olil3.polylineSlider.utils

import android.content.Context
import android.graphics.ColorFilter
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.ViewCompat

class VerticalSeekBarWrapper(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var mVerticalSeekBar: VerticalSeekBar = VerticalSeekBar(context)
    var thumbAlpha: Int
        get() {
            return mVerticalSeekBar.thumb.alpha
        }
        set(value) {
            mVerticalSeekBar.thumb.alpha = value
        }

    var thumbColor: ColorFilter?
        get() {
            return mVerticalSeekBar.thumb.colorFilter
        }
        set(value) {
            mVerticalSeekBar.thumb.colorFilter = value
        }

    var sliderAlpha: Int
        get() {
            return mVerticalSeekBar.progressDrawable.alpha
        }
        set(value) {
            mVerticalSeekBar.progressDrawable.alpha = value
        }

    var sliderColor: ColorFilter?
        get() {
            return mVerticalSeekBar.progressDrawable.colorFilter
        }
        set(value) {
            mVerticalSeekBar.progressDrawable.colorFilter = value
        }

    var sliderProgress: Int
        get() {
            return mVerticalSeekBar.progress
        }
        set(value) {
            mVerticalSeekBar.progress = value
        }

    var sliderMax: Int
        get() {
            return mVerticalSeekBar.max
        }
        set(value) {
            mVerticalSeekBar.max = value
        }

    init {
        mVerticalSeekBar.rotationAngle = VerticalSeekBar.ROTATION_ANGLE_CW_270
        this.addView(
            mVerticalSeekBar,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (useViewRotation()) {
            onSizeChangedUseViewRotation(w, h, oldw, oldh)
        } else {
            onSizeChangedTraditionalRotation(w, h, oldw, oldh)
        }
    }

    private fun onSizeChangedTraditionalRotation(
        w: Int,
        h: Int,
        oldw: Int,
        oldh: Int
    ) {
        val seekBar = childSeekBar
        val hPadding = paddingLeft + paddingRight
        val vPadding = paddingTop + paddingBottom
        val lp = seekBar.layoutParams as LayoutParams
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT
        lp.height = maxOf(0, h - vPadding)
        seekBar.layoutParams = lp
        seekBar.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        val seekBarMeasuredWidth = seekBar.measuredWidth
        seekBar.measure(
            MeasureSpec.makeMeasureSpec(
                maxOf(0, w - hPadding),
                MeasureSpec.AT_MOST
            ),
            MeasureSpec.makeMeasureSpec(
                maxOf(0, h - vPadding),
                MeasureSpec.EXACTLY
            )
        )
        lp.gravity = Gravity.TOP or Gravity.LEFT
        lp.leftMargin = (maxOf(0, w - hPadding) - seekBarMeasuredWidth) / 2
        seekBar.layoutParams = lp
        super.onSizeChanged(w, h, oldw, oldh)
    }

    private fun onSizeChangedUseViewRotation(w: Int, h: Int, oldw: Int, oldh: Int) {
        val seekBar = childSeekBar
        val hPadding = paddingLeft + paddingRight
        val vPadding = paddingTop + paddingBottom
        seekBar.measure(
            MeasureSpec.makeMeasureSpec(
                maxOf(0, h - vPadding),
                MeasureSpec.EXACTLY
            ),
            MeasureSpec.makeMeasureSpec(
                maxOf(0, w - hPadding),
                MeasureSpec.AT_MOST
            )
        )
        applyViewRotation(w, h)
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val seekBar = childSeekBar
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        if (widthMode != MeasureSpec.EXACTLY) {
            val seekBarWidth: Int
            val seekBarHeight: Int
            val hPadding = paddingLeft + paddingRight
            val vPadding = paddingTop + paddingBottom
            val innerContentWidthMeasureSpec =
                MeasureSpec.makeMeasureSpec(maxOf(0, widthSize - hPadding), widthMode)
            val innerContentHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                maxOf(0, heightSize - vPadding),
                heightMode
            )
            if (useViewRotation()) {
                seekBar.measure(innerContentHeightMeasureSpec, innerContentWidthMeasureSpec)
                seekBarWidth = seekBar.measuredHeight
                seekBarHeight = seekBar.measuredWidth
            } else {
                seekBar.measure(innerContentWidthMeasureSpec, innerContentHeightMeasureSpec)
                seekBarWidth = seekBar.measuredWidth
                seekBarHeight = seekBar.measuredHeight
            }
            val measuredWidth =
                View.resolveSizeAndState(seekBarWidth + hPadding, widthMeasureSpec, 0)
            val measuredHeight = View.resolveSizeAndState(
                seekBarHeight + vPadding,
                heightMeasureSpec,
                0
            )
            setMeasuredDimension(measuredWidth, measuredHeight)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    /*package*/
    fun applyViewRotation() {
        applyViewRotation(width, height)
    }

    private fun applyViewRotation(w: Int, h: Int) {
        val seekBar = childSeekBar
        val isLTR =
            ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_LTR
        val rotationAngle = seekBar.rotationAngle
        val seekBarMeasuredWidth = seekBar.measuredWidth
        val seekBarMeasuredHeight = seekBar.measuredHeight
        val hPadding = paddingLeft + paddingRight
        val vPadding = paddingTop + paddingBottom
        val hOffset =
            (maxOf(0, w - hPadding) - seekBarMeasuredHeight) * 0.5f
        val lp = seekBar.layoutParams
        lp.width = maxOf(0, h - vPadding)
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
        seekBar.layoutParams = lp
        seekBar.pivotX = if (isLTR) 0F else maxOf(0, h - vPadding).toFloat()
        seekBar.pivotY = 0f
        when (rotationAngle) {
            VerticalSeekBar.ROTATION_ANGLE_CW_90 -> {
                seekBar.rotation = 90f
                if (isLTR) {
                    seekBar.translationX = seekBarMeasuredHeight + hOffset
                    seekBar.translationY = 0f
                } else {
                    seekBar.translationX = -hOffset
                    seekBar.translationY = seekBarMeasuredWidth.toFloat()
                }
            }
            VerticalSeekBar.ROTATION_ANGLE_CW_270 -> {
                seekBar.rotation = 270f
                if (isLTR) {
                    seekBar.translationX = hOffset
                    seekBar.translationY = seekBarMeasuredWidth.toFloat()
                } else {
                    seekBar.translationX = -(seekBarMeasuredHeight + hOffset)
                    seekBar.translationY = 0f
                }
            }
        }
    }

    val childSeekBar: VerticalSeekBar
        get() {
            return getChildAt(0) as VerticalSeekBar
        }

    private fun useViewRotation(): Boolean {
        val seekBar = childSeekBar
        return seekBar.useViewRotation()
    }

    fun getSliderCoordinates(): EPointF {
        val seekBarThumbBounds = mVerticalSeekBar.thumb.bounds
        val xPos: Float =
            this.left + seekBarThumbBounds.exactCenterY() + ((this.width - (mVerticalSeekBar.paddingLeft * 1.1f)) / 2)
        val yPos: Float =
            this.bottom - seekBarThumbBounds.exactCenterX() - (seekBarThumbBounds.height() * 0.4f)

        return EPointF(xPos, yPos)
    }
}