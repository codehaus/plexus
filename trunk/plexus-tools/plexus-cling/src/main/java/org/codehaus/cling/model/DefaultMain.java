/* Created on Sep 13, 2004 */
package org.codehaus.cling.model;

/**
 * @author jdcasey
 */
public class DefaultMain
    implements Main
{

    private final String mainMethod;
    private final String mainClass;

    public DefaultMain(String mainClass, String mainMethod)
    {
        this.mainClass = mainClass;
        this.mainMethod = mainMethod;
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
