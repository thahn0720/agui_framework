package thahn.java.agui.view;

import thahn.java.agui.app.Context;
import thahn.java.agui.app.Intent;
import thahn.java.agui.exception.NotExistException;
import thahn.java.agui.graphics.Drawable;
import thahn.java.agui.widget.TextView;


public class MenuItemImpl implements MenuItem {

	Context															mContext;
	int 															id;
	int 															groupId;
	int 															order;
	int 															showAsAction;
	int 															layoutId;
	String 															title;
	Drawable														icon;	
	View															actionView;
	MenuItem.OnMenuItemClickListener								menuItemClickListener;
	
	public static final MenuItem makeMenuItem(Context context, int id) {
		MenuItem item = new MenuItemImpl(context, id);
		return item;
	}
	
	private MenuItemImpl(Context context, int id) {
		mContext = context;
		this.id = id; 
	}
	
	MenuItemImpl(Context context, AttributeSet a) {
		mContext = context;
		
		id = a.getResourceId(thahn.java.agui.R.attr.Menu_id, -1);
		setIcon(a.getDrawable(thahn.java.agui.R.attr.Menu_icon, -1));
		setTitle(a.getString(thahn.java.agui.R.attr.Menu_title, ""));
		
		layoutId = a.getLayoutId(thahn.java.agui.R.attr.ActionBar_actionLayout, -1);
		if(layoutId == -1) {
			actionView = LayoutInflater.inflate(context, thahn.java.agui.R.layout.agui_actionbar_item, null);
			actionView.setId(id);
			if(icon != null) actionView.findViewById(thahn.java.agui.R.id.item_icon).setBackgroundDrawable(icon);
			if(title != null) ((TextView)actionView.findViewById(thahn.java.agui.R.id.item_title)).setText(title);
		} else {
			actionView = LayoutInflater.inflate(context, layoutId, null);
		}
		order = a.getInt(thahn.java.agui.R.attr.ActionBar_orderInCategory, -1);
		setShowAsAction(a.getIntFromEnum(thahn.java.agui.R.attr.ActionBar_showAsAction, MenuItem.SHOW_AS_ACTION_ALWAYS));
	}

	@Override
	public int getItemId() {
		return id;
	}

	@Override
	public int getGroupId() {
		return 0;
	}

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public MenuItem setTitle(CharSequence title) {
		this.title = String.valueOf(title);
		if(layoutId == -1) ((TextView)actionView.findViewById(thahn.java.agui.R.id.item_title)).setText(this.title);
		return this;
	}

	@Override
	public MenuItem setTitle(int title) {
		String ret = mContext.getResources().getString(title);
		if(ret != null) {
			setTitle(ret);
		} else {
			throw new NotExistException("resource id : " + title + " not exist");
		}
		return this;
	}

	@Override
	public CharSequence getTitle() {
		String ret = null;
		if(layoutId == -1) ret = ((TextView)actionView.findViewById(thahn.java.agui.R.id.item_title)).getText();
		return ret;
	}

	@Override
	public MenuItem setTitleCondensed(CharSequence title) {
		return null;
	}

	@Override
	public CharSequence getTitleCondensed() {
		return null;
	}

	@Override
	public MenuItem setIcon(Drawable icon) {
		if(icon != null) {
			this.icon = icon;
		}
		return this;
	}

	@Override
	public MenuItem setIcon(int iconRes) {
		this.icon = mContext.getResources().getDrawable(iconRes);
		return this;
	}

	@Override
	public Drawable getIcon() {
		return this.icon;
	}

	@Override
	public MenuItem setIntent(Intent intent) {
		return null;
	}

	@Override
	public Intent getIntent() {
		return null;
	}

	@Override
	public MenuItem setShortcut(char numericChar, char alphaChar) {
		return null;
	}

	@Override
	public MenuItem setNumericShortcut(char numericChar) {
		return null;
	}

	@Override
	public char getNumericShortcut() {
		return 0;
	}

	@Override
	public MenuItem setAlphabeticShortcut(char alphaChar) {
		return null;
	}

	@Override
	public char getAlphabeticShortcut() {
		return 0;
	}

	@Override
	public MenuItem setCheckable(boolean checkable) {
		return null;
	}

	@Override
	public boolean isCheckable() {
		return false;
	}

	@Override
	public MenuItem setChecked(boolean checked) {
		return null;
	}

	@Override
	public boolean isChecked() {
		return false;
	}

	@Override
	public MenuItem setVisible(boolean visible) {
		return null;
	}

	@Override
	public boolean isVisible() {
		return false;
	}

	@Override
	public MenuItem setEnabled(boolean enabled) {
		return null;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	@Override
	public boolean hasSubMenu() {
		return false;
	}

	@Override
	public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
		this.menuItemClickListener = menuItemClickListener;
		return this;
	}

	@Override
	public void setShowAsAction(int actionEnum) {
		showAsAction = actionEnum;
		switch (showAsAction) {
		case MenuItem.SHOW_AS_ACTION_ALWAYS:
			if(layoutId == -1) {
				actionView.setVisibility(View.VISIBLE);
				actionView.findViewById(thahn.java.agui.R.id.item_title).setVisibility(View.GONE);
			}
			break;
		case MenuItem.SHOW_AS_ACTION_WITH_TEXT:
			if(layoutId == -1) {
				actionView.setVisibility(View.VISIBLE);
				actionView.findViewById(thahn.java.agui.R.id.item_title).setVisibility(View.VISIBLE);
			}
			break;
		case MenuItem.SHOW_AS_ACTION_NEVER:
			actionView.setVisibility(View.GONE);
			break;
		case MenuItem.SHOW_AS_ACTION_IF_ROOM:
			break;
		case MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW:
			break;
		}
	}

	@Override
	public MenuItem setShowAsActionFlags(int actionEnum) {
		return null;
	}

	@Override
	public MenuItem setActionView(View view) {
		actionView = view;
		return this;
	}

	@Override
	public MenuItem setActionView(int resId) {
		return null;
	}

	@Override
	public View getActionView() {
		return actionView;
	}

	@Override
	public boolean expandActionView() {
		return false;
	}

	@Override
	public boolean collapseActionView() {
		return false;
	}

	@Override
	public boolean isActionViewExpanded() {
		return false;
	}

	@Override
	public MenuItem setOnActionExpandListener(OnActionExpandListener listener) {
		return null;
	}
}
