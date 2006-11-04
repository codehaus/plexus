/*

 Copyright (c) 2002 John Casey. All rights reserved.

 SEE licenses/cj-license.txt FOR MORE INFORMATION.

 */

/*
 * AbstractOption.java
 *
 * Created on November 19, 2001, 4:38 PM
 */

package org.codehaus.plexus.cling.cli;

import java.util.StringTokenizer;

/**
 * @author John Casey
 * @version
 */
public abstract class AbstractOption
    implements Option
{

    private static final int FIRST_COL_WIDTH = 35;

    private static final int SECOND_COL_WIDTH = 45;

    private String description;

    private String longName;

    private Character shortName;

    private final boolean required;

    private final String objectProperty;

    /** Creates new AbstractOption */
    public AbstractOption( boolean required, Character shortName, String longName, String description, String objectProperty )
    {
        this.required = required;
        this.shortName = shortName;
        this.longName = longName;
        this.description = description;
        this.objectProperty = objectProperty;
    }

    public Character getShortName()
    {
        return shortName;
    }
    
    public String getObjectProperty() {
        return objectProperty;
    }

    public String getLongName()
    {
        return longName;
    }

    public boolean isRequired()
    {
        return required;
    }

    public abstract boolean isSatisfied();

    public abstract boolean requiresValue();

    public String getUsage()
    {
        StringBuffer sb = new StringBuffer( 30 );
        sb.append( "  " );

        int lineLen = 0;
        if ( longName != null )
        {
            sb.append( "--" );
            sb.append( longName );
            if ( requiresValue() )
            {
                sb.append( "=<value>" );
            }

            if ( !isRequired() )
            {
                sb.append( " [OPT]" );
            }

            lineLen = sb.length();
        }

        if ( shortName.charValue() != '\00' )
        {
            StringBuffer buf = new StringBuffer();
            if ( longName != null )
            {
                sb.append( "\n" );
                buf.append( "   " );
            }

            buf.append( "-" );
            buf.append( shortName );
            if ( requiresValue() )
            {
                buf.append( " <value>" );
            }
            lineLen = buf.length();

            sb.append( buf.toString() );
        }

        int padding = FIRST_COL_WIDTH - lineLen;
        if ( padding > 0 )
        {
            for ( int i = 0; i < padding; i++ )
            {
                sb.append( ' ' );
            }
        }

        if ( description.length() + 5 > SECOND_COL_WIDTH )
        {
            StringBuffer tmp = new StringBuffer( FIRST_COL_WIDTH + 2 );
            tmp.append( "  " );
            for ( int i = 0; i < FIRST_COL_WIDTH; i++ )
            {
                tmp.append( " " );
            }
            String offset = tmp.toString();

            StringBuffer descBuf = new StringBuffer( description.length() );
            StringTokenizer tokens = new StringTokenizer( description, " \n", true );

            int currentLineLen = 0;
            while ( tokens.hasMoreTokens() )
            {
                String token = tokens.nextToken();
                if ( token.startsWith( "\n" )
                    || ((descBuf.length() + token.length() - currentLineLen) > SECOND_COL_WIDTH) )
                {
                    descBuf.append( "\n" );
                    descBuf.append( offset );
                    currentLineLen = descBuf.length();
                    descBuf.append( token.trim() );
                }
                else
                {
                    descBuf.append( token );
                }

            }

            sb.append( descBuf.toString() );
        }
        else
        {
            sb.append( description );
        }

        return sb.toString();
    }

    public boolean equals( Object obj )
    {
        if ( !(obj instanceof AbstractOption) )
        {
            return false;
        }

        AbstractOption opt = (AbstractOption) obj;
        boolean result = ((shortName == opt.shortName) && (longName.equals( opt.longName )));

        return result;
    }

    public int hashCode()
    {
        int result = 19;

        result += shortName.hashCode();
        result *= longName.hashCode();

        return result;
    }

}