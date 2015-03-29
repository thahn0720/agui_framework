package thahn.java.agui;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

public class DynamicClassLoader extends ClassLoader {

	private String repositoryPath;
	
	public DynamicClassLoader(ClassLoader parent) {
		super(parent);
	}
	
	public String getRepositoryPath() {
		return repositoryPath;
	}

	public void setRepositoryPath(String repositoryPath) {
		this.repositoryPath = repositoryPath;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Class<?> findClass(final String name) throws ClassNotFoundException {

		AccessControlContext acc = AccessController.getContext();

		try {
			return (Class) AccessController.doPrivileged(
					new PrivilegedExceptionAction() {
						public Object run() throws ClassNotFoundException {
							FileInputStream fi = null;
							try {
								String path = name.replace('.', '/');
								fi = new FileInputStream(repositoryPath + path + ".class");
								ByteArrayOutputStream baos = new ByteArrayOutputStream();
								byte[] buffer = new byte[8192]; // a big chunk
								int read;
								while ((read = fi.read(buffer)) > 0)
									baos.write(buffer, 0, read);
								byte[] classBytes = baos.toByteArray();

								return defineClass(name, classBytes, 0, classBytes.length);
							} catch (Exception e) {
								throw new ClassNotFoundException(name);
							}
						}
					}, acc);
		} catch (java.security.PrivilegedActionException pae) {
			return super.findClass(name);
		}
	}
}
