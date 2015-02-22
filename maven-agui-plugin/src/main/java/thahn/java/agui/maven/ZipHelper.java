package thahn.java.agui.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipHelper {
	
	public void compressZipDir(String dirName, String nameZipFile) throws IOException {
		ZipOutputStream zip = null;
		FileOutputStream fW = null;
		fW = new FileOutputStream(nameZipFile);
		zip = new ZipOutputStream(fW);
		addFolderToZip("", dirName, zip);
		zip.close();
		fW.close();
	}

	private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws IOException {
		File folder = new File(srcFolder);
		if (folder.list().length == 0) {
			addFileToZip(path, srcFolder, zip, true);
		} else {
			for (String fileName : folder.list()) {
				if (path.equals("")) {
					addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip, false);
				} else {
					addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip, false);
				}
			}
		}
	}

	private void addFileToZip(String path, String srcFile, ZipOutputStream zip, boolean flag) throws IOException {
		File folder = new File(srcFile);
		if (flag) {
			zip.putNextEntry(new ZipEntry(path + "/" + folder.getName() + "/"));
		} else {
			if (folder.isDirectory()) {
				addFolderToZip(path, srcFile, zip);
			} else {
				byte[] buf = new byte[1024];
				int len;
				FileInputStream in = new FileInputStream(srcFile);
				zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
				while ((len = in.read(buf)) > 0) {
					zip.write(buf, 0, len);
				}
			}
		}
	}
	
	public boolean extract(String srcZip, String targetPath, OnCondition listener) throws IOException {
		ZipFile in = new ZipFile(srcZip);
	    Enumeration<? extends ZipEntry> entries = in.entries();
	    while (entries.hasMoreElements()) {
	    	ZipEntry entry = entries.nextElement();
	    	String zipEntryPath = entry.getName();
	    	if (listener.onCondition(zipEntryPath)) {
	    		zipEntryPath = "/" + zipEntryPath;
				File resFile = Paths.get(targetPath, zipEntryPath).toFile();
				if (entry.isDirectory()) {
					if (!resFile.exists() && !resFile.mkdir()) {
//						Log.t(TAG, "can not make new Res Directory : " + resFile.getAbsolutePath());
						return false;
					} 
				} else {
					Files.copy(in.getInputStream(entry),
							resFile.toPath(), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
				}
	    	}
		}
	    in.close();
	    return true;
	}
	
	public interface OnCondition {
		boolean onCondition(String zipentry);
	}
}