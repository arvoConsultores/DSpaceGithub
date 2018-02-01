package pt.keep.oaiextended;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.dspace.core.ConfigurationManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class is used as a wrapper for the actual request context
 * (may change in the future)
 * 
 * @author João Melo
 */
public class AgentContext {
    private static Logger log = Logger.getLogger(AgentContext.class);
	private String harvestSet;
	private String initialHarvestSet;
	private static List<String> vSets = null;
	/**
	 * Empty Constructor
	 */
	public AgentContext() {
		this.harvestSet = null;
	}
	/**
	 * Parameterized Constructor
	 * 
	 * @param harvestSet the set
	 */
	public AgentContext(String harvestSet) {
		this.harvestSet = harvestSet;
		this.initialHarvestSet = harvestSet;
	}
	/**
	 * Gets the set
	 * 
	 * @return the set
	 */
	public String getModifiedHarvestSet() {
		return harvestSet;
	}
	public String getHarvestSet() {
		return initialHarvestSet;
	}
	/**
	 * Sets the set
	 * 
	 * @param harvestSet set
	 */
	public void setHarvestSet(String harvestSet) {
		this.harvestSet = harvestSet;
	}
	
	
	/**
	 * Tells if the context has set defined
	 * @return true if defined
	 */
	public boolean hasHarvestSet () {
		return (this.initialHarvestSet != null);
	}
	
	private static String getSetSpec (Node node) {
    	if (node.getNodeType() == Node.ELEMENT_NODE)
    		return getSetSpec(node.getFirstChild());
    	else if (node.getNodeType() == Node.TEXT_NODE)
    		return node.getNodeValue();
    	return "";
    }
    
	/**
	 * Gets the list of virtual sets defined.
	 * @return List of virtual sets
	 */
    public static List<String> getVirtualSets() {
    	AgentContext.vSets = null;
    	if (AgentContext.vSets == null) {
	        String dir = ConfigurationManager.getProperty("dspace.dir");
			File file;
			List<String> array = new ArrayList<String>();
			if (dir != null) {
				if (dir.endsWith("/"))
					dir += "config/oaiextended/virtualSets.xml";
				else
					dir += "/config/oaiextended/virtualSets.xml";
				file = new File(dir);
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db;
				try {
					db = dbf.newDocumentBuilder();
					Document doc = db.parse(file);
					NodeList list = doc.getElementsByTagName("setSpec");
					int m = list.getLength();
					for (int i=0;i<m;i++)
						array.add(AgentContext.getSetSpec(list.item(i)));
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			AgentContext.vSets = array;
    	} 
		return AgentContext.vSets;
	}
    
    /**
     * 
     * @return true if the query has a virtual set
     */
    public boolean isVirtualSet () {
    	boolean answer = (this.hasHarvestSet() && AgentContext.isVirtual(this.getHarvestSet()));
    	return answer;
    }
    
	public static boolean isVirtual (String harvestSet) {
		List<String> vSets = AgentContext.getVirtualSets();
		for (String str : vSets) {
			if (str.trim().toLowerCase().equals(harvestSet.trim().toLowerCase()))
				return true;
		}
		return false;
	}
}
