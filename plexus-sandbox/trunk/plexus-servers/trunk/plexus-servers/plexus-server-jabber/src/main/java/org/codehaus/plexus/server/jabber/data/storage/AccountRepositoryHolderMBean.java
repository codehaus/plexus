/*
 * BSD License http://open-im.org/bsd-license.html
 * Copyright (c) 2003, OpenIM Project http://open-im.org
 * All rights reserved.
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.codehaus.plexus.server.jabber.data.storage;

import java.util.List;


/*
 * @phoenix:mx-topic name="AccountRepositoryHolder"
 */
public interface AccountRepositoryHolderMBean
{
    /*
     * @phoenix:@phoenix:mx-attribute
     * @phoenix:mx-description Create a new account (format name/password)
     * @phoenix:mx-isWriteable true
     */
    public void setAccount( String accountStr );

    /*
    * @phoenix:@phoenix:mx-attribute
    * @phoenix:mx-isWriteable false
    */
    public List getAccountList();

}
