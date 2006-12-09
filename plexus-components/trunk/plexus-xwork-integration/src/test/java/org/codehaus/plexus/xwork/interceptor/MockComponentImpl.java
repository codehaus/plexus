/**
 * 
 */
package org.codehaus.plexus.xwork.interceptor;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id$
 * @plexus.component 
 */
public class MockComponentImpl
    implements MockComponent
{

    /* (non-Javadoc)
     * @see org.codehaus.plexus.xwork.interceptor.TestComponent#execute()
     */
    public void displayResult( String result )
    {
        System.out.println( result );
    }

}
