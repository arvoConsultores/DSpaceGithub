package es.arvo.orcid;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.ToStringBuilder;
/*{"access_token":"c47026d9-90bf-4480-a259-a953bc103495","token_type":"bearer","expires_in":631138517,"scope":"/orcid-works/read-limited","orcid":"0000-0003-1495-7122","name":"ORCID Test"}*/
@Generated("org.jsonschema2pojo")
public class OrcidAccessResponseVO {
    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("token_type")
    @Expose
    private String tokenType;
    @SerializedName("expires_in")
    @Expose
    private Integer expiresIn;
    @Expose
    private String scope;
    @Expose
    private String orcid;
    @Expose
    private String name;
    @Expose
    private String messageVersion;
    @Expose
    private Object orcidProfile;
    @Expose
    private Object orcidSearchResults;
    @Expose
    private ErrorDescVO errorDesc;

    /**
    *
    * @return
    * The accessToken
    */
    public String getAccessToken() {
    return accessToken;
    }

    /**
    *
    * @param accessToken
    * The access_token
    */
    public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
    }

    /**
    *
    * @return
    * The tokenType
    */
    public String getTokenType() {
    return tokenType;
    }

    /**
    *
    * @param tokenType
    * The token_type
    */
    public void setTokenType(String tokenType) {
    this.tokenType = tokenType;
    }

    /**
    *
    * @return
    * The expiresIn
    */
    public Integer getExpiresIn() {
    return expiresIn;
    }

    /**
    *
    * @param expiresIn
    * The expires_in
    */
    public void setExpiresIn(Integer expiresIn) {
    this.expiresIn = expiresIn;
    }

    /**
    *
    * @return
    * The scope
    */
    public String getScope() {
    return scope;
    }

    /**
    *
    * @param scope
    * The scope
    */
    public void setScope(String scope) {
    this.scope = scope;
    }

    /**
    *
    * @return
    * The orcid
    */
    public String getOrcid() {
    return orcid;
    }

    /**
    *
    * @param orcid
    * The orcid
    */
    public void setOrcid(String orcid) {
    this.orcid = orcid;
    }

    /**
    *
    * @return
    * The name
    */
    public String getName() {
    return name;
    }

    /**
    *
    * @param name
    * The name
    */
    public void setName(String name) {
    this.name = name;
    }
    /**
    *
    * @return
    * The messageVersion
    */
    public String getMessageVersion() {
    return messageVersion;
    }

    /**
    *
    * @param messageVersion
    * The messageVersion
    */
    public void setMessageVersion(String messageVersion) {
    this.messageVersion = messageVersion;
    }

    /**
    *
    * @return
    * The orcidProfile
    */
    public Object getOrcidProfile() {
    return orcidProfile;
    }

    /**
    *
    * @param orcidProfile
    * The orcidProfile
    */
    public void setOrcidProfile(Object orcidProfile) {
    this.orcidProfile = orcidProfile;
    }

    /**
    *
    * @return
    * The orcidSearchResults
    */
    public Object getOrcidSearchResults() {
    return orcidSearchResults;
    }

    /**
    *
    * @param orcidSearchResults
    * The orcidSearchResults
    */
    public void setOrcidSearchResults(Object orcidSearchResults) {
    this.orcidSearchResults = orcidSearchResults;
    }

    /**
    *
    * @return
    * The errorDesc
    */
    public ErrorDescVO getErrorDesc() {
    return errorDesc;
    }

    /**
    *
    * @param errorDesc
    * The errorDesc
    */
    public void setErrorDesc(ErrorDescVO errorDesc) {
    this.errorDesc = errorDesc;
    }

    @Override
    public String toString() {
    return ToStringBuilder.reflectionToString(this);
    }
    }


