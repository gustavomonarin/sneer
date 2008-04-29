package sneer.bricks.deployer.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

import org.apache.commons.io.FilenameUtils;

import sneer.bricks.deployer.DeployerException;
import sneer.bricks.deployer.impl.filters.ImplFinder;
import sneer.bricks.deployer.impl.filters.InterfaceFinder;
import sneer.lego.Brick;
import sneer.lego.utils.JarBuilder;
import sneer.lego.utils.io.SimpleFilter;
import sneer.lego.utils.metaclass.MetaClass;

public class VirtualDirectory {

	private String _brickName;
	
	private File _root;

	private String _path;
	
	private List<File> _apiClassFiles = new ArrayList<File>();
	
	private List<File> _apiSourceFiles = new ArrayList<File>();
	
	private List<File> _implSourceFiles = new ArrayList<File>();
	
	private List<File> _implClassFiles;

	public VirtualDirectory(File root, String path) {
		_path = path;
		_root = root;
	}

	public File rootDirectory() {
		return _root;
	}
	
	public String brickName() {
		return _brickName;
	}

	public List<File> api() {
		if(_apiSourceFiles.size() > 0)
			return _apiSourceFiles;
		
		SimpleFilter walker = new InterfaceFinder(_root);
		_apiSourceFiles = walker.list(); 
		return _apiSourceFiles;
	}

	public List<File> impl() {
		if(_implSourceFiles.size() > 0)
			return _implSourceFiles;
		
		SimpleFilter walker = new ImplFinder(_root);
		_implSourceFiles = walker.list(); 
		return _implSourceFiles;
	}

	public void grabCompiledClasses(List<MetaClass> classFiles) {
		ClassLoader cl = classLoaderFromRootFolder();
		List<File> sourceFiles = api();
		for (File sourceFile : sourceFiles) {
			String className = toClassName(sourceFile);
			File classFile = findClassFile(classFiles, className);
			if(classFile != null) {
				_apiClassFiles.add(classFile);

				if(_brickName == null) 
					tryBrickName(cl, className);
			}
		}
	}

	private String toClassName(File sourceFile) {
		String className = _path + File.separator + sourceFile.getName();
		className = FilenameUtils.separatorsToUnix(className);
		className = className.replaceAll("/", ".");
		return className.substring(0, className.indexOf(".java"));
	}

	private File findClassFile(List<MetaClass> classFiles, String className) {
		for(MetaClass metaClass : classFiles) {
			if(className.equals(metaClass.getName()))
				return metaClass.classFile();
		}
		return null;
	}

	private void tryBrickName(ClassLoader cl, String className) {
		try {
			Class<?> c = cl.loadClass(className);
			if(Brick.class.isAssignableFrom(c))
				_brickName = className;
		} catch (ClassNotFoundException e) {
			throw new wheel.lang.exceptions.NotImplementedYet(e); // Implement Handle this exception.
		}
	}

	private ClassLoader classLoaderFromRootFolder() {
		try {
			return  new URLClassLoader(new URL[]{new URL("file://"+_root.getAbsolutePath()+"/")});
		} catch (MalformedURLException e) {
			throw new wheel.lang.exceptions.NotImplementedYet(e);
		}
	}

	public JarFile jarSrcApi() {
		return jar(_apiSourceFiles, "api-src");
	}

	public JarFile jarSrcImpl() {
		return jar(_implSourceFiles, "impl-src");
	}

	public JarFile jarBinaryApi() {
		return jar(_apiClassFiles, "api");
	}

	public JarFile jarBinaryImpl() {
		return jar(_implClassFiles, "impl");
	}

	public void setImplClasses(List<MetaClass> classFiles) {
		_implClassFiles = new ArrayList<File>();
		for (MetaClass metaClass : classFiles) {
			_implClassFiles.add(metaClass.classFile());
		}
	}

 	private JarFile jar(List<File> files, String role) {
		String brickName = brickName();
		String jarName = brickName + "-" + role;
		try {
			File tmp = File.createTempFile(jarName+"-", ".jar");
			JarBuilder builder = JarBuilder.builder(tmp.getAbsolutePath());

			//sneer meta
			String meta = sneerMeta(brickName, "1.0-SNAPSHOT", role);
			builder.add("sneer.meta", meta);

			for(File file : files) {
				String middle = File.separator;
				if(role.startsWith("impl")) {
					middle = File.separator + "impl" + File.separator;
				}
				String entryName = _path + middle + file.getName();
				builder.add(entryName, file);
			}
			return new JarFile(builder.close());
			
		} catch (IOException e) {
			throw new DeployerException("Error", e);
		}
	}

	private String sneerMeta(String brickName, String version, String role) {
		StringBuilder builder = new StringBuilder();
		builder.append("brick-name: ").append(brickName).append("\n");
		builder.append("brick-version: ").append(version).append("\n");
		builder.append("role: ").append(role).append("\n");
		return builder.toString();
	}
}
