package org.codehaus.plexus.mailsender.simple;

/*
 * LICENSE
 */

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DateFormatUtilsTest
	extends TestCase
{
    public void testGetDateHeader()
    {
        Calendar calendar = new GregorianCalendar( TimeZone.getTimeZone( "Europe/Oslo" ) );

        calendar.setTime( new Date( 1098099262422l ) );

        String formattedDate = DateFormatUtils.getDateHeader( calendar.getTime() );

        assertEquals( "Mon, 18 Oct 04 13:34:22 +0200", formattedDate );
    }
}
