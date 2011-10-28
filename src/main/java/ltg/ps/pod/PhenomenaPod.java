/*
 * Created Jan 30, 2010
 */
package ltg.ps.pod;


import ltg.ps.api.phenomena.Phenomena;
import ltg.ps.api.phenomena.PhenomenaWindow;
import ltg.ps.exceptions.UnknownPhenomenaException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Main class used to start a particular Phenomenon Server pod.
 * This class is a phenomena container that once started connects to the net... bla bla
 *
 * @author Gugo
 */
public class PhenomenaPod {
	private Logger log = LoggerFactory.getLogger(PhenomenaPod.class);
	private PodNetworkController net = null;
	private Phenomena phenomenon = null;

	
	
	/**
	 * Default constructor. Uses the phenomena default to initialize this instance.
	 * @param id
	 * @param pass
	 * @param phenomenaType
	 * @throws UnknownPhenomenaException 
	 */
	public PhenomenaPod(String id, String pass, String phenomenaType) throws UnknownPhenomenaException {
		phenomenon = PhenomenaFactory.getPhenomena(phenomenaType, id);
		net = new PodNetworkController(phenomenon, pass);
	}
	
	
	
	/**
	 * Configure constructor. Uses XML to configure the phenomena.
	 * @param id
	 * @param pass
	 * @param phenomenaType
	 * @param configXML
	 * @throws UnknownPhenomenaException 
	 */
	public PhenomenaPod(String id, String pass, String phenomenaType, String configXML) throws UnknownPhenomenaException {
		this(id, pass, phenomenaType);
		configure(configXML);
	}



	/**
	 * Starts the phenomena pod.
	 */
	public synchronized void start() {
		if(net.connect()) {
			// Successful connection
			phenomenon.start();
			addObservers();
			net.listen();
			log.info("PhenomenaPod " + phenomenon.getInstanceName() + " started!");
		}
	}


	/**
	 * Stops the phenomena pod.
	 */
	public synchronized void stop() {
		// Disconnects from the XMPP server
		if(net.disconnect()) {
			deleteObservers();
			// Stops the phenomena
			phenomenon.stop();
			log.info("PhenomenaPod stopped");
		}
	}

	
	synchronized  public void configureWindows(String configXML) {
		phenomenon.configureWindows(configXML);
	}
	
	
	synchronized  public void configure(String configXML) {
		phenomenon.configure(configXML);
	}
	
	
	synchronized public void restore() {
		phenomenon.restore();
	}
	
	
	synchronized public void cleanup() {
		phenomenon.cleanup();
	}
	
	
	public String getId() {
		return phenomenon.getInstanceName();
	}
	
	
	public String getPass() {
		return net.getPass();
	}
	
	
	public String getType() {
		return phenomenon.getClass().getSimpleName();
	}


	public boolean isRunning() {
		return phenomenon.isRunning();
	}
	
	
	private void addObservers() {
		phenomenon.addObserver(net);
		for(PhenomenaWindow w : phenomenon.getWindows())
			phenomenon.addObserver(w);
	}
	
	
	private void deleteObservers() {
		phenomenon.deleteObserver(net);
		for(PhenomenaWindow w : phenomenon.getWindows())
			phenomenon.deleteObserver(w);
	}



	
}