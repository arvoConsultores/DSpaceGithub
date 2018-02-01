package pt.keep.oaiextended.filters;

import java.util.ArrayList;
import java.util.List;

import org.dspace.content.DCValue;
import org.dspace.content.Item;

import pt.keep.oaiextended.AgentItem;
import pt.keep.oaiextended.Filter;

public class KeywordsFilter extends Filter {
	public static final String KEYWORDS = "open access";
	public static final String APPLICABLE_SETS_SPECS = "test";
	
	
	private List<String> applicableSetSpecs;
	private List<String> keywords;
	
	public KeywordsFilter () {
		
		// request set's
		String[] array = super.getProperty("applicableSetsSpecs", APPLICABLE_SETS_SPECS).split(",");
		this.applicableSetSpecs = new ArrayList<String>();
		for (String string : array) {
			this.applicableSetSpecs.add(string.trim().toLowerCase());
		}
		// dc.rights possible values
		array = super.getProperty("keywords", KEYWORDS).split(",");
		this.keywords = new ArrayList<String>();
		for (String string : array) {
			this.keywords.add(string.trim().toLowerCase());
		}
	}
	
	@Override
	public boolean isItemFiltered(AgentItem filterItem) {
		if (!filterItem.hasInformation()) return true;
		DCValue[] values = filterItem.getInformation().item.getMetadata("dc", "subject", Item.ANY, Item.ANY);
		if (values == null) return true;
		boolean contain = false;
		for (DCValue value : values) {
			for (String string : this.keywords) {
				if (value.value.toLowerCase().contains(string)) {
					contain = true; break;
				}
			}
		}
		return !contain;
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
