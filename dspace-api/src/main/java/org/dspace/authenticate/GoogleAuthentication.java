/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.authenticate;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.jackson.JacksonFactory;

import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;

/**
 * 
 * 
 * 
 * @author Sergio Lopez Gonzalez at arvo.es
 *
 */
public class GoogleAuthentication implements AuthenticationMethod
{
	private static final String CLIENT_ID = ConfigurationManager.getProperty("authentication-google","google.client.id");
	private static final String CLIENT_SECRET = ConfigurationManager.getProperty("authentication-google","google.client.secret");
	private static final String CALLBACK_URI = ConfigurationManager.getProperty("authentication-google", "google.callback.uri");
	
	// start google authentication constants
	private static final Iterable<String> SCOPE = Arrays.asList("https://www.googleapis.com/auth/userinfo.profile;https://www.googleapis.com/auth/userinfo.email".split(";"));
	private static final String USER_INFO_URL = ConfigurationManager.getProperty("authentication-google", "google.userinfo.url");
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	// end google authentication constants
	
	private String stateToken;
	
	private final GoogleAuthorizationCodeFlow flow;
	
	/**
	 * Constructor initializes the Google Authorization Code Flow with CLIENT ID, SECRET, and SCOPE 
	 */
	public GoogleAuthentication() {
		flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT,JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, (Collection<String>) SCOPE).build();
		generateStateToken();
	}

	/**
	 * Builds a login URL based on client ID, secret, callback URI, and scope 
	 */
	public String buildLoginUrl() {
		final GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();
		return url.setRedirectUri(CALLBACK_URI).setState(stateToken).build();
	}
	
	/**
	 * Generates a secure state token 
	 */
	private void generateStateToken(){
		SecureRandom sr1 = new SecureRandom();
		stateToken = "google;"+sr1.nextInt();
	}
	
	/**
	 * Accessor for state token
	 */
	public String getStateToken(){
		return stateToken;
	}
	
	/**
	 * Expects an Authentication Code, and makes an authenticated request for the user's profile information
	 * @return JSON formatted user profile information
	 * @param authCode authentication code provided by google
	 */
	public String getUserInfoJson(final String authCode) throws IOException {

		final GoogleTokenResponse response = flow.newTokenRequest(authCode).setRedirectUri(CALLBACK_URI).execute();
		final Credential credential = flow.createAndStoreCredential(response, null);
		final HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(credential);
		// Make an authenticated request
		final GenericUrl url = new GenericUrl(USER_INFO_URL);
		final HttpRequest request = requestFactory.buildGetRequest(url);
		request.getHeaders().setContentType("application/json");
		final String jsonIdentity = request.execute().parseAsString();

		return jsonIdentity;

	}

	@Override
	public boolean canSelfRegister(Context context, HttpServletRequest request,
			String username) throws SQLException {
		return false;
	}

	@Override
	public void initEPerson(Context context, HttpServletRequest request,
			EPerson eperson) throws SQLException {
	}

	@Override
	public boolean allowSetPassword(Context context,
			HttpServletRequest request, String username) throws SQLException {
		return false;
	}

	@Override
	public boolean isImplicit() {
		return false;
	}

	@Override
	public int[] getSpecialGroups(Context context, HttpServletRequest request)
			throws SQLException {
		return new int[0];
	}

	@Override
	public int authenticate(Context context, String username, String password,
			String realm, HttpServletRequest request) throws SQLException {
		new GoogleAuthentication();
		try {
			String user = getUserInfoJson(stateToken);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public String loginPageURL(Context context, HttpServletRequest request,
			HttpServletResponse response) {
		return buildLoginUrl();
	}

	@Override
	public String loginPageTitle(Context context) {
		return "org.dspace.authenticate.GoogleAuthentication.title";
	}
}

