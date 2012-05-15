/*
 * Created May 14, 2012
 */
package ltg.ps.server.ps_commands;

import java.util.ArrayList;
import java.util.List;

import ltg.ps.pod.PhenomenaFactory;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.commands.LocalCommand;
import org.jivesoftware.smackx.packet.DataForm;

/**
 * TODO Description
 *
 * @author Gugo
 */
public class AvailablePhenomena extends LocalCommand {

	/**
	 * 
	 */
	public AvailablePhenomena() {
	}

	/* (non-Javadoc)
	 * @see org.jivesoftware.smackx.commands.LocalCommand#hasPermission(java.lang.String)
	 */
	@Override
	public boolean hasPermission(String arg0) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.jivesoftware.smackx.commands.LocalCommand#isLastStage()
	 */
	@Override
	public boolean isLastStage() {
		// One stage command
		return true;
	}

	/* (non-Javadoc)
	 * @see org.jivesoftware.smackx.commands.AdHocCommand#cancel()
	 */
	@Override
	public void cancel() throws XMPPException {
		// blank, because we are awesome...
	}

	/* (non-Javadoc)
	 * @see org.jivesoftware.smackx.commands.AdHocCommand#complete(org.jivesoftware.smackx.Form)
	 */
	@Override
	public void complete(Form form) throws XMPPException {
		this.setForm(form);
	}

	/* (non-Javadoc)
	 * @see org.jivesoftware.smackx.commands.AdHocCommand#execute()
	 */
	@Override
	public void execute() throws XMPPException {
		DataForm df = new DataForm(Form.TYPE_RESULT);
		// Reported node
		List<FormField> rdf = new ArrayList<FormField>();
		FormField ff = new FormField("phen-type");
		ff.setType(FormField.TYPE_FIXED);
		ff.setLabel("Phenomena name");
		rdf.add(ff);
		df.setReportedData(new DataForm.ReportedData(rdf));
		// Items 
		for (String pn : PhenomenaFactory.getAvailablePhenomena()) {
			rdf = new ArrayList<FormField>();
			ff = new FormField("phen-type");
			ff.addValue(pn);
			rdf.add(ff);
			df.addItem(new DataForm.Item(rdf));
		}
		Form f = new Form(df);
		this.complete(f);
	}

	/* (non-Javadoc)
	 * @see org.jivesoftware.smackx.commands.AdHocCommand#next(org.jivesoftware.smackx.Form)
	 */
	@Override
	public void next(Form arg0) throws XMPPException {
		// empty, one stage command
	}

	/* (non-Javadoc)
	 * @see org.jivesoftware.smackx.commands.AdHocCommand#prev()
	 */
	@Override
	public void prev() throws XMPPException {
		// empty, one stage command
	}

}
