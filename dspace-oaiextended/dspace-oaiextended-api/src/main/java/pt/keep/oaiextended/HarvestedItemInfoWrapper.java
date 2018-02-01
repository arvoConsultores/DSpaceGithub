package pt.keep.oaiextended;

import java.util.ArrayList;
import java.util.List;

import org.dspace.search.HarvestedItemInfo;

public class HarvestedItemInfoWrapper extends HarvestedItemInfo {
	public AgentContext agentContext;
	public List<AgentSet> sets;
	
	public static HarvestedItemInfoWrapper getInstance (HarvestedItemInfo info) {
		HarvestedItemInfoWrapper item = new HarvestedItemInfoWrapper();
		
		item.collectionHandles = info.collectionHandles;
		item.context = info.context;
		item.datestamp = info.datestamp;
		item.handle = info.handle;
		item.item = info.item;
		item.itemID = info.itemID;
		item.withdrawn = info.withdrawn;
		item.sets = new ArrayList<AgentSet>();
		
		return item;
	}
	
	public static HarvestedItemInfoWrapper getInstance (HarvestedItemInfo info, AgentContext context) {
		HarvestedItemInfoWrapper item = new HarvestedItemInfoWrapper();
		
		item.collectionHandles = info.collectionHandles;
		item.context = info.context;
		item.datestamp = info.datestamp;
		item.handle = info.handle;
		item.item = info.item;
		item.itemID = info.itemID;
		item.withdrawn = info.withdrawn;
		item.agentContext = context;
		item.sets = new ArrayList<AgentSet>();
		
		return item;
	}
}
