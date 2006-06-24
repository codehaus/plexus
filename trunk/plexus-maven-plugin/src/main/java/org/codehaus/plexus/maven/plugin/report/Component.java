/*
 * The MIT License
 *
 * Copyright (c) 2006, The Codehaus
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

package org.codehaus.plexus.maven.plugin.report;

import org.jdom.Element;
import org.codehaus.doxia.sink.Sink;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class Component
    implements Comparable
{
    private String role;
    private String roleHint;
    private String implementation;

    public Component( Element component )
    {
        role = component.getChildText( "role" );
        roleHint = component.getChildText( "role-hint" );
        implementation = component.getChildText( "implementation" );
    }

    public String getRole()
    {
        return role;
    }

    public String getRoleHint()
    {
        return roleHint;
    }

    public String getImplementation()
    {
        return implementation;
    }

    public boolean hasRoleHint()
    {
        return StringUtils.isNotEmpty( roleHint );
    }

    public void print( Sink sink )
    {
        if ( !hasRoleHint() )
        {
            sink.text( "Role: " + role ); sink.lineBreak();
        }
        else
        {
            sink.text( "Role hint: " + roleHint ); sink.lineBreak();
        }
        sink.text( "Implementation: " + implementation ); sink.lineBreak();
    }

    public int compareTo( Object o )
    {
        return ((Component) o).getId().compareTo( getId() );
    }

    private String getId()
    {
        if ( !hasRoleHint() )
        {
            return role;
        }
        else
        {
            return role + ":" + roleHint;
        }
    }
}
