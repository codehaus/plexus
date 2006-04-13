package org.codehaus.plexus.httpd;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DefaultProcessor
    implements Processor
{
    // I'm emulating mx4j but why is the default page hidden in the processor. jvz.
    private String defaultPage = "server";

    private VelocityEngine velocity;

    private static String root = "org/apache/plexus/manager/adaptor/processor";

    private Map mimeTypes = new HashMap();

    // Set the classloader as we need to make this work in plexus correctly.
    private ClassLoader targetClassLoader = ClassLoader.getSystemClassLoader();

    /** Indicated whether the file are read from a file */
    private boolean useJar = true;


    public DefaultProcessor()
    {
        mimeTypes.put( ".gif", "image/gif" );
        mimeTypes.put( ".jpg", "image/jpg" );
        mimeTypes.put( ".png", "image/png" );
        mimeTypes.put( ".tif", "image/tiff" );
        mimeTypes.put( ".tiff", "image/tiff" );
        mimeTypes.put( ".html", "text/html" );
        mimeTypes.put( ".htm", "text/html" );
        mimeTypes.put( ".txt", "text/plain" );
        mimeTypes.put( ".xml", "text/xml" );
        mimeTypes.put( ".xsl", "text/xsl" );
        mimeTypes.put( ".css", "text/css" );
        mimeTypes.put( ".js", "text/x-javascript" );
        mimeTypes.put( ".jar", "application/java-archive" );

        // Velocity engine setup.
        velocity = new VelocityEngine();

        Properties p = new Properties();

        try
        {
            p.load( Thread.currentThread()
                    .getContextClassLoader().getResourceAsStream( root + "/velocity.properties" ) );

            System.out.println( "properties loaded!" );

            velocity.init( p );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }


    }

    public String getName()
    {
        return "Velocity Processor";
    }

    public void writeResponse( HttpOutputStream out,
                               HttpInputStream in,
                               Context context,
                               String template )
        throws Exception
    {
        template = root + "/vsl/" + template + ".vsl";

        Writer w = new BufferedWriter( new OutputStreamWriter( out ) );

        out.setCode( HttpConstants.STATUS_OKAY );
        out.setHeader( "Content-Type", "text/html" );
        // added some caching attributes to fornce not to cache
        out.setHeader( "Cache-Control", "no-cache" );
        out.setHeader( "expires", "now" );
        out.setHeader( "pragma", "no-cache" );
        out.sendHeaders();

        velocity.mergeTemplate( template, context, w );

        w.flush();
        //w.close();
    }

    public void writeError( HttpOutputStream out,
                            HttpInputStream in,
                            Exception e )
        throws IOException
    {
    }

    public String preProcess( String path )
    {
        if ( path.equals( "/" ) )
        {
            path = "/" + defaultPage;
        }

        return path;
    }

    public String notFoundElement( String path,
                                   HttpOutputStream out,
                                   HttpInputStream in )
        throws IOException, HttpException
    {
        File file = new File( this.root, path );
        //if ( log.isEnabledFor( Logger.INFO ) ) log.info( "Processing file request " + file );
        String name = file.getName();
        int extensionIndex = name.lastIndexOf( '.' );
        String mime = null;
        if ( extensionIndex < 0 )
        {
            //log.warn( "Filename has no extensions " + file.toString() );
            mime = "text/plain";
        }
        else
        {
            String extension = name.substring( extensionIndex, name.length() );
            if ( mimeTypes.containsKey( extension ) )
            {
                mime = (String) mimeTypes.get( extension );
            }
            else
            {
                //log.warn( "MIME type not found " + extension );
                mime = "text/plain";
            }
        }
        try
        {
            //if ( log.isEnabledFor( Logger.DEBUG ) ) log.debug( "Trying to read file " + file );
            BufferedInputStream fileIn = new BufferedInputStream( getInputStream( path ) );
            ByteArrayOutputStream outArray = new ByteArrayOutputStream();
            BufferedOutputStream outBuffer = new BufferedOutputStream( outArray );
            int piece = 0;
            while ( ( piece = fileIn.read() ) >= 0 )
            {
                outBuffer.write( piece );
            }
            outBuffer.flush();
            out.setCode( HttpConstants.STATUS_OKAY );
            out.setHeader( "Content-type", mime );
            out.sendHeaders();
            //if ( log.isEnabledFor( Logger.DEBUG ) ) log.debug( "File output " + mime );
            outArray.writeTo( out );
            fileIn.close();
        }
        catch ( Exception e )
        {
            //log.warn( "Exception loading file " + file, e );
            throw new HttpException( HttpConstants.STATUS_NOT_FOUND, "file " + file + " not found" );
        }
        return null;
    }

    protected InputStream getInputStream( String path )
    {
        InputStream file = null;
        if ( !useJar )
        {
            try
            {
                // load from a dir
                file = new FileInputStream( new File( this.root, path ) );
            }
            catch ( FileNotFoundException e )
            {
                //Logger log = getLogger();
                //log.error( "File not found", e );
            }
        }
        else
        {
            // load from a jar
            String targetFile = this.root;
            // workaround, should tought of somehting better
            if ( path.startsWith( "/" ) )
            {
                targetFile += path;
            }
            else
            {
                targetFile += "/" + path;
            }
            if ( root != null )
            {
                file = targetClassLoader.getResourceAsStream( targetFile );
            }
            if ( file == null )
            {
                ClassLoader cl = getClass().getClassLoader();
                if ( cl == null )
                {
                    file = ClassLoader.getSystemClassLoader().getResourceAsStream( targetFile );
                }
                else
                {
                    file = getClass().getClassLoader().getResourceAsStream( targetFile );
                }
                file = getClass().getClassLoader().getResourceAsStream( targetFile );
            }
        }

        return file;
    }
}
