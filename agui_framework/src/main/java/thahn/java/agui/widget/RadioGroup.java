package thahn.java.agui.widget;

import thahn.java.agui.app.Context;
import thahn.java.agui.exception.WrongFormatException;
import thahn.java.agui.view.AttributeSet;
import thahn.java.agui.view.View;

public class RadioGroup extends LinearLayout {

	private int																mCheckedId			= -1;
	private OnCheckedChangeListener											mOnCheckedChangeListener;
	private CompoundButton.OnCheckedChangeListener 							mChildOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(isChecked) {
				checkInternal(buttonView.getId());
			} else if(buttonView.getId() == mCheckedId) {
				if (mOnCheckedChangeListener != null) {
	                mOnCheckedChangeListener.onCheckedChanged(RadioGroup.this, -1);
	            }
			}
		}
	};
	
	public RadioGroup(Context context) {
		this(context, new AttributeSet(context));
	}
	
	public RadioGroup(Context context, AttributeSet attrs) {
		this(context, attrs, ViewName.WIDGET_RADIO_GROUP_CODE);
	}
	
	public RadioGroup(Context context, AttributeSet attrs, int defaultType) {
		super(context, attrs, defaultType);
	}

	@Override
	protected void initFromAttributes(AttributeSet attrs, int defaultType) {
		super.initFromAttributes(attrs, defaultType);
		
//		int value = attributes.getResourceId(R.styleable.RadioGroup_checkedButton, View.NO_ID);
//        if (value != View.NO_ID) {
//            mCheckedId = value;
//        }
	}
	
    @Override
	public void arrange() {
		super.arrange();
	}

	@Override
	public void addView(View v) {
    	if(v instanceof CompoundButton) {
    		super.addView(v);
    		((CompoundButton)v).setOnCheckedChangeListener(mChildOnCheckedChangeListener);
    	} else {
    		throw new WrongFormatException("implements Checkable interface.");
    	}
	}
    
    @Override
	public void addViewInternal(View v) {
    	if(v instanceof CompoundButton) {
    		super.addViewInternal(v);
    		((CompoundButton)v).setOnCheckedChangeListener(mChildOnCheckedChangeListener);
    	} else {
    		throw new WrongFormatException("implements Checkable interface.");
    	}
	}

	public void check(int id) {
    	boolean checked = true;
    	if(id == -1) {
    		if(mCheckedId == -1) return ;
    		id = mCheckedId;
    		checked = false;
    	} 
    	CompoundButton button = (CompoundButton) findViewById(id);
    	if(button != null) button.setChecked(checked);
    }
    
    private void checkInternal(int id) {
        // don't even bother
        if (id != -1 && (id == mCheckedId)) {
            return;
        }
        int prevId = mCheckedId;
        
        setCheckedId(id);

        if (prevId != -1) {
            setCheckedStateForView(prevId, false);
        }
    }

    private void setCheckedId(int id) {
        mCheckedId = id;
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, mCheckedId);
        }
    }
    
    private void setCheckedStateForView(int id, boolean is) {
    	for(View child : getChildren()) { 
    		CompoundButton checkableView = (CompoundButton) child;
    		if(checkableView.getId() == id) {
    			checkableView.setChecked(is);
    			return ;
    		}
    	}
    }

	/**
     * <p>Register a callback to be invoked when the checked radio button
     * changes in this group.</p>
     *
     * @param listener the callback to call on checked state change
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }
	
	/**
     * <p>Interface definition for a callback to be invoked when the checked
     * radio button changed in this group.</p>
     */
    public interface OnCheckedChangeListener {
        /**
         * <p>Called when the checked radio button has changed. When the
         * selection is cleared, checkedId is -1.</p>
         *
         * @param group the group in which the checked radio button has changed
         * @param checkedId the unique identifier of the newly checked radio button
         */
        public void onCheckedChanged(RadioGroup group, int checkedId);
    }

}
