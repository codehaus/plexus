package org.codehaus.plexus.webdav;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface RepositoryMapper
{
    RepositoryMapping getRepositoryMapping( HttpServletRequest request )
        throws Exception;
}
