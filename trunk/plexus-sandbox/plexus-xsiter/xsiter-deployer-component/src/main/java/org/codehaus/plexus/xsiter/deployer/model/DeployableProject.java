package org.codehaus.plexus.xsiter.deployer.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.xsiter.deployer.DeploymentManager;

/**
 * Deployable Project entity.
 * <p>
 * This wraps the Project details required by the {@link DeploymentManager} to
 * deploy it.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @dated: 14/09/2006, 3:43:05 PM
 */
public class DeployableProject extends DeployerResource
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

}
