package org.codehaus.jasf.impl.basic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.commons.digester.AbstractObjectCreationFactory;
import org.apache.commons.digester.Digester;
import org.codehaus.jasf.Authenticator;
import org.codehaus.jasf.exception.AuthenticationException;
import org.codehaus.jasf.exception.UnknownEntityException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * UserAuthenticationController loads in a defined set of users
 * and credentials via an XML file.
 * 
 * @author Dan Diephouse
 * @since Nov 22, 2002 
 */
public class UserAuthenticationController extends AbstractLogEnabled
    implements Authenticator, Configurable, Initializable
{
    // Where the users are stored after initialization
    List users;
    
    // Where the roles are stored after initialization
    List roles;
    
    // The digester which reads in the users and their roles
    Digester userDigester;
    
    // The digester which reads in the roles
    Digester roleDigester;
     
    private File rolesFile;

    private File usersFile;

    public UserAuthenticationController()
    {
    }
        
    /**
     * @see org.apache.fulcrum.jasf.Authenticator#authenticate(String, String)
     */
    public Object authenticate(String entityname, String password)
        throws UnknownEntityException, AuthenticationException
    {
        Iterator itr = users.iterator();
        while (itr.hasNext())
        {
            BasicUser user = (BasicUser) itr.next();

            if (user.getUserName().equals(entityname))
            {
                if(user.getPassword().equals(password))
                    return user;
                else
                    throw new AuthenticationException();
            }
        }
        
        throw new UnknownEntityException();
    }

    /**
     * @see org.apache.fulcrum.jasf.EntityAuthenticationController#getAnonymousEntity()
     */
    public Object getAnonymousEntity()
    {
        return new BasicUser();
    }
    
    /**
     * Lookup a <code>Role</code> by its name.
     * 
     * @param name
     * @return Role
     */
    public Role getRole( String name )
    {
        getLogger().debug("Retrieving role: " + name);
        Iterator itr = roles.iterator();
        while (itr.hasNext())
        {
            Role r = (Role) itr.next();
            if (r.getName().equals(name))
                return r;
        }
        
        // TODO: Throw exception instead
        return null;
    }
    
    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration config) throws ConfigurationException
    {
        usersFile = new File( config.getChild("users").getValue() );
        rolesFile = new File( config.getChild("roles").getValue() );
    }
    
    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        // Set up the Digest to load in the roles file.
        roleDigester = new Digester();

        roleDigester.addObjectCreate("roles", ArrayList.class);
        roleDigester.addObjectCreate("roles/role", Role.class);
        roleDigester.addSetNext("roles/role", "add", ArrayList.class.getName());
        roleDigester.addCallMethod("roles/role/name", "setName", 0);
        roleDigester.addCallMethod("roles/role/credentials/credential", "addCredential", 0);        

        // Set up the digester to load in the users file.
        userDigester = new Digester();

        userDigester.addObjectCreate("users", ArrayList.class);
        userDigester.addObjectCreate("users/user", BasicUser.class);
        userDigester.addSetNext("users/user", "add", ArrayList.class.getName());
        userDigester.addCallMethod("users/user/username", "setUserName", 0);
        userDigester.addCallMethod("users/user/password", "setPassword", 0);
        userDigester.addFactoryCreate( "users/user/roles/role",
            new RoleCreationFactory(this) );
        userDigester.addSetNext("users/user/roles/role", "addRole", Role.class.getName());
        
        try
        {
            InputStream rolesStream = new FileInputStream( rolesFile );
            roles = (List) roleDigester.parse( rolesStream );
        }
        catch (FileNotFoundException e)
        {
            getLogger().error("Could not find the roles file.");
            throw new ConfigurationException("Could not find the roles file!");
        } catch (IOException e)
        {
            getLogger().error("Error reading the roles file.");
            throw new ConfigurationException("Error reading the roles file.");
        } catch (SAXException e)
        {
            getLogger().error("Error parsing the roles file.");
            throw new ConfigurationException("Error parsing the roles file.");
        }

        try
        {
            InputStream usersStream = new FileInputStream(usersFile);
            users = (List) userDigester.parse( usersStream );
        }
        catch (FileNotFoundException e)
        {
            getLogger().error("Could not find the users file.");
            throw new ConfigurationException("Could not find the users file!");
        } catch (IOException e)
        {
            getLogger().error("Error reading the users file.");
            throw new ConfigurationException("Error reading the users file.");
        } catch (SAXException e)
        {
            getLogger().error("Error parsing the users file.");
            throw new ConfigurationException("Error parsing the users file.");
        }        
    }

    /**
     * A factory that pulls the Role objects as needed.
     * 
     * @author <a href="dan@envoisolutions.com">Dan Diephouse</a>
     * @since Jan 23, 2003
     */
    protected class RoleCreationFactory extends AbstractObjectCreationFactory
    {
        private UserAuthenticationController _controller;
    
        public RoleCreationFactory( UserAuthenticationController controller )
        {
            _controller = controller;
        }
    
        /**
         * @see org.apache.commons.digester.ObjectCreationFactory#createObject(org.xml.sax.Attributes)
         */
        public Object createObject(Attributes att) throws Exception
        {
            String name = att.getValue("name");
            return _controller.getRole(name);
        }

    }
}
