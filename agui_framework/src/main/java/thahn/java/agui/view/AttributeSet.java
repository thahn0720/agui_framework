package thahn.java.agui.view;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jdom2.Element;
import org.xml.sax.Attributes;

import thahn.java.agui.app.Context;
import thahn.java.agui.exception.WrongFormatException;
import thahn.java.agui.graphics.Color;
import thahn.java.agui.graphics.ColorDrawable;
import thahn.java.agui.graphics.Drawable;
import thahn.java.agui.res.ResourcesManager;
import thahn.java.agui.utils.AguiUtils;
import thahn.java.agui.utils.MyUtils;
import thahn.java.agui.widget.ViewName;

public class AttributeSet {

	private Context 													mContext;
	private HashMap<Integer, Attribute> 			mAttrs 		= new HashMap<>();

	public AttributeSet(Context c) {
		init(c);
	}

	/**
	 * for DOM
	 * 
	 * @param e
	 */
	public AttributeSet(Context c, Element e) {
		init(c);
		parseFromDOM(e);
	}

	/**
	 * for SAX
	 * 
	 * @param attributes
	 */
	public AttributeSet(Context c, Attributes attributes) {
		init(c);
		parseFromSAX(attributes);
	}

	private void init(Context c) {
		mContext = c;
	}

	private void parseFromSAX(Attributes attributes) {
		int lenght = attributes.getLength();
		for (int i = 0; i < lenght; ++i) {
			String value = attributes.getValue(i); // value : @+id/root,
													// localname : agui:id
			String[] local = attributes.getLocalName(i).split(":");
			int id = 0;
			if (local.length >= 2) {
				id = local[1].hashCode();
			} else {
				id = local[0].hashCode();
			}
			mAttrs.put(id, new Attribute(id, value, local[0]));
		}
	}

	private void parseFromDOM(Element e) {
		List<org.jdom2.Attribute> list = e.getAttributes();

		for (org.jdom2.Attribute temp : list) {
			mAttrs.put(temp.getName().hashCode(), new Attribute(temp.getName()
					.hashCode(), temp.getValue(), temp.getNamespace()
					.getPrefix()));
		}
	}

	public boolean hasValue(int i) {
		return mAttrs.containsKey(i);
	}

	public Attribute getAttributeValue(int i) {
		return mAttrs.get(i);
	}

	public String getAttributeValue(String namespace, String attrName) {
		Attribute attr = getAttributeValue(attrName.hashCode());
		if (attr != null) {
			return attr.getValue();
		}
		return null;
	}

	/**
	 * parameter에 type을 놔두고 id 인지 drawable인지 구분<br>
	 * attr "id"의 값을 gen로부터 구함<br>
	 * 
	 * @param i
	 * @param def
	 * @return
	 */
	public int getResourceId(int i, int def) {
		int value = def;
		Attribute a = mAttrs.get(i);
		if (a != null) {
			// resourcesid 는 "@+id/name"
			value = GenParser.getInstance().findId(
					AguiUtils.getPackageNameByNS(a.getValue()), a.getValue());// Integer.parseInt(a.getValue());
		}
		return value;
	}

	public int getLayoutId(int i, int def) {
		int value = def;
		Attribute a = mAttrs.get(i);
		if (a != null) {
			// resourcesid 는 "@+id/name"
			value = GenParser.getInstance().findLayoutId(
					AguiUtils.getPackageNameByNS(a.getValue()), a.getValue());// Integer.parseInt(a.getValue());
		}
		return value;
	}

	public int getAnimationId(int i, int def) {
		int value = def;
		Attribute a = mAttrs.get(i);
		if (a != null)
			value = GenParser.getInstance().findAnimId(
					AguiUtils.getPackageNameByNS(a.getValue()), a.getValue());// Integer.parseInt(a.getValue());
		return value;
	}

	public int getInt(int i, int def) {
		int value = def;
		Attribute a = mAttrs.get(i);
		if (a != null)
			value = Integer.parseInt(a.getValue());
		return value;
	}

