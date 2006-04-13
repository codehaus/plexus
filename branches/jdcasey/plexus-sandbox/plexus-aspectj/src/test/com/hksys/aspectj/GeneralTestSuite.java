package com.hksys.aspectj;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Setup the classloader before loading {@link SimpleTest}.
 *
 * @author Stephen Haberman
 */
public class GeneralTestSuite
{

    /**
     * @return {@link SimpleTest} after setting up the classloader
     */
    public static Test suite() throws Exception
    {
        AspectJClassLoader weaver = new AspectJClassLoader(GeneralTestSuite.class.getClassLoader());

        // Use both addPackages and addPackage
        weaver.addPackagesToIgnore("org.w3c,org.apache.xerces");
        weaver.addPackageToIgnore("junit");

        weaver.addLibraryJarFile(new File("target/aspectj-classloader-1.1.0-1-aspects.jar"));

        TestSuite suite = new TestSuite();
        suite.addTestSuite(Class.forName("com.hksys.aspectj.SimpleTest", true, weaver));
        suite.addTestSuite(Class.forName("com.hksys.aspectj.StateTest", true, weaver));
        return suite;
    }

}
