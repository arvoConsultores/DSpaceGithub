package es.arvo.dspace.orciduploader.model;

import java.util.List;

public class WorksBuilderOrcidUploader {

	private List<WorkOrcidUploader> works;

	public WorksBuilderOrcidUploader(List<WorkOrcidUploader> works) {
		this.works = works;
	}

	public List<WorkOrcidUploader> getWorks() {
		return works;
	}

	@Override
	public String toString() {
		String result = "";

		result += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		result += "<orcid-message xmlns=\"http://www.orcid.org/ns/orcid\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.orcid.org/ns/orcid https://raw.github.com/ORCID/ORCID-Source/master/orcid-model/src/main/resources/orcid-message-1.2.xsd\">";
		result += "<message-version>1.2</message-version>";
		result += "<orcid-profile>";
		result += "<orcid-activities>";
		result += "<orcid-works>";
		for (WorkOrcidUploader work : works)
			result += work.toString();
		result += "</orcid-works>";
		result += "</orcid-activities>";
		result += "</orcid-profile>";
		result += "</orcid-message>";
		
		return result;
	}

}
