package thahn.java.agui.widget;

/**
 * Defines an extension for views that make them checkable.
 *
 */
public interface Checkable {
    
    /**
     * Change the checked state of the view
     * 
     * @param checked The new checked state
     */
    void setChecked(boolean checked);
        
    /**
     * @return The current checked state of the view
     */
    boolean isChecked();
    
    /**
     * Change the checked state of the view to the inverse of its current state
     *
     */
    void toggle();
}
