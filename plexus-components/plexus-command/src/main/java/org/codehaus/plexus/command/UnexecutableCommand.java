package org.codehaus.plexus.command;

/**
 * <copyright>
 *
 * Copyright (c) 2002 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.codehaus.org/legal/cpl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 *
 * </copyright>
 *
 * plugins/org.codehaus.emf.common/src/org/codehaus/emf/common/command/UnexecutableCommand.java, emf.common, org.codehaus.dev, 20030620_1105VL
 * @version 1.5 6/20/03
 */

/**
 * A singleton {@link UnexecutableCommand#INSTANCE} that cannot execute.
 *
 * @author <a href="jason@maven.org">Jason van Zyl</a>
 */
public class UnexecutableCommand extends AbstractCommand
{
    /**
     * The one instance of this object.
     */
    public static final UnexecutableCommand INSTANCE = new UnexecutableCommand();

    /**
     * Only one private instance is created.
     */
    private UnexecutableCommand()
    {
        super
            ( "_UI_UnexecutableCommand_label",
              "_UI_UnexecutableCommand_description" );
    }

    /**
     * Returns <code>false</code>.
     * @return <code>false</code>.
     */
    public boolean canExecute()
    {
        return false;
    }

    /**
     * Throws an exception if it should ever be called.
     * @exception UnsupportedOperationException always.
     */
    public void execute()
    {
        throw new UnsupportedOperationException( "Execute not supported." );
    }

    /**
     * Returns <code>false</code>.
     * @return <code>false</code>.
     */
    public boolean canUndo()
    {
        return false;
    }

    /**
     * Throws an exception if it should ever be called.
     * @exception UnsupportedOperationException always.
     */
    public void redo()
    {
        throw new UnsupportedOperationException( "Redo not supported." );
    }
}
