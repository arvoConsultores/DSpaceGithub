<?xml version="1.0" encoding="utf-8"?>
<!-- sord-swap-ingest.xsl UFV
 * 
 * Copyright (c) 2007, Aberystwyth University
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above
 *    copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 *  - Neither the name of the Centre for Advanced Software and
 *    Intelligent Systems (CASIS) nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 -->

<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:dim="http://www.dspace.org/xmlns/dspace/dim"
        xmlns:epdcx="http://purl.org/eprint/epdcx/2006-11-16/"
        version="1.0">

<!-- NOTE: This stylesheet is a work in progress, and does not
     cover all aspects of the SWAP and EPDCX specification/schema.
     It is used principally to demonstrate the SWORD ingest
     process -->

<!-- This stylesheet converts incoming DC metadata in a SWAP
     profile into the DSpace Interal Metadata format (DIM) -->

	<!-- Catch all.  This template will ensure that nothing
	     other than explicitly what we want to xwalk will be dealt
	     with -->
	<xsl:template match="text()"></xsl:template>
    
    <!-- match the top level descriptionSet element and kick off the
         template matching process -->
    <xsl:template match="/epdcx:descriptionSet">
    	<dim:dim>
    		<xsl:apply-templates/>
    	</dim:dim>
    </xsl:template>
    
    <!-- general matcher for all "statement" elements -->
    <xsl:template match="/epdcx:descriptionSet/epdcx:description/epdcx:statement">
    
    	<!-- title element: dc.title -->
    	<xsl:if test="./@epdcx:propertyURI='http://purl.org/dc/elements/1.1/title'">
    		<dim:field mdschema="dc" element="title">
    			<xsl:value-of select="epdcx:valueString"/>
    		</dim:field>
    	</xsl:if>
    	
    	<!-- abstract element: dc.description.abstract -->
    	<xsl:if test="./@epdcx:propertyURI='http://purl.org/dc/terms/abstract'">
    		<dim:field mdschema="dc" element="description" qualifier="abstract">
    			<xsl:value-of select="epdcx:valueString"/>
    		</dim:field>
    	</xsl:if>
    
	<!-- creator element: dc.contributor.author -->
    	<xsl:if test="./@epdcx:propertyURI='http://purl.org/dc/elements/1.1/creator'">
    		<dim:field mdschema="dc" element="contributor" qualifier="author">
    			<xsl:value-of select="epdcx:valueString"/>
    		</dim:field>
    	</xsl:if>

<!-- language element: dc.language.iso -->
    	<xsl:if test="./@epdcx:propertyURI='http://purl.org/dc/elements/1.1/language' and ./@epdcx:vesURI='http://purl.org/dc/terms/iso'">
    	    		<dim:field mdschema="dc" element="language" qualifier="iso">
    	    		    			<xsl:value-of select="epdcx:valueString"/>
    	    		    			    		</dim:field>
    	    		    			    		    	</xsl:if>

        <!-- subject (keywords) element: dc.subject -->
    	<xsl:if test="./@epdcx:propertyURI='http://purl.org/dc/elements/1.1/subject'">
    	    		<dim:field mdschema="dc" element="subject">
    	    		    			<xsl:value-of select="epdcx:valueString"/>
    	    		    			    		</dim:field>
    	    		    			    		    	</xsl:if>


<!-- publisher element: dc.publisher -->
    	<xsl:if test="./@epdcx:propertyURI='http://purl.org/dc/terms/publisher'">
    	    		<dim:field mdschema="dc" element="publisher">
    	    		    			<xsl:value-of select="epdcx:valueString"/>
    	    		    			    		</dim:field>
    	    		    			    		    	</xsl:if>


<!-- rights element: dc.rights -->
    	<xsl:if test="./@epdcx:propertyURI='http://purl.org/dc/terms/rights'">
    	    		<dim:field mdschema="dc" element="rights">
    	    		    			<xsl:value-of select="epdcx:valueString"/>
    	    		    			    		</dim:field>

    	    		    			    		    	</xsl:if>

<!-- issn: dc.identifier.issn -->
        <xsl:if test="./@epdcx:propertyURI='http://purl.org/dc/elements/1.1/issn'">
            <dim:field mdschema="dc" element="identifier" qualifier="issn">
                <xsl:value-of select="epdcx:valueString"/>
            </dim:field>
        </xsl:if>	

<!-- doi: dc.identifier.doi -->
        <xsl:if test="./@epdcx:propertyURI='http://purl.org/dc/elements/1.1/doi'">
            <dim:field mdschema="dc" element="identifier" qualifier="doi">
                <xsl:value-of select="epdcx:valueString"/>
            </dim:field>
        </xsl:if>

 <!-- provenance : dc.description.provenance -->

        <xsl:if test="./@epdcx:propertyURI='http://purl.org/dc/terms/provenance'">
            <dim:field mdschema="dc" element="description" qualifier="provenance" >
                <xsl:value-of select="epdcx:valueString"/>
            </dim:field>
        </xsl:if>
		
	<!-- dc.relation.ispartof -->	
		<xsl:if test="./@epdcx:propertyURI='http://purl.org/dc/terms/bibliographicCitation'">
            <dim:field mdschema="dc" element="relation" qualifier="ispartof" >
                <xsl:value-of select="epdcx:valueString"/>
            </dim:field>
        </xsl:if>

	
	<!--ARVO metadatos genericos UFV-->
	
	<!--dc.rights.URI -->
	<xsl:if test="./@epdcx:propertyURI='http://purl.org/dc/terms/uri'">
            <dim:field mdschema="dc" element="rights" qualifier="uri" >
                <xsl:value-of select="epdcx:valueString"/>
            </dim:field>
        </xsl:if>
		
		<!--dc.type -->
		<xsl:if test="./@epdcx:propertyURI='http://purl.org/dc/terms/type'">
            <dim:field mdschema="dc" element="type" >
                <xsl:value-of select="epdcx:valueString"/>
            </dim:field>
        </xsl:if>
		
		<!--dc.description.version -->
		<xsl:if test="./@epdcx:propertyURI='http://purl.org/dc/terms/version'">
            <dim:field mdschema="dc" element="description" qualifier="version" >
                <xsl:value-of select="epdcx:valueString"/>
            </dim:field>
        </xsl:if>
		
		<!--dc.rights.accessRights -->
		<xsl:if test="./@epdcx:propertyURI='http://purl.org/dc/terms/access'">
            <dim:field mdschema="dc" element="rights" qualifier="accessRights" >
                <xsl:value-of select="epdcx:valueString"/>
            </dim:field>
        </xsl:if>
		


    </xsl:template>
    
</xsl:stylesheet>
