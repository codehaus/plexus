/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.codehaus.plexus.taskqueue;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class DefaultTaskQueue
    implements TaskQueue
{
    private TaskEntryEvaluator entryEvaluator;
    private TaskViabilityEvaluator viabilityEvaluator;
    private TaskExitEvaluator exitEvaluator;

    //private LinkedQueue from doug lea


    public boolean put( Task task )
    {
        return true;
    }

    public Object take()
    {
        return null;
    }
}
