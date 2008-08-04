package org.codehaus.plexus.mailsender.javamail;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

/**
 * @author <a href="mailto:evenisse@apache.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public class DummyTrustManager
    implements X509TrustManager
{

    public void checkClientTrusted( X509Certificate[] cert, String authType )
    {
        // everything is trusted
    }

    public void checkServerTrusted( X509Certificate[] cert, String authType )
    {
        // everything is trusted
    }

    public X509Certificate[] getAcceptedIssuers()
    {
        return new X509Certificate[0];
    }
}

