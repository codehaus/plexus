package org.codehaus.bayesian;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;


/**
 This class holds the information collated from the corpora of spam and ham.
 It contains a HashMap of (String, Count) pairsm where count is a class (with package scope)
 that keeps track of the number of occurances.
 */
public class WordList implements Serializable
{
    private HashMap words;
    private double mails;
    private int minStringLength;

    /**
     Default constructor. This will setup the HashMap, assign the number of mails as being 1
     (to stop divide by zero errors) and will set the min number of letters in a valid word
     to be equal to or greater than three (to improve filtering).
     */
    public WordList()
    {
        words = new HashMap( 1000, 0.80F );
        mails = 1;
        minStringLength = 3;
    }

    /**
     Constructor, this assigns the given parameters to the private variables.
     @param m this is the number of mails.
     @param s this is the min number of letters a valid word must contain.
     */
    public WordList( double m, int s )
    {
        mails = m;
        minStringLength = s;
    }


    /**
     This will get search the hashmap for a reference of the word.
     Once it has the number of times it has occured it will divide this by the number of
     mails. It will return this result.

     @return double the number of occurances of that word in the WordList.
     */
    public double getWord( String s )
    {
        // see if its in the hashmap
        Count c = (Count) words.get( s );
        if ( c == null )
        {
            return 0.0;	 // its not present in the HashMap
        }
        else
        {
            double occurances = c.getValue();
            return occurances;
        }

    }


    /**
     This will return the total number of E-mails comprising the WordList.

     @return double the number of E-mails.
     */
    public double getTotal()
    {
        return (double) mails;
    }

    /**
     This will return the min number of letters a word must have to be valid.

     @return int number of letters a valid word must contain.
     */
    public int getMinStringLength()
    {
        final int minLength = minStringLength;
        return minLength;
    }

    /**
     This sets the min number of letters a valid word must contain.

     @param s the min number of letters.
     */
    public void setMinStringLength( final int s )
    {
        minStringLength = s;
    }

    /**
     This will increment the number of occurances of a word in the WordList.
     @param s the word.
     */
    public void updateWord( String s )
    {
        // see if its in the hashmap
        if ( s.length() >= minStringLength )
        {
            Count c = (Count) words.get( s );
            if ( c == null )
            {
                c = new Count();
                words.put( s, c );
            }

            c.increment();
        }
    }

    /**
     This will determine the top ten words which occur most often in the
     WordList. It does this by creating a TreeSet of Map.Entry objects.
     I have supplied a comparator class to oder the Set.

     @return A 2D array containing the top ten words.
     */
    public Object[][] topTen()
    {
        Object[][] topten = new Object[10][2];

        TreeSet sorter = new TreeSet( new Sorting() );
        Set s = words.entrySet();
        sorter.addAll( s );

        Iterator i = sorter.iterator();
        int inSet = 0;
        while ( inSet < 10 && i.hasNext() )
        {
            Entry e = (Entry) i.next();
            String test = (String) e.getKey();
            Count eCount = (Count) e.getValue();
            Integer eValue = new Integer( eCount.getValue() );

            topten[inSet][0] = test;
            topten[inSet][1] = eValue;

            inSet++;
        }

        return topten;
    }

    /**
     This will update the WordList with a list of words.
     Typically this is called when we want to update the WordList with a parsed E-Mail.
     @param t the parsed E-Mail.
     @param d the number of E-Mails the String contains.
     */
    public void update( String t, double d )
    {
        // see if its in the hashmap
        StringTokenizer st = new StringTokenizer( t );
        while ( st.hasMoreTokens() )
        {
            String s = st.nextToken();
            if ( s.length() >= minStringLength )
            {
                Count c = (Count) words.get( s );
                if ( c == null )
                {
                    c = new Count();
                    words.put( s, c );
                }

                c.increment();
            }
        }

        // increment the number of mails
        mails += d;

    }
}


/**
 Class count for use in the hash map to store number of occurances.
 */
class Count implements Serializable
{
    int value = 0;

    /**
     Increments the number of occurances.
     */
    public void increment()
    {
        value++;
    }

    /**
     Decrements the number of occurances.
     I dont think this will be used but left in for completeness.
     */
    public void decrement()
    {
        value--;
    }

    /**
     Retreives the number of occurances.
     @return int the number of occurances.
     */
    public int getValue()
    {
        return value;
    }
}

/**
 This class will compare two Map Entry objects and determine their order.
 */
class Sorting implements Comparator, Serializable
{
    public int compare( Object a, Object b )
    {
        // cast to map entry objects
        Entry x = (Entry) a;
        Entry y = (Entry) b;

        Count xCount = (Count) x.getValue();
        Count yCount = (Count) y.getValue();

        int xValue = xCount.getValue();

        int yValue = yCount.getValue();

        return ( yValue - xValue );
    }
}