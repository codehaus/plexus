package org.codehaus.plexus.rmi.test;

/**
 * @plexus.component
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultMyService
    implements MyService
{
    public String partyTime()
    {
        return "YES!!";
    }
}
