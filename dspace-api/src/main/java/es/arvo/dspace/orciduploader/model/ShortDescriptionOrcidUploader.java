package es.arvo.dspace.orciduploader.model;

public class ShortDescriptionOrcidUploader {

	private String shortDescription;

	public ShortDescriptionOrcidUploader(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	@Override
	public String toString() {
		return "<short-description>" + shortDescription + "</short-description>";
	}
}
