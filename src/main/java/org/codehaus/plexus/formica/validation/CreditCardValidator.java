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

/**
 * @plexus.component
 *  role-hint="credit-card"
 *
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
