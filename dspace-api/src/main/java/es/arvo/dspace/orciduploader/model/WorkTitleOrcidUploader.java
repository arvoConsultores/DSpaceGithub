package es.arvo.dspace.orciduploader.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WorkTitleOrcidUploader {

	private String title;
	private String subTitle;
	private Map<String, String> translatedTitle;

	public WorkTitleOrcidUploader(String title) {
		this.title = title;
		this.subTitle = "";
		this.translatedTitle = new HashMap<String, String>();
	}

	public WorkTitleOrcidUploader(String title, String subTitle) {
		this.title = title;
		this.subTitle = subTitle;
		this.translatedTitle = new HashMap<String, String>();
	}

	public WorkTitleOrcidUploader(String title, Map<String, String> translatedTitle) {
		this.title = title;
		this.subTitle = "";
		this.translatedTitle = translatedTitle;
	}

	public WorkTitleOrcidUploader(String title, String subTitle, Map<String, String> translatedTitle) {
		this.title = title;
		this.subTitle = subTitle;
		this.translatedTitle = translatedTitle;
	}

	public void addTranslatedTitle(String languageCode, String translatedTitle) {
		if (this.translatedTitle != null)
			this.translatedTitle.put(languageCode, translatedTitle);
	}

	public String getTitle() {
		return title;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public Map<String, String> getTranslatedTitle() {
		return translatedTitle;
	}

	@Override
	public String toString() {
		String result = "";

		result += "<work-title>";
		result += "<title>" + title + "</title>";
		if (!subTitle.equals("")) {
			result += "<subtitle>" + subTitle + "</subtitle>";
		}
		if (translatedTitle.size() > 0) {
			Set<String> keys = translatedTitle.keySet();
			for (String key : keys) {
				result += "<translated-title language-code=\"" + key + "\">" + translatedTitle.get(key)
						+ "</translated-title>";
			}
		}
		result += "</work-title>";

		return result;
	}

}
