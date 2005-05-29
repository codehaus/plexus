package org.codehaus.plexus.formica.validation;

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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @plexus.component
 *  role-hint="date"
 *
 * <p>Perform date validations.</p>
 * <p/>
 * This class is a Singleton; you can retrieve the instance via the getInstance() method.
 * </p>
 *
 * @author David Winterfeldt
 * @author James Turner
 * @author <a href="mailto:husted@apache.org">Ted Husted</a>
 * @author David Graham
 * @version $Revision$
 */
public class DateValidator
    extends AbstractValidator
{
    /**
     * @plexus.configuration
     *  default-value="MM/dd/yyyy"
     */
    private String pattern;

    private boolean strict = true;

    /**
     * Protected constructor for subclasses to use.
     */
    public DateValidator()
    {
    }

    /**
     * <p>Checks if the field is a valid date.  The pattern is used with
     * <code>java.text.SimpleDateFormat</code>.  If strict is true, then the
     * length will be checked so '2/12/1999' will not pass validation with
     * the format 'MM/dd/yyyy' because the month isn't two digits.
     * The setLenient method is set to <code>false</code> for all.</p>
     *
     * @param value The value validation is being performed on.
     */
    public boolean validate( String value )
    {
        if ( value == null
            || pattern == null
            || pattern.length() <= 0 )
        {

            return false;
        }

        SimpleDateFormat formatter = new SimpleDateFormat( pattern );
        formatter.setLenient( false );

        try
        {
            formatter.parse( value );
        }
        catch ( ParseException e )
        {
            return false;
        }

        if ( strict && ( pattern.length() != value.length() ) )
        {
            return false;
        }

        return true;
    }

    /**
     * <p>Checks if the field is a valid date.  The <code>Locale</code> is
     * used with <code>java.text.DateFormat</code>.  The setLenient method
     * is set to <code>false</code> for all.</p>
     *
     * @param value  The value validation is being performed on.
     * @param locale The locale to use for the date format, defaults to the default
     *               system default if null.
     */
    public boolean isValid( String value, Locale locale )
    {

        if ( value == null )
        {
            return false;
        }

        DateFormat formatter;
        if ( locale != null )
        {
            formatter = DateFormat.getDateInstance( DateFormat.SHORT, locale );
        }
        else
        {
            formatter =
                DateFormat.getDateInstance( DateFormat.SHORT,
                                            Locale.getDefault() );
        }

        formatter.setLenient( false );

        try
        {
            formatter.parse( value );
        }
        catch ( ParseException e )
        {
            return false;
        }

        return true;
    }

    public void setPattern( String pattern )
    {
        this.pattern = pattern;
    }

}
