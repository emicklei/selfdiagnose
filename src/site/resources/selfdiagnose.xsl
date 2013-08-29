<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html" indent="yes" encoding="US-ASCII"/>
  <xsl:decimal-format decimal-separator="." grouping-separator="," />
  <xsl:template match="selfdiagnose">
    <html>
      <head>
		<link 
			rel="stylesheet" 
			media="screen, projection, print" 
			type="text/css" 
			href="selfdiagnose.css"/>
      </head>
      <body>
        <a name="top"></a>
        <xsl:call-template name="results"/>
        <h4>
        	<a href="http://selfdiagnose.sourceforge.net">SelfDiagnose</a>
        	[<xsl:value-of select="@version"/>] report generated on <xsl:value-of select="@run" />        	 
        	for context <xsl:value-of select="@context"/>.
        	
        </h4>
      </body>
    </html>
  </xsl:template>  
  
 <xsl:template name="results">
    <table>
    <tr valign="top">
      <th>Task</th>
      <th>Status</th>
      <th>Message</th>
    </tr>
    <xsl:apply-templates/>
    </table>
  </xsl:template>
  
  <xsl:template match="result">
  	   <tr>
    	<xsl:attribute name="class"><xsl:value-of select="@status"/></xsl:attribute>
    	<td><xsl:value-of select="@task"/></td>
    	<td><xsl:value-of select="@status"/></td>
    	<td><xsl:value-of select="@message"/></td>
  		</tr>
  </xsl:template>    
  
</xsl:stylesheet>  