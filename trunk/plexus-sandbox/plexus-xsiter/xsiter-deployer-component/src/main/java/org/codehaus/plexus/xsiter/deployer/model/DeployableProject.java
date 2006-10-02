package org.codehaus.plexus.xsiter.deployer.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.xsiter.deployer.DeploymentManager;

import com.effacy.plexus.vhost.VirtualHostConfiguration;

/**
 * Deployable Project entity.
 * <p>
 * This wraps the Project details required by the {@link DeploymentManager} to
 * deploy it.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @dated: 14/09/2006, 3:43:05 PM
 */
public class DeployableProject
{

    // Required Project creation properties
    public static enum ProjectProperties {
        PROP_SCM_URL("scmURL", true), PROP_SCM_USER("scmUsername", true), PROP_SCM_PASSWORD("scmPassword", true), PROP_SCM_TAG(
            "scmTag", true), PROP_LABEL("label", true);

        /**
         * Property name
         */
        private String name;

        /**
         * Is Property required?
         */
        private boolean required;

        /**
         * @param name
         * @param required
         */
        private ProjectProperties( String name, boolean required )
        {
            this.name = name;
            this.required = required;
        }

        /**
         * @return the name
         */
        public String getName()
        {
            return name;
        }

        /**
         * @return the required
         */
        public boolean isRequired()
        {
            return required;
        }

        /**
         * Returns a List of required project properties
         * 
         * @return
         */
        public static List getRequiredProjectProperties()
        {
            List list = new ArrayList();
            ProjectProperties[] allProps = values();
            for ( int i = 0; i < allProps.length; i++ )
            {
                if ( allProps[i].required )
                    list.add( allProps[i] );
            }
            return list;
        }

    }

    /**
     * SCM URl for the project
     */
    private String scmURL;

    /**
     * Label for this Project instance.
     * <p>
     * This is used in workspace creation for the project.
     */
    private String label;

    /**
     * Username to use for logging in to SCM
     */
    private String scmUsername = "anonymous";

    /**
     * Password to use for logging in to SCM.
     */
    private String scmPassword = "";

    /**
     * Source control repository tag to be used for checking out a project.
     */
    private String scmTag = "HEAD";

    /**
     * Virtual Host Configuration for a {@link DeployableProject}.
     * 
     * @deprecated <em>Virtual Host Configuration is now sourced from the pom.xml which is a logical choice</em>
     */
    VirtualHostConfiguration virtualHostConfiguration;

    /**
     * @return the scmURL
     */
    public String getScmURL()
    {
        return scmURL;
    }

    /**
     * @param scmURL the scmURL to set
     */
    public void setScmURL( String scmURL )
    {
        this.scmURL = scmURL;
    }

    /**
     * @return the projectId
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * @param projectId the projectId to set
     */
    public void setLabel( String projectId )
    {
        this.label = projectId;
    }

    /**
     * SCM Username to use for logging into Source Control
     * 
     * @return
     */
    public String getScmUsername()
    {
        // TODO Auto-generated method stub
        return this.scmUsername;
    }

    /**
     * SCM Password to user for logging into Source Control
     * 
     * @return
     */
    public String getScmPassword()
    {
        // TODO Auto-generated method stub
        return this.scmPassword;
    }

    /**
     * @param scmPassword the scmPassword to set
     */
    public void setScmPassword( String scmPassword )
    {
        this.scmPassword = scmPassword;
    }

    /**
     * @param scmUsername the scmUsername to set
     */
    public void setScmUsername( String scmUsername )
    {
        this.scmUsername = scmUsername;
    }

    /**
     * @return the virtualHostConfiguration
     */
    public VirtualHostConfiguration getVirtualHostConfiguration()
    {
        return virtualHostConfiguration;
    }

    /**
     * @param vhostConfig the virtualHostConfiguration to set
     */
    public void setVirtualHostConfiguration( VirtualHostConfiguration vhostConfig )
    {
        this.virtualHostConfiguration = vhostConfig;
    }

    /**
     * Returns <code>true</code> if a Virtual Host Configuration was
     * specified.
     * 
     * @return
     */
    public boolean hasVirtualHostConfiguration()
    {
        return ( null != getVirtualHostConfiguration() );
    }

    /**
     * @return the scmTag
     */
    public String getScmTag()
    {
        return scmTag;
    }

    /**
     * @param scmTag the scmTag to set
     */
    public void setScmTag( String scmTag )
    {
        this.scmTag = scmTag;
    }

}
