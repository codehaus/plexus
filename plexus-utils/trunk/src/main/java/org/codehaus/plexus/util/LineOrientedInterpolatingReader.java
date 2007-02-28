package org.codehaus.plexus.util;

/* ====================================================================
 *   Copyright 2001-2004 The Apache Software Foundation.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * ====================================================================
 */

import org.codehaus.plexus.util.reflection.Reflector;
import org.codehaus.plexus.util.reflection.ReflectorException;

import java.io.FilterReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author jdcasey Created on Feb 3, 2005
 */
public class LineOrientedInterpolatingReader
    extends FilterReader
{
    public static final String DEFAULT_START_DELIM = "${";

    public static final String DEFAULT_END_DELIM = "}";

    public static final String DEFAULT_ESCAPE_SEQ = "\\";

    private static final char CARRIAGE_RETURN_CHAR = '\r';

    private static final char NEWLINE_CHAR = '\n';

    private final PushbackReader pushbackReader;

    private final Map context;

    private final String startDelim;

    private final String endDelim;

    private final String escapeSeq;

    private final int minExpressionSize;

    private final Reflector reflector;

    private int lineIdx = -1;

    private String line;

    public LineOrientedInterpolatingReader( Reader reader, Map context, String startDelim, String endDelim,
                                           String escapeSeq )
    {
        super( reader );

        this.startDelim = startDelim;

        this.endDelim = endDelim;

        this.escapeSeq = escapeSeq;

        // Expressions have to be at least this size...
        this.minExpressionSize = startDelim.length() + endDelim.length() + 1;

        this.context = Collections.unmodifiableMap( context );

        this.reflector = new Reflector();

        if ( reader instanceof PushbackReader )
        {
            this.pushbackReader = (PushbackReader) reader;
        }
        else
        {
            this.pushbackReader = new PushbackReader( reader, 1 );
        }
    }

    public LineOrientedInterpolatingReader( Reader reader, Map context, String startDelim, String endDelim )
    {
        this( reader, context, startDelim, endDelim, DEFAULT_ESCAPE_SEQ );
    }

    public LineOrientedInterpolatingReader( Reader reader, Map context )
    {
        this( reader, context, DEFAULT_START_DELIM, DEFAULT_END_DELIM, DEFAULT_ESCAPE_SEQ );
    }

    public int read() throws IOException
    {
        if ( line == null || lineIdx >= line.length() )
        {
            readAndInterpolateLine();
        }

        int next = -1;

        if ( line != null && lineIdx < line.length() )
        {
            next = line.charAt( lineIdx++ );
        }

        return next;
    }

    public int read( char[] cbuf, int off, int len ) throws IOException
    {
        int fillCount = 0;

        for ( int i = off; i < off + len; i++ )
        {
            int next = read();
            if ( next > -1 )
            {
                cbuf[i] = (char) next;
            }
            else
            {
                break;
            }

            fillCount++;
        }

        if ( fillCount == 0 )
        {
            fillCount = -1;
        }

        return fillCount;
    }

    public long skip( long n ) throws IOException
    {
        long skipCount = 0;

        for ( long i = 0; i < n; i++ )
        {
            int next = read();

            if ( next < 0 )
            {
                break;
            }

            skipCount++;
        }

        return skipCount;
    }

    private void readAndInterpolateLine() throws IOException
    {
        String rawLine = readLine();

        if(rawLine != null)
        {
            Set expressions = parseForExpressions( rawLine );

            Map evaluatedExpressions = evaluateExpressions( expressions );

            String interpolated = replaceWithInterpolatedValues( rawLine, evaluatedExpressions );

            if ( interpolated != null && interpolated.length() > 0 )
            {
                line = interpolated;
                lineIdx = 0;
            }
        }
        else
        {
            line = null;
            lineIdx = -1;
        }
    }

    private String readLine() throws IOException
    {
        StringBuffer lineBuffer = new StringBuffer( 40 ); // half of the "normal" line maxsize
        int next = -1;

        boolean lastWasCR = false;
        while ( ( next = pushbackReader.read() ) > -1 )
        {
            char c = (char) next;

            if ( c == CARRIAGE_RETURN_CHAR )
            {
                lastWasCR = true;
                lineBuffer.append( c );
            }
            else if ( c == NEWLINE_CHAR )
            {
                lineBuffer.append( c );
                break; // end of line.
            }
            else if ( lastWasCR )
            {
                pushbackReader.unread( c );
                break;
            }
            else
            {
                lineBuffer.append( c );
            }
        }

        if ( lineBuffer.length() < 1 )
        {
            return null;
        }
        else
        {
            return lineBuffer.toString();
        }
    }

    private String replaceWithInterpolatedValues( String rawLine, Map evaluatedExpressions )
    {
        String result = rawLine;

        for ( Iterator it = evaluatedExpressions.entrySet().iterator(); it.hasNext(); )
        {
            Map.Entry entry = (Map.Entry) it.next();

            String expression = (String) entry.getKey();

            String value = String.valueOf( entry.getValue() );

            result = findAndReplaceUnlessEscaped( result, expression, value );
        }

        return result;
    }

    private Map evaluateExpressions( Set expressions )
    {
        Map evaluated = new TreeMap();

        for ( Iterator it = expressions.iterator(); it.hasNext(); )
        {
            String rawExpression = (String) it.next();

            String realExpression = rawExpression.substring( startDelim.length(), rawExpression.length()
                - endDelim.length() );

            String[] parts = realExpression.split( "\\." );
            if ( parts.length > 0 )
            {
                Object value = context.get( parts[0] );

                if ( value != null )
                {
                    for ( int i = 1; i < parts.length; i++ )
                    {
                        try
                        {
                            value = reflector.getObjectProperty( value, parts[i] );

                            if ( value == null )
                            {
                                break;
                            }
                        }
                        catch ( ReflectorException e )
                        {
                            // TODO: Fix this! It should report, but not interrupt.
                            e.printStackTrace();

                            break;
                        }
                    }

                    evaluated.put( rawExpression, value );
                }
            }
        }

        return evaluated;
    }

    private Set parseForExpressions( String rawLine )
    {
        Set expressions = new HashSet();

        if ( rawLine != null )
        {
            int placeholder = -1;

            do
            {
                // find the beginning of the next expression.
                int start = findDelimiter( rawLine, startDelim, placeholder );

                // if we can't find a start-delimiter, then there is no valid expression. Ignore everything else.
                if ( start < 0 )
                {
                    // no expression found.
                    break;
                }

                // find the end of the next expression.
                int end = findDelimiter( rawLine, endDelim, start + 1 );

                // if we can't find an end-delimiter, then this is not a valid expression. Ignore it.
                if ( end < 0 )
                {
                    // no VALID expression found.
                    break;
                }

                // if we reach this point, we have a valid start and end position, which 
                // means we have a valid expression. So, we add it to the set of
                // expressions in need of evaluation.
                expressions.add( rawLine.substring( start, end + endDelim.length() ) );

                // increment the placeholder so we can look beyond this expression.
                placeholder = end + 1;
            } while ( placeholder < rawLine.length() - minExpressionSize );
        }

        return expressions;
    }

    private int findDelimiter( String rawLine, String delimiter, int lastPos )
    {
        int placeholder = lastPos;

        int position = -1;
        do
        {
            position = rawLine.indexOf( delimiter, placeholder );

            if ( position < 0 )
            {
                break;
            }
            else
            {
                int escEndIdx = rawLine.indexOf( escapeSeq, placeholder ) + escapeSeq.length();

                if ( escEndIdx > escapeSeq.length() - 1 && escEndIdx == position )
                {
                    placeholder = position + 1;
                    position = -1;
                }
            }

        } while ( position < 0 && placeholder < rawLine.length() - endDelim.length() );
        // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        // use length() - endDelim.length() b/c otherwise there is nothing left to search.

        return position;
    }
    
    private String findAndReplaceUnlessEscaped(String rawLine, String search, String replace)
    {
        StringBuffer lineBuffer = new StringBuffer( (int)(rawLine.length() * 1.5) );
        
        int lastReplacement = -1;
        
        do
        {
            int nextReplacement = rawLine.indexOf( search, lastReplacement + 1 );
            if(nextReplacement > -1)
            {
                if(lastReplacement < 0)
                {
                    lastReplacement = 0;
                }
                
                lineBuffer.append( rawLine.substring( lastReplacement, nextReplacement ) );
                
                int escIdx = rawLine.indexOf( escapeSeq, lastReplacement + 1 );
                if(escIdx > -1 && escIdx + escapeSeq.length() == nextReplacement)
                {
                    lineBuffer.setLength( lineBuffer.length() - escapeSeq.length() );
                    lineBuffer.append( search );
                }
                else
                {
                    lineBuffer.append( replace );
                }
                
                lastReplacement = nextReplacement + search.length();
            }
            else
            {
                break;
            }
        }
        while(lastReplacement > -1);
        
        if( lastReplacement < rawLine.length() )
        {
            lineBuffer.append( rawLine.substring( lastReplacement ) );
        }

        return lineBuffer.toString();
    }
    
}