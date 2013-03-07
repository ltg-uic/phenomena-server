/*
 * Created Jul 20, 2010
 */
package ltg.ps;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ltg.ps.exceptions.PodNotFoundException;
import ltg.ps.exceptions.UnknownPhenomenaException;
import ltg.ps.pod.PhenomenaFactory;
import ltg.ps.pod.PhenomenaPod;
import ltg.ps.server.ConfFile;
import ltg.ps.server.PSClassLoader;
import ltg.ps.server.PSNetworkController;
import ltg.ps.server.PSPersistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TODO Description
 *
 * @author Gugo
 */
public class PhenomenaServer {
	// Phenomena pods
	private Map<String, PhenomenaPod> phenomena = new HashMap<String, PhenomenaPod>();
	// Components
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private ConfFile config= null;
	private PSNetworkController net = null;
	private PSPersistence db = null; 


	/**
	 * Private constructor prevents instantiation from other classes.
	 */
	private PhenomenaServer() {
		// Don't write ANY instructions here! 
		// (unless they DON'T use the configuration file for something) 
	}


	/**
	 * SingletonHolder is loaded on the first execution of <code>Singleton.getInstance()</code> 
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	private static class SingletonHolder { 
		private static final PhenomenaServer INSTANCE = new PhenomenaServer();
	}


	public void start(String configFile) {
		config = new ConfFile(configFile);
		if(!config.parse())
			System.exit(1);
		new PSClassLoader().loadAllPhenomena(ConfFile.getProperty("PHENOMENA_LOCATION"));
		log.info("Supported phenomena: " + PhenomenaFactory.printAvailablePhenomena());
		db = new PSPersistence(this.getClass().getSimpleName());
		db.restore();
		net = new PSNetworkController(ConfFile.getProperty("DEFAULT_PS_XMPP_USERNAME"),
				ConfFile.getProperty("DEFAULT_PS_XMPP_PASSWORD"));
		if (!net.connect()) 
			Thread.currentThread().interrupt();
		if(!Thread.currentThread().isInterrupted()) {
			log.info("Phenomena server STARTED");
			net.listen();
		}
	}
	
	
	public void start(String phenomenaJarFile, ConfFile configuration) {
		config = configuration;
		new PSClassLoader().loadPhenomena(phenomenaJarFile);
		log.info("Supported phenomena: " + PhenomenaFactory.printAvailablePhenomena());
		db = new PSPersistence(this.getClass().getSimpleName());
		db.restore();
		net = new PSNetworkController(ConfFile.getProperty("DEFAULT_PS_XMPP_USERNAME"),
				ConfFile.getProperty("DEFAULT_PS_XMPP_PASSWORD"));
		if (!net.connect()) 
			Thread.currentThread().interrupt();
		if(!Thread.currentThread().isInterrupted()) {
			log.info("Phenomena server STARTED");
			net.listen();
		}
	}


	public static PhenomenaServer getInstance() {
		return SingletonHolder.INSTANCE;
	}


	public synchronized boolean addPod(String id, String pass, String phenomenaType) {
		PhenomenaPod p = null;
		try {
			p = new PhenomenaPod(id, pass, phenomenaType);
		} catch (UnknownPhenomenaException e) {
			log.error("Impossible to create a new phenomenaPod of type " + phenomenaType + ". " +
					"Phenomena is unknown");
			return false;
		}
		if (phenomena.containsKey(id)) {
			log.error("The phenomenaPod with id " + id + " has already been instantiated!");
			return false;
		}
		phenomena.put(id,p);
		db.save();
		log.info("Created new pod " + id + " of type " + phenomenaType);
		return true;
	}
	
	
	public synchronized boolean deletePod(String id) {
		PhenomenaPod removedPod = phenomena.remove(id); 
		if(removedPod == null) {
			log.error("Tried to remove a non-existing pod.");
			return false;
		}
		removedPod.cleanup();
		db.save();
		log.info("Removed pod " + id);
		return true;
	}


	public synchronized boolean startPod(String id) {
		try {
			this.getPod(id).start();
		} catch (PodNotFoundException e) {
			log.error("Impossible to start " + id + ". Pod has probably not been created yet.");
			return false;
		}
		db.save();
		return true;
	}


	public synchronized boolean stopPod(String id) {
		try {
			this.getPod(id).stop();
		} catch (PodNotFoundException e) {
			log.error("Impossible to stop " + id + ". Pod has probably not been created yet.");
			return false;
		}
		db.save();
		return true;
	}


	public synchronized PhenomenaPod getPod(String id) throws PodNotFoundException {
		PhenomenaPod p = phenomena.get(id);
		if (p!= null) 
			return p;
		throw new PodNotFoundException();
	}


	public synchronized List<PhenomenaPod> getInstantiatedPods() {
		return new ArrayList<PhenomenaPod>(phenomena.values());
	}

	
	public static void main(String[] args) {
		String configFile = null;
		if (args.length == 1)
			 configFile = args[0];
		PhenomenaServer.getInstance().start(configFile);
	}

}
