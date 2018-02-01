package pt.keep.oaiextended;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dspace.core.LogManager;


/**
 * This class is used as the main class for the filtering operations.
 * 
 * @author João Melo
 */
public class AgentManager {
    private static Logger log = Logger.getLogger(AgentManager.class);
	private List<Filter> installedFilters;
	private List<Filter> applicableFilters;
	private List<Modifier> installedModifiers;
	private List<Modifier> applicableModifiers;
	private AgentContext context;
	
	/**
	 * Parameterized Contructor
	 * 
	 * @param context
	 */
	public AgentManager (AgentContext context) {
		
		this.context = context;
		this.applicableFilters = new ArrayList<Filter>();
		this.applicableModifiers = new ArrayList<Modifier>();
		
		this.getInstalledAgents();
		this.determineApplicableAgents();

        log.info(LogManager.getHeader(null, "oai_driver",
                "Modifiers Applied: "+this.applicableModifiers.size()+"/"+this.installedModifiers.size()));
        log.info(LogManager.getHeader(null, "oai_driver",
                "Filters Applied: "+this.applicableFilters.size()+"/"+this.installedFilters.size()));
		
		this.updateAgentsContext();
	}
	/**
	 * Creates and Add all known filters to the installed filters
	 */
	private void getInstalledAgents() {
		this.installedFilters = Filter.getFilters(this.context);
		this.installedModifiers = Modifier.getModifiers(this.context);
	}
	
	/**
	 * Choose only the applicable filters
	 */
	private void determineApplicableAgents () {
		for (Filter filter : this.installedFilters) {
			if (filter.isApplicable())
				this.applicableFilters.add(filter);
		}
		for (Modifier modifier : this.installedModifiers) {
			if (modifier.isApplicable())
				this.applicableModifiers.add(modifier);
		}
	}
	/**
	 * Apply context changes made by the filters applicable
	 * in the actual context.
	 */
	private void updateAgentsContext () {
		for (Filter filter : this.applicableFilters)
			filter.updateRequestContext();
		for (Modifier modifier : this.applicableModifiers)
			modifier.updateRequestContext();
	}
	/**
	 * Verifies if the item is filtered by some filter
	 * 
	 * @param item
	 * @return true if some filter is applied
	 */
	public boolean isAnyFilterApplied(AgentItem filterItem) {
		boolean itemFilteredBySomeFilter = false; 
		for (Filter filter : applicableFilters) {
			itemFilteredBySomeFilter |= filter.isItemFiltered(filterItem);
			if (itemFilteredBySomeFilter)
				break;
		}
		return itemFilteredBySomeFilter;
	}
	
	/**
	 * Changes the item, applying it all the applicable modifiers.
	 * 
	 * @param filterItem The item to modify
	 */
	public void applyModifiers (AgentItem filterItem) {
		// This modifiers will only be applied to filtered items.
		for (Modifier modifier : this.applicableModifiers) {
			modifier.modifyItem(filterItem);
		}
	}
}
