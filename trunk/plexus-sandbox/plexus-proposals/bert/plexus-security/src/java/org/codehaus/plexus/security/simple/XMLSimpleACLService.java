package org.codehaus.plexus.security.simple;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.codehaus.plexus.configuration.XmlPullConfigurationBuilder;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * <p>Requires:
 * <ul>
 * 	<li>SimpleACLService</li>
 * </ul>
 * </p>
  * <p>Created on 21/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class XMLSimpleACLService extends AbstractLogEnabled implements Configurable, SimpleACLService
{
    private Map roleMap = new HashMap();

    private Map permissionMap = new HashMap();

    private Map roleUserMap = new HashMap();

    /**
     * 
     */
    public XMLSimpleACLService()
    {
        super();
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration config) throws ConfigurationException
    {
        Configuration[] files = config.getChildren("aclFile");
        for (int i = 0; i < files.length; i++)
        {
            String file = files[i].getValue();
            XmlPullConfigurationBuilder builder = new XmlPullConfigurationBuilder();

            Configuration conf;
            try
            {
                conf = builder.parse(new FileReader(file));
            }
            catch (FileNotFoundException e)
            {
                throw new ConfigurationException("The aclFile:'" + file + "' cannot be found");
            }
            catch (Exception e)
            {
                throw new ConfigurationException("Error parsing aclFile at:" + file, e);
            }
            configurePermissions(conf);
            configureRoles(conf);
            configureRoleUsers(conf);
        }
    }
    public SimpleACL buildACL(String userId)
    {
        //go through all the role-user entries and for the
        //ones which contain the given user, obtain the role
        //and add it to the users roles. 
        Iterator iter = roleUserMap.values().iterator();
        Collection roles = new Vector();

        while (iter.hasNext())
        {
            RoleUserEntry entry = (RoleUserEntry) iter.next();
            if (entry.containsUser(userId))            
            {
            	Role role = (Role) roleMap.get(entry.getRoleName());
				roles.add(role);
            }            
        }
        //construct the acl using the roles the user belongs to
        return new DefaultSimpleACL(roles, this);
    }

    /**
     * Create all the permissions from the configuration
     * 
     * @param config
     * @throws ConfigurationException
     */
    private void configurePermissions(Configuration config) throws ConfigurationException
    {
        Configuration[] perms = config.getChild("permissions").getChildren("perm");
        for (int i = 0; i < perms.length; i++)
        {
            Permission perm = new Permission();
            perm.setName(perms[i].getChild("name").getValue());
            perm.setDescription(perms[i].getChild("desc").getValue(null));
            //check no duplicate permissions
            if (permissionMap.containsKey(perm.getName()))
            {
                throw new ConfigurationException("Duplicate pemission:" + perm.getName());
            }
            permissionMap.put(perm.getName(), perm);
        }
    }

    /**
     * Verify the given configuration
     * 
     * @param config
     * @throws ConfigurationException
     */
    public static void verifyConfiguration(Configuration config) throws ConfigurationException
    {
        XMLSimpleACLService verifier = new XMLSimpleACLService();
        verifier.configure(config);
    }

    /**
     * Create all the roles from the configuration. All the permissions referenced by these roles
     * must already be created.
     * 
     * @param config
     * @throws ConfigurationException
     */
    private void configureRoles(Configuration config) throws ConfigurationException
    {
        Configuration[] roles = config.getChild("roles").getChildren("role");
        //iterate over the listed roles and build them
        for (int i = 0; i < roles.length; i++)
        {
            Role role = new Role();
            role.setName(roles[i].getAttribute("name"));
            role.setDescription(roles[i].getChild("desc").getValue( null ));
            if (roleMap.containsKey(role.getName()))
            {
                throw new ConfigurationException("Duplicate role:" + role.getName());
            }
            //iterate over the permissions this role claims it has and insert them into 
            //the role. Check the permission exists in the permissionsMap
            Configuration[] perms = roles[i].getChildren("perm");
            for (int j = 0; j < perms.length; j++)
            {
                grantPermissionsFromExpression(perms[j].getValue(), role);
            }
            roleMap.put(role.getName(), role);
        }
    }

    private void grantPermissionsFromExpression(String name, Role role)
        throws ConfigurationException
    {
        if (name == null)
        {
            return;
        }

        int indx = name.indexOf('*');
        if (indx == -1)
        {
            Permission perm = (Permission) permissionMap.get(name);
            if (perm == null)
            {
                throw new ConfigurationException(
                    "Role:"
                        + role.getName()
                        + " specifies it contains permission:"
                        + perm.getName()
                        + " but this permission does not exist");
            }
            role.grant(perm);
            return;
        }
        else
        {
            //we have a wildcard permission expressions. Find all the permissions
            //starting with the part before the wildcard
            //so 'login*' returns 'login'
            String prefix = name.substring(0, indx);
            Iterator perms = permissionMap.values().iterator();
            while (perms.hasNext())
            {
                Permission perm = (Permission) perms.next();
                if (perm.getName().startsWith(prefix))
                {
                    role.grant(perm);
                }

            }
        }
    }

    private void configureRoleUsers(Configuration config) throws ConfigurationException
    {
        //build all the RoleUser entries
        Configuration[] entries = config.getChild("acl-entries").getChildren("role");
        for (int i = 0; i < entries.length; i++)
        {
            String roleName = entries[i].getAttribute("name");
            Role role = (Role) roleMap.get(roleName);
            if (role == null)
            {
                throw new ConfigurationException(
                    "acl-entry specifies role:" + roleName + " but no such role exists");
            }
            //ensure we haven't already got an entry for this role
            if (roleUserMap.containsKey(roleName))
            {
                throw new ConfigurationException("Duplicate acl-entry. Duplicate role:" + roleName);
            }
            RoleUserEntry roleUser = new RoleUserEntry(roleName);
            //obtain all the users part of this RoleUser 
            Configuration[] users = entries[i].getChildren("user");
            for (int j = 0; j < users.length; j++)
            {
                String userId = users[j].getValue();
                roleUser.grant(userId);
            }
            roleUserMap.put(roleName, roleUser);
        }
    }

    /**
     * @see org.codehaus.plexus.security.rolebased.SimpleACLBuilder#getPermission(java.lang.String)
     */
    public Permission getPermission(String name)
    {
        return (Permission) permissionMap.get(name);
    }

    /**
     * @see org.codehaus.plexus.security.rolebased.SimpleACLBuilder#getRole(java.lang.String)
     */
    public Role getRole(String name)
    {
        return (Role) roleMap.get(name);
    }

    /**
     * @see org.codehaus.plexus.security.rolebased.SimpleACLBuilder#hasPermission(java.lang.String)
     */
    public boolean hasPermission(String name)
    {
        return permissionMap.containsKey(name);
    }

    /**
     * @see org.codehaus.plexus.security.rolebased.SimpleACLBuilder#hasRole(java.lang.String)
     */
    public boolean hasRole(String name)
    {
        return roleMap.containsKey(name);
    }

}
