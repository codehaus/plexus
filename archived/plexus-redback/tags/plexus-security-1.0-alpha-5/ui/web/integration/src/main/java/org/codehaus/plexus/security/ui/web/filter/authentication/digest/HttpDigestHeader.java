package org.codehaus.plexus.security.ui.web.filter.authentication.digest;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.security.ui.web.HttpUtils;
import org.codehaus.plexus.security.ui.web.filter.authentication.HttpAuthenticationException;
import org.codehaus.plexus.util.Base64;
import org.codehaus.plexus.util.StringUtils;

import java.util.Properties;

/**
 * HttpDigestHeader
 * 
 * @plexus.component role="org.codehaus.plexus.security.ui.web.filter.authentication.digest.HttpClientHeader"
 *                   instantiation-strategy="per-lookup"
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
class HttpDigestHeader
    extends AbstractLogEnabled
{
    public String username;

    public String realm;

    public String nonce;

    public String uri;

    public String response;

    public String qop;

    public String nc;

    public String cnonce;

    public void parseClientHeader( String rawHeader, String expectedRealm, String digestKey )
        throws HttpAuthenticationException
    {
        Properties authHeaderProps = HttpUtils.complexHeaderToProperties( rawHeader, ",", "=" );

        username = authHeaderProps.getProperty( "username" );
        realm = authHeaderProps.getProperty( "realm" );
        nonce = authHeaderProps.getProperty( "nonce" );
        uri = authHeaderProps.getProperty( "uri" );
        response = authHeaderProps.getProperty( "response" );
        qop = authHeaderProps.getProperty( "qop" );
        nc = authHeaderProps.getProperty( "nc" );
        cnonce = authHeaderProps.getProperty( "cnonce" );

        // [RFC 2067] Validate all required values
        if ( StringUtils.isEmpty( username ) || StringUtils.isEmpty( realm ) || StringUtils.isEmpty( nonce )
            || StringUtils.isEmpty( uri ) || StringUtils.isEmpty( response ) )
        {
            getLogger().debug( "Missing mandatory fields: Raw Digest Header : [" + rawHeader + "]" );

            throw new HttpAuthenticationException( "Missing mandatory digest fields per RFC2069." );
        }

        // [RFC 2617] Validate realm.
        if ( !StringUtils.equals( expectedRealm, realm ) )
        {
            getLogger().debug( "Realm name is invalid: expected [" + expectedRealm + "] but got [" + realm + "]" );

            throw new HttpAuthenticationException( "Response realm does not match expected realm." );
        }

        // [RFC 2617] Validate "auth" qop
        if ( StringUtils.equals( "auth", qop ) )
        {
            if ( StringUtils.isEmpty( nc ) || StringUtils.isEmpty( cnonce ) )
            {
                getLogger().debug( "Missing mandatory qop fields: nc [" + nc + "] cnonce [" + cnonce + "]" );

                throw new HttpAuthenticationException( "Missing mandatory qop digest fields per RFC2617." );
            }
        }

        // [RFC 2617] Validate nonce
        if ( !Base64.isArrayByteBase64( nonce.getBytes() ) )
        {
            getLogger().debug( "Nonce is not encoded in Base64: nonce [" + nonce + "]" );

            throw new HttpAuthenticationException( "Response nonce is not encoded in Base64." );
        }

        // Decode nonce
        String decodedNonce = new String( Base64.decodeBase64( nonce.getBytes() ) );
        String nonceTokens[] = StringUtils.split( decodedNonce, ":" );

        // Validate nonce format
        if ( nonceTokens.length != 2 )
        {
            getLogger().debug( "Nonce format expected [2] elements, but got [" + nonceTokens.length
                                   + "] instead.  Decoded nonce [" + decodedNonce + "]" );

            throw new HttpAuthenticationException( "Nonce format is invalid.  "
                + "Received an unexpected number of sub elements." );
        }

        // Extract nonce timestamp
        long nonceTimestamp = 0;

        try
        {
            nonceTimestamp = Long.parseLong( nonceTokens[0] );
        }
        catch ( NumberFormatException e )
        {
            throw new HttpAuthenticationException( "Unexpected nonce timestamp." );
        }

        // Extract nonce signature
        String expectedSignature = Digest.md5Hex( nonceTimestamp + ":" + digestKey );

        if ( !StringUtils.equals( expectedSignature, nonceTokens[1] ) )
        {
            getLogger().error( "Nonce parameter has been compromised." );

            throw new HttpAuthenticationException( "Nonce parameter has been compromised." );
        }
    }
}