/**
 * 
 */
package org.codehaus.plexus.xsiter.deployer;

import java.util.Properties;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @dated: 22/09/2006, 11:01:19 AM
 */
public interface DeployerApplication
{

    public static final String ROLE = DeployerApplication.class.getName();

    /**
     * Set up a Project Deployment workspace
     * 
     * @param props
     * @throws Exception
     */
    public void addProject( Properties props )
        throws Exception;

    /**
     * Set up a Virtual Host for the specified workspace/project Id
     * 
     * @param pid
     * @throws Exception
     */
    public void addVirtualHost( String pid )
        throws Exception;

    /**
     * Check out the project for the specified id.
     * 
     * @param pid
     * @throws Exception
     */
    public void checkoutProject( String pid )
        throws Exception;

    /**
     * Check out a particular version of SCM tag.
     * 
     * @param pid
     * @param scmTag
     * @throws Exception
     */
    public void checkoutProject( String pid, String scmTag )
        throws Exception;

    /**
     * Deploy Project for the specified Id.
     * 
     * @param pid
     */
    public void deployProject( String pid )
        throws Exception;

    /**
     * Deploy Project for the specified Id with custom goals.
     * <p>
     * Use this for deploying for target profiles.
     * 
     * @param pid
     * @param goals String that wraps up the goals to call for deployment.
     * @throws Exception
     */
    public void deployProject( String pid, String goals )
        throws Exception;

    /**
     * Deploy project with custom goals on a specific version
     * 
     * @param pid
     * @param goals
     * @param scmTag
     * @throws Exception
     */
    public void deployProject( String pid, String goals, String scmTag )
        throws Exception;

    /**
     * Remove project for the specified Id
     * 
     * @param pid
     * @throws Exception
     */
    public void removeProject( String pid )
        throws Exception;

}
