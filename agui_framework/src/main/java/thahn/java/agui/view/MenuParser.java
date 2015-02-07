package thahn.java.agui.view;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import thahn.java.agui.app.Activity;
import thahn.java.agui.app.Context;
import thahn.java.agui.app.Menu;
import thahn.java.agui.parser.AbstractParser;
import thahn.java.agui.utils.MyUtils;
import thahn.java.agui.view.MenuItem.OnMenuItemClickListener;
import thahn.java.agui.view.View.OnClickListener;


public class MenuParser extends AbstractParser {

	private Context													mContext;
	private Object													mRealOwner;
	private String 													mMenuPath;
	private Menu													mMenuContainer;
	
	public MenuParser(Context context, Object realOwner, Menu menu, String projectPath) {
		super();
		mContext = context;
		mRealOwner = realOwner;
		mMenuContainer = menu;
		mMenuPath = projectPath;
	}

//	agui:id="@+id/menu_refresh"
//			agui:title="refresh"
//			agui:icon="@drawable/test"
//			agui:showAsAction="always"
//			agui:orderInCategory="101"
	@Override
	public void parse() {
		try {
			BufferedInputStream bi = new BufferedInputStream(MyUtils.getResourceInputStream(mMenuPath));
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(bi);
			Element root = doc.getRootElement();
			PriorityQueue<MenuItem> temp = new PriorityQueue<>(10, new Comparator<MenuItem>() {
				@Override
				public int compare(MenuItem o1, MenuItem o2) {
					int ret = 0;
					if(o1.getOrder() > o2.getOrder()) {
						ret = 1;
					} else if(o1.getOrder() < o2.getOrder()) {
						ret = -1;
					}
					return ret;
				}
			});
			// menu
			for(Element e : (List<Element>) root.getChildren()) {
				AttributeSet attrSet = new AttributeSet(mContext, e);
				MenuItemImpl item = new MenuItemImpl(mContext, attrSet);
				item.setOnMenuItemClickListener(onMenuItemClickListener);
				//
				View actionView = item.getActionView();
				if(actionView != null) {
					setMenuItemClickListener(actionView, item);
				}
				//
				temp.add(item);
			}
			for(MenuItem item : temp) {
				mMenuContainer.addMenu(item);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}
	
	private void setMenuItemClickListener(View actionView, MenuItem item) {
		actionView.setTag(item);
		actionView.setOnClickListener(onClickListener);
		if(actionView instanceof ViewGroup) {
			View[] views = ((ViewGroup) actionView).getChildren();
			int size = views.length;
			for(int i=0;i<size;++i) {
				setMenuItemClickListener(views[i], item);
			}
			
		} 
	}
	
	public Menu getMenu() {
		return mMenuContainer;
	}
	
	private OnMenuItemClickListener onMenuItemClickListener = new OnMenuItemClickListener() {
		
		@Override
		public boolean onMenuItemClick(MenuItem item) {
			if (mRealOwner == null) {
				((Activity) mContext).onOptionsItemSelected(item);
			} else {
				((Activity) mRealOwner).onOptionsItemSelected(item);
			}
			return false;
		}
	};
	
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			MenuItemImpl item = v.getTag()!=null?(MenuItemImpl)v.getTag():null;
			item.menuItemClickListener.onMenuItemClick(item);
		}
	};
	
//	class MenuInfo {
//		String name;
//		HashMap<Integer, String> attrs;
//	}
}
