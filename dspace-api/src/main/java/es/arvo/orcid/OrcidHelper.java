package es.arvo.orcid;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.mail.MessagingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Email;
import org.dspace.core.I18nUtil;

public class OrcidHelper {

    public static String ORCIDMAIL="orcid_request";
    public static String ORCIDMAILREINTENTO="orcid_request_retry";
    public static String ORCIDMAILGESTIONMANUAL="orcid_mail_gestion_manual";
    public static String MODE_AUTENTICACION="autenticacion";
    public static String MODE_CREACION="creacion";
    // Primo para encriptar un poco el id de usuario
    public static int MAGICNUMBER=317;
    private static final Logger log = Logger.getLogger(OrcidHelper.class);
    
    private static SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmssSSSS");
    

    private static String construirUrlAceptarPermisos(OrcidVO persona) {
	   //https://sandbox.orcid.org/oauth/authorize?client_id=APP-674MCQQR985VZQ2Z&response_type=code&scope=/orcid-profile/read-limited&redirect_uri=https://developers.google.com/oauthplayground
	//   https://sandbox.orcid.org/oauth/authorize?client_id=APP-EC86TCB4725VXPSG&response_type=code&scope=/orcid-profile/read-limited&redirect_uri=http://89.128.83.119:8080/e-ieo/orcid_access
	    StringBuffer url=new StringBuffer();
	    //url.append("<a id=\"connect-orcid-link\" href=\"");
	    url.append(ConfigurationManager.getProperty("authentication-oauth", "authentication-oauth.orcid-api-url"));
	    url.append("/oauth/authorize?client_id=");
	    url.append(ConfigurationManager.getProperty("authentication-oauth", "authentication-oauth.application-client-id"));
	    if(StringUtils.isNotBlank(persona.getNombre())){
		url.append("&given_names="+URLEncoder.encode(persona.getNombre()));
	    }
	    if(StringUtils.isNotBlank(persona.getApellidos())){
		url.append("&family_names="+URLEncoder.encode(persona.getApellidos()));
	    }
	   
	    if(StringUtils.isNotBlank(persona.getOrcidId())){
		url.append("&orcid="+URLEncoder.encode(persona.getOrcidId()));
	    }else  if(StringUtils.isNotBlank(persona.getEmail())){
		url.append("&email="+URLEncoder.encode(persona.getEmail()));
	    }

	    url.append("&state="+persona.getId()*MAGICNUMBER);
	    url.append("&response_type=code&lang=es&scope=/read-limited%20/activities/update%20/person/update&redirect_uri=");
	    url.append(ConfigurationManager.getProperty("authentication-oauth", "authentication-oauth.application-client-redirect"));
	   // url.append("\"><img id=\"orcid-id-logo\" src=\"http://orcid.org/sites/default/files/images/orcid_16x16.png\" width='16' height='16' alt=\"ORCID logo\"/>Create or Connect your ORCID iD</a>");
	    return url.toString();
    }
    
   // 'client_id=APP-674MCQQR985VZQ2Z&client_secret=5f63d1c5-3f00-4fa5-b096-fd985ffd0df7&grant_type=authorization_code&code=Q70Y3A&redirect_uri=https://developers.google.com/oauthplayground'
    public static String construirDataUrlToken(String code) {
    	StringBuffer url=new StringBuffer();
    	url.append("client_id="+ConfigurationManager.getProperty("authentication-oauth", "authentication-oauth.application-client-id"));
    	url.append("&client_secret="+ConfigurationManager.getProperty("authentication-oauth", "authentication-oauth.application-client-secret"));
    	url.append("&grant_type=authorization_code");
    	url.append("&code="+code);
    	url.append("&redirect_uri="+ConfigurationManager.getProperty("authentication-oauth", "authentication-oauth.application-client-redirect"));	
    	return url.toString();
    }


}
