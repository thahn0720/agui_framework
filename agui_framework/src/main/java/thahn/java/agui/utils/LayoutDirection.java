package thahn.java.agui.utils;
/**
 * A class for defining layout directions. A layout direction can be left-to-right (LTR)
 * or right-to-left (RTL). It can also be inherited (from a parent) or deduced from the default
 * language script of a locale.
 */
public final class LayoutDirection {

    // No instantiation
    private LayoutDirection() {}

    /**
     * Horizontal layout direction is from Left to Right.
     */
    public static final int LTR = 0;

    /**
     * Horizontal layout direction is from Right to Left.
     */
    public static final int RTL = 1;

    /**
     * Horizontal layout direction is inherited.
     */
    public static final int INHERIT = 2;

    /**
     * Horizontal layout direction is deduced from the default language script for the locale.
     */
    public static final int LOCALE = 3;
}