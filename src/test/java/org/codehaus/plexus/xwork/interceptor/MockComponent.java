/**
 * 
 */
package org.codehaus.plexus.xwork.interceptor;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 *
 */
public interface MockComponent
{
    String ROLE = MockComponent.class.getName();

    public void displayResult( String result );

}
