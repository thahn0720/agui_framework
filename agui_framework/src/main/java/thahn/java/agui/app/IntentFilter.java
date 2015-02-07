package thahn.java.agui.app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Structured description of Intent values to be matched.  An IntentFilter can
 * match against actions, categories, and data (either via its type, scheme,
 * and/or path) in an Intent.  It also includes a "priority" value which is
 * used to order multiple matching filters.
 *
 * <p>IntentFilter objects are often created in XML as part of a package's
 * {@link android.R.styleable#AndroidManifest AndroidManifest.xml} file,
 * using {@link android.R.styleable#AndroidManifestIntentFilter intent-filter}
 * tags.
 *
 * <p>There are three Intent characteristics you can filter on: the
 * <em>action</em>, <em>data</em>, and <em>categories</em>.  For each of these
 * characteristics you can provide
 * multiple possible matching values (via {@link #addAction},
 * {@link #addDataType}, {@link #addDataScheme} {@link #addDataAuthority},
 * {@link #addDataPath}, and {@link #addCategory}, respectively).
 * For actions, the field
 * will not be tested if no values have been given (treating it as a wildcard);
 * if no data characteristics are specified, however, then the filter will
 * only match intents that contain no data.
 *
 * <p>The data characteristic is
 * itself divided into three attributes: type, scheme, authority, and path.
 * Any that are
 * specified must match the contents of the Intent.  If you specify a scheme
 * but no type, only Intent that does not have a type (such as mailto:) will
 * match; a content: URI will never match because they always have a MIME type
 * that is supplied by their content provider.  Specifying a type with no scheme
 * has somewhat special meaning: it will match either an Intent with no URI
 * field, or an Intent with a content: or file: URI.  If you specify neither,
 * then only an Intent with no data or type will match.  To specify an authority,
 * you must also specify one or more schemes that it is associated with.
 * To specify a path, you also must specify both one or more authorities and
 * one or more schemes it is associated with.
 *
 * <p>A match is based on the following rules.  Note that
 * for an IntentFilter to match an Intent, three conditions must hold:
 * the <strong>action</strong> and <strong>category</strong> must match, and
 * the data (both the <strong>data type</strong> and
 * <strong>data scheme+authority+path</strong> if specified) must match.
 *
 * <p><strong>Action</strong> matches if any of the given values match the
 * Intent action, <em>or</em> if no actions were specified in the filter.
 *
 * <p><strong>Data Type</strong> matches if any of the given values match the
 * Intent type.  The Intent
 * type is determined by calling {@link Intent#resolveType}.  A wildcard can be
 * used for the MIME sub-type, in both the Intent and IntentFilter, so that the
 * type "audio/*" will match "audio/mpeg", "audio/aiff", "audio/*", etc.
 * <em>Note that MIME type matching here is <b>case sensitive</b>, unlike
 * formal RFC MIME types!</em>  You should thus always use lower case letters
 * for your MIME types.
 *
 * <p><strong>Data Scheme</strong> matches if any of the given values match the
 * Intent data's scheme.
 * The Intent scheme is determined by calling {@link Intent#getData}
 * and {@link android.net.Uri#getScheme} on that URI.
 * <em>Note that scheme matching here is <b>case sensitive</b>, unlike
 * formal RFC schemes!</em>  You should thus always use lower case letters
 * for your schemes.
 *
 * <p><strong>Data Authority</strong> matches if any of the given values match
 * the Intent's data authority <em>and</em> one of the data scheme's in the filter
 * has matched the Intent, <em>or</em> no authories were supplied in the filter.
 * The Intent authority is determined by calling
 * {@link Intent#getData} and {@link android.net.Uri#getAuthority} on that URI.
 * <em>Note that authority matching here is <b>case sensitive</b>, unlike
 * formal RFC host names!</em>  You should thus always use lower case letters
 * for your authority.
 * 
 * <p><strong>Data Path</strong> matches if any of the given values match the
 * Intent's data path <em>and</em> both a scheme and authority in the filter
 * has matched against the Intent, <em>or</em> no paths were supplied in the
 * filter.  The Intent authority is determined by calling
 * {@link Intent#getData} and {@link android.net.Uri#getPath} on that URI.
 *
 * <p><strong>Categories</strong> match if <em>all</em> of the categories in
 * the Intent match categories given in the filter.  Extra categories in the
 * filter that are not in the Intent will not cause the match to fail.  Note
 * that unlike the action, an IntentFilter with no categories
 * will only match an Intent that does not have any categories.
 */
public class IntentFilter implements Serializable {
	private static final long 							serialVersionUID = 6896355278188094267L;
	private final ArrayList<String> 					mActions;
    private ArrayList<String> 							mCategories = null;
    private ArrayList<String> 							mDataSchemes = null;
    private ArrayList<String> 							mDataTypes = null;
    private boolean 									mHasPartialTypes = false;
    private int 										mPriority;

    /**
     * This exception is thrown when a given MIME type does not have a valid
     * syntax.
     */
    public static class MalformedMimeTypeException extends RuntimeException {
        public MalformedMimeTypeException() {
        }

        public MalformedMimeTypeException(String name) {
            super(name);
        }
    };

    /**
     * Create a new IntentFilter instance with a specified action and MIME
     * type, where you know the MIME type is correctly formatted.  This catches
     * the {@link MalformedMimeTypeException} exception that the constructor
     * can call and turns it into a runtime exception.
     *
     * @param action The action to match, i.e. Intent.ACTION_VIEW.
     * @param dataType The type to match, i.e. "vnd.android.cursor.dir/person".
     *
     * @return A new IntentFilter for the given action and type.
     *
     * @see #IntentFilter(String, String)
     */
    public static IntentFilter create(String action, String dataType) {
        try {
            return new IntentFilter(action, dataType);
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("Bad MIME type", e);
        }
    }

    /**
     * New empty IntentFilter.
     */
    public IntentFilter() {
        mPriority = 0;
        mActions = new ArrayList<String>();
    }

    /**
     * New IntentFilter that matches a single action with no data.  If
     * no data characteristics are subsequently specified, then the
     * filter will only match intents that contain no data.
     *
     * @param action The action to match, i.e. Intent.ACTION_MAIN.
     */
    public IntentFilter(String action) {
        mPriority = 0;
        mActions = new ArrayList<String>();
        addAction(action);
    }

    /**
     * New IntentFilter that matches a single action and data type.
     *
     * <p><em>Note: MIME type matching in the Android framework is
     * case-sensitive, unlike formal RFC MIME types.  As a result,
     * you should always write your MIME types with lower case letters,
     * and any MIME types you receive from outside of Android should be
     * converted to lower case before supplying them here.</em></p>
     *
     * <p>Throws {@link MalformedMimeTypeException} if the given MIME type is
     * not syntactically correct.
     *
     * @param action The action to match, i.e. Intent.ACTION_VIEW.
     * @param dataType The type to match, i.e. "vnd.android.cursor.dir/person".
     *
     */
    public IntentFilter(String action, String dataType)
        throws MalformedMimeTypeException {
        mPriority = 0;
        mActions = new ArrayList<String>();
        addAction(action);
        addDataType(dataType);
    }

    /**
     * New IntentFilter containing a copy of an existing filter.
     *
     * @param o The original filter to copy.
     */
    public IntentFilter(IntentFilter o) {
        mPriority = o.mPriority;
        mActions = new ArrayList<String>(o.mActions);
        if (o.mCategories != null) {
            mCategories = new ArrayList<String>(o.mCategories);
        }
        if (o.mDataTypes != null) {
            mDataTypes = new ArrayList<String>(o.mDataTypes);
        }
        if (o.mDataSchemes != null) {
            mDataSchemes = new ArrayList<String>(o.mDataSchemes);
        }
        mHasPartialTypes = o.mHasPartialTypes;
    }

    /**
     * Modify priority of this filter.  The default priority is 0. Positive
     * values will be before the default, lower values will be after it.
     * Applications must use a value that is larger than
     * {@link #SYSTEM_LOW_PRIORITY} and smaller than
     * {@link #SYSTEM_HIGH_PRIORITY} .
     *
     * @param priority The new priority value.
     *
     * @see #getPriority
     * @see #SYSTEM_LOW_PRIORITY
     * @see #SYSTEM_HIGH_PRIORITY
     */
    public final void setPriority(int priority) {
        mPriority = priority;
    }

    /**
     * Return the priority of this filter.
     *
     * @return The priority of the filter.
     *
     * @see #setPriority
     */
    public final int getPriority() {
        return mPriority;
    }

    /**
     * Add a new Intent action to match against.  If any actions are included
     * in the filter, then an Intent's action must be one of those values for
     * it to match.  If no actions are included, the Intent action is ignored.
     *
     * @param action Name of the action to match, i.e. Intent.ACTION_VIEW.
     */
    public final void addAction(String action) {
        if (!mActions.contains(action)) {
            mActions.add(action.intern());
        }
    }

    /**
     * Return the number of actions in the filter.
     */
    public final int countActions() {
        return mActions.size();
    }

    /**
     * Return an action in the filter.
     */
    public final String getAction(int index) {
        return mActions.get(index);
    }
    
    /**
     * Is the given action included in the filter?  Note that if the filter
     * does not include any actions, false will <em>always</em> be returned.
     *
     * @param action The action to look for.
     *
     * @return True if the action is explicitly mentioned in the filter.
     */
    public final boolean hasAction(String action) {
        return mActions.contains(action);
    }

    /**
     * Match this filter against an Intent's action.  If the filter does not
     * specify any actions, the match will always fail.
     *
     * @param action The desired action to look for.
     *
     * @return True if the action is listed in the filter or the filter does
     *         not specify any actions.
     */
    public final boolean matchAction(String action) {
        if (action == null || mActions == null || mActions.size() == 0) {
            return false;
        }
        return mActions.contains(action);
    }

    /**
     * Return an iterator over the filter's actions.  If there are no actions,
     * returns null.
     */
    public final Iterator<String> actionsIterator() {
        return mActions != null ? mActions.iterator() : null;
    }

    /**
     * Add a new Intent data type to match against.  If any types are
     * included in the filter, then an Intent's data must be <em>either</em>
     * one of these types <em>or</em> a matching scheme.  If no data types
     * are included, then an Intent will only match if it specifies no data.
     *
     * <p><em>Note: MIME type matching in the Android framework is
     * case-sensitive, unlike formal RFC MIME types.  As a result,
     * you should always write your MIME types with lower case letters,
     * and any MIME types you receive from outside of Android should be
     * converted to lower case before supplying them here.</em></p>
     *
     * <p>Throws {@link MalformedMimeTypeException} if the given MIME type is
     * not syntactically correct.
     *
     * @param type Name of the data type to match, i.e. "vnd.android.cursor.dir/person".
     *
     * @see #matchData
     */
    public final void addDataType(String type)
        throws MalformedMimeTypeException {
        final int slashpos = type.indexOf('/');
        final int typelen = type.length();
        if (slashpos > 0 && typelen >= slashpos+2) {
            if (mDataTypes == null) mDataTypes = new ArrayList<String>();
            if (typelen == slashpos+2 && type.charAt(slashpos+1) == '*') {
                String str = type.substring(0, slashpos);
                if (!mDataTypes.contains(str)) {
                    mDataTypes.add(str.intern());
                }
                mHasPartialTypes = true;
            } else {
                if (!mDataTypes.contains(type)) {
                    mDataTypes.add(type.intern());
                }
            }
            return;
        }

        throw new MalformedMimeTypeException(type);
    }

    /**
     * Is the given data type included in the filter?  Note that if the filter
     * does not include any type, false will <em>always</em> be returned.
     *
     * @param type The data type to look for.
     *
     * @return True if the type is explicitly mentioned in the filter.
     */
    public final boolean hasDataType(String type) {
        return mDataTypes != null && findMimeType(type);
    }

    /**
     * Return the number of data types in the filter.
     */
    public final int countDataTypes() {
        return mDataTypes != null ? mDataTypes.size() : 0;
    }

    /**
     * Return a data type in the filter.
     */
    public final String getDataType(int index) {
        return mDataTypes.get(index);
    }

    /**
     * Return an iterator over the filter's data types.
     */
    public final Iterator<String> typesIterator() {
        return mDataTypes != null ? mDataTypes.iterator() : null;
    }

    /**
     * Add a new Intent data scheme to match against.  If any schemes are
     * included in the filter, then an Intent's data must be <em>either</em>
     * one of these schemes <em>or</em> a matching data type.  If no schemes
     * are included, then an Intent will match only if it includes no data.
     *
     * <p><em>Note: scheme matching in the Android framework is
     * case-sensitive, unlike formal RFC schemes.  As a result,
     * you should always write your schemes with lower case letters,
     * and any schemes you receive from outside of Android should be
     * converted to lower case before supplying them here.</em></p>
     *
     * @param scheme Name of the scheme to match, i.e. "http".
     *
     * @see #matchData
     */
    public final void addDataScheme(String scheme) {
        if (mDataSchemes == null) mDataSchemes = new ArrayList<String>();
        if (!mDataSchemes.contains(scheme)) {
            mDataSchemes.add(scheme.intern());
        }
    }

    /**
     * Return the number of data schemes in the filter.
     */
    public final int countDataSchemes() {
        return mDataSchemes != null ? mDataSchemes.size() : 0;
    }

    /**
     * Return a data scheme in the filter.
     */
    public final String getDataScheme(int index) {
        return mDataSchemes.get(index);
    }

    /**
     * Is the given data scheme included in the filter?  Note that if the
     * filter does not include any scheme, false will <em>always</em> be
     * returned.
     *
     * @param scheme The data scheme to look for.
     *
     * @return True if the scheme is explicitly mentioned in the filter.
     */
    public final boolean hasDataScheme(String scheme) {
        return mDataSchemes != null && mDataSchemes.contains(scheme);
    }

    /**
     * Return an iterator over the filter's data schemes.
     */
    public final Iterator<String> schemesIterator() {
        return mDataSchemes != null ? mDataSchemes.iterator() : null;
    }

    /**
     * Add a new Intent category to match against.  The semantics of
     * categories is the opposite of actions -- an Intent includes the
     * categories that it requires, all of which must be included in the
     * filter in order to match.  In other words, adding a category to the
     * filter has no impact on matching unless that category is specified in
     * the intent.
     *
     * @param category Name of category to match, i.e. Intent.CATEGORY_EMBED.
     */
    public final void addCategory(String category) {
        if (mCategories == null) mCategories = new ArrayList<String>();
        if (!mCategories.contains(category)) {
            mCategories.add(category.intern());
        }
    }

    /**
     * Return the number of categories in the filter.
     */
    public final int countCategories() {
        return mCategories != null ? mCategories.size() : 0;
    }

    /**
     * Return a category in the filter.
     */
    public final String getCategory(int index) {
        return mCategories.get(index);
    }

    /**
     * Is the given category included in the filter?
     *
     * @param category The category that the filter supports.
     *
     * @return True if the category is explicitly mentioned in the filter.
     */
    public final boolean hasCategory(String category) {
        return mCategories != null && mCategories.contains(category);
    }

    /**
     * Return an iterator over the filter's categories.
     */
    public final Iterator<String> categoriesIterator() {
        return mCategories != null ? mCategories.iterator() : null;
    }

    /**
     * Match this filter against an Intent's categories.  Each category in
     * the Intent must be specified by the filter; if any are not in the
     * filter, the match fails.
     *
     * @param categories The categories included in the intent, as returned by
     *                   Intent.getCategories().
     *
     * @return If all categories match (success), null; else the name of the
     *         first category that didn't match.
     */
    public final String matchCategories(Set<String> categories) {
        if (categories == null) {
            return null;
        }

        Iterator<String> it = categories.iterator();

        if (mCategories == null) {
            return it.hasNext() ? it.next() : null;
        }

        while (it.hasNext()) {
            final String category = it.next();
            if (!mCategories.contains(category)) {
                return category;
            }
        }

        return null;
    }

    private final boolean findMimeType(String type) {
        final ArrayList<String> t = mDataTypes;

        if (type == null) {
            return false;
        }

        if (t.contains(type)) {
            return true;
        }

        // Deal with an Intent wanting to match every type in the IntentFilter.
        final int typeLength = type.length();
        if (typeLength == 3 && type.equals("*/*")) {
            return !t.isEmpty();
        }

        // Deal with this IntentFilter wanting to match every Intent type.
        if (mHasPartialTypes && t.contains("*")) {
            return true;
        }

        final int slashpos = type.indexOf('/');
        if (slashpos > 0) {
            if (mHasPartialTypes && t.contains(type.substring(0, slashpos))) {
                return true;
            }
            if (typeLength == slashpos+2 && type.charAt(slashpos+1) == '*') {
                // Need to look through all types for one that matches
                // our base...
                final Iterator<String> it = t.iterator();
                while (it.hasNext()) {
                    String v = it.next();
                    if (type.regionMatches(0, v, 0, slashpos+1)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
