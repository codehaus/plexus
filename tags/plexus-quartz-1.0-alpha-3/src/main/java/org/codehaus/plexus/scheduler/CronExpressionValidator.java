package org.codehaus.plexus.scheduler;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import org.codehaus.plexus.util.StringUtils;
import org.quartz.CronTrigger;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public final class CronExpressionValidator
{
    public static final String ROLE = CronExpressionValidator.class.getName();

    /**
     * Validates a cron expression.
     *
     * @param cronExpression The expression to validate
     * @return True is expression is valid
     */
    public boolean validate( String cronExpression )
    {
        try
        {
            String[] cronParams = StringUtils.split( cronExpression );

            if ( cronParams.length < 6 || cronParams.length > 7 )
            {
                return false;
            }

            CronTrigger cronTrigger = new CronTrigger();

            cronTrigger.setCronExpression( cronExpression );

            if ( cronParams[3].equals( "?" ) || cronParams[5].equals( "?" ) )
            {
                //Check seconds param
                if ( !checkSecondsField( cronParams[0] ) )
                {
                    return false;
                }

                //Check minutes param
                if ( !checkMinutesField( cronParams[1] ) )
                {
                    return false;
                }

                //Check hours param
                if ( !checkHoursField( cronParams[2] ) )
                {
                    return false;
                }

                //Check day-of-month param
                if ( !checkDayOfMonthField( cronParams[3] ) )
                {
                    return false;
                }

                //Check months param
                if ( !checkMonthsField( cronParams[4] ) )
                {
                    return false;
                }

                //Check day-of-week param
                if ( !checkDayOfWeekField( cronParams[5] ) )
                {
                    return false;
                }

                //Check year param
                if ( cronParams.length == 7 )
                {
                    if ( !checkYearField( cronParams[6] ) )
                    {
                        return false;
                    }
                }

                return true;
            }
            else
            {
                return false;
            }
        }
        catch ( ParseException e )
        {
            return false;
        }
    }

    private boolean checkSecondsField( String secondsField )
    {
        return checkField( secondsField, 0, 59 );
    }

    private boolean checkMinutesField( String minutesField )
    {
        return checkField( minutesField, 0, 59 );
    }

    private boolean checkHoursField( String hoursField )
    {
        return checkField( hoursField, 0, 23 );
    }

    private boolean checkDayOfMonthField( String dayOfMonthField )
    {
        if ( "?".equals( dayOfMonthField ) )
        {
            return true;
        }

        if ( dayOfMonthField.indexOf( "L" ) >= 0 )
        {
            return checkFieldWithLetter( dayOfMonthField, "L", 1, 7, -1, -1 );
        }
        else if ( dayOfMonthField.indexOf( "W" ) >= 0 )
        {
            return checkFieldWithLetter( dayOfMonthField, "W", 1, 31, -1, -1 );
        }
        else if ( dayOfMonthField.indexOf( "C" ) >= 0 )
        {
            return checkFieldWithLetter( dayOfMonthField, "C", 1, 31, -1, -1 );
        }
        else
        {
            return checkField( dayOfMonthField, 1, 31 );
        }
    }

    private boolean checkMonthsField( String monthsField )
    {
        monthsField = StringUtils.replace( monthsField, "JAN", "1" );
        monthsField = StringUtils.replace( monthsField, "FEB", "2" );
        monthsField = StringUtils.replace( monthsField, "MAR", "3" );
        monthsField = StringUtils.replace( monthsField, "APR", "4" );
        monthsField = StringUtils.replace( monthsField, "MAY", "5" );
        monthsField = StringUtils.replace( monthsField, "JUN", "6" );
        monthsField = StringUtils.replace( monthsField, "JUL", "7" );
        monthsField = StringUtils.replace( monthsField, "AUG", "8" );
        monthsField = StringUtils.replace( monthsField, "SEP", "9" );
        monthsField = StringUtils.replace( monthsField, "OCT", "10" );
        monthsField = StringUtils.replace( monthsField, "NOV", "11" );
        monthsField = StringUtils.replace( monthsField, "DEC", "12" );

        return checkField( monthsField, 1, 31 );
    }

    private boolean checkDayOfWeekField( String dayOfWeekField )
    {
        dayOfWeekField = StringUtils.replace( dayOfWeekField, "SUN", "1" );
        dayOfWeekField = StringUtils.replace( dayOfWeekField, "MON", "2" );
        dayOfWeekField = StringUtils.replace( dayOfWeekField, "TUE", "3" );
        dayOfWeekField = StringUtils.replace( dayOfWeekField, "WED", "4" );
        dayOfWeekField = StringUtils.replace( dayOfWeekField, "THU", "5" );
        dayOfWeekField = StringUtils.replace( dayOfWeekField, "FRI", "6" );
        dayOfWeekField = StringUtils.replace( dayOfWeekField, "SAT", "7" );

        if ( "?".equals( dayOfWeekField ) )
        {
            return true;
        }

        if ( dayOfWeekField.indexOf( "L" ) >= 0 )
        {
            return checkFieldWithLetter( dayOfWeekField, "L", 1, 7, -1, -1 );
        }
        else if ( dayOfWeekField.indexOf( "C" ) >= 0 )
        {
            return checkFieldWithLetter( dayOfWeekField, "C", 1, 7, -1, -1 );
        }
        else if ( dayOfWeekField.indexOf( "#" ) >= 0 )
        {
            return checkFieldWithLetter( dayOfWeekField, "#", 1, 7, 1, 5 );
        }
        else
        {
            return checkField( dayOfWeekField, 1, 7 );
        }
    }

    private boolean checkYearField( String yearField )
    {
        return checkField( yearField, 1970, 2099 );
    }

    private boolean checkField( String secondsField, int minimal, int maximal )
    {
        if ( secondsField.indexOf( "-" ) > -1 )
        {
            String startValue = secondsField.substring( 0, secondsField.indexOf( "-" ) );
            String endValue = secondsField.substring( secondsField.indexOf( "-" ) + 1 );

            if ( !( checkIntValue( startValue, minimal, maximal ) && checkIntValue( endValue, minimal, maximal ) ) )
            {
                return false;
            }
            try
            {
                int startVal = Integer.parseInt( startValue );
                int endVal = Integer.parseInt( endValue );

                return endVal > startVal;

            }
            catch ( NumberFormatException e )
            {
                return false;
            }
        }
        else if ( secondsField.indexOf( "," ) > -1 )
        {
            return checkListField( secondsField, minimal, maximal );
        }
        else if ( secondsField.indexOf( "/" ) > -1 )
        {
            return checkIncrementField( secondsField, minimal, maximal );
        }
        else if ( secondsField.indexOf( "*" ) != -1 )
        {
            return true;
        }
        else
        {
            return checkIntValue( secondsField, minimal, maximal );
        }
    }

    private boolean checkFieldWithLetter( String value, String letter, int minimalBefore, int maximalBefore,
                                          int minimalAfter, int maximalAfter )
    {
        boolean canBeAlone = false;
        boolean canHaveIntBefore = false;
        boolean canHaveIntAfter = false;
        boolean mustHaveIntBefore = false;
        boolean mustHaveIntAfter = false;

        if ( "L".equals( letter ) )
        {
            canBeAlone = true;
            canHaveIntBefore = true;
            canHaveIntAfter = false;
            mustHaveIntBefore = false;
            mustHaveIntAfter = false;
        }
        if ( "W".equals( letter ) || "C".equals( letter ) )
        {
            canBeAlone = false;
            canHaveIntBefore = true;
            canHaveIntAfter = false;
            mustHaveIntBefore = true;
            mustHaveIntAfter = false;
        }
        if ( "#".equals( letter ) )
        {
            canBeAlone = false;
            canHaveIntBefore = true;
            canHaveIntAfter = true;
            mustHaveIntBefore = true;
            mustHaveIntAfter = true;
        }

        String beforeLetter = "";
        String afterLetter = "";

        if ( value.indexOf( letter ) >= 0 )
        {
            beforeLetter = value.substring( 0, value.indexOf( letter ) );
        }

        if ( !value.endsWith( letter ) )
        {
            afterLetter = value.substring( value.indexOf( letter ) + 1 );
        }

        if ( value.indexOf( letter ) >= 0 )
        {
            if ( letter.equals( value ) )
            {
                return canBeAlone;
            }

            if ( canHaveIntBefore )
            {
                if ( mustHaveIntBefore && beforeLetter.length() == 0 )
                {
                    return false;
                }

                if ( !checkIntValue( beforeLetter, minimalBefore, maximalBefore, true ) )
                {
                    return false;
                }
            }
            else
            {
                if ( beforeLetter.length() > 0 )
                {
                    return false;
                }
            }

            if ( canHaveIntAfter )
            {
                if ( mustHaveIntAfter && afterLetter.length() == 0 )
                {
                    return false;
                }

                if ( !checkIntValue( afterLetter, minimalAfter, maximalAfter, true ) )
                {
                    return false;
                }
            }
            else
            {
                if ( afterLetter.length() > 0 )
                {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean checkIncrementField( String value, int minimal, int maximal )
    {
        String start = value.substring( 0, value.indexOf( "/" ) );

        String increment = value.substring( value.indexOf( "/" ) + 1 );

        if ( !"*".equals( start ) )
        {
            return checkIntValue( start, minimal, maximal ) && checkIntValue( increment, minimal, maximal, false );
        }
        else
        {
            return checkIntValue( increment, minimal, maximal );
        }
    }

    private boolean checkListField( String value, int minimal, int maximal )
    {
        StringTokenizer st = new StringTokenizer( value, "," );

        List values = new ArrayList();

        while ( st.hasMoreTokens() )
        {
            values.add( st.nextToken() );
        }

        int previousValue = -1;

        for ( Iterator i = values.iterator(); i.hasNext(); )
        {
            String currentValue = (String) i.next();

            if ( !checkIntValue( currentValue, minimal, maximal ) )
            {
                return false;
            }

            try
            {
                int val = Integer.parseInt( currentValue );

                if ( val <= previousValue )
                {
                    return false;
                }
                else
                {
                    previousValue = val;
                }
            }
            catch ( NumberFormatException e )
            {
                // we have always an int
            }
        }

        return true;
    }

    private boolean checkIntValue( String value, int minimal, int maximal )
    {
        return checkIntValue( value, minimal, maximal, true );
    }

    private static boolean checkIntValue( String value, int minimal, int maximal, boolean checkExtremity )
    {
        try
        {
            int val = Integer.parseInt( value );

            if ( checkExtremity )
            {
                if ( val < minimal || val > maximal )
                {
                    return false;
                }
            }

            return true;
        }
        catch ( NumberFormatException e )
        {
            return false;
        }
    }
}
