package org.codehaus.plexus.httpserver;

import junit.framework.TestCase;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class HttpParameterTest extends TestCase
{
	public void testEmpty()
	{
		try
		{
			new HttpParameter("");
			fail("Should have thrown illegal argument exception");
		}
		catch (IllegalArgumentException e)
		{
			//Success   
		}
	}

	public void testNull()
	{
		try
		{
			new HttpParameter(null);
			fail("Should have thrown an IllegalArgumentException");
		}
		catch (IllegalArgumentException e)
		{
			//Expected   
		}
	}

	public void testSimple()
	{
        testSimple("a", "a", null);
        testSimple("a=", "a", "");
		testSimple("ab=", "ab", "");
		testSimple("a=b", "a", "b");
		testSimple("=b", "", "b");
	}

	private void testSimple(String input, String expectedName, String expectedValue)
	{
        HttpParameter p = new HttpParameter(input);
		assertEquals(input + "a.name", expectedName, p.getName());
		assertEquals(input + "a.value", expectedValue, p.getValue());
	}
}
