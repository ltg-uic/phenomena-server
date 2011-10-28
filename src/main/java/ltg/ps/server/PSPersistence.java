/*
 * Created Nov 15, 2010
 */
package ltg.ps.server;

import java.util.List;

import ltg.ps.PhenomenaServer;
import ltg.ps.abstract_components.Persistence;
import ltg.ps.exceptions.PodNotFoundException;
import ltg.ps.pod.PhenomenaPod;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * TODO Description
 *
 * @author Gugo
 */
public class PSPersistence extends Persistence {

	private PhenomenaServer ps = PhenomenaServer.getInstance();

	/**
	 * @param fileName
	 */
	public PSPersistence(String fileName) {
		super(fileName);
	}


	/* (non-Javadoc)
	 * @see ltg.ps.Persistence#save()
	 */
	@Override
	public void save() {
		ps = PhenomenaServer.getInstance();
		doc = DocumentHelper.createDocument();
		doc.addElement(fileName);
		for(PhenomenaPod p : ps.getInstantiatedPods()) {
			doc.getRootElement().addElement("phenomenaPod")
				.addAttribute("id", p.getId())
				.addAttribute("pass", p.getPass())
				.addAttribute("type", p.getType())
				.addAttribute("running", String.valueOf(p.isRunning()));
		}
		writeFile();
	}


	/* (non-Javadoc)
	 * @see ltg.ps.Persistence#restore()
	 */
	@Override
	public void restore() {
		ps = PhenomenaServer.getInstance();
		doc = DocumentHelper.createDocument();
		readFile();
		@SuppressWarnings("unchecked")
		List<Element> phenomena = doc.getRootElement().elements();
		if (phenomena==null || phenomena.isEmpty())
			return;
		for(Element e : phenomena) {
			ps.addPod(e.attributeValue("id"), e.attributeValue("pass"), e.attributeValue("type"));
			try {
				ps.getPod(e.attributeValue("id")).restore();
				if(Boolean.valueOf(e.attributeValue("running")))
					ps.startPod(e.attributeValue("id"));
			} catch (PodNotFoundException except) {
				log.error("Impossible to find pod!");
			}
		}
	}
}
