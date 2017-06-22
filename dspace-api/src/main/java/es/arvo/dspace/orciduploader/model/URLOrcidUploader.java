package es.arvo.dspace.orciduploader.model;

public class URLOrcidUploader {

	private String url;

	public URLOrcidUploader(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public String toString() {
		return "<url>" + url + "</url>";
	}

}
