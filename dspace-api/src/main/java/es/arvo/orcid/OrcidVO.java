package es.arvo.orcid;

import java.util.Date;

public class OrcidVO {
    private int id;
    private boolean idChanged;
    private String nombre;
    private boolean nombreChanged;
    private String apellidos;
    private boolean apellidosChanged;
    private String orcidId;
    private boolean orcidIdChanged;
    private String email;
    private boolean emailChanged;
    private Date lastEmailTryDate;
    private boolean lastEmailTryDateChanged;
    private Date firstEmailTryDate;
    private boolean firstEmailTryDateChanged;
    private String orcidName;
    private boolean orcidNameChanged;
    private String accessToken;
    private boolean accessTokenChanged;
    private String tokenType;
    private boolean tokenTypeChanged;
    private int tokenExpiration;
    private boolean tokenExpirationChanged;
    private Date aceptationDate;
    private boolean aceptationDateChanged;
    private boolean baja;
    private boolean bajaChanged;

    public String getBBDDSetString(){
	StringBuffer sb=new StringBuffer();
	boolean first=true;
	// El id no deberia cambiar, si cambia es otro item, pero bueno, se pone por siaca
	if(idChanged){
	    if(!first){
		sb.append(",");
	    }
	    sb.append(" id ="+id);
	    first=false;
	}
	
	if(nombreChanged){
	    if(!first){
		sb.append(",");
	    }
	    if(nombre==null){
		sb.append(" nombre = null");
	    }else{
		sb.append(" nombre ='"+nombre+"'");
	    }
	    first=false;
	}
	
	if(apellidosChanged){
	    if(!first){
		sb.append(",");
	    }
	    if(apellidos==null){
		sb.append(" apellidos =null");
	    }else{
		sb.append(" apellidos ='"+apellidos+"'");
	    }
	    first=false;
	}
	
	if(orcidIdChanged){
	    if(!first){
		sb.append(",");
	    }
	    if(orcidId==null){
		sb.append(" orcidId =null");
	    }else{
		sb.append(" id_orcid ='"+orcidId+"'");
	    }
	    first=false;
	}
	
	if(emailChanged){
	    if(!first){
		sb.append(",");
	    }
	    if(email==null){
		 sb.append(" email =null");
	    }else{
		sb.append(" email ='"+email+"'");
	    }
	    first=false;
	}
	
	if(lastEmailTryDateChanged){
	    if(!first){
		sb.append(",");
	    }
	    if(lastEmailTryDate==null){
		sb.append(" last_email_try_date =null ");
	    }else{
		sb.append(" last_email_try_date ='"+new java.sql.Date(lastEmailTryDate.getTime())+"'");
	    }
	    first=false;
	}
	
	if(firstEmailTryDateChanged){
	    if(!first){
		sb.append(",");
	    }
	    if(firstEmailTryDate==null){
		sb.append(" first_email_try_date =null ");
	    }
	    sb.append(" first_email_try_date ='"+new java.sql.Date(firstEmailTryDate.getTime())+"'");
	    first=false;
	}
	
	if(orcidNameChanged){
	    if(!first){
		sb.append(",");
	    }
	    if(orcidName==null){
		sb.append(" orcid_name =null");
	    }else{
		sb.append(" orcid_name ='"+orcidName+"'");
	    }
	    first=false;
	}
	
	if(accessTokenChanged){
	    if(!first){
		sb.append(",");
	    }
	    if(accessToken==null){
		 sb.append(" access_token =null");
	    }else{
		sb.append(" access_token ='"+accessToken+"'");
	    }
	    first=false;
	}
	
	if(tokenTypeChanged){
	    if(!first){
		sb.append(",");
	    }
	    if(tokenType==null){
		sb.append(" token_type =null");
	    }else{
		sb.append(" token_type ='"+tokenType+"'");
	    }
	    first=false;
	}
	
	if(tokenExpirationChanged){
	    if(!first){
		sb.append(",");
	    }
//	    if(tokenExpiration==null){
//		sb.append(" token_expiration =null");
//	    }else{
		sb.append(" token_expiration ='"+tokenExpiration+"'");
//	    }
	    first=false;
	}
	
	if(aceptationDateChanged){
	    if(!first){
		sb.append(",");
	    }
	    if(aceptationDate==null){
		sb.append(" aceptation_date =null");
	    }else{
		sb.append(" aceptation_date ='"+new java.sql.Date(aceptationDate.getTime())+"'");
	    }
	    first=false;
	}
	
	if(bajaChanged){
	    if(!first){
		sb.append(",");
	    }
//	    if(baja==null){
//		sb.append(" baja =null);
//	    }else{
		sb.append(" baja ="+baja);
//	    }
	    first=false;
	}
	return sb.toString();
    }
    /**
     * Resetea todos los flags de cambios a false
     */
    public void resetChanges(){
	idChanged=false;
	nombreChanged=false;
	apellidosChanged=false;
	orcidIdChanged=false;
	emailChanged=false;
	lastEmailTryDateChanged=false;
	firstEmailTryDateChanged=false;
	orcidNameChanged=false;
	orcidNameChanged=false;
	accessTokenChanged=false;
	tokenTypeChanged=false;
	tokenExpirationChanged=false;
	aceptationDateChanged=false;
	bajaChanged=false;
    }
    /**
     * Constructor por defecto. Si luego se rellena con datos base hay que resetear los cambios con "resetChanges()"
     */
    public OrcidVO(){
	
    }
    public OrcidVO(int id,String nombre,String apellidos,String orcidId,String email,Date lastEmailTryDate,Date firstEmailTryDate,String orcidName,String accessToken,String tokenType,int tokenExpiration,Date aceptationDate,boolean baja){
	  this.id=id;
	  this.nombre=nombre;
	  this.apellidos=apellidos;
	  this.orcidId=orcidId;
	  this.email=email;
	  this.lastEmailTryDate=lastEmailTryDate;
	  this.firstEmailTryDate=firstEmailTryDate;
	  this.orcidName=orcidName;
	  this.accessToken=accessToken;
	  this.tokenType=tokenType;
	  this.tokenExpiration=tokenExpiration;
	  this.aceptationDate=aceptationDate;
	  this.baja=baja;
    }
    
