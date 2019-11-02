package ru.alazarev.validator;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ValidatorTest {
    private static Validator validator;
    private static String xmlFileName = "xml.xml";
    private static String xsdFileName = "xsd.xsd";
    private static String xsltFileName = "xslt.xslt";
    private static String resultXmlFileName = "resultXml.xml";
    private static File xml;
    private static File xsd;
    private static File xslt;
    private static File resultXml;
    private static String[] args;

    @BeforeAll
    private static void genFile() {
        validator = new Validator();
        String xmlText = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
                "<catalog>\n" +
                "\t<disk>\n" +
                "\t\t<title>Empire Burlesque</title>\n" +
                "\t\t<artist>Bob Dylan</artist>\n" +
                "\t\t<country>USA</country>\n" +
                "\t\t<company>Columbia</company>\n" +
                "\t\t<price>10.90</price>\n" +
                "\t\t<year>1985</year>\n" +
                "\t</disk>\n" +
                "\t<disk>\n" +
                "\t\t<title>Hide your heart</title>\n" +
                "\t\t<artist>Bonnie Tyler</artist>\n" +
                "\t\t<country>UK</country>\n" +
                "\t\t<company>CBS Records</company>\n" +
                "\t\t<price>9.90</price>\n" +
                "\t\t<year>1988</year>\n" +
                "\t</disk>\n" +
                "\t<disk>\n" +
                "\t\t<title>Greatest Hits</title>\n" +
                "\t\t<artist>Dolly Parton</artist>\n" +
                "\t\t<country>USA</country>\n" +
                "\t\t<company>RCA</company>\n" +
                "\t\t<price>9.90</price>\n" +
                "\t\t<year>1982</year>\n" +
                "\t</disk>\n" +
                "</catalog>";
        String xsdText = "<xs:schema attributeFormDefault=\"unqualified\" elementFormDefault=\"qualified\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n" +
                "  <xs:element name=\"artists\">\n" +
                "    <xs:complexType>\n" +
                "      <xs:sequence>\n" +
                "        <xs:element type=\"xs:string\" name=\"artist\" maxOccurs=\"unbounded\" minOccurs=\"0\"/>\n" +
                "      </xs:sequence>\n" +
                "    </xs:complexType>\n" +
                "  </xs:element>\n" +
                "  <xs:element name=\"catalog\">\n" +
                "    <xs:complexType>\n" +
                "      <xs:sequence>\n" +
                "        <xs:element name=\"disk\" maxOccurs=\"unbounded\" minOccurs=\"0\">\n" +
                "          <xs:complexType>\n" +
                "            <xs:sequence>\n" +
                "              <xs:element type=\"xs:string\" name=\"title\"/>\n" +
                "              <xs:element type=\"xs:string\" name=\"artist\"/>\n" +
                "              <xs:element type=\"xs:string\" name=\"country\"/>\n" +
                "              <xs:element type=\"xs:string\" name=\"company\"/>\n" +
                "              <xs:element type=\"xs:float\" name=\"price\"/>\n" +
                "              <xs:element type=\"xs:short\" name=\"year\"/>\n" +
                "            </xs:sequence>\n" +
                "          </xs:complexType>\n" +
                "        </xs:element>\n" +
                "      </xs:sequence>\n" +
                "    </xs:complexType>\n" +
                "  </xs:element>\n" +
                "</xs:schema>";
        String xsltText = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
                "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n" +
                "<xsl:template match=\"/\">\n" +
                "<artists>\n" +
                "<xsl:for-each select=\"catalog/disk\">\n" +
                "<artist>\n" +
                "Artist <xsl:value-of select=\"artist\"/> born in <xsl:value-of select=\"country\"/>\n" +
                "</artist>\n" +
                "</xsl:for-each>\n" +
                "</artists>\n" +
                "</xsl:template>\n" +
                "</xsl:stylesheet>";
        String resultXmlText = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><artists><artist>\n" +
                "Artist Bob Dylan born in USA</artist><artist>\n" +
                "Artist Bonnie Tyler born in UK</artist><artist>\n" +
                "Artist Dolly Parton born in USA</artist></artists>";
        xml = generateFile(xmlFileName, xmlText);
        xsd = generateFile(xsdFileName, xsdText);
        xslt = generateFile(xsltFileName, xsltText);
        resultXml = generateFile(resultXmlFileName, resultXmlText);
        args = new String[]{xmlFileName, xsdFileName, xsltFileName, resultXmlFileName};
    }

    private static File generateFile(String fileName, String text) {
        File file = new File(new File("").getAbsolutePath() + "\\" + fileName);
        try (FileWriter writer = new FileWriter(file, false)) {
            writer.write(text);
            writer.flush();
        } catch (IOException ioe) {
            ioe.getMessage();
        }
        return file;
    }

    @Test
    void whenValidateXMLByXsdThenTrue() throws SAXException, IOException {
        boolean validate = validator.validateXMLbyXSD(xml, xsd);
        assertTrue(validate);
    }

    @Test
    void whenTransformXMLByXSLTThenTrue() throws TransformerException {
        assertTrue(validator.transformXMLbyXSLT(xml, xslt, resultXml));
    }

    private void startExceptionTest(Class exception, String[] args) {
        assertThrows(exception, () -> validator.start(args));
    }

    @Test
    void whenStartWithFourArguments() throws Exception {
        validator.start(args);
    }

    @Test
    void whenStartWithoutArguments() {
        startExceptionTest(IllegalArgumentException.class, new String[]{});
    }

    @Test
    void whenStartButOneFileDoesNotExistThenIOException() {
        String[] current = args.clone();
        current[0] = "exist.xml";
        startExceptionTest(IOException.class, current);
    }

    @Test
    void whenStartButXSDFileIsEmptyThenSAXException() {
        String emptyXSDFileName = "empty.xsd";
        String[] current = args.clone();
        current[1] = emptyXSDFileName;
        File emptyXSD = new File(new File("").getAbsolutePath() + "\\" + emptyXSDFileName);
        startExceptionTest(SAXException.class, current);
        emptyXSD.deleteOnExit();
    }

    @Test
    void whenMainTestWithRequiredArgumentsThenOk() {
        Validator.main(args);
    }

    @Test
    void whenMainTestWithInvalidArgumentsThenSAXExceptionHandled() {
        String[] current = args.clone();
        String invalidArgument = "invalid.xml";
        current[1] = invalidArgument;
        Validator.main(current);
        new File(invalidArgument).deleteOnExit();
    }

    @AfterAll
    public static void deleteFiles() {
        xml.deleteOnExit();
        xsd.deleteOnExit();
        xslt.deleteOnExit();
        resultXml.deleteOnExit();
    }
}