package org.codehaus.plexus.cdc;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class MavenModelParser
{
    private BootstrapPomParser bootstrapPomParser;

    public MavenModelParser( File model, Properties properties )
        throws SAXException
    {
        bootstrapPomParser = new BootstrapPomParser();

        bootstrapPomParser.setProperties( properties );
        
        bootstrapPomParser.parse( model );
    }

    public List getDependencies()
    {
        return bootstrapPomParser.getDependencies();
    }

    public String getSourceDirectory()
    {
        return bootstrapPomParser.getSourceDirectory();
    }

    public String getId()
    {
    	return bootstrapPomParser.getId();
    }

    public String getGroupId()
    {
        return bootstrapPomParser.getGroupId();
    }

    public String getArtifactId()
    {
        return bootstrapPomParser.getArtifactId();
    }

    public String getCurrentVersion()
    {
        return bootstrapPomParser.getCurrentVersion();
    }
    
    class BootstrapPomParser
        extends DefaultHandler
    {
        private List dependencies = new ArrayList();

        private Dependency currentDependency;

        private String sourceDirectory;

        private String id;

        private String groupId;

        private String artifactId;

        private String currentVersion;

        private SAXParserFactory saxFactory;

        private boolean insideDependency = false;

        private StringBuffer bodyText = new StringBuffer();

        private File file;

        private Properties properties;
        
        public List getDependencies()
        {
            return dependencies;
        }

        public String getSourceDirectory()
        {
            return sourceDirectory;
        }
        
        public String getGroupId()
        {
            return groupId;
        }
        
        public String getId()
        {
            return id;
        }

        public String getArtifactId()
        {
            return artifactId;
        }
        
        public String getCurrentVersion()
        {
            return currentVersion;
        }  

        public Properties getProperties()
        {
            return properties;
        }
        
        public void setProperties( Properties properties )
        {
            this.properties = properties;
        }
        
        public void parse( File file )
            throws SAXException
        {
            this.file = file;

            System.out.println("Parsing POM " + file.getPath() );
            
            saxFactory = SAXParserFactory.newInstance();

            try
            {
                SAXParser parser = saxFactory.newSAXParser();
    
                InputSource is = new InputSource( new FileInputStream( file ) );

                parser.parse( is, this );
            }
            catch( Exception ex )
            {
                throw new SAXException( "Exception while parsing.", ex );
            }
        }

        private String interpolate( String text, Map namespace )
        {
            Iterator keys = namespace.keySet().iterator();

            while ( keys.hasNext() )
            {
                String key = keys.next().toString();

                Object obj = namespace.get( key );

                String value = obj.toString();

                text = replace( text, "${" + key + "}", value );

                if ( key.indexOf( " " ) == -1 )
                {
                    text = replace( text, "$" + key, value );
                }
            }
            return text;
        }

        private String replace( String text, String repl, String with )
        {
            return replace( text, repl, with, -1 );
        }

        private String replace( String text, String repl, String with, int max )
        {
            if ( text == null || repl == null || with == null || repl.length() == 0 )
            {
                return text;
            }

            StringBuffer buf = new StringBuffer( text.length() );
            int start = 0, end = 0;
            while ( ( end = text.indexOf( repl, start ) ) != -1 )
            {
                buf.append( text.substring( start, end ) ).append( with );
                start = end + repl.length();

                if ( --max == 0 )
                {
                    break;
                }
            }
            buf.append( text.substring( start ) );
            return buf.toString();
        }
        
        public void startElement( String uri, String localName, String rawName, Attributes attributes )
        {
            if ( rawName.equals( "dependency" ) )
            {
                currentDependency = new Dependency();

                insideDependency = true;
            }
        }

        public void characters( char buffer[], int start, int length )
        {
            bodyText.append( buffer, start, length );
        }

        private String getBodyText()
        {
            return bodyText.toString().trim();
        }

        public void endElement( String uri, String localName, String rawName )
            throws SAXException
        {
            if ( rawName.equals( "dependency" ) )
            {
                dependencies.add( currentDependency );

                insideDependency = false;
            }
            else if ( rawName.equals( "id" ) && !insideDependency )
            {
                id = getBodyText();
            }
            else if ( rawName.equals( "groupId" ) && !insideDependency )
            {
                groupId = getBodyText();
            }
            else if ( rawName.equals( "artifactId" ) && !insideDependency )
            {
                artifactId = getBodyText();
            }
            else if ( rawName.equals( "currentVersion" ) && !insideDependency )
            {
                currentVersion = getBodyText();
            }
            else if ( rawName.equals( "sourceDirectory" ) )
            {
                sourceDirectory = getBodyText();
            }
            else if ( rawName.equals( "extend" ) )
            {
                String extend = interpolate( getBodyText(), properties ) ;

                BootstrapPomParser extendedPom = new BootstrapPomParser();

                extendedPom.setProperties( properties );

                if ( new File( extend ).isAbsolute() )
                    extendedPom.parse( new File( extend ) );
                else
                    extendedPom.parse( new File( file.getParentFile(), extend ) );

                dependencies.addAll( extendedPom.getDependencies() );
                
                if ( groupId == null && extendedPom.getGroupId() != null )
                {
                	groupId = extendedPom.getGroupId();
                }

                if ( extendedPom.getSourceDirectory() != null )
                    sourceDirectory = extendedPom.getSourceDirectory();
            }
            else if ( insideDependency )
            {
                if ( rawName.equals( "id" ) )
                {
                    currentDependency.setId( getBodyText() );
                }
                else if ( rawName.equals( "version" ) )
                {
                    currentDependency.setVersion( getBodyText() );
                }
                else if ( rawName.equals( "jar" ) )
                {
                    currentDependency.setJar( getBodyText() );
                }
                else if ( rawName.equals( "type" ) )
                {
                    currentDependency.setType( getBodyText() );
                }
                else if ( rawName.equals( "groupId" ) )
                {
                    currentDependency.setGroupId( getBodyText() );
                }
                else if ( rawName.equals( "artifactId" ) )
                {
                    currentDependency.setArtifactId( getBodyText() );
                }
            }

            bodyText = new StringBuffer();
        }

        public void warning( SAXParseException spe )
        {
            printParseError( "Warning", spe );
        }

        public void error( SAXParseException spe )
        {
            printParseError( "Error", spe );
        }

        public void fatalError( SAXParseException spe )
        {
            printParseError( "Fatal Error", spe );
        }

        private final void printParseError( String type, SAXParseException spe )
        {
            System.err.println( type + " [line " + spe.getLineNumber() +
                                ", row " + spe.getColumnNumber() + "]: " +
                                spe.getMessage() );
        }
    }

    public static class Dependency
    {
        private String id;

        private String version;

        private String url;

        private String jar;

        private String artifactId;

        private String groupId;

        private String type = "jar";

        public Dependency()
        {
        }

        public void setId( String id )
        {
            this.id = id;
        }

        public String getId()
        {
            if ( isValid( getGroupId() )
                && isValid( getArtifactId() ) )
            {
                return getGroupId() + ":" + getArtifactId();
            }

            return id;
        }

        public void setGroupId( String groupId )
        {
            this.groupId = groupId;
        }

        public String getGroupId()
        {
            return groupId;
        }

        public String getArtifactDirectory()
        {
            if ( isValid( getGroupId() ) )
            {
                return getGroupId();
            }

            return getId();
        }

        public String getArtifactId()
        {
            return artifactId;
        }

        public void setArtifactId( String artifactId )
        {
            this.artifactId = artifactId;
        }

        public String getArtifact()
        {
            // If the jar name has been explicty set then use that. This
            // is when the <jar/> element is explicity used in the POM.
            if ( jar != null )
            {
                return jar;
            }

            if ( isValid( getArtifactId() ) )
            {
                return getArtifactId() + "-" + getVersion() + "." + getType();
            }
            else
            {
                return getId() + "-" + getVersion() + "." + getType();
            }
        }

        public void setVersion( String version )
        {
            this.version = version;
        }

        public String getVersion()
        {
            return version;
        }

        public void setJar( String jar )
        {
            // This is a check we need because of the jelly interpolation
            // process. If we don't check an empty string will be set and
            // screw up getArtifact() above.
            if ( jar.trim().length() == 0 )
            {
                return;
            }

            this.jar = jar;
        }

        public String getJar()
        {
            return jar;
        }

        public void setUrl( String url )
        {
            this.url = url;
        }

        public String getUrl()
        {
            return url;
        }

        public String getType()
        {
            return type;
        }

        public void setType( String type )
        {
            this.type = type;
        }

        private boolean isValid( String value )
        {
            if ( value != null
                && value.trim().equals( "" ) == false )
            {
                return true;
            }

            return false;
        }
    }
}
