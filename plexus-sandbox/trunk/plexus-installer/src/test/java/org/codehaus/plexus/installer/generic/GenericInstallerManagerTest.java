package org.codehaus.plexus.installer.generic;

import org.codehaus.plexus.installer.InstallerManager;
import org.codehaus.plexus.installer.InstallerManagerTest;

public class GenericInstallerManagerTest
    extends InstallerManagerTest
{

    /**
     * Look up with a Generic installer
     * 
     * @throws Exception
     */
    public void testLookupGenericInstaller() throws Exception
    {
        InstallerManager manager = (InstallerManager) lookup( InstallerManager.ROLE );

        manager.getInstaller( "generic" );
    }

}
