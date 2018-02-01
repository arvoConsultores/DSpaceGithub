package pt.keep.oaiextended.modifiers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.dspace.content.DCValue;
import org.dspace.content.Item;
import org.dspace.core.ConfigurationManager;

import pt.keep.oaiextended.AgentItem;
import pt.keep.oaiextended.Modifier;

/**
 * This Modifier will be able to add a prefix to a restricted list of dc.types
 * 
 * @author João Melo
 */
@SuppressWarnings("deprecation")
public class AccessRightsModifier extends Modifier {
	public static final String PREFIX = "info:eu-repo/semantics/";
	public static final String DCRIGHTS_MAP_FILE = "oaiextended/dcrights.map";
	
	private String prefix;
	private Map<String, String> dcRights;
	private List<String> dcRightsValues;
	
	public AccessRightsModifier () {
		super();
		this.prefix = super.getProperty("prefix", PREFIX);
		this.getDcRightsMap();
	}
	
	/**
	 * Gets the list of known dc types.
	 * 
	 * @return List
	 */
	private void getDcRightsMap () {
		this.dcRights = new TreeMap<String,String>();
		this.dcRightsValues = new ArrayList<String>();
		try {
			String dir = ConfigurationManager.getProperty("dspace.dir");
			if (dir.endsWith("/"))
				dir += "config/"+ DCRIGHTS_MAP_FILE;
			else
				dir += "/config/" + DCRIGHTS_MAP_FILE;
			InputStream is;
			if (dir != null) {
				is = new FileInputStream(dir);
				BufferedReader stream = new BufferedReader(
						new InputStreamReader(is));
				String line;
				while ((line = stream.readLine()) != null) {
					line = line.trim();
					if (!line.equals("") && !line.startsWith("#")) { // Isn't a
																		// comment
						String parts[] = line.split("=");
						if (parts.length > 1) {
							this.dcRights.put(parts[0].toLowerCase().trim(), parts[1].trim());
						} else if (parts.length == 1) {
							this.dcRightsValues.add(parts[0].trim().toLowerCase());
						}
					}
				}
				stream.close();
				is.close();
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void modifyItem(AgentItem oaiItem) {
		if (oaiItem.hasInformation()) {
			DCValue[] values = oaiItem.getInformation().item.getMetadata("dc", "rights", Item.ANY, Item.ANY);
			oaiItem.getInformation().item.clearMetadata("dc", "rights", Item.ANY, Item.ANY);
			for (DCValue value : values) {
				if (this.dcRights.containsKey(value.value.toLowerCase()))
					oaiItem.getInformation().item.addMetadata(value.schema, value.element, value.qualifier, value.language, this.prefix + this.dcRights.get(value.value.toLowerCase()));
				else if (this.dcRightsValues.contains(value.value.toLowerCase()))
					oaiItem.getInformation().item.addMetadata(value.schema, value.element, value.qualifier, value.language, this.prefix + value.value);
				else oaiItem.getInformation().item.addMetadata(value.schema, value.element, value.qualifier, value.language, value.value);
			}
		}
	}
}
