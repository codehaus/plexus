package org.codehaus.plexus.mailsender.util;

/*
 * LICENSE
 */

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DateFormatUtils
{
    private static SimpleDateFormat formatter = new SimpleDateFormat( "EEE, dd MMM yy HH:mm:ss Z", Locale.ENGLISH );

    public static synchronized String getDateHeader( Date date )
    {
        return formatter.format( date );
    }
}
