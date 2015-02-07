package thahn.java.agui.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import thahn.java.agui.Global;
import thahn.java.agui.res.RBase;
import thahn.java.agui.res.RMaker;


public class AguiUtils {

	private static File f;
	private static FileChannel channel;
	private static FileLock lock;

	public static boolean isRunnable() {
		boolean runnable = true;
		try {
			String path = Global.aguiSdkPath + File.separator + "Agui.lock";
			f = new File(path);
			// 파일이 존재하면 삭제를 시도합니다. 다른 JVM에서 파일의 Lock을 획득하고 있다면 파일은 지워지지
			// 않습니다.
			if (f.exists()) {
				if(f.delete()) runnable = false; 
			} else {
				runnable = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return runnable;
	}
	
	public static boolean tryLock() {
		boolean runnable = true;
		try {
			String path = Global.aguiSdkPath + File.separator + "Agui.lock";
			f = new File(path);
			// 파일이 존재하면 삭제를 시도합니다. 다른 JVM에서 파일의 Lock을 획득하고 있다면 파일은 지워지지
			// 않습니다.
			if (f.exists()) {
				f.delete();
			}
			// 파일을 생성하여 채널 객체를 얻습니다.
			channel = new RandomAccessFile(f, "rw").getChannel();
			// lock 을 시도합니다.
			lock = channel.tryLock();
			// lock이 null이라는 것은 다른 JVM에서 이미 파일의 lock을 획득했다는 것입니다. 따라서 채널을 종료하고
			// runnable의 값을 false로 유지시킵니다.
			if (lock == null) {
				channel.close();
			} else {
				// Add shutdown hook to release lock when application shutdown
				runnable = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return runnable;
	}
	
	public static void registerUnlockOnTerminate() {
		HookShutdown shutdownHook = new HookShutdown();
		Runtime.getRuntime().addShutdownHook(shutdownHook);
	}
	
	// lock을 풀고 파일을 제거 합니다.
	private static void unlockFile() {
		try {
			if (lock != null) {
				lock.release();
				channel.close();
				f.delete();
				Runtime.getRuntime().exec("cmd /c taskkill /F /IM rmiregistry.exe");
				System.out.println("Shutdown & Unlock finish successfully.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 프로세스가 종료 될 때 실행 될 작업을 등록 할 클래스
	private static class HookShutdown extends Thread {
		public void run() {
			unlockFile();
		}
	}
	
	public static boolean isAguiNS(String resourceValue) {
		if(resourceValue.startsWith("@agui:")) {
			return true;
		} else { 
			return false;
		}
	}
	
	public static boolean isAguiNS(int resourceId) {
		if(resourceId < RMaker.PROJECT_R_START_INDEX) {
			return true;
		} else { 
			return false;
		}
	}
	
	public static String getPackageNameByNS(String resourceValue) {
		return isAguiNS(resourceValue)?Global.corePackageName:Global.projectPackageName;
	}
	
	public static String getPackageNameById(int id) {
		return isAguiNS(id)?Global.corePackageName:Global.projectPackageName;
	}
}