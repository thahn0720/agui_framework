package thahn.java.agui.utils;

/**
 * @hide
 */
public interface Poolable<T> {
    void setNextPoolable(T element);
    T getNextPoolable();
//    boolean isPooled();
//    void setPooled(boolean isPooled);
}