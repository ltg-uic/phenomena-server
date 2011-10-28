/*
 * Created Oct 29, 2010
 */
package ltg.ps.server.ps_commands;

import ltg.ps.exceptions.PodNotFoundException;
import ltg.ps.server.PSCommand;

import org.dom4j.Element;

/**
 * TODO Description
 *
 * @author Gugo
 */
public class New extends PSCommand {
	private String id = null;
	private String pass = null;
	private String type = null;
	private String windows = null;
	private String config = null;

	@Override
	public void execute() {
		if (id!=null && pass!=null && type!= null) {
			ps.addPod(id, pass, type);
			if (windows!=null)
				try {
					ps.getPod(id).configureWindows(windows);
				} catch (PodNotFoundException e) {
					log.error("Impossible to configure windows in a pod that has not been even created!");
				}
			if (config!=null)
				try {
					ps.getPod(id).configure(config);
				} catch (PodNotFoundException e) {
					log.error("Impossible to configure a pod that has not been even created!");
				}
		}
	}

	
	@Override
	public void parse(Element xml) {
		Element w, c = null; 
		id = xml.attributeValue("id");
		pass = xml.attributeValue("pass");
		type = xml.attributeValue("type");
		w = xml.element("windows");
		if (w!=null) 
			windows = w.asXML();
		c = xml.element("config");
		if (c!=null) 
			config = c.asXML();
	}

}
