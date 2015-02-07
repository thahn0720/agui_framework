package thahn.java.agui.view;

import thahn.java.agui.app.Context;
import thahn.java.agui.app.Menu;

public class MenuInflater {
	
	private Context														mContext;
	private MenuParser 													mMenuParser;
	private Object														mRealOwner;
	
	public MenuInflater(Context context) {
		mContext = context;
	}
	
	public MenuInflater(Context context, Object realOwner) {
		mContext = context;
		mRealOwner = realOwner;
	}

	public Menu inflate(int menuRes, Menu menu) {
		mMenuParser = new MenuParser(mContext, mRealOwner, menu, mContext.getResources().getMenu(menuRes));
		mMenuParser.parse();
		return mMenuParser.getMenu();
    }
}
