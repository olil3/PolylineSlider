package olil3.polylineSlider

import android.graphics.Color

data class PolylineSliderProperties(
    val mNumberOfDataPoints: Int,
    val isSliderVisible: Boolean,
    val mThumbColor: Any?,
    val mSliderColor: Any?,
    val mGradientColor: Int?,
    val mXAxisUnit: String?,
    val mXAxisValues: Array<String>?,
    val mYAxisUnit: String?,
    val mYAxisMinValue: Int,
    val mYAxisMaxValue: Int,
    val mYAxisInitialValue: Int
) {

    internal val DEFAULT_THUMB_COLOR = Color.MAGENTA
    internal val DEFAULT_SLIDER_COLOR = Color.MAGENTA
    internal val DEFAULT_GRADIENT_COLOR = Color.rgb(238, 130, 238)

    internal var usePositionOffsetAsXAxis: Boolean = false
    internal var useDefaultThumbColor: Boolean = false
    internal var useDefaultSliderColor: Boolean = false
    internal var useDefaultGradientColor: Boolean = false

    init {
        if (mThumbColor != null) {
            if (!(mThumbColor is Int || mThumbColor is IntArray)) {
                throw(IllegalArgumentException("Thumb Color must either be an Int or an array of Ints!"))
            }
        } else {
            useDefaultThumbColor = true
        }

        if (mSliderColor != null) {
            if (!(mSliderColor is Int || mSliderColor is IntArray)) {
                throw(IllegalArgumentException("Slider Color must either be an Int or an array of Ints!"))
            }
        } else {
            useDefaultSliderColor = true
        }

        if (mGradientColor == null) {
            useDefaultGradientColor = true
        }

        if (mXAxisValues == null) {
            usePositionOffsetAsXAxis = true
        } else if (mXAxisValues.size != mNumberOfDataPoints) {
            throw(IllegalArgumentException("X Axis Values Array must have the same size as that of the number of data points!"))
        }

        if (mYAxisInitialValue > mYAxisMaxValue || mYAxisInitialValue < mYAxisMinValue) {
            throw(IllegalArgumentException("Y Axis Initial Value must lie within the inclusive bounds of the min and max values!"))
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
        result = 31 * result + mYAxisMinValue
        result = 31 * result + mYAxisMaxValue
        result = 31 * result + mYAxisInitialValue
        return result
    }
}
