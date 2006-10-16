package org.codehaus.plexus.httpserver;

import junit.framework.TestCase;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class HttpConstantsTest extends TestCase
{
	public void testSimple()
	{
		assertEquals("OK", HttpConstants.getDefaultMessage(HttpConstants.HTTP_OK));
	}
}
