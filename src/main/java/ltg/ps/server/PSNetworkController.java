/*
 * Created Oct 27, 2010
 */
package ltg.ps.server;

import ltg.ps.abstract_components.NetworkController;

import org.jivesoftware.smack.PacketCollector;

/**
 * TODO Description
 *
 * @author Gugo
 */
public class PSNetworkController extends NetworkController {
	
	// Packet collector
	private PacketCollector pc = null;
	
	
	public PSNetworkController(String id, String password) {
		super(id, password);
		parser = new PSTranslator();
	}
	
	
	/**
	 * Start to wait for commands. As a command comes it is parsed and then executed
	 */
	public synchronized void listen() {
		pc = connection.createPacketCollector(null);
		while(!Thread.currentThread().isInterrupted())
				parser.getCommand(pc.nextResult().toXML()).execute();
		this.disconnect();
	}	
	
}
