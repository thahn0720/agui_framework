package thahn.java.agui.controller.install;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import thahn.java.agui.AguiConstants;
import thahn.java.agui.app.Bundle;
import thahn.java.agui.app.Context;
import thahn.java.agui.app.Dialog;
import thahn.java.agui.app.Toast;
import thahn.java.agui.compress.ZipHelper;
import thahn.java.agui.controller.R;
import thahn.java.agui.swt.SwtHelper;
import thahn.java.agui.view.View;
import thahn.java.agui.widget.EditText;

/**
 *
 * @author thAhn
 * 
 */
public class InstallAppDialog extends Dialog {

	private EditText							mPathEdit;
	private File 								mInstallAppFile;
	
	public InstallAppDialog(Context context) {
		super(context);
	}

	public InstallAppDialog(Context context, int theme) {
		super(context, theme);
	}
	
	public InstallAppDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_install_app);
		mPathEdit = (EditText) findViewById(R.id.install_app_path);
		findViewById(R.id.btn_browse).setOnClickListener(mButtonListener);
		findViewById(R.id.btn_install).setOnClickListener(mButtonListener);
	}
	
	private View.OnClickListener mButtonListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_browse:
				FileDialog dialog = new org.eclipse.swt.widgets.FileDialog(SwtHelper.getShell(), SWT.OPEN);
				String path = dialog.open();
				if (path != null) {
					File file = new File(path);
					if (file.exists() && isAguiFile(file)) {
						mInstallAppFile = file;
						mPathEdit.setText(path);
					} else {
						Toast.makeText(getContext(), "path is wrong.", Toast.LENGTH_LONG).show();
					}
				} 
				break;
			case R.id.btn_install:
				String aguiHome = System.getenv(AguiConstants.ENV_AGUI_HOME);
				if (aguiHome != null) {
//					get package and make package folder then extract
					String outputPath = Paths.get(aguiHome, AguiConstants.PATH_DATA).toFile().getAbsolutePath();  
					ZipHelper zipHelper = new ZipHelper();
					try {
						zipHelper.extract(mInstallAppFile.getAbsolutePath(), outputPath);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					// FIXME : error : show dialog
				}
				break;
			}
		}
	};
	
	private boolean isAguiFile(File file) {
		boolean ret = false; 
		if (file.getAbsolutePath().endsWith(AguiConstants.AGUI_EXTENSION)) {
			ret = true;
		}
		return ret;
	}
}