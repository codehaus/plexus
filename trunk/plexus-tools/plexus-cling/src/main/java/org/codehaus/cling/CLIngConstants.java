/* Created on Sep 13, 2004 */
package org.codehaus.cling;

/**
 * @author jdcasey
 */
public final class CLIngConstants
{
    // - CLING SYSTEM PROPERTY KEYS -
    public static final String APPDIR_SYSPROP = "app.basedir";
    
    public static final String APPXML_SYSPROP = "app.xml.file";
    
    public static final String DEFAULT_APPXML_VALUE = "app.xml";
    // ------------------------------
    
    // - MARMALADE CONTEXT VARIABLE KEYS -
    public static final String PLEXUS_CONTAINER_CONTEXT_KEY = "plexus-container";

    public static final String LOCAL_REPOSITORY_CONTEXT_KEY = "localRepository";
    
    public static final String REMOTE_REPOSITORIES_CONTEXT_KEY = "remoteRepositories";
    // -----------------------------------

    private CLIngConstants()
    {
    }

}
