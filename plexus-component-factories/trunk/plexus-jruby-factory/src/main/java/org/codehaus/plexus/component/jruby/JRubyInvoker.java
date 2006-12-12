package org.codehaus.plexus.component.jruby;

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

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringOutputStream;
import org.jruby.IRuby;
import org.jruby.Ruby;
import org.jruby.RubyIO;
import org.jruby.javasupport.JavaUtil;
import org.jruby.javasupport.bsf.JRubyEngine;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.util.NormalizedFile;

/**
 * Configures and invokes the JRuby runtime. The "invoke" method executes a given
 * script (set as a Reader) and returns the results as an IRuby object.
 * 
 * @author eredmond
 */
public class JRubyInvoker
{
    private boolean assumePrintLoop;

    private boolean assumeLoop;

    private boolean autoSplit;

    private boolean processLineEnds;

    private boolean debug;

    private int warning = -1;

    private Map inputs = new HashMap();

    private List libPaths = new LinkedList();

    private List reqLibs = new LinkedList();

    private ClassLoader classLoader;

    private ComponentDescriptor componentDescriptor;

    private Reader reader;

    private IRuby runtime;

    /**
     * Create a reader JRubyInvoker that reads a JRuby script from the given reader.
     */
    public JRubyInvoker( Reader scriptReader )
    {
        this.reader = scriptReader;
    }

