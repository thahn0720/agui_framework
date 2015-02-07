package thahn.java.agui.app;

import thahn.java.agui.graphics.Drawable;
import thahn.java.agui.view.MenuItem;
import thahn.java.agui.view.View;
import thahn.java.agui.view.View.OnCreateContextMenuListener;
import thahn.java.agui.widget.TextView;

public class TitleActionBar extends ActionBar {

	/*package*/ TitleBar													mTitleBar;
	/*package*/ Menu														mMenu;
	
	public TitleActionBar() {
//		mMenu = new MenuContainer();
	}
	
	public void refresh() {
		if (mMenu != null) {
			mMenu = null;
		}
//		if (mMenu == null || mMenu.size() > 0) {
//			mMenu = new MenuContainer();
//		}
	}
	
	void setTitleBar(TitleBar bar) {
		mTitleBar = bar;
	}
	
	@Override
	public void setCustomView(View view) {
		mTitleBar.setCustomView(view);
	}

	@Override
	public void setCustomView(View view, LayoutParams layoutParams) {
	}

	@Override
	public void setCustomView(int resId) {
		mTitleBar.setCustomView(resId);
	}

	@Override
	public void setIcon(int resId) {
	}

	@Override
	public void setIcon(Drawable icon) {
	}

	@Override
	public void setLogo(int resId) {
	}

	@Override
	public void setLogo(Drawable logo) {
	}

	@Override
	public void setSelectedNavigationItem(int position) {
	}

	@Override
	public int getSelectedNavigationIndex() {
		return 0;
	}

	@Override
	public int getNavigationItemCount() {
		return 0;
	}

	@Override
	public void setTitle(CharSequence title) {
		if (mTitleBar.mTitleView != null) mTitleBar.mTitleView.setText(title.toString());
	}

	@Override
	public void setTitle(int resId) {
		if (mTitleBar.mTitleView != null) mTitleBar.mTitleView.setText(resId);
	}

	@Override
	public void setSubtitle(CharSequence subtitle) {
		if (mTitleBar.mSubTitleView != null) mTitleBar.mSubTitleView.setText(subtitle.toString());
	}

	@Override
	public void setSubtitle(int resId) {
		if (mTitleBar.mSubTitleView != null) mTitleBar.mSubTitleView.setText(resId);
	}

	@Override
	public void setDisplayOptions(int options) {
	}

	@Override
	public void setDisplayOptions(int options, int mask) {
	}

	@Override
	public void setDisplayUseLogoEnabled(boolean useLogo) {
	}

	@Override
	public void setDisplayShowHomeEnabled(boolean showHome) {
	}

	@Override
	public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {
	}

	@Override
	public void setDisplayShowTitleEnabled(boolean showTitle) {
	}

	@Override
	public void setDisplayShowCustomEnabled(boolean showCustom) {
	}

	@Override
	public void setBackgroundDrawable(Drawable d) {
		mTitleBar.mDecorView.setBackgroundDrawable(d);
	}

//	@Override
//	public void setBackgroundColor(int color) {
//		mTitleBar.mDecorView.setBackgroundColor(color);
//	}
	
	@Override
	public void setMaximizeDrawable(Drawable d) {
		mTitleBar.mMaxView.setBackgroundDrawable(d);
	}

	@Override
	public void setMinimizeDrawable(Drawable d) {
		mTitleBar.mMinView.setBackgroundDrawable(d);
	}

	@Override
	public void setExitDrawable(Drawable d) {
		mTitleBar.mExitView.setBackgroundDrawable(d);
	}
	
	@Override
	public void setMaximizeVisible(int visible) {
		mTitleBar.mMaxView.setVisibility(visible);
	}

	@Override
	public void setMinimizeVisible(int visible) {
		mTitleBar.mMinView.setVisibility(visible);
	}

	@Override
	public void setExitVisible(int visible) {
		mTitleBar.mExitView.setVisibility(visible);
	}

	@Override
	public View getCustomView() {
		return mTitleBar.mDecorView;
	}

	@Override
	public CharSequence getTitle() {
		return (CharSequence) mTitleBar.mTitleView.getText();
	}
	
//	@Override
//	public TextView getTitleView() {
//		return mTitleBar.mTitleView;
//	}

	@Override
	public CharSequence getSubtitle() {
		return (CharSequence) mTitleBar.mSubTitleView.getText();
	}
	
//	@Override
//	public TextView getSubtitleView() {
//		return mTitleBar.mSubTitleView;
//	}

	@Override
	public int getNavigationMode() {
		return 0;
	}

	@Override
	public void setNavigationMode(int mode) {
	}

	@Override
	public int getDisplayOptions() {
		return 0;
	}

	@Override
	public Tab newTab() {
		return null;
	}

	@Override
	public void addTab(Tab tab) {
	}

	@Override
	public void addTab(Tab tab, boolean setSelected) {
	}

	@Override
	public void addTab(Tab tab, int position) {
	}

	@Override
	public void addTab(Tab tab, int position, boolean setSelected) {
	}

	@Override
	public void removeTab(Tab tab) {
	}

	@Override
	public void removeTabAt(int position) {
	}

	@Override
	public void removeAllTabs() {
	}

	@Override
	public void selectTab(Tab tab) {
	}

	@Override
	public Tab getSelectedTab() {
		return null;
	}

	@Override
	public Tab getTabAt(int index) {
		return null;
	}

	@Override
	public int getTabCount() {
		return 0;
	}

	@Override
	public int getHeight() {
		return mTitleBar.mDecorView.getHeight();
	}

	@Override
	public void show() {
		mTitleBar.setVisible(true);
	}

	@Override
	public void hide() {
		mTitleBar.setVisible(false);
	}

	@Override
	public boolean isShowing() {
		return false;
	}

	@Override
	public void addOnMenuVisibilityListener(OnMenuVisibilityListener listener) {
	}

	@Override
	public void removeOnMenuVisibilityListener(OnMenuVisibilityListener listener) {
	}
	
	@Override
	public void registerForContextMenu(OnCreateContextMenuListener contextMenuListener, View view) {
		mTitleBar.registerForContextMenu(contextMenuListener, view);
	}
	
	@Override
	public void registerForContextMenu(OnCreateContextMenuListener contextMenuListener, View view, int btnCode) {
		mTitleBar.registerForContextMenu(contextMenuListener, view, btnCode);
	}
	
	@Override
	public Context getThemedContext() {
		return mTitleBar!=null?mTitleBar.getContext():null;
	}
	
	public void setVisible(boolean is) {
		mTitleBar.setVisible(is);
	}
	
	public boolean isTitleVisible() {
		boolean ret = false;
		if (mTitleBar != null) {
			ret = mTitleBar.isVisible();
		}
		return ret;
	}
	
	/*package*/ void setMenu() {
		for (int i=0;i<mMenu.size();++i) {
			MenuItem item = mMenu.getItem(i);
			mTitleBar.addMenuItem(item);
		}
	}
	
	/*package*/ void resizeTitleBar() {
		mTitleBar.resize();
	}
}
