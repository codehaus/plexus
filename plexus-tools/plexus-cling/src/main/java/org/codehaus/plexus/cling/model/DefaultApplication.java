/* Created on Sep 13, 2004 */
package org.codehaus.plexus.cling.model;

import java.util.Properties;


/**
 * @author jdcasey
 */
public class DefaultApplication
    implements AppModel
{

    private Main main;
    private Properties environment;
    private Classpath classpath;
    private LegalUsage legalUsage;
    private String argDescription;
    private String appDescription;

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
        Properties env = new Properties();
        env.putAll(environment);
        
        this.environment = env;
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

    public void setApplicationDescription( String appDescription )
    {
        this.appDescription = appDescription;
    }

    public String getApplicationDescription()
    {
        return appDescription;
    }

    public void setArgumentDescription( String argDescription )
    {
        this.argDescription = argDescription;
    }

    public String getArgumentDescription()
    {
        return argDescription;
    }

}
