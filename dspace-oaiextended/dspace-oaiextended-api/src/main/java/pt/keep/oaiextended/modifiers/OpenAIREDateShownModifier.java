package pt.keep.oaiextended.modifiers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.dspace.content.DCValue;
import org.dspace.content.Item;

import pt.keep.oaiextended.AgentItem;
import pt.keep.oaiextended.Modifier;

@SuppressWarnings("deprecation")
public class OpenAIREDateShownModifier extends Modifier {
	private static Logger log = LogManager.getLogger(OpenAIREDateShownModifier.class);
	
	public static final String DEFAULT_DATES_SHOWN = "issued";
	
	private String prefix;
	private String qualifier;
	private String issuedQualifier;
	
	public OpenAIREDateShownModifier () {
		super();
		this.issuedQualifier = DEFAULT_DATES_SHOWN;
		this.prefix = super.getProperty("embargoPrefix", "info:eu-repo/date/embargoEnd/");
		this.qualifier = super.getProperty("embargoQualifier", "embargo");
		if (this.qualifier.equals("")) this.qualifier = null;
	}

	@Override
	public void modifyItem(AgentItem oaiItem) {
		boolean add = false;
		DCValue[] rights = oaiItem.getInformation().item.getMetadata("dc", "rights", Item.ANY, Item.ANY);
		for (DCValue v : rights)
			if (v.value.toLowerCase().contains("embargo")) add = true;
		
		if (oaiItem.hasInformation()) {
			DCValue[] values = oaiItem.getInformation().item.getMetadata("dc", "date", Item.ANY, Item.ANY);
			DCValue issued = null, embargo = null;
			oaiItem.getInformation().item.clearMetadata("dc", "date", Item.ANY, Item.ANY);
			for (DCValue value : values) {
				if (value.qualifier != null && this.issuedQualifier.toLowerCase().equals(value.qualifier.toLowerCase()))
					issued = value;
				else if (add && this.equalFields(value.qualifier, this.qualifier)) 
					embargo = value;
			}
			if (embargo != null)
				oaiItem.getInformation().item.addMetadata(embargo.schema, embargo.element, embargo.qualifier, embargo.language, this.prefix + this.format(embargo.value));
			if (issued != null)
				oaiItem.getInformation().item.addMetadata(issued.schema, issued.element, issued.qualifier, issued.language, this.format(issued.value));
		}
	}
	
	/**
	 * 
	 * @param actual Actual value
	 * @param expected Expected value
	 * @return If values are equal
	 */
	private boolean equalFields(String actual, String expected) {
		if (actual == null && expected == null) return true;
		else if (actual != null && expected != null) return actual.equals(expected);
		else return false;
	}
	
	/**
	 * Formats the dc.date to a human readable date
	 * 
	 * @param value
	 * @return date
	 */
	private String format(String value) {
		Pattern[] patterns = {
				Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}"),
				Pattern.compile("[0-9]{4}-[0-9]{2}"),
				Pattern.compile("[0-9]{4}")
		};
		Matcher matcher;
		for (Pattern pattern : patterns) {
			matcher = pattern.matcher(value);
			if (matcher.find()) return matcher.group();
		}
		return value;
	}

}
