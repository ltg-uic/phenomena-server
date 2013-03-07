/*
 * Created Dec 3, 2010
 */
package ltg.ps.server;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import ltg.class_loader.JarClassLoader;
import ltg.ps.api.phenomena.Phenomena;
import ltg.ps.api.phenomena.PhenomenaCommand;
import ltg.ps.api.phenomena.PhenomenaWindow;
import ltg.ps.pod.PhenomenaFactory;
import ltg.ps.pod.PhenomenaWindowFactory;
import ltg.ps.pod.pod_commands.PhenomenaCommandFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Description
 *
 * @author Gugo
 */
public class PSClassLoader {

	private Logger log = LoggerFactory.getLogger(PhenomenaFactory.class);
	private String jarDir = null;

	
	public PSClassLoader() {
	}


	/**
	 * Loads all classes from jar files in the phenomena directory.
	 * The method processes a jar at the time and progressively adds classes to the factories 
	 */
	public void loadAllPhenomena(String jar_dir) {
		this.jarDir = jar_dir;
		File[] jars = findJars();
		if (jars==null || jars.length==0)
			return;
		for (int i = 0; i<jars.length; i++)
			loadJarClasses(jars[i]);
	}
	
	
	public void loadPhenomena(String jar) {
		File jarFile = new File(jar);
		loadJarClasses(jarFile);
	}
	
	
	private  File[] findJars() {
		File[] jars = null;
		jars = new File(jarDir).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) { 
				return Pattern.matches(".+\\.jar", name);
			}
		});
		return jars;
	}

	
	/**
	 * Loads all the classes in a particular jar into the classLoader and
	 * then into the factory classes.
	 *
	 * @param jar the Jar currently being loaded.
	 */
	private void loadJarClasses(File jar) {
		if (!jar.exists()) {
			log.error("Impossible to load jar file \"" + jar.getName() + "\"");
			return;
		}
		List<Class<?>> jarClasses = null;
		try {
			jarClasses = loadJar(jar);
		} catch (ClassNotFoundException e) {
			log.error("Impossible to load the jar file \"" + jar.getName() + "\"");
			return;
		}
		if(verifyJar(jarClasses))
			loadJarClassesIntoFactories(jarClasses); 
	}


	private  List<Class<?>> loadJar(File jar) throws ClassNotFoundException {
		JarClassLoader jarLoader = new JarClassLoader (jar.getAbsolutePath());
		Enumeration<JarEntry> entries;
		try {
			entries = new JarFile(jar).entries();
		} catch (IOException e) {
			log.error("Impossible to read the file. Check your permissions");
			throw new ClassNotFoundException();
		}
		String className = null;
		List<Class<?>> loadedClasses = new ArrayList<Class<?>>();
		while(entries.hasMoreElements()) {
			className = getClassName(entries.nextElement().getName());
			try {
				if(className!= null) {
					loadedClasses.add(jarLoader.loadClass(className, true));
				}
			} catch (ClassNotFoundException e) {
				log.error("Unable to find the class!");
				throw e;
			} 
		}
		return loadedClasses;
	}


	private  boolean verifyJar(List<Class<?>> jarClasses) {
		// Verify there is one and only one phenomena per jar
		// TODO more checks need to be performed on the structure!
		int phenCount=0;
		for(Class<?> c: jarClasses) 
			if(Phenomena.class.isAssignableFrom(c))
				phenCount++;
		if (phenCount==1)
			return true;
		return false;
	}
	
	
	@SuppressWarnings("unchecked")
	private void loadJarClassesIntoFactories(List<Class<?>> jarClasses) {
		// Save the path to commands package
		String phenomena = null;
		String commandPackage = null;
		for(Class<?> c: jarClasses) 
			if(Phenomena.class.isAssignableFrom(c)) 
				phenomena = c.getSimpleName();
		for(Class<?> c: jarClasses)
			if (PhenomenaCommand.class.isAssignableFrom(c)) {
				commandPackage  = getPackageName(c.getCanonicalName());
				break;
			}
		PhenomenaCommandFactory.addCommandPackage(phenomena, commandPackage);
		// Load classes into factories
		for(Class<?> c: jarClasses) 
			if(Phenomena.class.isAssignableFrom(c)) {
				PhenomenaFactory.addPhenomena(c.getSimpleName(), (Class<Phenomena>) c);
			} else if (PhenomenaCommand.class.isAssignableFrom(c)) {
				PhenomenaCommandFactory.addCommand(c.getCanonicalName(), (Class<PhenomenaCommand>) c);
			} else if (PhenomenaWindow.class.isAssignableFrom(c)) {
				PhenomenaWindowFactory.addWindow(c.getCanonicalName(), (Class<PhenomenaWindow>) c);
			}
	}
	
	
	private String getPackageName(String canonicalName) {
		return canonicalName.substring(0, canonicalName.lastIndexOf('.')+1);
	}


	private  String getClassName(String name) {
		if (name.contains(".class")) {
			return name.replaceAll(File.separator, ".").replaceAll(".class", "");
		}
		return null;
	}


//	public void printLoadedClasses() {
//		String m = "";
//		for(String s: PhenomenaFactory.getAvailablePhenomena())
//			m += " " + s;
//		log.info("Loaded phenomena: " + m);
//		log.info("-->Commands");
//		for(String s: PhenomenaCommandFactory.getAvailableCommands())
//			log.info("---->" + s);
//		log.info("-->Windows");
//		for(String s: PhenomenaWindowFactory.getAvailableWindows())
//			log.info("---->" + s);
//	}


}
