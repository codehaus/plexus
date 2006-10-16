<?xml version="1.0"?>
<!--

 WebWork, Web Application Framework

 Distributable under Apache license.
 See terms of license at opensource.org

 This XSLT stylesheet renders any xml to a fancy html view.

 Author: Philipp Meier <meier@o-matic.de>
 Version: $Revision: 1.1 $

-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" media-type="text/html" />

<xsl:template  match="/">
<html>
    <head>
        <title>XML Showtree</title>
        <style type="text/css">
          body {
            background-color: white;
            color: black;
            font-size: 10pt;
            font-family: verdana, arial, sans-serif;
          }

          div.element {
            color: blue;
          }

          div.text {
            color: black;
          }

        </style>
    </head>
    <body>
        <xsl:apply-templates mode="showtree"/>
    </body>
</html>
</xsl:template>

<!-- match non-empty elements -->
<xsl:template match="*[*]|*[text()]|*[comment()]|*[processing-instruction()]" mode="showtree">
    <xsl:variable name="level">
        <xsl:value-of select="count(ancestor::*)" />
    </xsl:variable>
    <div class="element" style="margin-left: {$level*20}pt;">
        <xsl:value-of select="name(.)"/> <xsl:apply-templates select="@*" mode="showtree"/>
    </div>
    <xsl:apply-templates mode="showtree"/>
</xsl:template>

<!-- match empty elements -->
<xsl:template match="*" mode="showtree">
    <xsl:variable name="level">
        <xsl:value-of select="count(ancestor::*)" />
    </xsl:variable>
    <div class="element" style="margin-left: {$level*20}pt;">
        <xsl:value-of select="name(.)"/> <xsl:apply-templates select="@*" mode="showtree"/>
    </div>
</xsl:template>

<!-- match text -->
<xsl:template match="text()" mode="showtree">
    <xsl:variable name="level">
        <xsl:value-of select="count(ancestor::*)" />
    </xsl:variable>
    <div class="text" style="margin-left: {$level*20}pt;"><xsl:value-of select="." /></div>
</xsl:template>


<xsl:template match="@*" mode="showtree">
  <xsl:value-of select="concat(' ',name(.),'=')"/>&quot;<xsl:value-of select="." />&quot;
</xsl:template>

</xsl:stylesheet>
