<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html" media-type="text/html"/>

    <xsl:template match="result">
        <html>
            <head>
                <title>Webwork XSLT Demot - Simple Actions</title>
            </head>
            <body>

                <h1>Webwork XSLT Demot - Simple Actions</h1>
                <p>This demonstrates the capabilites of the new XSLTResult for webwork2.</p>

                <xsl:apply-templates select="scalar"/>

                <xsl:apply-templates select="multiList"/>

            </body>
        </html>
    </xsl:template>

    <xsl:template match="scalar">
        <h2>Scalar</h2>
        <p>This is the property scalar</p>
        <xsl:value-of select="."/>
    </xsl:template>

    <xsl:template match="multiList">
        <h2>MultiList</h2>
        <p>This is the property multiList</p>
        <ul>
            <xsl:for-each select="*">
                <li><xsl:apply-templates select="."/></li>
            </xsl:for-each>
        </ul>
    </xsl:template>

    <xsl:template match="list">
        <h2>List</h2>
        <p>This is a the property list</p>
        <ul>
            <xsl:for-each select="*">
                <li><xsl:apply-templates select="."/></li>
            </xsl:for-each>
        </ul>
    </xsl:template>

</xsl:stylesheet>
