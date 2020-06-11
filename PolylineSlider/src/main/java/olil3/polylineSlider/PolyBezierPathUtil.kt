package olil3.polylineSlider

import android.graphics.Path
import java.util.*

internal class PolyBezierPathUtil {
    /**
     * This segment of this library is authored by:
     * Stuart Kent
     *
     *
     * Website: https://www.stkent.com/2015/07/03/building-smooth-paths-using-bezier-curves.html
     *
     *
     *
     *
     * This code has been slightly modified and adapted for the use of this project.
     *
     *
     * Computes a Poly-Bezier curve passing through a given list of knots.
     * The curve will be twice-differentiable everywhere and satisfy natural
     * boundary conditions at both ends.
     *
     * @param knots a list of knots
     * @return a Path representing the twice-differentiable curve
     * passing through all the given knots
     */
    fun computePathThroughKnots(knots: List<EPointF>): Path {
        throwExceptionIfInputIsInvalid(knots)
        val polyBezierPath = Path()
        val firstKnot = knots[0]
        polyBezierPath.moveTo(firstKnot.x, firstKnot.y)

        /*
         * variable representing the number of Bezier curves we will join
         * together
         */
        val n = knots.size - 1
        if (n == 1) {
            val lastKnot = knots[1]
            polyBezierPath.lineTo(lastKnot.x, lastKnot.y)
        } else {
            val controlPoints = computeControlPoints(n, knots)
            for (i in 0 until n) {
                val targetKnot = knots[i + 1]
                appendCurveToPath(
                    polyBezierPath,
                    controlPoints[i],
                    controlPoints[n + i],
                    targetKnot
                )
            }
        }
        return polyBezierPath
    }

    private fun computeControlPoints(
        n: Int,
        knots: List<EPointF>
    ): Array<EPointF?> {
        val result = arrayOfNulls<EPointF>(2 * n)
        val target = constructTargetVector(n, knots)
        val lowerDiag = constructLowerDiagonalVector(n - 1)
        val mainDiag = constructMainDiagonalVector(n)
        val upperDiag = constructUpperDiagonalVector(n - 1)
        val newTarget = arrayOfNulls<EPointF>(n)
        val newUpperDiag = arrayOfNulls<Float>(n - 1)

        // forward sweep for control points c_i,0:
        newUpperDiag[0] = upperDiag[0]!! / mainDiag[0]!!
        newTarget[0] = target[0]!!.scaleBy(1 / mainDiag[0]!!)
        for (i in 1 until n - 1) {
            newUpperDiag[i] = upperDiag[i]!! /
                    (mainDiag[i]!! - lowerDiag[i - 1]!! * newUpperDiag[i - 1]!!)
        }
        for (i in 1 until n) {
            val targetScale = 1 /
                    (mainDiag[i]!! - lowerDiag[i - 1]!! * newUpperDiag[i - 1]!!)
            newTarget[i] =
                target[i]!!.minus(newTarget[i - 1]!!.scaleBy(lowerDiag[i - 1]!!)).scaleBy(
                    targetScale
                )
        }

        // backward sweep for control points c_i,0:
        result[n - 1] = newTarget[n - 1]
        for (i in n - 2 downTo 0) {
            result[i] = newTarget[i]!!.minus(newUpperDiag[i]!!, result[i + 1]!!)
        }

        // calculate remaining control points c_i,1 directly:
        for (i in 0 until n - 1) {
            result[n + i] = knots[i + 1].scaleBy(2f).minus(result[i + 1]!!)
        }
        result[2 * n - 1] = knots[n].plus(result[n - 1]!!).scaleBy(0.5f)
        return result
    }

    private fun constructTargetVector(
        n: Int,
        knots: List<EPointF>
    ): Array<EPointF?> {
        val result = arrayOfNulls<EPointF>(n)
        result[0] = knots[0].plus(2f, knots[1])
        for (i in 1 until n - 1) {
            result[i] = knots[i].scaleBy(2f).plus(knots[i + 1]).scaleBy(2f)
        }
        result[result.size - 1] = knots[n - 1].scaleBy(8f).plus(knots[n])
        return result
    }

    private fun constructLowerDiagonalVector(length: Int): Array<Float?> {
        val result = arrayOfNulls<Float>(length)
        for (i in 0 until result.size - 1) {
            result[i] = 1f
        }
        result[result.size - 1] = 2f
        return result
    }

    private fun constructMainDiagonalVector(n: Int): Array<Float?> {
        val result = arrayOfNulls<Float>(n)
        result[0] = 2f
        for (i in 1 until result.size - 1) {
            result[i] = 4f
        }
        result[result.size - 1] = 7f
        return result
    }

    private fun constructUpperDiagonalVector(length: Int): Array<Float?> {
        val result = arrayOfNulls<Float>(length)
        Arrays.fill(result, 1f)
        return result
    }

    private fun appendCurveToPath(
        path: Path,
        control1: EPointF?,
        control2: EPointF?,
        targetKnot: EPointF
    ) {
        path.cubicTo(
            control1!!.x,
            control1.y,
            control2!!.x,
            control2.y,
            targetKnot.x,
            targetKnot.y
        )
    }

    private fun throwExceptionIfInputIsInvalid(knots: Collection<EPointF>) {
        require(knots.size >= 2) { "Collection must contain at least two knots" }
    }
}