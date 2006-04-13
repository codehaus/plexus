package org.codehaus.plexus.cling.cli;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Map of command line options, keyed both by short name and long name.
 * 
 * @author John Casey
 */
public class InvocationTemplate
{

    private HashMap options;

    private Set optionsList;

    private boolean hasRequiredOptions;

    /**
     * Creates new InvocationTemplate
     * 
     * @param optionsList
     *            The array of Option objects to map here.
     */
    public InvocationTemplate( Set optionsList )
    {
        this.optionsList = optionsList;

        options = new HashMap();
        for ( Iterator it = optionsList.iterator(); it.hasNext(); )
        {
            Option option = (Option) it.next();
            
            if ( option.isRequired() )
            {
                hasRequiredOptions = true;
            }

            Character ch = option.getShortName();
            if ( ch != null )
            {
                options.put( ch, option );
            }

            String str = option.getLongName();
            if ( str != null )
            {
                options.put( str, option );
            }
        }
    }

    /**
     * Return whether this map contains any Option objects that are required.
     * 
     * @return true if one or more of the contained Option's are required, else
     *         false.
     */
    public boolean hasRequiredOptions()
    {
        return hasRequiredOptions;
    }

    /**
     * Return whether this map's required Option's are all satisfied, if there
     * are any.
     * 
     * @return true if the map either has no required Option's, or those
     *         required Option's are satisfied, else false.
     */
    public boolean isSatisfied()
    {
        for ( Iterator it = options.values().iterator(); it.hasNext(); )
        {
            if ( !((Option) it.next()).isSatisfied() )
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Return the number of Option instances included in this map.
     * 
     * @return the size.
     */
    public int size()
    {
        return optionsList.size();
    }

    /**
     * Return the Option object keyed by the specified single-character option
     * name.
     * 
     * @param ch
     *            The single-character short name of the option to lookup.
     * @return the corresponding Option object, or null if none match.
     */
    public Option getOption( Character ch )
    {
        return (Option) options.get( ch );
    }

    /**
     * Return the Option object keyed by the specified verbose option name.
     * 
     * @param str
     *            The verbose, long name of the option to lookup.
     * @return the corresponding Option object, or null if none match.
     */
    public Option getOption( String str )
    {
        return (Option) options.get( str );
    }

//    /**
//     * Return an array of all Option objects contained within this map. NOTE:
//     * Each option is only represented once within the array, even though it is
//     * keyed twice within the actual map.
//     * 
//     * @return the array of options.
//     */
//    public Option[] getOptions()
//    {
//        HashSet set = new HashSet();
//        set.addAll( options.values() );
//
//        return (Option[]) set.toArray( new Option[set.size()] );
//    }

    /**
     * Return the array of Option objects that are required and still unset.
     * 
     * @return The array of unsatisfied options.
     */
    public Set getUnsatisfiedOptions()
    {
        Set unsat = new HashSet();
        for ( Iterator it = options.values().iterator(); it.hasNext(); )
        {
            Option opt = (Option) it.next();

            if ( unsat.contains( opt ) )
            {
                continue;
            }
            else if ( !opt.isSatisfied() )
            {
                unsat.add( opt );
            }
        }

        return Collections.unmodifiableSet(unsat);
    }

    public void setValues( Map vals )
    {
        for ( Iterator it = vals.entrySet().iterator(); it.hasNext(); )
        {
            Map.Entry entry = (Map.Entry) it.next();
            Object key = entry.getKey();

            Option option = (Option) options.get( entry.getKey() );

            if ( option == null )
            {
                throw new IllegalStateException( "illegal invocation option detected in template" );
            }

            if ( option instanceof AbstractArgOption )
            {
                ((AbstractArgOption) option).setValue( (String) entry.getValue() );
            }
            else
            {
                ((NoArgOption) option).set();
            }
        }
    }

    public int scoreRequirements( Map vals )
    {
        int score = 0;
        for ( Iterator it = optionsList.iterator(); it.hasNext(); )
        {
            Option option = (Option) it.next();
            String optionName = option.getLongName();
            Character optionChar = option.getShortName();

            if ( option.isRequired() && !vals.containsKey( optionName ) && !vals.containsKey( optionChar ) )
            {
                score++;
            }
        }

        return score;
    }

    /**
     * Return a string describing the proper usage clause for the options
     * specified in this template.
     * 
     * @return the usage phrase for this template's options
     */
    public String getShortTemplateUsage()
    {
        StringBuffer sb = new StringBuffer( 200 );

        if ( !optionsList.isEmpty() )
        {
            sb.append( " " );
            sb.append( "<options>" );
        }

        return sb.toString();
    }

    /**
     * Return a string describing the proper usage clause for the options
     * specified in this template.
     * 
     * @return the usage phrase for this template's options
     */
    public String getTemplateUsage()
    {
        StringBuffer sb = new StringBuffer( 200 );

        sb.append( "Parameters:\n---------------\n\n" );

        for ( Iterator it = optionsList.iterator(); it.hasNext(); )
        {
            Option option = (Option) it.next();
            sb.append( option.getUsage() );
            sb.append( "\n\n" );
        }

        return sb.toString();
    }

    public boolean equals( Object obj )
    {
        if ( obj instanceof InvocationTemplate )
        {
            Set others = ((InvocationTemplate) obj).optionsList;

            if ( optionsList.isEmpty() && others.isEmpty() )
            {
                return true;
            }
            else
            {
                for ( Iterator it = optionsList.iterator(); it.hasNext(); )
                {
                    Option option = (Option) it.next();
                    if ( !others.contains( option ) )
                    {
                        return false;
                    }
                }

                return true;
            }
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        int result = 17;

        if ( options != null )
        {
            for ( Iterator it = optionsList.iterator(); it.hasNext(); )
            {
                Option option = (Option) it.next();
                result *= option.hashCode();
            }
        }

        return result;
    }

    public Map getOptionPropertyMappings()
    {
        Map mappings = new TreeMap();
        
        for ( Iterator it = optionsList.iterator(); it.hasNext(); )
        {
            Option option = (Option) it.next();
            
            Object value = null;
            if(option instanceof NoArgOption) {
                value = Boolean.valueOf(((NoArgOption)option).isSet());
            }
            else {
                value = ((AbstractArgOption)option).getValue();
            }
            
            if(value != null) {
                mappings.put(option.getObjectProperty(), value);
            }
        }
        
        return mappings;
    }

}