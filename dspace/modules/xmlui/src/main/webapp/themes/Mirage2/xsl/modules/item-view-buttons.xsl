<!--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

-->
<!--
    Rendering specific to the item display page.

    Author: Adan Roman
    Author Sergio Nieto

-->

<xsl:stylesheet
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
    xmlns:dri="http://di.tamu.edu/DRI/1.0/"
    xmlns:mets="http://www.loc.gov/METS/"
    xmlns:dim="http://www.dspace.org/xmlns/dspace/dim"
    xmlns:xlink="http://www.w3.org/TR/xlink/"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:atom="http://www.w3.org/2005/Atom"
    xmlns:ore="http://www.openarchives.org/ore/terms/"
    xmlns:oreatom="http://www.openarchives.org/ore/atom/"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:xalan="http://xml.apache.org/xalan"
    xmlns:encoder="xalan://java.net.URLEncoder"
    xmlns:util="org.dspace.app.xmlui.utils.XSLUtils"
    xmlns:jstring="java.lang.String"
    xmlns:rights="http://cosimo.stanford.edu/sdr/metsrights/"
    xmlns:confman="org.dspace.core.ConfigurationManager"
    xmlns:wos="es.arvo.wos.WokUtils"
    exclude-result-prefixes="xalan encoder i18n dri mets dim xlink xsl util jstring rights wos">

    <xsl:output indent="yes"/>
    
     <xsl:template name="itemSummaryView-DIM-social-red">      	
		<xsl:variable name="link" select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='request'][@qualifier='URI']" />
		<div class="simple-view-icons ocultar">
		
			<!-- AddThis Button BEGIN -->

		
			<!-- Go to www.addthis.com/dashboard to customize your tools -->
<!-- 			<div class="addthis_sharing_toolbox"></div> -->
<!-- Go to www.addthis.com/dashboard to generate a new set of sharing buttons -->

