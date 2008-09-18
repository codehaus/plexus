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
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class Requirement
{
    private String role;
    private String roleHint;
    private String fieldName;

    public Requirement( Element requireqment )
    {
        role = requireqment.getChildTextTrim( "role" );
        roleHint = requireqment.getChildTextTrim( "roleHint" );
        fieldName = requireqment.getChildTextTrim( "fieldName" );
    }

    public String getRole()
    {
        return role;
    }

    public String getRoleHint()
    {
        return roleHint;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public void print( Sink sink )
    {
/*
        sink.text( "Role: " + role ); sink.lineBreak();
        if( StringUtils.isNotEmpty( roleHint ) )
        {
            sink.text( "Role hint: " + roleHint ); sink.lineBreak();
        }
        if( StringUtils.isNotEmpty( fieldName ) )
        {
            sink.text( "Field name: " + fieldName ); sink.lineBreak();
        }
*/
        sink.tableRow();
        sink.tableCell();
        sink.text( "Role" );
        sink.tableCell_();
        sink.tableCell();
        sink.monospaced();
        sink.text( role );
        sink.monospaced_();
        sink.tableCell_();
        sink.tableRow_();

        if( StringUtils.isNotEmpty( roleHint ) )
        {
            sink.tableRow();
            sink.tableCell();
            sink.text( "Hint" );
            sink.tableCell_();
            sink.tableCell();
            sink.monospaced();
            sink.text( roleHint );
            sink.monospaced_();
            sink.tableCell_();
            sink.tableRow_();
        }

        if( StringUtils.isNotEmpty( fieldName ) )
        {
            sink.tableRow();
            sink.tableCell();
            sink.text( "Field name" );
            sink.tableCell_();
            sink.tableCell();
            sink.monospaced();
            sink.text( fieldName );
            sink.monospaced_();
            sink.tableCell_();
            sink.tableRow_();
        }
    }
}
