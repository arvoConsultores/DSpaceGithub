/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.bibliography.export;

import java.sql.SQLException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.dspace.content.DCDate;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.service.ItemService;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.handle.factory.HandleServiceFactory;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;

/**
 * Generation of the content of a RIS file of an item with the metadata stored in the repository. 
 * The correspondence between metadata and RIS tags can be configured in configuration files 
 * mapMetadata-RIS.cfg and mapTypes.cfg.
 *  
 * @author Marta Rodriguez
 *
 */
public class RISExporter {
	protected static ConfigurationService configService = DSpaceServicesFactory.getInstance().getConfigurationService();
	private static String schema = configService.getProperty("RIS.schema");
	private static final String RIS_HEAD = "TY  - ";
	private static final String RIS_FOOT = "ER  - ";
	private static final String SEPARATOR = ":";
	private static final List<String> DATA_TYPES= Arrays.asList("text", "name", "year", "date", "other", "charlimit");
	private static final int CHARLIMIT=255;
	
	/**
	 * Extracts the information of an item to generate  String that with the content of a RIS file.
	 * @param handle: Handle if the item.
	 * @param context: current context.
	 * @return String that contains RIS information.
	 */
    public static String renderRisFormat(String handle, Context context) {
		try {
			DSpaceObject dso = HandleServiceFactory.getInstance().getHandleService().resolveToObject(context, handle);

	 	    if (dso.getType() == Constants.ITEM) {
	 	    	StringBuffer sb = new StringBuffer();
	 	    	Item item = (Item) dso;
	 	    	
	 	    	//Get RIS type:
	 	    	String risType = getType(item);
	 	    	sb.append(RIS_HEAD + risType);
	 	    	sb.append(" \n");
	 	    	
 			    //Get RIS fields:
	 	    	ItemService is = item.getItemService();
			    List<MetadataValue> allMetadata=is.getMetadata(item, schema, Item.ANY, Item.ANY, Item.ANY);
			    
 			    //List of used tags:
 			    List<String> usedtags=new ArrayList<String>();
 			    Iterator<MetadataValue> it = allMetadata.iterator();
 			    
 			    while (it.hasNext()){
 			    	MetadataValue metadata = it.next();
 			    	String metadataName= metadata.getMetadataField().toString();
 			    	metadataName = metadataName.replaceAll("_", ".");
 			    	
 			    	//Check if a specific metadata for this type exists. If it doesn't, use the generic:
 			    	String value = configService.getProperty("RIS."+risType+"."+metadataName);
 			    	if (value==null||value.isEmpty())
 			    		value = configService.getProperty("RIS."+metadataName);

 			    	if (value!=null && !value.isEmpty()){
 			    		String tag=value;
 			    		if (value.indexOf(SEPARATOR)!=-1){
 			    			tag=value.substring(0, value.indexOf(SEPARATOR));
 			    			value = value.substring(value.indexOf(SEPARATOR)+1);
 			    		}
 			    		List<String> tags = Arrays.asList(value.split(SEPARATOR));
 			    		Boolean single=tags.contains("single");

 			    		if ((single && !usedtags.contains(tag))){
 			    			sb.append(tag + "  - " + processData(metadata.getValue(),tags));
 	    					sb.append("\n");
 	    					usedtags.add(tag);
 			    		}else if (!single){
 			    			sb.append(tag + "  - " + processData(metadata.getValue(),tags));
 	    					sb.append("\n");
 			    		}
 			    	}
 			    }
 			    
 			    	
 			    sb.append(RIS_FOOT + "\n\n");
	 			

		 		String RisData = sb.toString();
		 		return RisData;
		 	    } 
		 	   
		} catch (IllegalStateException | SQLException e) {
			//items=new Item[0];//TODO Cambiar
		}
		return null;
		
    }
/**
 * Gets the RIS type of this item. In case there is not an equivalent, returns the default type set on 
 * the configuration file.
 * @param item
 * @return RIS type
 */
	private static String getType(Item item) {
		String risType="";
		boolean typeFound = false;
		    
		    ItemService is = item.getItemService();
		    List<MetadataValue> types=is.getMetadataByMetadataString(item, schema+".type");
		    Iterator<MetadataValue> it = types.iterator();
		    
		    while(it.hasNext()){
		    	MetadataValue mv = it.next();
		    	String value = mv.getValue();
		    	// Look if there's a type in the type map
				if (value!=null && !value.isEmpty()){
					risType=configService.getProperty("RIS."+value.replaceAll("\\s", "").toLowerCase());
					if (risType!=null && !risType.isEmpty()){
						typeFound = true;
					}
				}
		    }
		    // Set type in case no type is given
		    if (!typeFound) {
		    	risType=configService.getProperty("RIS.default.type");
		    }
		    return risType;
	}

	/**
	 * Function that processes the introduced value depending on the type of data specified in the tags.
	 * @param value: Value of the metadata
	 * @param dataTypes: List that contains the datat types associated to this value.
	 */
	private static String processData(String value, List<String> dataTypes) {
		String outData=value;
		Iterator<String> it =dataTypes.iterator();
		while(it.hasNext()){
			String tag= it.next();
			if (DATA_TYPES.contains(tag)){
				switch(tag){
					case "date":{
						DCDate dcDate = new DCDate(value);
						int month = dcDate.getMonth();
						int year=dcDate.getYear();
						outData = Integer.toString(year);
						
						if (month!=-1){
							String mm=Integer.toString(month);
							if (mm.length()==1)
								mm="0"+mm;
							outData=outData+"/"+mm;
							int day = dcDate.getDay();
							if (day!=-1){
								String dd=Integer.toString(month);
								if (dd.length()==1)
									dd="0"+dd;
								outData=outData+"/"+dd;
							}
						}
						break;
					} case "year":{
						outData = Integer.toString(new DCDate(value).getYear());
						break;
					} case "charlimit":{
						if (outData.length()>CHARLIMIT-3){
							outData=outData.substring(0,CHARLIMIT-3);
							int spaceIndex=outData.lastIndexOf(" ");
							if (spaceIndex!=-1)
								outData=outData.substring(0, spaceIndex);
							outData = outData+"...";
						}
						break;
					} case "text": {
						outData= Normalizer.normalize(value, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
						outData=outData.replaceAll("\\*", "");
						break;
					}case "name":{	
						//Asterisks are not allowed in come fields like Author or Periodical names.
						outData=outData.replaceAll("\\*", "");
						break;
					}
				}
			}
		}
		return outData;
	}
}