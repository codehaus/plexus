package org.codehaus.plexus;

public interface ServiceableComponent
{
    static String ROLE = ServiceableComponent.class.getName();

    boolean simpleServiceLookup()
        throws Exception;

    boolean roleBasedServiceLookup()
        throws Exception;
}
