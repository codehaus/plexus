/*
 * BSD License http://open-im.net/bsd-license.html
 * Copyright (c) 2003, OpenIM Project http://open-im.net
 * All rights reserved.
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the OpenIM project. For more
 * information on the OpenIM project, please see
 * http://open-im.net/
 */
package net.java.dev.openim.data.storage;



import java.util.List;

import net.java.dev.openim.data.Account;



/*
 * @version 1.0
 *
 * @author AlAg
 */
public interface AccountRepositoryHolder 
{
    public void setAccount( Account account );
    public Account getAccount( String username );
    public List getAccountList( String searchPattern );
    public Account removeAccount( String username );
}


