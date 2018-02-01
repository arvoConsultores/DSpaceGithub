package pt.keep.oaiextended;

import org.dspace.search.HarvestedItemInfo;

/**
 * This class will be used as a wrapper to the item information
 * (that could be modified in future)
 * 
 * @author João Melo
 */
public class AgentItem {
	private HarvestedItemInfo info;
	
	/**
	 * Parameterized Constructor
	 * 
	 * @param info Item Information
	 */
	public AgentItem (HarvestedItemInfo info) {
		this.info = info;
	}
	
	/**
	 * Gets the item information
	 * 
	 * @return Item Information
	 */
	public HarvestedItemInfo getInformation () {
		return this.info;
	}
	
	/**
	 * Verifies if this item has information
	 * 
	 * @return has this item information?
	 */
	public boolean hasInformation () {
		if (this.info == null) return false;
		if (this.info.item == null) return false;
		return true;
	}
}
