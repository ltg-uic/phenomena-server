/*
 * Created Oct 27, 2010
 */
package ltg.ps.abstract_components;

import ltg.ps.api.Command;
import ltg.ps.server.ps_commands.PSEmptyCommand;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * TODO Description
 *
 * @author Gugo
 */
public abstract class Translator {
	
	protected Logger log = LoggerFactory.getLogger(getClass());
	
	
	
	public abstract Command getCommand(String xml);
	
	protected abstract Command parseMessage(Element e);
	
	protected abstract Command parsePresence(Element e);
	
	protected abstract Command parseIQ(Element e);
	
	
	/**
	 * This is the crucial function that parses the command
	 * TODO Description
	 *
	 * @param xml
	 * @return
	 * @throws DocumentException 
	 */
	protected Command parse(String xml) {
		Element root = null;
		try {
			root = DocumentHelper.parseText(xml).getRootElement();
		} catch (DocumentException e) {
			log.warn("Unable to parse XML " + xml);
			return new PSEmptyCommand();
		} 
		if (root.getName().equals("message")) {
			return parseMessage(root); 
		} 
		if (root.getName().equals("presence")) {
			return parsePresence(root);
		} 
		if (root.getName().equals("iq")) {
			return parseIQ(root);
		}  
		// This will never happen! An XMPP packet is always either a message,
		// a presence or an IQ!!!
		log.error("The received packet doesn't comply to the XMPP standard!!!");
		return new PSEmptyCommand();
	}

}
