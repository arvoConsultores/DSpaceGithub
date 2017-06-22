/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.xmlui.aspect.eperson;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.AbstractAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.environment.http.HttpEnvironment;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.log4j.Logger;
import org.dspace.app.xmlui.utils.AuthenticationUtil;
import org.dspace.app.xmlui.utils.ContextUtil;
import org.dspace.authorize.AuthorizeException;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.core.LogManager;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;

import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gson.Gson;

/**
 * 
 * It tries to authenticate based on the credentials of Google. The action uses
 * the parameters of supplier hhtp by Google.
 * 
 * If the authentication attempt is successful, then a redirect indicated in the
 * URL will be performed.
 * 
 * If the authentication fails, the action returns null.
 * 
 * @author Sergio Lopez Gonzalez at arvo.es
 *
 */
public class GoogleAction extends AbstractAction {

	private static Logger log = Logger.getLogger(GoogleAction.class);
	private String code = null;

	/**
	 * Attempt to authenticate the user. 
	 */
	public Map act(Redirector redirector, SourceResolver resolver,
			Map objectModel, String source, Parameters parameters)
			throws Exception {
		Request request = ObjectModelHelper.getRequest(objectModel);
		Context context = ContextUtil.obtainContext(objectModel);

		code = request.getParameter("code");
		String state = request.getParameter("state");
		String error = request.getParameter("error");
		if (code != null && error == null) {
			Gson gson = new Gson();
			String accessToken = executeAccessTokenRequest(code);
			/*
			 * Access token:
			 * ya29.GlsXBP2h0zeeDhmAuoi49AbSICycYEwAKmB4PR6p1MefOzZ
			 * -ARTW5LWByJgxjmPJX8CarH
			 * -n89KvrYlOK7WpQF7KuSE_UPUd6154d-VstuYFwb8nbRyliuQWqxHV
			 */
			if (accessToken != null) {
				String curlResponse = StringUtils.newStringUtf8(executePersonalDetails(accessToken).getBytes());
//				curlResponse=new String(Charset.forName("UTF-8").encode(curlResponse).array());
				/*
				 * curlResponse: 
				 {
 					"id": "",
 					"email": "",
 					"verified_email": true,
 					"name": "",
 					"given_name": "",
 					"family_name": "",
 					"link": "",
 					"picture": ".jpg",
 					"gender": "male",
 					"locale": "es"
				}
				 */
				es.arvo.google.GooglePersonalDetailsVO datos = gson.fromJson(curlResponse, es.arvo.google.GooglePersonalDetailsVO.class);
				String email = datos.getEmail();
				EPerson eperson = null;
				if (email != null) {
					context.turnOffAuthorisationSystem();
					eperson = EPerson.findByEmail(context, email);
					if (eperson == null) {
						eperson = EPerson.create(context);
						eperson.setEmail(email);
						eperson.update();
						context.commit();
					}
					eperson.setFirstName(datos.getName());
					context.restoreAuthSystemState();

					assignGroups(context);

					final HttpServletResponse httpResponse = (HttpServletResponse) objectModel.get(HttpEnvironment.HTTP_RESPONSE_OBJECT);

					String redirectURL = request.getContextPath();

					if (AuthenticationUtil.isInterupptedRequest(objectModel)) {
						redirectURL += AuthenticationUtil.resumeInterruptedRequest(objectModel);
					} else {
						String loginRedirect = ConfigurationManager.getProperty("xmlui.user.loginredirect");
						redirectURL += (loginRedirect != null) ? loginRedirect.trim() : "/";
					}
					httpResponse.sendRedirect(redirectURL);
					request.setAttribute("eperson", eperson);
					context.setCurrentUser(null);
					AuthenticationUtil.authenticate(objectModel, null, null,null);
					return new HashMap();
				}
			}
		}
		return null;
	}

	/**
	 * Return the information of the user provides by google.
	 * 
	 * @param accessToken
	 * @return
	 * @throws MalformedURLException 
	 */
	private String executePersonalDetails(String accessToken) throws IOException{
		StringBuilder page = new StringBuilder(ConfigurationManager.getProperty("authentication-google", "google.personaldetails.url"));
		
		URL url = new URL(page.toString());

		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Authorization", "Bearer " + accessToken);
		// read the response
		BufferedReader input = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String c;
		StringBuilder resultBuf = new StringBuilder();
		while ((c = input.readLine()) != null){
			resultBuf.append(c);
		}
		input.close();
		return resultBuf.toString();
	}

	/**
	 * Add authenticated users to the group defined in authentication-google.cfg
	 * 
	 * @param context The user context
	 */
	private void assignGroups(Context context) {
		int i = 1;
		String groupMap = ConfigurationManager.getProperty("authentication-google", "login.groupmap" + i);

		while (groupMap != null) {
			String t[] = groupMap.split(":");
			String dspaceGroupName = t[1];

			try {
				Group googleGroup = Group.findByName(context, dspaceGroupName);
				if (googleGroup != null) {
					googleGroup.addMember(context.getCurrentUser());
					googleGroup.update();
					context.commit();
				}
			} catch (SQLException e) {
				log.debug(LogManager.getHeader(context, "Could not find group",dspaceGroupName));
			} catch (AuthorizeException e) {
				log.debug(LogManager.getHeader(context,"Could not authorize addition to group",dspaceGroupName));
			}

		}

	}

	/**
	 * Confirm that the token returned by google is valid.
	 * 
	 * @param code The code needed to validate the user
	 * @return The access token provides by Google
	 * @throws IOException
	 */
	private String executeAccessTokenRequest(String code) throws IOException {
		TokenResponse response = null;
		try {
			response = new AuthorizationCodeTokenRequest( new NetHttpTransport(),
					new JacksonFactory(),
					new GenericUrl(ConfigurationManager.getProperty("authentication-google", "google.generic.url")),code)
					.setRedirectUri(ConfigurationManager.getProperty("authentication-google", "google.callback.uri"))
					.set("client_id", ConfigurationManager.getProperty("authentication-google", "google.client.id"))
					.set("client_secret", ConfigurationManager.getProperty( "authentication-google", "google.client.secret"))
					 .set("scope","")
					.set("grant_type", "authorization_code").execute();
		} catch (TokenResponseException e) {
			if (e.getDetails() != null) {
				System.err.println("Error: " + e.getDetails().getError());
				if (e.getDetails().getErrorDescription() != null) {
					System.err.println(e.getDetails().getErrorDescription());
				}
				if (e.getDetails().getErrorUri() != null) {
					System.err.println(e.getDetails().getErrorUri());
				}
			} else {
				System.err.println(e.getMessage());
			}
		}
		if (response != null)
			return response.getAccessToken();
		else
			return null;
	}

	/**
	 * 
	 * @param context The user context
	 * @return
	 */
	public String loginPageTitle(Context context) {
		return "org.dspace.authenticate.GoogleAuthentication.title";
	}

}
