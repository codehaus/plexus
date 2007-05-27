package org.codehaus.plexus.component.factory.groovy;

/**
 * ???
 *
 * @version $Id$
 */
class HelloImpl
    extends HelloSupport
{
    void initialize()
    {
        System.out.println( "initialize()" );
    }
}
