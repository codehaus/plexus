package org.codehaus.plexus.httpserver;

import java.io.IOException;

import org.codehaus.plexus.util.StringInputStream;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class MockHttpBehaviour implements HttpBehaviour
{

    /* (non-Javadoc)
     * @see org.apache.maven.proxy.standalone.http.AbstractHttpServer#getResponse(org.apache.maven.proxy.standalone.http.Request)
     */
    public HttpResponse getResponse(HttpRequest request) throws IOException
    {
        HttpResponse r = new HttpResponse();
        r.setContentType("text/html");

        if (request.getResource().equals("/"))
        {
            String i = "HELLO THIS IS HTML";
            r.setInputStream(new StringInputStream(i));
            r.setContentLength(i.length());
            r.setStatusCode(HttpConstants.HTTP_ACCEPTED);
            return r;
        }

        if (request.getResource().equals("/404a"))
        {
            r.setStatusCode(HttpConstants.HTTP_NOT_FOUND);
            return r;
        }

        if (request.getResource().equals("/404b"))
        {
            r.setStatusCode(HttpConstants.HTTP_NOT_FOUND);
            r.setStatusMessage("Couldn't find your stupid file");
            return r;
        }

        if (request.getResource().equals("/800"))
        {
            r.setStatusCode(800);
            r.setStatusMessage("Couldn't find your stupid file");
            return r;
        }

        r.setStatusCode(HttpConstants.HTTP_NOT_FOUND);
        return r;
    }

}
