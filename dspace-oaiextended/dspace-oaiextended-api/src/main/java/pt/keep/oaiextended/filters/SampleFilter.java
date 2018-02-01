package pt.keep.oaiextended.filters;

import java.util.ArrayList;
import java.util.List;

import org.dspace.content.DCValue;
import pt.keep.oaiextended.AgentItem;
import pt.keep.oaiextended.Filter;

public class SampleFilter extends Filter {
	public static final String APPLICABLE_SETS_SPECS = "driver";

	private List<String> applicableSetSpecs;
	
	public SampleFilter () {
		String[] array = super.getProperty("applicableSetsSpecs", APPLICABLE_SETS_SPECS).split(",");
		this.applicableSetSpecs = new ArrayList<String>();
		for (String string : array) {
			this.applicableSetSpecs.add(string.trim().toLowerCase());
		}
	}
	
	@Override
	public boolean isItemFiltered(AgentItem item) {
		boolean result = false;
		// TODO: Something
		return result;
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
