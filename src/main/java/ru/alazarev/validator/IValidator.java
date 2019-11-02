package ru.alazarev.validator;

import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;

/**
 * Interface IValidator.
 *
 * @author Aleksey Lazarev
 * @since 02.11.2019
 */
public interface IValidator {
    /**
     * Method validate XML by XSD.
     *
     * @param xml XML file.
     * @param xsd XSD file.
     * @return result of validate.
     * @throws SAXException
     * @throws IOException
     */
    boolean validateXMLbyXSD(File xml, File xsd) throws SAXException, IOException;

    /**
     * Method transform XML by XSLT.
     *
     * @param xml  XML file.
     * @param xslt XSLT file.
     * @return result of transform.
     * @throws TransformerException
     */
    boolean transformXMLbyXSLT(File xml, File xslt, File resultXml) throws TransformerException;
}
