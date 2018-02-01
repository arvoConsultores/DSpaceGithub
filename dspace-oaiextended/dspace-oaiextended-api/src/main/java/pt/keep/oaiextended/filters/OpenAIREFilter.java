package pt.keep.oaiextended.filters;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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
public class OpenAIREFilter extends Filter {
	
	public static final String APPLICABLE_SETS_SPECS = "ec_fundedresources";
	public static final String DCRIGHTS_MAP_FILE = "oaiextended/dcrights.map";
	
	
	private List<String> applicableSetSpecs;
	private String openAirPrefix;
	private String prefix;
	private List<String> dcRightsValues;
	
	public OpenAIREFilter () {
		
		// request set's
		String[] array = super.getProperty("applicableSetsSpecs", APPLICABLE_SETS_SPECS).split(",");
		this.applicableSetSpecs = new ArrayList<String>();
		for (String string : array) {
			this.applicableSetSpecs.add(string.trim().toLowerCase());
		}
		this.getDcRightsMap();
		this.openAirPrefix = super.getProperty("idPrefix", "info:eu-repo/grantAgreement/EC/FP7/").toLowerCase();
		this.prefix = super.getProperty("accessPrefix", "info:eu-repo/semantics/").toLowerCase();
	}
	private void getDcRightsMap () {
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
						if (parts.length > 0) {
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
	
	public boolean elementAtDcRightsList (String value) {
		for (String str : this.dcRightsValues) {
			if (str.toLowerCase().trim().equals(value)) return true;
			if ((this.prefix + str).toLowerCase().trim().equals(value))
				return true;
		}
		
		return false;
	}
	
	@Override
	public boolean isItemFiltered(AgentItem filterItem) {
		if (!filterItem.hasInformation()) return true;
		DCValue[] relations = filterItem.getInformation().item.getMetadata("dc", "relation", Item.ANY, Item.ANY);
		DCValue[] rights = filterItem.getInformation().item.getMetadata("dc", "rights", Item.ANY, Item.ANY);
		boolean prefix = false;
		boolean right = false;
		if (relations == null) return true;
		for (DCValue value : relations) {
			if (value.value.toLowerCase().startsWith(this.openAirPrefix)) {
				prefix = true;
			}
		}
		if (rights == null) return true;
		for (DCValue value : rights) {
			if (this.elementAtDcRightsList(value.value.toLowerCase().trim())) {
				right = true;
			}
		}
		
		return !(prefix && right);
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
