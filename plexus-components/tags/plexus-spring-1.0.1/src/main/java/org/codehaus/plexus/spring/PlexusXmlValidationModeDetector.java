package org.codehaus.plexus.spring;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.BufferedReader;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.util.StringUtils;

/**
 * 
 * @version $Id$
 */
public class PlexusXmlValidationModeDetector
{

    /**
     * The token that indicates the start of an XML comment.
     */
    private static final String START_COMMENT = "<!--";

    /**
     * The token that indicates the end of an XML comment.
     */
    private static final String END_COMMENT = "-->";

    /**
     * Indicates whether or not the current parse position is inside an XML comment.
     */
    private boolean inComment;

    /**
     * Detect the validation mode for the XML document in the supplied {@link InputStream}. Note that the supplied
     * {@link InputStream} is closed by this method before returning.
     * 
     * @param inputStream the InputStream to parse
     * @throws IOException in case of I/O failure
     * @see #VALIDATION_DTD
     * @see #VALIDATION_XSD
     */
    public boolean isPlexusDefinition( InputStream inputStream )
        throws IOException
    {
        // Peek into the file to look for DOCTYPE.
        BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream ) );
        try
        {
            String content;
            while ( ( content = reader.readLine() ) != null )
            {
                content = consumeCommentTokens( content );
                if ( this.inComment || !StringUtils.hasText( content ) )
                {
                    continue;
                }
                if ( hasOpeningTag( content ) )
                {
                    return isPlexusElement( content );
                }
            }
            return false;
        }
        catch ( CharConversionException ex )
        {
            // Choked on some character encoding...
            // Leave the decision up to the caller.
            return false;
        }
        finally
        {
            reader.close();
        }
    }

    private boolean isPlexusElement( String content )
    {
        // plexus files can have a top element of <component-set> or <plexus>
        return ( content.indexOf( "<component-set" ) >= 0 ) || ( content.indexOf( "<plexus" ) >= 0 );
    }

    // Copied from XmlValidationModeDetector

    /**
     * Does the supplied content contain an XML opening tag. If the parse state is currently in an XML comment then this
     * method always returns false. It is expected that all comment tokens will have consumed for the supplied content
     * before passing the remainder to this method.
     */
    private boolean hasOpeningTag( String content )
    {
        if ( this.inComment )
        {
            return false;
        }
        int openTagIndex = content.indexOf( '<' );
        return ( openTagIndex > -1 && content.length() > openTagIndex && Character.isLetter( content.charAt( openTagIndex + 1 ) ) );
    }

    /**
     * Consumes all the leading comment data in the given String and returns the remaining content, which may be empty
     * since the supplied content might be all comment data. For our purposes it is only important to strip leading
     * comment content on a line since the first piece of non comment content will be either the DOCTYPE declaration or
     * the root element of the document.
     */
    private String consumeCommentTokens( String line )
    {
        if ( line.indexOf( START_COMMENT ) == -1 && line.indexOf( END_COMMENT ) == -1 )
        {
            return line;
        }
        while ( ( line = consume( line ) ) != null )
        {
            if ( !this.inComment && !line.trim().startsWith( START_COMMENT ) )
            {
                return line;
            }
        }
        return line;
    }

    /**
     * Consume the next comment token, update the "inComment" flag and return the remaining content.
     */
    private String consume( String line )
    {
        int index = ( this.inComment ? endComment( line ) : startComment( line ) );
        return ( index == -1 ? null : line.substring( index ) );
    }

    /**
     * Try to consume the {@link #START_COMMENT} token.
     * 
     * @see #commentToken(String, String, boolean)
     */
    private int startComment( String line )
    {
        return commentToken( line, START_COMMENT, true );
    }

    private int endComment( String line )
    {
        return commentToken( line, END_COMMENT, false );
    }

    /**
     * Try to consume the supplied token against the supplied content and update the in comment parse state to the
     * supplied value. Returns the index into the content which is after the token or -1 if the token is not found.
     */
    private int commentToken( String line, String token, boolean inCommentIfPresent )
    {
        int index = line.indexOf( token );
        if ( index > -1 )
        {
            this.inComment = inCommentIfPresent;
        }
        return ( index == -1 ? index : index + token.length() );
    }

}
