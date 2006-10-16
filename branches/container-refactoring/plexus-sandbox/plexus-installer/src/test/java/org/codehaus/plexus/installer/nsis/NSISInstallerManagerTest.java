package org.codehaus.plexus.installer.nsis;

import org.codehaus.plexus.installer.InstallerManager;
import org.codehaus.plexus.installer.InstallerManagerTest;

public class NSISInstallerManagerTest
    extends InstallerManagerTest
{

    /**
     * Look up with a NSIS installer
     * 
     * @throws Exception
     */
    public void testLookupNSISInstaller() throws Exception
    {
        InstallerManager manager = (InstallerManager) lookup( InstallerManager.ROLE );

        manager.getInstaller( "nsi" );
    }

}
