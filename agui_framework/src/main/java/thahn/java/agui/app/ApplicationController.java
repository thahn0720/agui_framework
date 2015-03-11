package thahn.java.agui.app;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.io.File;
import java.nio.file.Paths;

import thahn.java.agui.AguiConstants;
import thahn.java.agui.BuildTool;
import thahn.java.agui.Global;
import thahn.java.agui.app.controller.HandlerThread;
import thahn.java.agui.app.controller.Looper;
import thahn.java.agui.exception.NotExistException;
import thahn.java.agui.jmx.JmxConnectorHelper;
import thahn.java.agui.res.ManifestParser;
import thahn.java.agui.res.RMaker;
import thahn.java.agui.res.ResourcesManager;
import thahn.java.agui.settings.AguiSettings;
import thahn.java.agui.utils.AguiUtils;
import thahn.java.agui.utils.Log;
import thahn.java.agui.utils.MyUtils;

import com.google.common.base.Strings;

/**
 * 
 * @author thAhn
 *
 */
public class ApplicationController extends ContextThemeWrapper implements Window.Callback {
	
	private static final long serialVersionUID = -9172835460659506867L;
	
	private ThreadLocal<Integer>											mCurWinWidth	 = new ThreadLocal<Integer>();
	private ThreadLocal<Integer>											mCurWinHeight	 = new ThreadLocal<Integer>();
	
	public ApplicationController() {
		super();
		// FIXME : hardcoding
		String temp = "D:/agui-sdk-windows";//System.getenv(AguiConstants.ENV_AGUI_HOME);
		if (Strings.isNullOrEmpty(temp)) {
			throw new NotExistException("Env Variable : AGUI_HOME is not defined. " +
					"before executing agui app, AGUI_HOME should be defined like ANDROID_HOME." +
					"click the setAguiHome batch file in sdk tool's folder");
		}
		Global.aguiHomePath = temp + AguiConstants.PATH_DATA;
		//
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		Global.screenWidth = gd.getDisplayMode().getWidth();
		Global.screenHeight = gd.getDisplayMode().getHeight();
		ApplicationSetting.getInstance();
		setApplicationContext(this);
	}

