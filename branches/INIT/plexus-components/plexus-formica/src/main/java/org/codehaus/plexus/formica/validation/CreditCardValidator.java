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

/**
 * <p>Perform credit card validations.</p>
 * <p/>
 * This class is a Singleton; you can retrieve the instance via the getInstance() method.
 * </p>
 * Reference Sean M. Burke's script at
 * http://www.ling.nwu.edu/~sburke/pub/luhn_lib.pl
 *
 * @author David Winterfeldt
 * @author James Turner
 * @author <a href="mailto:husted@apache.org">Ted Husted</a>
 * @author David Graham
 * @version $Revision$ $Date$
 */
public class CreditCardValidator
    extends AbstractValidator
{

    private static String AMEX_PREFIX = "34,37,";
    private static String VISA_PREFIX = "4";
    private static String MASTERCARD_PREFIX = "51,52,53,54,55,";
    private static String DISCOVER_PREFIX = "6011";

    /**
     * Protected constructor for subclasses to use.
     */
    public CreditCardValidator()
    {
    }

    /**
     * Checks if the field is a valid credit card number.
     *
     * @param value The value validation is being performed on.
     */
    public boolean validate( String value )
    {
        return (
            this.validateCreditCardLuhnCheck( value )
            && this.validateCreditCardPrefixCheck( value ) );
    }

    /**
     * Checks for a valid credit card number.
     *
     * @param cardNumber Credit Card Number.
     */
    protected boolean validateCreditCardLuhnCheck( String cardNumber )
    {
        // number must be validated as 0..9 numeric first!!
        int digits = cardNumber.length();
        int oddoeven = digits & 1;
        long sum = 0;
        for ( int count = 0; count < digits; count++ )
        {
            int digit;
            try
            {
                digit =
                    Integer.parseInt( String.valueOf( cardNumber.charAt( count ) ) );
            }
            catch ( NumberFormatException e )
            {
                return false;
            }
            if ( ( ( count & 1 ) ^ oddoeven ) == 0 )
            { // not
                digit *= 2;
                if ( digit > 9 )
                {
                    digit -= 9;
                }
            }
            sum += digit;
        }
        if ( sum == 0 )
        {
            return false;
        }

        if ( sum % 10 == 0 )
        {
            return true;
        }

        return false;
    }

    /**
     * Checks for a valid credit card number.
     *
     * @param cardNumber Credit Card Number.
     */
    protected boolean validateCreditCardPrefixCheck( String cardNumber )
    {

        int length = cardNumber.length();
        if ( length < 13 )
        {
            return false;
        }

        int cardType = 0;

        String prefix2 = cardNumber.substring( 0, 2 ) + ",";

        if ( AMEX_PREFIX.indexOf( prefix2 ) != -1 )
        {
            cardType = 3;
        }
        if ( cardNumber.substring( 0, 1 ).equals( VISA_PREFIX ) )
        {
            cardType = 4;
        }
        if ( MASTERCARD_PREFIX.indexOf( prefix2 ) != -1 )
        {
            cardType = 5;
        }
        if ( cardNumber.substring( 0, 4 ).equals( DISCOVER_PREFIX ) )
        {
            cardType = 6;
        }

        if ( ( cardType == 3 ) && ( length == 15 ) )
        {
            return true;
        }
        if ( ( cardType == 4 ) && ( ( length == 13 ) || ( length == 16 ) ) )
        {
            return true;
        }
        if ( ( cardType == 5 ) && ( length == 16 ) )
        {
            return true;
        }
        if ( ( cardType == 6 ) && ( length == 16 ) )
        {
            return true;
        }

        return false;
    }

}
