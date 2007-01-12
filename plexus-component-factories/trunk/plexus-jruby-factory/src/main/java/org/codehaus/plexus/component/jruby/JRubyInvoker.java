package org.codehaus.plexus.component.jruby;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;

import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.jruby.IRuby;

/**
 * Configures and invokes the JRuby runtime. The "invoke" method executes a given
 * script (set as a Reader) and returns the results as an IRuby object.
 * 
 * @author eredmond
 */
public interface JRubyInvoker
{
    /**
     * The sole way of sending a script to this invoker object.
     * If not set, invoker will extract reader from the componentDescriptor.
     * @param reader
     */
    public void setReader( Reader reader );

    /**
     * Not required, however if set, the invoker will tear down
     * the runtime before exiting Invoke. If you plan on changing the
     * state of inkove-returned IRubyObjects, you should set
     * this externally via Ruby.getDefaultInstance(), and tear it
     * down when done via tearDown() method.
     * @param runtime
     */
    public void setRuntime( IRuby runtime );

    /**
     * As per the Ruby command line arg -n.
     * @param assumeLoop
     */
    public void setAssumeLoop( boolean assumeLoop );

    /**
     * As per the Ruby command line arg -p.
     * @param assumePrintLoop
     */
    public void setAssumePrintLoop( boolean assumePrintLoop );

    /**
     * As per the Ruby command line arg -a.
     * @param autoSplit
     */
    public void setAutoSplit( boolean autoSplit );

    /**
     * As per the Ruby command line arg -W#.
     * @param warning
     */
    public void setWarning( int warning );

    /**
     * As per the Ruby command line arg -d. (sets $DEBUG to true)
     * @param debug
     */
    public void setDebug( boolean debug );

    /**
     * As per the Ruby command line arg -l.
     * @param processLineEnds
     */
    public void setProcessLineEnds( boolean processLineEnds );

    /**
     * Adds a library as per the Ruby command line arg -I.
     * @param libPath
     */
    public void addLibPath( String libPath );

    /**
     * Adds a 'require' file as per the Ruby command line arg -r.
     * @param reqLib
     */
    public void addReqLib( String reqLib );

    /**
     * Appends an input value with the given key to the Ruby
     * script by prepending the following code to the Ruby script:
     *  $INPUT['key'] = value;
     * 
     * @param key
     * @param value
     */
    public void inputValue( String key, Object value );

    /**
     * Invokes the script after all other values are set.
     * @return an Object of possibly returned value
     */
    public Object invoke()
        throws IOException, ComponentInstantiationException;

    /**
     * Invokes the script after all other values are set.
     * @param stdout stream where jruby output to
     * @param stderr stream where jruby errors to
     * @return an Object of possibly returned values
     */
    public Object invoke( OutputStream stdout, OutputStream stderr )
        throws IOException, ComponentInstantiationException;
}
