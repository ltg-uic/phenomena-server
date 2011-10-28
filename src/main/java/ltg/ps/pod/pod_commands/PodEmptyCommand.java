/*
 * Created Oct 29, 2010
 */
package ltg.ps.pod.pod_commands;

import org.dom4j.Element;

import ltg.ps.api.phenomena.Phenomena;
import ltg.ps.api.phenomena.PhenomenaCommand;
import ltg.ps.api.phenomena.PhenomenaWindow;

/**
 * TODO Description
 *
 * @author Gugo
 */
public class PodEmptyCommand extends PhenomenaCommand {

	
	public PodEmptyCommand(Phenomena target, PhenomenaWindow origin) {
		super(target, origin);
	}


	public void execute() {
	}

	
	public void parse(Element xml) {
	}

	
	public String toXML() {
		return "";
	}

}
