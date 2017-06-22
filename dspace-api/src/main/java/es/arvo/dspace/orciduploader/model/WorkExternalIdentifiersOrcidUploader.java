package es.arvo.dspace.orciduploader.model;

import java.util.ArrayList;
import java.util.List;

public class WorkExternalIdentifiersOrcidUploader {
	
	 /**
	 * A unique identifier, used identifiers types not included in this list.
	 */
	public static final String OTHER_ID = "other-id";
	
	/**
	 * Agricola identifier
	 */
	public static final String AGR = "agr";
	
	/**
	 * ARXIV
	 */
	public static final String ARXIV = "arxiv";
	
	/**
	 * Amazon Standard Identification Number
	 */
	public static final String ASIN = "asin";
	
	/**
	 * Asin top-level domain for amazon sites other than the us; valid values: co.jp co.uk ca cn fr de it es
	 */
	public static final String ASIN_TLD = "asin-tld";
	
	/**
	 * Bibcode; used by a number of astronomical data systems; example: 1924mnras..84..308e
	 */
	public static final String BIBCODE = "bibcode";
	
	/**
	 * Chinese Biological Abstracts Identifier
	 */
	public static final String CBA = "cba";
	
	/**
	 * CITESEER
	 */
	public static final String CIT﻿ = "cit";
	
	/**
	 * Citexplore submission
	 */
	public static final String CTX﻿ = "ctx";
	
	/**
	 * Digital Object Identifier; Example: 10.1038/news070508-7
	 */
	public static final String DOI = "doi";
	
	/**
	 * Identifier used by scopus
	 */
	public static final String EID = "eid";
	
	/**
	 * Ethos persistent identifier
	 */
	public static final String ETHOS = "ethos";
	
	/**
	 * Handle system
	 */
	public static final String HANDLE﻿ = "handle";
	
	/**
	 * NHS evidence identifier
	 */
	public static final String HIR = "hir";
	
	/**
	 * International Standard Book Number. Such as 978-0812695939
	 */
	public static final String ISBN = "isbn";
	
	/**
	 * International Standard Serial Number. [ISSN is not recommended to be included with journal articles]
	 */
	public static final String ISSN = "issn";
	
	/**
	 * Jahrbuch über die Fortschritte der Mathematik
	 */
	public static final String JFM = "jfm";
	
	/**
	 * JSTOR abstract
	 */
	public static final String JSTOR = "jstor";
	
	/**
	 * Library Of Congress Control number
	 */
	public static final String LCCN = "lccn";
	
	/**
	 * Mathematical Reviews
	 */
	public static final String MR = "mr";
	
	/**
	 * Online Computer Library Center
	 */
	public static final String OCLC = "oclc";
	
	/**
	 * Open Library
	 */
	public static final String OL = "ol";
	
	/**
	 * Office of Scientific and Technical Information
	 */
	public static final String OSTI = "osti";
	
	/**
	 * Patent number
	 */
	public static final String PAT = "pat";
	
	/**
	 * Pubmed central article number for full-text free repository of an article
	 */
	public static final String PMC = "pmc";
	
	/**
	 * Pubmed Unique Identifier
	 */
	public static final String PMID = "pmid";
	
	/**
	 * Request For Comments
	 */
	public static final String RFC = "rfc";
	
	/**
	 * Local identifier. This field should be used when no standard identifiers exist for the work
	 */
	public static final String SOURCE_WORK_ID = "source-work-id";
	
	/**
	 * Social Science Research Network
	 */
	public static final String SSRN = "ssrn";
	
	/**
	 * Uniform Resource Identifier
	 */
	public static final String URI = "uri";
	
	/**
	 * Uniform Resource Name
	 */
	public static final String URN = "urn";
	
	/**
	 * Identifier used by Web of Science™
	 */
	public static final String WOSUID = "wosuid";
	
	/**
	 * Zentralblatt math
	 */
	public static final String ZBL = "zbl";

	private List<OrcidAPIWorkExternalIdentifier> identifiers;

	public WorkExternalIdentifiersOrcidUploader() {
		this.identifiers = new ArrayList<OrcidAPIWorkExternalIdentifier>();
	}

	public void addIdentifier(String type, String id) {
		identifiers.add(new OrcidAPIWorkExternalIdentifier(type, id));
	}

	public List<OrcidAPIWorkExternalIdentifier> getIdentifiers() {
		return identifiers;
	}

	@Override
	public String toString() {
		String result = "";

		if(identifiers.size() > 0){
			result += "<work-external-identifiers>";
			for (OrcidAPIWorkExternalIdentifier orcidAPIWorkExternalIdentifier : identifiers) {
				result += orcidAPIWorkExternalIdentifier.toString();
			}
			result += "</work-external-identifiers>";
		}

		return result;
	}

	private class OrcidAPIWorkExternalIdentifier {

		private String type;
		private String id;

		public OrcidAPIWorkExternalIdentifier(String type, String id) {
			this.type = type;
			this.id = id;
		}

		public String getType() {
			return type;
		}

		public String getId() {
			return id;
		}

		@Override
		public String toString() {
			String result = "";

			result += "<work-external-identifier>";
			result += "<work-external-identifier-type>" + type + "</work-external-identifier-type>";
			result += "<work-external-identifier-id>" + id + "</work-external-identifier-id>";
			result += "</work-external-identifier>";

			return result;
		}

	}

}
