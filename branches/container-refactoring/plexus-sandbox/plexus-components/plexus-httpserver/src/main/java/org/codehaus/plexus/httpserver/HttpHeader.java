/*
 * Created on 22/10/2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.codehaus.plexus.httpserver;

/**
 * Represents a single http header.
 * Immutable object.
 * @author  Ben Walding
 * @version $Id$
 */
public class HttpHeader
{
    private final String name;
    private final String value;
    
	public HttpHeader(String header) {
        if (header == null) {
         throw new IllegalArgumentException("header shall not be null");   
        }
        int colonPos = header.indexOf(":");
        if (colonPos == -1) {
          throw new IllegalArgumentException("Headers must have a ': ' (I think - check the spec"); //TODO check the spec to see if this is true   
        }
        name = header.substring(0, colonPos);
        String tvalue = header.substring(colonPos + 1); // ": "
        for (int i = 0; i < tvalue.length(); i++) {
          if (tvalue.charAt(i) != ' ') {
          	tvalue = tvalue.substring(i);
            break;
          }
        }        
        value = tvalue;
    }
    
	/**
	 * @return Returns the header name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return Returns the header value.
	 */
	public String getValue()
	{
		return value;
	}

}
