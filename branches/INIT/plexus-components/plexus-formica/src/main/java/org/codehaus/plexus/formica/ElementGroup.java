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

import org.codehaus.plexus.formica.validation.group.GroupValidator;

import java.util.ArrayList;
import java.util.List;


/**
 * @author <a href="mailto:mmmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public class ElementGroup
    extends Identifiable
{
    private List elements;

    private String validator;

    private GroupValidator validatorObject;

    private String errorMessageKey;

    private Form form;

    public String getValidator()
    {
        return validator;
    }

    public void setValidator( String validator )
    {
        this.validator = validator;
    }

    public List getElements()
    {
        return elements;
    }

    public void setElements( List elements )
    {
        this.elements = elements;
    }

    public void addElement( Element element )
    {
        if ( elements == null )
        {
            elements = new ArrayList();
        }
        elements.add( element );
    }

    public void setValidatorObject( GroupValidator validatorObject )
    {
        this.validatorObject = validatorObject;
        if ( errorMessageKey == null )
        {
            errorMessageKey = validatorObject.getErrorMessageKey();
        }
    }

    public GroupValidator getValidatorObject()
    {
        return validatorObject;
    }

    public void setForm( Form form )
    {
        this.form = form;
    }

    public String getErrorMessageKey()
    {
        return errorMessageKey;
    }
}
