package org.codehaus.plexus.formica;

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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FormValidationResult
{
    private Map elementResults;

    private Map groupResults;

    public FormValidationResult()
    {
        elementResults = new HashMap();

        groupResults = new HashMap();
    }

    // ----------------------------------------------------------------------
    // Element results
    // ----------------------------------------------------------------------

    public void addElementValidationResult( String elementId, Boolean result )
    {
        elementResults.put( elementId, result );
    }

    /**
     * A quick way to check if all element form data is valid.
     *
     * @return Flag indicating element data validity.
     */
    public boolean elementsValid()
    {
        for ( Iterator i = elementResults.values().iterator(); i.hasNext(); )
        {
            Boolean result = (Boolean) i.next();

            if ( !result.booleanValue() )
            {
                return false;
            }
        }

        return true;
    }

    // ----------------------------------------------------------------------
    // Group results
    // ----------------------------------------------------------------------

    public void addGroupValidationResult()
    {
    }
}



