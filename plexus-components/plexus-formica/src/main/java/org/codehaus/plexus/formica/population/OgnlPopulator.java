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
        for ( Iterator i = data.keySet().iterator(); i.hasNext(); )
        {
            String key = (String) i.next();

            String expression = form.getElement( key ).getExpression();

            String elementData = (String) data.get( key );

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
