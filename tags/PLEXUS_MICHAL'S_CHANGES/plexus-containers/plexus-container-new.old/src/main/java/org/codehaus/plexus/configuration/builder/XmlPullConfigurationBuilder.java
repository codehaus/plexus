package org.codehaus.plexus.configuration.builder;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.DefaultPlexusConfiguration;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class XmlPullConfigurationBuilder
{
    /**
     *
     * @param reader
     * @return
     * @throws java.lang.Exception
     */
    public PlexusConfiguration parse( Reader reader )
        throws Exception
    {
        List elements = new ArrayList();

        List values = new ArrayList();

        PlexusConfiguration configuration = null;

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

        XmlPullParser parser = factory.newPullParser();

        parser.setInput( reader );

        int eventType = parser.getEventType();

        while ( eventType != XmlPullParser.END_DOCUMENT )
        {
            if ( eventType == XmlPullParser.START_TAG )
            {
                String rawName = parser.getName();

                DefaultPlexusConfiguration childConfiguration = createConfiguration( rawName, getLocationString() );
                // depth of new childConfiguration (not decrementing here, childConfiguration
                // is to be added)
                int depth = elements.size();

                if ( depth > 0 )
                {
                    DefaultPlexusConfiguration parent = (DefaultPlexusConfiguration) elements.get( depth - 1 );

                    parent.addChild( childConfiguration );
                }

                elements.add( childConfiguration );

                values.add( new StringBuffer() );

                int attributesSize = parser.getAttributeCount();

                for ( int i = 0; i < attributesSize; i++ )
                {
                    String name = parser.getAttributeName( i );

                    String value = parser.getAttributeValue( i );

                    childConfiguration.setAttribute( name, value );
                }
            }
            else if ( eventType == XmlPullParser.TEXT )
            {
                // it is possible to play micro-optimization here by doing
                // manual trimming and thus preserve some precious bits
                // of memory, but it's really not important enough to justify
                // resulting code complexity
                int depth = values.size() - 1;

                StringBuffer valueBuffer = (StringBuffer) values.get( depth );

                valueBuffer.append( parser.getText() );
            }
            else if ( eventType == XmlPullParser.END_TAG )
            {
                int depth = elements.size() - 1;

                DefaultPlexusConfiguration finishedConfiguration = (DefaultPlexusConfiguration) elements.remove( depth );

                String accumulatedValue = ( values.remove( depth ) ).toString();

                if ( finishedConfiguration.getChildren().length == 0 )
                {
                    // leaf node
                    String finishedValue;

                    if ( 0 == accumulatedValue.length() )
                    {
                        finishedValue = null;
                    }
                    else
                    {
                        finishedValue = accumulatedValue.trim();
                    }

                    finishedConfiguration.setValue( finishedValue );
                }
                else
                {
                    String trimmedValue = accumulatedValue.trim();

                    if ( trimmedValue.length() > 0 )
                    {
                        throw new Exception( "Not allowed to define mixed content in the "
                                             + "element " + finishedConfiguration.getName() );
                    }
                }

                if ( 0 == depth )
                {
                    configuration = finishedConfiguration;
                }
            }

            eventType = parser.next();
        }

        reader.close();

        return configuration;
    }

    /**
     * Create a new <code>DefaultConfiguration</code> with the specified
     * local name and location.
     *
     * @param localName a <code>String</code> value
     * @param location a <code>String</code> value
     * @return a <code>DefaultConfiguration</code> value
     */
    protected DefaultPlexusConfiguration createConfiguration( String localName,
                                                        String location )
    {
        return new DefaultPlexusConfiguration( localName );
    }

    /**
     * Returns a string showing the current system ID, line number and column number.
     *
     * @return a <code>String</code> value
     */
    protected String getLocationString()
    {
        return "Unknown";
    }
}