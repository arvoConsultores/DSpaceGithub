package pt.keep.oaiextended.filters;

import java.util.ArrayList;
import java.util.List;

import org.dspace.content.DCValue;
import org.dspace.content.Item;

import pt.keep.oaiextended.AgentItem;
import pt.keep.oaiextended.Filter;

public class ThesisFilter extends Filter {
	public static final String APPLICABLE_SETS_SPECS = "thesis";
	
	private List<String> applicableSetSpecs;
	private List<String> docTypes;

	public ThesisFilter () {
		String[] array = super.getProperty("applicableSetsSpecs", APPLICABLE_SETS_SPECS).split(",");
		this.applicableSetSpecs = new ArrayList<String>();
		for (String string : array) {
			this.applicableSetSpecs.add(string.trim().toLowerCase());
		}

		this.docTypes = new ArrayList<String>();
		String[] array2 = super.getProperty("doctypes", "doctoralThesis, masterThesis").split(",");
		for (String string : array2) {
			this.docTypes.add(string.trim().toLowerCase());
		}
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
	public boolean isItemFiltered(AgentItem item) {
		if (!item.hasInformation()) return true;
		DCValue[] values = item.getInformation().item.getMetadata("dc", "type", Item.ANY, Item.ANY);
		if (values == null) return true;
		if (values.length > 0) {
			for (DCValue dc : values) {
				if (this.valueEndWithSomeStringOfList(this.docTypes, dc.value.trim().toLowerCase()))
					return false;
			}
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
