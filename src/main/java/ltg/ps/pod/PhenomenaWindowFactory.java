/*
 * Created Dec 3, 2010
 */
package ltg.ps.pod;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ltg.ps.api.phenomena.PhenomenaWindow;
import ltg.ps.exceptions.UnknownWindowException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Description
 *
 * @author Gugo
 */
public class PhenomenaWindowFactory {
	
	private static Logger log = LoggerFactory.getLogger(PhenomenaWindowFactory.class);
	private static Map<String,Class<PhenomenaWindow>> availableWindows = 
		new HashMap<String, Class<PhenomenaWindow>>();
	
	
	private PhenomenaWindowFactory() {
	}
	
	
	public static PhenomenaWindow getWindow(String type, String id) throws UnknownWindowException {
		if (availableWindows == null) {
			log.error("There has been an error loading classes. This shouldn't happen...");
			throw new UnknownWindowException();
		}
		if (availableWindows.containsKey(type))	
			return newInstance(availableWindows.get(type), id);
		throw new UnknownWindowException();
	}


	private static PhenomenaWindow newInstance(Class<PhenomenaWindow> type, String id) {
		Constructor<PhenomenaWindow> c = null;
		try {
			c = type.getConstructor(new Class[]{String.class});
		} catch (SecurityException e) {
			log.error("Security exception: impossible to load the constructor.", e);
		} catch (NoSuchMethodException e) {
			log.error("Could not find phenomena constructor. " +
					"(This shouldn't happen and it's very weired!", e);
		}
		if (c!= null) {
			try {
				return c.newInstance(id);
			} catch (IllegalArgumentException e) {
				log.error("Wrong parameters for the constructor.", e);
			} catch (InstantiationException e) {
				log.error("Impossible to instantiate the phenomena.", e);
			} catch (IllegalAccessException e) {
				log.error("Security exception: Illegal access.", e);
			} catch (InvocationTargetException e) {
				log.error("InvocationTargetException.", e);
			}
		}
		throw new RuntimeException("Impossible to create an " +
				"instance of " + type.getCanonicalName());
	}
	
	public  static void addWindow(String name, Class<PhenomenaWindow> command) { 
		availableWindows.put(name, command);
	}
	
	
	public static List<String> getAvailableWindows() {
		return new ArrayList<String>(availableWindows.keySet());
	}

}
