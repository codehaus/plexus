package org.codehaus.plexus.maven.plugin.report;

/*
 * Copyright 2007 The Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.codehaus.doxia.sink.Sink;
import org.jdom.Element;

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

    public void print( Sink sink, String javaDocDestDir, String jxrDestDir )
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
            component.print( sink, javaDocDestDir, jxrDestDir );
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
                component.print( sink, javaDocDestDir, jxrDestDir );
                sink.paragraph_();
            }
        }
    }
}
