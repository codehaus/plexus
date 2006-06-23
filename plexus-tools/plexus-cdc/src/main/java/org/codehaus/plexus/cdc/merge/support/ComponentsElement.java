package org.codehaus.plexus.cdc.merge.support;

/*
 * The MIT License
 *
 * Copyright (c) 2006, The Codehaus
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

import org.jdom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id$
 */
public class ComponentsElement
    extends AbstractMergeableElementList
{
    private List conflictVerificationkeys = new ArrayList();

    {
        conflictVerificationkeys.add( ComponentsXmlTag.ROLE.getTagName() );
        conflictVerificationkeys.add( ComponentsXmlTag.ROLE_HINT.getTagName() );
    }

    public ComponentsElement( Element element )
    {
        super( element );
    }

    protected ComponentsXmlTag[] getAllowedTags()
    {
        return new ComponentsXmlTag[]{ComponentsXmlTag.COMPONENT};
    }

    protected boolean isExpectedElementType( Mergeable me )
    {
        return ( me instanceof ComponentsElement );
    }

    protected List getElementNamesForConflictChecks( List defaultList )
    {
        // Allow to return custom keys for conflict checks/resolution.
        return this.conflictVerificationkeys;
    }

    protected String getTagNameForRecurringMergeable()
    {
        return ComponentsXmlTag.COMPONENT.getTagName();
    }

    protected List getElementNamesForConflictResolution( List defaultList )
    {
        List l = new ArrayList();
        l.add( ComponentsXmlTag.ROLE.getTagName() );
        l.add( ComponentsXmlTag.ROLE_HINT.getTagName() );
        return l;
    }
}
