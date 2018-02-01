package pt.keep.oaiextended;

import java.util.Map;

public class HarvestResult {
	private Map map;
	private boolean moreResults;
	private String resumptionToken;
	private boolean noResults = false;
	
	public HarvestResult () {
		this.moreResults = false;
	}
	
	public Map getMap () {
		return this.map;
	}
	
	public void setMap (Map map) {
		this.map = map;
	}
	
	public void setResumptionToken (String token) {
		this.resumptionToken = token;
	}
	
	public boolean emptyMapButHasMoreDocumentsToSearch () {
		return (this.moreResults);
	}
	
	public String getResumptionToken () {
		return this.resumptionToken;
	}
	
	public void tryNextPage () {
		this.moreResults = true;
	}
	
	public void setNoResults () {
		this.noResults = true;
	}
	
	public boolean hasNoResults () {
		return this.noResults;
	}
}
