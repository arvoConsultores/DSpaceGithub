/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.xmlui.aspect.artifactbrowser;

import java.io.IOException;
import java.util.Map;

import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Response;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.reading.AbstractReader;
import org.dspace.bibliography.export.RISExporter;
import org.dspace.core.Context;
import org.xml.sax.SAXException;

/**
 * 
 * Reader that generates a text file of item with bibliography data. 
 * It supports RIS format but can be expanded to other formats.
 * 
 * @author Andres Quast (Original Servlet version)
 * @author Adan Roman Ruiz
 * @author Marta Rodriguez Gonzalez
 */

public class BibliographyReader extends AbstractReader implements Recyclable {

    protected static final int BUFFER_SIZE = 8192;

    /**
     * When should a download expire in milliseconds. This should be set to some
     * low value just to prevent someone hitting DSpace repeatily from killing
     * the server. Note: 60000 milliseconds are in a second.
     * 
     * Format: minutes * seconds * milliseconds
     */
    protected static final int expires = 60 * 60 * 60000;

    /** The Cocoon response */
    protected Response response;

    /** The Cocoon request */
    protected Request request;

    String result = null;
    String filename = null;

    /**
     * Set up the export reader.
     * 
     * See the class description for information on configuration options.
     */
    public void setup(SourceResolver resolver, @SuppressWarnings("rawtypes") Map objectModel, String src,
	    Parameters par) throws ProcessingException, SAXException,
	    IOException {
	super.setup(resolver, objectModel, src, par);

	try {
	    this.request = ObjectModelHelper.getRequest(objectModel);
	    this.response = ObjectModelHelper.getResponse(objectModel);
	    Context context = org.dspace.app.xmlui.utils.ContextUtil.obtainContext(objectModel);

	    String format = par.getParameter("format");
	    String handle = par.getParameter("handle");

	    
	    if (format.equals("ris")) {
	    	result = RISExporter.renderRisFormat(handle, context); 
	    }
	    filename = handle.replaceAll("/", "-") + "-" + format + ".txt";

	} catch (RuntimeException e) {
	    throw e;
	} catch (Exception e) {
	    throw new ProcessingException("Unable to read bitstream.", e);
	}
    }

    public void generate() throws IOException, SAXException,
	    ProcessingException {

	response.setContentType("text/plain; charset=UTF-8");
	response.setHeader("Content-Disposition", "attachment; filename=" + filename);

	out.write(result.getBytes("UTF-8"));
	out.flush();
	out.close();

    }

    /**
     * Recycle
     */
    public void recycle() {
	this.response = null;
	this.request = null;

    }

}