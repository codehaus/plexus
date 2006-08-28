package org.codehaus.bacon.component.language;

import org.codehaus.bacon.component.factory.InstanceFactory;
import org.codehaus.bacon.component.injection.ComponentInjector;

public interface LanguagePack
{
    
    String getLanguageKey();
    
    ComponentInjector getComponentInjector();
    
    InstanceFactory getInstanceFactory();

}
