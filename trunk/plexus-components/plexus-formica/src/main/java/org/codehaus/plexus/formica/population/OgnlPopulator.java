package org.codehaus.plexus.formica.population;

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

import ognl.Ognl;
import ognl.OgnlException;
import org.codehaus.plexus.formica.Form;
import org.codehaus.plexus.formica.Element;

import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class OgnlPopulator
    implements Populator
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

        for ( Iterator i = form.getElements().iterator(); i.hasNext(); )
        {
            Element element = (Element) i.next();

            String id = element.getId();

            Object elementData = data.get( id );

            if ( elementData != null )
            {
                String expression = element.getExpression();

                try
                {
                    Ognl.setValue( expression, target, elementData );
                }
                catch ( OgnlException e )
                {
                    throw new TargetPopulationException( e );
                }
            }

        }
    }
}
