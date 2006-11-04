package org.codehaus.plexus.ldap.helper.factory;

import javax.naming.NamingException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PersonLdapFactory
    extends AbstractLdapFactory
{
    public PersonLdapFactory()
    {
        super( Person.class, new String[] { "person" } );
    }

    public Object createObject()
        throws NamingException
    {
        Person person = new Person();

        person.setName( getStringAttribute( "commonName" ) );
        person.setDescription( getStringAttribute( "description" ) );

        return person;
    }

    public void createAttributes( Object object )
        throws NamingException
    {
        Person person = (Person) object;

        setAttribute( "commonName", person.getName() );
        setAttribute( "description", person.getDescription() );
    }
}
