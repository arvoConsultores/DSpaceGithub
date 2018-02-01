package pt.keep.oaiextended.filters;

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
import pt.keep.oaiextended.Filter;

/**
 * This class implements a filter:
 * - Is applied if the request set = (Dspace Configuration) oaidriver.rightsFilter.applicableSetSpec
 * - If the dc.rights element = (Dspace Configuration) oaidriver.rightsFilter.dcRightsValue
 * the item is listed.
 * 
 * @author João Melo
 */
@SuppressWarnings("deprecation")
public class DcTypeFilter extends Filter {
	public static final String APPLICABLE_SETS_SPECS = "driver";
	public static final String KNOWN_DCTYPES_FILE = "oaiextended/knownDcTypes.list";
	
	
	
	private List<String> applicableSetSpecs;
	private List<String> knownDcTypes;
	
	public DcTypeFilter () {
		
		// request set's
		String[] array = super.getProperty("applicableSetsSpecs", APPLICABLE_SETS_SPECS).split(",");
		this.applicableSetSpecs = new ArrayList<String>();
		for (String string : array) {
			this.applicableSetSpecs.add(string.trim().toLowerCase());
		}
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
	public boolean isItemFiltered(AgentItem filterItem) {
		if (!filterItem.hasInformation()) return true;
		DCValue[] values = filterItem.getInformation().item.getMetadata("dc", "type", Item.ANY, Item.ANY);
		if (values == null) return true;
		for (DCValue value : values) {
			for (String knownType : this.knownDcTypes)
				if (value.value.trim().toLowerCase().endsWith(knownType))
					return false;
		}
		return true;
	}
	@Override
	public boolean isFilterApplicable() {
		if (!super.getContext().isVirtualSet()) return false;
		if (this.applicableSetSpecs.contains(super.getContext().getHarvestSet().toLowerCase())) return true;
		else return false;
	}
	@Override
	protected void updateContext() {
		super.getContext().setHarvestSet(null);
	}
}
