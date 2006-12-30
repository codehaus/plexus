package org.codehaus.plexus.security.policy;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.security.configuration.UserConfiguration;

/**
 * TODO
 */
public abstract class AbstractCookieSettings
    implements CookieSettings, Initializable
{
    /**
     * @plexus.requirement
     */
    protected UserConfiguration config;

    /**
     * Timeout (in minutes) for the sign on cookie.
     */
    private int cookieTimeout;

    /**
     * The domain for the cookie.
     */
    private String domain;

    /**
     * The path for the cookie.
     */
    private String path;

    private static final int DEFAULT_COOKIE_TIMEOUT = 525600; // 1 year (365 days), in minutes

    public int getCookieTimeout()
    {
        return cookieTimeout;
    }

    public String getDomain()
    {
        return domain;
    }

    public String getPath()
    {
        return path;
    }

    public void initialize()
        throws InitializationException
    {
        String prefix = getConfigKeyPrefix();
        this.cookieTimeout = config.getInt( prefix + ".timeout", DEFAULT_COOKIE_TIMEOUT );
        this.domain = config.getString( prefix + ".domain" );
        this.path = config.getString( prefix + ".path" );
    }

    protected abstract String getConfigKeyPrefix();
}
