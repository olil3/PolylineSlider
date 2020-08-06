package olil3.polylineSlider

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter

data class PolylineSliderProperties(
    val mNumberOfDataPoints: Int,
    val isSliderVisible: Boolean,
    val mThumbColor: Any?,
    private val mSliderColor: Any?,
    val mGradientColor: Int?,
    val mXAxisUnit: String?,
    private val mXAxisValues: Array<String>?,
    val mYAxisUnit: String?,
    val mYAxisMinValue: Float,
    val mYAxisMaxValue: Float,
    val mYAxisInitialValue: Float
) {

    internal val DEFAULT_GRADIENT_COLOR = Color.rgb(238, 130, 238)
    internal var useDefaultGradientColor: Boolean = false

    internal val mXAxisUnitArray: List<String>
    internal val mSliderColorArray: List<PorterDuffColorFilter>
    internal val mThumbColorArray: List<PorterDuffColorFilter>

    init {

        mThumbColorArray = if (mThumbColor != null) {
            if (!(mThumbColor is Int || mThumbColor is IntArray)) {
                throw(IllegalArgumentException("Thumb Color must either be an Int or an array of Ints!"))
            } else if (mThumbColor is Int) {
                List(mNumberOfDataPoints) {
                    PorterDuffColorFilter(
                        mThumbColor,
                        PorterDuff.Mode.SRC_ATOP
                    )
                }
            } else {
                if ((mThumbColor as IntArray).size == mNumberOfDataPoints) {
                    List(mNumberOfDataPoints) { position ->
                        PorterDuffColorFilter(
                            mThumbColor[position],
                            PorterDuff.Mode.SRC_ATOP
                        )
                    }
                } else {
                    throw(IllegalArgumentException("Thumb Color Array must have the same size as that of the number of data points! Expected:$mNumberOfDataPoints Found:${mThumbColor.size}"))
                }
            }
        } else {
            List(mNumberOfDataPoints) {
                PorterDuffColorFilter(
                    Color.MAGENTA,
                    PorterDuff.Mode.SRC_ATOP
                )
            }
        }

        mSliderColorArray = if (mSliderColor != null) {
            if (!(mSliderColor is Int || mSliderColor is IntArray)) {
                throw(IllegalArgumentException("Slider Color must either be an Int or an array of Ints!"))
            } else if (mSliderColor is Int) {
                List(mNumberOfDataPoints) {
                    PorterDuffColorFilter(
                        mSliderColor,
                        PorterDuff.Mode.SRC_ATOP
                    )
                }
            } else {
                if ((mSliderColor as IntArray).size == mNumberOfDataPoints) {
                    List(mNumberOfDataPoints) { position ->
                        PorterDuffColorFilter(
                            mSliderColor[position],
                            PorterDuff.Mode.SRC_ATOP
                        )
                    }
                } else {
                    throw(IllegalArgumentException("Slider Color Array must have the same size as that of the number of data points! Expected:$mNumberOfDataPoints Found:${mSliderColor.size}"))
                }
            }
        } else {
            List(mNumberOfDataPoints) {
                PorterDuffColorFilter(
                    Color.MAGENTA,
                    PorterDuff.Mode.SRC_ATOP
                )
            }
        }

        if (mGradientColor == null) {
            useDefaultGradientColor = true
        }

        mXAxisUnitArray = when {
            mXAxisValues == null -> {
                List(mNumberOfDataPoints) { position -> (position + 1).toString() }
            }
            mXAxisValues.size == mNumberOfDataPoints -> {
                mXAxisValues.asList()
            }
            else -> {
                throw(IllegalArgumentException("X Axis Values Array must have the same size as that of the number of data points! Expected:$mNumberOfDataPoints Found:${mXAxisValues.size}"))
            }
        }

        if (mYAxisInitialValue > mYAxisMaxValue || mYAxisInitialValue < mYAxisMinValue) {
            throw(IllegalArgumentException("Y Axis Initial Value must lie within the inclusive bounds of the min and max values! Min:$mYAxisMinValue Max:$mYAxisMinValue Initial:$mYAxisInitialValue"))
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PolylineSliderProperties

        if (mNumberOfDataPoints != other.mNumberOfDataPoints) return false
        if (isSliderVisible != other.isSliderVisible) return false
        if (mThumbColor != other.mThumbColor) return false
        if (mGradientColor != other.mGradientColor) return false
        if (mXAxisUnit != other.mXAxisUnit) return false
        if (mXAxisValues != null) {
            if (other.mXAxisValues == null) return false
            if (!mXAxisValues.contentEquals(other.mXAxisValues)) return false
        } else if (other.mXAxisValues != null) return false
        if (mYAxisUnit != other.mYAxisUnit) return false
        if (mYAxisMinValue != other.mYAxisMinValue) return false
        if (mYAxisMaxValue != other.mYAxisMaxValue) return false
        if (mYAxisInitialValue != other.mYAxisInitialValue) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mNumberOfDataPoints
        result = 31 * result + isSliderVisible.hashCode()
        result = 31 * result + (mThumbColor?.hashCode() ?: 0)
        result = 31 * result + (mGradientColor?.hashCode() ?: 0)
        result = 31 * result + (mXAxisUnit?.hashCode() ?: 0)
        result = 31 * result + (mXAxisValues?.contentHashCode() ?: 0)
        result = 31 * result + (mYAxisUnit?.hashCode() ?: 0)
        result = 31 * result + mYAxisMinValue.hashCode()
        result = 31 * result + mYAxisMaxValue.hashCode()
        result = 31 * result + mYAxisInitialValue.hashCode()
        return result
    }
}
