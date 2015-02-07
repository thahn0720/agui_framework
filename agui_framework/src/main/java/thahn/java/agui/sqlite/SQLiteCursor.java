package thahn.java.agui.sqlite;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import thahn.java.agui.app.Bundle;
import thahn.java.agui.database.DataSetObserver;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;


/**
 * cursor is zero-based
 * @author thAhn
 *
 */
public class SQLiteCursor implements Cursor {

	private int														mPosition;
	private ResultSet												mResultSet;
	private ResultSetMetaData										mMetaData;
	private BiMap<String, Integer>									mColumnMap;
	private LinkedList<List<Object>>								mRecords;
	
	private SQLiteCursor(ResultSet rs) {
		init();
		setResultSet(rs);
	}
	
	private void init() {
		mRecords = new LinkedList<>();
		mColumnMap = HashBiMap.create();
		mPosition = 0;
	}
	
	public static Cursor createCursor(Connection conn, String sql) {
		Cursor cursor = null;
		try {
			Statement stat = conn.createStatement();
			if(stat.execute(sql)) {
				cursor = new SQLiteCursor(stat.getResultSet());
			} else {
				// update
				stat.getUpdateCount();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cursor;
	}
	
	private void setResultSet(ResultSet rs) {
		mResultSet = rs;
		try {
			mMetaData = rs.getMetaData();
			
			for(int i=0;i<mMetaData.getColumnCount();++i) {
				mColumnMap.put(mMetaData.getColumnName(i+1), i);
			}
			BiMap<Integer, String> temp = mColumnMap.inverse();
			while (rs.next()) {
				List<Object> record = new ArrayList<>();
				for(int i=1;i<=mColumnMap.size();++i) {
//					String column = String.valueOf(temp.get(i));
					int type = mMetaData.getColumnType(i);
					Object value = null;
					switch (type) {
					case Types.TINYINT:
					case Types.SMALLINT:
					case Types.BIGINT:
					case Types.INTEGER:
						value = rs.getInt(i);
						break;
					case Types.FLOAT:
					case Types.REAL:
						value = rs.getFloat(i);
						break;
					case Types.BLOB:
						Blob blob = rs.getBlob(i);
						value = blob.getBytes((long)0, (int)(blob.length()-1));
						break;
					case Types.DOUBLE:
						value = rs.getDouble(i);
						break;
					case Types.VARCHAR:
						value = rs.getString(i);
						break;
					}
					record.add(value);
				}
				mRecords.add(record);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getCount() {
		return mRecords.size();
	}

	@Override
	public int getPosition() {
		return mPosition;
	}
	
	/**
	 * 
	 * @param position zero-based
	 * @return
	 */
	private boolean moveTo(int position) {
		boolean ret = false;
		if(position <= getCount()-1) {
			ret = true;
			mPosition = position;
		} else {
			throw new IndexOutOfBoundsException();
		}
		return ret;
//		boolean ret = false;
//		try {
//			ret = mResultSet.absolute(position);
//			mPosition = position;
//		} catch (SQLException e) {
//			ret = false;
//			e.printStackTrace();
//		}
//		return ret;
	}

	@Override
	public boolean move(int offset) {
		return moveTo(mPosition+offset);
	}

	@Override
	public boolean moveToPosition(int position) {
		return moveTo(position);
	}

	/**
	 * did not implement in sqlite jdbc.
	 */
	@Deprecated // by me
	@Override
	public boolean moveToFirst() {
		return moveTo(0);
//		boolean ret = false;
//		try {
//			ret = mResultSet.first();
//			mPosition = 0;
//		} catch (SQLException e) {
//			ret = false;
//			e.printStackTrace();
//		}
//		return ret;
	}

	/**
	 * did not implement in sqlite jdbc.
	 */
	@Deprecated // by me
	@Override
	public boolean moveToLast() {
		return moveTo(getCount()-1);
//		boolean ret = false;
//		try {
//			ret = mResultSet.last();
//			mPosition = mResultSet.getRow()-1;
//		} catch (SQLException e) {
//			ret = false;
//			e.printStackTrace();
//		}
//		return ret;
	}

	@Override
	public boolean moveToNext() {
		boolean ret = true;
		++mPosition;
		if(mPosition >= mRecords.size()) {
			ret = false;
			mPosition = mRecords.size()-1;
		}
//		boolean ret = false;
//		try {
//			ret = mResultSet.next();
//			if(ret) mPosition += 1;
//		} catch (SQLException e) {
//			ret = false;
//			e.printStackTrace();
//		}
		return ret;
	}

	@Override
	public boolean moveToPrevious() {
		return moveTo(getPosition()-1);
//		boolean ret = false;
//		try {
//			ret = mResultSet.previous();
//			if(ret) mPosition -= 1;
//		} catch (SQLException e) {
//			ret = false;
//			e.printStackTrace();
//		}
//		return ret;
	}

	@Override
	public boolean isFirst() {
		return mPosition == 0? true:false;
//		boolean ret = false;
//		try {
//			ret = mResultSet.isFirst();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return ret;
	}

	@Override
	public boolean isLast() {
		return mPosition == getCount()-1? true:false;
//		boolean ret = false;
//		try {
//			ret = mResultSet.isLast();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return ret;
	}

	/**
	 * did not implement in sqlite jdbc.
	 */
	@Deprecated // by me
	@Override
	public boolean isBeforeFirst() {
//		mResultSet.afterLast()
		return false;
	}

	/**
	 * did not implement in sqlite jdbc.
	 */
	@Deprecated // by me
	@Override
	public boolean isAfterLast() {
//		mResultSet.beforeFirst()
		return false;
	}
	
	@Override
	public int getColumnIndex(String columnName) {
		int ret = -1;
		try {
			ret = mResultSet.findColumn(columnName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
		Integer value = mColumnMap.get(columnName);
		if(value == null) {
			throw new IllegalArgumentException();
		} else {
			return value;
		}
//		int ret = -1;
//		try {
//			ret = mResultSet.findColumn(columnName);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		if(ret == -1) throw new IllegalArgumentException();
//		return ret;
	}

	@Override
	public String getColumnName(int columnIndex) {
		String ret = null;
		try {
			ret = mMetaData.getColumnName(columnIndex);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public String[] getColumnNames() {
		return mColumnMap.keySet().toArray(new String[mColumnMap.keySet().size()]);
//		String[] ret = null;
//		try {
//			int size = mMetaData.getColumnCount();
//			ret = new String[size];
//			for(int i=0;i<size;++i) {
//				ret[i] = mMetaData.getColumnName(i);
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return ret;
	}

	@Override
	public int getColumnCount() {
		return mColumnMap.size();
//		int ret = 0;
//		try {
//			ret = mMetaData.getColumnCount();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return ret;
	}

	@Override
	public byte[] getBlob(int columnIndex) {
		return (byte[]) mRecords.get(mPosition).get(columnIndex);
//		byte[] ret = null;
//		try {
//			Blob temp = mResultSet.getBlob(columnIndex);
//			ret = temp.getBytes((long)0, (int)temp.length());
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return ret;
	}

	@Override
	public String getString(int columnIndex) {
		return (String) mRecords.get(mPosition).get(columnIndex);
//		String ret = null;
//		try {
//			ret = mResultSet.getString(columnIndex);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return ret;
	}

	@Override
	public short getShort(int columnIndex) {
		return (Short) mRecords.get(mPosition).get(columnIndex);
//		short ret = 0;
//		try {
//			ret = mResultSet.getShort(columnIndex);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return ret;
	}

	@Override
	public int getInt(int columnIndex) {
		return (Integer) mRecords.get(mPosition).get(columnIndex);
//		int ret = 0;
//		try {
//			ret = mResultSet.getInt(columnIndex);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return ret;
	}

	@Override
	public long getLong(int columnIndex) {
		Object value = mRecords.get(mPosition).get(columnIndex);
		if(value instanceof Integer) {
			float temp = (Integer) value;
			value = temp;
		}
		return (Long) value;
//		long ret = 0;
//		try {
//			ret = mResultSet.getLong(columnIndex);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return ret;
	}

	@Override
	public float getFloat(int columnIndex) {
		Object value = mRecords.get(mPosition).get(columnIndex);
		if(value instanceof Integer) {
			float temp = (Integer) value;
			value = temp;
		}
		return (Float) value;
//		float ret = 0;
//		try {
//			ret = mResultSet.getFloat(columnIndex);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return ret;
	}

	@Override
	public double getDouble(int columnIndex) {
		Object value = mRecords.get(mPosition).get(columnIndex);
		if(value instanceof Integer) {
			double temp = (Integer) value;
			value = temp;
		}
		return (Double) value;
//		double ret = 0;
//		try {
//			ret = mResultSet.getDouble(columnIndex);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return ret;
	}

	@Deprecated // by me
	@Override
	public boolean isNull(int columnIndex) {
		boolean ret = false;
		Object value = mRecords.get(mPosition).get(columnIndex);
		if(value == null) {
			ret = true;
		}
		return ret;
	}

	/**
	 * this is the same with {@link SQLiteCursor#close()}
	 */
	@Override
	public void deactivate() {
		close();
	}

	@Deprecated // by me
	@Override
	public boolean requery() {
		return false;
	}

	@Override
	public void close() {
		try {
			mResultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isClosed() {
		boolean ret = true;
		try {
			ret = mResultSet.isClosed();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}

	@Deprecated // by me
	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
	}

	@Deprecated // by me
	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
	}

	@Deprecated // by me
	@Override
	public boolean getWantsAllOnMoveCalls() {
		return false;
	}

	@Deprecated // by me
	@Override
	public Bundle getExtras() {
		return null;
	}

	@Deprecated // by me
	@Override
	public Bundle respond(Bundle extras) {
		return null;
	}

	@Override
	@Deprecated
	public boolean deleteRow() {
		return false;
	}

	@Override
	@Deprecated
	public boolean supportsUpdates() {
		return false;
	}

	@Override
	@Deprecated
	public boolean hasUpdates() {
		return false;
	}

	@Override
	@Deprecated
	public boolean updateBlob(int columnIndex, byte[] value) {
		return false;
	}

	@Override
	@Deprecated
	public boolean updateString(int columnIndex, String value) {
		return false;
	}

	@Override
	@Deprecated
	public boolean updateShort(int columnIndex, short value) {
		return false;
	}

	@Override
	@Deprecated
	public boolean updateInt(int columnIndex, int value) {
		return false;
	}

	@Override
	@Deprecated
	public boolean updateLong(int columnIndex, long value) {
		return false;
	}

	@Override
	@Deprecated
	public boolean updateFloat(int columnIndex, float value) {
		return false;
	}

	@Override
	@Deprecated
	public 	boolean updateDouble(int columnIndex, double value) {
		return false;
	}

	@Override
	@Deprecated
	public 	boolean updateToNull(int columnIndex) {
		return false;
	}

	@Override
	@Deprecated
	public boolean commitUpdates() {
		return false;
	}

	@Override
	@Deprecated
	public boolean commitUpdates(
			Map<? extends Long, ? extends Map<String, Object>> values) {
		return false;
	}

	@Override
	@Deprecated
	public void abortUpdates() {
	}
}
