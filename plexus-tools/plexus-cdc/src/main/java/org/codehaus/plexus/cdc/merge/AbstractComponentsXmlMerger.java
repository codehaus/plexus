package org.codehaus.plexus.cdc.merge;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.jdom.Document;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public abstract class AbstractComponentsXmlMerger
    extends AbstractLogEnabled
    implements Merger
{

    /**
     * The dominant document instance.
     */
    private Document dDocument;

    public AbstractComponentsXmlMerger()
    {
        super();
    }

    public void setDominantDocument( Document document )
    {
        this.dDocument = document;
    }

    /**
     * @return the dDocument
     */
    public Document getDominantDocument()
    {
        return dDocument;
    }

}
