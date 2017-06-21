<!-- The contents of this file are subject to the license and copyright detailed 
	in the LICENSE and NOTICE files at the root of the source tree and available 
	online at http://www.dspace.org/license/ -->
<!-- Rendering specific to the item display page. Author: Adan Roman -->

<xsl:stylesheet xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
	xmlns:dri="http://di.tamu.edu/DRI/1.0/" xmlns:mets="http://www.loc.gov/METS/"
	xmlns:dim="http://www.dspace.org/xmlns/dspace/dim" xmlns:xlink="http://www.w3.org/TR/xlink/"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:atom="http://www.w3.org/2005/Atom" xmlns:ore="http://www.openarchives.org/ore/terms/"
	xmlns:oreatom="http://www.openarchives.org/ore/atom/" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:xalan="http://xml.apache.org/xalan" xmlns:encoder="xalan://java.net.URLEncoder"
	xmlns:util="org.dspace.app.xmlui.utils.XSLUtils" xmlns:jstring="java.lang.String"
	xmlns:rights="http://cosimo.stanford.edu/sdr/metsrights/"
	xmlns:confman="org.dspace.core.ConfigurationManager" xmlns:wos="es.arvo.wos.WokUtils"
	exclude-result-prefixes="xalan encoder i18n dri mets dim xlink xsl util jstring rights wos">

	<xsl:output indent="yes" />

	<xsl:template name="share_links">
		<!-- Go to www.addthis.com/dashboard to customize your tools -->
		<script type="text/javascript"
			src="//s7.addthis.com/js/300/addthis_widget.js#pubid=ra-589afda1c66476e5"></script>
	</xsl:template>

	<xsl:template name="itemSummaryView-DIM-exports">
		<xsl:param name="link" select="//@OBJID" />
		<h5><i18n:text>xmlui.dri2xhtml.METS-1.0.export</i18n:text></h5>
		<div id="export_buttons">
			<div class="btn-group export">
				<a>
					<xsl:attribute name="href">
					<xsl:value-of
						select="concat(substring-before($link,'handle'),'ris/handle',substring-after($link,'handle'))" />
				</xsl:attribute>
					<img>
						<xsl:attribute name="src">
							<xsl:value-of select="concat($theme-path,'/images/ris.png')"></xsl:value-of>
						</xsl:attribute>
						<xsl:attribute name="alt">
							<xsl:text>xmlui.BibliographyReader.rislogo.alt</xsl:text>
						</xsl:attribute>
						<xsl:attribute name="title">
							<xsl:text>xmlui.BibliographyReader.rislogo.alt</xsl:text>
						</xsl:attribute>
						<xsl:attribute name="i18n:attr">
							<xsl:text>alt title</xsl:text>
						</xsl:attribute>
					</img>
				</a>
			</div>
		</div>
	</xsl:template>

</xsl:stylesheet>