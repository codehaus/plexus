package org.codehaus.plexus.formica.population;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class Address
{
    private String street;
    private String country;

    public String getStreet()
    {
        return street;
    }

    public void setStreet( final String street )
    {
        this.street = street;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry( final String country )
    {
        this.country = country;
    }
}


