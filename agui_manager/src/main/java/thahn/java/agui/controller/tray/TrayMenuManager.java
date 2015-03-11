package thahn.java.agui.controller.tray;

import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import thahn.java.agui.ContextMenu;
import thahn.java.agui.app.Context;
import thahn.java.agui.app.Dialog;
import thahn.java.agui.app.Service;
import thahn.java.agui.controller.R;
import thahn.java.agui.utils.Log;
import thahn.java.agui.view.View;
import thahn.java.agui.view.View.OnClickListener;

public class TrayMenuManager {

	private static final String									MENU_SET_USER_INFOS		= 	"Set User Info";
	private static final String									MENU_MANAGE_APP 		= 	"Manage Agui App";
	private static final String									MENU_UPDATE_AGUI 		= 	"Update Agui";
	private static final String									MENU_INSTALL_APP 		= 	"Install Application";
	private static final String									MENU_INFORMATION 		= 	"Information";
	private static final String									MENU_EXIT		 		= 	"Exit";
	
	private static final int									DIALOG_INFORMATION 		= 	0;
	private static final int									DIALOG_USER_INFO 		= 	1;
	private static final int									DIALOG_INSTALL_APP 		= 	2;

	private Service 											mService;
	
	public void setTray(Context context, Service service) {
		mService = service ;
		
		ContextMenu menu = new ContextMenu("Agui Manager");
		addMenuItem(menu, MENU_SET_USER_INFOS);
		addMenuItem(menu, MENU_MANAGE_APP);
		addMenuItem(menu, MENU_UPDATE_AGUI);
		addMenuItem(menu, MENU_INSTALL_APP);
		addMenuItem(menu, MENU_INFORMATION);
		addMenuItem(menu, MENU_EXIT);
		mService.setTray(context, "Agui Manage Service", R.drawable.test, menu, null);
	}
	
	private void addMenuItem(ContextMenu menu, String actionCommand) {
		MenuItem userInfo = new MenuItem(actionCommand);
		userInfo.addActionListener(mActionListener);
		menu.add(userInfo);
	}
	
	private ActionListener mActionListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case MENU_SET_USER_INFOS:
				mService.showDialog(DIALOG_USER_INFO);
				break;
			case MENU_MANAGE_APP:
				break;
			case MENU_UPDATE_AGUI:
				break;
			case MENU_INSTALL_APP:
				mService.showDialog(DIALOG_INSTALL_APP);
				break;
			case MENU_INFORMATION:
				mService.showDialog(DIALOG_INFORMATION);
				break;
			case MENU_EXIT:
				break;
			}
		}
	};
	
	public Dialog onCreateDialog(int i) {
		
		switch (i) {
		case DIALOG_INFORMATION: {
			Dialog dialog = new Dialog(mService.getApplicationContext());
			dialog.setContentView(R.layout.dialog_information);
			return dialog;
		}
		case DIALOG_USER_INFO:{
			Dialog dialog = new Dialog(mService.getApplicationContext());
			dialog.setContentView(R.layout.dialog_user_info);
			dialog.findViewById(R.id.btn_sync).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Log.i("touch");
				}
			});
			return dialog;
		}
		case DIALOG_INSTALL_APP:{
			Dialog dialog = new Dialog(mService.getApplicationContext());
			dialog.setContentView(R.layout.dialog_install_app);
			dialog.findViewById(R.id.btn_browse).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Log.i("browse");
				}
			});
			dialog.findViewById(R.id.btn_install).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Log.i("install");
				}
			});
			return dialog;
		}
		}
		return null;
	}
}
