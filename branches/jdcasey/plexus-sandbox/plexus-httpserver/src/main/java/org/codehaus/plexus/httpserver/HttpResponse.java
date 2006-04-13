package org.codehaus.plexus.httpserver;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Maven" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Maven", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * ====================================================================
 */

import java.io.InputStream;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class HttpResponse
{
	private InputStream inputStream;
	private String contentType = "application/octet-stream";
	private long contentLength = -1;
	private int statusCode;
	private String statusMessage;

	/**
	 * @return Returns the inputStream.
	 */
	public InputStream getInputStream()
	{
		return inputStream;
	}

	/**
	 * @param inputStream The inputStream to set.
	 */
	public void setInputStream(InputStream inputStream)
	{
		this.inputStream = inputStream;
	}

	/**
	 * @return Returns the mimeType.
	 */
	public String getContentType()
	{
		return contentType;
	}

	/**
	 * @param mimeType The mimeType to set.
	 */
	public void setContentType(String mimeType)
	{
		this.contentType = mimeType;
	}

	/**
	 * @return Returns the size.
	 */
	public long getContentLength()
	{
		return contentLength;
	}

	/**
	 * @param size The size to set.
	 */
	public void setContentLength(long contentLength)
	{
		this.contentLength = contentLength;
	}

	/**
	 * @return Returns the statusCode.
	 */
	public int getStatusCode()
	{
		return statusCode;
	}

	/**
	 * @param statusCode The statusCode to set.
	 */
	public void setStatusCode(int statusCode)
	{
		this.statusCode = statusCode;
	}

	/**
	 * @return Returns the statusMessage.
	 */
	public String getStatusMessage()
	{
		if (statusMessage == null)
		{
			statusMessage = HttpConstants.getDefaultMessage(statusCode);
		}
		return statusMessage;
	}

	/**
	 * @param statusMessage The statusMessage to set.
	 */
	public void setStatusMessage(String statusMessage)
	{
		this.statusMessage = statusMessage;
	}

}
