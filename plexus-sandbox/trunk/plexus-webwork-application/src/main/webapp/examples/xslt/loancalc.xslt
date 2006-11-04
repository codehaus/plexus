<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html" media-type="text/html" />
  <xsl:template match="result">
    <html>
      <head>
        <title>Loan Calculator</title>
      </head>

      <body>
        <h1>Loan Calculator</h1>

        <form>
          <table>
            <tr>
              <td>Present Value</td>

              <td>
                <input name="presentValue">
                  <xsl:attribute name="value">
                    <xsl:value-of select="presentValue" />
                  </xsl:attribute>
                </input>
              </td>
            </tr>

            <tr>
              <td>Interest Rate</td>

              <td>
                <input name="interestRate">
                  <xsl:attribute name="value">
                    <xsl:value-of select="interestRate" />
                  </xsl:attribute>
                </input>
              </td>
            </tr>

            <tr>
              <td>Instalment</td>

              <td>
                <input name="instalment">
                  <xsl:attribute name="value">
                    <xsl:value-of select="instalment" />
                  </xsl:attribute>
                </input>
              </td>
            </tr>

            <tr>
              <td>&#160;</td>

              <td>
                <input type="submit"
                value="Calculate monthly dues" />
              </td>
            </tr>
          </table>
        </form>


        <xsl:if test="hasErrorMessages = 'true'">

            <xsl:apply-templates select="errorMessages"/>

        </xsl:if>


        <xsl:apply-templates select="monthlyDues" />
      </body>
    </html>
  </xsl:template>

  <xsl:template match="monthlyDues">
    <table>
      <tr>
        <th>Month</th>

        <th>Present Value</th>

        <th>Interest</th>

        <th>Redemption</th>

        <th>Instalment</th>
      </tr>

      <xsl:apply-templates />
    </table>
  </xsl:template>

  <xsl:template match="monthlyDues/item">
    <tr>
      <xsl:if test="position() mod 2">
        <xsl:attribute name="bgcolor">#CCCCCC</xsl:attribute>
      </xsl:if>

      <td align="right">
        <xsl:value-of select="month" />
      </td>

      <td align="right">
        <xsl:value-of
        select="format-number(presentValue, '#0.00')" />
      </td>

      <td align="right">
        <xsl:value-of select="format-number(interest, '#0.00')" />
      </td>

      <td align="right">
        <xsl:value-of select="format-number(redemption, '#0.00')" />
      </td>

      <td align="right">
        <xsl:value-of select="format-number(instalment, '#0.00')" />
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="errorMessages/item">
    <p style="color: red;"><xsl:value-of select="text()"/></p>

  </xsl:template>
</xsl:stylesheet>
