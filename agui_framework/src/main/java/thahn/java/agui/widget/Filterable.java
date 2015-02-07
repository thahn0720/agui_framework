package thahn.java.agui.widget;

/**
 * <p>Defines a filterable behavior. A filterable class can have its data
 * constrained by a filter. Filterable classes are usually
 * {@link android.widget.Adapter} implementations.</p>
 *
 * @see android.widget.Filter
 */
public interface Filterable {
    /**
     * <p>Returns a filter that can be used to constrain data with a filtering
     * pattern.</p>
     *
     * <p>This method is usually implemented by {@link android.widget.Adapter}
     * classes.</p>
     *
     * @return a filter used to constrain data
     */
    Filter getFilter();
}
