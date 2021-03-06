package olil3.polylineSlider

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import olil3.polylineSlider.utils.VerticalSeekBarWrapper
import java.util.concurrent.ConcurrentHashMap

internal class PolylineSliderGraphAdapter(
    private val mParentRecyclerView: PolylineSliderGraph,
    private val mDataClass: PolylineSliderProperties,
    private val mSliderSpacing: Int,
    private val mSliderWrapperIDArray: IntArray,
    private val mContext: Context
) :
    RecyclerView.Adapter<PolylineSliderGraphAdapter.VerticalSeekBarObject>() {

    private val mProgressMap: ConcurrentHashMap<String, Int> = ConcurrentHashMap()

    class VerticalSeekBarObject(val mVerticalSliderComponent: RelativeLayout) :
        RecyclerView.ViewHolder(mVerticalSliderComponent) {
        val mVerticalSlider = mVerticalSliderComponent.getChildAt(0) as VerticalSeekBarWrapper
        val mXTextView = mVerticalSliderComponent.getChildAt(1) as TextView
        val mYTextView = mVerticalSliderComponent.getChildAt(2) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerticalSeekBarObject {
        val mVerticalSliderItem = LayoutInflater.from(mContext)
            .inflate(R.layout.vertical_slider_item, parent, false) as RelativeLayout
        val mVerticalSlider = mVerticalSliderItem.findViewById<VerticalSeekBarWrapper>(R.id.slider)
        val mXAxis = mVerticalSliderItem.findViewById<TextView>(R.id.x_axis_text)
        val mYAxis = mVerticalSliderItem.findViewById<TextView>(R.id.y_axis_text)

        mVerticalSliderItem.id = View.generateViewId()
        mVerticalSliderItem.layoutParams =
            RecyclerView.LayoutParams(mSliderSpacing, RecyclerView.LayoutParams.MATCH_PARENT)

        mVerticalSlider.sliderAlpha = if (mDataClass.isSliderVisible) 255 else 0
        mVerticalSlider.sliderMax = 100
        mVerticalSlider.sliderProgress = getSliderProgressFromValue(mDataClass.mYAxisInitialValue)

        mXAxis.gravity = Gravity.CENTER
        mYAxis.gravity = Gravity.CENTER
        return (VerticalSeekBarObject(
            mVerticalSliderItem
        ))
    }

    override fun onBindViewHolder(holder: VerticalSeekBarObject, position: Int) {
        mSliderWrapperIDArray[position] = holder.mVerticalSliderComponent.id
        val mVerticalSliderSeekBar = holder.mVerticalSlider
        mVerticalSliderSeekBar.thumbColor =
            mDataClass.mThumbColorArray[position]
        mVerticalSliderSeekBar.sliderColor =
            mDataClass.mSliderColorArray[position]

        mProgressMap[mDataClass.mXAxisUnitArray[position]] = mVerticalSliderSeekBar.sliderProgress

        holder.mXTextView.text = ("${mDataClass.mXAxisUnitArray[position]}${mDataClass.mXAxisUnit}")
        holder.mYTextView.text =
            ("${getYAxisTextValue(mVerticalSliderSeekBar.childSeekBar.progress)}${mDataClass.mYAxisUnit}")

        mVerticalSliderSeekBar.childSeekBar
            .setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    mParentRecyclerView.updateSliderParams(mVerticalSliderSeekBar, position)
                    holder.mYTextView.text =
                        ("${getYAxisTextValue(mVerticalSliderSeekBar.childSeekBar.progress)}${mDataClass.mYAxisUnit}")
                    mProgressMap[mDataClass.mXAxisUnitArray[position]] =
                        mVerticalSliderSeekBar.sliderProgress
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })
    }

    override fun getItemCount(): Int {
        return mDataClass.mNumberOfDataPoints
    }

    override fun getItemId(position: Int): Long {
        return mSliderWrapperIDArray[position].toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    private fun getYAxisTextValue(sliderProgress: Int): String {
        return "%.2f".format((mDataClass.mYAxisMinValue + ((mDataClass.mYAxisMaxValue - mDataClass.mYAxisMinValue) * sliderProgress / 100)))
    }

    private fun getSliderProgressFromValue(mValue: Float): Int {
        return (((mValue - mDataClass.mYAxisMinValue) / (mDataClass.mYAxisMaxValue - mDataClass.mYAxisMinValue)) * 100f).toInt()
    }

    fun getSliderProgressAsPercentage(): MutableMap<String, Int> {
        return mProgressMap.toMutableMap()
    }

    fun getSliderProgressAsValue(): MutableMap<String, Float> {
        val toReturn = mutableMapOf<String, Float>()
        for (iterator in mProgressMap.entries) {
            toReturn[iterator.key] =
                (mDataClass.mYAxisMinValue + ((mDataClass.mYAxisMaxValue - mDataClass.mYAxisMinValue) * iterator.value / 100))
        }
        return toReturn
    }
}
