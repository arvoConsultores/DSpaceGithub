/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.authenticate;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dspace.authority.orcid.OrcidService;
//import org.dspace.authority.orcid.jaxb.OrcidBio;
//import org.dspace.authority.orcid.jaxb.OrcidMessage;
//import org.dspace.authority.orcid.jaxb.OrcidProfile;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Metadatum;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.core.LogManager;
import org.dspace.eperson.EPerson;
import org.orcid.jaxb.model.message.OrcidMessage;

/**
 *
 * @author mdiggory at atmire.com
 */
public class OAuthAuthenticationMethod implements AuthenticationMethod {

	/** log4j category */
	private static Logger log = Logger.getLogger(OAuthAuthenticationMethod.class);

	@Override
	public boolean canSelfRegister(Context context, HttpServletRequest request,
			String username) throws SQLException {
		return false; // To change body of implemented methods use File |
						// Settings | File Templates.
	}

	@Override
	public void initEPerson(Context context, HttpServletRequest request,
			EPerson eperson) throws SQLException {
		// To change body of implemented methods use File | Settings | File
		// Templates.
	}

	@Override
	public boolean allowSetPassword(Context context,
			HttpServletRequest request, String username) throws SQLException {
		return false; // To change body of implemented methods use File |
						// Settings | File Templates.
	}

	@Override
	public boolean isImplicit() {
		return false; // To change body of implemented methods use File |
						// Settings | File Templates.
	}

	@Override
	public int[] getSpecialGroups(Context context, HttpServletRequest request)
			throws SQLException {
		return new int[0]; // To change body of implemented methods use File |
							// Settings | File Templates.
	}

	@Override
	public int authenticate(Context context, String username, String password,String realm, HttpServletRequest request) throws SQLException {

		String email = null;

		String orcid = (String) request.getAttribute("orcid");
		String token = (String) request.getAttribute("access_token");
		String scope = (String) request.getAttribute("scope");
		// String refreshToken = (String) request.getAttribute("refresh_token");
		if (request == null || orcid == null) {
			return BAD_ARGS;
		}

		EPerson eperson = EPerson.findByOrcid(context, orcid);

		// No email address, perhaps the eperson has been setup, better check it
		if (eperson == null) {
			eperson = context.getCurrentUser();
			if (eperson != null) {
				// if eperson exists then get ORCID Profile and binding data to
				// Eperson Account
				email = eperson.getEmail();
			}
		}
		// get the orcid profile
//		OrcidProfile profile = null;
//		OrcidService orcidObject = OrcidService.getOrcid();
//		if (orcid != null) {
//			if (StringUtils.isNotBlank(token) && StringUtils.contains(scope, "/orcid-profile")) {
//				profile = orcidObject.getProfile(orcid, token);
//			} else {
//				// try to retrieve public information
//				profile = orcidObject.getProfile(orcid);
//			}
//
//		}
		String curlResponse;
		OrcidMessage profile = null;
		try {
			curlResponse = executeProfileRequest(orcid, token);
			JAXBContext jc = JAXBContext.newInstance(OrcidMessage.class);
			Unmarshaller u = jc.createUnmarshaller();
			profile = (OrcidMessage) u.unmarshal(new StringReader(curlResponse));
		} catch (IOException e1) {
			e1.printStackTrace();
		
		} catch (JAXBException e1) {
			e1.printStackTrace();
		}
		
		// get the email from orcid
		if ((profile != null && email == null)
			&& (profile.getOrcidProfile() != null)
			&& (profile.getOrcidProfile().getOrcidBio() != null) 
			&& (profile.getOrcidProfile().getOrcidBio().getContactDetails() != null)
			&& (profile.getOrcidProfile().getOrcidBio().getContactDetails().getEmail() != null
			&& !profile.getOrcidProfile().getOrcidBio().getContactDetails().getEmail().isEmpty())) {
				email = profile.getOrcidProfile().getOrcidBio().getContactDetails().getEmail().get(0).getValue();
		}

		// //If Eperson does not exist follow steps similar to Shib....
		// if (eperson == null && email == null)
		// {
		// log.error("No email is given, you're denied access by OAuth, please release email address");
		// return AuthenticationMethod.BAD_ARGS;
		// }

		if (email != null) {
			email = email.toLowerCase();
		}

		String fname = "";
		String lname = "";
		if ((profile != null && profile.getOrcidProfile() != null && profile.getOrcidProfile().getOrcidBio() != null)
			&& (profile.getOrcidProfile().getOrcidBio().getPersonalDetails() != null)) {
				// try to grab name from the orcid profile
				fname= profile.getOrcidProfile().getOrcidBio().getPersonalDetails().getGivenNames().getContent();

				// try to grab name from the orcid profile
				lname = profile.getOrcidProfile().getOrcidBio().getPersonalDetails().getFamilyName().getContent();
		}

		if (eperson == null && email != null) {
			try {
				eperson = EPerson.findByEmail(context, email);
			} catch (AuthorizeException e) {
				log.warn("Fail to locate user with email:" + email, e);
				eperson = null;
			}
		}

		try {
			// TEMPORARILY turn off authorisation
			context.turnOffAuthorisationSystem();
			// auto create user if needed
			if (eperson == null && ConfigurationManager.getBooleanProperty("authentication-oauth", "autoregister")) {
				log.info(LogManager.getHeader(context, "autoregister", "orcid="+ orcid));

				eperson = EPerson.create(context);
				eperson.setEmail(email != null ? email : orcid);
				eperson.setFirstName(fname);
				eperson.setLastName(lname);
				eperson.setCanLogIn(true);
				AuthenticationManager.initEPerson(context, request, eperson);
				eperson.setNetid(orcid);
				eperson.addMetadata("eperson", "orcid", null, null, orcid);
				eperson.addMetadata("eperson", "orcid", "accesstoken", null,token);
				eperson.update();
				context.commit();
				context.setCurrentUser(eperson);
			} else if (eperson != null) {
				// found the eperson , update the eperson record with orcid id
				eperson.setOrcidId(orcid);
				if (eperson.getEmail() == null) {
					eperson.setEmail(email != null ? email : orcid);
				}
				// eperson.setMetadata("access_token",token);
				Metadatum[] md = eperson.getMetadata("eperson", "orcid", null,null);
				boolean found = false;
				if (md != null && md.length > 0) {
					for (Metadatum m : md) {
						if (StringUtils.equals(m.value, orcid)) {
							found = true;
							break;
						}
					}
				}
				if (!found) {
					eperson.addMetadata("eperson", "orcid", null, null, orcid);
				}
				eperson.addMetadata("eperson", "orcid", "accesstoken", null,token);
				eperson.update();
				context.commit();
			}
		} catch (AuthorizeException e) {
			log.warn("Fail to authorize user with orcid: " + orcid + " email:" + email, e);
			eperson = null;
		} finally {
			context.restoreAuthSystemState();
		}

		if (eperson == null) {
			return AuthenticationMethod.NO_SUCH_USER;
		} else {
			// the person exists, just return ok
			context.setCurrentUser(eperson);
			request.getSession().setAttribute("oauth.authenticated", Boolean.TRUE);
		}

		return AuthenticationMethod.SUCCESS;
	}
	
