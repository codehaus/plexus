/* Created on Sep 13, 2004 */
package org.codehaus.plexus.cling;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import ognl.Ognl;
import ognl.OgnlException;

import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.DuplicateRealmException;
import org.codehaus.marmalade.el.ognl.OgnlExpressionEvaluator;
import org.codehaus.marmalade.el.ognl.PathSafeOgnlExpressionEvaluator;
import org.codehaus.marmalade.metamodel.MarmaladeTaglibResolver;
import org.codehaus.marmalade.metamodel.ModelBuilderException;
import org.codehaus.marmalade.metamodel.ScriptBuilder;
import org.codehaus.marmalade.model.MarmaladeScript;
import org.codehaus.marmalade.model.MarmaladeTag;
import org.codehaus.marmalade.parsing.DefaultParsingContext;
import org.codehaus.marmalade.parsing.MarmaladeParsetimeException;
import org.codehaus.marmalade.parsing.MarmaladeParsingContext;
import org.codehaus.marmalade.parsing.ScriptParser;
import org.codehaus.marmalade.runtime.DefaultContext;
import org.codehaus.marmalade.runtime.MarmaladeExecutionContext;
import org.codehaus.marmalade.runtime.MarmaladeExecutionException;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.cling.cli.Invocation;
import org.codehaus.plexus.cling.cli.InvocationException;
import org.codehaus.plexus.cling.configuration.CLIngConfiguration;
import org.codehaus.plexus.cling.configuration.DefaultCLIngConfiguration;
import org.codehaus.plexus.cling.model.AppModel;
import org.codehaus.plexus.cling.model.Classpath;
import org.codehaus.plexus.cling.model.ClasspathEntry;
import org.codehaus.plexus.cling.model.ResolvedClasspathEntry;
import org.codehaus.plexus.cling.tags.app.AppTag;
import org.codehaus.plexus.cling.tags.app.AppTagLibrary;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.embed.Embedder;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 * @author jdcasey
 */
