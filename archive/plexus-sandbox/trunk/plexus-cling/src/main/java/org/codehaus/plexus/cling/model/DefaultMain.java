/* Created on Sep 13, 2004 */
package org.codehaus.plexus.cling.model;

/**
 * @author jdcasey
 */
public class DefaultMain
    implements Main
{

    private String mainMethod;
    private String mainClass;

    public DefaultMain()
    {
    }
    
    public void setMainMethod(String mainMethod) 
    {
        this.mainMethod = mainMethod;
    }
    
    public void setMainClass(String mainClass)
    {
        this.mainClass = mainClass;
    }

    public String getMainClass()
    {
        return mainClass;
    }

    public String getMainMethod()
    {
        return mainMethod;
    }

}
