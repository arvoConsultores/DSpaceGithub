/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.xmlui.aspect.eperson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;


import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.AbstractAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.environment.http.HttpEnvironment;
import org.apache.log4j.Logger;
import org.dspace.app.xmlui.utils.AuthenticationUtil;
import org.dspace.app.xmlui.utils.ContextUtil;
import org.dspace.authorize.AuthorizeException;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.core.LogManager;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.orcid.jaxb.model.record_v2.Emails;
import org.orcid.jaxb.model.record_v2.PersonalDetails;

import com.google.gson.Gson;


import es.arvo.orcid.OrcidHelper;

/**
 * It tries to authenticate based on the credentials of orcid. The action uses
 * the parameters of supplier hhtp by Orcid.
 * 
 * If the authentication attempt is successful, then a redirect indicated in the
 * URL will be performed.
 * 
 * If the authentication fails, the action returns null.
 * 
 * @author Sergio Lopez Gonzalez at arvo.es
 *
 */
public class OrcidAction extends AbstractAction {

	private static Logger log = Logger.getLogger(OrcidAction.class);
	private String code = null;

	/**
	 * Attempt to authenticate the user. At least the primary email has to be
	 * verified. In case the email is not verified, in the database will be
	 * saved the orcidId.
	 */
	public Map act(Redirector redirector, SourceResolver resolver,
			Map objectModel, String source, Parameters parameters)
			throws Exception {
		Request request = ObjectModelHelper.getRequest(objectModel);
		Context context = ContextUtil.obtainContext(objectModel);

		if (code == null)
			code = request.getParameter("code");
		String state = request.getParameter("state");
		String error = request.getParameter("error");
		if (code != null && error == null) {
			Gson gson = new Gson();
			String curlResponse = executeAccessTokenRequest(code);
			/*{"access_token":"8184bccc-6458-42df-b6b5-81c2e2e3e342","token_type":"bearer","refresh_token":"f34023ad-78da-4bcd-90a7-4ac70c0a2ee2","expires_in":631138518,"scope":"/read-limited","name":"sergio arvo","orcid":"0000-0002-5959-1728"}*/
			String bodyAsString = curlResponse;
			es.arvo.orcid.OrcidAccessResponseVO orcidAccessResponse = gson
					.fromJson(bodyAsString,
							es.arvo.orcid.OrcidAccessResponseVO.class);
			String accessToken = orcidAccessResponse.getAccessToken();
			String orcidId = orcidAccessResponse.getOrcid();
			
			// Email
			curlResponse = executeEmailRequest(orcidId, accessToken);
			JAXBContext jc = JAXBContext.newInstance(Emails.class);
			Unmarshaller u = jc.createUnmarshaller();
			Emails email = (Emails) u.unmarshal(new StringReader(curlResponse));
			
			// Personal Details
			curlResponse = executeProfileRequest(orcidId, accessToken);
			jc = JAXBContext.newInstance(PersonalDetails.class);
			u = jc.createUnmarshaller();
			PersonalDetails personalDetails = (PersonalDetails) u.unmarshal(new StringReader(curlResponse));
			
			
			EPerson eperson = null;
			if (orcidId != null) {
				context.turnOffAuthorisationSystem();
				eperson = EPerson.findByOrcid(context, orcidId);
				if (eperson==null){
					if (email!=null && email.getEmails()!=null){
						eperson = EPerson.findByEmail(context, email.getEmails().get(0).getEmail());
						if (eperson==null){
							eperson = EPerson.create(context);
							eperson.setEmail(email.getEmails().get(0).getEmail());
							if (personalDetails.getName()!=null && personalDetails.getName().getGivenNames()!=null)
								eperson.setFirstName(personalDetails.getName().getGivenNames().getContent());
							if (personalDetails.getName() != null && personalDetails.getName().getFamilyName()!=null)
								eperson.setLastName(personalDetails.getName().getFamilyName().getContent());
							eperson.setOrcidId(orcidId);
							eperson.update();
							context.commit();
						}
						else{
							eperson.setOrcidId(orcidId);
							if (personalDetails.getName() != null && personalDetails.getName().getGivenNames()!=null)
								eperson.setFirstName(personalDetails.getName().getGivenNames().getContent());
							if (personalDetails.getName()!=null && personalDetails.getName().getFamilyName()!=null)
								eperson.setLastName(personalDetails.getName().getFamilyName().getContent());
						}
					}
				}
				else{
					if (email!=null && email.getEmails()!=null)
						eperson.setEmail(email.getEmails().get(0).getEmail());
					if (personalDetails.getName()!=null && personalDetails.getName().getGivenNames()!=null)
						eperson.setFirstName(personalDetails.getName().getGivenNames().getContent());
					if (personalDetails.getName()!=null && personalDetails.getName().getFamilyName()!=null)
						eperson.setLastName(personalDetails.getName().getFamilyName().getContent());
				}
				eperson.update();
				context.commit();
			}
			context.restoreAuthSystemState();

			assignGroups(context);

			final HttpServletResponse httpResponse = (HttpServletResponse) objectModel.get(HttpEnvironment.HTTP_RESPONSE_OBJECT);

			String redirectURL = request.getContextPath();

			if (AuthenticationUtil.isInterupptedRequest(objectModel)) {
				redirectURL += AuthenticationUtil
						.resumeInterruptedRequest(objectModel);
			} else {
				String loginRedirect = ConfigurationManager.getProperty("xmlui.user.loginredirect");
				redirectURL += (loginRedirect != null) ? loginRedirect.trim() : "/";
			}

			httpResponse.sendRedirect(redirectURL);
			request.setAttribute("eperson", eperson);

			context.setCurrentUser(null);
			AuthenticationUtil.authenticate(objectModel, null, null, null);
			return new HashMap();
		}
		else{
			// Code is null or get an orcid error
		}
		return null;
	}