<a href="https://api.addthis.com/oexchange/0.8/forward/print/offer?url={$link}&amp;pubid=ra-51b82912054799e1&amp;ct=1&amp;title=Arias%20Montano%20-%20Repositorio%20Institucional%20de%20la%20Universidad%20de%20Huelva&amp;pco=tbxnj-1.0" target="_blank"><img src="https://cache.addthiscdn.com/icons/v3/thumbs/32x32/print.png" border="0" alt="Print"/></a>
<a href="https://api.addthis.com/oexchange/0.8/forward/facebook/offer?url=http%3A%2F%2Fwww.addthis.com&amp;pubid=ra-51b82912054799e1&amp;ct=1&amp;title=Arias%20Montano%20-%20Repositorio%20Institucional%20de%20la%20Universidad%20de%20Huelva&amp;pco=tbxnj-1.0" target="_blank"><img src="https://cache.addthiscdn.com/icons/v3/thumbs/32x32/facebook.png" border="0" alt="Facebook"/></a>
<a href="https://api.addthis.com/oexchange/0.8/forward/twitter/offer?url=http%3A%2F%2Fwww.addthis.com&amp;pubid=ra-51b82912054799e1&amp;ct=1&amp;title=Arias%20Montano%20-%20Repositorio%20Institucional%20de%20la%20Universidad%20de%20Huelva&amp;pco=tbxnj-1.0" target="_blank"><img src="https://cache.addthiscdn.com/icons/v3/thumbs/32x32/twitter.png" border="0" alt="Twitter"/></a>
<a href="https://www.addthis.com/bookmark.php?source=tbx32nj-1.0&amp;v=300&amp;url=http%3A%2F%2Fwww.addthis.com&amp;pubid=ra-51b82912054799e1&amp;ct=1&amp;title=Arias%20Montano%20-%20Repositorio%20Institucional%20de%20la%20Universidad%20de%20Huelva&amp;pco=tbxnj-1.0" target="_blank"><img src="https://cache.addthiscdn.com/icons/v3/thumbs/32x32/addthis.png" border="0" alt="Addthis"/></a>

			
			<!-- Go to www.addthis.com/dashboard to customize your tools -->
			
			<script type="text/javascript" async="async">
				<xsl:attribute name="src" >
	                <xsl:text>https://s7.addthis.com/js/300/addthis_widget.js#pubid=ra-51b82912054799e1</xsl:text>
                </xsl:attribute>
				<xsl:text>&#160;</xsl:text>
			</script>
			
			
		</div>	
	</xsl:template>

	  <!-- Almetrics -->
         
      <xsl:template name="itemSummaryView-altmetrics">      
		  <xsl:param name="link" select="//@OBJID" />
		  <xsl:param name="doi" select="//dim:field[@element='identifier'][@qualifier='doi']"/>
		    <script type='text/javascript' src='https://d1bxh8uas1mnw7.cloudfront.net/assets/embed.js'>&#160;</script>
			<div class="simple-view-icons">
			<xsl:choose>
			<xsl:when test="$doi">
			<span title="Almetrics" data-badge-popover="bottom" data-badge-type="donut"  data-hide-no-mentions="false" class="altmetric-embed">
				<xsl:attribute name="data-doi">
					 <xsl:value-of select="$doi"/>
				</xsl:attribute>
			</span>
			</xsl:when>
			<xsl:otherwise>
			<span title="Almetrics" data-badge-popover="bottom" data-badge-type="donut"  data-hide-no-mentions="false" class="altmetric-embed">
				<xsl:attribute name="data-handle">
					 <xsl:value-of select="substring-after($link,'handle/')"/>
				</xsl:attribute>
			</span>
			</xsl:otherwise>
			</xsl:choose>
		</div>
	  </xsl:template>
      
      <!-- Scope Cite -->
      
      <xsl:template name="itemSummaryView-scope-cite">      
	      <xsl:param name="doi" select="//dim:field[@element='identifier'][@qualifier='doi']"/>
	       <xsl:param name="apikey" select="confman:getProperty('scopus.apikey')"/>
		  	<xsl:param name="link" select="//@OBJID" />	
		    
		 <xsl:if test="$doi">
		 	<div class="simple-view-icons">
		  		<span title="Scopus" id="citedBy">
		        <object class="scopusImage" height="40">
			        <xsl:attribute name="data">
			        	<xsl:text>http://api.elsevier.com/content/abstract/citation-count?doi=</xsl:text>
			        	<xsl:value-of select="$doi"></xsl:value-of>
			        	<xsl:text>&amp;httpAccept=image/jpeg&amp;apiKey=</xsl:text>
			        	<xsl:value-of select="$apikey"></xsl:value-of>
			        </xsl:attribute>
		        </object>
	        	</span>

			</div>
	       </xsl:if>
	      
	  </xsl:template>
	<xsl:variable name="contextPath">
    	<xsl:value-of select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='contextPath']"/>    		
    </xsl:variable>
    
    <xsl:variable name="uri">
    	<xsl:value-of select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='request'][@qualifier='URI']"/>    		
    </xsl:variable>
	<xsl:template name="itemSummaryView-DIM-exports">
		<xsl:param name="link" select="//@OBJID" />
			<div id="export_buttons">
				<center>
					<a id="refwork_logo">					
					<xsl:attribute name="href">					
						<xsl:value-of select="confman:getProperty('refworks.server.url')"/>
						<xsl:value-of select="confman:getProperty('dspace.url')"/>					
						<xsl:text>/refworks/handle/</xsl:text>
						<xsl:value-of select="substring-after($uri,'handle/')"/>
					</xsl:attribute>
					<img>
						<xsl:attribute name="src">
							<xsl:value-of select="$contextPath"/>
							<xsl:text>/static/images/RefWorks.jpg</xsl:text>
						</xsl:attribute>
						<xsl:attribute name="alt">						
							<xsl:text>Refworks</xsl:text>
						</xsl:attribute>
						<xsl:attribute name="title">						
							<xsl:text>Refworks</xsl:text>
						</xsl:attribute>
					</img>			
					</a>
					<a id="bibtex">
						<xsl:attribute name="href">
							<xsl:value-of
								select="concat(substring-before($link,'handle'),'bibtex/handle',substring-after($link,'handle'))" />
						</xsl:attribute>
						<img>
							<xsl:attribute name="src">
								<xsl:value-of select="$context-path"/>
								<xsl:text>/themes/Mirage2/images/bibtex.png</xsl:text>
							</xsl:attribute>
							<xsl:attribute name="alt">
								<xsl:text>xmlui.BibliographyReader.bibtex.alt</xsl:text>
							</xsl:attribute>
							<xsl:attribute name="title">
								<xsl:text>xmlui.BibliographyReader.bibtex.alt</xsl:text>
							</xsl:attribute>
							<xsl:attribute name="i18n:attr">
								<xsl:text>alt title</xsl:text>
							</xsl:attribute>
							
						</img>
					</a>
				</center>
			</div>
	</xsl:template>
	
</xsl:stylesheet>
