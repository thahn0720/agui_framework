package thahn.java.agui;

import java.awt.PopupMenu;

public class ContextMenu extends PopupMenu {
								//JPopupMenu {

	public ContextMenu() {
		super();
	}

	public ContextMenu(String label) {
		super(label);
	}
	
	public interface ContextMenuInfo {
		
	}
}
