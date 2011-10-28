/*
 * Created Jul 20, 2010
 */
package ltg.ps.pod;

import java.util.List;

import ltg.StringUtilities;
import ltg.ps.abstract_components.Translator;
import ltg.ps.api.Command;
import ltg.ps.api.phenomena.Phenomena;
import ltg.ps.api.phenomena.PhenomenaCommand;
import ltg.ps.api.phenomena.PhenomenaWindow;
import ltg.ps.exceptions.UnknownCommandException;
import ltg.ps.pod.pod_commands.PhenomenaCommandFactory;
import ltg.ps.pod.pod_commands.PodEmptyCommand;

import org.dom4j.Element;

/**
 * Translates commands for phenomena pods
 *
 * @author Gugo
 */
public class PodTranslator extends Translator {
	
	private Phenomena p = null;
	
	
	public PodTranslator(Phenomena p) {
		this.p = p;
	}


	public PhenomenaCommand getCommand(String xml) {
		xml = StringUtilities.toJava(xml);
		return (PhenomenaCommand) parse(xml);
	}


	@Override
	protected Command parseMessage(Element xml) {
		PhenomenaCommand pc = null;
		List<?> l = xml.element("body").elements();
		String[] id = xml.attributeValue("from").split("@", 2);
		// Checks that body contains proper XML and body is not null...
		if (id[0]!= null && id[0]!="" && 
				l != null && l.size() == 1 && xml.element("body").getText().isEmpty()) {
			Element e = (Element) l.get(0);
			PhenomenaWindow origin = null;
			for(PhenomenaWindow w: p.getWindows())
				if (w.getWindowId().equals(id[0])) {
					origin = w;
					break;
				}
			if (e != null && origin != null) {
				// return command
				try {
					pc =  PhenomenaCommandFactory.getCommand(e.getName(), p, origin);
					pc.parse(e);
					return pc;
				} catch (UnknownCommandException e1) {
					log.info("Received unknown message command: " + e.asXML());
				}
			}
		}
		return new PodEmptyCommand(p, null);
	}


	@Override
	protected Command parsePresence(Element xml) {
		PhenomenaCommand pc = null;
		//TODO... needs to be changed... need to incorporate the @ part
		String[] id = xml.attributeValue("from").split("@", 2);
		for(PhenomenaWindow w: p.getWindows()) {
			if(w.getWindowId().equals(id[0]))
				try {
					pc =  PhenomenaCommandFactory.getCommand(xml.getName(), p, w);
					pc.parse(xml);
					return pc;
				} catch (UnknownCommandException e1) {
					log.info("Received unknown presence command: " + xml.asXML());
				}
		}
		return new PodEmptyCommand(p, null);
	}


	@Override
	protected Command parseIQ(Element xml) {
		return new PodEmptyCommand(p, null);
	}

}
