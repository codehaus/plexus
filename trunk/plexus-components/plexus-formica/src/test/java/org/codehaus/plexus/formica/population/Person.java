package org.codehaus.plexus.formica.population;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class Person
{
    private Address address;

    public Address getAddress()
    {
        if ( address == null )
        {
            address = new Address();
        }

        return address;
    }

    public void setAddress( final Address address )
    {
        this.address = address;
    }
}
