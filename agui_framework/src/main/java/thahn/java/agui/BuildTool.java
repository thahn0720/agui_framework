package thahn.java.agui;

import java.io.File;
import java.nio.file.Paths;

/**
 *
 * @author thAhn
 * 
 */
public enum BuildTool {
	/**condition(reg exp), bin, resource */
	ECLIPSE_JAVA(".*/bin/", "/bin/", ""),
	MAVEN(".*/target/classes/", "/target/classes/", "/src/main/resources/"),
	RUNTIME("", "", ""),
	;

	private String condition;
	private String binPath;
	private String resPath;

	private BuildTool(String condition, String binPath, String resPath) {
		this.condition = condition;
		this.binPath = binPath;
		this.resPath = resPath;
	}
	
	public String getCondition() {
		return condition;
	}

	public String getBinPath() {
		return binPath;
	}

	public String getResPath() {
		return resPath;
	}
	
	/**
	 * 
	 * @param classpath contain specific path that is separated with other build tool <br>
	 * 			   ex) d:/project/bin/classes 
	 * @return
	 */
	public static Object[] getBuildToolByClasspath(String classpath) {
		String retPath = classpath;
		BuildTool ret = BuildTool.RUNTIME;
		BuildTool[] buildTools = BuildTool.values(); 
		for (BuildTool buildTool : buildTools) {
			if (classpath.matches(buildTool.getCondition())) {
				String binPath = buildTool.getBinPath();
				retPath = classpath.substring(0, classpath.length() - binPath.length());
				switch (buildTool) {
				case ECLIPSE_JAVA:
					ret = BuildTool.ECLIPSE_JAVA;
					break;
				case MAVEN:
					ret = BuildTool.MAVEN;
					break;
				}
			}
		}
		
		return new Object[] {retPath, ret};
	}
	
	/**
	 * 
	 * @param path project base path <br>
	 * 			   ex) d:/project/ 
	 * @return
	 */
	public static Object[] getBuildTool(String path) {
		String retPath = path;
		BuildTool ret = BuildTool.RUNTIME;
		BuildTool[] buildTools = BuildTool.values(); 
		for (BuildTool buildTool : buildTools) {
			File binDir = Paths.get(path, buildTool.getBinPath()).toAbsolutePath().toFile();
			if (binDir.exists()) {
				switch (buildTool) {
				case ECLIPSE_JAVA:
					ret = BuildTool.ECLIPSE_JAVA;
					break;
				case MAVEN:
					ret = BuildTool.MAVEN;
					break;
				}
			}
		}
		
		return new Object[] {retPath, ret};
	}
}
