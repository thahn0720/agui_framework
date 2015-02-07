package thahn.java.agui.view;

import thahn.java.agui.app.Context;
import thahn.java.agui.exception.NotExistException;
import thahn.java.agui.view.ViewGroup.LayoutParams;
import thahn.java.agui.widget.FrameLayout;
import thahn.java.agui.widget.ViewName;

public class IncludeViewHelper {

	public static View getView(Context context, AttributeSet attrs, ViewGroup.LayoutParams params) {
		int layoutid = attrs.getLayoutId(thahn.java.agui.R.attr.include_layout, -1);
		int id = attrs.getResourceId(thahn.java.agui.R.attr.View_id, -1);
		if(layoutid == -1) throw new NotExistException("include layout id is wrong");
		
		View view = LayoutInflater.inflate(context, layoutid, null);
		if(id != -1) {
			FrameLayout frame = new FrameLayout(context, attrs);
			if (params != null) {
				frame.setLayoutParams(params);
			}
			frame.addView(view);
			return frame;
		} else {
			if (params != null) {
				view.setLayoutParams(params);
			}
			return view;
		}
	}
}
