/*
 * Created Oct 29, 2010
 */
package ltg.ps.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ltg.ps.PhenomenaServer;
import ltg.ps.api.Command;

/**
 * TODO Description
 *
 * @author Gugo
 */
public abstract class PSCommand implements Command {
	
	protected Logger log = LoggerFactory.getLogger(this.getClass());
	
	protected PhenomenaServer ps = PhenomenaServer.getInstance();
	

}
