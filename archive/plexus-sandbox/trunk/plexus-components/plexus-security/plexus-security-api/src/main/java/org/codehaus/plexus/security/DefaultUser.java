package org.codehaus.plexus.security;

public class DefaultUser
    implements User
{
    private String username;
    private String password;
    private String fullName;
    private String email;
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean passwordNonExpired;
    private Object details;

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName( String fullName )
    {
        this.fullName = fullName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled( boolean enabled )
    {
        this.enabled = enabled;
    }

    public boolean isAccountNonExpired()
    {
        return accountNonExpired;
    }

    public void setAccountNonExpired( boolean accountNonExpired )
    {
        this.accountNonExpired = accountNonExpired;
    }

    public boolean isAccountNonLocked()
    {
        return accountNonLocked;
    }

    public void setAccountNonLocked( boolean accountNonLocked )
    {
        this.accountNonLocked = accountNonLocked;
    }

    public boolean isPasswordNonExpired()
    {
        return passwordNonExpired;
    }

    public void setPasswordNonExpired( boolean passwordNonExpired )
    {
        this.passwordNonExpired = passwordNonExpired;
    }

    public Object getDetails()
    {
        return details;
    }

    public void setDetails( Object details )
    {
        this.details = details;
    }
}
