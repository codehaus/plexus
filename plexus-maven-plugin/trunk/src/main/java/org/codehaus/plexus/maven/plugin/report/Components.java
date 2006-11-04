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

import org.codehaus.doxia.sink.Sink;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class Components
{
    private List components = new ArrayList();

    public Components( Element element )
    {
        for ( Iterator it = element.getChildren( "component" ).iterator(); it.hasNext(); )
        {
            Element component = (Element) it.next();

            components.add( new Component( component ) );
        }
    }

    public void print( Sink sink )
    {
        SortedSet roles = new TreeSet();
        SortedSet emptyList = new TreeSet();
        SortedMap roleMap = new TreeMap();

        // ----------------------------------------------------------------------
        // Build stuff
        // ----------------------------------------------------------------------

        for ( Iterator it = components.iterator(); it.hasNext(); )
        {
            Component component = (Component) it.next();

            roles.add( component.getRole() );

            if ( !component.hasRoleHint() )
            {
                emptyList.add( component );
            }
            else
            {
                List list = (List) roleMap.get( component.getRole() );

                if ( list == null )
                {
                    list = new ArrayList();
                    roleMap.put( component.getRole(), list );
                }

                list.add( component );
            }
        }

        // ----------------------------------------------------------------------
        // Component role index
        // ----------------------------------------------------------------------

        sink.section2();
        sink.sectionTitle2();
        sink.text( "Index" );
        sink.sectionTitle2_();
        sink.section2_();

        sink.paragraph();
        for ( Iterator it = roles.iterator(); it.hasNext(); )
        {
            String role = (String) it.next();
            sink.link( "#" + role );
            sink.text( role );
            sink.link_();
            sink.lineBreak();
        }
        sink.paragraph_();

        // ----------------------------------------------------------------------
        // Components without role hints
        // ----------------------------------------------------------------------

        sink.section2();
        sink.sectionTitle2();
        sink.text( "Components without role-hint" );
        sink.sectionTitle2_();
        sink.section2_();

        for ( Iterator it = emptyList.iterator(); it.hasNext(); )
        {
            Component component = (Component) it.next();
            sink.anchor( component.getRole() );
            sink.paragraph();
            component.print( sink );
            sink.paragraph_();
            sink.anchor_();
        }

        // ----------------------------------------------------------------------
        // Components with role hint, sorted by role
        // ----------------------------------------------------------------------

        for ( Iterator it = roleMap.entrySet().iterator(); it.hasNext(); )
        {
            Map.Entry entry = (Map.Entry) it.next();

            sink.section2();
            sink.sectionTitle2();
            sink.anchor( (String) entry.getKey() );
            sink.text( "Components implementing " + entry.getKey() );
            sink.anchor_();
            sink.sectionTitle2_();
            sink.section2_();

            for ( Iterator j = ( (List) entry.getValue() ).iterator(); j.hasNext(); )
            {
                Component component = (Component) j.next();

                sink.paragraph();
                component.print( sink );
                sink.paragraph_();
            }
        }
    }
}
