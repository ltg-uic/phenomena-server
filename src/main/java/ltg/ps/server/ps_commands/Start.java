/*
 * Created Oct 29, 2010
 */
package ltg.ps.server.ps_commands;

import ltg.ps.server.PSCommand;

import org.dom4j.Element;

/**
 * TODO Description
 *
 * @author Gugo
 */
public class Start extends PSCommand {

	private String id = null;

	
	@Override
	public void execute() {
		if(id!=null)
			ps.startPod(id);
	}

	
	@Override
	public void parse(Element xml) {
		id = xml.attributeValue("id");
	}

}
