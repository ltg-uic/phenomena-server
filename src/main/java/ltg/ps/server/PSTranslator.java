/*
 * Created Oct 27, 2010
 */
package ltg.ps.server;

import java.util.List;

import ltg.StringUtilities;
import ltg.ps.abstract_components.Translator;
import ltg.ps.api.Command;
import ltg.ps.exceptions.UnknownCommandException;
import ltg.ps.server.ps_commands.PSEmptyCommand;

import org.dom4j.Element;

/**
 * TODO Description
 *
 * @author Gugo
 */
public class PSTranslator extends Translator {
	
	private String COMMAND_PACKAGE = null;
	
	public PSTranslator() {
		COMMAND_PACKAGE = "ltg.ps.server.ps_commands.";
	}


	public PSCommand getCommand(String xml) {
		xml = StringUtilities.toJava(xml);
		return (PSCommand) parse(xml);
	}

	
	protected Command parseMessage(Element root) {
		if(root.element("body")!=null) {
			List<?> l = root.element("body").elements();
			// Body contains proper XML
			if (l != null && l.size() == 1 && root.element("body").getText().isEmpty()) {
				Element e = (Element) l.get(0);
				if (e != null) {
					// return command
					try {
						return findCommand(e.getName(), e);
					} catch (UnknownCommandException e1) {
						return new PSEmptyCommand();
					}
				}
			}
		}
		//log.warn("Unknown message received, ignoring...");
		return new PSEmptyCommand();
	}


	protected Command parsePresence(Element root) {
		return new PSEmptyCommand();
	}


	protected Command parseIQ(Element root) {
		return new PSEmptyCommand();
	}
	
	
	private Command findCommand(String name, Element xml) throws UnknownCommandException {
		// Get proper command class
		Class<Command> cmdType = getClassFromName(name);
		if (cmdType != null) {
			Command cmd = null;
			try {
				cmd = cmdType.newInstance();
				cmd.parse(xml);
				return cmd;
			} catch (InstantiationException e) {
				log.error("InstantiationException", e);
			} catch (IllegalAccessException e) {
				log.error("IllegalAccessException", e);
			}
		}
		// Unknown command (default)
		log.warn("Unknown command received: " + xml.asXML());
		throw new UnknownCommandException();
	}


	@SuppressWarnings("unchecked")
	private  Class<Command> getClassFromName(String name) {
		Class<Command> c = null;
		name = StringUtilities.capitalize(name);
		if (COMMAND_PACKAGE != null)
			name = COMMAND_PACKAGE + name; 
		else
			return c;
		try {
			c = (Class<Command>) Class.forName(name);
		} catch (ClassNotFoundException e) {
		}
		return c;
	}
	
}
