                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         package org.codehaus.plexus.security.authentication;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.plexus.security.Agent;


/**
  * 
  * <p>Created on 12/08/2003</p>
  *
  * @author <a href="mailto:bertvanbrakel@sourceforge.net">Bert van Brakel</a>
  * @revision $Revision$
  */
public abstract class AbstractAuthenticationHandler implements AuthenticationHandler
{
	private Map tokens = new HashMap();
	
    /**
     * @see nz.co.bonzo.zirco.application.security.AuthenticationTokenHandler#getAgentId(java.lang.Object)
     */
    public String getAgentId(Object token)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see nz.co.bonzo.zirco.application.security.AuthenticationTokenHandler#handlesToken(java.lang.Class)
     */
    public boolean handlesToken(Class clazz)
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see nz.co.bonzo.zirco.application.security.AuthenticationTokenHandler#newToken(java.lang.String)
     */
    public abstract Object newToken(Agent user);
    
    protected abstract void validateToken(Object token);
    protected void addToken(Object token,Agent user)
    {
    	tokens.put(token,user);
    	
    }
    
    public String identifiesAgent(Object token)
    {
    	return (String)tokens.get(token);    	
    }

}
