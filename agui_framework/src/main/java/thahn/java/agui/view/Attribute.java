package thahn.java.agui.view;

import thahn.java.agui.res.ResourcesManager;
import thahn.java.agui.utils.AguiUtils;

public class Attribute {

	private int attrNameHash;
	private String value;
	private String ns;
	private String defaultValue;

	public Attribute(int attrNameHash, String value, String ns) {
		this.attrNameHash = attrNameHash;
		this.ns = ns;
		// @agui:string/
		// @string/
		// @integer/
		if(value.startsWith("@agui:")) {
			String valueName = value.split("/")[1]; 
			if(value.startsWith("@agui:string")) {
				int id = GenParser.getInstance().findString(AguiUtils.getPackageNameByNS(value), valueName);
				this.value = ResourcesManager.getInstance().getString(id);
			} else if(value.startsWith("@agui:bool")) {
				int id = GenParser.getInstance().findBool(AguiUtils.getPackageNameByNS(value), valueName);
				this.value = String.valueOf(ResourcesManager.getInstance().getBoolean(id));
			} else if(value.startsWith("@agui:anim")) {
				this.value = value;
			} else if(value.startsWith("@agui:id")) {
				this.value = value;
			} else {
				this.value = value;
			}
		} else if(value.startsWith("@+")) {
			this.value = value;
		} else if(value.startsWith("@")) {
			if(value.contains("/")) {
				String valueName = value.split("/")[1]; 
				if(value.startsWith("@string")) {
					int id = GenParser.getInstance().findString(AguiUtils.getPackageNameByNS(value), valueName);
					this.value = ResourcesManager.getInstance().getString(id);
				} else if(value.startsWith("@bool")) {
					int id = GenParser.getInstance().findBool(AguiUtils.getPackageNameByNS(value), valueName);
					this.value = String.valueOf(ResourcesManager.getInstance().getBoolean(id));
				} else if(value.startsWith("@anim")) {
					this.value = value;
				} else if(value.startsWith("@drawable")) { 
					this.value = value;
				} else if(value.startsWith("@dimen")) { 
					int id = GenParser.getInstance().findDimension(AguiUtils.getPackageNameByNS(value), valueName);
					this.value = String.valueOf(ResourcesManager.getInstance().getDimension(id));
				} else {
					this.value = value;
				}
			} else {
				if(value.equals("@null")) {
					this.value = "#00000000";
				}
			}
		} else {
			this.value = value;
		}
	}

	public int getAttrNameHash() {
		return attrNameHash;
	}
	
	public String getValue() {
		return value;
	}

	public String getNs() {
		return ns;
	}

	public int getDefaultInt() {
		return Integer.parseInt(defaultValue);
	}
	
	public String getDefaultString() {
		return defaultValue;
	}

	void setDefaultValue(String value) {
		this.defaultValue = defaultValue;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Attribute [attrNameHash=").append(attrNameHash)
				.append(", value=").append(value).append(", ns=").append(ns)
				.append(", defaultValue=").append(defaultValue).append("]");
		return builder.toString();
	}
}
