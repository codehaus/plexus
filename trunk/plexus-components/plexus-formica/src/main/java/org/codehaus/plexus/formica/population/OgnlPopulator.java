package org.codehaus.plexus.formica.population;

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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ognl.Ognl;
import ognl.OgnlException;

import org.codehaus.plexus.formica.Element;
import org.codehaus.plexus.formica.ElementGroup;
import org.codehaus.plexus.formica.Form;
import org.codehaus.plexus.formica.population.transformer.Transformer;
import org.codehaus.plexus.formica.population.transformer.TransformerNotFoundException;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class OgnlPopulator
    extends AbstractPopulator
{
    public void populate( Form form, Map data, Object target )
        throws TargetPopulationException
    {
        // ----------------------------------------------------------------------
        // The incoming Map may not consist soley of form data. We may have
        // something like a Map of parameters coming from a servlet which may
        // contain other items. So we will walk the elements of the Form and
        // see if there is a datum within the Map keyed by form element id
        // in question. If the datum exists we will attempt to populate the
        // target object with the said value.
        // ----------------------------------------------------------------------

        if ( form.getElements() != null )
        {
            populateElements( data, target, form.getElements(), form.getId() );
        }

        if ( form.getElementGroups() != null )
        {
            for ( Iterator itr = form.getElementGroups().iterator(); itr.hasNext(); )
            {
                ElementGroup group = (ElementGroup) itr.next();

                if ( group.getElements() != null )
                {
                    populateElements( data, target, group.getElements(), form.getId() );
                }
            }
        }
    }

    /**
     * @param data
     * @param target
     * @param elements
     * @throws TargetPopulationException
     */
    private void populateElements( Map data, Object target, List elements, String formId )
        throws TargetPopulationException
    {
        for ( Iterator i = elements.iterator(); i.hasNext(); )
        {
            Element element = (Element) i.next();

            String id = element.getId();

            Object elementData = data.get( id );

            // ----------------------------------------------------------------------
            // Populate the default value if our data for the element is null and
            // there is a default value available.
            // ----------------------------------------------------------------------

            if ( elementData == null && element.getDefaultValue() != null )
            {
                elementData = element.getDefaultValue();
            }

            // OGNL interprets the string literal "false" as true because it's non-null
            if ( elementData instanceof String )
            {
                String s = (String) elementData;

                //TODO: we need to check element.type
                if ( s.equals( "false" ) || s.equals( "off" ) || s.equals( "0" ) )
                {
                    elementData = Boolean.FALSE;
                }
                else if ( s.equals( "true" ) || s.equals( "on" ) || s.equals( "1" ) )
                {
                    elementData = Boolean.TRUE;
                }
            }


            if ( elementData != null )
            {
                String expression = element.getExpression();

                if ( expression == null )
                {
                    throw new TargetPopulationException( "Expression for " + element.getId() + " in " + formId + " cannot be null." );
                }

                try
                {
                    Ognl.setValue( expression, data, target, elementData );
                }
                catch ( OgnlException e )
                {
                    throw new TargetPopulationException( e );
                }
            }
        }
    }
}
