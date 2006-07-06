/**
 * 
 */
package org.codehaus.plexus.tutorial;

import java.util.List;

/**
 * Adds contract for this component to be initialized with requisite configuration.<p>
 * This allows us to conveniently configure our component from say a Maven Plugin Mojo. 
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id:$
 */
public interface Initializable
{

    void initialize( List websites );

    boolean isInitialized();
}
