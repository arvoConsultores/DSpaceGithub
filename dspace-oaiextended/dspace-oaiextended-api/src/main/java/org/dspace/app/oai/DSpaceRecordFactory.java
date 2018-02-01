/*
 * DSpaceRecordFactory.java
 *
 * Version: $Revision: 3705 $
 *
 * Date: $Date: 2009-04-11 17:02:24 +0000 (Sat, 11 Apr 2009) $
 *
 * Copyright (c) 2002-2005, Hewlett-Packard Company and Massachusetts
 * Institute of Technology.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * - Neither the name of the Hewlett-Packard Company nor the name of the
 * Massachusetts Institute of Technology nor the names of their
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */
package org.dspace.app.oai;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.dspace.content.DCDate;
import org.dspace.core.ConfigurationManager;
import org.dspace.search.HarvestedItemInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import pt.keep.oaiextended.AgentContext;
import pt.keep.oaiextended.AgentItem;
import pt.keep.oaiextended.AgentManager;
import pt.keep.oaiextended.AgentSet;
import pt.keep.oaiextended.HarvestedItemInfoWrapper;

import ORG.oclc.oai.server.catalog.RecordFactory;
import ORG.oclc.oai.server.verb.CannotDisseminateFormatException;

/**
 * Implementation of the OAICat RecordFactory base class for DSpace items.
 * 
 * @author Robert Tansley
 * @version $Revision: 3705 $
 */
public class DSpaceRecordFactory extends RecordFactory
{
    /** log4j category */
    private static Logger log = Logger.getLogger(DSpaceRecordFactory.class);

    public DSpaceRecordFactory(Properties properties)
    {
        // We don't use the OAICat properties; pass on up
        super(properties);
    }

    public String fromOAIIdentifier(String identifier)
    {
        // Our local identifier is actually the same as the OAI one (the Handle)
        return identifier;
    }

    public String quickCreate(Object nativeItem, String schemaURL,
            String metadataPrefix) throws IllegalArgumentException,
            CannotDisseminateFormatException
    {
        // Not supported
        return null;
    }

    public String getOAIIdentifier(Object nativeItem)
    {
        String h = DSpaceOAICatalog.OAI_ID_PREFIX
                + ((HarvestedItemInfo) nativeItem).handle;

        return h;
    }

    public String getDatestamp(Object nativeItem)
    {
        Date d = ((HarvestedItemInfo) nativeItem).datestamp;

        // Return as ISO8601
        return new DCDate(d).toString();
    }

    public Iterator getSetSpecs(Object nativeItem)
    {
    	HarvestedItemInfoWrapper hii = null;
    	Iterator i = null;
    	if (nativeItem instanceof HarvestedItemInfoWrapper) {
    		hii = (HarvestedItemInfoWrapper) nativeItem;
    		i = hii.sets.iterator();
    	} else {
    		i = ((HarvestedItemInfo) nativeItem).collectionHandles.iterator();
    	}
        List setSpecs = new LinkedList();

        // Convert the DB Handle string 123.456/789 to the OAI-friendly
        // hdl_123.456/789
        while (i.hasNext())
        {
        	Object obj = i.next();
        	if (obj instanceof String) {
        		String handle = "hdl_" + (String) obj;
        		setSpecs.add(handle.replace('/', '_'));
        	} else if (obj instanceof AgentSet) {
        		AgentSet set = (AgentSet) obj;
        		if (set.isVirtual()) {
        			setSpecs.add(set.getName());
        		} else {
        			String handle = "hdl_" + set.getName();
        			setSpecs.add(handle.replace('/', '_'));
        		}
        	}
        }
        
        
        /**
        if (hii != null) {
	        if (hii.agentContext != null) {
	        	if (hii.agentContext.isVirtualSet()) {
	        		for (String set : hii.agentContext.getVirtualSets())
	        			setSpecs.add(set);
	        	}
	        }
        }
		**/
        return setSpecs.iterator();
    }

    public boolean isDeleted(Object nativeItem)
    {
        HarvestedItemInfo hii = (HarvestedItemInfo) nativeItem;

        return hii.withdrawn;
    }

    public Iterator getAbouts(Object nativeItem)
    {
        // Nothing in the about section for now
        return new LinkedList().iterator();
    }
}
