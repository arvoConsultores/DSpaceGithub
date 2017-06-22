package es.arvo.dspace.orciduploader.model;

public class WorkCitationOrcidUploader {
	
	public static final String FORMATTED_UNSPECIFIED = "formatted-unspecified";
	public static final String BIBTEX = "bibtex";
	public static final String FORMATTED_APA = "formatted-apa";
	public static final String FORMATTED_HARVARD = "formatted-harvard";
	public static final String FORMATTED_IEEE = "formatted-ieee";
	public static final String FORMATTED_MLA = "formatted-mla";
	public static final String FORMATTED_VANCOUVER = "formatted-vancouver";
	public static final String FORMATTED_CHICAGO = "formatted-chicago";
	
	private String workCitationType;
	private String citation;

	public WorkCitationOrcidUploader(String workCitationType, String citation) {
		this.workCitationType = workCitationType;
		this.citation = citation;
	}

	public String getWorkCitationType() {
		return workCitationType;
	}

	public String getCitation() {
		return citation;
	}

	@Override
	public String toString() {
		String result = "";

		result += "<work-citation>";
		result += "<work-citation-type>" + workCitationType + "</work-citation-type>";
		result += "<citation>" + citation + "</citation>";
		result += "</work-citation>";
		
		return result;

	}

}
