package thahn.java.agui.sqlite;

/**
 * An object created from a SQLiteDatabase that can be closed.
 */
public abstract class SQLiteClosable {
    private int mReferenceCount = 1;
    private Object mLock = new Object();

    protected abstract void onAllReferencesReleased();
    protected void onAllReferencesReleasedFromContainer() {}

    public void acquireReference() {
        synchronized(mLock) {
            if (mReferenceCount <= 0) {
                throw new IllegalStateException(
                        "attempt to re-open an already-closed object: ");// + getObjInfo());
            }
            mReferenceCount++;
        }
    }

    public void releaseReference() {
        synchronized(mLock) {
            mReferenceCount--;
            if (mReferenceCount == 0) {
                onAllReferencesReleased();
            }
        }
    }

    public void releaseReferenceFromContainer() {
        synchronized(mLock) {
            mReferenceCount--;
            if (mReferenceCount == 0) {
                onAllReferencesReleasedFromContainer();
            }
        }
    }

//    private String getObjInfo() {
//        StringBuilder buff = new StringBuilder();
//        buff.append(this.getClass().getName());
//        buff.append(" (");
//        if (this instanceof SQLiteDatabase) {
//            buff.append("database = ");
//            buff.append(((SQLiteDatabase)this).getPath());
//        } else if (this instanceof SQLiteProgram || this instanceof SQLiteStatement ||
//                this instanceof SQLiteQuery) {
//            buff.append("mSql = ");
//            buff.append(((SQLiteProgram)this).mSql);
//        } else if (this instanceof CursorWindow) {
//            buff.append("mStartPos = ");
//            buff.append(((CursorWindow)this).getStartPosition());
//        }
//        buff.append(") ");
//        return buff.toString();
//    }
}
