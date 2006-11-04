package org.codehaus.bayesian;

import java.math.*;
import java.util.*;

/**
 This class implements the Bayesian Filter as proposed by Paul Graham in his paper
 <a href="http://www.paulgraham.com">"A Plan For Spam" </a>.

 */
public class BayesianFilter
{
    private Parser parser = null;
    private int numberInterestingWords;
    private int maxRepeat;
    private int minStringLength;
    private double treshold;
    private WordList spam;
    private WordList ham;
    private double[] interestingValueArray;
    private double[] interestingDistanceArray;
    private String[] interestingStringArray;

    /**
     This Constructor will initialise the bayesian with all of the data it
     needs. The two corpora from which it will extract the number of times
     a word happend in each. The number of interesting words to consider.
     The Maximum number of repeats in the interesting words list. The minimum
     string length. and the threshold value.
     */
    public BayesianFilter( WordList g, WordList b, int interest, int max, int min, double t )
    {
        parser = new Parser();								// the parser
        spam = b;											// spam corpus
        ham = g;											// ham corpus
        numberInterestingWords = interest;					// how many words to consider
        maxRepeat = max;									// how many repeats to allow
        minStringLength = min;								// how big words have to be
        treshold = t;										// spam treshold value.
        interestingValueArray = new double[interest];		// holds actual probability
        interestingDistanceArray = new double[interest];	// holds deviation from mean
        interestingStringArray = new String[interest];		// holds the word.
    }


    /**
     This is implementation of Paul Graham's algorithm/heuristic.
     Plese see the secion titled "Bayesian Statistics and how its used in BEST" in
     the technical manual.

     Each e-mail is passed to this bayesian and it will return true if its spam.
     The e-mail is first parsed to extract the information that we will base our
     calculations on.

     If the probability is above the threshold we update the spam coupus with
     the information from this e-mail. Otherwise we update the ham corpus with the
     information from this e-mail.

     @param m The e-mail that we want to calculate the probability of being spam.
     @return boolean true if the e-mail is spam, otherwise false.
     */
    public boolean filter( Message m )
    {
        double spammyness = 0.5;						// set default value = 0.5 (50/50)

        String testing = parser.parse( m );				// Parse message + get back the string
        if ( testing != null )
        {
            StringTokenizer st = new StringTokenizer( testing );
            while ( st.hasMoreTokens() )
            {
                String word = st.nextToken();

                if ( word.length() >= minStringLength )		// only deal with bigger words.
                {

                    double spamValue = spam.getWord( word );	// # of occurances
                    double hamValue = ham.getWord( word );	// # of occurances

                    if ( spamValue == 0.0 && hamValue == 0.0 )// never seen before.
                    {
                        spammyness = 0.4;				// default to 0.4 (from A plan for spam)
                        updateArray( word, spammyness );	// update the array
                    }
                    else
                    {
                        if ( ( spamValue + hamValue ) > 2 )		// if we have see it more than once
                        {
                            double numSpam = spam.getTotal(); // get total number of spam mails
                            double numHam = ham.getTotal();	// get total number of ham mails

                            double x = spamValue / numSpam;	// Start Calculation.
                            double y = hamValue / numHam;	// P.G. uses 3rd table,
                            double z = x / ( x + y );		// but I calculate on the fly.

                            if ( z > 0.99 )					// round off.
                            {
                                z = 0.99;
                            }
                            else if ( z < 0.01 )				// round off.
                            {
                                z = 0.01;
                            }
                            spammyness = z;					// set spammyness
                            updateArray( word, spammyness );	// update array.
                        }
                    }
                }
            }
        }

        // determine here if spam(true) or ham(false)

        double spammy = processArray();		// process the interesting words.
        //System.out.println("Spammy = " + spammy);

        m.setSpammy( spammy );

        if ( spammy > treshold )				// if spam update spam corpus
        {
            updateSpam( testing );
            return true;
        }
        else								// if ham update ham corpus
        {
            updateHam( testing );
            return false;
        }
    }


