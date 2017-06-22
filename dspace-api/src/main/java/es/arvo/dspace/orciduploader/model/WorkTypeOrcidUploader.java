package es.arvo.dspace.orciduploader.model;

public class WorkTypeOrcidUploader {

	public static final String ARTICLE = "article";
	public static final String ARTISTIC_PERFORMANCE = "artistic-performance";
	public static final String BOOK = "book";
	public static final String BOOK_CHAPTER = "book-chapter";
	public static final String BOOK_REVIEW = "book-review";
	public static final String CONFERENCE_ABSTRACT = "conference-abstract";
	public static final String CONFERENCE_PAPER = "conference-paper";
	public static final String CONFERENCE_POSTER = "conference-poster";
	public static final String DATA_SET = "data-set";
	public static final String DICTIONARY_ENTRY = "dictionary-entry";
	public static final String DISCLOSURE = "disclosure";
	public static final String DISSERTATION = "dissertation";
	public static final String EDITED_BOOK = "edited-book";
	public static final String ENCYCLOPEDIA_ENTRY = "encyclopedia-entry";
	public static final String INVENTION = "invention";
	public static final String JOURNAL_ARTICLE = "journal-article";
	public static final String JOURNAL_ISSUE = "journal-issue";
	public static final String LECTURE_SPEECH = "lecture-speech";
	public static final String LICENSE = "license";
	public static final String MAGAZINE = "magazine";
	public static final String MANUAL = "manual";
	public static final String NEWSLETTER_ARTICLE = "newsletter-article";
	public static final String NEWSPAPER_ARTICLE = "newspaper-article";
	public static final String ONLINE_RESOURCE = "online-resource";
	public static final String OTHER = "other";
	public static final String PATENT = "patent";
	public static final String REGISTERED_COPYRIGHT = "registered-copyright";
	public static final String REPORT = "report";
	public static final String RESEARCH_TECHNIQUE = "research-technique";
	public static final String RESEARCH_TOOL = "research-tool";
	public static final String SPIN_OFF_COMPANY = "spin-off-company";
	public static final String STANDARDS_AND_POLICY = "standards-and-policy";
	public static final String SUPERVISED_STUDENT_PUBLICATION = "supervised-student-publication";
	public static final String TECHNICAL_STANDARD = "technical-standard";
	public static final String TEST = "test";
	public static final String TRANSLATION = "translation";
	public static final String WEBSITE = "website";
	public static final String WORKING_PAPER = "working-paper";

	private String workType;

	public WorkTypeOrcidUploader(String workType) {
		this.workType = workType;
	}

	public String getWorkType() {
		return workType;
	}

	@Override
	public String toString() {
		return "<work-type>" + workType + "</work-type>";
	}

}
