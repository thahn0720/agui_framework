package thahn.java.agui.app;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import thahn.java.agui.view.MenuItem;
import thahn.java.agui.view.View;


public class MenuContainer implements Menu {
	
	private List<MenuItem> 										mMenuContainer;
	
	public MenuContainer() {
		 mMenuContainer = new ArrayList<>();
	}
	
	@Override
	public void addMenu(MenuItem item) {
		mMenuContainer.add(item.getOrder(), item);
	}

	@Override
	public MenuItem add(CharSequence title) {
		return null;
	}

	@Override
	public MenuItem add(int titleRes) {
		return null;
	}

	@Override
	public MenuItem add(int groupId, int itemId, int order, CharSequence title) {
		return null;
	}

	@Override
	public MenuItem add(int groupId, int itemId, int order, int titleRes) {
		return null;
	}

	@Override
	public int addIntentOptions(int groupId, int itemId, int order,
			ComponentName caller, Intent[] specifics, Intent intent, int flags,
			MenuItem[] outSpecificItems) {
		return 0;
	}

	@Override
	public void removeItem(int id) {
	}

	@Override
	public void removeGroup(int groupId) {
	}

	@Override
	public void clear() {
	}

	@Override
	public void setGroupCheckable(int group, boolean checkable,
			boolean exclusive) {
	}

	@Override
	public void setGroupVisible(int group, boolean visible) {
	}

	@Override
	public void setGroupEnabled(int group, boolean enabled) {
	}

	@Override
	public boolean hasVisibleItems() {
		return false;
	}

	@Override
	public MenuItem findItem(int id) {
		MenuItem ret = null;
		for (MenuItem item : mMenuContainer) {
			if(item.getItemId() == id) {
				ret = item;
				break;
			}
		}
		return ret;
	}

	@Override
	public int size() {
		return mMenuContainer.size();
	}

	@Override
	public MenuItem getItem(int index) {
		return mMenuContainer.get(index);
	}

	@Override
	public void close() {
	}

	@Override
	public boolean performShortcut(int keyCode, KeyEvent event, int flags) {
		return false;
	}

	@Override
	public boolean isShortcutKey(int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public boolean performIdentifierAction(int id, int flags) {
		return false;
	}

	@Override
	public void setQwertyMode(boolean isQwerty) {
	}
}
