package thahn.java.agui.maven;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import thahn.java.agui.maven.exception.AguiReleasePluginException;

/**
 *
 * @author thAhn
 * 
 */
public class AguiSdkLayoutHelper {

	private final String 										relativePath;
	private final String 										version;
	private final List<File>									moduleJarFiles;
	private final File	 										baseSdkPath;
	
	public AguiSdkLayoutHelper(String relativePath, String version, List<File> moduleJarFiles) {
		this.relativePath = relativePath;
		this.version = version;
		this.moduleJarFiles = moduleJarFiles;
		this.baseSdkPath = Paths.get(relativePath, "agui-sdk-windows-" + version).toFile();
	}
	
	public Result makeLayout() {
		Result ret = new Result();
		try {
			ret.ret = true;
			// base
			makeDirectory(baseSdkPath);
			// os
			File osPath = Paths.get(baseSdkPath.getAbsolutePath(), "os").toFile();
			makeDirectory(osPath);
			File dataPath = Paths.get(osPath.getAbsolutePath(), "data").toFile();
			makeDirectory(dataPath);
			// platforms
			File platformsPath = Paths.get(baseSdkPath.getAbsolutePath(), "platforms").toFile();
			makeDirectory(platformsPath);
			Path aguiLibPath = Paths.get(platformsPath.getAbsolutePath(), "agui-" + version);
			makeDirectory(aguiLibPath.toFile());
			// platforms/lib : move modules jar to lib 
			for (File moduleJar : moduleJarFiles) {
				Path aguiLibFile = Paths.get(aguiLibPath.toFile().getAbsolutePath(), moduleJar.getName());
				Files.copy(Paths.get(moduleJar.getAbsolutePath()), aguiLibFile
						, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES });
			}
			// tools
			File toolsPath = Paths.get(baseSdkPath.getAbsolutePath(), "tools").toFile();
			makeDirectory(toolsPath);
			File libPath = Paths.get(toolsPath.getAbsolutePath(), "lib").toFile();
			makeDirectory(libPath);
		} catch (Exception e) {
			e.printStackTrace();
			ret.ret = false;
			ret.e = e;
			ret.reason = e.getMessage();
		}
		
		return ret;
	}
	
	private void makeDirectory(File file) throws Exception {
		raiseException(!file.exists() && !file.mkdirs(), "cannot make dir : " + file.getAbsolutePath());
	}
	
	private void raiseException(boolean is, String message) throws Exception {
		if (is) {
	       throw new AguiReleasePluginException(message);
		}
	}

	/**
	 * 
	 * @param outputPath directory
	 * @return
	 */
	public Result compressToJar(String outputPath) {
		Result ret = new Result();
		try {
			ret.ret = true;
			int buffer = 2048;
			CheckedOutputStream checksum = new CheckedOutputStream(new FileOutputStream(outputPath)
											, new Adler32());
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(checksum));
			// out.setMethod(ZipOutputStream.DEFLATED);
			byte data[] = new byte[buffer];
			// get a list of files from current directory
			BufferedInputStream origin = null;
			File sourceFile = baseSdkPath;
			String files[] = sourceFile.list();
			for (int i = 0; i < files.length; i++) {
				FileInputStream fi = new FileInputStream(files[i]);
				origin = new BufferedInputStream(fi, buffer);
				ZipEntry entry = new ZipEntry(files[i]);
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, buffer)) != -1) {
					out.write(data, 0, count);
				}
				origin.close();
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			ret.ret = false;
			ret.e = e;
			ret.reason = e.getMessage();
		}
		
		return ret;
	}
	
	public class Result {
		
		/*package*/ boolean ret;
		/*package*/ String reason;
		/*package*/ Exception e;
	}
}
