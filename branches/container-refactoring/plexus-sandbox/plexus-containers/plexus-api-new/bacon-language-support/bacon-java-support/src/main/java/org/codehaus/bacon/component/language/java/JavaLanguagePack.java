package org.codehaus.bacon.component.language.java;

import org.codehaus.bacon.component.factory.InstanceFactory;
import org.codehaus.bacon.component.factory.java.JavaInstanceFactory;
import org.codehaus.bacon.component.injection.ComponentInjector;
import org.codehaus.bacon.component.injector.java.JavaComponentInjector;
import org.codehaus.bacon.component.language.LanguagePack;

public class JavaLanguagePack
    implements LanguagePack
{
    
    public static final String LANGUAGE = "java";
    
    private InstanceFactory instanceFactory = new JavaInstanceFactory();
    private ComponentInjector componentInjector = new JavaComponentInjector();

    public String getLanguageKey()
    {
        return LANGUAGE;
    }

    public ComponentInjector getComponentInjector()
    {
        return componentInjector;
    }

    public InstanceFactory getInstanceFactory()
    {
        return instanceFactory;
    }

}
