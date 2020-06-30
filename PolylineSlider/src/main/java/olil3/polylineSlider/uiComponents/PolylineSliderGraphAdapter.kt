package olil3.polylineSlider.uiComponents

import android.content.Context
import android.graphics.PorterDuffColorFilter
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import olil3.polylineSlider.ON_TOUCH_DOWN
import olil3.polylineSlider.ON_TOUCH_UP
import olil3.polylineSlider.utils.VerticalSlider

internal class PolylineSliderGraphAdapter(
    private val mParentRecyclerView: PolylineSliderGraph,
    private val mNumberOfDataPoints: Int,
    private val mSliderSpacing: Int,
    private val mSliderAlphaVal: Int,
    private val mSliderInitialVal: Int,
    private val mThumbColorFilter: PorterDuffColorFilter,
    private val mSliderColorFilter: PorterDuffColorFilter,
    private val mSliderWrapperIDArray: IntArray,
    private val mContext: Context
) :
    RecyclerView.Adapter<PolylineSliderGraphAdapter.VerticalSeekBarObject>() {
    override fun getItemCount(): Int {
        return mNumberOfDataPoints
    }

    override fun onBindViewHolder(holder: VerticalSeekBarObject, position: Int) {
        mSliderWrapperIDArray[position] = holder.mVerticalSlider.id
        val mVerticalSliderSeekBar = holder.mVerticalSlider.getSeekBarObject()
        mVerticalSliderSeekBar
            .setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    mParentRecyclerView.updateSliderParams(holder.mVerticalSlider, position)
                    mParentRecyclerView.displayYAxisProgress(position, mVerticalSliderSeekBar.progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    mParentRecyclerView.updateText(position, ON_TOUCH_DOWN)
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    mParentRecyclerView.updateText(position, ON_TOUCH_UP)
                }
            })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerticalSeekBarObject {
        val mVerticalSlider = VerticalSlider(mContext)
        mVerticalSlider.layoutParams =
            RecyclerView.LayoutParams(mSliderSpacing, RecyclerView.LayoutParams.MATCH_PARENT)
        mVerticalSlider.id = View.generateViewId()
        mVerticalSlider.sliderAlpha = mSliderAlphaVal
        mVerticalSlider.thumbColor = mThumbColorFilter
        mVerticalSlider.sliderColor = mSliderColorFilter
        mVerticalSlider.sliderMax = 100
        mVerticalSlider.sliderProgress = mSliderInitialVal
        return (VerticalSeekBarObject(
            mVerticalSlider
        ))
    }

    override fun getItemId(position: Int): Long {
        return mSliderWrapperIDArray[position].toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class VerticalSeekBarObject(val mVerticalSlider: VerticalSlider) :
        RecyclerView.ViewHolder(mVerticalSlider)
}
