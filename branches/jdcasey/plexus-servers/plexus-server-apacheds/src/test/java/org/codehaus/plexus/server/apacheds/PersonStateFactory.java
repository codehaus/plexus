package org.codehaus.plexus.server.apacheds;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.SchemaViolationException;
import javax.naming.spi.DirStateFactory;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PersonStateFactory
    implements DirStateFactory
{
    public DirStateFactory.Result getStateToBind( Object obj, Name name, Context nameCtx, Hashtable environment, Attributes inAttrs )
        throws NamingException
    {
        // Only interested in Person objects
        if ( obj instanceof Person )
        {

            Attributes outAttrs;

            if ( inAttrs == null )
            {
                outAttrs = new BasicAttributes( true );
            }
            else
            {
                outAttrs = (Attributes) inAttrs.clone();
            }

            // Set up object class
            if ( outAttrs.get( "objectclass" ) == null )
            {
                BasicAttribute oc = new BasicAttribute( "objectclass", "person" );

                oc.add( "top" );

                outAttrs.put( oc );
            }

            Person per = (Person) obj;

            // mandatory attributes
            if ( per.getLastname() != null )
            {
                outAttrs.put( "sn", per.getLastname() );
            }
            else
            {
                throw new SchemaViolationException( "Person must have surname" );
            }

            if ( per.getCn() != null )
            {
                outAttrs.put( "cn", per.getCn() );
            }
            else
            {
                throw new SchemaViolationException( "Person must have common name" );
            }

            // optional attributes
            if ( per.getPassword() != null )
            {
                outAttrs.put( "userPassword", per.getPassword() );
            }
            if ( per.getTelephoneNumber() != null )
            {
                outAttrs.put( "telephoneNumber", per.getTelephoneNumber() );
            }
            if ( per.getSeealso() != null )
            {
                outAttrs.put( "seeAlso", per.getSeealso() );
            }
            if ( per.getDescription() != null )
            {
                outAttrs.put( "description", per.getDescription() );
            }

            return new DirStateFactory.Result( null, outAttrs );
        }

        return null;
    }

    public Object getStateToBind( Object obj, Name name, Context nameCtx, Hashtable environment )
        throws NamingException
    {
        throw new UnsupportedOperationException( "Please use directory support overload with Attributes argument." );
    }
}
