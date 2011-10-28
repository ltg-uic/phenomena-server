/*
 * Created Feb 19, 2011
 */
package ltg.ps.pod.pod_commands;

import ltg.ps.api.phenomena.Phenomena;
import ltg.ps.api.phenomena.PhenomenaCommand;
import ltg.ps.api.phenomena.PhenomenaWindow;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Description
 *
 * @author Gugo
 */
public class Presence extends PhenomenaCommand {
	
	private  Logger log = LoggerFactory.getLogger(this.getClass());
	private String type = "available";
	private String from = null;

	
	/**
	 * @param target
	 */
	public Presence(Phenomena target, PhenomenaWindow origin) {
		super(target, origin);
	}

	
	/* (non-Javadoc)
	 * @see ltg.ps.api.Command#execute()
	 */
	@Override
	public void execute() {
		if(type.equals("available")) {
			log.info("Window " + from + " CONNECTED");
			for(PhenomenaWindow pw : target.getWindows())
				if(pw.getWindowId().equals(from)) {
					target.notifyObservers();
				}
		}
		if(type.equals("unavailable"))
			log.info("Window " + from + " DISCONNECTED");
	}

	
	/* (non-Javadoc)
	 * @see ltg.ps.api.Command#parse(org.dom4j.Element)
	 */
	@Override
	public void parse(Element e) {
		//TODO... needs to be changed... need to incorporate the @ part
		String[] id = e.attributeValue("from").split("@", 2);
		from = id[0];
		if(e.attributeValue("type")!=null)
			type = e.attributeValue("type");	
	}
}
