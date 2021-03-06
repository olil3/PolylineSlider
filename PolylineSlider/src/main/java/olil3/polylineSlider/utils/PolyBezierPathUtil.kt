package olil3.polylineSlider.utils

import android.graphics.Path

class PolyBezierPathUtil {
    /**
     * This segment of this library is authored by:
     * Stuart Kent
     *
     *
     * Website: https://www.stkent.com/2015/07/03/building-smooth-paths-using-bezier-curves.html
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

    private fun computePathThroughKnots(knots: List<EPointF>): Path {
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

    fun computePathThroughKnots(
        xArray: FloatArray,
        yArray: FloatArray,
        mYInitialVal: Float,
        recyclerViewScrollRange: Int,
        recyclerViewScrollOffset: Int,
        mGradientPath: Path
    ): Path {
        val listToReturn = mutableListOf<EPointF>()
        for (i in xArray.indices) {
            listToReturn.add(
                EPointF(
                    xArray[i],
                    yArray[i]
                )
            )
        }
        listToReturn.add(
            0,
            EPointF(-recyclerViewScrollOffset.toFloat(), mYInitialVal)
        )
        listToReturn.add(
            EPointF(
                (recyclerViewScrollRange - recyclerViewScrollOffset).toFloat(),
                mYInitialVal
            )
        )
        val toReturn = computePathThroughKnots(listToReturn)
        mGradientPath.set(toReturn)
        return toReturn
    }

    private fun computeControlPoints(
        n: Int,
        knots: List<EPointF>
    ): Array<EPointF?> {
        val result = arrayOfNulls<EPointF>(2 * n)
        val newTarget = arrayOfNulls<EPointF>(n)
        val newUpperDiagonal = arrayOfNulls<Float>(n - 1)

        // forward sweep for control points c_i,0:
        newUpperDiagonal[0] = 0.5f
        newTarget[0] = knots[0].plus(2f, knots[1]).scaleBy(0.5f)

        for (i in 1 until n - 1) {
            newUpperDiagonal[i] = 1f /
                    (4f - 1f * newUpperDiagonal[i - 1]!!)
            newTarget[i] =
                knots[i].scaleBy(2f).plus(knots[i + 1]).scaleBy(2f)
                    .minus(newTarget[i - 1]!!.scaleBy(1f)).scaleBy(
                        1 /
                                (4f - 1f * newUpperDiagonal[i - 1]!!)
                    )
        }

        newTarget[n - 1] =
            knots[n - 1].scaleBy(8f).plus(knots[n]).minus(newTarget[n - 2]!!.scaleBy(2f)).scaleBy(
                1 /
                        (7f - 2f * newUpperDiagonal[n - 2]!!)
            )

        // backward sweep for control points c_i,0:
        result[n - 1] = newTarget[n - 1]
        for (i in n - 2 downTo 0) {
            result[i] = newTarget[i]!!.minus(newUpperDiagonal[i]!!, result[i + 1]!!)
        }

        // calculate remaining control points c_i,1 directly:
        for (i in 0 until n - 1) {
            result[n + i] = knots[i + 1].scaleBy(2f).minus(result[i + 1]!!)
        }
        result[2 * n - 1] = knots[n].plus(result[n - 1]!!).scaleBy(0.5f)
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
