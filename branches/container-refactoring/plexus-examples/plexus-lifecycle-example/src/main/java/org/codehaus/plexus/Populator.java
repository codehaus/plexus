package org.codehaus.plexus;

/**
 * @author Jason van Zyl
 */
public interface Populator
{
    String ROLE = Populator.class.getName();

    boolean isConfigured();

    boolean isMonitored();

    boolean isExecuted();
}
