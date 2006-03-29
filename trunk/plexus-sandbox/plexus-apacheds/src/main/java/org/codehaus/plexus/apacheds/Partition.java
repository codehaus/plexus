package org.codehaus.plexus.apacheds;

import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import java.util.Set;
import java.util.HashSet;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class Partition
{
    private String name;

    private String suffix;

    private Set indexedAttributes;

    private Attributes contextAttributes;

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getSuffix()
    {
        return suffix;
    }

    public void setSuffix( String suffix )
    {
        this.suffix = suffix;
    }

    public Set getIndexedAttributes()
    {
        if ( indexedAttributes == null )
        {
            indexedAttributes = new HashSet();
        }

        return indexedAttributes;
    }

    public void setIndexedAttributes( Set indexedAttributes )
    {
        this.indexedAttributes = indexedAttributes;
    }

    public Attributes getContextAttributes()
    {
        if ( contextAttributes == null )
        {
            contextAttributes = new BasicAttributes();
        }

        return contextAttributes;
    }

    public void setContextAttributes( Attributes contextAttributes )
    {
        this.contextAttributes = contextAttributes;
    }
}
