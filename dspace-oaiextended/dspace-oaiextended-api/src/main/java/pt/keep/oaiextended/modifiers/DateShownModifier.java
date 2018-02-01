package pt.keep.oaiextended.modifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dspace.content.DCValue;
import org.dspace.content.Item;

import pt.keep.oaiextended.AgentItem;
import pt.keep.oaiextended.Modifier;

@SuppressWarnings("deprecation")
public class DateShownModifier extends Modifier {
	
	public static final String DEFAULT_DATES_SHOWN = "issued";
	
	private List<String> qualifiersShown;
	
	public DateShownModifier () {
		super();
		this.qualifiersShown = new ArrayList<String>();
		String[] array = super.getProperty("qualifierShown", DEFAULT_DATES_SHOWN).split(",");
		for (String string : array) {
			this.qualifiersShown.add(string.trim().toLowerCase());
		}
	}

	@Override
	public void modifyItem(AgentItem oaiItem) {
		if (oaiItem.hasInformation()) {
			DCValue[] values = oaiItem.getInformation().item.getMetadata("dc", "date", Item.ANY, Item.ANY);
			oaiItem.getInformation().item.clearMetadata("dc", "date", Item.ANY, Item.ANY);
			for (DCValue value : values) {
				if (value.qualifier != null && this.qualifiersShown.contains(value.qualifier.toLowerCase())) {
					oaiItem.getInformation().item.addMetadata(value.schema, value.element, value.qualifier, value.language, this.format(value.value));
				}
			}
		}
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
