package org.codehaus.plexus.formica.validation;

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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
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
