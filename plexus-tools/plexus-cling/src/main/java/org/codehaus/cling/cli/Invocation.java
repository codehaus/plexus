package org.codehaus.cling.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * @author John Casey
 * @version
 */
public class Invocation
{

    private Set templates = new HashSet();

    private String[] arguments;

    private String description;

    private String argDescription;

    private InvocationTemplate bestMatch;

    private int bestScore = -1;

    private boolean emptyTemplateEnabled = false;

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

    public void enableEmptyTemplate( boolean enabled )
    {
        if ( enabled )
        {
            templates.add( new InvocationTemplate( Collections.EMPTY_LIST ) );
        }
        else
        {
            templates.remove( new InvocationTemplate( Collections.EMPTY_LIST ) );
        }
        
        this.emptyTemplateEnabled = enabled;
    }

    public boolean emptyTemplateEnabled()
    {
        return emptyTemplateEnabled;
    }
    
    public boolean matchesTemplate( InvocationTemplate template )
    {
        return bestMatch.equals( template );
    }

    public boolean matchesEmptyTemplate()
    {
        return bestMatch.equals( new InvocationTemplate( Collections.EMPTY_LIST ) );
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

        this.arguments = (String[]) nonOptions.toArray( new String[nonOptions.size()] );

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

    private void processAsShortNameCollection( Map vals, LinkedList argsList )
    {
        String arg = (String)argsList.removeFirst();
        
        char[] args = arg.toCharArray(); 
        int argLen = args.length;

        String nextArg = (String)argsList.getFirst();
        boolean usedNextArg = false;
        
        if(argLen == 1) {
            Character argC = new Character(args[0]);
            String value = null;
            
            if(!nextArg.startsWith("-")) {
                value = nextArg;
                usedNextArg = true;
            }
            
            vals.put(argC, value);
        }
        else {
            for ( int i = 0; i < args.length; i++ )
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
        int eqPos = arg.indexOf( '=' );
        
        String key = arg;
        String value = null;
        if(eqPos > 0) {
            key = arg.substring(0, eqPos);
            
            value = arg.substring(eqPos+1);
        }
        
        vals.put( key, value );
    }

    public String[] getArguments()
    {
        return arguments;
    }

    public int getArgumentCount()
    {
        return arguments.length;
    }

    public Option getOption( String longName )
    {
        return bestMatch.getOption( longName );
    }

    public Option getOption( char shortName )
    {
        return bestMatch.getOption( shortName );
    }

    public boolean isSatisfied()
    {
        return bestScore == 0;
    }

    public Option[] getUnsatisfiedOptions()
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
}