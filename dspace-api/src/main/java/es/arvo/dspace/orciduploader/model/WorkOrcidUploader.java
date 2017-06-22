package es.arvo.dspace.orciduploader.model;

import java.util.Map;

public class WorkOrcidUploader {

	private String title;
	private String type;

	private CountryOrcidUploader country;
	private JournalTitleOrcidUploader journalTitle;
	private LanguageCodeOrcidUploader languageCode;
	private PublicationDateOrcidUploader publicationDate;
	private ShortDescriptionOrcidUploader shortDescription;
	private URLOrcidUploader url;
	private WorkCitationOrcidUploader workCitation;
	private WorkContributorsOrcidUploader workContributors;
	private WorkExternalIdentifiersOrcidUploader workExternalIdentifiers;
	private WorkTitleOrcidUploader workTitle;
	private WorkTypeOrcidUploader workType;

	public WorkOrcidUploader(String title, String workType) {
		this.title = title;
		this.type = workType;

		setWorkTitle();
		setWorkType();
	}

	public void setCountry(String country) {
		this.country = new CountryOrcidUploader(country);
	}

	public void setJournalTitle(String journalTitle) {
		this.journalTitle = new JournalTitleOrcidUploader(journalTitle);
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = new LanguageCodeOrcidUploader(languageCode);
	}

	public void setPublicationDate(String year) {
		this.publicationDate = new PublicationDateOrcidUploader(year);
	}

	public void setPublicationDate(String year, String month) {
		this.publicationDate = new PublicationDateOrcidUploader(year, month);
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = new ShortDescriptionOrcidUploader(shortDescription);
	}

	public void setUrl(String url) {
		this.url = new URLOrcidUploader(url);
	}

	public void setWorkCitation(String workCitationType, String citation) {
		this.workCitation = new WorkCitationOrcidUploader(workCitationType, citation);
	}

	public void setWorkContributors(String orcidURI, String creditName, String contributorSequence,
			String contributorRole) {
		if(this.workContributors == null)
			this.workContributors = new WorkContributorsOrcidUploader();
		this.workContributors.addContributor(orcidURI, creditName, contributorSequence, contributorRole);
	}

	public void setWorkExternalIdentifiers(String type, String id) {
		if(this.workExternalIdentifiers == null)
			this.workExternalIdentifiers = new WorkExternalIdentifiersOrcidUploader();
		this.workExternalIdentifiers.addIdentifier(type, id);
	}

	public void setWorkTitle() {
		this.workTitle = new WorkTitleOrcidUploader(title);
	}

	public void setWorkTitle(String subTitle) {
		this.workTitle = new WorkTitleOrcidUploader(title, subTitle);
	}

	public void setWorkTitle(Map<String, String> translatedTitle) {
		this.workTitle = new WorkTitleOrcidUploader(title, translatedTitle);
	}

	public void setWorkTitle(String subTitle, Map<String, String> translatedTitle) {
		this.workTitle = new WorkTitleOrcidUploader(title, subTitle, translatedTitle);
	}

	public void setWorkType() {
		this.workType = new WorkTypeOrcidUploader(type);
	}

	@Override
	public String toString() {
		String result = "";
		
		result += "<orcid-work>";
		if(workTitle != null)
			result += workTitle.toString();
		
		if(journalTitle != null)
			result += journalTitle.toString();
		
		if(shortDescription != null)
			result += shortDescription.toString();
		
		if(workCitation != null)
			result += workCitation.toString();
		
		if(workType != null)
			result += workType.toString();
		
		if(publicationDate != null)
			result += publicationDate.toString();
		
		if(workExternalIdentifiers != null)
			result += workExternalIdentifiers.toString();
		
		if(url != null)
			result += url.toString();
		
		if(workContributors != null)
			result += workContributors.toString();
		
		if(languageCode != null)
			result += languageCode.toString();
		
		if(country != null)
			result += country.toString();
		
		result += "</orcid-work>";

		return result;
	}

}
