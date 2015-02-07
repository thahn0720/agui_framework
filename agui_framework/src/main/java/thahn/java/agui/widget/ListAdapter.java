package thahn.java.agui.widget;

/**
 * Extended {@link Adapter} that is the bridge between a {@link ListView}
 * and the data that backs the list. Frequently that data comes from a Cursor,
 * but that is not
 * required. The ListView can display any data provided that it is wrapped in a
 * ListAdapter.
 */
public interface ListAdapter extends Adapter {

    /**
     * Indicates whether all the items in this adapter are enabled. If the
     * value returned by this method changes over time, there is no guarantee
     * it will take effect.  If true, it means all items are selectable and
     * clickable (there is no separator.)
     * 
     * @return True if all items are enabled, false otherwise.
     * 
     * @see #isEnabled(int) 
     */
    public boolean areAllItemsEnabled();

    /**
     * Returns true if the item at the specified position is not a separator.
     * (A separator is a non-selectable, non-clickable item).
     * 
     * The result is unspecified if position is invalid. An {@link ArrayIndexOutOfBoundsException}
     * should be thrown in that case for fast failure.
     *
     * @param position Index of the item
     * 
     * @return True if the item is not a separator
     * 
     * @see #areAllItemsEnabled() 
     */
    boolean isEnabled(int position);
}