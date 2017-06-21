/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.xmlui.aspect.artifactbrowser;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Response;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.reading.AbstractReader;
import org.dspace.app.xmlui.utils.ContextUtil;
import org.dspace.bibliography.export.BibtexExporter;
import org.dspace.content.Collection;
import org.dspace.content.DCDate;
import org.dspace.content.DCPersonName;
import org.dspace.content.DCValue;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.ItemIterator;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.core.Utils;
import org.dspace.handle.HandleManager;
import org.xml.sax.SAXException;

/**
 * 
 * Reader that generates a text file of item or collections with bibliography
 * data. It Support bibtex, ris and endnote formats
 * 
 * @author Andres Quast (Original Servlet version)
 * @author Adan Roman Ruiz
 * @author Marta Rodriguez Gonzalez
 */

@SuppressWarnings("deprecation")
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
    @SuppressWarnings("rawtypes")
    public void setup(SourceResolver resolver, Map objectModel, String src,
	    Parameters par) throws ProcessingException, SAXException,
	    IOException {
	super.setup(resolver, objectModel, src, par);

	try {
	    this.request = ObjectModelHelper.getRequest(objectModel);
	    this.response = ObjectModelHelper.getResponse(objectModel);
	    Context context = ContextUtil.obtainContext(objectModel);

	    String format = par.getParameter("format");
	    String handle = par.getParameter("handle");

	    DSpaceObject dso = HandleManager.resolveToObject(context, handle);

	    ArrayList<Item> list = new ArrayList<Item>();
	    if (dso.getType() == Constants.ITEM) {
		list.add((Item) dso);
	    } else if (dso.getType() == Constants.COLLECTION) {
		ItemIterator it = ((Collection) dso).getItems();
		while (it.hasNext()) {
		    list.add(it.next());
		}
	    }

	    if (format.equals("bibtex")) {
	    	result = BibtexExporter.renderBibtexFormat(handle, context); 
	    	filename = handle.replaceAll("/", "-") + "-" + format + ".bib";
	    } else if (format.equals("ris")) {
		result = renderRisFormat(list.toArray(new Item[list.size()]),
			request.getLocale());
			filename = handle.replaceAll("/", "-") + "-" + format + ".txt";
	    } else if (format.equals("endnote")) {
		result = renderEndNoteFormat(
			list.toArray(new Item[list.size()]),
			request.getLocale());
			filename = handle.replaceAll("/", "-") + "-" + format + ".txt";
	    }else if (format.equals("refworks")) {
			result = renderRefworksFormat(
			list.toArray(new Item[list.size()]),
			request.getLocale());
			filename = handle.replaceAll("/", "-") + "-" + format + ".txt";
	    }

	} catch (RuntimeException e) {
	    throw e;
	} catch (Exception e) {
	    throw new ProcessingException("Unable to read bitstream.", e);
	}
    }

    public void generate() throws IOException, SAXException,
	    ProcessingException {

	response.setContentType("text/plain; charset=UTF-8");
	response.setHeader("Content-Disposition", "attachment; filename="
		+ filename);

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

    
	public String renderRisFormat(Item[] items, Locale locale) {
	// angenommen ich bekomme eine Liste von Items: items Dann muss diese
	// gerendert werden
	// create a stringbuffer for storing the metadata
	String schema = "dc";
	String RisHead = "TY - ";
	String RisFoot = "ER - ";

	// define needed metadatafields
	int bibType = 1;
	// variable document types may need various metadata fields
	String[] DC2Bib = new String[5];
	DC2Bib[1] = "dc.contributor.author, dc.title, dc.relation.ispartofseries, dc.date.issued, dc.identifier.issn, dc.identifier.uri, dc.description.abstract, dc.subject,dc.language,dc.publisher";
	DC2Bib[2] = "dc.contributor, dc.title, dc.publisher, dc.date.issued, dc.identifier.isbn,  dc.identifier.uri, dc.description.abstract, dc.subject,dc.language,dc.publisher";
	DC2Bib[3] = "dc.contributor, dc.title, dc.publisher, dc.date.issued, dc.identifier.isbn,  dc.identifier.uri, dc.description.abstract, dc.subject,dc.language,dc.publisher";

	StringBuffer sb = new StringBuffer();

	// Parsing metadatafields
	for (int i = 0; i < items.length; i++) {
	    boolean tyFound = false;
	    Item item = items[i];
	    // First needed for Ris: dc.type
	    DCValue[] types = item.getMetadata(schema, "type", Item.ANY,
		    Item.ANY);

	    for (int j = 0; (j < types.length) && !tyFound; j++) {
		String type = Utils.addEntities(types[j].value);

		if (type.equals("Article")) {
		    sb.append(RisHead + "JOUR");
		    bibType = 1;
		    tyFound = true;
		} else if (type.equals("Book")) {
		    sb.append(RisHead + "BOOK");
		    bibType = 2;
		    tyFound = true;
		} else if (type.equals("Book Chapter")) {
		    sb.append(RisHead + "CHAP");
		    bibType = 3;
		    tyFound = true;
		} else if (type.equals("Thesis")) {
		    sb.append(RisHead + "THES");
		    bibType = 4;
		    tyFound = true;
		} else if (type.equals("Technical Report")) {
		    sb.append(RisHead + "RPRT");
		    bibType = 1;
		    tyFound = true;
		} else if (type.equals("Preprint")) {
		    sb.append(RisHead + "JOUR");
		    bibType = 1;
		    tyFound = true;
		}
	    }

	    // set type in case no type is given
	    if (!tyFound) {
		sb.append(RisHead + "JOUR");
		bibType = 1;
	    }

	    sb.append(" \n");

	    // Now get all the metadata needed for the requested objecttype
	    StringTokenizer st = new StringTokenizer(DC2Bib[bibType], ",");

	    while (st.hasMoreTokens()) {
		String field = st.nextToken().trim();
		String[] eq = field.split("\\.");
		schema = eq[0];
		String element = eq[1];
		String qualifier = Item.ANY;
		if (eq.length > 2 && eq[2].equals("*")) {
		    qualifier = Item.ANY;
		} else if (eq.length > 2) {
		    qualifier = eq[2];
		}

		DCValue[] values = item.getMetadata(schema, element, qualifier,
			Item.ANY);

		// Parse the metadata into a record
		for (int k = 0; k < values.length; k++) {
		    if (element.equals("contributor")) {
			if (k == 0) {
			    sb.append("A1 - "
				    + Utils.addEntities(values[k].value));
			} else {
			    sb.append("AU - "
				    + Utils.addEntities(values[k].value));
			}

		    } else if (element.equals("relation")) {
			if (k == 0) {
			    sb.append("JO - "
				    + Utils.addEntities(values[k].value));
			}
			if (k == 1) {
			    sb.append("VL - "
				    + Utils.addEntities(values[k].value));
			}
		    } else if (element.equals("title")) {
			if (k == 0) {
			    sb.append("T1 - "
				    + Utils.addEntities(values[k].value));
			}
		    } else if (element.equals("description")) {
			if (k == 0) {
			    sb.append("AB - "
				    + Utils.addEntities(values[k].value));
			}
		    } else if (element.equals("identifier")
			    && (qualifier.equals("issn") || qualifier.equals("isbn"))) {
			if (k == 0) {
			    sb.append("SN - "
				    + Utils.addEntities(values[k].value));
			}
		    }

		    else if (element.equals("identifier")
			    && qualifier.equals("uri")) {
			sb.append("UR - " + Utils.addEntities(values[k].value));
		    }

		    else if (element.equals("subject")) {
			sb.append("KW - " + Utils.addEntities(values[k].value));
		    }

		    else if (element.equals("date")) {
			if (k == 0) {
			    // formating the Date
			    DCDate dd = new DCDate(values[k].value);
			    String date = dd.displayDate(false, false, locale)
				    .trim();
			    int last = date.length();
			    date = date.substring((last - 4), (last));

			    sb.append("Y1 - " + date);
			}
		    } 
		    
		    else if (element.equals("language")) {
			sb.append("LA - " + Utils.addEntities(values[k].value));
		    }
		    
		    else if (element.equals("publisher")) {
			sb.append("PB - " + Utils.addEntities(values[k].value));
		    }
		    
		    else {
			if (k == 0) {
			    sb.append(qualifier + " - "
				    + Utils.addEntities(values[k].value));
			}
		    }
		    sb.append("\n");
		}
	    }
	    sb.append(RisFoot + "\n\n");
	}

	String RisData = sb.toString();
	return RisData;
    }

	public String renderEndNoteFormat(Item[] items, Locale locale) {
	// angenommen ich bekomme eine Liste von Items: items Dann muss diese
	// gerendert werden
	// create a stringbuffer for storing the metadata
	String schema = "dc";
	String ENHead = "%0 ";
	String ENFoot = "%~ GOEDOC, SUB GOETTINGEN";

	// define needed metadatafields
	int bibType = 1;
	// variable document types may need various metadata fields
	String[] DC2Bib = new String[5];
	DC2Bib[1] = "dc.contributor.author, dc.title, dc.relation.ispartofseries, dc.date.issued, dc.identifier.issn, dc.identifier.uri, dc.description.abstract, dc.subject";
	DC2Bib[2] = "dc.contributor, dc.title, dc.publisher, dc.date.issued, dc.identifier.isbn,  dc.identifier.uri, dc.description.abstract, dc.subject";
	DC2Bib[3] = "dc.contributor, dc.title, dc.publisher, dc.date.issued, dc.identifier.isbn,  dc.identifier.uri, dc.description.abstract, dc.subject";

	StringBuffer sb = new StringBuffer();

	// Parsing metadatafields
	for (int i = 0; i < items.length; i++) {
	    boolean tyFound = false;
	    Item item = items[i];
	    // First needed for BibTex: dc.type
	    DCValue[] types = item.getMetadata(schema, "type", Item.ANY,
		    Item.ANY);

	    for (int j = 0; (j < types.length) && !tyFound; j++) {
		String type = Utils.addEntities(types[j].value);

		if (type.equals("Article")) {
		    sb.append(ENHead + "Journal Article");
		    bibType = 1;
		    tyFound = true;
		} else if (type.equals("Book")) {
		    sb.append(ENHead + type);
		    bibType = 2;
		    tyFound = true;
		} else if (type.equals("Book Section")) {
		    sb.append(ENHead + "");
		    bibType = 3;
		    tyFound = true;
		} else if (type.equals("Thesis")) {
		    sb.append(ENHead + "Thesis");
		    bibType = 4;
		    tyFound = true;
		} else if (type.equals("Technical Report")) {
		    sb.append(ENHead + "Report");
		    bibType = 1;
		    tyFound = true;
		} else if (type.equals("Preprint")) {
		    sb.append(ENHead + "Journal Article");
		    bibType = 1;
		    tyFound = true;
		}
	    }

	    // set type in case no type is given
	    if (!tyFound) {
		sb.append(ENHead + "Journal Article");
		bibType = 1;
	    }

	    sb.append(" \n");

	    // Now get all the metadata needed for the requested objecttype
	    StringTokenizer st = new StringTokenizer(DC2Bib[bibType], ",");

	    while (st.hasMoreTokens()) {
		String field = st.nextToken().trim();
		String[] eq = field.split("\\.");
		schema = eq[0];
		String element = eq[1];
		String qualifier = Item.ANY;
		if (eq.length > 2 && eq[2].equals("*")) {
		    qualifier = Item.ANY;
		} else if (eq.length > 2) {
		    qualifier = eq[2];
		}

		DCValue[] values = item.getMetadata(schema, element, qualifier,
			Item.ANY);

		// Parse the metadata into a record
		for (int k = 0; k < values.length; k++) {
		    if (element.equals("contributor")) {
			if (k == 0) {
			    sb.append("%A "
				    + Utils.addEntities(values[k].value));
			} else {
			    sb.append("%A "
				    + Utils.addEntities(values[k].value));
			}

		    } else if (element.equals("relation")) {
			if (k == 0) {
			    sb.append("%J "
				    + Utils.addEntities(values[k].value));
			}
			if (k == 1) {
			    sb.append("%V "
				    + Utils.addEntities(values[k].value));
			}
		    } else if (element.equals("title")) {
			if (k == 0) {
			    sb.append("%T "
				    + Utils.addEntities(values[k].value));
			}
		    } else if (element.equals("description")) {
			if (k == 0) {
			    sb.append("%X "
				    + Utils.addEntities(values[k].value));
			}
		    } else if (element.equals("identifier")
			    && qualifier.equals("issn")) {
			if (k == 0) {
			    sb.append("%@ "
				    + Utils.addEntities(values[k].value));
			}
		    }

		    else if (element.equals("identifier")
			    && qualifier.equals("uri")) {
			sb.append("%U " + Utils.addEntities(values[k].value));
		    }

		    else if (element.equals("subject")) {
			sb.append("%K " + Utils.addEntities(values[k].value));
		    }

		    else if (element.equals("date")) {
			if (k == 0) {
			    // formating the Date
			    DCDate dd = new DCDate(values[k].value);
			    String date = dd.displayDate(false, false, locale)
				    .trim();
			    int last = date.length();
			    date = date.substring((last - 4), (last));

			    sb.append("%D " + date);
			}
		    } else {
			if (k == 0) {
			    sb.append(qualifier + " "
				    + Utils.addEntities(values[k].value));
			}
		    }
		    sb.append("\n");
		}
	    }
	    sb.append(ENFoot + "\n\n");
	}
	String ENData = sb.toString();
	return ENData;
    }
    
	public String renderRefworksFormat(Item[] items, Locale locale) throws UnsupportedEncodingException {

    	StringBuffer sb = new StringBuffer();
    	for (int k = 0; k < items.length; k++) {
    	    Item item=items[k];
    	    DCValue[] type = item.getDC("type", null, Item.ANY);
    	    if (type.length > 0)
    	    {
    		String s_type = type[0].value;

    		if (s_type.equals("Animation"))
    		    s_type = "Computer Program";
    		else if (s_type.equals("Article"))
    		    s_type = "Journal, Electronic";
    		else if (s_type.equals("Book"))
    		    s_type = "Book, Whole";
    		else if (s_type.equals("Book chapter"))
    		    s_type = "Book, Section";
    		else if (s_type.equals("Dataset"))
    		    s_type = "Generic";
    		else if (s_type.equals("Image"))
    		    s_type = "Artwork";
    		else if (s_type.equals("Image, 3-D"))
    		    s_type = "Artwork";
    		else if (s_type.equals("Learning Object"))
    		    s_type = "Generic";
    		else if (s_type.equals("Map"))
    		    s_type = "Map";
    		else if (s_type.equals("Musical Score"))
    		    s_type = "Music Score";
    		else if (s_type.equals("Other"))
    		    s_type = "Generic";
    		else if (s_type.equals("Plan or blueprint"))
    		    s_type = "Generic";
    		else if (s_type.equals("Preprint"))
    		    s_type = "Generic";
    		else if (s_type.equals("Presentation"))
    		    s_type = "Generic";
    		else if (s_type.equals("Recording, acoustical"))
    		    s_type = "Sound Recording";
    		else if (s_type.equals("Recording, musical"))
    		    s_type = "Sound Recording";
    		else if (s_type.equals("Recording, oral"))
    		    s_type = "Sound Recording";
    		else if (s_type.equals("Software"))
    		    s_type = "Computer Program";
    		else if (s_type.equals("Technical Report"))
    		    s_type = "Report";
    		else if (s_type.equals("Thesis"))
    		    s_type = "Dissertation/Thesis";
    		else if (s_type.equals("Video"))
    		    s_type = "Video/DVD";
    		else if (s_type.equals("Working Paper"))
    		    s_type = "Report";

    		sb.append("RT " + s_type + "\n");
    	    }

    	    DCValue[] titles = item.getDC("title", null, Item.ANY);

    	    if (titles.length > 0)
    		sb.append("T1 " + titles[0].value + "\n");

    	    DCValue[] titlesa = item.getDC("title", "alternative", Item.ANY);

    	    if (titlesa.length > 0)
    		sb.append("T2 " + titlesa[0].value + "\n");

    	    DCValue[] authors = item.getDC("contributor", "author", Item.ANY);

    	    if (authors.length > 0)
    	    {
    		for (int i = 0; i < authors.length; i++ )
    		{
    		    String auth = authors[i].value;
    		    int commafirstIndex = auth.indexOf(",");
    		    int commalastIndex = auth.lastIndexOf(",");

    		    if ( commalastIndex > commafirstIndex)
    		    {
    			sb.append("A1 " + auth.substring(0, commalastIndex) +"\n");
    			if (!auth.substring(commalastIndex+1).equals(null) ||!auth.substring(commalastIndex+1).equals(" ") )
    			    sb.append("AD " + auth.substring(commalastIndex+1) + "\n");
    		    }
    		    else
    			sb.append("A1 " + auth +"\n");
    		}
    	    }


    	    DCValue[] editor = item.getDC("contributor", "editor", Item.ANY);

    	    if (editor.length > 0)
    	    {
    		for (int i = 0; i < editor.length; i++ )
    		{
    		    DCPersonName dpn = new DCPersonName(editor[i].value);
    		    sb.append("A2 " + dpn.getLastName() + dpn.getFirstNames() +"\n");
    		}
    	    }

    	    DCValue[] illustrator = item.getDC("contributor", "illustrator", Item.ANY);

    	    if (illustrator.length > 0)
    	    {
    		for (int i = 0; i < illustrator.length; i++ )
    		{
    		    DCPersonName dpn = new DCPersonName(illustrator[i].value);
    		    sb.append("A2 " + dpn.getLastName() + dpn.getFirstNames() +"\n");
    		}
    	    }

    	    DCValue[] other = item.getDC("contributor", "other", Item.ANY);

    	    if (other.length > 0)
    	    {
    		for (int i = 0; i < other.length; i++ )
    		{
    		    DCPersonName dpn = new DCPersonName(other[i].value);
    		    sb.append("A2 " + dpn.getLastName() + dpn.getFirstNames() +"\n");
    		}
    	    }

    	    DCValue[] keyword = item.getDC("subject", Item.ANY, Item.ANY);

    	    if (keyword.length > 0)
    	    {
    		for (int i = 0; i < keyword.length; i++ )
    		{    
    		    sb.append("K1 " + keyword[i].value + "\n");
    		}
    	    }

    	    DCValue[] abs = item.getDC("description", "abstract", Item.ANY);

    	    if (abs.length > 0)
    	    {
    		byte [] b_abs = ((abs[0].value).replaceAll("\n","").replaceAll("\r","")).getBytes("UTF-8");
    		String s_abs = new String(b_abs);
    		sb.append("AB " + s_abs + "\n");
    	    }

    	    DCValue[] publisher = item.getDC("publisher", null, Item.ANY);

    	    if (publisher.length > 0)
    	    {
    		sb.append("PB " + publisher[0].value + "\n");
    	    }

    	    DCValue[] isbn = item.getDC("identifier", "isbn", Item.ANY);

    	    if (isbn.length > 0)
    	    {
    		sb.append("SN " + isbn[0].value + "\n");
    	    }

    	    DCValue[] issn = item.getDC("identifier", "issn", Item.ANY);

    	    if (issn.length > 0)
    	    {
    		sb.append("SN " + issn[0].value + "\n");
    	    }

    	    DCValue[] dates = item.getDC("date", "issued", Item.ANY);

    	    if (dates.length > 0)
    	    {
    		String fullDate = dates[0].value;

    		String yearDate = fullDate.substring(0, 4);

    		sb.append("YR " + yearDate + "\n");

    		sb.append("FD " + fullDate + "\n");
    	    }

    	    DCValue[] identifier = item.getDC("identifier", "uri", Item.ANY);

    	    if (identifier.length > 2)
    	    {
    		for(int i =0; i < identifier.length; i++)
    		{
    		    if (identifier[i].value.startsWith("http://hdl.handle.net"))
    			sb.append("LK " + identifier[i].value + "\n");
    		}
    	    }
    	    else
    	    {
    		sb.append("LK " + identifier[0].value + "\n");
    	    }

    	    DCValue[] uri = item.getDC("identifier", "uri", Item.ANY);
    	    for (int i = 0; i < uri.length; i++ )
    	    {
    		sb.append("UL " + uri[i].value + "\n");
    	    }

    	    DCValue[] lang = item.getDC("language", "iso", Item.ANY);
    	    if (lang.length > 0)
    	    {
    		sb.append("LA " + lang[0].value + "\n");
    	    }

    	    DCValue[] citation = item.getDC("identifier", "citation", Item.ANY);
    	    DCValue[] descn = item.getDC("description", null, Item.ANY);
    	    DCValue[] descs = item.getDC("description", "sponsorship", Item.ANY);
    	    DCValue[] descst = item.getDC("description", "statementofresponsibility", Item.ANY);
    	    DCValue[] desct = item.getDC("description", "tableofcontents", Item.ANY);
    	    DCValue[] descu = item.getDC("description", "uri", Item.ANY);

    	    if (citation.length > 0)
    	    {
    		sb.append("NO " + citation[0].value.replaceAll("\n","").replaceAll("\r","") + "\n");
    	    }
    	    if (descn.length > 0)
    	    {
    		for (int i =0; i < descn.length; i++)
    		{
    		    sb.append("NO " + descn[i].value.replaceAll("\n","").replaceAll("\r","") + "\n");
    		}
    	    }
    	    if (descs.length > 0)
    	    {
    		sb.append("NO " + descs[0].value.replaceAll("\n","").replaceAll("\r","") + "\n");
    	    }
    	    if (descst.length >0)
    	    {
    		sb.append("NO " + descst[0].value.replaceAll("\n","").replaceAll("\r","") + "\n");
    	    }
    	    if (desct.length >0)
    	    {
    		sb.append("NO " + desct[0].value.replaceAll("\n","").replaceAll("\r","") + "\n");
    	    }
    	    if (descu.length >0)
    	    {
    		byte [] b_descu = descu[0].value.replaceAll("\n","").replaceAll("\r","").getBytes("UTF-8");
    		String s_descu = new String(b_descu);
    		sb.append("NO " + s_descu + "\n");
    	    }

    	    sb.append("DS " + "MINDS@UW" + "\n");

    	    Date d = new Date();
    	    String df = DateFormat.getDateInstance(DateFormat.MEDIUM).format(d);
    	    sb.append("RD " + df + "\n");
    	}
    	return sb.toString();  
        }
}
