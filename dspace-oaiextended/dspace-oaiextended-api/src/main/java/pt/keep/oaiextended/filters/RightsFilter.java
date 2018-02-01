package pt.keep.oaiextended.filters;

import java.util.ArrayList;
import java.util.List;

import org.dspace.content.DCValue;
import org.dspace.content.Item;

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
public class RightsFilter extends Filter {
	public static final String DC_RIGHTS_VALUES = "open access";
	public static final String APPLICABLE_SETS_SPECS = "driver";
	
	
	private List<String> applicableSetSpecs;
	private List<String> dcRightsValues;
	
	public RightsFilter () {
		
		// request set's
		String[] array = super.getProperty("applicableSetsSpecs", APPLICABLE_SETS_SPECS).split(",");
		this.applicableSetSpecs = new ArrayList<String>();
		for (String string : array) {
			this.applicableSetSpecs.add(string.trim().toLowerCase());
		}
		// dc.rights possible values
		array = super.getProperty("dcRightsValues", DC_RIGHTS_VALUES).split(",");
		this.dcRightsValues = new ArrayList<String>();
		for (String string : array) {
			this.dcRightsValues.add(string.trim().toLowerCase());
		}
	}
	
	@Override
	public boolean isItemFiltered(AgentItem filterItem) {
		if (!filterItem.hasInformation()) return true;
		DCValue[] values = filterItem.getInformation().item.getMetadata("dc", "rights", Item.ANY, Item.ANY);
		if (values == null) return true;
		for (DCValue value : values) {
			if (this.dcRightsValues.contains(value.value.toLowerCase())) return false;
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
