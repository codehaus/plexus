package org.codehaus.plexus.security.authorisation.acl;

import org.codehaus.plexus.security.credentials.acl.ACL;




/**
  * 
  * <p>Created on 15/08/2003</p>
  *
  * @author <a href="mailto:bertvanbrakel@sourceforge.net">Bert van Brakel</a>
  * @revision $Revision$
  */
public interface ACLRestriction
{	
	public boolean pass(ACL acl);
}
