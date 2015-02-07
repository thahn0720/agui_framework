package thahn.java.agui.sqlite;

import thahn.java.agui.app.Context;
import thahn.java.agui.utils.Log;

public abstract class SQLiteOpenHelper {
	private static final String 										TAG 				= 	"SQLiteOpenHelper";
	private boolean 													isOpened 			= 	false;
	private SQLiteDatabase												mDatabase;
	
	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void init() {
		mDatabase = new SQLiteDatabase(mName);
	}
	
//	public void execute() throws SQLException {
//		Statement statement = mConnection.createStatement();
//		statement.setQueryTimeout(30); // set timeout to 30 sec.
//
//		statement.executeUpdate("drop table if exists person");
//		statement.executeUpdate("create table person (id integer, name string)");
//		statement.executeUpdate("insert into person values(1, 'leo')");
//		statement.executeUpdate("insert into person values(2, 'yui')");
//		ResultSet rs = statement.executeQuery("select * from person");
//		while (rs.next()) {
//			// read the result set
//			System.out.println("name = " + rs.getString("name"));
//			System.out.println("id = " + rs.getInt("id"));
//		}
//	}
	
    private final Context mContext;
    private final String mName;
//    private final CursorFactory mFactory;
    private final int mNewVersion;

    private boolean mIsInitializing = false;

    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use to open or create the database
     * @param name of the database file, or null for an in-memory database
     * @param factory to use for creating cursor objects, or null for the default
     * @param version number of the database (starting at 1); if the database is older,
     *     {@link #onUpgrade} will be used to upgrade the database
     */
    public SQLiteOpenHelper(Context context, String name, int version) {
        if (version < 1) throw new IllegalArgumentException("Version must be >= 1, was " + version);

        mContext = context;
        mName = name;
        mNewVersion = version;
    }

    /**
     * Create and/or open a database that will be used for reading and writing.
     * The first time this is called, the database will be opened and
     * {@link #onCreate}, {@link #onUpgrade} and/or {@link #onOpen} will be
     * called.
     *
     * <p>Once opened successfully, the database is cached, so you can
     * call this method every time you need to write to the database.
     * (Make sure to call {@link #close} when you no longer need the database.)
     * Errors such as bad permissions or a full disk may cause this method
     * to fail, but future attempts may succeed if the problem is fixed.</p>
     *
     * <p class="caution">Database upgrade may take a long time, you
     * should not call this method from the application main thread, including
     * from {@link android.content.ContentProvider#onCreate ContentProvider.onCreate()}.
     *
     * @throws SQLiteException if the database cannot be opened for writing
     * @return a read/write database object valid until {@link #close} is called
     */
    public synchronized SQLiteDatabase getWritableDatabase() {
        if (mDatabase != null && mDatabase.isOpen() && !mDatabase.isReadOnly()) {
            return mDatabase;  // The database is already open for business
        }
        
        if (mIsInitializing) {
            throw new IllegalStateException("getWritableDatabase called recursively");
        }

        // If we have a read-only database open, someone could be using it
        // (though they shouldn't), which would cause a lock to be held on
        // the file, and our attempts to open the database read-write would
        // fail waiting for the file lock.  To prevent that, we acquire the
        // lock on the read-only database, which shuts out other users.
        
        boolean success = false;
        SQLiteDatabase db = null;
        
//        if (mDatabase != null) mDatabase.lock();
        try {
            mIsInitializing = true;
//            if (mName == null) {
            	// TODO : implements if name is null
//                db = SQLiteDatabase.create(null);
//            } else {
            db = new SQLiteDatabase(mName);
            	db.openOrCreateDatabase();
//            }
            
            int version = db.getVersion();
            if (version != mNewVersion) {
            	// TODO : implements transaction
//                db.beginTransaction();
                try {
                    if (version == 0) {
                        onCreate(db);
                    } else {
                        if (version > mNewVersion) {
                            Log.i(TAG, "Can't downgrade read-only database from version " +
                                    version + " to " + mNewVersion + ": " + mName);
                        }
                        onUpgrade(db, version, mNewVersion);
                    }
                    db.setVersion(mNewVersion);
//                    db.setTransactionSuccessful();
                } finally {
//                    db.endTransaction();
                }
            }

            onOpen(db);
            success = true;
            return db;
        } finally {
            mIsInitializing = false;
            if (success) {
//                if (mDatabase != null) {
//                    try { mDatabase.close(); } catch (Exception e) { }
//                    mDatabase.unlock();
//                }
                mDatabase = db;
            } else {
                if (mDatabase != null) mDatabase.unlock();
                if (db != null) db.close();
            }
        }
    }

    /**
     * Create and/or open a database.  This will be the same object returned by
     * {@link #getWritableDatabase} unless some problem, such as a full disk,
     * requires the database to be opened read-only.  In that case, a read-only
     * database object will be returned.  If the problem is fixed, a future call
     * to {@link #getWritableDatabase} may succeed, in which case the read-only
     * database object will be closed and the read/write object will be returned
     * in the future.
     *
     * <p class="caution">Like {@link #getWritableDatabase}, this method may
     * take a long time to return, so you should not call it from the
     * application main thread, including from
     * {@link android.content.ContentProvider#onCreate ContentProvider.onCreate()}.
     *
     * @throws SQLiteException if the database cannot be opened
     * @return a database object valid until {@link #getWritableDatabase}
     *     or {@link #close} is called.
     */
//    public synchronized SQLiteDatabase getReadableDatabase() {
//        if (mDatabase != null && mDatabase.isOpen()) {
//            return mDatabase;  // The database is already open for business
//        }
//
//        if (mIsInitializing) {
//            throw new IllegalStateException("getReadableDatabase called recursively");
//        }
//
//        try {
//            return getWritableDatabase();
//        } catch (SQLiteException e) {
//            if (mName == null) throw e;  // Can't open a temp database read-only!
//            Log.e(TAG, "Couldn't open " + mName + " for writing (will try read-only):", e);
//        }
//
//        SQLiteDatabase db = null;
//        try {
//            mIsInitializing = true;
//            String path = mContext.getDatabasePath(mName).getPath();
//            db = SQLiteDatabase.openDatabase(path, mFactory, SQLiteDatabase.OPEN_READONLY);
//            if (db.getVersion() != mNewVersion) {
//                throw new SQLiteException("Can't upgrade read-only database from version " +
//                        db.getVersion() + " to " + mNewVersion + ": " + path);
//            }
//
//            onOpen(db);
//            Log.i(TAG, "Opened " + mName + " in read-only mode");
//            mDatabase = db;
//            return mDatabase;
//        } finally {
//            mIsInitializing = false;
//            if (db != null && db != mDatabase) db.close();
//        }
//    }
    
    /**
     * Close any open database object.
     */
    public synchronized void close() {
        if (mIsInitializing) throw new IllegalStateException("Closed during initialization");

        if (mDatabase != null && mDatabase.isOpen()) {
            mDatabase.close();
            mDatabase = null;
        }
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    public abstract void onCreate(SQLiteDatabase db);

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     *
     * <p>The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     *
     * @param db The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    public abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

    /**
     * Called when the database has been opened.  The implementation
     * should check {@link SQLiteDatabase#isReadOnly} before updating the
     * database.
     *
     * @param db The database.
     */
    public void onOpen(SQLiteDatabase db) {}
}
