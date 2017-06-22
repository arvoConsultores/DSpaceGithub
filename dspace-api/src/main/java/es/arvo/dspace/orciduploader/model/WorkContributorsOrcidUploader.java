package es.arvo.dspace.orciduploader.model;

import java.util.ArrayList;
import java.util.List;

public class WorkContributorsOrcidUploader {

	private List<Contributor> contributors;

	public WorkContributorsOrcidUploader() {
		this.contributors = new ArrayList<WorkContributorsOrcidUploader.Contributor>();
	}

	public void addContributor(String orcidURI, String creditName, String contributorSequence, String contributorRole) {
		Contributor contributor;
		if (creditName != null && !creditName.equals(""))
			contributor = new Contributor(creditName);
		else
			contributor = new Contributor();
		contributor.addContributor(orcidURI, contributorSequence, contributorRole);
		contributors.add(contributor);
	}

	@Override
	public String toString() {
		String result = "";

		result += "<work-contributors>";
		for (Contributor contributor : contributors)
			result += contributor.toString();
		result += "</work-contributors>";

		return result;
	}

	private class Contributor {

		private ContributorOrcid contributorOrcid;
		private String creditName;
		private ContributorAttributes contributorAttributes;

		public Contributor() {
		}

		public Contributor(String creditName) {
			this.creditName = creditName;
		}

		public void addContributor(String orcidURI, String contributorSequence, String contributorRole) {
			if (orcidURI != null && !orcidURI.equals("")) {
				this.contributorOrcid = new ContributorOrcid(orcidURI);
			}
			if (contributorSequence != null && !contributorSequence.equals("")) {
				if (contributorAttributes == null) {
					contributorAttributes = new ContributorAttributes();
				}
				contributorAttributes.setContributorSequence(contributorSequence);
			}
			if (contributorRole != null && !contributorRole.equals("")) {
				if (contributorAttributes == null) {
					contributorAttributes = new ContributorAttributes();
				}
				contributorAttributes.setContributorRole(contributorRole);
			}

		}

		public String getCreditName() {
			return creditName;
		}

		@Override
		public String toString() {
			String result = "";

			result += "<contributor>";
			if (contributorOrcid != null) {
				result += contributorOrcid.toString();
			}
			if (contributorAttributes != null) {
				result += contributorAttributes.toString();
			}
			result += "</contributor>";

			return result;
		}

		private class ContributorOrcid {

			private String uri;

			public ContributorOrcid(String uri) {
				this.uri = uri;
			}

			public String getUri() {
				return uri;
			}

			@Override
			public String toString() {
				String result = "";

				result += "<contributor-orcid>";
				result += "<uri>" + uri + "</uri>";
				result += "</contributor-orcid>";
				return result;
			}

		}

		private class ContributorAttributes {

			private String contributorSequence;
			private String contributorRole;

			public String getContributorSequence() {
				return contributorSequence;
			}

			public void setContributorSequence(String contributorSequence) {
				this.contributorSequence = contributorSequence;
			}

			public String getContributorRole() {
				return contributorRole;
			}

			public void setContributorRole(String contributorRole) {
				this.contributorRole = contributorRole;
			}

			@Override
			public String toString() {
				String result = "";

				result += "<contributor-attributes>";
				if (!contributorSequence.equals(""))
					result += "<contributor-sequence>" + contributorSequence + "</contributor-sequence>";
				if (!contributorRole.equals(""))
					result += "<contributor-role>" + contributorRole + "</contributor-role>";
				result += "</contributor-attributes>";

				return result;
			}

		}

	}

}
