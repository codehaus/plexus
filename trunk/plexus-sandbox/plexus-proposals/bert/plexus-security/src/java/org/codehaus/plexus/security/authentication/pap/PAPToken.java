package org.codehaus.plexus.security.authentication.pap;

/**
  * Password Authentication Protocol (PAP) token
  * 
  * <p>Created on 17/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class PAPToken
{
	private String username;
	private String password;
	
    /**
     * 
     */
    public PAPToken(String username,String password)
    {
        super();
        this.username = username;
        this.password = password;
    }

    /**
     * @return
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * @return
     */
    public String getUsername()
    {
        return username;
    }

}
