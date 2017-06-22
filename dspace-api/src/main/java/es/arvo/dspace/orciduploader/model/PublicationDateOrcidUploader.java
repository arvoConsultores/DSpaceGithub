package es.arvo.dspace.orciduploader.model;

public class PublicationDateOrcidUploader {

	private String year;
	private String month;

	public PublicationDateOrcidUploader(String year) {
		this.year = year;
	}

	public PublicationDateOrcidUploader(String year, String month) {
		this.year = year;
		this.month = month;
	}

	public String getYear() {
		return year;
	}

	public String getMonth() {
		return month;
	}

	@Override
	public String toString() {
		String result = "";

		result += "<publication-date>";
		result += "<year>" + year + "</year>";
		result += "<month>" + month + "</month>";
		result += "</publication-date>";
		
		return result;
	}

}
