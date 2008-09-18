package org.codehaus.plexus.cdc.merge;

/*
 * The MIT License
 *
 * Copyright (c) 2006, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.codehaus.plexus.cdc.merge.support.ComponentSetElement;
import org.codehaus.plexus.util.IOUtil;
import org.jdom.Document;
import org.jdom.output.XMLOutputter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id$
 */
public class ComponentsXmlMerger
    extends AbstractMerger
{

    /**
     * @see Merger#merge(Document, Document)
     */
    public Document merge( Document dDocument, Document rDocument )
        throws MergeException
    {
        // TODO: Ideally we don't want to manipulate the original 
        // dominant document but use its copy for merge.        
        //Document mDoc = (Document) dDocument.clone();        // doesn't merge properly
        Document mDoc = dDocument;
        ComponentSetElement dCSE = new ComponentSetElement( mDoc.getRootElement() );
        ComponentSetElement rCSE = new ComponentSetElement( rDocument.getRootElement() );
        dCSE.merge( rCSE );
        // the contents are merged into the dominant document DOM.
        return mDoc;
    }

}
