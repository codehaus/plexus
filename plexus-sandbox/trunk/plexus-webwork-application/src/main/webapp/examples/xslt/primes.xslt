<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" media-type="text/html" />

<xsl:template match="result">
<html>
<head>
<title>Primes from <xsl:value-of select="start"/> to <xsl:value-of select="end"/></title>
</head>
<body>

<h1>Primes from <xsl:value-of select="start"/> to <xsl:value-of select="end"/></h1>
    <xsl:apply-templates select="primes" />
</body>
</html>
</xsl:template>

<xsl:template match="primes/item">
	<xsl:value-of select="format-number(position(), '0000')"/>:
	<xsl:value-of select="text()" /><br/><xsl:text>
	</xsl:text>
</xsl:template>
</xsl:stylesheet>
