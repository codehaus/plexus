package org.codehaus.jasf.impl;

import org.codehaus.jasf.resources.PageResource;

/**
 * XmlPageResource.java
 * 
 * @author <a href="dan@envoisolutions.com">Dan Diephouse</a>
 * @since Jan 22, 2003
 */
public class XmlPageResource
    extends PageResource
{
    String credential;
    
    /**
     * Returns the credential required to view this page.
     * @return String
     */
    public String getCredential()
    {
        return credential;
    }

    /**
     * Sets the credential required to view this page.
     * @param credential The credential to set
     */
    public void setCredential(String credential)
    {
        this.credential = credential;
    }

}
