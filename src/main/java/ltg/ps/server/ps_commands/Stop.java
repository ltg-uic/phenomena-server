/*
 * Created Oct 31, 2010
 */
package ltg.ps.server.ps_commands;

import org.dom4j.Element;

import ltg.ps.server.PSCommand;

/**
 * TODO Description
 *
 * @author Gugo
 */
public class Stop extends PSCommand {
	
	private String id = null;

	
	@Override
	public void execute() {
		if(id!=null)
			ps.stopPod(id);
	}


	@Override
	public void parse(Element xml) {
		id = xml.attributeValue("id");
	}


}
