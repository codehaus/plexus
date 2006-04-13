package org.codehaus.plexus.cling.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author John Casey
 * @version
 */
public class Invocation
{

    private Set templates = new HashSet();

    private List arguments;

    private String description;

    private String argDescription;

    private InvocationTemplate bestMatch;

    private int bestScore = -1;

    /** Creates new Invocation */
    public Invocation( String description, String argDescription )
    {
        this.description = description;
        this.argDescription = argDescription;
    }

    public Invocation( String description )
    {
        this.description = description;
    }
    
    public Map getOptionPropertyMappings() {
        return bestMatch.getOptionPropertyMappings();
    }

    public boolean matchesTemplate( InvocationTemplate template )
    {
        return bestMatch.equals( template );
    }

    public void addInvocationTemplate( InvocationTemplate template )
    {
        templates.add( template );
    }

    public void removeInvocationTemplate( InvocationTemplate template )
    {
        templates.remove( template );
    }

    public void parseArgs( String[] args ) throws InvocationException
    {
        if(templates.isEmpty()) {
            throw new IllegalStateException("at least one invocation template is required for parseArgs");
        }
        else {
            bestMatch = null;

            LinkedList argsList = new LinkedList(Arrays.asList(args));
            ArrayList nonOptions = new ArrayList();
            Map vals = new HashMap();
            while(!argsList.isEmpty())
            {
                String arg = (String)argsList.getFirst();
                if ( arg.startsWith( "--" ) )
                {
                    processAsLongName(vals, argsList);
                }
                else if ( arg.charAt( 0 ) == '-' )
                {
                    processAsShortNameCollection(vals, argsList);
                }
                else
                {
                    nonOptions.add( arg );
                    argsList.removeFirst();
                }
            }

            this.arguments = Collections.unmodifiableList(nonOptions);

            bestScore = Integer.MAX_VALUE;
            for ( Iterator it = templates.iterator(); it.hasNext(); )
            {
                InvocationTemplate templ = (InvocationTemplate) it.next();
                int score = templ.scoreRequirements( vals );
                if ( score < bestScore )
                {
                    bestScore = score;
                    bestMatch = templ;
                }
            }

            bestMatch.setValues( vals );
        }
    }

    private void processAsShortNameCollection( Map vals, LinkedList argsList )
    {
        String arg = (String)argsList.removeFirst();
        
        char[] args = arg.toCharArray(); 
        int argLen = args.length;

        String nextArg = (String)argsList.getFirst();
        boolean usedNextArg = false;
        
        if(argLen == 1) {
            throw new IllegalArgumentException("\'-\' is not a valid argument.");
        }
        else if(argLen == 2) {
            Character argC = new Character(args[1]);
            String value = null;
            
            if(!nextArg.startsWith("-")) {
                value = nextArg;
                usedNextArg = true;
            }
            
            vals.put(argC, value);
        }
        else {
            for ( int i = 1; i < args.length; i++ )
            {
                char c = args[i];
                Character argC = new Character(c);
                
                String value = null;
                if(i+2 > args.length) {
                    if(!nextArg.startsWith("-")) {
                        value = nextArg;
                        usedNextArg = true;
                    }
                }
                
                vals.put(argC, value);
            }
        }
        
        if(usedNextArg) {
            argsList.removeFirst();
        }
    }

    private void processAsLongName( Map vals, LinkedList argsList )
    {
        String arg = (String)argsList.removeFirst();
        
        String key = arg.substring(2);
        
        int eqPos = arg.indexOf( '=' );
        
        Object value = null;
        if(eqPos > 0) {
            key = arg.substring(2, eqPos);
            
            value = arg.substring(eqPos+1);
        }
        
        if(value == null) {
            value = Boolean.TRUE;
        }
        
        vals.put( key, value );
    }

    public List getArguments()
    {
        return arguments;
    }

    public int getArgumentCount()
    {
        return arguments.size();
    }

    public Option getOption( String longName )
    {
        return bestMatch.getOption( longName );
    }

    public Option getOption( Character shortName )
    {
        return bestMatch.getOption( shortName );
    }

    public boolean isSatisfied()
    {
        return bestScore == 0;
    }

    public Set getUnsatisfiedOptions()
    {
        return bestMatch.getUnsatisfiedOptions();
    }

    public String getDescription( String zeroArg )
    {
        return _getDescription( bestMatch, zeroArg, new StringBuffer() ).toString();
    }

    public String getAllDescriptions( String zeroArg )
    {
        StringBuffer buffer = new StringBuffer();
        for ( Iterator it = templates.iterator(); it.hasNext(); )
        {
            InvocationTemplate template = (InvocationTemplate) it.next();
            buffer = _getDescription( template, zeroArg, buffer );
            buffer.append( "\n\n" );
        }

        return buffer.toString();
    }

    private StringBuffer _getDescription( InvocationTemplate template, String zeroArg, StringBuffer buffer )
    {
        if ( zeroArg != null )
        {
            if ( description != null )
            {
                buffer.append( "\n" );
                buffer.append( zeroArg );
                buffer.append( " -\t" );
                buffer.append( description );
                buffer.append( "\n\n" );
            }
        }

        return buffer;
    }

    public String getUsage( String zeroArg )
    {
        return _getUsage( bestMatch, zeroArg, new StringBuffer() ).toString();
    }

    public String getAllUsages( String zeroArg )
    {
        StringBuffer buffer = new StringBuffer();
        for ( Iterator it = templates.iterator(); it.hasNext(); )
        {
            InvocationTemplate template = (InvocationTemplate) it.next();
            buffer = _getUsage( template, zeroArg, buffer );
            if ( it.hasNext() )
            {
                buffer.append( "**************************************\n" );
            }
        }

        return buffer.toString();
    }

    private StringBuffer _getUsage( InvocationTemplate template, String zeroArg, StringBuffer buffer )
    {
        if ( zeroArg != null )
        {
            buffer = _getDescription( template, zeroArg, buffer );
            buffer.append( "Usage:\n\t" );
            buffer.append( zeroArg );
            buffer.append( template.getShortTemplateUsage() );

            if ( argDescription != null )
            {
                buffer.append( " " );
                buffer.append( argDescription );
            }

            buffer.append( "\n" );
        }

        buffer.append( template.getTemplateUsage() );

        return buffer;
    }

    public void setInvocationTemplates( Set invocationTemplates )
    {
        this.templates = invocationTemplates;
    }
}