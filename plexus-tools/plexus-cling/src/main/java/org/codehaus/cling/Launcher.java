/* Created on Sep 13, 2004 */
package org.codehaus.cling;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import ognl.Ognl;
import ognl.OgnlException;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.DuplicateRealmException;
import org.codehaus.cling.cli.Invocation;
import org.codehaus.cling.cli.InvocationException;
import org.codehaus.cling.model.AppModel;
import org.codehaus.cling.model.Classpath;
import org.codehaus.cling.tags.AppTag;
import org.codehaus.marmalade.el.ognl.OgnlExpressionEvaluator;
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
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.embed.Embedder;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 * @author jdcasey
 */
public class Launcher implements Contextualizable
{

    public static final String ROLE = Launcher.class.getName();
    
    private ClassRealm containerRealm;

    public int execute( String[] args ) throws CLIngLaunchException
    {
        // Execute the cling lifecycle:
        // [[1]] Setup the CLIng execution environment. The CLIng basedir is
        //       assumed to be <user.dir> if the JVM property <app.basedir>
        //       is not specified.
        //
        String basedir = System.getProperty( CLIngConstants.APPDIR_SYSPROP, System.getProperty( "user.dir" ) );

        // [[2]] Parse the <<<app.xml>>> file in <app.basedir> of the CLIng
        //       execution directory.
        //
        AppModel model = parseAppXml(basedir);
        
        // [[3]] Setup the application class-realm with the downloaded
        //       dependencies and any specified local classpath locations.
        //
        ClassRealm appRealm = buildRealm(model);
        
        // [[4]] Setup the System environment by merging the <env> element's
        //       body content with pre-existing System properties.
        //
        System.setProperties(model.getEnvironment());
        
        // [[5]] Instantiate the main-class.
        //
        Object main = instantiateMain(model, appRealm);
        
        // [[6]] Parse the command-line arguments, and validate each. Set each
        //       validated argument as a property on the main-class.
        //
        parseCommandLine(args, model, main);
        
        // [[7]] Reflectively lookup the specified execute method. Verify that
        //       it returns an int type. If not, fail the entire application.
        //
        Method execute = findMethod(main, model);
        
        // [[8]] Invoke the execute method and save the result to a local
        //       variable.
        //
        int result = invokeMethod(execute, main);
        
        // See the main() method for [9].
        return result;
    }

    private int invokeMethod( Method execute, Object main ) 
    throws CLIngLaunchException
    {
        int result = 0;
        
        try
        {
            Integer returnValue = (Integer)execute.invoke(main, new Object[0]);
            result = returnValue.intValue();
        }
        catch ( IllegalArgumentException e )
        {
            throw new CLIngLaunchException("main-method should not use arguments, but found one anyway", CLIngErrors.ERROR_MAIN_METHOD_HAD_ILLEGAL_ARGUMENT, e);
        }
        catch ( IllegalAccessException e )
        {
            throw new CLIngLaunchException("main-method cannot be accessed for invocation", CLIngErrors.ERROR_MAIN_METHOD_NOT_ACCESSIBLE_FOR_INVOCATION, e);
        }
        catch ( InvocationTargetException e )
        {
            throw new CLIngLaunchException("main-method failed to execute", CLIngErrors.ERROR_MAIN_METHOD_FAILED_TO_EXECUTE, e);
        }
        
        return result;
    }

    private Method findMethod( Object main, AppModel model ) 
    throws CLIngLaunchException
    {
        Method execute = null;
        try
        {
            String methodName = model.getMain().getMainMethod();
            
            Method[] methods = main.getClass().getMethods();
            for ( int i = 0; i < methods.length; i++ )
            {
                Method method = methods[i];
                
                if(method.getName().equals(methodName) && method.getReturnType().equals(Integer.TYPE)) {
                    execute = method;
                    break;
                }
            }
        }
        catch ( SecurityException e )
        {
            throw new CLIngLaunchException("Cannot gain access to main-method to execute", CLIngErrors.ERROR_ACCESSING_MAIN_METHOD, e);
        }
        
        return execute;
    }

    private void parseCommandLine( String[] args, AppModel model, Object main ) throws CLIngLaunchException
    {
        Invocation cliInvocation = new Invocation(model.getApplicationDescription(), model.getArgumentDescription());
        try
        {
            cliInvocation.parseArgs(args);
        }
        catch ( InvocationException e )
        {
            throw new CLIngLaunchException("Cannot parse command-line arguments", CLIngErrors.ERROR_PARSING_ARGS, e);
        }
        
        if(cliInvocation.isSatisfied()) {
            // pull options and set main instance properties accordingly
            Map properties = cliInvocation.getOptionPropertyMappings();
            
            for ( Iterator it = properties.entrySet().iterator(); it.hasNext(); )
            {
                Map.Entry entry = (Map.Entry) it.next();
                String key = (String)entry.getKey();
                Object value = entry.getValue();
                
                try
                {
                    Ognl.setValue(key, value, main);
                }
                catch ( OgnlException e )
                {
                    throw new CLIngLaunchException("Cannot set object property from option value", CLIngErrors.ERROR_SETTING_OBJECT_PROPERTY, e);
                }
            }
        }
        else {
            String appName = System.getProperty("$0");
            if(appName == null || appName.length() < 1) {
                appName = main.getClass().getName();
            }
            
            String usage = cliInvocation.getAllUsages(appName);
            System.out.println(usage);
            
            throw new CLIngLaunchException("Required options not satisfied", CLIngErrors.ERROR_REQUIRED_OPTIONS_NOT_SATISFIED);
        }
    }