    public boolean hasChanged(){
	return idChanged||nombreChanged||apellidosChanged||orcidIdChanged||emailChanged||lastEmailTryDateChanged||firstEmailTryDateChanged||orcidNameChanged||orcidNameChanged||accessTokenChanged||tokenTypeChanged||tokenExpirationChanged||aceptationDateChanged||bajaChanged;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
        idChanged=true;
    }
    public Date getAceptationDate() {
        return aceptationDate;
    }
    public void setAceptationDate(Date aceptationDate) {
        this.aceptationDate = aceptationDate;
        aceptationDateChanged=true;
    }
    public boolean isBaja() {
        return baja;
    }
    public void setBaja(boolean baja) {
        this.baja = baja;
        bajaChanged=true;
    }
    public Date getLastEmailTryDate() {
        return lastEmailTryDate;
    }
    public void setLastEmailTryDate(Date lastEmailTryDate) {
        this.lastEmailTryDate = lastEmailTryDate;
        lastEmailTryDateChanged=true;
    }
    public Date getFirstEmailTryDate() {
        return firstEmailTryDate;
    }
    public void setFirstEmailTryDate(Date firstEmailTryDate) {
        this.firstEmailTryDate = firstEmailTryDate;
        firstEmailTryDateChanged=true;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
        emailChanged=true;
    }

    public String getOrcidName() {
        return orcidName;
    }
    public void setOrcidName(String orcidName) {
        this.orcidName = orcidName;
        orcidNameChanged=true;
    }
    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        accessTokenChanged=true;
    }
    public String getTokenType() {
        return tokenType;
    }
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
        tokenTypeChanged=true;
    }
    public int getTokenExpiration() {
        return tokenExpiration;
    }
    public void setTokenExpiration(int tokenExpiration) {
        this.tokenExpiration = tokenExpiration;
        tokenExpirationChanged=true;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
        nombreChanged=true;
    }
    public String getApellidos() {
        return apellidos;
    }
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
        apellidosChanged=true;
    }
    public String getOrcidId() {
        return orcidId;
    }
    public void setOrcidId(String orcidId) {
        this.orcidId = orcidId;
        orcidIdChanged=true;
    }
    
}
