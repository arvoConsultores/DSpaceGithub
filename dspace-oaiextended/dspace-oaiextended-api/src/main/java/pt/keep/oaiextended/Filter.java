package pt.keep.oaiextended;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.LogManager;


/**
 * This abstract class determines the base functionality filters must contain.
 * Its also used for defining some important static methods.
 * 
 * @author João Melo
 */
public abstract class Filter extends Agent {
	private static Logger log = Logger.getLogger(Filter.class);
	public static final String DEFAULT_FILTERS = "pt.keep.oaiextended.filters.RightsFilter, pt.keep.oaiextended.filters.AnonymousReadFilter";
	public static final String AGENT_GROUP_NAME = "filters";
	
	/**
	 * Empty constructorpt.keep.oaidriver.
	 * Constructs the filter
	 */
	public Filter () {
		super();
	}
	/**
	 * Gets all filters existing inside defined package.
	 * This method
	 * 
	 * @param ctx Request Filter Context
	 * @return All known filters
	 */
	@SuppressWarnings("unchecked")
	public static List<Filter> getFilters(AgentContext ctx) {
		String filtrs = ConfigurationManager.getProperty(Agent.getPropertyName(AGENT_GROUP_NAME, "list"));
		if (filtrs == null) filtrs = DEFAULT_FILTERS;
		List<String> filters = Arrays.asList(filtrs.split(","));
		List<Filter> list = new ArrayList<Filter>();
		for (String string : filters) {
			try {
				Class cls = Class.forName(string.trim());
				Object obj = cls.newInstance();
				if (obj instanceof Filter) {
					Filter m = (Filter) obj;
					m.setContext(ctx);
					list.add(m);
				}
			} catch (ClassNotFoundException e) {
				log.info(LogManager.getHeader(null, "oaidriver_error",
                "Class "+string.trim()+" not found"),e);
			} catch (InstantiationException e) {
				log.info(LogManager.getHeader(null, "oaidriver_error",
						"Impossible to create instance of "+string.trim()),e);
			} catch (IllegalAccessException e) {
				log.info(LogManager.getHeader(null, "oaidriver_error",
						"Class "+string.trim()+" doesn't have empty constructor"),e);
			}
		}
		return list;
    }
	
	

	/**
	 * Determines if the item is filtered (not added to the list) by this filter
	 * 
	 * @param item Information about the item
	 * @return true - item filtered (doens't appear)
	 */
	public abstract boolean isItemFiltered(pt.keep.oaiextended.AgentItem item);
	

	/**
	 * Determines if the filter is applicable in the actual context.
	 * 
	 * @return true if applicable
	 */
	public abstract boolean isFilterApplicable ();
	
	/**
	 * Determines if the agent is applicable in the actual context.
	 * 
	 * @return true if applicable
	 */
	public boolean isApplicable () {
		return isFilterApplicable();
	}
	
	/**
	 * Gets the agent group name (used to get configuration values)
	 */
	public String getAgentGroupName () {
		return AGENT_GROUP_NAME;
	}
}