	public int getDimensionPixelSize(int i, int def) {
		int value = def;
		Attribute a = mAttrs.get(i);
		if (a != null) {
			String temp = a.getValue();
			if (temp.contains("px")) {
				value = Integer.parseInt(temp.replace("px", ""));
			} else if (temp.contains("dip")) {
				value = Integer.parseInt(temp.replace("dip", ""));
			} else if (temp.contains("dp")) {
				value = Integer.parseInt(temp.replace("dp", ""));
			} else {
				value = Integer.parseInt(temp);
			}
		}
		return value;
	}

	public float getFloat(int i, float def) {
		float value = def;
		Attribute a = mAttrs.get(i);
		if (a != null)
			value = Float.parseFloat(a.getValue());
		return value;
	}

	public String getString(int i, String def) {
		String value = def;
		Attribute a = mAttrs.get(i);
		if (a != null)
			value = a.getValue();
		return value;
	}

	public int getStringHash(int i, int def) {
		int value = def;
		Attribute a = mAttrs.get(i);
		if (a != null)
			value = a.getValue().hashCode();
		return value;
	}

	public boolean getBoolean(int i, boolean def) {
		boolean value = def;
		Attribute a = mAttrs.get(i);
		if (a != null)
			value = Boolean.parseBoolean(a.getValue());
		return value;
	}

	public Attribute getAttrValueFromIdhash(int id) {
		return mAttrs.get(id);
	}

	public int getAnimationId(int i) {
		Attribute a = mAttrs.get(i);
		int value = -1;
		if (a != null) {
			String v = a.getValue();
			value = GenParser.getInstance().findAnimId(
					AguiUtils.getPackageNameByNS(v), v);
		}
		return value;
	}

	public String getAnimation(int i) {
		Attribute a = mAttrs.get(i);
		String value = null;
		if (a != null) {
			String v = a.getValue();
			value = mContext.getResources().getAnimation(GenParser.getInstance().findAnimId(AguiUtils.getPackageNameByNS(v), v));
		}
		return value;
	}

	public Drawable getDrawable(int i) {
		return getDrawable(i, -1);
	}

