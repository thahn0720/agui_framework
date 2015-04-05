package thahn.java.agui.controller.install;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import thahn.java.agui.AguiConstants;
import thahn.java.agui.Global;
import thahn.java.agui.app.Bundle;
import thahn.java.agui.app.Context;
import thahn.java.agui.app.Dialog;
import thahn.java.agui.app.Toast;
import thahn.java.agui.compress.ZipHelper;
import thahn.java.agui.controller.R;
import thahn.java.agui.res.ManifestParser;
import thahn.java.agui.res.ManifestParser.ManifestInfo;
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
					// get package and make package folder then extract
					try {
						ZipFile aguiFile = new ZipFile(mInstallAppFile);
						Enumeration<? extends ZipEntry> entries = aguiFile.entries();
						while (entries.hasMoreElements()) {
							ZipEntry zipEntry = (ZipEntry) entries.nextElement();
							if (AguiConstants.AGUI_MANIFEST_NAME.equals(zipEntry.getName())) {
								// parse manifest
								InputStream is = aguiFile.getInputStream(zipEntry);
								ManifestParser mfParser = new ManifestParser(null);
								mfParser.parseHeader(is);
								ManifestInfo manifestInfo = mfParser.getManifestInfo();
								// extract
								String outputPath = Paths.get(aguiHome, AguiConstants.PATH_DATA, manifestInfo.packageName)
										.toFile().getAbsolutePath();  
								new File(outputPath).mkdirs();
								ZipHelper zipHelper = new ZipHelper();
								zipHelper.extract(mInstallAppFile.getAbsolutePath(), outputPath);
								// make exe in windows
								String sdkAbsolutePath = Paths.get(Global.aguiSdkPath, AguiConstants.PATH_PLATFORMS
										, AguiConstants.SDK_DIR_NAME_PREFIX + manifestInfo.versionCode
										, AguiConstants.RELEASE_SDK_JAR_NAME).toFile().getAbsolutePath();
								String exeContents = String.format("java -classpath %s -jar %s %s %s"
										, outputPath, sdkAbsolutePath, outputPath
										, manifestInfo.packageName);
								File bat = new File(outputPath, Global.os.getExeFileName());
								bat.createNewFile();
								FileWriter writer = new FileWriter(bat);
								writer.write(exeContents);
								writer.close();
								// FIXME : show success dialog
								break;
							}
						}
						// FIXME : error : show dialog : msg -> AguiManifest.xml does not exist
					} catch (ZipException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					// FIXME : error : show dialog : msg -> invalid
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
