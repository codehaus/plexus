package org.codehaus.plexus.webdav;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class RepositoryMapping
{
    /**
     * The local repository file system path.
     */
    private String repositoryPath;

    /**
     * An optional repository id field. This is not used by the dav proxy itself!
     */
    private String repositoryId;

    /**
     * The path within the repository.
     */
    private String logicalPath;

    public RepositoryMapping( String repositoryPath, String logicalPath )
    {
        this.repositoryPath = repositoryPath;
        this.logicalPath = logicalPath;
    }

    public RepositoryMapping( String repositoryPath, String repositoryId, String logicalPath )
    {
        this.repositoryPath = repositoryPath;
        this.repositoryId = repositoryId;
        this.logicalPath = logicalPath;
    }

    public String getRepositoryPath()
    {
        return repositoryPath;
    }

    public String getRepositoryId()
    {
        return repositoryId;
    }

    public String getLogicalPath()
    {
        return logicalPath;
    }
}
