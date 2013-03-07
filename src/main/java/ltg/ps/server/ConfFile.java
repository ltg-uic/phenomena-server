/*
 * Created Nov 23, 2010
 */
package ltg.ps.server;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import ltg.ps.exceptions.PropertyNotFoundException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Description
 *
 * @author Gugo
 */
public class ConfFile {
	 
	private static Map<String, String> properties = new HashMap<String, String>();
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private File configFile = null;
	 

	public ConfFile(String configFile) {
		if (configFile==null) {
			log.error("Undefined configuration file! Terminating...");
			Thread.currentThread().interrupt();
		} else {
			this.configFile = new File(configFile);
			checkConfigFile();
		}
	}
	
	
	public ConfFile() {
	}
	
	
	public void setDefaultProperties(String jid, String password) {
		String[] s = jid.split("@", 2);
		properties.put("XMPP_SERVER_PORT", "5222");
		properties.put("DEFAULT_RESOURCE_ID", "phenomenaServer");
		properties.put("XMPP_SERVER_URL", s[1]);
		properties.put("XMPP_SERVER_DOMAIN", s[1]);
		properties.put("DEFAULT_PS_XMPP_USERNAME", s[0]);
		properties.put("DEFAULT_PS_XMPP_PASSWORD", password);
		properties.put("DB_LOCATION", "src/main/db");
		// Save the db location as a system property
		storeDbLocation();
	}

	
	private void checkConfigFile() {
		if(!configFile.exists()) {
			log.error("Undefined configuration file! Terminating...");
			Thread.currentThread().interrupt();
		}
	}


	public boolean parse() {
		Document doc = null;
		SAXReader r = new SAXReader();
		try {
			if(configFile!=null)
				doc = r.read(configFile);
			else
				return false;
		} catch (DocumentException e) {
			log.error("Error parsing the configuration file. Terminating.");
			return false;
		}
		@SuppressWarnings("unchecked")
		List<Element> elems = doc.getRootElement().elements("entry");
		if (elems.isEmpty())
			return false;
		for(Element e: elems)
			properties.put(e.attributeValue("key"), e.getTextTrim());
		// Save the db location as a system property
		storeDbLocation();
		return true;
	}
	
	
	private void storeDbLocation() {
		Properties p = new Properties(System.getProperties());
		p.setProperty("DB_LOCATION", getProperty("DB_LOCATION"));
		System.setProperties(p);
	}


	public static String getProperty(String name) {
		String p = properties.get(name);
		if(p == null)
			throw new PropertyNotFoundException();
		return p; 
	}

}
