package org.codehaus.plexus.jmx.mgmt;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PartyMgmt
    implements PartyMgmtMBean
{
    private boolean party;

    public boolean isParty()
    {
        return party;
    }

    public void setParty( boolean party )
    {
        this.party = party;
    }
}
