package es.arvo.dspace.orciduploader.model;

public class LanguageCodeOrcidUploader {

	private String languageCode;

	public LanguageCodeOrcidUploader(String languageCode) {
		this.languageCode = changeLanguageCode(languageCode);
	}

	private String changeLanguageCode(String languageCode) {
		if (languageCode.equalsIgnoreCase("eng"))
			return "en";
		else if (languageCode.equalsIgnoreCase("spa"))
			return "es";
		else if (languageCode.equalsIgnoreCase("glg"))
			return "gl";
		else if (languageCode.equalsIgnoreCase("cat"))
			return "ca";
		else if (languageCode.equalsIgnoreCase("eus"))
			return "eu";
		else if (languageCode.equalsIgnoreCase("deu"))
			return "de";
		else if (languageCode.equalsIgnoreCase("fra"))
			return "fr";
		else if (languageCode.equalsIgnoreCase("ita"))
			return "it";
		else if (languageCode.equalsIgnoreCase("jpn"))
			return "ja";
		else // Chino
			return "zh_CN";		
	}

	public String getLanguageCode() {
		return languageCode;
	}

	@Override
	public String toString() {
		return "<language-code>" + languageCode + "</language-code>";
	}

}
