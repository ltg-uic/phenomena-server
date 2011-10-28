/*
 * Created Jul 20, 2010
 */
package ltg.ps.pod;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ltg.ps.api.phenomena.Phenomena;
import ltg.ps.exceptions.UnknownPhenomenaException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Description
 *
 * @author Gugo
 */
public class PhenomenaFactory {
	
	private static Logger log = LoggerFactory.getLogger(PhenomenaFactory.class);
	private static Map<String,Class<Phenomena>> availablePhenomena = 
		new HashMap<String, Class<Phenomena>>();
	
	
	private PhenomenaFactory() {
	}


	/**
	 * Returns all the phenomena that are available for instantiation.
	 *
	 * @return
	 */
	public static List<String> getAvailablePhenomena() {
		return new ArrayList<String>(availablePhenomena.keySet());
	}
	
	
	public static String printAvailablePhenomena() {
		String out = "";
		for(String p: availablePhenomena.keySet())
			out = out + p + ", ";
		if (out.equals(""))
			return "none.";
		char s[] = out.toCharArray();
		s[out.length()-2] = '.';
		return String.valueOf(s);
	}
	
	
	/**
	 * TODO Description
	 *
	 * @param phenomenaType
	 * @param id
	 * @return
	 * @throws UnknownPhenomenaException
	 */
	public static Phenomena getPhenomena(String phenomenaType, String id) throws UnknownPhenomenaException {
		if (availablePhenomena == null) {
			log.error("There has been an error loading classes. This shouldn't happen...");
			throw new UnknownPhenomenaException();
		}
		if (availablePhenomena.containsKey(phenomenaType))	
			return newInstance(availablePhenomena.get(phenomenaType), id);
		throw new UnknownPhenomenaException();
	}
	
	
	private static Phenomena newInstance(Class<Phenomena> phenomenaType, String id) {
		Constructor<Phenomena> c = null;
		try {
			c = phenomenaType.getConstructor(new Class[]{String.class});
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
				"instance of " + phenomenaType.getCanonicalName());
	}		
	
	
	/**
	 * TODO Description
	 * @param phenomena 
	 *
	 */
	public static void addPhenomena(String phenomenaType, Class<Phenomena> phenomena) { 
		availablePhenomena.put(phenomenaType, phenomena);
	}

	
}
