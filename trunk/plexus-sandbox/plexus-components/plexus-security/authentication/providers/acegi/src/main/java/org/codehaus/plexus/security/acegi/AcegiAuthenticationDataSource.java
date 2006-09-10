/**
 * 
 */
package org.codehaus.plexus.security.acegi;

import org.codehaus.plexus.security.authentication.AuthenticationDataSource;

import java.util.Map;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 *
 */
public class AcegiAuthenticationDataSource
    extends AuthenticationDataSource
{

    private Map tokenMap;

    public AcegiAuthenticationDataSource( String login, String password, Map tokenMap )
    {
        super( login, password );
        this.tokenMap = tokenMap;
    }

    /**
     * @return the tokenMap
     */
    public Map getTokenMap()
    {
        return tokenMap;
    }

    /**
     * @param tokenMap the tokenMap to set
     */
    public void setTokenMap( Map tokenMap )
    {
        this.tokenMap = tokenMap;
    }

}
