package org.codehaus.plexus.formica.web.summary;

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
import org.codehaus.plexus.formica.Element;
import org.codehaus.plexus.formica.Form;
import org.codehaus.plexus.formica.Operation;
import org.codehaus.plexus.formica.SummaryElement;
import org.codehaus.plexus.formica.web.AbstractFormRenderer;
import org.codehaus.plexus.formica.web.FormRenderingException;
import org.codehaus.plexus.i18n.I18N;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.XMLWriter;
import org.codehaus.plexus.summit.rundata.RunData;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class SummaryFormRenderer
    extends AbstractFormRenderer
{
    public String getHeaderTitle( Form form, I18N i18n )
    {
        return i18n.getString( form.getSummary().getTitleKey() );
    }

    public void body( Form form, XMLWriter w, I18N i18n, Object data, String baseUrl, RunData parameters )
        throws FormRenderingException
    {
        w.startElement( "table" );

        w.addAttribute( "cellpadding", "3" );

        w.addAttribute( "cellspacing", "2" );

        w.addAttribute( "border", "1" );

        w.addAttribute( "width", "100%" );

        // ----------------------------------------------------------------------
        // Headings
        // ----------------------------------------------------------------------

        w.startElement( "tr" );

        for ( Iterator i = form.getSummary().getSummaryElements().iterator(); i.hasNext(); )
        {
            SummaryElement se = (SummaryElement) i.next();

            Element element = form.getElement( se.getId() );

            w.startElement( "th" );

            w.writeText( i18n.getString( element.getLabelKey() ) );

            w.endElement();
        }

        w.startElement( "th" );

        w.addAttribute( "colspan", "3" );

        w.writeText( "Operations" );

        w.endElement();

        w.endElement();

        // ----------------------------------------------------------------------
        // Summary
        // ----------------------------------------------------------------------
        // Here we have a collection of the form's target object and we want to
        // extract the fields from the object that correspond to what we want to
        // show in the summary.
        // ----------------------------------------------------------------------

        Iterator summaryIterator;

        if ( data instanceof Collection )
        {
            summaryIterator = ((Collection)data).iterator();
        }
        else
        {
            summaryIterator = (Iterator) data;
        }

        String rowClass = "a";

        for ( Iterator i = summaryIterator; i.hasNext(); )
        {
            w.startElement( "tr" );

            w.addAttribute( "class", rowClass );

            Object item = i.next();

            for ( Iterator j = form.getSummary().getSummaryElements().iterator(); j.hasNext(); )
            {
                SummaryElement se = (SummaryElement) j.next();

                Element element = form.getElement( se.getId() );

                w.startElement( "td" );

                try
                {
                    w.writeText( Ognl.getValue( element.getExpression(), item ).toString() );
                }
                catch ( OgnlException e )
                {
                    throw new FormRenderingException( "Error extracting value from " + item + " using the expression " + element.getExpression() );
                }

                w.endElement();
            }

            // ----------------------------------------------------------------------
            // Operations
            // ----------------------------------------------------------------------

            for ( Iterator j = form.getSummary().getOperations().iterator(); j.hasNext(); )
            {
                Operation op = (Operation) j.next();

                w.startElement( "td" );

                w.startElement( "a" );

                String id = null;

                String type = null;

                // TODO; throw an exception if the expression key isn't there
                try
                {
                    id = (String) Ognl.getValue( form.getKeyExpression(), item );

                    type = (String) Ognl.getValue( form.getTypeExpression(), item );
                }
                catch ( OgnlException e )
                {
                    throw new FormRenderingException( "Error retrieving expression:", e );
                }

                String s = StringUtils.replace( op.getAction(), "$id$", id );

                s = StringUtils.replace( s, "$formId$", form.getId() );

                s = StringUtils.replace( s, "$type$", type );

                w.addAttribute( "href", baseUrl + "/" + s );

                w.writeText( op.getName() );

                w.endElement();

                w.endElement();
            }

            // ----------------------------------------------------------------------

            w.endElement();

            if ( rowClass.equals( "a" ) )
            {
                rowClass = "b";
            }
            else
            {
                rowClass = "a";
            }
        }

        w.endElement(); //table
    }
}
