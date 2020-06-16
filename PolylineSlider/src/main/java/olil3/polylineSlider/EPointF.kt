package olil3.polylineSlider

/**
 * This segment of the code is authored by:
 * Stuart Kent
 *
 *
 * Website: https://www.stkent.com/2015/07/03/building-smooth-paths-using-bezier-curves.html
 *
 *
 * This code has been slightly modified and adapted for the use of this project.
 * API inspired by the Apache Commons Math Vector2D class.
 */
internal class EPointF(val x: Float, val y: Float) {

    fun plus(factor: Float, ePointF: EPointF): EPointF {
        return EPointF(x + factor * ePointF.x, y + factor * ePointF.y)
    }

    operator fun plus(ePointF: EPointF): EPointF {
        return plus(1.0f, ePointF)
    }

    fun minus(factor: Float, ePointF: EPointF): EPointF {
        return EPointF(x - factor * ePointF.x, y - factor * ePointF.y)
    }

    operator fun minus(ePointF: EPointF): EPointF {
        return minus(1.0f, ePointF)
    }

    fun scaleBy(factor: Float): EPointF {
        return EPointF(factor * x, factor * y)
    }
}
