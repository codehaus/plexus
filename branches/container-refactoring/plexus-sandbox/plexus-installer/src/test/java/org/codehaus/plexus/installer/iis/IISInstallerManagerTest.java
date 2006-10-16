package org.codehaus.plexus.installer.iis;

import org.codehaus.plexus.installer.InstallerManager;
import org.codehaus.plexus.installer.InstallerManagerTest;

public class IISInstallerManagerTest
    extends InstallerManagerTest
{

    /**
     * Look up with an InnoSetup installer
     * 
     * @throws Exception
     */
    public void testLookupIISInstaller() throws Exception
    {
        InstallerManager manager = (InstallerManager) lookup( InstallerManager.ROLE );

        manager.getInstaller( "iis" );
    }

}
