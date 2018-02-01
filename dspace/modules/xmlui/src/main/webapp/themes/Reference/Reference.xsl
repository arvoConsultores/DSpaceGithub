<?xml version="1.0" encoding="UTF-8"?>
<!--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

-->

<xsl:stylesheet 
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
    xmlns:dri="http://di.tamu.edu/DRI/1.0/"
    xmlns:mets="http://www.loc.gov/METS/"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:dim="http://www.dspace.org/xmlns/dspace/dim"
    xmlns:mods="http://www.loc.gov/mods/v3"
    xmlns:xlink="http://www.w3.org/TR/xlink/"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    
    <xsl:import href="../dri2xhtml.xsl"/>
    <xsl:import href="lib/core/page-structure.xsl" />
    <xsl:output indent="yes"/>
    
         
    <!-- 
        This xsl is provided only as an example of over ridding selected templates. 
        Uncomment the following to add ">" to the end of all trail links, currently 
        only browsers which support the pseudo element of ":after" are able to 
        render the ">" trail postfix.
        
        Remember to remove the pseudo element from the CSS when uncommenting!
    -->
         
    <!--
    <xsl:template match="dri:trail">
        <li>
            <xsl:attribute name="class">
                <xsl:text>ds-trail-link </xsl:text>
                <xsl:if test="position()=1">
                    <xsl:text>first-link</xsl:text>
                </xsl:if>
                <xsl:if test="position()=last()">
                    <xsl:text>last-link</xsl:text>
                </xsl:if>
            </xsl:attribute>
            <xsl:choose>
                <xsl:when test="./@target">
                    <a>
                        <xsl:attribute name="href">
                            <xsl:value-of select="./@target"/>
                        </xsl:attribute>
                        <xsl:apply-templates />
                    </a>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates />
                </xsl:otherwise>
            </xsl:choose>
            &gt;
        </li>
    </xsl:template>
    -->



	<!--Iconos añadidos en la barra de menu-->
   <xsl:template match="dri:options">
        <div id="ds-options">        	
            <h3 id="ds-search-option-head" class="ds-option-set-head"><i18n:text>xmlui.dri2xhtml.structural.search</i18n:text></h3>
            <div id="ds-search-option" class="ds-option-set">
                <!-- The form, complete with a text box and a button, all built from attributes referenced
                    from under pageMeta. -->
                <form id="ds-search-form" method="post">
                    <xsl:attribute name="action">
                        <xsl:value-of select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='contextPath']"/>
                        <xsl:value-of select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='search'][@qualifier='simpleURL']"/>
                    </xsl:attribute>
                    <fieldset>
                        <input class="ds-text-field " type="text">
                            <xsl:attribute name="name">
                                <xsl:value-of select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='search'][@qualifier='queryField']"/>
                            </xsl:attribute>
                        </input>
                        <input class="ds-button-field " name="submit" type="submit" i18n:attr="value" value="xmlui.general.go" >
                            <xsl:attribute name="onclick">
                                <xsl:text>
                                    var radio = document.getElementById(&quot;ds-search-form-scope-container&quot;);
                                    if (radio != undefined &amp;&amp; radio.checked)
                                    {
                                    var form = document.getElementById(&quot;ds-search-form&quot;);
                                    form.action=
                                </xsl:text>
                                <xsl:text>&quot;</xsl:text>
                                <xsl:value-of select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='contextPath']"/>
                                <xsl:text>/handle/&quot; + radio.value + &quot;</xsl:text>
                                <xsl:value-of select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='search'][@qualifier='simpleURL']"/>
                                <xsl:text>&quot; ; </xsl:text>
                                <xsl:text>
                                    }
                                </xsl:text>
                            </xsl:attribute>
                        </input>
                        <xsl:if test="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='focus'][@qualifier='container']">
                            <label>
                                <input id="ds-search-form-scope-all" type="radio" name="scope" value="" checked="checked"/>
                                <i18n:text>xmlui.dri2xhtml.structural.search</i18n:text>
                            </label>
                            <br/>
                            <label>
                                <input id="ds-search-form-scope-container" type="radio" name="scope">
                                    <xsl:attribute name="value">
                                        <xsl:value-of select="substring-after(/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='focus'][@qualifier='container'],':')"/>
                                    </xsl:attribute>
                                </input>
                                <xsl:choose>
                                    <xsl:when test="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='focus'][@qualifier='containerType']/text() = 'type:community'">
                                            <i18n:text>xmlui.dri2xhtml.structural.search-in-community</i18n:text>
                                    </xsl:when>
                                    <xsl:otherwise>
                                            <i18n:text>xmlui.dri2xhtml.structural.search-in-collection</i18n:text>
                                    </xsl:otherwise>
                                                                                              
                                </xsl:choose>
                            </label>
                        </xsl:if>
                    </fieldset>
                </form>
                <!--Only add if the advanced search url is different from the simple search-->
                <xsl:if test="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='search'][@qualifier='advancedURL'] != /dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='search'][@qualifier='simpleURL']">
                    <!-- The "Advanced search" link, to be perched underneath the search box -->
                    <a>
                        <xsl:attribute name="href">
                            <xsl:value-of select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='search'][@qualifier='advancedURL']"/>
                        </xsl:attribute>
                        <i18n:text>xmlui.dri2xhtml.structural.search-advanced</i18n:text>
                    </a>
                </xsl:if>
            </div>
            
            <!-- Once the search box is built, the other parts of the options are added -->
            <xsl:apply-templates />

            <!-- DS-984 Add RSS Links to Options Box -->
            <xsl:if test="count(/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='feed']) != 0">
                <h3 id="ds-feed-option-head" class="ds-option-set-head">
                    <i18n:text>xmlui.feed.header</i18n:text>
                </h3>
                <div id="ds-feed-option" class="ds-option-set">
                    <ul>
                        <xsl:call-template name="addRSSLinks"/>
                    </ul>
                </div>
            </xsl:if>
            
            <h3 id="ds-feed-option-head" class="ds-option-set-head">Enlaces</h3>
            <div id="ds-feed-option" class="ds-option-set">
            	<a href="http://recolecta.fecyt.es/" target="_blank"><img style=" border: 0 ; width: 100px; height: 25px; text-align: center ; hspace: 100px"
            	alt="Recolecta" title="Recolecta" src="/xmlui/themes/Reference/images/recolecta.jpg"/></a>
            	<a href="http://es.creativecommons.org/" target="_blank"><img alt="Creative Commons" title="Creative Commons" src="/xmlui/themes/Reference/images/creative.jpg"/></a>
            	<a href="http://www.accesoabierto.net/dulcinea/" target="_blank"><img alt="Dulcinea" title="Dulcinea" src="/xmlui/themes/Reference/images/dulcinea.jpg"/></a>
            	<a href="http://www.sherpa.ac.uk/romeo/index.php?la=es" target="_blank"><img alt="Sherpa Romeo" title="Sherpa Romeo" src="/xmlui/themes/Reference/images/sherpa-romeo.jpg"/></a>
            </div>

        </div>
    </xsl:template>      
         
    <!--Quitar enlace defectuoso del Creative Commons-->

    <xsl:template match="mets:fileGrp[@USE='CC-LICENSE' or @USE='LICENSE']">
        
    </xsl:template>     

	
	    <!-- Generate the info about the item from the metadata section -->
    <xsl:template match="dim:dim" mode="itemSummaryList-DIM"> 
        <xsl:variable name="itemWithdrawn" select="@withdrawn" />

        <div class="artifact-description">
		   <div class="artifact-title">
                <xsl:element name="a">
                    <xsl:attribute name="href">
                        <xsl:choose>
                            <xsl:when test="$itemWithdrawn">
                                <xsl:value-of select="ancestor::mets:METS/@OBJEDIT" />
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="ancestor::mets:METS/@OBJID" />
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                    <xsl:choose>
                        <xsl:when test="dim:field[@mdschema='dc' and @element='title'] and (string-length(dim:field[@element='title']) &gt; 0)">
                            <xsl:value-of select="dim:field[@mdschema='dc' and @element='title'][1]/node()"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <i18n:text>xmlui.dri2xhtml.METS-1.0.no-title</i18n:text>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:element>
               <!-- Generate COinS with empty content per spec but force Cocoon to not create a minified tag  -->
               <span class="Z3988">
                   <xsl:attribute name="title">
                       <xsl:call-template name="renderCOinS"/>
                   </xsl:attribute>
                   &#xFEFF; <!-- non-breaking space to force separating the end tag -->
               </span>
           </div>
            <div class="artifact-info">
                <span class="author">
                    <xsl:choose>
                        <xsl:when test="dim:field[@element='contributor'][@qualifier='author']">
                            <xsl:for-each select="dim:field[@element='contributor'][@qualifier='author']">
                                <span>
                                  <xsl:if test="@authority">
                                    <xsl:attribute name="class"><xsl:text>ds-dc_contributor_author-authority</xsl:text></xsl:attribute>
                                  </xsl:if>
                                  <xsl:copy-of select="node()"/>
                                </span>
                                <xsl:if test="count(following-sibling::dim:field[@element='contributor'][@qualifier='author']) != 0">
                                    <xsl:text>; </xsl:text>
                                </xsl:if>
                            </xsl:for-each>
                        </xsl:when>
                        <xsl:when test="dim:field[@element='creator']">
                            <xsl:for-each select="dim:field[@element='creator']">
                                <xsl:copy-of select="node()"/>
                                <xsl:if test="count(following-sibling::dim:field[@element='creator']) != 0">
                                    <xsl:text>; </xsl:text>
                                </xsl:if>
                            </xsl:for-each>
                        </xsl:when>
                        <xsl:when test="dim:field[@element='contributor']">
                            <xsl:for-each select="dim:field[@element='contributor']">
                                <xsl:copy-of select="node()"/>
                                <xsl:if test="count(following-sibling::dim:field[@element='contributor']) != 0">
                                    <xsl:text>; </xsl:text>
                                </xsl:if>
                            </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                            <i18n:text>xmlui.dri2xhtml.METS-1.0.no-author</i18n:text>
                        </xsl:otherwise>
                    </xsl:choose>
                </span>
                <xsl:text> </xsl:text>
                <xsl:if test="dim:field[@element='date' and @qualifier='issued'] or dim:field[@element='publisher']">
	                <span class="publisher-date">
	                    <xsl:text>(</xsl:text>
	                    <xsl:if test="dim:field[@element='publisher']">

				 <xsl:for-each select="dim:field[@element='publisher']">
	                       		 <span class="publisher">
	                           	 <!--<xsl:copy-of select="dim:field[@element='publisher']/node()"/>-->
					<xsl:copy-of select="node()"/>
					
					 <xsl:if test="count(following-sibling::dim:field[@element='publisher']) != 0">
                                    		<xsl:text> </xsl:text>
                                	</xsl:if>
	                        	</span>
				</xsl:for-each>
	                        <xsl:text>, </xsl:text>
	                    </xsl:if>
	                    <span class="date">
	                        <xsl:value-of select="substring(dim:field[@element='date' and @qualifier='issued']/node(),1,10)"/>
	                    </span>
	                    <xsl:text>)</xsl:text>
	                </span>
                </xsl:if>
            </div>
        </div>
    </xsl:template>
    
<!-- remove search area on home page --> 
<xsl:template match="dri:div[@n='front-page-search']"> 
 <xsl:apply-templates select="@pagination"> 
 <xsl:with-param name="position">bottom</xsl:with-param> 
 </xsl:apply-templates> 
</xsl:template> 

<!-- remove community list -->
<!--   <xsl:template name="disable_frontpage_browse" 
match="dri:div[@id='aspect.artifactbrowser.CommunityBrowser.div.comunity-browser']"> 
   </xsl:template> -->
		           
</xsl:stylesheet>
