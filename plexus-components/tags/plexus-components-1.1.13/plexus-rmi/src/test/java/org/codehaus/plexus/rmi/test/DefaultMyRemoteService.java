package org.codehaus.plexus.rmi.test;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultMyRemoteService
    implements MyRemoteService
{
    /**
     * @plexus.requirement
     */
    private MyService myService;

    public String partyTime()
    {
        return myService.partyTime();
    }
}
