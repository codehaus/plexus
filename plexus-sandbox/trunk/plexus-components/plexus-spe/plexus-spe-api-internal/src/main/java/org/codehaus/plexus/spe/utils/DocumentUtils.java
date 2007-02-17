package org.codehaus.plexus.spe.utils;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DocumentUtils
{
    public static Document getEmptyDocument( String rootTag )
    {
        Document document = documentBuilder.newDocument();

        document.appendChild( document.createElement( rootTag ) );

//        System.err.println( "root=" + document.getFirstChild() );

        return document;
    }

    public static void dumpDocument( Document document )
    {
        try
        {
            Source source = new DOMSource( document );

            Result result = new StreamResult( System.err );

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform( source, result );
            System.err.println();
        }
        catch ( TransformerException e )
        {
            throw new RuntimeException( "Error while dumping document" );
        }
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    private static DocumentBuilder documentBuilder;

    static
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try
        {
            documentBuilder = factory.newDocumentBuilder();
        }
        catch ( ParserConfigurationException e )
        {
            throw new RuntimeException( "Error whilee getting new document builder" );
        }
    }
}
