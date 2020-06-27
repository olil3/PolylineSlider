package olil3.polylineSlider.uiComponents

import android.content.Context
import android.graphics.PorterDuffColorFilter
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView

internal class PolylineSliderGraphAdapter(
    private val mParentRecyclerView: PolylineSliderGraph,
    private val mNumberOfDataPoints: Int,
    private val mSliderSpacing: Int,
    private val mSliderAlphaVal: Int,
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
                    mParentRecyclerView.updateText(position, 1)
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    mParentRecyclerView.updateText(position, 0)
                }
            })
        mVerticalSliderSeekBar.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                Log.e("Slider Down", "id: $position")
                mParentRecyclerView.changeSliderAlpha(mVerticalSliderSeekBar.progress, 1)
            } else if (event.action == MotionEvent.ACTION_UP) {
                mParentRecyclerView.changeSliderAlpha(mVerticalSliderSeekBar.progress, 0)
                Log.e("Slider up", "id: $position")
            }
            v.performClick()
            false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerticalSeekBarObject {
        val mVerticalSlider = VerticalSlider(mContext)
        mVerticalSlider.layoutParams =
            RecyclerView.LayoutParams(mSliderSpacing, RecyclerView.LayoutParams.MATCH_PARENT)
        mVerticalSlider.id = View.generateViewId()
        mVerticalSlider.sliderAlpha = mSliderAlphaVal
        mVerticalSlider.thumbColor = mThumbColorFilter
        mVerticalSlider.sliderColor = mSliderColorFilter
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