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
 * plugins/org.codehaus.emf.common/src/org/codehaus/emf/common/command/CommandStackListener.java, emf.common, org.codehaus.dev, 20030620_1105VL
 * @version 1.3 6/20/03
 */

import java.util.EventObject;


/**
 * A listener to a {@link CommandManager}.
 *
 * @author <a href="jason@maven.org">Jason van Zyl</a>
 */
public interface CommandStackListener
{
    /**
     * Called when the {@link CommandManager}'s state has changed.
     * @param event the event.
     */
    void commandStackChanged( EventObject event );
}
