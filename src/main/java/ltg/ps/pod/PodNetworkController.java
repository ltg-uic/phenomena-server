/*
 * Created Oct 27, 2010
 */
package ltg.ps.pod;

import java.util.Observable;
import java.util.Observer;

import ltg.ps.abstract_components.NetworkController;
import ltg.ps.api.phenomena.Phenomena;
import ltg.ps.api.phenomena.PhenomenaWindow;
import ltg.ps.server.ConfFile;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;

/**
 * TODO Description
 *
 * @author Gugo
 */
public class PodNetworkController extends NetworkController implements Observer {
	
	//private PacketCollector pc = null;
	
	public PodNetworkController(Phenomena phenomenon, String password) {
		super(phenomenon.getInstanceName(), password);
		parser = new PodTranslator(phenomenon);
	}


	/**
	 * Creates an event listener for all packets. 
	 * This enables a particular phenomena to react upon the 
	 * connection/disconnection of a client to the server 
	 * (for instance by creating/destroying a new phenomena window) 
	 */
	public synchronized void listen() {
		connection.addPacketListener(new PacketListener() {
			@Override
			public void processPacket(Packet p) {
				parser.getCommand(p.toXML()).execute();
			}
		}, null);
	}


	@Override
	public void update(Observable o, Object arg) {
		for(PhenomenaWindow win : ((Phenomena) o).getWindows()) {
			String jid = win.getWindowId() + "@" + ConfFile.getProperty("XMPP_SERVER_DOMAIN");
			if (win.toXML() != null && !"".equals(win.toXML()))
				this.sendTo(jid, win.toXML());
		}
	}

}