	/**
	 * Add authenticated users to the group defined in authentication-oauth.cfg
	 * 
	 * @param context the user context
	 */
	private void assignGroups(Context context) {
		int i = 1;
		String groupMap = ConfigurationManager.getProperty("authentication-oauth", "login.groupmap" + i);

		while (groupMap != null) {
			String t[] = groupMap.split(":");
			String dspaceGroupName = t[1];

			try {
				Group orcidGroup = Group.findByName(context, dspaceGroupName);
				if (orcidGroup != null) {
					orcidGroup.addMember(context.getCurrentUser());
					orcidGroup.update();
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
	 * Obtains Orcid profile data by means of its profile in orcid.
	 * 
	 * @param orcidId User's orcid
	 * @param accessToken  Acces Token needed to validate the request
	 * @return Orcid profile in a gson object
	 * @throws IOException
	 */
	private String executeProfileRequest(String orcidId, String accessToken)
			throws IOException {
		// curl -H "Content-Type: application/vdn.orcid+xml" -H
		// "Authorization: Bearer 693ecca5-ec1a-4084-9d40-7df5e75c2be6" -X GET
		// "https://api.sandbox.orcid.org/v1.2/0000-0002-5539-6343/orcid-profile"
		// -L -i

		URL url = new URL(ConfigurationManager.getProperty(
				"authentication-oauth", "orcid.api.url.request") + "/v2.0/" + orcidId + "/personal-details");
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
	
	/**
	 * Obatins the emails of the orcid profile
	 * 
	 * @param orcidId User's orcid
	 * @param accessToken  Acces Token needed to validate the request
	 * @return List of emails in orcid profile in a gson object
	 * @throws IOException
	 */
	private String executeEmailRequest(String orcidId, String accessToken)
			throws IOException {
		// curl -H "Content-Type: application/vdn.orcid+xml" -H
		// "Authorization: Bearer 693ecca5-ec1a-4084-9d40-7df5e75c2be6" -X GET
		// "https://api.sandbox.orcid.org/v1.2/0000-0002-5539-6343/orcid-profile"
		// -L -i

		// curl_init and url
		URL url = new URL(ConfigurationManager.getProperty("authentication-oauth", "orcid.api.url.request")+ "/v2.0/" + orcidId + "/email");
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

	/**
	 * Confirm that the token returned by orcid is valid.
	 * 
	 * @param code Code returned by orcid to validate the future request
	 * @return The access token in a gson object
	 * @throws IOException
	 */
	private String executeAccessTokenRequest(String code) throws IOException {
		// curl -i -L -H 'Accept: application/json' --data
		// 'client_id=APP-674MCQQR985VZQ2Z&client_secret=5f63d1c5-3f00-4fa5-b096-fd985ffd0df7&grant_type=authorization_code&code=Q70Y3A&redirect_uri=https://developers.google.com/oauthplayground'
		// 'https://api.sandbox.orcid.org/oauth/token'

		// curl_init and url
		URL url = new URL(ConfigurationManager.getProperty("authentication-oauth", "authentication-oauth.orcid-api-url") + "oauth/token");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		// CURLOPT_POST
		con.setRequestMethod("POST");

		// CURLOPT_FOLLOWLOCATION
		con.setInstanceFollowRedirects(true);

		con.setRequestProperty("Accept", "application/json");

		String postData = OrcidHelper.construirDataUrlToken(code);
		con.setRequestProperty("Content-length",String.valueOf(postData.length()));

		con.setDoOutput(true);
		con.setDoInput(true);

		DataOutputStream output = new DataOutputStream(con.getOutputStream());
		output.writeBytes(postData);
		output.close();

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

	/**
	 * 
	 * @param context the user context
	 * @return
	 */
	public String loginPageTitle(Context context) {
		return "org.dspace.authenticate.OrcidAuthentication.title";
	}

}
