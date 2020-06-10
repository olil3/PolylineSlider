package olil3.polylineSlider.utils;

/**
 * This segment of the code is authored by:
 * Stuart Kent
 * <p>
 * Website: https://www.stkent.com/2015/07/03/building-smooth-paths-using-bezier-curves.html
 * <p>
 * This code has been slightly modified and adapted for the use of this project.
 * API inspired by the Apache Commons Math Vector2D class.
 */
public class EPointF {

    private final float x;
    private final float y;

    public EPointF(final float x, final float y) {
        this.x = x;
        this.y = y;
    }

    public EPointF(final int[] intArray) {
        this.x = intArray[0];
        this.y = intArray[1];
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public EPointF plus(float factor, EPointF ePointF) {
        return new EPointF(x + factor * ePointF.x, y + factor * ePointF.y);
    }

    public EPointF plus(EPointF ePointF) {
        return plus(1.0f, ePointF);
    }

    public EPointF minus(float factor, EPointF ePointF) {
        return new EPointF(x - factor * ePointF.x, y - factor * ePointF.y);
    }

    public EPointF minus(EPointF ePointF) {
        return minus(1.0f, ePointF);
    }

    public EPointF scaleBy(float factor) {
        return new EPointF(factor * x, factor * y);
    }

}
