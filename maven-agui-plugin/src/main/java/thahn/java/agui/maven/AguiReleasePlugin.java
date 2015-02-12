package thahn.java.agui.maven;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import thahn.java.agui.maven.AguiSdkLayoutHelper.Result;

/**
 * 
 * @author thAhn
 * @goal releaseAgui
 * @phase package
 */
public class AguiReleasePlugin extends AbstractMojo {

	public static final String TAG = "AguiReleasePlugin";

	/**
	 * @parameter expression="${basedir}"
	 */
	private File baseDir;
	
	/**
     * @parameter expression="${project.build.directory}"
     */
	private File outputDirectory;
	
	/**
	 * Directory containing the classes and resource files that should be
	 * packaged into the JAR.
     * @parameter expression="${project.build.outputDirectory}"
     */
	private File classesDirectory;

	/**
	 * Name of the generated JAR.
	 * @parameter expression="${project.build.finalName}"
	 */
	private String finalName;

	/**
	 * The Maven project.
	 * @parameter expression="${project}"
	 */
	private MavenProject project;

	/**
     * @parameter expression="${session}"
     */
	private MavenSession session;

	/**
	 * Path to the default MANIFEST file to use. It will be used if
	 * <code>useDefaultManifestFile</code> is set to <code>true</code>.
	 * @parameter expression="${project.build.outputDirectory}/META-INF/MANIFEST.MF"
	 * @since 2.2
	 */
	private File defaultManifestFile;

	/**
	 * Set this to <code>true</code> to enable the use of the
	 * <code>defaultManifestFile</code>.
	 * @parameter expression="${jar.useDefaultManifestFile}"
	 * @since 2.2
	 */
	private boolean useDefaultManifestFile;

	 /**
     * Classifier to add to the artifact generated. If given, the artifact will be attached.
     * If this is not given,it will merely be written to the output directory
     * according to the finalName.
     * @parameter expression="${release.version}"
     */
    private String classifier;
    
    /**
     * @parameter expression="${agui.framework.folder.name}"
     */
    private String aguiFrameworkFolderName;
    
    /**
     * @parameter expression="${agui.manager.folder.name}"
     */
    private String aguiManagerFolderName;
	
	/**
	 * @component
	 */
	private MavenProjectHelper projectHelper;

	private AguiSdkLayoutHelper aguiSdkLayoutHelper;
	
	/**
	 * Generates the JAR.
	 * 
	 * @todo Add license files in META-INF directory.
	 */
	public void execute() throws MojoExecutionException {
		log("Started releasing agui framework");
		// prepare
		File releaseTempDir = Paths.get(outputDirectory.getAbsolutePath(), "agui-release-temp").toFile();
		if (!releaseTempDir.exists() && releaseTempDir.mkdirs()) {
			error("can not make release temp dir : " + releaseTempDir.getAbsolutePath());
		}
		// move source jar for releasing
		List<File> moduleJars = getModuleJar(releaseTempDir);
		// make release layout
		aguiSdkLayoutHelper = new AguiSdkLayoutHelper(releaseTempDir.getAbsolutePath(), classifier, moduleJars);
		Result ret = aguiSdkLayoutHelper.makeLayout();
		if (ret.ret) {
			Result ret2 = aguiSdkLayoutHelper.compressToJar(aguiSdkLayoutHelper.getBaseSdkPath().getAbsolutePath()
					, Paths.get(outputDirectory.getAbsolutePath()
							, aguiSdkLayoutHelper.getBaseSdkPath().getName() + ".zip").toFile().getAbsolutePath());
			if (!ret2.ret) {
				error("can not compress agui sdk layout : " + ret.reason);
			}
		} else {
			error("can not make agui sdk layout : " + ret.reason);
		}
		log("Finished releasing agui framework");
	}
	
	private List<File> getModuleJar(File moveToThis) throws MojoExecutionException {
		List<File> moduleJars = new ArrayList<>();
		List<String> modules = project.getParent().getModel().getModules();
		for (String module : modules) {
			File jarRet = null;
			File moduleTargetPath = Paths.get(baseDir.getParent(), module, "target").toFile();
			if (moduleTargetPath.exists()) {
				for (File fileItem : moduleTargetPath.listFiles()) {
					if (fileItem.isFile()) {
						String name = fileItem.getName();
						String extension = name.substring(name.lastIndexOf(".") + 1);
						if (extension.equals("jar")) {
							if (jarRet == null) {
								jarRet = fileItem;
							} else {
								if (jarRet.length() < fileItem.length()) {
									jarRet = fileItem;
								}
							}
						}
					}
				}
			} else {
				error("module name and module folder name is not the same");
			}
			
			if (jarRet != null) {
				try {
					Path distPath = Paths.get(moveToThis.getAbsolutePath(), jarRet.getName());
					Files.copy(jarRet.toPath(), distPath 
							, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES });
					moduleJars.add(distPath.toFile());
				} catch (IOException e) {
					e.printStackTrace();
					error("can not copy " + jarRet.getAbsolutePath());
				}
			} else {
				error("jar does not exist in " + moduleTargetPath.getAbsolutePath());
			}
		}
		
		return moduleJars;
	}

	protected static File getJarFile(File basedir, String finalName, String classifier) {
		if (classifier == null) {
			classifier = "";
		} else if (classifier.trim().length() > 0 && !classifier.startsWith("-")) {
			classifier = "-" + classifier;
		}

		return new File(basedir, finalName + classifier + ".jar");
	}
	
	private void log(String message) {
		getLog().info(TAG + " : " + message);
	}
	
	private void error(String message) throws MojoExecutionException {
		error(message, null);
	}
	
	private void error(String message, Throwable t) throws MojoExecutionException {
		if (t != null) {
			getLog().error(TAG + " : " + message, t);
		} else {
			getLog().error(TAG + " : " + message);
		}
		throw new MojoExecutionException(message);
	}
}