    /**
     * Create a JRubyInvoker that runs under the context of this class loader.
     * @param componentDescriptor
     * @param classLoader
     */
    public JRubyInvoker( ComponentDescriptor componentDescriptor, ClassLoader classLoader )
    {
        this.componentDescriptor = componentDescriptor;
        this.classLoader = classLoader;
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
     * Not required, however if set, the invoker will tear down
     * the runtime before exiting Invoke. If you plan on changing the
     * state of inkove-returned IRubyObjects, you should set
     * this externally via Ruby.getDefaultInstance(), and tear it
     * down when done via tearDown() method.
     * @param runtime
     */
    public void setRuntime( IRuby runtime )
    {
        this.runtime = runtime;
    }

    /**
     * As per the Ruby command line arg -n.
     * @param assumeLoop
     */
    public void setAssumeLoop( boolean assumeLoop )
    {
        this.assumeLoop = assumeLoop;
    }

    /**
     * As per the Ruby command line arg -p.
     * @param assumePrintLoop
     */
    public void setAssumePrintLoop( boolean assumePrintLoop )
    {
        this.assumePrintLoop = assumePrintLoop;
    }

    /**
     * As per the Ruby command line arg -a.
     * @param autoSplit
     */
    public void setAutoSplit( boolean autoSplit )
    {
        this.autoSplit = autoSplit;
    }

    /**
     * As per the Ruby command line arg -W#.
     * @param warning
     */
    public void setWarning( int warning )
    {
        this.warning = warning;
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
     * As per the Ruby command line arg -l.
     * @param processLineEnds
     */
    public void setProcessLineEnds( boolean processLineEnds )
    {
        this.processLineEnds = processLineEnds;
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
    
            if ( classLoader != null )
            {
//                if ( classRealm.getResource( impl ) == null )
//                {
//                    StringBuffer buf = new StringBuffer( "Cannot find: " + impl + " in classpath:" );
//                    for ( int i = 0; i < classRealm.getURLs().length; i++ )
//                    {
//                        URL constituent = classRealm.getURLs()[i];
//                        buf.append( "\n   [" + i + "]  " + constituent );
//                    }
//                    throw new ComponentInstantiationException( buf.toString() );
//                }
    
                theReader = new InputStreamReader( classLoader.getResourceAsStream( impl ) );
            }
            else if ( theReader == null )
            {
                throw new ComponentInstantiationException( "If no classRealm is given in the constructor, a script Reader must be set." );
            }
        }

        Object result = null;
        ClassLoader oldClassLoader = null;
        if ( classLoader != null )
        {
            oldClassLoader = Thread.currentThread().getContextClassLoader();
        }

        StringOutputStream bos = null;

        System.setProperty( "jruby.script", "<invoker>" );
        System.setProperty( "jruby.shell", "/bin/sh" );
        System.setProperty( "jruby.home", new NormalizedFile( System.getProperty("user.dir"), ".jruby" ).getAbsolutePath() );
        System.setProperty( "jruby.lib", new NormalizedFile( System.getProperty("jruby.home"), "lib" ).getAbsolutePath() );

        boolean isExternalRuntime = true;
        if ( runtime == null )
        {
            isExternalRuntime = false;
            runtime = Ruby.getDefaultInstance();
        }

        try
        {
            if ( classLoader != null )
            {
                Thread.currentThread().setContextClassLoader( classLoader );
            }

            bos = new StringOutputStream();

            int read = -1;
            // append the required output streams to the head the script.
            // TODO: Is this still necessary? Could probably let JRuby handle this.
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

            IRubyObject out = new RubyIO( runtime, stdout );
            IRubyObject err = new RubyIO( runtime, stderr );

            BSFManager manager = new BSFManager();

            try
            {
                for (Iterator iter = inputs.entrySet().iterator(); iter.hasNext(); )
                {
                    Map.Entry entry = (Map.Entry)iter.next();

                    String key = (String)entry.getKey();
                    Object value = entry.getValue();
                    if ( key != null && value != null )
                    {
                        manager.declareBean(key , value, value.getClass() );
                    }
                }

                BSFManager.registerScriptingEngine( "ruby", JRubyEngine.class.getName(), new String[] { "rb" } );

                // runtime.getLoadService().init( libPaths );
                
                
                manager.declareBean( "stdout", out, RubyIO.class);
                manager.declareBean( "defout", out, RubyIO.class);
                manager.declareBean( ">", out, RubyIO.class);
                manager.declareBean( "stderr", err, RubyIO.class);
                manager.declareBean( "deferr", err, RubyIO.class);

                manager.declareBean("STDOUT", out, RubyIO.class);
                manager.declareBean("STDERR", err, RubyIO.class);

                manager.declareBean( "VERBOSE", warning == 2 ? Boolean.TRUE : Boolean.FALSE, Boolean.class );
//                manager.declareBean( "VERBOSE", Boolean.TRUE, Boolean.class );
//                manager.declareBean( "DEBUG", Boolean.TRUE, Boolean.class );

                String[] args = new String[1];
                args[0] = buildLibs();
                IRubyObject argumentArray = runtime.newArray( JavaUtil.convertJavaArrayToRuby( runtime, args ) );
                manager.declareBean( "ARGV", argumentArray, String[].class );
                manager.declareBean( "*", argumentArray, String[].class );

                manager.declareBean( "-p", assumePrintLoop ? Boolean.TRUE : Boolean.FALSE, Boolean.class );
                manager.declareBean( "-n", assumeLoop ? Boolean.TRUE : Boolean.FALSE, Boolean.class );
                manager.declareBean( "-a", autoSplit ? Boolean.TRUE : Boolean.FALSE, Boolean.class );
                manager.declareBean( "-l", processLineEnds ? Boolean.TRUE : Boolean.FALSE, Boolean.class );

                result = manager.eval( "ruby", "<invoker>", 1, 1, bos.toString() );
            }
            catch( BSFException e )
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

            if ( runtime != null )
            {
                // Only tear down the runtime if it was set externally
                if ( !isExternalRuntime )
                {
                    runtime.tearDown();
                }
            }

            // If this reader was created in this method, then close it.
            // OTherwise, it was passed in, so its not my job.
            if ( reader == null )
            {
                IOUtil.close( theReader ); 
            }
        }
        return result;
    }

    private String buildLibs()
    {
        String pathSeperator = System.getProperty( "path.separator" );
        pathSeperator = pathSeperator == null ? ";" : pathSeperator;

        if ( !libPaths.isEmpty() )
        {
            StringBuffer libs = new StringBuffer();
            libs.append( "-I" );
            for ( Iterator iter = libPaths.iterator(); iter.hasNext(); )
            {
                String req = (String) iter.next();
                if ( !"".equals( req ) )
                {
                    libs.append( req );
                    libs.append( pathSeperator );
                }
            }
            return libs.toString();
        }
        return "";
    }

    /** 
     * First search the classrealm... if is does not exist, turn the string to a File 
     */
    private InputStream getFileStream( String resourceName )
        throws ComponentInstantiationException
    {
        InputStream scriptStream = null;
        if ( classLoader != null )
        {
            scriptStream = classLoader.getResourceAsStream( resourceName );
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
//            if ( classRealm != null )
//            {
//                buf.append( ":" );
//                for ( int i = 0; i < classRealm.getURLs().length; i++ )
//                {
//                    URL constituent = classRealm.getURLs()[i];
//                    buf.append( "\n   [" + i + "]  " + constituent );
//                }
//            }
            throw new ComponentInstantiationException( buf.toString() );
        }

        return scriptStream;
    }

//    // Build script args
//    private void initializeRuntime( final IRuby runtime, final OutputStream out, final OutputStream err )
//    {
//        runtime.getLoadService().init( libPaths );
//
//        Iterator iter = reqLibs.iterator();
//        while ( iter.hasNext() )
//        {
//            String scriptName = (String) iter.next();
//            RubyKernel.require( runtime.getTopSelf(), runtime.newString( scriptName ) );
//        }
//    }
}
