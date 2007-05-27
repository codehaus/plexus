package org.codehaus.plexus.component.factory.groovy;

/**
 * Base-class to help ensure that extending Groovy classes works as expected.
 *
 * @version $Id$
 */
abstract class HelloSupport
    implements Hello
{
    void start()
    {
        System.out.println( "start()" );
    }

    void stop()
    {
        System.out.println( "stop()" );
    }

    void dispose()
    {
        System.out.println( "dispose()" );
    }

    void hello()
    {
        System.out.println( "hello!" );
    }
}
