/* Created on Sep 13, 2004 */
package org.codehaus.cling.model;

import java.util.Properties;

/**
 * @author jdcasey
 */
public interface Application
{

    public Main getMain();

    public void setMain( Main main );
    
    public Properties getEnvironment();

    public void setEnvironment( Properties environment );
    
    public Classpath getClasspath();

    public void setClasspath( Classpath classpath );
    
    public LegalUsage getLegalUsage();
    
    public void setLegalUsage(LegalUsage legalUsage);

}
