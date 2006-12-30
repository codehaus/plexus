package org.codehaus.plexus.webdav;

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class SimpleRepositoryMapper
    implements RepositoryMapper
{
    private File repositoryRoot;

    private String prefix;

    public SimpleRepositoryMapper( File repositoryRoot )
    {
        this.repositoryRoot = repositoryRoot;
    }

    public SimpleRepositoryMapper( File repositoryRoot, String prefix )
    {
        this.repositoryRoot = repositoryRoot;
        this.prefix = trimTrailingSlashes( prefix );
    }

    // -----------------------------------------------------------------------
    // RepositoryMapper Implementation
    // -----------------------------------------------------------------------

    public RepositoryMapping getRepositoryMapping( HttpServletRequest request )
        throws Exception
    {
        String prefix = getPrefix( request );

        String requestPathInfo = trimTrailingSlashes( request.getPathInfo() );

        if ( StringUtils.isEmpty( requestPathInfo ) || StringUtils.equals( "/", requestPathInfo ) ||
            StringUtils.equals( prefix, requestPathInfo ) )
        {
            return new RepositoryMapping( repositoryRoot.getAbsolutePath(), "/" );
        }

        requestPathInfo = trimTrailingSlashes( requestPathInfo );

        return new RepositoryMapping( repositoryRoot.getAbsolutePath(), requestPathInfo );
    }

    /**
     * Splits up the logical path (<code>/repository/foo/bar</code>) into:
     * <ul>
     *   <li>A <i>repository id</i>: the <code>/repository</code> part, </li>
     *   <li>A new <i>logical path</i>: the <code>/foo/bar</code> part, </li>
     * </ul>
     */
    public static RepositoryMapping splitRepositoryIdFromPath( RepositoryMapping mapping )
    {
        String requestPathInfo = FileUtils.normalize( mapping.getLogicalPath() );

        // Remove prefixing slash as the repository id doesn't contain it;
        if ( requestPathInfo.startsWith( "/" ) )
        {
            requestPathInfo = requestPathInfo.substring( 1 );
        }

        // Find first element, if slash exists.
        int slash = requestPathInfo.indexOf( '/' );
        if ( slash > 0 )
        {
            // Filtered: "central/org/apache/maven/" -> "central"
            String repositoryId = requestPathInfo.substring( 0, slash );

            String repoPath = requestPathInfo.substring( slash );

            if ( repoPath.endsWith( "/.." ) )
            {
                repoPath += "/";
            }

            if ( repoPath == null )
            {
                repoPath = "/";
            }

            return new RepositoryMapping( mapping.getRepositoryPath(), repositoryId, repoPath );
        }
        else
        {
            return new RepositoryMapping( mapping.getRepositoryPath(), requestPathInfo, "/" );
        }
    }

    // -----------------------------------------------------------------------
    // Private
    // -----------------------------------------------------------------------

    private String getPrefix( HttpServletRequest request )
    {
        if ( prefix != null )
        {
            return prefix;
        }

        return trimTrailingSlashes( request.getServletPath() );
    }

    private String trimTrailingSlashes( String string )
    {
        while ( string.endsWith( "/" ) )
        {
            string = string.substring( 0, string.length() - 1 );
        }

        return string;
    }
}
