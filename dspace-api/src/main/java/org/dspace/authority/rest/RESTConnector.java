/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.authority.rest;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.dspace.authority.util.XMLUtils;
import org.dspace.services.ConfigurationService;
import org.glassfish.jersey.client.ClientConfig;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import java.io.InputStream;
import java.util.Scanner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;
import org.dspace.services.ConfigurationService;
import org.dspace.utils.DSpace;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

/**
 *
 * @author Antoine Snyers (antoine at atmire.com)
 * @author Kevin Van de Velde (kevin at atmire dot com)
 * @author Ben Bosman (ben at atmire dot com)
 * @author Mark Diggory (markd at atmire dot com)
 */
public class RESTConnector {

    /**
     * log4j logger
     */
    private static Logger log = Logger.getLogger(RESTConnector.class);

    private String url;
    
    // ARVO
    private ClientConfig clientConfig = null;

    public RESTConnector(String url) {
        this.url = url;
    }

    public Document get(String path) {
        Document document = null;

        InputStream result = null;
        path = trimSlashes(path);

        String fullPath = url + '/' + path;
        HttpGet httpGet = new HttpGet(fullPath);
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpResponse getResponse = httpClient.execute(httpGet);
            //do not close this httpClient
            result = getResponse.getEntity().getContent();
            document = XMLUtils.convertStreamToXML(result);

        } catch (Exception e) {
            getGotError(e, fullPath);
        }

        return document;
    }

    protected void getGotError(Exception e, String fullPath) {
        log.error("Error in rest connector for path: "+fullPath, e);
    }

    public static String trimSlashes(String path) {
        while (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        while (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }

    public static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
    public WebTarget getClientRest(String path) {
    	Client client = ClientBuilder.newClient(getClientConfig());
    	WebTarget target = client.target(url).path(path);
    	return target;
    }

	public ClientConfig getClientConfig() {
		if(this.clientConfig == null) {
	        ConfigurationService configurationService = new DSpace().getConfigurationService();
	        String proxyHost =  configurationService.getProperty("http.proxy.host");
	        int proxyPort = configurationService.getPropertyAsType("http.proxy.port", 80);
	        
	        this.clientConfig = new ClientConfig();
	        if(StringUtils.isNotBlank(proxyHost)){
	        	this.clientConfig.connectorProvider(new ApacheConnectorProvider());
	            this.clientConfig.property(ClientProperties.PROXY_URI, proxyHost + ":" + proxyPort);
	        }
		}
		return clientConfig;
	}

}
