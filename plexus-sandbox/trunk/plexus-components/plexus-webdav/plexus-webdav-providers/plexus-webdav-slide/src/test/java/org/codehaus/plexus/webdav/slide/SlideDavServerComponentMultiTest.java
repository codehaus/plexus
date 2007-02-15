package org.codehaus.plexus.webdav.slide;

import org.codehaus.plexus.webdav.test.AbstractMultiWebdavProviderTestCase;

/**
 * SlideDavServerComponentBasicTest
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id: SlideDavServerComponentBasicTest.java 5379 2007-01-07 22:54:41Z joakime $
 */
public class SlideDavServerComponentMultiTest
    extends AbstractMultiWebdavProviderTestCase
{
    public SlideDavServerComponentMultiTest()
    {
        super();
        setProviderHint( "slide" );
    }
}
