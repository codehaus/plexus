/* Created on Sep 13, 2004 */
package org.codehaus.cling.model;

import java.util.Properties;


/**
 * @author jdcasey
 */
public class DefaultApplication
    implements Application
{

    private Main main;
    private Properties environment;
    private Classpath classpath;
    private LegalUsage legalUsage;

    public DefaultApplication()
    {
    }

    public Main getMain()
    {
        return main;
    }

    public void setMain( Main main )
    {
        this.main = main;
    }

    public Properties getEnvironment()
    {
        return environment;
    }

    public void setEnvironment( Properties environment )
    {
        this.environment = environment;
    }

    public Classpath getClasspath()
    {
        return classpath;
    }

    public void setClasspath( Classpath classpath )
    {
        this.classpath = classpath;
    }

    public LegalUsage getLegalUsage()
    {
        return legalUsage;
    }

    public void setLegalUsage( LegalUsage legalUsage )
    {
        this.legalUsage = legalUsage;
    }

}
