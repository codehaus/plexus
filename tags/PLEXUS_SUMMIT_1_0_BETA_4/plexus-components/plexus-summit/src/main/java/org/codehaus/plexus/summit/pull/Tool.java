package org.codehaus.plexus.summit.pull;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class Tool
{
    private String name;

    private String role;

    private String roleHint;

    private String scope;

    public String getName()
    {
        return name;
    }

    public String getRole()
    {
        return role;
    }

    public String getRoleHint()
    {
        return roleHint;
    }

    public String getScope()
    {
        return scope;
    }
}
