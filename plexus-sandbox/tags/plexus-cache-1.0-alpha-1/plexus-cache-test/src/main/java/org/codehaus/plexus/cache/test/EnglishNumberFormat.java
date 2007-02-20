package org.codehaus.plexus.cache.test;

/*
 * Copyright 2001-2007 The Codehaus.
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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 * EnglishNumberFormat - Silly utility to generate text from numbers.
 * Used in the generation of large data sets for the Cache testing. 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class EnglishNumberFormat
{
    public static final String[] basic = { // basic numbers from 1 to 19 
    null, // zero
        "one",
        "two",
        "three",
        "four",
        "five",
        "six",
        "seven",
        "eight",
        "nine",
        "ten",
        "eleven",
        "twelve",
        "thirteen",
        "fourteen",
        "fifteen",
        "sixteen",
        "seventeen",
        "eighteen",
        "nineteen" };

    public static final String[] tens = { // numbers representing the 2nd digit 
    null, // zeroty ??
        null, // ten
        "twenty",
        "thirty",
        "fourty",
        "fifty",
        "sixty",
        "seventy",
        "eighty",
        "ninety" };

    public static final String[] dreds = { // number multiples 
    null, // tendred ??
        "hundred",
        "thousand",
        "million",
        "billion",
        "trillion",
        "quadrillion",
        "quintillion" };

    public String toText( long number )
    {
        // Special case
        if ( number == 0 )
        {
            return "zero";
        }

        // Quick return - single digit
        if ( number <= 9 )
        {
            return basic[(int) number];
        }

        NumberText txt = new NumberText();
        Digits digits = new Digits( number );
        int multi = 1;
        int offset = 0;

        while ( digits.at( offset ) >= 0 )
        {
            // Handle last 2 digits first.
            int first = digits.at( offset );
            int second = digits.at( offset + 1 );
            int third = digits.at( offset + 2 );

            if ( second >= 0 )
            {
                int basicDigits = ( second * 10 ) + first;

                if ( basicDigits <= 19 )
                {
                    txt.prepend( basic[basicDigits] );
                }
                else
                {
                    txt.prepend( basic[first] );
                    txt.prepend( tens[second] );
                }
            }
            else
            {
                txt.prepend( basic[first] );
            }

            if ( third > 0 )
            {
                txt.prepend( basic[third] + " " + dreds[1] );
            }

            offset += 3;
            multi++;
            txt.setPotentialMultiple( dreds[multi] );
        }

        return txt.toString();
    }

    class NumberText
    {
        private StringBuffer txt;

        private String potentialMulti = null;

        public NumberText()
        {
            txt = new StringBuffer();
        }

        public void setPotentialMultiple( String multiple )
        {
            potentialMulti = multiple;
        }

        public void prepend( String name )
        {
            if ( name != null )
            {
                if ( potentialMulti != null )
                {
                    txt.insert( 0, potentialMulti + " " );
                    potentialMulti = null;
                }

                if ( txt.length() > 0 )
                {
                    name += " ";
                }
                txt.insert( 0, name );
            }
        }

        public String toString()
        {
            return txt.toString();
        }
    }

    class Digits
    {
        private String digits;

        public Digits( long number )
        {
            digits = StringUtils.reverse( String.valueOf( number ) );
        }

        public int at( int offset )
        {
            if ( offset >= digits.length() )
            {
                return ( -1 );
            }

            return NumberUtils.toInt( "" + digits.charAt( offset ) );
        }
    }
}
