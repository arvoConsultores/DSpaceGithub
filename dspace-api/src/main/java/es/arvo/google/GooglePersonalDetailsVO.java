package es.arvo.google;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"id",
"email",
"verified_email",
"name",
"given_name",
"family_name",
"link",
"picture",
"gender",
"locale"
})
public class GooglePersonalDetailsVO {

@JsonProperty("id")
private String id;
@JsonProperty("email")
private String email;
@JsonProperty("verified_email")
private Boolean verifiedEmail;
@JsonProperty("name")
private String name;
@JsonProperty("given_name")
private String givenName;
@JsonProperty("family_name")
private String familyName;
@JsonProperty("link")
private String link;
@JsonProperty("picture")
private String picture;
@JsonProperty("gender")
private String gender;
@JsonProperty("locale")
private String locale;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("id")
public String getId() {
return id;
}

@JsonProperty("id")
public void setId(String id) {
this.id = id;
}

@JsonProperty("email")
public String getEmail() {
return email;
}

@JsonProperty("email")
public void setEmail(String email) {
this.email = email;
}

@JsonProperty("verified_email")
public Boolean getVerifiedEmail() {
return verifiedEmail;
}

@JsonProperty("verified_email")
public void setVerifiedEmail(Boolean verifiedEmail) {
this.verifiedEmail = verifiedEmail;
}

@JsonProperty("name")
public String getName() {
return name;
}

@JsonProperty("name")
public void setName(String name) {
this.name = name;
}

@JsonProperty("given_name")
public String getGivenName() {
return givenName;
}

@JsonProperty("given_name")
public void setGivenName(String givenName) {
this.givenName = givenName;
}

@JsonProperty("family_name")
public String getFamilyName() {
return familyName;
}

@JsonProperty("family_name")
public void setFamilyName(String familyName) {
this.familyName = familyName;
}

@JsonProperty("link")
public String getLink() {
return link;
}

@JsonProperty("link")
public void setLink(String link) {
this.link = link;
}

@JsonProperty("picture")
public String getPicture() {
return picture;
}

@JsonProperty("picture")
public void setPicture(String picture) {
this.picture = picture;
}

@JsonProperty("gender")
public String getGender() {
return gender;
}

@JsonProperty("gender")
public void setGender(String gender) {
this.gender = gender;
}

@JsonProperty("locale")
public String getLocale() {
return locale;
}

@JsonProperty("locale")
public void setLocale(String locale) {
this.locale = locale;
}

@JsonAnyGetter
public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

@JsonAnySetter
public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

}