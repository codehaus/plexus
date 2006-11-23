package org.codehaus.plexus.component.factory.jruby;

import java.lang.reflect.Method;

import junit.framework.TestCase;

public class JRubyTestCase extends TestCase
{
    protected void runTest()
        throws Throwable
    {
        // super.runTest();
        System.out.println( getClass() );

        for ( int i = 0; i < getClass().getMethods().length; i++ )
        {
            Method method = getClass().getMethods()[i];

            System.out.println( method.getName() );
        }
    }
}
