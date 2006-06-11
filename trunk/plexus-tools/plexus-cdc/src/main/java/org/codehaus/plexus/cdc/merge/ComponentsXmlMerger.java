/**
 * 
 */
package org.codehaus.plexus.cdc.merge;

import java.io.File;
import java.io.FileWriter;

import org.codehaus.plexus.cdc.merge.support.ComponentSetElement;
import org.jdom.Document;
import org.jdom.output.XMLOutputter;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public class ComponentsXmlMerger
    extends AbstractComponentsXmlMerger
{

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.plexus.cdc.merge.Merger#merge(org.jdom.Document)
     */
    public void merge( Document rDocument )
        throws MergeException
    {

        // refactored merge.
        ComponentSetElement dCSE = new ComponentSetElement( getDominantDocument().getRootElement() );
        ComponentSetElement rCSE = new ComponentSetElement( rDocument.getRootElement() );
        dCSE.merge( rCSE );

    }

    public void writeMergedDescriptor( File f )
        throws Exception
    {
        XMLOutputter out = new XMLOutputter();
        FileWriter fw = new FileWriter( f );
        out.output( getDominantDocument(), fw );
    }

}
