package ru.alazarev.validator;


import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;


/**
 * Задание:
 * 1. Сделать runnable jar файл который бы получал как входные данные:
 * имя xml файла,
 * имя xsd файла(по этой схеме должен быть сформирован xml файл),
 * имя xslt файла
 * и имя результирующего файла(тоже хмл).
 * Приложение должно взять xml файл по имени,
 * провалидировать его по xsd схеме,
 * затем произвести xslt трансформацию по xslt,
 * затем результат провалидировать по xsd схеме(результирующий хмл также должен соответствовать некоему классу их xsd схемы)
 * и сохранить результирующий xml в файл.
 * Нужно предусмотреть обработку эксепшенов и ведение лога по каждой операции(валидация входящего хмл, xslt трансформация, валидация результата).
 * 2. Сделать юнит тесты на JUnit 5.
 * 3. Вычислить покрытие кода тестами используя JaCoCo.
 * 4. Покрытие должно быть 100%, либо объяснено почему 100% недостижимо.
 *
 * @author Aleksey Lazarev
 * @since 2.11.2019
 */
public class Validator implements IValidator {
    private static final Logger LOGGER = Logger.getLogger(Validator.class.getName());
    private final String path = new File("").getAbsolutePath().concat("\\");
    private String xml;
    private String xsd;
    private String xslt;
    private String resultXml;

    /**
     * Method performs the necessary operations with logging.
     *
     * @param arguments Need 4 files in this sequence *.xml *.xsd *.xslt *.xml
     * @throws IOException
     * @throws SAXException
     * @throws TransformerException
     */
    public void start(String[] arguments) throws IOException, SAXException, TransformerException {
        LOGGER.info("Start program");
        boolean currentState = checkArguments(arguments);
        LOGGER.info("Check input parameters: " + currentState);
        if (currentState) {
            this.xml = arguments[0];
            this.xsd = arguments[1];
            this.xslt = arguments[2];
            this.resultXml = arguments[3];
            LOGGER.info("Found folder with files: " + path);
            File xmlFile = getFile(xml);
            File xsdFile = getFile(xsd);
            LOGGER.info("Initiate validate XML by XSD . . .");
            currentState = validateXMLbyXSD(xmlFile, xsdFile);
            LOGGER.info("Validate XML by XSD: " + currentState);
            File xsltFile = getFile(xslt);
            LOGGER.info("Initiate transform XML by XSLT . . .");
            File resultXmlFile = getFile(resultXml);
            currentState = transformXMLbyXSLT(xmlFile, xsltFile, resultXmlFile);
            LOGGER.info("Transform XML by XSLT and save " + resultXml + ": " + currentState);
            LOGGER.info("Initiate validate result XML by XSD");
            currentState = validateXMLbyXSD(resultXmlFile, xsdFile);
            LOGGER.info("Validate result XML by XSD: " + currentState);
        } else {
            LOGGER.error("Arguments check is fail.");
            throw new IllegalArgumentException();
        }
        LOGGER.info("Close program.");
    }

    /**
     * Method validate XML by XSD.
     *
     * @param xml XML file.
     * @param xsd XSD file.
     * @return result of validate.
     * @throws SAXException
     * @throws IOException
     */
    @Override
    public boolean validateXMLbyXSD(File xml, File xsd) throws SAXException, IOException {
        SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                .newSchema(xsd)
                .newValidator()
                .validate(new StreamSource(xml));
        return true;
    }

    /**
     * Method transform XML by XSLT.
     *
     * @param xml  XML file.
     * @param xslt XSLT file.
     * @return result of transform.
     * @throws TransformerException
     */
    @Override
    public boolean transformXMLbyXSLT(File xml, File xslt, File resultXml) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Source xsltSource = new StreamSource(xslt);
        Transformer transformer = factory.newTransformer(xsltSource);
        Source xmlSource = new StreamSource(xml);
        transformer.transform(xmlSource, new StreamResult(resultXml));
        return true;
    }

    /**
     * Method get file by its name.
     *
     * @param fileName File name.
     * @return File object.
     */
    private File getFile(String fileName) {
        return new File(path + fileName);
    }

    /**
     * Method check arguments, if arguments size 0 or arguments contain invalid types, then false.
     *
     * @param arguments String array of arguments.
     * @return result of checking.
     */
    private boolean checkArguments(String[] arguments) {
        boolean result = arguments.length > 0;
        if (result) {
            Pattern reg = Pattern.compile("^.*\\.x(ml|sd|slt)$");
            for (String arg : arguments) {
                result = reg.matcher(arg).find();
            }
        }
        return result;
    }

    /**
     * Main method, in arguments must be specified: original XML file, XSD schema, XSLT file and result XML file.
     *
     * @param args xml, xsd, xslt, result
     */
    public static void main(String[] args) {
        try {
            Validator validator = new Validator();
            validator.start(args);
        } catch (IOException | SAXException | TransformerException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
