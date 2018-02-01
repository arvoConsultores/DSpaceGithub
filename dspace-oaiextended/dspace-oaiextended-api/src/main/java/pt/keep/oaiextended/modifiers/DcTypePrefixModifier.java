package pt.keep.oaiextended.modifiers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
public class DcTypePrefixModifier extends Modifier {
	public static final String PREFIX = "info:eu-repo/semantics/";
	public static final String KNOWN_DCTYPES_FILE = "oaiextended/knownDcTypes.list";
	
	private String prefix;
	private List<String> knownDcTypes;
	
	public DcTypePrefixModifier () {
		super();
		this.prefix = super.getProperty("prefix", PREFIX);
		this.knownDcTypes = this.getKnownDcTypes();
	}
	
	/**
	 * Gets the list of known dc types.
	 * 
	 * @return List
	 */
	private List<String> getKnownDcTypes () {
		List<String> list = new ArrayList<String>();
		try {
			String dir = ConfigurationManager.getProperty("dspace.dir");
			if (dir.endsWith("/"))
				dir += "config/"+KNOWN_DCTYPES_FILE;
			else
				dir += "/config/" + KNOWN_DCTYPES_FILE;
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
						list.add(line.toLowerCase());
					}
				}
				stream.close();
				is.close();
			}
			return list;
		} catch (Exception e) {
			return list;
		}
	}

	@Override
	public void modifyItem(AgentItem oaiItem) {
		if (oaiItem.hasInformation()) {
			DCValue[] values = oaiItem.getInformation().item.getMetadata("dc", "type", Item.ANY, Item.ANY);
			oaiItem.getInformation().item.clearMetadata("dc", "type", Item.ANY, Item.ANY);
			for (DCValue value : values) {
				if (this.knownDcTypes.contains(value.value.toLowerCase()))
					oaiItem.getInformation().item.addMetadata(value.schema, value.element, value.qualifier, value.language, this.prefix + value.value);
				else oaiItem.getInformation().item.addMetadata(value.schema, value.element, value.qualifier, value.language, value.value);
			}
		}
	}
}
