/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.authority.orcid.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.orcid.jaxb.model.message.Citation;
import org.orcid.jaxb.model.message.CitationType;
import org.orcid.jaxb.model.message.Contributor;
import org.orcid.jaxb.model.message.ContributorEmail;
import org.orcid.jaxb.model.message.ContributorOrcid;
import org.orcid.jaxb.model.message.ContributorRole;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.Day;
import org.orcid.jaxb.model.message.Month;
import org.orcid.jaxb.model.message.PublicationDate;
import org.orcid.jaxb.model.message.Source;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.TranslatedTitle;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.Work;
import org.orcid.jaxb.model.message.WorkContributors;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierId;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.message.WorkExternalIdentifiers;
import org.orcid.jaxb.model.message.WorkTitle;
import org.orcid.jaxb.model.message.WorkType;
import org.orcid.jaxb.model.message.Year;
import org.orcid.jaxb.model.message.ContributorAttributeSequence;
import org.orcid.jaxb.model.common_v2.ContributorAttributes;
import org.dspace.authority.util.EnumUtils;
import org.dspace.authority.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Antoine Snyers (antoine at atmire.com)
 * @author Kevin Van de Velde (kevin at atmire dot com)
 * @author Ben Bosman (ben at atmire dot com)
 * @author Mark Diggory (markd at atmire dot com)
 */
public class XMLtoWork extends Converter {

    /**
     * log4j logger
     */
    private static Logger log = Logger.getLogger(XMLtoWork.class);

    /**
     * orcid-message XPATHs
     */

    protected String ORCID_WORKS = "//orcid-works";
    protected String ORCID_WORK = ORCID_WORKS + "/orcid-work";

    protected String WORK_TITLE = "work-title";
    protected String TITLE = WORK_TITLE + "/title";
    protected String SUBTITLE = WORK_TITLE + "/subtitle";
    protected String TRANSLATED_TITLES = WORK_TITLE + "/translated-title";
    protected String TRANSLATED_TITLES_LANGUAGE = "@language-code";

    protected String SHORT_DESCRIPTION = "short-description";

    protected String WORK_CITATION = "work-citation";
    protected String CITATION_TYPE = WORK_CITATION + "/work-citation-type";
    protected String CITATION = WORK_CITATION + "/citation";

    protected String WORK_TYPE = "work-type";

    protected String PUBLICATION_DATE = "publication-date";
    protected String YEAR = PUBLICATION_DATE + "/year";
    protected String MONTH = PUBLICATION_DATE + "/month";
    protected String DAY = PUBLICATION_DATE + "/day";

    protected String WORK_EXTERNAL_IDENTIFIERS = "work-external-identifiers";
    protected String WORK_EXTERNAL_IDENTIFIER = WORK_EXTERNAL_IDENTIFIERS + "/work-external-identifier";
    protected String WORK_EXTERNAL_IDENTIFIER_TYPE = "work-external-identifier-type";
    protected String WORK_EXTERNAL_IDENTIFIER_ID = "work-external-identifier-id";

    protected String URL = "url";

    protected String WORK_CONTRIBUTOR = "work-contributors";
    protected String CONTRIBUTOR = WORK_CONTRIBUTOR+"/contributor";
    protected String CONTRIBUTOR_ORCID = "contributor-orcid";
    protected String CREDIT_NAME = "credit-name";
    protected String CONTRIBUTOR_EMAIL = "contributor-email";
    protected String CONTRIBUTOR_ATTRIBUTES = "contributor-attributes";
    protected String CONTRIBUTOR_SEQUENCE = "contributor-sequence";
    protected String CONTRIBUTOR_ROLE = "contributor-role";

    protected String WORK_SOURCE = "work-source";


    public List<Work> convert(Document document) {
        List<Work> result = new ArrayList<Work>();

        if (XMLErrors.check(document)) {

            try {
                Iterator<Node> iterator = XMLUtils.getNodeListIterator(document, ORCID_WORK);
                while (iterator.hasNext()) {
                    Work work = convertWork(iterator.next());
                    result.add(work);
                }
            } catch (XPathExpressionException e) {
                log.error("Error in xpath syntax", e);
            }
        } else {
            processError(document);
        }

        return result;
    }

    protected Work convertWork(Node node) throws XPathExpressionException {
        Work work = new Work();
        setTitle(node, work);
        setDescription(node, work);
        setCitation(node, work);
        setWorkType(node, work);
        setPublicationDate(node, work);
        setExternalIdentifiers(node, work);
        setUrl(node, work);
        setContributors(node, work);
        setWorkSource(node, work);

        return work;
    }

    protected void setWorkSource(Node node, Work work) throws XPathExpressionException {
        String workSource = XMLUtils.getTextContent(node, WORK_SOURCE);
        //work.setWorkSource(workSource);
        work.setSource(new Source(workSource));
    }

