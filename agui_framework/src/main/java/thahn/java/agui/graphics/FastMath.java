package thahn.java.agui.graphics;


/**
 * Fast and loose math routines.
 */
public class FastMath {

    /**
     * Fast round from float to int. This is faster than Math.round()
     * thought it may return slightly different results. It does not try to
     * handle (in any meaningful way) NaN or infinities.
     */
    public static int round(float x) {
        long lx = (long)(x * (65536 * 256f));
        return (int)((lx + 0x800000) >> 24);
    }
}

