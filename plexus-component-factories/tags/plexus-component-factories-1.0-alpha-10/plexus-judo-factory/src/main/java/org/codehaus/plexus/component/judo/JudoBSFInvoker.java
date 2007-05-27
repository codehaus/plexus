package org.codehaus.plexus.component.judo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.component.MapOrientedComponent;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringOutputStream;

import com.judoscript.JudoEngine;

/**
 * Configures and invokes the Judo runtime. The "invoke" method executes a given
 * script (set as a Reader) and returns the results.
 * 
 * @author eredmond
 */
public class JudoBSFInvoker
    implements MapOrientedComponent
{
    private boolean debug;

    private Map inputs = new HashMap();

    private List libPaths = new LinkedList();

    private List reqLibs = new LinkedList();

    private ClassRealm classRealm;

    private ComponentDescriptor componentDescriptor;

    private Reader reader;

    /**
     * Create a reader JRubyInvoker that reads a JRuby script from the given reader.
     */
    public JudoBSFInvoker( Reader scriptReader )
    {
        this.reader = scriptReader;
    }

    /**
     * Create a JRubyInvoker that runs under the context of this class loader.
     * @param componentDescriptor
     * @param classRealm
     */
    public JudoBSFInvoker( ComponentDescriptor componentDescriptor, ClassRealm classRealm )
    {
        this.componentDescriptor = componentDescriptor;
        this.classRealm = classRealm;
    }

    /**
     * The sole way of sending a script to this invoker object.
     * If not set, invoker will extract reader from the componentDescriptor.
     * @param reader
     */
    public void setReader( Reader reader )
    {
        this.reader = reader;
    }

    /**
     * As per the Ruby command line arg -d. (sets $DEBUG to true)
     * @param debug
     */
    public void setDebug( boolean debug )
    {
        this.debug = debug;
    }

    /**
     * Adds a library as per the Ruby command line arg -I.
     * @param libPath
     */
    public void addLibPath( String libPath )
    {
        this.libPaths.add( libPath );
    }

    /**
     * Adds a 'require' file as per the Ruby command line arg -r.
     * @param reqLib
     */
    public void addReqLib( String reqLib )
    {
        this.reqLibs.add( reqLib );
    }

    /**
     * Appends an input value with the given key to the Ruby
     * script by prepending the following code to the Ruby script:
     *  $INPUT['key'] = value;
     * 
     * @param key
     * @param value
     */
    public void inputValue( String key, Object value )
    {
        this.inputs.put( key, value );
    }

    /**
     * Invokes the script after all other values are set.
     * @return an Object of possibly returned value
     */
    public Object invoke()
        throws IOException, ComponentInstantiationException
    {
        return invoke( System.out, System.err );
    }

    /**
     * Invokes the script after all other values are set.
     * @param stdout stream where jruby output to
     * @param stderr stream where jruby errors to
     * @return an Object of possibly returned values
     */
    public Object invoke( OutputStream stdout, OutputStream stderr )
        throws IOException, ComponentInstantiationException
    {
        Reader theReader = reader;

        // Use the given reader, unless it it null. Then load one.
        if ( theReader == null )
        {
            String impl = componentDescriptor.getImplementation();
            if ( !impl.startsWith( "/" ) )
            {
                impl = "/" + impl;
            }
    
            if ( classRealm != null )
            {
                theReader = new InputStreamReader( classRealm.getResourceAsStream( impl ) );
            }
            else if ( theReader == null )
            {
                throw new ComponentInstantiationException( "If no classRealm is given in the constructor, a script Reader must be set." );
            }
        }

        Object result = null;
        ClassLoader oldClassLoader = null;
        if ( classRealm != null )
        {
            oldClassLoader = Thread.currentThread().getContextClassLoader();
        }

        StringOutputStream bos = null;

        try
        {
            if ( classRealm != null )
            {
                Thread.currentThread().setContextClassLoader( classRealm.getClassLoader() );
            }

            bos = new StringOutputStream();

            int read = -1;

            // append the required output streams to the head the script.
            // TODO: Is this still necessary?
            for ( Iterator iter = reqLibs.iterator(); iter.hasNext(); )
            {
                String reqLibPath = (String) iter.next();
                InputStream ris = getFileStream( reqLibPath );
                while ( ( read = ris.read() ) != -1 )
                {
                    bos.write( read );
                }
                ris.close();
            }

            while ( ( read = theReader.read() ) != -1 )
            {
                bos.write( read );
            }
            bos.flush();

            if ( debug )
            {
                stdout.write( bos.toString().getBytes() );
                stdout.flush();
            }

//            IRubyObject out = new RubyIO( runtime, stdout );
//            IRubyObject err = new RubyIO( runtime, stderr );

            try
            {
                JudoEngine je = new JudoEngine();

                je.runCode( bos.toString(), new String[] {}, inputs );
            }
            catch( Exception e )
            {
                e.printStackTrace();
            }
        }
        finally
        {
            if ( oldClassLoader != null )
            {
                Thread.currentThread().setContextClassLoader( oldClassLoader );
            }

            if ( bos != null )
                bos.close();

            // If this reader was created in this method, then close it.
            // OTherwise, it was passed in, so its not my job.
            if ( reader == null )
            {
                IOUtil.close( theReader ); 
            }
        }
        return result;
    }

    /** 
     * First search the classrealm... if is does not exist, turn the string to a File 
     */
    private InputStream getFileStream( String resourceName )
        throws ComponentInstantiationException
    {
        InputStream scriptStream = null;
        if ( classRealm != null )
        {
            scriptStream = classRealm.getResourceAsStream( resourceName );
            if ( scriptStream == null )
            {
                File resourceFile = new File( resourceName );
                if ( resourceFile.exists() )
                {
                    try
                    {
                        scriptStream = new FileInputStream( resourceFile );
                    }
                    catch ( FileNotFoundException e )
                    {
                        throw new ComponentInstantiationException( "Volitle file. This should not happen!", e );
                    }
                }
            }
        }
        else
        {
            scriptStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( resourceName );
        }

        if ( scriptStream == null )
        {
            StringBuffer buf = new StringBuffer( "Cannot find: " + resourceName + " in classpath" );

            throw new ComponentInstantiationException( buf.toString() );
        }

        return scriptStream;
    }

    public void setComponentConfiguration( Map inputs ) throws ComponentConfigurationException
    {
        inputs.putAll( inputs );
    }

    public void addComponentRequirement( ComponentRequirement arg0, Object arg1 ) throws ComponentConfigurationException
    {
        
    }
}
