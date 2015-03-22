package thahn.java.agui.compress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipHelper {
	
	/**
	 * 
	 * @param dirName target directory
	 * @param nameZipFile source zip file
	 * @throws IOException
	 */
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
	
	public void extract(String filePath, String outputPath) throws IOException {
        ZipInputStream zis = new ZipInputStream(new FileInputStream(filePath));
        ZipEntry ze = zis.getNextEntry();
        while (ze != null){
            String entryName = ze.getName();
            File outputFile = Paths.get(outputPath, entryName).toFile();
            outputFile.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(outputFile);
            int len;
            byte buffer[] = new byte[1024];
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();   
            ze = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
	}
}