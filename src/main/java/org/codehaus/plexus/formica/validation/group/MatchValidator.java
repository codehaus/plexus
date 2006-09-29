package org.codehaus.plexus.formica.validation.group;

/*
 * Copyright (c) 2004, Codehaus.org
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

import org.codehaus.plexus.formica.Element;
import org.codehaus.plexus.formica.ElementGroup;
import org.codehaus.plexus.formica.FormicaException;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @plexus.component
 *  role-hint="match"
 *
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
            if ( pattern == null )
                pattern = "";

            while ( iterator.hasNext() )
            {
                element = (Element) iterator.next();

                String currentPattern = (String) formData.get( element.getId() );
                if ( currentPattern == null )
                    currentPattern = "";

                if ( !pattern.equals( currentPattern ) )
                {
                    elementsMatch = false;
                }
            }
        }

        return elementsMatch;
    }
}