	private static String executeProfileRequest(String orcidId, String accessToken)
			throws IOException {
		// curl -H "Content-Type: application/vdn.orcid+xml" -H
		// "Authorization: Bearer 693ecca5-ec1a-4084-9d40-7df5e75c2be6" -X GET
		// "https://api.sandbox.orcid.org/v1.2/0000-0002-5539-6343/orcid-profile"
		// -L -i

		// curl_init and url
		URL url = new URL(ConfigurationManager.getProperty("authentication-oauth", "orcid.api.url.request") + "/v1.2/" + orcidId + "/orcid-bio");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		// CURLOPT_POST
		con.setRequestMethod("GET");

		// CURLOPT_FOLLOWLOCATION
		con.setInstanceFollowRedirects(true);

		con.setRequestProperty("Content-Type", "application/vdn.orcid+xml");
		con.setRequestProperty("Authorization", "Bearer " + accessToken);

		con.setDoOutput(true);
		con.setDoInput(true);

		// read the response
		DataInputStream input = new DataInputStream(con.getInputStream());
		int c;
		StringBuilder resultBuf = new StringBuilder();
		while ((c = input.read()) != -1) {
			resultBuf.append((char) c);
		}
		input.close();

		return resultBuf.toString();
	}

	// En este método componemos la url para poder ir a "ORCID"	
	@Override
	public String loginPageURL(Context context, HttpServletRequest request, HttpServletResponse response) {
		if (ConfigurationManager.getBooleanProperty("authentication-oauth", "choice-page")) {
			StringBuffer url=new StringBuffer();
			url.append(ConfigurationManager.getProperty("authentication-oauth", "authentication-oauth.orcid-api-url"));
			url.append("oauth/authorize?client_id=");
			url.append(ConfigurationManager.getProperty("authentication-oauth", "authentication-oauth.application-client-id"));
			url.append("&response_type=code&scope=");
			url.append(ConfigurationManager.getProperty("authentication-oauth", "authentication-oauth.application-scope"));
			url.append("&redirect_uri=");
			url.append(ConfigurationManager.getProperty("authentication-oauth", "authentication-oauth.application-client-redirect"));
			url.append("&show_login=true");
			return url.toString();
			
			
		} else {
			return null;
		}
	}

	@Override
	public String loginPageTitle(Context context) {
		return "org.dspace.authenticate.OrcidAuthentication.title";
	}
}
