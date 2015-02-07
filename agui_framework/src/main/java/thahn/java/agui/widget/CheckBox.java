package thahn.java.agui.widget;

import thahn.java.agui.app.Context;
import thahn.java.agui.view.AttributeSet;

public class CheckBox extends CompoundButton {

	public CheckBox(Context context) {
		this(context, new AttributeSet(context));
	}

	public CheckBox(Context context, AttributeSet attrs) {
		this(context, attrs, ViewName.WIDGET_CHECK_BOX_CODE);
	}

	public CheckBox(Context context, AttributeSet attrs, int defaultType) {
		super(context, attrs, defaultType);
	}
}
