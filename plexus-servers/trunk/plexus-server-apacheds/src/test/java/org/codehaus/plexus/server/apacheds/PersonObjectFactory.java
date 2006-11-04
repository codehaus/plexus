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
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.spi.DirObjectFactory;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PersonObjectFactory
    implements DirObjectFactory
{
    public Object getObjectInstance( Object obj, Name name, Context nameCtx, Hashtable environment, Attributes attrs )
        throws Exception
    {
        // Only interested in Attributes with "person" objectclass
        // System.out.println("object factory: " + attrs);
        Attribute oc = ( attrs != null ? attrs.get( "objectclass" ) : null );

        if ( oc == null && !oc.contains( "person" ) )
        {
            return null;
        }

        // Extract the password
        Attribute attr = attrs.get( "userPassword" );

        String passwd = null;

        if ( attr != null )
        {
            Object pw = attr.get();

            if ( pw instanceof String )
            {
                passwd = (String) pw;
            }
            else
            {
                passwd = new String( (byte[]) pw );
            }
        }

        Person person = new Person( (String) attrs.get( "sn" ).get(),
                                    (String) attrs.get( "cn" ).get(),
                                    passwd,
                                    ( attr = attrs.get( "telephoneNumber" ) ) != null ? (String) attr.get() : null,
                                    ( attr = attrs.get( "seealso" ) ) != null ? (String) attr.get() : null,
                                    ( attr = attrs.get( "description" ) ) != null ? (String) attr.get() : null );
        return person;
    }

    public Object getObjectInstance( Object obj, Name name, Context nameCtx, Hashtable environment )
        throws Exception
    {
        throw new UnsupportedOperationException( "Please use directory support overload with Attributes argument." );
    }
}
