package olil3.polyLineSlider

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import olil3.polylineSlider.PolylineSlider

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mPolylineSlider = findViewById<PolylineSlider>(R.id.mPolylineSlider)

        findViewById<Button>(R.id.getPercentageMap).setOnClickListener {
            Toast.makeText(
                this,
                "${mPolylineSlider.getSliderProgressAsPercentage()}",
                Toast.LENGTH_LONG
            ).show()
        }

        findViewById<Button>(R.id.getValueMap).setOnClickListener {
            Toast.makeText(this, "${mPolylineSlider.getSliderProgressAsValue()}", Toast.LENGTH_LONG)
                .show()
        }
    }
}
