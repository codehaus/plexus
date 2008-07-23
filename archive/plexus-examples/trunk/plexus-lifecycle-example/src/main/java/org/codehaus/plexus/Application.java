package org.codehaus.plexus;

/**
 * @author Jason van Zyl
 */
public interface Application
{
    String ROLE = Application.class.getName();

    void doWork();

    boolean isWorkDone();

    Populator getPopulator();
}
