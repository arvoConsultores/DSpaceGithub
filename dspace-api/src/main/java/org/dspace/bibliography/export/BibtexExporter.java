/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.bibliography.export;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.dspace.content.Collection;
import org.dspace.content.DCDate;
import org.dspace.content.DCValue;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.ItemIterator;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.core.Utils;
import org.dspace.handle.HandleManager;

/**
 * Class that generates the content of a BibTeX file of an item or collection using the stored information 
 * in its the metadata.
 *  
 * @author Marta Rodriguez
 *
 */
@SuppressWarnings("deprecation")
public class BibtexExporter {
	private static final String schema = ConfigurationManager.getProperty("map-bibtex", "schema");
	private static final String BIBTEX_HEAD = "@";
	private static final String BIBTEX_OPEN="{";
	private static final String BIBTEX_CLOSE = "}";
	private static final String SEPARATOR = ":";
	private static final List<String> DATA_TYPES= Arrays.asList("text", "date");
/**
 * Extracts the information of an item to generate a String that contains the BibTeX information.
 * @param handle: Handle of the item or collection.
 * @param context: current context.
 * @param local: current Locale.
 * @return
 */
    public static String renderBibtexFormat(String handle, Context context) {
    	Item[] items;
		try {
			DSpaceObject dso = HandleManager.resolveToObject(context, handle);
			 ArrayList<Item> list = new ArrayList<Item>();
		 	    if (dso.getType() == Constants.ITEM) {
		 	    	list.add((Item) dso);
		 	    } else if (dso.getType() == Constants.COLLECTION) {
			 		ItemIterator it = ((Collection) dso).getItems();
			 		while (it.hasNext()) {
			 		    list.add(it.next());
			 		}
		 	    }
		 	   items = list.toArray(new Item[list.size()]);
		} catch (IllegalStateException e) {
			items=new Item[0];
		} catch (SQLException e){
			items=new Item[0];
		}
		
		StringBuffer sb = new StringBuffer();
	
		for (int i = 0; i < items.length; i++) {
		    boolean typeFound = false;
		    Item item = items[i];
		    
		    DCValue[] types = item.getMetadata(schema, "type", Item.ANY, Item.ANY);
		    String bibtexType="";
		    
			for (int j = 0; (j < types.length) && !typeFound; j++) {
				String type = types[j].value;
				
				// Look if there's a type in the type map
				if (type!=null && !type.isEmpty()){
					String prefix = ConfigurationManager.getProperty("map-bibtex", "BIBTEX.type.prefix");
					if (prefix!=null){
						try{
							type=type.substring(prefix.length());
						}catch (IndexOutOfBoundsException e){}
					}
					bibtexType=ConfigurationManager.getProperty("map-bibtex", "BIBTEX.type."+type.replaceAll("\\s", "").toLowerCase());
					if (bibtexType!=null && !bibtexType.isEmpty())
						typeFound = true;
				}
			}
	
		    // Set type in case no type is given
		    if (!typeFound) {
		    	bibtexType=ConfigurationManager.getProperty("map-bibtex","BIBTEX.type.default");
		    }
		    sb.append(BIBTEX_HEAD + bibtexType + BIBTEX_OPEN);
		    
		    // Citation key -> handle
		    sb.append(handle + ",\n");
		    
		    //Authors:
		    List<String> authors= new ArrayList<String>();
		    
		    DCValue[] allmetadata = item.getMetadata(Item.ANY, Item.ANY, Item.ANY, Item.ANY);
		    //List of used tags:
		    List<String> usedtags=new ArrayList<String>();
		    
		    for (int k=0;k<allmetadata.length;k++){
		    	DCValue metadata = allmetadata[k];
		    	String field=metadata.schema + "." + metadata.element;		    	
		    	if (metadata.qualifier!=null)
		    		field=field + "." + metadata.qualifier;
		    	
		    	//Check if a specific metadata for this type exists. If it doesn't, use the generic:
		    	String value = ConfigurationManager.getProperty("map-bibtex", "BIBTEX."+field);
		    	if (value!=null && !value.isEmpty()){
		    		String bibtexTag=value;
		    		if (value.indexOf(SEPARATOR)!=-1){
		    			bibtexTag=value.substring(0, value.indexOf(SEPARATOR));
		    			value = value.substring(value.indexOf(SEPARATOR)+1);
		    		}
		    		List<String> tags = Arrays.asList(value.split(SEPARATOR));
		    		Boolean single=tags.contains("single");

		    		if (!bibtexTag.equals("author")){
			    		if ((single && !usedtags.contains(bibtexTag))){
			    			sb.append(processData(bibtexTag, metadata.value,tags));
	    					usedtags.add(bibtexTag);
			    		}else if (!single){
			    			sb.append(processData(bibtexTag, metadata.value,tags));
			    		}
		    		}else{
		    			authors.add(metadata.value);
		    		}
		    	}
		    }
		    sb.append(includeAuthors(authors));
		    sb.append(BIBTEX_CLOSE);
		}

	String bibtexData = sb.toString();
	return bibtexData;
    }
    
    /**
     * Generates the property-value string in the BibTeX format.
     * @param tag: BibTeX tag.
     * @param value: Value of the field.
     */
    private static String generateProperty(String tag, String value){
    	return tag + " = {" + value + "},\n";
    }

    /**
     * Function that creates the string for the list of Authors to a BibTeX format.
     * @param authors: List of Authors's names.
     */
	private static String includeAuthors(List<String> authors) {
		String output="";		
		Iterator<String> iterator = authors.iterator();
		while (iterator.hasNext()){
			String author = iterator.next();
			output = output + author;
			if (iterator.hasNext())
				output = output + " and ";
		}
		output = generateProperty("author",output);
		return output;
	}

	/**
	 * Function that processes the introduced value depending on the type of data specified in the tags.
	 * @param bibtexTag: BibTeX tag.
	 * @param value: Value of the metadata.
	 * @param dataTypes: List that contains the data types associated to this value.
	 */
	private static String processData(String bibtexTag, String value, List<String> dataTypes) {
		String outData=generateProperty(bibtexTag,value);
		Iterator<String> it =dataTypes.iterator();
		while(it.hasNext()){
			String tag= it.next();
			if (DATA_TYPES.contains(tag)){
				if (tag.equals("date")){
					DCDate dcDate = new DCDate(value);
					int month = dcDate.getMonth();
					int year=dcDate.getYear();
					outData="";
					
					if (year!=-1){
						outData = generateProperty("year",Integer.toString(year));
					}						
					if (month>=1 && month<=12){
						outData = outData + generateProperty("month",Integer.toString(month));
					}
				} else{
					outData=Utils.addEntities(outData);
				}
			}
		}
		return outData;
	}
}
