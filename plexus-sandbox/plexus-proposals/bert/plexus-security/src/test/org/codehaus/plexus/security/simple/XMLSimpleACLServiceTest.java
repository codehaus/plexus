package org.codehaus.plexus.security.simple;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.security.PlexusSession;
import org.codehaus.plexus.security.SecurityService;
import org.codehaus.plexus.security.authentication.pap.PAPToken;

/**
  * 
  * <p>Created on 22/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class XMLSimpleACLServiceTest extends PlexusTestCase
{

    /**
     * @param arg0
     */
    public XMLSimpleACLServiceTest(String arg0)
    {
        super(arg0);
    }

    public void test() throws Exception
    {
        SimpleACLService acl = (SimpleACLService) lookup(SimpleACLService.ROLE);
        //permissions
        assertTrue(acl.hasPermission("perm1"));
        assertTrue(acl.hasPermission("perm2"));
        assertTrue(acl.hasPermission("perm3"));
        assertTrue(acl.hasPermission("admin:perm1"));
        assertTrue(acl.hasPermission("admin:perm2"));
        assertTrue(acl.hasPermission("admin:perm3"));

        //roles
        assertTrue(acl.hasRole("role1"));
        assertTrue(acl.hasRole("role2"));
        assertTrue(acl.hasRole("role3"));

        //roles contain permissions
        Role role1 = acl.getRole("role1");
        assertTrue(role1.hasPermission("perm1"));
        assertFalse(role1.hasPermission("perm2"));
        assertTrue(role1.hasPermission("perm3"));

        Role role2 = acl.getRole("role2");
        assertTrue(role2.hasPermission("admin:perm1"));
        assertTrue(role2.hasPermission("admin:perm2"));
        assertTrue(role2.hasPermission("admin:perm3"));

        assertFalse(role2.hasPermission("perm1"));
        assertFalse(role2.hasPermission("perm2"));
        assertFalse(role2.hasPermission("perm3"));

        Role role3 = acl.getRole("role3");
        assertTrue(role3.hasPermission("perm1"));
        assertTrue(role3.hasPermission("perm2"));
        assertTrue(role3.hasPermission("admin:perm1"));
        assertFalse(role3.hasPermission("perm3"));
        assertFalse(role3.hasPermission("admin:perm2"));
        assertFalse(role3.hasPermission("admin:perm3"));

		release( acl );

        SecurityService security = (SecurityService) lookup(SecurityService.ROLE);
        //tom
        PlexusSession sessBob = security.authenticate(newToken("tom", "tomtheman"));
        SimpleAgent agentBob = (SimpleAgent) sessBob.getAgent();
		assertEquals("Incorrect agent id","tom", agentBob.getId());
		assertEquals("Incorrect agent humnaName", "tom", agentBob.getHumanName());      
        assertNotNull("Expected agent 'tom' to have a non-null ACL", agentBob.getACL() );
        assertTrue(agentBob.getACL().hasPermission("perm1"));
        assertTrue(agentBob.getACL().hasPermission("perm3"));

        //dick
        PlexusSession sessDick = security.authenticate(newToken("dick", "dicks123password"));
        SimpleAgent agentDick = (SimpleAgent) sessDick.getAgent();
		assertNotNull("Expected agent 'dick' to have a non-null ACL", agentDick.getACL() );
        assertTrue(agentDick.getACL().hasPermission("perm1"));
        assertTrue(agentDick.getACL().hasPermission("perm3"));
        assertTrue(agentDick.getACL().hasPermission("admin:perm1"));
        assertTrue(agentDick.getACL().hasPermission("admin:perm2"));
        assertTrue(agentDick.getACL().hasPermission("admin:perm3"));

        //harry
        PlexusSession sessHarry = security.authenticate(newToken("harry", "themaster"));
        SimpleAgent agentHarry = (SimpleAgent) sessHarry.getAgent();
		assertNotNull("Expected agent 'harry' to have a non-null ACL", agentHarry.getACL() );
        assertTrue(agentHarry.getACL().hasPermission("perm1"));
        assertTrue(agentHarry.getACL().hasPermission("perm2"));
        assertTrue(agentHarry.getACL().hasPermission("admin:perm1"));

        release(security);

    }

    private PAPToken newToken(String userName, String password)
    {
        return new PAPToken(userName, password);
    }

}
