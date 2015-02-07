package thahn.java.agui.graphics;


/**
 * Math routines similar to those found in {@link java.lang.Math}. Performs
 * computations on {@code float} values directly without incurring the overhead
 * of conversions to and from {@code double}.
 *
 * <p>On one platform, {@code FloatMath.sqrt(100)} executes in one third of the
 * time required by {@code java.lang.Math.sqrt(100)}.</p>
 */
public class FloatMath {

    /** Prevents instantiation. */
    private FloatMath() {}

    /**
     * Returns the float conversion of the most positive (i.e. closest to
     * positive infinity) integer value which is less than the argument.
     *
     * @param value to be converted
     * @return the floor of value
     */
    public static native float floor(float value);

    /**
     * Returns the float conversion of the most negative (i.e. closest to
     * negative infinity) integer value which is greater than the argument.
     *
     * @param value to be converted
     * @return the ceiling of value
     */
    public static native float ceil(float value);

    /**
     * Returns the closest float approximation of the sine of the argument.
     *
     * @param angle to compute the cosine of, in radians
     * @return the sine of angle
     */
    public static native float sin(float angle);

    /**
     * Returns the closest float approximation of the cosine of the argument.
     *
     * @param angle to compute the cosine of, in radians
     * @return the cosine of angle
     */
    public static native float cos(float angle);

    /**
     * Returns the closest float approximation of the square root of the
     * argument.
     *
     * @param value to compute sqrt of
     * @return the square root of value
     */
    public static native float sqrt(float value);
}
