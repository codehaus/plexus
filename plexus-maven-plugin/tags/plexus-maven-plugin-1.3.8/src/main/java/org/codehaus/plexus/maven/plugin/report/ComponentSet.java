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
import java.util.List;

import org.codehaus.doxia.sink.Sink;
import org.jdom.Element;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ComponentSet
{
    private Components components;

    public ComponentSet( Element element )
    {
        List list = element.getChildren( "components" );

        if ( list.size() > 1 )
        {
            throw new RuntimeException( "The component set can only contain a single <components> section." );
        }

        components = new Components( (Element) list.get( 0 ) );
    }

    public void print( Sink sink, String javaDocDestDir, String jxrDestDir )
    {
        components.print( sink, javaDocDestDir, jxrDestDir );
    }
}
