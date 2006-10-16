package org.codehaus.plexus.xfire.soap;

public interface Echo
{
    String ROLE = Echo.class.getName();

    public String echo( String echo );
}
