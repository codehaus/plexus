package org.codehaus.plexus.formica.web.view;

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
import org.codehaus.plexus.formica.web.AbstractFormRenderer;
import org.codehaus.plexus.formica.web.FormRenderingException;
import org.codehaus.plexus.i18n.I18N;
import org.codehaus.plexus.util.xml.XMLWriter;
import org.codehaus.plexus.summit.rundata.RunData;

import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class ViewFormRenderer
    extends AbstractFormRenderer
{
    public String getHeaderTitle( Form form, I18N i18n )
    {
        return i18n.getString( form.getView().getTitleKey() );
    }

    public void body( Form form, XMLWriter w, I18N i18n, Object data, String baseUrl, RunData parameters )
        throws FormRenderingException
    {
        // ----------------------------------------------------------------------

        w.startElement( "form" );

        w.addAttribute( "method", "post" );

        w.addAttribute( "action", baseUrl + "/" + form.getView() );

        // ----------------------------------------------------------------------

        String id = null;

        try
        {
            //TODO: throw an exception if there is no key expression

            System.out.println( "form.getKeyExpression() = " + form.getKeyExpression() );

            id = (String) Ognl.getValue( form.getKeyExpression(), data );
        }
        catch ( OgnlException e )
        {
            e.printStackTrace();
        }

        w.startElement( "input" );

        w.addAttribute( "type", "hidden" );

        w.addAttribute( "name", "id" );

        w.addAttribute( "value", id );

        w.endElement();

        // ----------------------------------------------------------------------

        w.startElement( "div" );

        w.addAttribute( "class", "axial" );

        w.startElement( "table" );

        w.addAttribute( "cellpadding", "3" );

        w.addAttribute( "cellspacing", "2" );

        w.addAttribute( "border", "0" );

        for ( Iterator i = form.getElements().iterator(); i.hasNext(); )
        {
            Element element = (Element) i.next();

            w.startElement( "tr" );

            // ----------------------------------------------------------------------

            w.startElement( "th" );

            w.writeText( i18n.getString( element.getLabelKey() ) );

            w.endElement();

            // ----------------------------------------------------------------------

            w.startElement( "td" );

            try
            {

                Object o = null;

                if ( data != null && element.getExpression() != null )
                {
                    o = Ognl.getValue( element.getExpression(), data );
                }

                if ( o != null )
                {
                    w.writeText( o.toString() );
                }
            }
            catch ( OgnlException e )
            {
                throw new FormRenderingException(
                    "Error extracting value in " + data + " with the expresion '" + element.getExpression() + "'" );
            }

            w.endElement();

            // ----------------------------------------------------------------------

            w.endElement();
        }

        w.endElement();  // table

        w.endElement();  // div

        // ----------------------------------------------------------------------

        w.startElement( "div" );

        w.addAttribute( "class", "functnbar2" );

        w.endElement(); // div

        w.endElement();
    }
}