    protected void setContributors(Node node, Work work) throws XPathExpressionException {

        List<Contributor> contributors = new ArrayList<Contributor>();

        Iterator<Node> iterator = XMLUtils.getNodeListIterator(node, CONTRIBUTOR);
        while (iterator.hasNext()) {
            Node nextContributorNode = iterator.next();
            String orcid = XMLUtils.getTextContent(nextContributorNode, CONTRIBUTOR_ORCID);
            String creditName = XMLUtils.getTextContent(nextContributorNode, CREDIT_NAME);
            String email = XMLUtils.getTextContent(nextContributorNode, CONTRIBUTOR_EMAIL);

            Set<ContributorAttributes> contributorAttributes = new HashSet<ContributorAttributes>();
            NodeList attributeNodes = XMLUtils.getNodeList(nextContributorNode, CONTRIBUTOR_ATTRIBUTES);
            Iterator<Node> attributesIterator = XMLUtils.getNodeListIterator(attributeNodes);
            while (attributesIterator.hasNext()) {
                Node nextAttribute = attributesIterator.next();

                String roleText = XMLUtils.getTextContent(nextAttribute, CONTRIBUTOR_ROLE);
                ContributorRole role = EnumUtils.lookup(ContributorRole.class, roleText);

                String sequenceText = XMLUtils.getTextContent(nextAttribute, CONTRIBUTOR_SEQUENCE);
                ContributorAttributeSequence sequence = EnumUtils.lookup(ContributorAttributeSequence.class, sequenceText);

                //Contributor attribute = new ContributorAttribute(role, sequence);
                //Contributor attribute = new Contributor(role, sequence);
//                Contributor attribute = new Contributor();
//                contributorAttributes.add(attribute);
            }

            //Contributor contributor = new Contributor(orcid, creditName, email, contributorAttributes);
            Contributor contributor = new Contributor();
            contributor.setContributorOrcid(new ContributorOrcid(orcid));
            contributor.setCreditName(new CreditName(creditName));
            contributor.setContributorEmail(new ContributorEmail(email));
            ContributorAttributes attributes = new ContributorAttributes();
            contributors.add(contributor);
            WorkContributors workContributor = new WorkContributors(contributors);
            work.setWorkContributors(workContributor);
        }
    }

    protected void setUrl(Node node, Work work) throws XPathExpressionException {
        String url = XMLUtils.getTextContent(node, URL);
        //work.setUrl(url);
        work.setUrl(new Url(url));
    }

    protected void setExternalIdentifiers(Node node, Work work) throws XPathExpressionException {

        Iterator<Node> iterator = XMLUtils.getNodeListIterator(node, WORK_EXTERNAL_IDENTIFIER);
        while (iterator.hasNext()) {
            Node work_external_identifier = iterator.next();
            String typeText = XMLUtils.getTextContent(work_external_identifier, WORK_EXTERNAL_IDENTIFIER_TYPE);

            WorkExternalIdentifierType type = EnumUtils.lookup(WorkExternalIdentifierType.class, typeText);

            String id = XMLUtils.getTextContent(work_external_identifier, WORK_EXTERNAL_IDENTIFIER_ID);

//            WorkExternalIdentifiers externalID;// = new WorkExternalIdentifier(type, id);
//            externalID.setWorkExternalIdentifierType(type);
//            WorkExternalIdentifiers external = new WorkExternalIdentifiers();
//            externalID.setWorkExternalIdentifiersId(new WorkExternalIdentifiers(id));
//            work.setWorkExternalIdentifiers(externalID);
            WorkExternalIdentifiers externalID = new WorkExternalIdentifiers();
            work.setWorkExternalIdentifiers(externalID);
        }
    }

    protected void setPublicationDate(Node node, Work work) throws XPathExpressionException {

        String year = XMLUtils.getTextContent(node, YEAR);
        String month = XMLUtils.getTextContent(node, MONTH);
        String day = XMLUtils.getTextContent(node, DAY);

//        String publicationDate = year;
//        if (StringUtils.isNotBlank(month)) {
//            publicationDate += "-" + month;
//            if (StringUtils.isNotBlank(day)) {
//                publicationDate += "-" + day;
//            }
//        }
        PublicationDate publicationDate = new PublicationDate();
        publicationDate.setYear(new Year(Integer.parseInt(year)));
        if (StringUtils.isNotBlank(month)) {
        	publicationDate.setMonth(new Month(Integer.parseInt(month)));
        	if (StringUtils.isNotBlank(day)) {
        		publicationDate.setDay(new Day(Integer.parseInt(day)));
        	}
        }

        work.setPublicationDate(publicationDate);
    }

    protected void setWorkType(Node node, Work work) throws XPathExpressionException {

        String workTypeText = XMLUtils.getTextContent(node, WORK_TYPE);
        WorkType workType = EnumUtils.lookup(WorkType.class, workTypeText);

        work.setWorkType(workType);
    }

    protected void setCitation(Node node, Work work) throws XPathExpressionException {

        String typeText = XMLUtils.getTextContent(node, CITATION_TYPE);
        CitationType type = EnumUtils.lookup(CitationType.class, typeText);

        String citationtext = XMLUtils.getTextContent(node, CITATION);

        Citation citation = new Citation();
        citation.setWorkCitationType(type);
        citation.setCitation(citationtext);
        work.setWorkCitation(citation);
    }

    protected void setDescription(Node node, Work work) throws XPathExpressionException {

        String description = null;
        description = XMLUtils.getTextContent(node, SHORT_DESCRIPTION);
        work.setShortDescription(description);
    }

    protected void setTitle(Node node, Work work) throws XPathExpressionException {

        String title = XMLUtils.getTextContent(node, TITLE);

        String subtitle = XMLUtils.getTextContent(node, SUBTITLE);

        Map<String, String> translatedTitles = new HashMap<String, String>();
        NodeList nodeList = XMLUtils.getNodeList(node, TRANSLATED_TITLES);
        Iterator<Node> iterator = XMLUtils.getNodeListIterator(nodeList);
        WorkTitle workTitle= new WorkTitle();//(title, subtitle, translatedTitles);
        workTitle.setTitle(new Title(title));
        while (iterator.hasNext()) {
            Node languageNode = iterator.next();
            String language = XMLUtils.getTextContent(languageNode, TRANSLATED_TITLES_LANGUAGE);
            String translated_title = XMLUtils.getTextContent(languageNode, ".");
            translatedTitles.put(language, translated_title);
            TranslatedTitle trans = new TranslatedTitle(translated_title);
            workTitle.setTranslatedTitle(trans);
        }
        
        work.setWorkTitle(workTitle);
    }

}
