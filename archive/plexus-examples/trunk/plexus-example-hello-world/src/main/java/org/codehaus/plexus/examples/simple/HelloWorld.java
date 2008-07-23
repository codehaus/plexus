package org.codehaus.plexus.examples.simple;

/** 
 * This component produces a greeting.
 * 
 * @author Pete Kazmier
 * @version $Revision$
 */
public interface HelloWorld
{
    /** The role associated with the component. */
    public static final String ROLE = HelloWorld.class.getName();

    /** 
     * Says hello by returning a greeting to the caller.
     * 
     * @return A greeting.
     */
    public String sayHello();
}
