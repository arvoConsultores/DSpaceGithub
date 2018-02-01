package pt.keep.oaiextended.filters;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.AuthorizeManager;
import org.dspace.content.Bitstream;
import org.dspace.content.Bundle;
import org.dspace.core.Constants;
import org.dspace.core.Context;

import pt.keep.oaiextended.AgentItem;
import pt.keep.oaiextended.Filter;

/**
 * This filter will query the database being able to filter items by 
 * it's visibility (anonymous read).
 * 
 * @author João Melo
 */
public class AnonymousReadFilter extends Filter {
	public static final String APPLICABLE_SETS_SPECS = "driver";
	private List<String> applicableSetsSpecs;
	
	public AnonymousReadFilter () {
		String[] array = super.getProperty("applicableSetsSpecs", APPLICABLE_SETS_SPECS).split(",");
		this.applicableSetsSpecs = new ArrayList<String>();
		for (String string : array) {
			this.applicableSetsSpecs.add(string.trim().toLowerCase());
		}
	}
	@Override
	public boolean isFilterApplicable() {
		if (super.getContext().isVirtualSet() &&
			this.applicableSetsSpecs.contains(super.getContext().getHarvestSet().toLowerCase()))
				return true;
		return false;
	}
	@Override
	public boolean isItemFiltered(AgentItem item) {
		try {
			Context ctx = item.getInformation().context;
			ctx.setCurrentUser(null);
			Bundle[] bundles = item.getInformation().item.getBundles();
			Bitstream[] bitstreams;
			for (Bundle bundle : bundles) {
				bitstreams = bundle.getBitstreams();
				for (Bitstream bitstream : bitstreams) {
					AuthorizeManager.authorizeAction(ctx, bitstream, Constants.READ);
				}
			}
			return false;
		} catch (AuthorizeException e) {
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	protected void updateContext() {
		super.getContext().setHarvestSet(null);
	}
}
