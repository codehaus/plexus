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

import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:mmmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public class Form
    extends Identifiable
{
    private List elements;

    private boolean skipNullValues;

    private boolean validateNullValues;

    private List elementGroups;

    private String targetClass;

    private String keyExpression;

    // ----------------------------------------------------------------------
    // Mode descriptors
    // ----------------------------------------------------------------------

    private Add add;

    private Update update;

    private View view;

    private Summary summary;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private String sourceRole;

    private String lookupExpression;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void addElement( Element element )
    {
        elements.add( element );
    }

    public List getElements()
    {
        return elements;
    }

    public List getElementGroups()
    {
        return elementGroups;
    }


    public void setSkipNullValues( boolean skipNullValues )
    {
        this.skipNullValues = skipNullValues;
    }

    public boolean skipNullValues()
    {
        return skipNullValues;
    }

    public void setValidateNullValues( boolean validateNullValues )
    {
        this.validateNullValues = validateNullValues;
    }

    public boolean validateNullValues()
    {
        return validateNullValues;
    }

    public String getTargetClass()
    {
        return targetClass;
    }

    public void setTargetClass( String targetClass )
    {
        this.targetClass = targetClass;
    }

    //!! The form should just be initialized to create a Map

    public Element getElement( String id )
    {
        Element retValue = null;

        for ( Iterator iterator = elements.iterator(); iterator.hasNext(); )
        {
            Element element = (Element) iterator.next();

            if ( element.getId().equals( id ) )
            {
                retValue = element;

                break;
            }
        }
        return retValue;
    }

    public ElementGroup getElementGroup( String id )
    {
        ElementGroup retValue = null;

        for ( Iterator iterator = elementGroups.iterator(); iterator.hasNext(); )
        {
            ElementGroup group = (ElementGroup) iterator.next();

            if ( group.getId().equals( id ) )
            {
                retValue = group;

                break;
            }
        }

        return retValue;
    }

    public String getKeyExpression()
    {
        return keyExpression;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public String getSourceRole()
    {
        return sourceRole;
    }

    public String getLookupExpression()
    {
        return lookupExpression;
    }

    // ----------------------------------------------------------------------
    // Mode descriptors
    // ----------------------------------------------------------------------

    public Add getAdd()
    {
        return add;
    }

    public Update getUpdate()
    {
        return update;
    }

    public View getView()
    {
        return view;
    }

    public Summary getSummary()
    {
        return summary;
    }
}