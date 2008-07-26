package org.codehaus.plexus.jmx.mgmt;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface PartyMgmtMBean
{
    void setParty( boolean on );

    boolean isParty();
}
