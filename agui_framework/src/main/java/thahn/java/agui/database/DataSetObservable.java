package thahn.java.agui.database;

import thahn.java.agui.widget.Observable;


public class DataSetObservable extends Observable<DataSetObserver> {
    /**
     * Invokes onChanged on each observer. Called when the data set being observed has
     * changed, and which when read contains the new state of the data.
     */
    public void notifyChanged() {
        synchronized(mObservers) {
            for (DataSetObserver observer : mObservers) {
                observer.onChanged();
            }
        }
    }

    /**
     * Invokes onInvalidated on each observer. Called when the data set being monitored
     * has changed such that it is no longer valid.
     */
    public void notifyInvalidated() {
        synchronized (mObservers) {
            for (DataSetObserver observer : mObservers) {
                observer.onInvalidated();
            }
        }
    }
}

