package org.codehaus.plexus.security.authentication.pap;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Map;

import javax.mail.internet.MimeUtility;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.codehaus.plexus.configuration.XmlPullConfigurationBuilder;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.security.authentication.AuthenticationException;
import org.codehaus.plexus.security.authentication.AuthenticationHandler;
import org.codehaus.plexus.util.ThreadSafeMap;

/**
  * Handles PAP tokens.
  * 
 * <p>Configuration format is as follows:
 * 
* <pre><![CDATA[
 *	<userFile>path/to/userfile.xml</userFile>
 *<userFile>path/to/another/userfile.xml</userFile>
 * ]]>
 * </pre>
 * 
 * where the user file is of the format:
 * 
 * <pre><![CDATA[
 *<pap-authentication>	
 *		<users>
 *			<user>
 *				<username></username>	
 *				<password></password>
 *				<id></id>
 *			</user>
 *			...
 *  	</users>
 * </pap-authentication>
 * ]]>
 * </pre>
 * </p>
  * <p>Created on 17/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class XMLPAPAuthenticationHandler
    extends AbstractLogEnabled
    implements AuthenticationHandler, Configurable
{
	
	/** the default algorithm for password encryption (SHA) */
	   public static final String DEFAULT_PASSWORD_ENCRYPTION_ALGORITHM = "SHA";
	   
	   public static final boolean DEFAULT_ENABLE_ENCRYPTION = false;
	   
	public static final boolean DEFAULT_IGNORE_CASE = false;
	
    /** User info keyed by username */
    private Map userInfoByUserName;

	/** If set to true passwords contained int he configuration file will be assumed to be encrypted */
	private boolean enableEncryption;
	
	/** The algorithm used to encrypt passwords */
	private String encryptAlgorithm;
	
	/** If true then password case is ignored */
	private boolean ignoreCase;
	
    /**
     * @see org.codehaus.plexus.security.authentication.AuthenticationHandler#authenticate(java.lang.Object)
     */
    public String authenticate(Object token) throws AuthenticationException
    {
        PAPToken tok = (PAPToken) token;
        getLogger().debug("[PAPAuthentication] authenticating user:" + tok.getUsername());
        UserInfo info = (UserInfo) userInfoByUserName.get(tok.getUsername());

        if (info == null)
        {
            getLogger().debug(
                "[PAPAuthentication] authentication failed for user: '"
                    + tok.getUsername()
                    + "' no such user");
            throw new AuthenticationException("No user with username:" + tok.getUsername());
        }
        if (passwordsMatch(tok.getPassword(), info.getPassword()))
        {
            getLogger().debug(
                "[PAPAuthentication] authentication successful for user: '"
                    + tok.getUsername()
                    + "'");
            return info.getId();
        }
        else
        {
            getLogger().debug(
                "[PAPAuthentication] authentication failed for user: '"
                    + tok.getUsername()
                    + "' incorrrect password");
            throw new AuthenticationException(
                "Incorrect password supplied for user:" + tok.getUsername());
        }
    }

    /**
     * @see org.codehaus.plexus.security.authentication.AuthenticationHandler#handlesToken()
     */
    public Class handlesToken()
    {
        return PAPToken.class;
    }

    private boolean passwordsMatch(String suppliedPassword, String storedPassword)
    {
        if (suppliedPassword == null || storedPassword == null)
        {
            return false;
        }
        if( ignoreCase )
        {
        	suppliedPassword = StringUtils.lowerCase(suppliedPassword);
        }
		if( enableEncryption )
		{
			//hash the supplied password and compare to the stored one (already hashed)
			suppliedPassword = encryptPassword(suppliedPassword);
			if( suppliedPassword != null)
			{
				return storedPassword.equals(suppliedPassword);				
			}
			else
			{
				return false;
			}
		}
		else
		{		
        	return storedPassword.equals(suppliedPassword);
		}
    }
    
	/**
	 * This method provides client-side encryption of passwords.
	 *
	 * If <code>secure.passwords</code> are enabled in TurbineResources,
	 * the password will be encrypted, if not, it will be returned unchanged.
	 * The <code>secure.passwords.algorithm</code> property can be used
	 * to chose which digest algorithm should be used for performing the
	 * encryption. <code>SHA</code> is used by default.
	 *
	 *Taken from turbines BaseSecurityService. Turbine can be found at 
	 *<a href="http://jakarta.apache.org/turbine/">http://jakarta.apache.org/turbine</a>
	 *
	 * @param password the password to process
	 * @return processed password
	 */
	public String encryptPassword(String password)
	{
		if (password == null)
		{
			return null;
		}
		if( ignoreCase )
		{
			password = StringUtils.lowerCase(password);
		}
		if (enableEncryption)
		{
			try
			{
				MessageDigest md = MessageDigest.getInstance(encryptAlgorithm);
				// We need to use unicode here, to be independent of platform's
				// default encoding. Thanks to SGawin for spotting this.
				byte[] digest = md.digest(password.getBytes("UTF-8"));
				ByteArrayOutputStream bas = new ByteArrayOutputStream(
						digest.length + digest.length / 3 + 1);
				OutputStream encodedStream = MimeUtility.encode(bas, "base64");
				encodedStream.write(digest);
				return bas.toString();
			}
			catch (Exception e)
			{
				getLogger().error("Unable to encrypt password.", e);
				return null;
			}
		}
		else
		{
			return password;
		}
	}
	
    /**
     * @see org.codehaus.plexus.security.authentication.AuthenticationHandler#handlesToken(java.lang.Class)
     */
    public boolean handlesToken(Class clazz)
    {
        return (PAPToken.class.isAssignableFrom(clazz));
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration config) throws ConfigurationException
    {
        Configuration[] files = config.getChildren("userFile");
        for (int i = 0; i < files.length; i++)
        {
            String file = files[i].getValue();
            XmlPullConfigurationBuilder builder = new XmlPullConfigurationBuilder();

            Configuration users;
            try
            {
                users = builder.parse(new FileReader(file));
            }
            catch (FileNotFoundException e)
            {
                throw new ConfigurationException("The userFile:'" + file + "' cannot be found");
            }
            catch (Exception e)
            {
                throw new ConfigurationException("Error parsing userFile at:" + file, e);
            }
            configureUsers(users);
        }		
		enableEncryption = config.getChild("password-encryption").getChild("enable").getValueAsBoolean(DEFAULT_ENABLE_ENCRYPTION);
		encryptAlgorithm= config.getChild("password-encryption").getChild("algorithm").getValue(DEFAULT_PASSWORD_ENCRYPTION_ALGORITHM);
		ignoreCase=config.getChild("password-encryption").getChild("ignorecase").getValueAsBoolean(DEFAULT_IGNORE_CASE);
    }

    private void configureUsers(Configuration config) throws ConfigurationException
    {
        ArrayList userIds = new ArrayList();
        userInfoByUserName = new ThreadSafeMap();

        Configuration[] users = config.getChild("users").getChildren("user");
        for (int i = 0; i < users.length; i++)
        {
            UserInfo info = new UserInfo();
            info.setUsername(users[i].getChild("username").getValue());
            info.setId(users[i].getChild("id").getValue());
            info.setPassword(users[i].getChild("password").getValue());
            if (userInfoByUserName.containsKey(info.getUsername()))
            {
                throw new ConfigurationException("Duplicate Username:" + info.getUsername());
            }
            if (userIds.contains(info.getId()))
            {
                throw new ConfigurationException("Duplicate UserId:" + info.getId());
            }
            userInfoByUserName.put(info.getUsername(), info);
            userIds.add(info.getId());
        }
    }

}

final class UserInfo
{
    private String id;
    private String username;
    private String password;

    /**
     * 
     */
    public UserInfo()
    {
        super();
    }

    /**
     * @return
     */
    public String getId()
    {
        return id;
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

    /**
     * @param string
     */
    public void setId(String string)
    {
        id = string;
    }

    /**
     * @param string
     */
    public void setPassword(String string)
    {
        password = string;
    }

    /**
     * @param string
     */
    public void setUsername(String string)
    {
        username = string;
    }

}
