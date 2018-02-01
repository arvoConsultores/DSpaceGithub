package pt.keep.oaiextended;

import org.apache.log4j.Logger;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.LogManager;

/**
 * This class is used to group Filters and Modifiers, because, they have
 * the same base functionality.
 * 
 * @author João Melo
 */
public abstract class Agent {
    private static Logger log = Logger.getLogger(Agent.class);
	/**
	 * Gets the property name in configuration file. Used as a wrapper to maintain the
	 * dspace.cfg structure.
	 * 
	 * @param agent Agent
	 * @param property Property name (suffix only)
	 * @return The complete property name
	 */
	public static String getPropertyName (Agent agent, String property) {
		return "oaiextended."+agent.getAgentGroupName()+"."+agent.getName()+"."+property;
	}
	/**
	 * Gets the property name in configuration file. Used as a wrapper to maintain the
	 * dspace.cfg structure.
	 * 
	 * @param agetnGroup the agent group name
	 * @param property Property name (suffix only)
	 * @return The complete property name
	 */
	public static String getPropertyName (String agentGroup, String property) {
		return "oaiextended."+agentGroup+"."+property;
	}
	
	private AgentContext context;
	
	/**
	 * Constructs the abstract class Agent
	 * 
	 * @param context Actual Request Context
	 */
	public Agent() {
		
	}

	/**
	 * Gets the context of the actual request
	 * 
	 * @return Actual request context
	 */
	public AgentContext getContext() {
		return context;
	}
	/**
	 * Sets the request context
	 * 
	 * @param context
	 */
	protected void setContext (AgentContext context) {
		this.context = context;
	}
	/**
	 * The name of the Agent
	 * 
	 * @return Name
	 */
	public String getName () {
		return this.getClass().getSimpleName();
	}
	
	/**
	 * Gets the configuration value (in the dspace.cfg file), this method
	 * is use as a wrapper. It appends the 'oaidriver.<agent-group>.<filterName>.' prefix.
	 * 
	 * @param property Property (only the suffix)
	 * @param defaultValue (value if the configuration file doesnt contains the date)
	 * @return Definition
	 */
	public String getProperty (String property, String defaultValue) {
		String value = ConfigurationManager.getProperty(Agent.getPropertyName(this, property));
		if (value == null) return defaultValue;
		else return value;
	}
	/**
	 * Gets the configuration value (in the dspace.cfg file), this method
	 * is use as a wrapper. It appends the 'oaidriver.filter.<filterName>.' prefix.
	 * 
	 * @param property Property (only the suffix)
	 * @param defaultValue (value if the configuration file doesnt contains the date)
	 * @return Definition
	 */
	public boolean getProperty (String property, boolean defaultValue) {
		boolean value = ConfigurationManager.getBooleanProperty(Agent.getPropertyName(this, property),defaultValue);
		return value;
	}
	/**
	 * Gets the Agent group (filters, modifiers, etc)
	 * 
	 * @return Gets the name
	 */
	protected abstract String getAgentGroupName ();
	/**
	 * Determines if this Agent is applicable to the actual context
	 * @return true if this Agent will be used to filter the results
	 */
	public abstract boolean isApplicable();
	
	/**
	 * Determines if the agent is applicable in the actual context.
	 * 
	 * @return true if applicable
	 */
	public boolean isAgentApplicable () {
		return this.isApplicable();
	}
	
	/**
	 * Some agents may need to modify the request context. Agents must use
	 * this method to do that.
	 */
	protected abstract void updateContext();
	
	/**
	 * This method is used as a wrapper, because the context can only be modified 
	 * by applicable filters.
	 */
	public void updateRequestContext () {
		if (this.isAgentApplicable()) this.updateContext();
	}
}
