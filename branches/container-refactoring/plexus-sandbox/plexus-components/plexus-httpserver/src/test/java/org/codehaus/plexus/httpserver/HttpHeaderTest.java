package org.codehaus.plexus.httpserver;

import junit.framework.TestCase;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class HttpHeaderTest extends TestCase
{
	public void testNull()
	{
		try
		{
			new HttpHeader(null);
			fail("Should have thrown an IllegalArgumentException");
		}
		catch (IllegalArgumentException e)
		{
			//Expected   
		}
	}

	public void testNoColon()
	{
		try
		{
			new HttpHeader("a");
			fail("Should have thrown an IllegalArgumentException (no colon)");
		}
		catch (IllegalArgumentException e)
		{
			//Expected   
		}
	}

	public void testSimple()
	{
		testSimple("a:", "a", "");

		testSimple("a:b", "a", "b");
		testSimple("a: b", "a", "b");
		testSimple("a:   b", "a", "b");
		testSimple("a:      b", "a", "b");

		testSimple("a:      b:c", "a", "b:c");
        testSimple("Host: localhost:9999", "Host", "localhost:9999");
    }

	public void testSimple(String input, String expectedName, String expectedValue)
	{
		HttpHeader h = new HttpHeader(input);
		assertEquals("new Header(" + input + ").getName()", expectedName, h.getName());
		assertEquals("new Header(" + input + ").getName()", expectedValue, h.getValue());
	}
}