public class Launcher extends AbstractLogEnabled
    implements Contextualizable
{

    public static final String ROLE = Launcher.class.getName();

    private ClassRealm containerRealm;

    private ArtifactResolver resolver;

    public int execute( String[] args ) throws CLIngLaunchException
    {
        Logger logger = getLogger();
        
        // Setup the default cling configuration
        CLIngConfiguration config = null;
        try
        {
            config = new DefaultCLIngConfiguration();
        }
        catch ( MalformedURLException e )
        {
            throw new CLIngLaunchException( "Cannot build default CLIng local artifact repository path",
                CLIngErrors.ERROR_BUILDING_DEFAULT_LOCAL_REPO_PATH, e );
        }

        // Execute the cling lifecycle:
        // [[1]] Setup the CLIng execution environment. The CLIng basedir is
        //       assumed to be <user.dir> if the JVM property <app.basedir>
        //       is not specified.
        //
        logger.debug( "Setting up CLIng environment." );
        
        String basedir = System.getProperty( CLIngConstants.APPDIR_SYSPROP, System.getProperty( "user.dir" ) );
        File basedirFile = new File(basedir);
        
        try
        {
            basedirFile = basedirFile.getCanonicalFile();
        }
        catch ( IOException e )
        {
            throw new CLIngLaunchException("Cannot canonicalize application basedir", CLIngErrors.ERROR_CANONICALIZING_APPDIR, e);
        }

        Properties sysProps = System.getProperties();
        sysProps.setProperty("user.dir", basedirFile.getPath());
        sysProps.setProperty(CLIngConstants.APPDIR_SYSPROP, basedirFile.getPath());
        
        System.setProperties(sysProps);
        
        logger.debug( "Done." );

        // [[2]] Parse the <<<app.xml>>> file in <app.basedir> of the CLIng
        //       execution directory.
        //
        logger.debug( "Parsing application descriptor." );
        AppModel model = parseAppXml( basedir, config );
        logger.debug( "Done." );

        // [[3]] Download the runtime dependencies of the application, as they
        //       are encountered in the <<<app.xml>>> file. If any of these fails,
        //       fail the entire application.
        // [[4]] Setup the application class-realm with the downloaded
        //       dependencies and any specified local classpath locations.
        //
        logger.debug( "Building class realm and setting up context classloader." );

        ClassRealm appRealm = resolveClasspath( model.getClasspath(), config );
        
        Thread.currentThread().setContextClassLoader(appRealm.getClassLoader());

        logger.debug( "Done." );

        // [[5]] Setup the System environment by merging the <env> element's
        //       body content with pre-existing System properties.
        //
        logger.debug( "Setting up environment properties." );
        System.setProperties( model.getEnvironment() );
        logger.debug( "Done." );

        // [[6]] Instantiate the main-class.
        //
        logger.debug( "Instantiating main object." );
        Object main = instantiateMain( model, appRealm );
        logger.debug( "Done" );

        // [[7]] Parse the command-line arguments, and validate each. Set each
        //       validated argument as a property on the main-class.
        //
        logger.debug( "Parsing command-line arguments, and configuring main-class instance." );
        Invocation invocation = parseCommandLine( args, model, main );
        configureMain(main, invocation);
        logger.debug( "Done." );

        // [[8]] Reflectively lookup the specified execute method. Verify that
        //       it returns an int type. If not, fail the entire application.
        //
        logger.debug( "Finding main execution method." );
        Method execute = findMethod( main, model, invocation );
        logger.debug( "Done." );

        // [[9]] Invoke the execute method and save the result to a local
        //       variable.
        //
        logger.debug( "Invoking main execution method." );
        int result = invokeMethod( execute, main, invocation );
        logger.debug( "Done." );

        // See the main() method for [9].
        return result;
    }

    private void configureMain( Object main, Invocation invocation ) 
    throws CLIngLaunchException
    {
        Map mappings = invocation.getOptionPropertyMappings();
        
        for ( Iterator it = mappings.entrySet().iterator(); it.hasNext(); )
        {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String)entry.getKey();
            Object value = entry.getValue();
            
            try
            {
                Ognl.setValue(key, main, value);
            }
            catch ( OgnlException e )
            {
                throw new CLIngLaunchException("Cannot configure main-class instance with one or more command-line options", CLIngErrors.ERROR_SETTING_OBJECT_PROPERTY, e);
            }
        }
    }

    private ClassRealm resolveClasspath( Classpath classpath, CLIngConfiguration config ) throws CLIngLaunchException
    {
        List entries = classpath.getEntries();

        ClassRealm appRealm = null;
        try
        {
            appRealm = containerRealm.createChildRealm( "application" );
        }
        catch ( DuplicateRealmException e )
        {
            throw new CLIngLaunchException( "Cannot create duplicate ClassRealm",
                CLIngErrors.ERROR_CREATING_DUPLICATE_CLASSREALM, e );
        }

        URL localRepoUrl = null;
        try
        {
            localRepoUrl = new URL( config.getLocalRepository().getUrl() );
        }
        catch ( MalformedURLException e )
        {
            throw new CLIngLaunchException( "Cannot construct local artifact repository URL",
                CLIngErrors.ERROR_CONSTRUCTING_LOCAL_REPO_URL, e );
        }

        File localRepoDir = new File( localRepoUrl.getPath() );
        if ( !localRepoDir.exists() )
        {
            localRepoDir.mkdirs();
        }

        for ( Iterator it = entries.iterator(); it.hasNext(); )
        {
            ClasspathEntry entry = (ClasspathEntry) it.next();

            // If this type of entry needs resolution, resolve it first.
            if ( entry instanceof ResolvedClasspathEntry )
            {
                try
                {
                    ((ResolvedClasspathEntry) entry).resolve( resolver, config );
                }
                catch ( MalformedURLException e )
                {
                    throw new CLIngLaunchException( "Cannot resolve classpath entry",
                        CLIngErrors.ERROR_RESOLVING_CLASSPATH_ENTRY, e );
                }
                catch ( ArtifactResolutionException e )
                {
                    throw new CLIngLaunchException( "Cannot resolve classpath entry",
                        CLIngErrors.ERROR_RESOLVING_CLASSPATH_ENTRY, e );
                }
            }

            appRealm.addConstituent( entry.getURL() );
        }

        return appRealm;
    }

    private int invokeMethod( Method execute, Object main, Invocation invocation ) throws CLIngLaunchException
    {
        int result = 0;

        try
        {
            Object[] params = null;

            if ( execute.getParameterTypes().length == 1 )
            {
                params = new Object[] { invocation.getArguments() };
            }
            else
            {
                params = new Object[0];
            }

            Integer returnValue = (Integer) execute.invoke( main, params );
            result = returnValue.intValue();
        }
        catch ( IllegalArgumentException e )
        {
            throw new CLIngLaunchException( "invalid parameter in main-method",
                CLIngErrors.ERROR_MAIN_METHOD_HAD_ILLEGAL_ARGUMENT, e );
        }
        catch ( IllegalAccessException e )
        {
            throw new CLIngLaunchException( "main-method cannot be accessed for invocation",
                CLIngErrors.ERROR_MAIN_METHOD_NOT_ACCESSIBLE_FOR_INVOCATION, e );
        }
        catch ( InvocationTargetException e )
        {
            throw new CLIngLaunchException( "main-method failed to execute",
                CLIngErrors.ERROR_MAIN_METHOD_FAILED_TO_EXECUTE, e );
        }

        return result;
    }

    private Method findMethod( Object main, AppModel model, Invocation invocation ) throws CLIngLaunchException
    {
        Method execute = null;
        try
        {
            String methodName = model.getMain().getMainMethod();

            Method[] methods = main.getClass().getMethods();
            for ( int i = 0; i < methods.length; i++ )
            {
                Method method = methods[i];

                if ( method.getName().equals( methodName ) && method.getReturnType().equals( Integer.TYPE ) )
                {
                    Class[] paramTypes = method.getParameterTypes();
                    if ( paramTypes.length == 1 && paramTypes[0].isAssignableFrom( List.class ) )
                    {

                        execute = method;
                        break;
                    }
                    else if(paramTypes.length == 0) {
                        execute = method;
                        break;
                    }
                }
            }

            if ( execute == null )
            {
                throw new CLIngLaunchException( "Cannot find suitable main-class launch method of specified name",
                    CLIngErrors.ERROR_FINDING_MAIN_METHOD );
            }

        }
        catch ( SecurityException e )
        {
            throw new CLIngLaunchException( "Cannot gain access to main-method to execute",
                CLIngErrors.ERROR_ACCESSING_MAIN_METHOD, e );
        }

        return execute;
    }

    private Invocation parseCommandLine( String[] args, AppModel model, Object main ) throws CLIngLaunchException
    {
        Invocation cliInvocation = new Invocation( model.getApplicationDescription(), model.getArgumentDescription() );
        Set invocationTemplates = model.getLegalUsage().getInvocationTemplates();
        cliInvocation.setInvocationTemplates( invocationTemplates );
        try
        {
            cliInvocation.parseArgs( args );
        }
        catch ( InvocationException e )
        {
            throw new CLIngLaunchException( "Cannot parse command-line arguments", CLIngErrors.ERROR_PARSING_ARGS, e );
        }

        if ( !cliInvocation.isSatisfied() )
        {
            String appName = System.getProperty( "$0" );
            if ( appName == null || appName.length() < 1 )
            {
                appName = main.getClass().getName();
            }

            String usage = cliInvocation.getAllUsages( appName );
            
            Logger logger = getLogger();
            logger.warn( usage );

            throw new CLIngLaunchException( "Required options not satisfied",
                CLIngErrors.ERROR_REQUIRED_OPTIONS_NOT_SATISFIED );
        }

        return cliInvocation;
    }

    private Object instantiateMain( AppModel model, ClassRealm realm ) throws CLIngLaunchException
    {
        Class mainClass = null;
        Logger logger = getLogger();

        try
        {
            logger.debug( "Attempting to instantiate: " + model.getMain().getMainClass() );
            mainClass = realm.loadClass( model.getMain().getMainClass() );
        }
        catch ( Throwable t )
        {
            throw new CLIngLaunchException( "Cannot find main-class", CLIngErrors.ERROR_MAIN_CLASS_NOT_FOUND, t );
        }

        Object main = null;
        try
        {
            main = mainClass.newInstance();
        }
        catch ( Throwable t )
        {
            throw new CLIngLaunchException( "Cannot instantiate main-class",
                CLIngErrors.ERROR_MAIN_CLASS_NOT_INSTANTIABLE, t );
        }

        return main;
    }

    private AppModel parseAppXml( String basedir, CLIngConfiguration config ) throws CLIngLaunchException
    {
        MarmaladeParsingContext parsingContext = initParsingContext( basedir );

        MarmaladeScript script = buildScript( parsingContext );

        AppModel model = executeScript( script, config );

        return model;
    }

    private AppModel executeScript( MarmaladeScript script, CLIngConfiguration config ) throws CLIngLaunchException
    {
        MarmaladeTag root = script.getRoot();

        if ( !(root instanceof AppTag) )
        {
            throw new CLIngLaunchException( "Invalid application descriptor (must begin with application tag)",
                CLIngErrors.ERROR_VALIDATING_APPXML );
        }

        MarmaladeExecutionContext context = new DefaultContext();
        context.setVariable( CLIngConstants.CLING_CONFIG_CONTEXT_KEY, config );

        try
        {
            script.execute( context );
        }
        catch ( MarmaladeExecutionException e )
        {
            throw new CLIngLaunchException( "Cannot build application model from descriptor",
                CLIngErrors.ERROR_BUILDING_APPMODEL_FROM_APPXML, e );
        }

        AppModel model = ((AppTag) root).getAppModel();

        return model;
    }

    private MarmaladeScript buildScript( MarmaladeParsingContext parsingContext ) throws CLIngLaunchException
    {
        ScriptParser parser = new ScriptParser();

        ScriptBuilder builder = null;
        try
        {
            builder = parser.parse( parsingContext );
        }
        catch ( MarmaladeParsetimeException e )
        {
            throw new CLIngLaunchException( "Cannot parse application descriptor", CLIngErrors.ERROR_PARSING_APPXML, e );
        }

        MarmaladeScript script = null;
        try
        {
            script = builder.build();
        }
        catch ( ModelBuilderException e )
        {
            throw new CLIngLaunchException( "Cannot build application descriptor script from metamodel",
                CLIngErrors.ERROR_BUILDING_APPXML, e );
        }

        return script;
    }

    private MarmaladeParsingContext initParsingContext( String basedir ) throws CLIngLaunchException
    {
        File base = new File( basedir );

        String appXmlName = System.getProperty( CLIngConstants.APPXML_SYSPROP, CLIngConstants.DEFAULT_APPXML_VALUE );
        File appXml = new File( base, appXmlName );

        BufferedReader reader;
        try
        {
            reader = new BufferedReader( new FileReader( appXml ) );
        }
        catch ( FileNotFoundException e )
        {
            throw new CLIngLaunchException( "Cannot open application descriptor", CLIngErrors.ERROR_OPENING_APPXML, e );
        }

        MarmaladeParsingContext parsingContext = new DefaultParsingContext();
        parsingContext.setDefaultExpressionEvaluator( new PathSafeOgnlExpressionEvaluator() );
        parsingContext.setInput( reader );
        parsingContext.setDefaultTagLibrary( new AppTagLibrary() );

        try
        {
            parsingContext.setInputLocation( appXml.getCanonicalPath() );
        }
        catch ( IOException e )
        {
            throw new CLIngLaunchException( "Cannot retrieve application descriptor's canonical path",
                CLIngErrors.ERROR_RETRIEVING_APPXML_CANONICAL_PATH, e );
        }

        return parsingContext;
    }

    public static void main( String[] args, ClassWorld world )
    {
        String resultProp = System.getProperty( CLIngConstants.APP_RESULT_SYSPROP );

        Embedder embedder = new Embedder();
        try
        {
            embedder.start( world );
        }
        catch ( Exception e )
        {
            System.out.println( "Cannot start embedded plexus container" );
            e.printStackTrace( System.err );

            System.exit( CLIngErrors.ERROR_STARTING_EMBEDDER );
        }

        Launcher launcher = null;
        try
        {
            launcher = (Launcher) embedder.lookup( ROLE );
        }
        catch ( ComponentLookupException e )
        {
            System.out.println( "Cannot lookup container-managed launcher" );
            e.printStackTrace( System.err );

            System.exit( CLIngErrors.ERROR_LOOKING_UP_LAUNCHER );
        }

        int result;
        try
        {
            result = launcher.execute( args );
        }
        catch ( CLIngLaunchException e )
        {
            System.out.println( e.getLocalizedMessage() );
            e.printStackTrace( System.err );

            result = e.getErrorCode();
        }
        catch ( Throwable t )
        {
            System.out.println( t.getLocalizedMessage() );
            t.printStackTrace( System.err );

            if ( t instanceof RuntimeException )
            {
                throw (RuntimeException) t;
            }
            else
            {
                result = CLIngErrors.UNHANDLED_EXCEPTION;
            }
        }

        if ( resultProp == null || resultProp.length() < 1 )
        {
            // [[10]] Call System.exit(x) where <x> is the result from [9].
            //
            System.exit( result );
        }
        else
        {
            System.setProperty( resultProp, Integer.toString( result ) );
        }
    }

    public void contextualize( Context context ) throws Exception
    {
        this.containerRealm = (ClassRealm) context.get( PlexusConstants.PLEXUS_CORE_REALM );
    }

}