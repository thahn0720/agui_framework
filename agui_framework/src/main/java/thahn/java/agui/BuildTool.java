package thahn.java.agui;

/**
 *
 * @author thAhn
 * 
 */
public enum BuildTool {
	DEFAULT("/bin/", ""),
	MAVEN("/target/classes/", "/src/main/resources/")
	;

	private String binPath;
	private String resPath;

	private BuildTool(String binPath, String resPath) {
		this.binPath = binPath;
		this.resPath = resPath;
	}
	
	public String getBinPath() {
		return binPath;
	}

	public String getResPath() {
		return resPath;
	}
}
