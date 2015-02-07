package thahn.java.agui.utils;

/**
 * @hide
 */
public interface PoolableManager<T extends Poolable<T>> {
    T newInstance();

    void onAcquired(T element);
    void onReleased(T element);
}