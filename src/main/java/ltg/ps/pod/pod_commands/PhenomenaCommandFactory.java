/*
 * Created Dec 3, 2010
 */
package ltg.ps.pod.pod_commands;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ltg.StringUtilities;
import ltg.ps.api.phenomena.Phenomena;
import ltg.ps.api.phenomena.PhenomenaCommand;
import ltg.ps.api.phenomena.PhenomenaWindow;
import ltg.ps.exceptions.UnknownCommandException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Description
 *
 * @author Gugo
 */
public class PhenomenaCommandFactory {
	
	private static String POD_COMMANDS_PACKAGE = "ltg.ps.pod.pod_commands.";
	private static Logger log = LoggerFactory.getLogger(PhenomenaCommandFactory.class);
	private static Map<String,Class<PhenomenaCommand>> availableCommands = 
		new HashMap<String, Class<PhenomenaCommand>>();
	private static Map<String,String> commandPackages = new HashMap<String, String>();
	
	
	
	private PhenomenaCommandFactory() {
	}

	

	public static PhenomenaCommand getCommand(String name, Phenomena target, PhenomenaWindow origin) throws UnknownCommandException {
		name = StringUtilities.capitalize(name);
		// Try the find the command in the default package
		Class<PhenomenaCommand> cmdType = getClassFromName(name);
		if (cmdType != null) 
			return newInstance(cmdType, target, origin);
		// Try to find the command within the loaded ones
		if (availableCommands == null) {
			log.error("There has been an error loading classes. This shouldn't happen...");
			throw new UnknownCommandException();
		}
		name = createCanonicalName(name, target.getClass().getSimpleName());
		if (availableCommands.containsKey(name))	
			return newInstance(availableCommands.get(name), target, origin);
		throw new UnknownCommandException();
	}

	
	private static String createCanonicalName(String name, String phenType) {
		if (commandPackages.containsKey(phenType))
				return commandPackages.get(phenType) + name;
		return name;
	}



	public  static void addCommand(String name, Class<PhenomenaCommand> command) { 
		availableCommands.put(name, command);
	}
	
	
	public  static void addCommandPackage(String phenomena, String commandPackage) { 
		commandPackages.put(phenomena, commandPackage);
	}
	
	
	public static List<String> getAvailableCommands() {
		return new ArrayList<String>(availableCommands.keySet());
	}
	
	
	@SuppressWarnings("unchecked")
	private  static Class<PhenomenaCommand> getClassFromName(String name) {
		Class<PhenomenaCommand> c = null;
		if (POD_COMMANDS_PACKAGE != null)
			name = POD_COMMANDS_PACKAGE + name; 
		else
			return c;
		try {
			c = (Class<PhenomenaCommand>) Class.forName(name);
		} catch (ClassNotFoundException e) {
		}
		return c;
	}


	private static PhenomenaCommand newInstance(Class<PhenomenaCommand> name, Phenomena target, PhenomenaWindow origin) {
		Constructor<PhenomenaCommand> c = null;
		try {
			c = name.getConstructor(new Class[]{Phenomena.class, PhenomenaWindow.class});
		} catch (SecurityException e) {
			log.error("Security exception: impossible to load the constructor.", e);
		} catch (NoSuchMethodException e) {
			log.error("Could not find phenomena constructor. " +
					"(This shouldn't happen and it's very weired!", e);
		}
		if (c!= null) {
			try {
				return c.newInstance(target, origin);
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
				"instance of " + name.getCanonicalName());
	}
}