	/**
	 * at first, once occurs
	 * @param projectPath
	 * @param fullPackageName
	 */
	public void create(String projectPath, String projectPackageName, String fullPackageName) {
		try {
			init(projectPath, projectPackageName);
			ActivityInfo info = ActivityManager.getInstance().getActivityInfo(fullPackageName); //ApplicationSetting.applicationInfo.width
			Intent intent = new Intent(null, MyUtils.getClass(projectPath, fullPackageName));
			intent.putIntExtra(Intent.EXTRA_POSITION_RELATIVE_SCREEN, WindowPosition.CENTER | WindowPosition.MID);
			createWindow("AGUI", info.width, info.height, intent);
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
	
	private void init(String projectPath, String projectPackageName) {
		// init resources
		ResourcesManager.getInstance();
		// init global constants
		Global.init();
		Global.corePath = MyUtils.getClassPath(thahn.java.agui.Main.class);
		Global.coreResBasePath = Global.corePath;
		String[] ends = new String[]{BuildTool.DEFAULT.getBinPath(), BuildTool.MAVEN.getBinPath()}; // ant, maven
		for (String end : ends) {
			if (Global.corePath.endsWith(end)) {
				Global.corePath = Global.corePath.substring(0, Global.corePath.length() - end.length());
				
				if (BuildTool.DEFAULT.getBinPath().equals(end)) {
					Global.coreResBasePath = Global.corePath;
					Global.buildTool = BuildTool.DEFAULT;
				} else if (BuildTool.MAVEN.getBinPath().equals(end)) {
					Global.coreResBasePath = Global.corePath + "/src/main/resources";
					Global.buildTool = BuildTool.MAVEN;
				}
			}
		}
		Global.projectPath = projectPath;
		Global.projectResBasePath = Global.projectPath;
		if (Global.buildTool == BuildTool.MAVEN) {
			Global.projectResBasePath = Paths.get(Global.projectResBasePath, BuildTool.MAVEN.getResPath()).toFile().getAbsolutePath();
		}
		Global.corePackageName = thahn.java.agui.Main.class.getPackage().getName();
		Global.projectPackageName = projectPackageName;
		// core res path		
		Global.coreDrawableImgPath = Global.coreResBasePath + "/res/drawable-hdpi/";
		Global.coreDrawablePath = Global.coreResBasePath + "/res/drawable/";
		Global.coreLayoutPath = Global.coreResBasePath + "/res/layout/"; 
		Global.coreValuesPath = Global.coreResBasePath + "/res/values/";
		Global.coreGenBasePath = Global.corePath;
		// project res path		
		Global.projectDrawableImgPath = Global.projectResBasePath + "/res/drawable-hdpi/";
		Global.projectDrawablePath = Global.projectResBasePath + "/res/drawable/";
		Global.projectLayoutPath = Global.projectResBasePath + "/res/layout/"; 
		Global.projectValuesPath = Global.projectResBasePath + "/res/values/";
		Global.projectGenBasePath = Global.projectPath;
		// agui project path
		Global.aguiProjectPath = Global.aguiHomePath + AguiConstants.DIR_SEPERATOR + Global.projectPackageName + AguiConstants.DIR_SEPERATOR;
		File projectFolder = new File(Global.aguiProjectPath);
		if (!projectFolder.exists()) projectFolder.mkdirs();
		
		boolean isRunnable = AguiUtils.isRunnable();
		if (isRunnable) {
			JmxConnectorHelper.getInstance().connectToAgui();
			JmxConnectorHelper.getInstance().setContext(this);
		} else {
			Log.i("AGUI OS did not start.");
		}
		 
		LooperManager.start();
		// make R  
		RMaker coreRMaker = new RMaker(true, Global.corePackageName, Global.corePath, Global.coreGenBasePath
				, Global.coreResBasePath, ResourcesManager.getInstance(), Global.corePackageName);
		coreRMaker.parse();
		RMaker projectRMaker = new RMaker(false, Global.corePackageName, Global.corePath, Global.projectGenBasePath
				, Global.projectResBasePath, ResourcesManager.getInstance(), Global.projectPackageName);
		projectRMaker.parse();
		RMaker.recycle();
		// parse from aguiManifest.xml 
		ManifestParser manifestParser = new ManifestParser(ResourcesManager.getInstance());
		manifestParser.parse(Global.projectPath);
		
		Log.i("application id : " + ApplicationSetting.applicationInfo.id);
		
		if (isRunnable) {
			JmxConnectorHelper.getInstance().registerApplicationInAgui(this, ApplicationSetting.applicationInfo);
		}
	}

	public void createWindow(String title, int width, int height, Intent intent) throws Exception {
		Log.i("created window : " + intent.getComponent().getClassName());
		mCurWinWidth.set(width);
		mCurWinHeight.set(height);
		Point position = WindowPosition.getWindowPosition(width, height, intent);
		
		Window window = new Window(title);
		window.setLocation(position);
		window.setSize(width, height);
		
		window.startActivityForResult(null, intent, 0);
		window.setCallback(this);
		
		WindowInfo windowInfo = new WindowInfo();
		windowInfo.name = title;
		windowInfo.window = window;
		WindowManager.getInstance().add(title, windowInfo);
	}
	
	private void applicationDestoy() {
		// terminate service looper. as service is background task, just terminate by user.
		if (ApplicationSetting.getInstance().isForceServiceTerminate()) {
			for (ServiceInfo serviceInfo : ServiceManager.getInstance().getServiceInfoList()) {
				if (serviceInfo.serviceLooper != null) {
					serviceInfo.service.stopSelf();
				}
			}
		}
		// Toast
		ToastManager.getInstance().stop();
		// asynctask thread factory & executor destroy
		AsyncTask.sExecutor.shutdown();
		// main looper
		LooperManager.stop();
		// shutdown 
		Runtime.getRuntime().exit(0);
	}

	@Override
	public void onWindowCreate() {
		
	}

	@Override
	public void onWindowDestroy(Window window) {
		// TODO : implements / terminate activity looper
		WindowManager.getInstance().destroyWindow(window);
		destroyNcheckApplicationChild();
	}
	
	@Override
	public void onActivityAddedInWindow(Activity activity) {
//		setApplicationContext(activity);
	}

	private void destroyNcheckApplicationChild() {
//		count window's count
		int windowCount = WindowManager.getInstance().getWindowList().size();
		if (windowCount <= 0) {
			Log.i("all window is finished. so, destroy application");
			applicationDestoy();
		}
	}
	
	@Override
	public int getWidth() {
		if (mCurWinWidth.get() == null) {
			return AguiSettings.getInstance().getDefaultActivityWidth();
		} 
		return mCurWinWidth.get();
	}

	@Override
	public int getHeight() {
		if (mCurWinHeight.get() == null) {
			return AguiSettings.getInstance().getDefaultActivityHeight();
		} 
		return mCurWinHeight.get();
	}

	//*****************************************************************************
	// TODO : 
	//*****************************************************************************
	@Override
	public Object getSystemService(String name) {
		return null;
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
	}

	@Override
	public void startActivityInNewWIndow(String windowName, Intent intent) {
		StartActivityInNewWindowTask task = new StartActivityInNewWindowTask(windowName, intent);
		task.start();
	}
	
	@Override
	public ComponentName startService(Intent intent) {
		Log.i("created service : " + intent.getComponent().getClassName());
		StartServiceTask task = new StartServiceTask(intent);
		task.start();
		return intent.getComponent();
	}

	//*****************************************************************************
	// StartActivityInNewWindowTask
	//*****************************************************************************
	/*package*/ class StartActivityInNewWindowTask extends HandlerThread {
	    Intent 										intent;
		String										windowName;
	    
		public StartActivityInNewWindowTask(String windowName, Intent intent) {
			super(intent.getComponent().getClassName());
			this.windowName = windowName;
			this.intent = intent;
		}

	    public void run() {
	    	try {
//	    		Looper.prepare();
//		        synchronized (this) {
//		            mLooper = Looper.myLooper();
//		            notifyAll();
//		        }
		        ActivityInfo info = ActivityManager.getInstance().getActivityInfo(intent.getComponent().getClassName());
				if (info == null) Log.t("a activity does not defined in manifest. : " + intent.getComponent().getClassName());
				
		        int width = intent.getIntExtra(Intent.EXTRA_WIDTH, -1);
		        width = width==-1?info.width:width;
		        int height = intent.getIntExtra(Intent.EXTRA_HEIGHT, -1);
		        height = height==-1?info.height:height;
		        //--------------------------------------------------
//		        Window window = WindowManager.getInstance().getWindowInfo(windowName).window; // 기존 윈도우에 추가
		        ApplicationController.this.createWindow(windowName , width , height , intent);
		        //--------------------------------------------------				
//				onLooperPrepared();
//				Looper.loop();
			} catch (RuntimeException e) {
				e.printStackTrace();
				throw new RuntimeException();
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
	}
	
	//*****************************************************************************
	// StartServiceTask
	//*****************************************************************************
	/*package*/ class StartServiceTask extends HandlerThread {
	    private Intent 										intent;

	    public StartServiceTask(Intent intent) {
			super(intent.getComponent().getClassName());
			this.intent = intent;
		}

	    public void run() {
	    	String serviceClass = intent.getComponent().getClassName();
	    	Log.i("start service : " + intent.getComponent().getClassName());
	    	try {
	    		Looper.prepare();
		        synchronized (this) {
		            mLooper = Looper.myLooper();
		            notifyAll();
		        }
		        //--------------------------------------------------
		        mCurWinWidth.set(AguiSettings.getInstance().getDefaultActivityWidth());
				mCurWinHeight.set(AguiSettings.getInstance().getDefaultActivityHeight());
				
		        ServiceInfo info = ServiceManager.getInstance().getServiceInfo(serviceClass);
		        if (info == null) Log.t("a service does not defined in manifest.");
		        if (info.serviceLooper != null) {
		        	info.service.onStartCommand(intent, Service.START_STICKY, 0);
		        	return ;
		        } 
		        info.serviceLooper = this;
		        Service service = intent.createService();
		        info.service = service;
		        info.service.mServiceInfo = info;
		        service.onCreate();
				service.onStartCommand(intent, Service.START_STICKY, (int) getId());
				//--------------------------------------------------				
				onLooperPrepared();
				Looper.loop();
			} catch (RuntimeException e) {
				e.printStackTrace();
				throw new RuntimeException();
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
	}
}
