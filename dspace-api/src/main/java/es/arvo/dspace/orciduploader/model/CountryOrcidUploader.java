package es.arvo.dspace.orciduploader.model;

public class CountryOrcidUploader {

	private String country;

	public CountryOrcidUploader(String country) {
		this.country = country;
	}

	public String getCountry() {
		return country;
	}

	@Override
	public String toString() {
		return "<country>" + country + "</country>";
	}

}
