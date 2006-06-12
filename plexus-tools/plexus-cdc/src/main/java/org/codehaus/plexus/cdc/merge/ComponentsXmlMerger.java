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
 * @version $Id:$
 */
public class ComponentsXmlMerger
    extends AbstractComponentsXmlMerger
{

    public void merge( Document rDocument )
        throws MergeException
    {

        // refactored merge.
        ComponentSetElement dCSE = new ComponentSetElement( getDominantDocument().getRootElement() );
        ComponentSetElement rCSE = new ComponentSetElement( rDocument.getRootElement() );
        dCSE.merge( rCSE );

    }

    /**
     * Writes out the merged Components descriptor to the specified file.
     * @param f File to write the merged contents to.
     * @throws Exception if there was an error while writing merged contents to the specified file.
     */
    public void writeMergedDescriptor( File f )
        throws Exception
    {
        XMLOutputter out = new XMLOutputter();
        FileWriter fw = new FileWriter( f );
        out.output( getDominantDocument(), fw );
    }

}
