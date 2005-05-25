package org.codehaus.plexus.summit.parameters;

import java.net.URLDecoder;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

/**
 * Summit implementation of a <tt>RequestParameterParser</tt>.  This
 * implementation uses the "Turbine"-style of parsing query info
 * and path info to assemble a parameter map.  The path info associated
 * with this request is assumed to be in the format of:
 * <pre>
 *     /param1/value1/param2/value2/param3/value3
 * </pre>
 * <p/>
 * This component is thread-safe.
 *
 * @plexus.component
 *
 * @plexus.role org.codehaus.plexus.summit.parameters.RequestParameterParser
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
 * @author <a href="mailto:sean@informage.net">Sean Legassick</a>
 * @author <a href="mailto:pete-apache-dev@kazmier.com">Pete Kazmier</a>
 * @version $Revision$
 */
public class SummitRequestParameterParser
    extends BaseRequestParameterParser
{
    /**
     * Parses the query info and path info for the parameters associated
     * with this request.  Please note that the parser assumes the path
     * info to be in the format described in the class documentation. If
     * you prefer to use the standard servlet method of parsing parameters,
     * see the {@link BaseRequestParameterParser}.
     *
     * @param request The HTTP request to parse for parameters.
     *                a RequestParameters object for easy access to the parameters.
     * @see org.codehaus.plexus.summit.parameters.RequestParameterParser#parse(javax.servlet.http.HttpServletRequest)
     */
    protected void doParse( HttpServletRequest request, Map parameterMap )
    {
        processPathInfo( request, parameterMap );
    }

    /**
     * Process the specified HTTP request for name/value pairs encoded within
     * the path info which are separated by a '/' character.  Each parsed pair
     * is stored in the supplied parameter map.
     *
     * @param request      The HTTP request to be parsed.
     * @param parameterMap The parameter map which is populated with the
     *                     parsed name/value pairs.
     * @todo Make the '/' character configurable and modify the parser so it
     * parses the data correctly.
     */
    private void processPathInfo( HttpServletRequest request, Map parameterMap )
    {
        try
        {
            String nameToken = null;
            boolean isNameToken = true;
            StringTokenizer tokenizer =
                new StringTokenizer( request.getPathInfo(), "/" );

            String encoding = request.getCharacterEncoding();
            if ( encoding == null )
                encoding = "UTF-8";

            while ( tokenizer.hasMoreTokens() )
            {
                if ( isNameToken )
                {
                    isNameToken = false;
                    nameToken = URLDecoder.decode( tokenizer.nextToken(), encoding );
                }
                else
                {
                    isNameToken = true;
                    if ( nameToken.length() > 0 )
                    {
                        addParameter( parameterMap,
                                      nameToken,
                                      URLDecoder.decode( tokenizer.nextToken(), encoding ) );
                    }
                }
            }
        }
        catch ( Exception e )
        {
        }
    }
}
