package pt.keep.oaiextended;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.LogManager;

public abstract class Modifier extends Agent {
	private static Logger log = Logger.getLogger(Modifier.class);
	public static final String DEFAULT_MODIFIERS = "pt.keep.oaiextended.modifiers.DateShownModifier, pt.keep.oaidriver.modifiers.DcTypePrefixModifier";
	public static final String AGENT_GROUP_NAME = "modifiers";
	public static final String APPLICABLE_SETS_SPECS = "driver";

	private List<String> applicableSetSpecs;
	
	public Modifier () {
		this.applicableSetSpecs = new ArrayList<String>();
		String[] array = super.getProperty("applicableSetsSpecs", APPLICABLE_SETS_SPECS).split(",");
		for (String string : array) {
			this.applicableSetSpecs.add(string.trim().toLowerCase());
		}
	}
	
	/**
	 * Gets the list of modifiers
	 * 
	 * @param context
	 * @return 
	 */
	@SuppressWarnings("unchecked")
	public static List<Modifier> getModifiers (AgentContext context) {
		String modifs = ConfigurationManager.getProperty(Agent.getPropertyName(AGENT_GROUP_NAME, "list"));
		if (modifs == null) modifs = DEFAULT_MODIFIERS;
		List<String> modifiers = Arrays.asList(modifs.split(","));
		List<Modifier> list = new ArrayList<Modifier>();
		for (String string : modifiers) {
			try {
				Class cls = Class.forName(string.trim());
				Object obj = cls.newInstance();
				if (obj instanceof Modifier) {
					Modifier m = (Modifier) obj;
					m.setContext(context);
					list.add(m);
				}
			} catch (ClassNotFoundException e) {
				log.debug(LogManager.getHeader(null, "oaidriver_error",
                "Class "+string.trim()+" not found"),e);
			} catch (InstantiationException e) {
				log.debug(LogManager.getHeader(null, "oaidriver_error",
                "Class "+string.trim()+" doesn't have empty constructor"),e);
			} catch (IllegalAccessException e) {
				log.debug(LogManager.getHeader(null, "oaidriver_error",
		                "Impossible to create instance of "+string.trim()),e);
			}
		}
		return list;
	}
	
	/**
	 * Determines if the agent is applicable in the actual context.
	 * 
	 * @return true if applicable
	 */
	public boolean isApplicable () {
		if (!super.getContext().isVirtualSet()) return false;
		if (this.applicableSetSpecs.contains(super.getContext().getHarvestSet().toLowerCase())) return true;
		return false;
	}

	/**
	 * Gets the agent group name (used to get configuration values)
	 */
	public String getAgentGroupName () {
		return AGENT_GROUP_NAME;
	}
	/**
	 * This method is used to modify the output information
	 * 
	 * @param oaiItem
	 */
	public abstract void modifyItem (AgentItem oaiItem);
	
	/**
	 * Updates the context. When the query has a virtual set defined.
	 * See documentation to get more information about virtual sets.
	 */
	public void updateContext () {
		if (this.isApplicable())
			super.getContext().setHarvestSet(null);
	}
}
