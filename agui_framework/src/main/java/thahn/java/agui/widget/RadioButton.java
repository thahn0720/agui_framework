package thahn.java.agui.widget;

import thahn.java.agui.app.Context;
import thahn.java.agui.view.AttributeSet;

public class RadioButton extends CompoundButton {

	public RadioButton(Context context) {
		this(context, new AttributeSet(context));
	}
	
	public RadioButton(Context context, AttributeSet attrs) {
		this(context, attrs, ViewName.WIDGET_RADIO_BUTTON_CODE);
	}
	
	public RadioButton(Context context, AttributeSet attrs, int defaultType) {
		super(context, attrs, defaultType);
	}
}