	public Drawable getDrawable(int i, int defaultType) {
		Drawable d = null;
		Attribute a = mAttrs.get(i);
		if (a != null) {
			String value = a.getValue();
			if (value.startsWith("#")) { // color
				int color = Color.parseColor(value);// getColorFromSharp(value);
				d = ColorDrawable.load(color);
			} else {
				int drawableId = GenParser.getInstance().findDrawable(
						AguiUtils.getPackageNameByNS(value), value);// String.valueOf(a.getValue());
				try {
					d = mContext.getResources().getDrawable(drawableId);// new Drawable(path);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else { // default
			if (defaultType == ViewName.WIDGET_BUTTON_CODE) {
				d = mContext.getResources().getDrawable(thahn.java.agui.R.drawable.button); // btn_nor, button
			} else if (defaultType == ViewName.WIDGET_SCROLLBAR_CODE) {
				d = mContext.getResources().getDrawable(thahn.java.agui.R.drawable.button); // btn_nor, button
			} else if (defaultType == ViewName.WIDGET_CHECK_BOX_CODE) {
				d = mContext.getResources().getDrawable(thahn.java.agui.R.drawable.check_box); // btn_nor, // button
			} else if (defaultType == ViewName.WIDGET_RADIO_BUTTON_CODE) {
				d = mContext.getResources().getDrawable(thahn.java.agui.R.drawable.radio_button); // btn_nor, // button
			} else if (defaultType == ViewName.WIDGET_SEEK_BAR_CODE) {
				d = mContext.getResources().getDrawable(thahn.java.agui.R.drawable.btn_nor); // btn_nor, button
			} else {
				d = mContext.getResources().getDrawable(defaultType); // btn_nor, button
			}
		}
		return d;
	}

	public int getColor(int i, int def) {
		int color = def;
		Attribute a = mAttrs.get(i);
		if (a != null) {
			String value = a.getValue();
			if (value.startsWith("#")) {
				color = Color.parseColor(value);// getColorFromSharp(value);//
			} else {
				throw new WrongFormatException(value
						+ " is not the color format");
			}
		}
		return color;
	}

	private int getColorFromSharp(String value) {
		int color = 0;
		String colorValue = value.substring(1);
		int hasAlpha = 0;
		int alpha = 255;
		if (colorValue.length() > 6) { // contains alpha
			alpha = Integer.parseInt(colorValue.substring(0, 2), 16);
			hasAlpha = 2;
		}
		int r = Integer.parseInt(colorValue.substring(0 + hasAlpha, 2 + hasAlpha), 16);
		int g = Integer.parseInt(colorValue.substring(2 + hasAlpha, 4 + hasAlpha), 16);
		int b = Integer.parseInt(colorValue.substring(4 + hasAlpha, 6 + hasAlpha), 16);
		color = ((alpha & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0);
		return color;
	}

	public int getIntFromEnum(int i, int def) {
		int ret = def;
		String[] value = new String[1];
		Attribute a = mAttrs.get(i);
		if (a != null) {
			value[0] = a.getValue();
			if (value[0].contains("|")) {
				value = value[0].replace("|", "/").split("/");
			}
			ret = 0;
			for (String temp : value) {
				String item = ResourcesManager.getInstance().getEnum(
						a.getAttrNameHash(), temp.hashCode());
				if (item == null) {
					return Integer.parseInt(temp); // gridview collumn autofit
													// or 1,2,3...
					// throw new WrongNameException("attribute value '" + item +
					// "' is wrong name.");
				}
				if (item.startsWith("0x")) {
					ret |= Integer.parseInt(item.substring(2), 16);
				} else if (item.startsWith("#")) {
					ret |= Integer.parseInt(item.substring(1), 16);
				} else {
					ret |= Integer.parseInt(item);
				}
			}
		}
		return ret;
	}

	public int getSize() {
		return mAttrs.size();
	}

	public void recycle() {
//		mAttrs = null;
	}

	public Attribute[] obtainRelatedAttr(String layout, int startPoint, int defaultType) {
		List<Attribute> rules = new ArrayList<Attribute>();
		try {
			for (int k = 0; k < 2; ++k) {
				Field[] fields = null;
				if (k == 0) {
					try {
						fields = thahn.java.agui.R.attr.class.getFields();
					} catch (Exception e) {
						// e.printStackTrace();
					}
				} else {
					try {
						fields = MyUtils.getProjectClass("R$attr").getFields();
					} catch (Exception e) {
						// e.printStackTrace();
					}
				}
				if (fields == null)
					continue;

				int i = 0;
				for (;;) {
					if (i == fields.length
							|| fields[i].getName().equals(layout)) {
						break;
					}
					i += fields[i].getInt(fields[i]) + 1;
				}

				if (i != fields.length) {
					int size = fields[i].getInt(fields[i]);
					for (int j = 1; j <= size; ++j) {
						int value = fields[i + j].getInt(fields[i + j]);
						Attribute attr = mAttrs.get(value);
						// attr.setDefaultValue();
						if (attr != null) {
							rules.add(attr);
						}
					}
					if (rules.size() > 0)
						break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rules.toArray(new Attribute[rules.size()]);
	}

	public int[] obtainType(int defaultType) {
		// int[] types = thahn.java.agui.R.styleable.View;
		// if(defaultType == AViewName.WIDGET_ATEXT_VIEW_CODE) {
		// thahn.java.agui.R.styleable.
		// }
		return null;
	}

	// FIXME : fixme
	public int[][] obtainDefaultValue(int defaultType) {
		int[][] defaults = thahn.java.agui.R.style.View;
		
		if(defaultType == ViewName.WIDGET_TEXT_VIEW_CODE) {
			defaults = thahn.java.agui.R.style.TextView;
		} else if (defaultType == ViewName.WIDGET_BUTTON_CODE) {
			defaults = thahn.java.agui.R.style.Button;
		} else if (defaultType == ViewName.WIDGET_EDIT_TEXT_CODE) {
			defaults = thahn.java.agui.R.style.EditText;
		} else if (defaultType == ViewName.WIDGET_PROGRESS_BAR_CODE) {
			defaults = thahn.java.agui.R.style.ProgressBar;	
		} else if (defaultType == ViewName.WIDGET_SEEK_BAR_CODE) {
			defaults = thahn.java.agui.R.style.SeekBar;	
		} else if (defaultType == ViewName.WIDGET_CHECK_BOX_CODE) {
			defaults = thahn.java.agui.R.style.CheckBox;	
		} 
		
		return defaults;
	}
}
