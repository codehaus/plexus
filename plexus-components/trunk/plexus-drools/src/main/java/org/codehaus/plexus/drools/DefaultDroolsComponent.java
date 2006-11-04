package org.codehaus.plexus.drools;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.DirectoryScanner;

import org.drools.DroolsException;
import org.drools.RuleBase;
import org.drools.TransactionalWorkingMemory;
import org.drools.WorkingMemory;
import org.drools.io.RuleSetLoader;
import org.drools.rule.RuleSet;

/**
 * This service is used to retrieve and manage rules
 * definitions, and able to fire them, e.g. using the rules to
 * modify objects and execute functions.
 *
 * @author Bart Selders
 * @version $Id$
 */
public class DefaultDroolsComponent
    extends AbstractLogEnabled
    implements DroolsComponent, Initializable
{
    /** The store that keeps a reference to all ruleBases */
    private HashMap ruleBaseStore;

    /** Directory where rules files are stored. */
    private String ruleFilesDirectory;

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    /** */
    public void initialize()
        throws InitializationException
    {
        if ( ruleFilesDirectory == null )
        {
            throw new InitializationException( "Missing configuration element: 'ruleFilesDirectory'." );
        }

        try
        {
            loadRules();
        }
        catch ( Exception e )
        {
            throw new InitializationException( "Error loading rules: ", e );
        }
    }

    /**
     * Makes a working memory available from this rulebase
     */
    public WorkingMemory getWorkingMemory( String ruleBaseIdentifier )
        throws Exception
    {
        getLogger().debug( "Create working memory for rulebase " + ruleBaseIdentifier );
        RuleBase ruleBase = getRuleBase( ruleBaseIdentifier );
        WorkingMemory workingMemory = ruleBase.createWorkingMemory();
        return workingMemory;
    }

    /**
     * Makes a transactional working memory available from this rulebase
     */
    public TransactionalWorkingMemory getTransactionalWorkingMemory( String ruleBaseIdentifier )
        throws Exception
    {
        getLogger().debug( "Create working memory for rulebase " + ruleBaseIdentifier );
        RuleBase ruleBase = getRuleBase( ruleBaseIdentifier );
        TransactionalWorkingMemory workingMemory = ruleBase.createTransactionalWorkingMemory();
        return workingMemory;
    }

    public RuleBase getRuleBase( String identifier )
    {
        return (RuleBase) getRuleBaseStore().get( identifier );
    }

    public void setRuleBase( String identifier, RuleBase ruleBase )
    {
        getRuleBaseStore().put( identifier, ruleBase );
    }

    public HashMap getRuleBaseStore()
    {
        if ( this.ruleBaseStore == null )
        {
            this.ruleBaseStore = new HashMap();
        }
        return this.ruleBaseStore;
    }

    private void loadRules() throws Exception
    {
        DirectoryScanner scanner = new DirectoryScanner();

        scanner.setBasedir( ruleFilesDirectory );

        scanner.setIncludes( new String[]{ "**/*.drl" } );

        scanner.scan();

        String[] fileNames = scanner.getIncludedFiles();

        for ( int i = 0; i < fileNames.length; i++ )
        {
            String fileName = fileNames[i];
            String identifier = null;

            // save way, expect filename including .drl extension,
            // but in case not, then add it
            if ( fileName.indexOf( ".drl" ) > 0 )
            {
                identifier = fileName.substring( 0, fileName.length() - 4 );
            }
            else
            {
                identifier = fileName;
                fileName = fileName + ".drl";
            }
            getLogger().debug( "Found rulebase filename " + fileName + ", identified as " + identifier );

            try
            {
                loadRuleBase( identifier, fileName );
            }
            catch ( DroolsException de )
            {
                getLogger().error( "RuleBase " + identifier + " could not be loaded", de );
                setRuleBase( identifier, null );
            }
            catch ( IOException ioe )
            {
                getLogger().error( "RuleBase " + identifier + " could not be loaded", ioe );
                setRuleBase( identifier, null );
            }
            catch ( Exception e )
            {
                getLogger().error( "RuleBase " + identifier + " could not be loaded", e );
                setRuleBase( identifier, null );
            }
        }
    }

    private void loadRuleBase( String identifier, String ruleFileName )
        throws DroolsException, IOException, Exception
    {
        RuleBase ruleBase = new RuleBase();
        RuleSetLoader loader = new RuleSetLoader();

        URL url = getClass().getClassLoader().getResource( ruleFileName );
        getLogger().debug( "loading rulebase: " + url );

        List ruleSets = loader.load( url );
        Iterator ruleSetIter = ruleSets.iterator();
        RuleSet eachRuleSet = null;
        while ( ruleSetIter.hasNext() )
        {
            eachRuleSet = (RuleSet) ruleSetIter.next();
            ruleBase.addRuleSet( eachRuleSet );
        }

        setRuleBase( identifier, ruleBase );
    }
}
