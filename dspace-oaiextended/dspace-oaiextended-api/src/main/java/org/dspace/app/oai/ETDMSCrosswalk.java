package org.dspace.app.oai;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dspace.content.DCValue;
import org.dspace.content.Item;
import org.dspace.core.ConfigurationManager;
import org.dspace.search.HarvestedItemInfo;

import ORG.oclc.oai.server.crosswalk.Crosswalk;
import ORG.oclc.oai.server.verb.CannotDisseminateFormatException;

public class ETDMSCrosswalk extends Crosswalk {
	// etdms.terms=doctoralThesis,masterThesis
	public static List<String> thesisAndDissertationsTerms = null;
	
	static {
		String[] array = ConfigurationManager.getProperty("oaiextended.filters.ThesisFilter.doctypes").split(",");
		thesisAndDissertationsTerms = new ArrayList<String>();
		for (String item : array)
			thesisAndDissertationsTerms.add(item.trim().toLowerCase());
	}
	
	
	

	private static final Pattern invalidXmlPattern = Pattern.compile("([^\\t\\n\\r\\u0020-\\ud7ff\\ue000-\\ufffd\\u10000-\\u10ffff]+|[&<>])");

	public ETDMSCrosswalk(Properties properties) {
		super("http://www.ndltd.org/standards/metadata/etdms/1.0/ http://www.ndltd.org/standards/metadata/etdms/1.0/etdms.xsd");
	}

	@Override
	public String createMetadata(Object nativeItem) throws CannotDisseminateFormatException {
		Item item = ((HarvestedItemInfo) nativeItem).item;
		StringBuffer metadata = new StringBuffer();
		metadata.append("<thesis xmlns=\"http://www.ndltd.org/standards/metadata/etdms/1.0/\" ")
				.append("xsi:schemaLocation=\"http://www.ndltd.org/standards/metadata/etdms/1.0/ ")
				.append("http://www.ndltd.org/standards/metadata/etdms/1.0/etdms.xsd\">");
		
		this.buildDcElement(item, metadata, "title", "dc", "title", Item.ANY);
		this.buildDcElement(item, metadata, "creator", "dc", "creator", Item.ANY);
		this.buildDcElement(item, metadata, "creator", "dc", "contributor", "author");
		this.buildDcElement(item, metadata, "subject", "dc", "subject", Item.ANY);
		this.buildDcElement(item, metadata, "description", "dc", "description", "abstract");
		this.buildDcElement(item, metadata, "publisher", "dc", "publisher", Item.ANY);
		KeyValue[] advisor = { new KeyValue("role", "advisor") };
		this.buildDcElement(item, metadata, "contributor", "dc", "contributor", Item.ANY,  advisor);
		this.buildDcElement(item, metadata, "date", "dc", "date", "issued");
		this.buildDcElement(item, metadata, "type", "dc", "type", Item.ANY);
		this.buildDcElement(item, metadata, "format", "dc", "format", Item.ANY);
		this.buildDcElement(item, metadata, "identifier", "dc", "identifier", Item.ANY);
		this.buildDcElement(item, metadata, "language", "dc", "language", Item.ANY);
		this.buildDcElement(item, metadata, "coverage", "dc", "coverage", Item.ANY);
		this.buildDcElement(item, metadata, "rights", "dc", "rights", Item.ANY);
		
		this.startElement(metadata, "degree");
		this.buildDcElement(item, metadata, "name", "thesis", "degree", "name");
		this.buildDcElement(item, metadata, "level", "thesis", "degree", "level");
		this.buildDcElement(item, metadata, "discipline", "thesis", "degree", "discipline");
		this.buildDcElement(item, metadata, "grantor", "thesis", "degree", "grantor");
		this.endElement(metadata, "degree");
		
		metadata.append("</thesis>");
		return metadata.toString();
	}
	
	private void buildDcElement (Item item, StringBuffer metadata, String tag, String md, String elem, String qualifier) {
		this.buildDcElement(item, metadata, tag, md, elem, qualifier, null);
	}

	
	private void buildDcElement (Item item, StringBuffer metadata, String tag, String md, String elem, String qualifier, KeyValue[] properties) {
		DCValue[] dublinCoreMetadata = item.getMetadata(md, elem, qualifier, Item.ANY);
		for (DCValue value : dublinCoreMetadata)
			buildElement(metadata, tag, value.value, properties);
	}

	/**
	 * 
	 * @param list
	 * @param end lower cased string
	 * @return
	 */
	private boolean valueEndWithSomeStringOfList (List<String> list, String value) {
		for (String str : list)
			if (value.endsWith(str.trim().toLowerCase()))
				return true;
		return false;
	}
	
	@Override
	public boolean isAvailableFor(Object nativeItem) {
		boolean available = false;
		Item item = ((HarvestedItemInfo) nativeItem).item;
		DCValue[] metadataWithDCType = item.getMetadata("dc", "type", Item.ANY, Item.ANY);
		if (metadataWithDCType != null) {
			for (DCValue type : metadataWithDCType) {
				if (this.valueEndWithSomeStringOfList(thesisAndDissertationsTerms, type.value.trim().toLowerCase())) {
					available = true;
				}
			}
		}
		return available;
	}
	private void buildElement(StringBuffer metadata, String tag, String element) {
		this.buildElement(metadata, tag, element, null);
	}
	private void startElement (StringBuffer metadata, String tag) {
		this.startElement(metadata, tag, null);
	}
	private void startElement (StringBuffer metadata, String tag, KeyValue[] properties) {
		metadata.append("<");
		metadata.append(tag);
		if (properties != null) {
			for (KeyValue kv : properties) {
				metadata.append(" ");
				metadata.append(kv.key);
				metadata.append("=\"");
				metadata.append(kv.value);
				metadata.append("\"");
			}
		}
		metadata.append(">");
	}
	
	private void endElement (StringBuffer metadata, String tag) {
		metadata.append("</");
		metadata.append(tag);
		metadata.append(">");
	}
	
	private void middleElement (StringBuffer metadata, String element) {
		StringBuffer valueBuf = new StringBuffer(element.length());
		Matcher xmlMatcher = invalidXmlPattern.matcher(element.trim());
		while (xmlMatcher.find()) {
			String group = xmlMatcher.group();
			// group will either contain a character that we need to encode
			// for xml
			// (ie. <, > or &), or it will be an invalid character
			// test the contents and replace appropriately
			if (group.equals("&"))
				xmlMatcher.appendReplacement(valueBuf, "&amp;");
			else if (group.equals("<"))
				xmlMatcher.appendReplacement(valueBuf, "&lt;");
			else if (group.equals(">"))
				xmlMatcher.appendReplacement(valueBuf, "&gt;");
			else
				xmlMatcher.appendReplacement(valueBuf, " ");
		}

		// add bit of the string after the final match
		xmlMatcher.appendTail(valueBuf);
		metadata.append(valueBuf.toString().trim());
	}
	
	private void buildElement(StringBuffer metadata, String tag, String element, KeyValue[] properties) {
		if (element != null && element.length() > 0) {
			// Escape XML chars <, > and &
			// Also replace all invalid characters with ' '
			this.startElement(metadata, tag, properties);
			this.middleElement(metadata, element);
			this.endElement(metadata, tag);
		}
	}
	
	class KeyValue {
		public String key;
		public String value;
		
		public KeyValue (String key, String value) {
			this.key = key;
			this.value = value;
		}
	}
}
