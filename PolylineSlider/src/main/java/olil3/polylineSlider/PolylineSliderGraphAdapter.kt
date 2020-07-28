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

internal class PolylineSliderGraphAdapter(
    private val mParentRecyclerView: PolylineSliderGraph,
    private val mDataClass: PolylineSliderProperties,
    private val mSliderSpacing: Int,
    private val mSliderWrapperIDArray: IntArray,
    private val mContext: Context
) :
    RecyclerView.Adapter<PolylineSliderGraphAdapter.VerticalSeekBarObject>() {
    override fun getItemCount(): Int {
        return mDataClass.mNumberOfDataPoints
    }

    override fun onBindViewHolder(holder: VerticalSeekBarObject, position: Int) {
        mSliderWrapperIDArray[position] = holder.mVerticalSliderComponent.id
        val mVerticalSliderSeekBar = holder.mVerticalSlider
        mVerticalSliderSeekBar.childSeekBar.thumb.colorFilter =
            mDataClass.mThumbColorArray[position]
        mVerticalSliderSeekBar.childSeekBar.progressDrawable.colorFilter =
            mDataClass.mSliderColorArray[position]

        holder.mXTextView.text = ("${mDataClass.mXAxisUnitArray[position]}${mDataClass.mXAxisUnit}")
        holder.mYTextView.text =
            ("${mVerticalSliderSeekBar.childSeekBar.progress}${mDataClass.mYAxisUnit}")

        mVerticalSliderSeekBar.childSeekBar
            .setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    mParentRecyclerView.updateSliderParams(mVerticalSliderSeekBar, position)
                    holder.mYTextView.text =
                        ("${mVerticalSliderSeekBar.childSeekBar.progress}${mDataClass.mYAxisUnit}")
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })
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
        mVerticalSlider.sliderProgress = mDataClass.mYAxisInitialValue

        mXAxis.gravity = Gravity.CENTER
        mYAxis.gravity = Gravity.CENTER
        return (VerticalSeekBarObject(
            mVerticalSliderItem
        ))
    }

    override fun getItemId(position: Int): Long {
        return mSliderWrapperIDArray[position].toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class VerticalSeekBarObject(val mVerticalSliderComponent: RelativeLayout) :
        RecyclerView.ViewHolder(mVerticalSliderComponent) {
        val mVerticalSlider = mVerticalSliderComponent.getChildAt(0) as VerticalSeekBarWrapper
        val mXTextView = mVerticalSliderComponent.getChildAt(1) as TextView
        val mYTextView = mVerticalSliderComponent.getChildAt(2) as TextView
    }
}
