package org.codehaus.jasf.impl.basic;

/**
 * BasicEntity is an interface which all entities that seek to use this
 * package's <code>ResourceAccessController</code>s must implement.
 * 
 * @author <a href="dan@envoisolutions.com">Dan Diephouse</a>
 * @since Jan 22, 2003
 */
public interface BasicEntity
{
    /**
     * See if the user has the specified credential.
     * 
     * @param credential
     * @return boolean
     */
    public boolean hasCredential( String credential );
}
