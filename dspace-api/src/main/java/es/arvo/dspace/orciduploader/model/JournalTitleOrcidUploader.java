package es.arvo.dspace.orciduploader.model;

public class JournalTitleOrcidUploader {

	private String journalTitle;

	public JournalTitleOrcidUploader(String journalTitle) {
		this.journalTitle = journalTitle;
	}

	public String getJournalTitle() {
		return journalTitle;
	}

	@Override
	public String toString() {
		return "<journal-title>" + journalTitle + "</journal-title>";
	}

}
