package pt.keep.oaiextended.modifiers;

import java.sql.SQLException;

import org.dspace.content.Bitstream;
import org.dspace.content.Bundle;
import org.dspace.content.Item;

import pt.keep.oaiextended.AgentItem;
import pt.keep.oaiextended.Modifier;

public class ListDcFormatModifier extends Modifier {
	
	public ListDcFormatModifier () {
		super();
	}

	@Override
	public void modifyItem(AgentItem oaiItem) {
		if (oaiItem.hasInformation()) {
			try {
				Bundle[] bundles = oaiItem.getInformation().item.getBundles("ORIGINAL");
				for (Bundle bundle : bundles) {
					Bitstream[] bitStreams = bundle.getBitstreams();
					for (Bitstream bitstream : bitStreams) {
						oaiItem.getInformation().item.addMetadata("dc", "format", Item.ANY, Item.ANY, bitstream.getFormat().getMIMEType());
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
