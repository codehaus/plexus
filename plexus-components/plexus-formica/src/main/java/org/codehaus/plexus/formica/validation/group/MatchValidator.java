package org.codehaus.plexus.formica.validation.group;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

import org.codehaus.plexus.formica.Element;
import org.codehaus.plexus.formica.ElementGroup;
import org.codehaus.plexus.formica.FormicaException;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class MatchValidator
    extends AbstractGroupValidator
{
    public boolean validate( ElementGroup elementGroup, Map formData )
        throws FormicaException
    {
        boolean elementsMatch = true;

        List elements = elementGroup.getElements();

        //check if group has less then two elements
        if ( elements.size() >= 2 )
        {
            Iterator iterator = elements.iterator();

            Element element = (Element) iterator.next();

            String pattern = (String) formData.get( element.getId() );

            while ( iterator.hasNext() )
            {
                element = (Element) iterator.next();

                String currentPattern = (String) formData.get( element.getId() );

                if ( !pattern.equals( currentPattern ) )
                {
                    elementsMatch = false;
                }
            }
        }

        return elementsMatch;
    }
}
