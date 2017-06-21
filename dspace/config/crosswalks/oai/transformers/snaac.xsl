<?xml version="1.0" encoding="UTF-8"?>
<!-- 


    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/
    
	Developed by DSpace @ Lyncode <dspace@lyncode.com> 
	Following Driver Guidelines 2.0:
		- http://www.driver-support.eu/managers.html#guidelines

 -->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:doc="http://www.lyncode.com/xoai">
	<xsl:output indent="yes" method="xml" omit-xml-declaration="yes" />

	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>
 
 	<!-- Formatting dc.date.issued -->
	<xsl:template match="/doc:metadata/doc:element[@name='dc']/doc:element[@name='date']/doc:element[@name='issued']/doc:element/doc:field/text()">
		<xsl:call-template name="formatdate">
			<xsl:with-param name="datestr" select="." />
		</xsl:call-template>
	</xsl:template>
	
	<!-- Removing other dc.date.* -->
	<xsl:template match="/doc:metadata/doc:element[@name='dc']/doc:element[@name='date']/doc:element[@name!='issued']" />

	<!-- Prefixing dc.type -->
	<xsl:template match="/doc:metadata/doc:element[@name='dc']/doc:element[@name='type']/doc:element/doc:field/text()">
		<xsl:call-template name="addPrefix">
			<xsl:with-param name="value" select="." />
			<xsl:with-param name="prefix" select="'info:eu-repo/semantics/'"></xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- Prefixing dc.type.hasVersion -->
	<xsl:template match="/doc:metadata/doc:element[@name='dc']/doc:element[@name='type']/doc:element[@name='hasVersion']/doc:element/doc:field/text()">
		<xsl:call-template name="addPrefix">
			<xsl:with-param name="value" select="." />
			<xsl:with-param name="prefix" select="'info:eu-repo/semantics/'"></xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<!-- Prefixing and Modifying dc.rights.accessRights -->
	<!-- Removing unwanted -->
	<xsl:template match="/doc:metadata/doc:element[@name='dc']/doc:element[@name='rights']/doc:element[@name!='accessRights']" />
	<!-- Replacing -->
        <xsl:template match="/doc:metadata/doc:element[@name='dc']/doc:element[@name='rights']/doc:element[@name='accessRights']/doc:element/doc:field/text()">
		<xsl:choose>
			<xsl:when test="contains(., 'openAccess')">
				<xsl:text>info:eu-repo/semantics/openAccess</xsl:text>
			</xsl:when>
			<xsl:when test="contains(., 'restrictedAccess')">
				<xsl:text>info:eu-repo/semantics/restrictedAccess</xsl:text>
			</xsl:when>
			<xsl:when test="contains(., 'embargoedAccess')">
				<xsl:text>info:eu-repo/semantics/embargoedAccess</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>info:eu-repo/semantics/restrictedAccess</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	


	<!-- AUXILIARY TEMPLATES -->
	
	<!-- dc.type prefixing -->
	<xsl:template name="addPrefix">
		<xsl:param name="value" />
		<xsl:param name="prefix" />
		<xsl:choose>
		<!-- ARVO: Solo ponemos prefijo a los tipos adecuados -->
			<xsl:when test="starts-with($value,'article')">
				<xsl:value-of select="concat($prefix, $value)" />
			</xsl:when>
			<xsl:when test="starts-with($value,'masterThesis')">
				<xsl:value-of select="concat($prefix, $value)" />
			</xsl:when>
			<xsl:when test="starts-with($value,'doctoralThesis')">
				<xsl:value-of select="concat($prefix, $value)" />
			</xsl:when>
			<xsl:when test="starts-with($value,'report')">
				<xsl:value-of select="concat($prefix, $value)" />
			</xsl:when>
			<xsl:when test="starts-with($value,'acceptedVersion')">
				<xsl:value-of select="concat($prefix, $value)" />
			</xsl:when>
			<xsl:when test="starts-with($value,'draft')">
				<xsl:value-of select="concat($prefix, $value)" />
			</xsl:when>
			<xsl:when test="starts-with($value,'publishedVersion')">
				<xsl:value-of select="concat($prefix, $value)" />
			</xsl:when>
			<xsl:when test="starts-with($value,'submittedVersion')">
				<xsl:value-of select="concat($prefix, $value)" />
			</xsl:when>
			<xsl:when test="starts-with($value,'updatedVersion')">
				<xsl:value-of select="concat($prefix, $value)" />
			</xsl:when>
			<!-- <xsl:when test="starts-with($value, $prefix)">
				<xsl:value-of select="$value" />
			</xsl:when>-->
			<xsl:otherwise>
				<xsl:value-of select="$value" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- Date format -->
	<xsl:template name="formatdate">
		<xsl:param name="datestr" />
		<xsl:variable name="sub">
			<xsl:value-of select="substring($datestr,1,10)" />
		</xsl:variable>
		<xsl:value-of select="$sub" />
	</xsl:template>
</xsl:stylesheet>
