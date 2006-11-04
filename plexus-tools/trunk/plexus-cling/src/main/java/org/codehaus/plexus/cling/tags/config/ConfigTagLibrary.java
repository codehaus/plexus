/* Created on Sep 15, 2004 */
package org.codehaus.plexus.cling.tags.config;

import org.codehaus.marmalade.metamodel.AbstractMarmaladeTagLibrary;

/**
 * @author jdcasey
 */
public class ConfigTagLibrary
    extends AbstractMarmaladeTagLibrary
{

    public ConfigTagLibrary()
    {
        registerTag("config", ConfigTag.class);
        registerTag("localRepository", LocalRepositoryTag.class);
        registerTag("remoteRepositories", RemoteRepositorySetTag.class);
        registerTag("remoteRepository", RemoteRepositoryTag.class);
    }

}
