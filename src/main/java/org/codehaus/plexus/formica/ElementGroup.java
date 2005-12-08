package org.codehaus.plexus.formica;

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

import org.codehaus.plexus.formica.validation.group.GroupValidator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
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
