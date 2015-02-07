package thahn.java.agui.utils;

/**
 * @hide
 */
public class Pools {
	
	/**
	 * @hide
	 */
	public interface Pool<T extends Poolable<T>> {
	    public abstract T acquire();
	    public abstract void release(T element);
	}
	
    private Pools() {
    }

    public static <T extends Poolable<T>> Pool<T> simplePool(PoolableManager<T> manager) {
        return new FinitePool<T>(manager);
    }
    
    public static <T extends Poolable<T>> Pool<T> finitePool(PoolableManager<T> manager, int limit) {
        return new FinitePool<T>(manager, limit);
    }

    public static <T extends Poolable<T>> Pool<T> synchronizedPool(Pool<T> pool) {
        return new SynchronizedPool<T>(pool);
    }

    public static <T extends Poolable<T>> Pool<T> synchronizedPool(Pool<T> pool, Object lock) {
        return new SynchronizedPool<T>(pool, lock);
    }
}
