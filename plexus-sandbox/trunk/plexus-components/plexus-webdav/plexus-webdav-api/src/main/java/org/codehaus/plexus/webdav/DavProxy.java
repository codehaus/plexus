package org.codehaus.plexus.webdav;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface DavProxy
{
    String ROLE = DavProxy.class.getName();

    void service( HttpServletRequest httpServletRequest,
                  HttpServletResponse httpServletResponse,
                  RepositoryMapper repositoryMapper )
        throws ServletException, IOException;
}
