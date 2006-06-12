package org.codehaus.plexus.cdc.merge;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.jdom.Document;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id:$
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

    /**
     * Sets the <b>dominant</b> document for merging.
     * @param document the dominant document.
     */
    public void setDominantDocument( Document document )
    {
        this.dDocument = document;
    }

    /**
     * Returns the <b>dominant</b> document instance to be used for merging.
     * @return the dominant document instance.
     */
    public Document getDominantDocument()
    {
        return dDocument;
    }

}
