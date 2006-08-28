package org.codehaus.plexus.httpserver;

/**
 * @author  Ben Walding
 * @version $Id$
 */
class HttpParameter
{
	private String name;
	private String value;
	public HttpParameter(String s)
	{
		if (s == null || s.length() == 0)
		{
			throw new IllegalArgumentException("Empty parameter string");
		}

		int equalPos = s.indexOf('=');

		if (equalPos == -1)
		{
			name = s;
			value = null;
		}
		else
		{
			name = s.substring(0, equalPos); //TODO decode
			value = s.substring(equalPos + 1); //TODO decode
		}
	}

	/**
	 * @return Returns the name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return Returns the value.
	 */
	public String getValue()
	{
		return value;
	}
}
