package pt.keep.oaiextended.modifiers;

import org.dspace.content.DCValue;
import org.dspace.content.Item;

import pt.keep.oaiextended.AgentItem;
import pt.keep.oaiextended.Modifier;

/**
 * 
 * @author João Melo
 */
public class DcContributorAuthorModifier extends Modifier {
	public DcContributorAuthorModifier () {
	}

	@Override
	public void modifyItem(AgentItem oaiItem) {
		if (oaiItem.hasInformation()) {
			DCValue[] values = oaiItem.getInformation().item.getMetadata("dc","contributor","author",Item.ANY);
			oaiItem.getInformation().item.clearMetadata("dc", "contributor", "author", Item.ANY);
			for (DCValue value : values) {
				oaiItem.getInformation().item.addMetadata(value.schema, "creator", Item.ANY, value.language, value.value);
			}
		}
	}
}
