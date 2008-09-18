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

package org.codehaus.plexus.maven.plugin.report;

import org.jdom.Element;
import org.codehaus.doxia.sink.Sink;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class Requirements
{
    private List requirements = new ArrayList();

    public Requirements( Element element )
    {
        for ( Iterator it = element.getChildren( "requirement" ).iterator(); it.hasNext(); )
        {
            Element requireqment = (Element) it.next();
            requirements.add( new Requirement( requireqment ) );
        }
    }

    public void print( Sink sink )
    {
        if ( requirements.size() == 0 )
        {
            return;
        }
/*
        sink.section4();
        sink.sectionTitle4();
        sink.text( "Requirements" );
        sink.sectionTitle4_();
        sink.section4_();
        sink.paragraph();
        for ( Iterator it = requirements.iterator(); it.hasNext(); )
        {
            Requirement requirement = (Requirement) it.next();
            requirement.print( sink );
        }
        sink.paragraph_();
*/
//        sink.section4();
//        sink.sectionTitle4();
//        sink.text( "Requirements" );
//        sink.sectionTitle4_();
//        sink.section4_();

        sink.paragraph();
        sink.table();
        sink.tableCaption();
        sink.text( "Requirements" );
        sink.tableCaption_();
        for ( Iterator it = requirements.iterator(); it.hasNext(); )
        {
            Requirement requirement = (Requirement) it.next();
            requirement.print( sink );
        }
        sink.table_();
        sink.paragraph_();
    }
}