    private Object instantiateMain( AppModel model, ClassRealm realm ) 
    throws CLIngLaunchException
    {
        Class mainClass = null;
        
        try
        {
            mainClass = realm.loadClass(model.getMain().getMainClass());
        }
        catch ( ClassNotFoundException e )
        {
            throw new CLIngLaunchException("Cannot find main-class", CLIngErrors.ERROR_MAIN_CLASS_NOT_FOUND, e);
        }
        
        Object main = null;
        try
        {
            main = mainClass.newInstance();
        }
        catch ( InstantiationException e )
        {
            throw new CLIngLaunchException("Cannot instantiate main-class", CLIngErrors.ERROR_MAIN_CLASS_NOT_INSTANTIABLE, e);
        }
        catch ( IllegalAccessException e )
        {
            throw new CLIngLaunchException("Cannot instantiate main-class", CLIngErrors.ERROR_MAIN_CLASS_NOT_INSTANTIABLE, e);
        }
        
        return main;
    }

    private ClassRealm buildRealm( AppModel model ) throws CLIngLaunchException
    {
        ClassRealm appRealm = null;
        try
        {
            appRealm = containerRealm.createChildRealm("application");
        }
        catch ( DuplicateRealmException e )
        {
            throw new CLIngLaunchException("Cannot create duplicate ClassRealm", CLIngErrors.ERROR_CREATING_DUPLICATE_CLASSREALM, e);
        }
        
        Classpath classpath = model.getClasspath();
        for ( Iterator it = classpath.getEntries().iterator(); it.hasNext(); )
        {
            URL entry = (URL) it.next();
            appRealm.addConstituent(entry);
        }
        
        return appRealm;
    }

    private AppModel parseAppXml( String basedir ) 
    throws CLIngLaunchException
    {
        MarmaladeParsingContext parsingContext = initParsingContext(basedir);
        
        MarmaladeScript script = buildScript(parsingContext);
        
        AppModel model = executeScript(script);
        
        return model;
    }

    private AppModel executeScript( MarmaladeScript script ) 
    throws CLIngLaunchException
    {
        MarmaladeTag root = script.getRoot();
        
        if(!(root instanceof AppTag)) {
            throw new CLIngLaunchException("Invalid application descriptor (must begin with application tag)", CLIngErrors.ERROR_VALIDATING_APPXML);
        }
        
        MarmaladeExecutionContext context = new DefaultContext();
        // TODO: setup the repo context variables...
        
        try
        {
            script.execute(context);
        }
        catch ( MarmaladeExecutionException e )
        {
            throw new CLIngLaunchException("Cannot build application model from descriptor", CLIngErrors.ERROR_BUILDING_APPMODEL_FROM_APPXML, e);
        }
        
        AppModel model = ((AppTag)root).getAppModel();
        
        return model;
    }

    private MarmaladeScript buildScript( MarmaladeParsingContext parsingContext ) 
    throws CLIngLaunchException
    {
        ScriptParser parser = new ScriptParser();
        
        ScriptBuilder builder = null;
        try
        {
            builder = parser.parse(parsingContext);
        }
        catch ( MarmaladeParsetimeException e )
        {
            throw new CLIngLaunchException("Cannot parse application descriptor", CLIngErrors.ERROR_PARSING_APPXML, e);
        }
        
        MarmaladeScript script = null;
        try
        {
            script = builder.build();
        }
        catch ( ModelBuilderException e )
        {
            throw new CLIngLaunchException("Cannot build application descriptor script from metamodel", CLIngErrors.ERROR_BUILDING_APPXML, e);
        }
        
        return script;
    }

    private MarmaladeParsingContext initParsingContext( String basedir ) 
    throws CLIngLaunchException
    {
        File base = new File(basedir);
        
        String appXmlName = System.getProperty(CLIngConstants.APPXML_SYSPROP, CLIngConstants.DEFAULT_APPXML_VALUE);
        File appXml = new File(base, appXmlName);
        
        BufferedReader reader;
        try
        {
            reader = new BufferedReader(new FileReader(appXml));
        }
        catch ( FileNotFoundException e )
        {
            throw new CLIngLaunchException("Cannot open application descriptor", CLIngErrors.ERROR_OPENING_APPXML, e);
        }
        
        MarmaladeParsingContext parsingContext = new DefaultParsingContext();
        parsingContext.setDefaultExpressionEvaluator(new OgnlExpressionEvaluator());
        parsingContext.setInput(reader);
        
        try
        {
            parsingContext.setInputLocation(appXml.getCanonicalPath());
        }
        catch ( IOException e )
        {
            throw new CLIngLaunchException("Cannot retrieve application descriptor's canonical path", CLIngErrors.ERROR_RETRIEVING_APPXML_CANONICAL_PATH, e);
        }
        
        return parsingContext;
    }

    public static void main( String[] args, ClassWorld world )
    {
        String basedir = System.getProperty( CLIngConstants.APPDIR_SYSPROP, System.getProperty( "user.dir" ) );

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
            System.out.println(e.getLocalizedMessage());
            e.printStackTrace(System.err);
            
            result = e.getErrorCode();
        }
        
        // [[9]] Call System.exit(x) where <x> is the result from [8].
        //
        System.exit( result );
    }

    public void contextualize( Context context ) throws Exception
    {
        this.containerRealm = (ClassRealm) context.get(PlexusConstants.PLEXUS_CORE_REALM);
    }
    
}