package thahn.java.agui.controller;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import javax.swing.JOptionPane;

public class ExecuteOneProcessTest {

	private static File f;
	private static FileChannel channel;
	private static FileLock lock;

	protected static boolean isRunnable() {
		boolean runnable = false;
		try {
			String path = System.getProperty("user.home") + File.separator
					+ "OneProcessTest.lock";
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
			// 예외를 발생시켜 runnable의 값을 false로 유지시킵니다.
			if (lock == null) {
				channel.close();
				throw new Exception();
			}
			// Add shutdown hook to release lock when application shutdown
			HookShutdown shutdownHook = new HookShutdown();
			Runtime.getRuntime().addShutdownHook(shutdownHook);
			runnable = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return runnable;
	}

	// lock을 풀고 파일을 제거 합니다.
	public static void unlockFile() {
		try {
			if (lock != null) {
				lock.release();
				channel.close();
				f.delete();

				System.out.println("Shutdown & Unlock finish successfully.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 프로세스가 종료 될 때 실행 될 작업을 등록 할 클래스
	static class HookShutdown extends Thread {
		public void run() {
			unlockFile();
		}
	}

//	public static void main(String[] args) throws Exception {
//		if (!isRunnable()) {
//			JOptionPane
//					.showMessageDialog(
//							null,
//							"ExecuteOneProcessTest already has started. The program will be closed.",
//							"Error", JOptionPane.ERROR_MESSAGE);
//			return;
//		}
//		Thread.sleep(5000);
//	}
}