    /**
     This is where we consider each word for entry into the interesting words list.

     Each word considered by the bayesian is assigned a probability. To construct the overall
     probability for the e-mail we need to combime these probabilities. But we do not combine
     all the probabilities of all the words as the result for each e-mail would be very
     similar thus making it extremely difficult to distinguish spam from ham.

     We only want to store words which are Interesting. By this we mean that their
     probabilities are unuasually high or unuasally low. We define a neutral probability
     of 0.5 and call the absolute distance from this probability its deviation.

     The user can specify how many words we should consider in the final calculation but we
     are left with how to choose which ones. My solution is to have an array which is
     populated with words whose deviation are the highest.

     Another factor that needs to be considered is the number of times a word can appeat in
     the srray. The more times one word appears in the list the less chance other words,
     which could condem or justify the e-mail, have of entering the list.

     From default the number of interesting words is 15
     and the number of repeats is 3.

     @param s the word for consideration.
     @param d the probability associated with that word.
     */
    protected void updateArray( String s, double d )
    {
        double dd = ( 50 - ( d * 100 ) );	// calculate the deviation from the mean. (.5)
        dd = Math.abs( dd );
        dd = ( dd / 100 );

        int count = 0;					// count of the # of repeats of the word in array.
        int pos = -1;					// position of the lowest value repeat.

        for ( int i = 0; i < numberInterestingWords; i++ )
        {
            String test = interestingStringArray[i];
            if ( s.equals( test ) )
            {
                count++;

                if ( pos == -1 )
                {
                    pos = i;
                }
                else if ( interestingDistanceArray[i] < interestingDistanceArray[pos] )
                {
                    pos = i;
                }
            }
        }

        if ( count == maxRepeat )
        {
            // replace lowest
            interestingValueArray[pos] = d;
            interestingDistanceArray[pos] = dd;
            interestingStringArray[pos] = s;
        }
        else
        {
            for ( int i = 0; i < numberInterestingWords; i++ )
            {
                if ( dd > interestingDistanceArray[i] )
                {
                    interestingValueArray[i] = d;
                    interestingDistanceArray[i] = dd;
                    interestingStringArray[i] = s;
                    break;
                }
            }
        }
    }

    /**
     This is where we calculate the overall probability for the e-mail.

     The interesting words array is read and its values are used in the calculation
     of the overall probability.

     the calculation is as follows
     X = product of the probabities for each word in list
     Y = product of the 1 minus probabities for each word in list

     X
     -----------  = Probability of e-mail being spam.
     X + Y

     Since the numbers can get extremely small I have used the BigDecimal class to
     represent some of the values in the class. At the end I force the number to
     two decimal places and return the value.

     @return double the probability of the e-mail being spam.

     */
    protected double processArray()
    {
        BigDecimal top = new BigDecimal( 1.0 );
        BigDecimal bottom = new BigDecimal( 1.0 );


        for ( int i = 1; i < interestingValueArray.length; i++ )
        {
            if ( interestingValueArray[i] == 0.0 )
            {
                continue;
            }
            BigDecimal topValue = new BigDecimal( interestingValueArray[i] );
            BigDecimal bottomValue = new BigDecimal( ( 1.0 - interestingValueArray[i] ) );

            top = top.multiply( topValue );
            bottom = bottom.multiply( bottomValue );
        }

        BigDecimal x = top.add( bottom );
        BigDecimal spammy = top.divide( x, BigDecimal.ROUND_HALF_UP );
        spammy = spammy.setScale( 2, BigDecimal.ROUND_HALF_UP );

        BigDecimal one = new BigDecimal( 1.0 );
        BigDecimal zero = new BigDecimal( 0.0 );

        int upperBound = spammy.compareTo( one );
        int lowerBound = spammy.compareTo( zero );

        if ( upperBound >= 0 && lowerBound > 0 )
        {
            return 0.99;
        }
        else if ( lowerBound <= 0 && upperBound < 0 )
        {
            return 0.01;
        }
        else
        {
            double result = spammy.doubleValue();
            return result;
        }
    }

    /**
     This returns the interesting array which contains the words
     and the associated probabilities that went into deducing the probability
     of the entire e-mail.

     This method is called by the run method in controller after the probability
     of the e-mail has been calculated. Its sole purpose is to supply data to populate
     a table in the GUI.

     @return the Interesting array formatted into a 2D object array.
     */
    public Object[][] getStats()
    {
        Object[][] stats = new Object[numberInterestingWords][2];
        for ( int i = 0; i < numberInterestingWords; i++ )
        {
            stats[i][0] = (String) interestingStringArray[i];
            stats[i][1] = new Double( interestingValueArray[i] );
        }
        return stats;
    }

    /**
     Will update the good hash table/map with the words found in the
     e-mail. The e-mail will most have been parsed to extract all the relevent
     text and the String passed to the WordList update method

     @param s the parsed text we want to update the corpus with.
     */
    protected void updateHam( String s )
    {
        ham.update( s, 1.0 );
    }

    /**
     Will update the bad hash table/map with the words found in the
     e-mail. The e-mail will most have been parsed to extract all the relevent
     text and the String passed to the WordList update method

     @param s the parsed text we want to update the corpus with.
     */
    protected void updateSpam( String s )
    {
        spam.update( s, 1.0 );
    }


}