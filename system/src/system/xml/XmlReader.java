package system.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.LocatorImpl;

import system.exception.ApplicationException;
import system.logging.LogManager;
import system.logging.Logger;
import system.utils.FileUtils;

public class XmlReader {

    private static final Logger log = LogManager.getLogger(XmlReader.class);

    private static final SAXParserFactory factory = SAXParserFactory.newInstance();

    protected File file;

    protected Charset charset;

    protected XmlNode root;

    private XmlNode parent;

    private String value;

    private String comment;

    private Locator locator;

    public static XmlReader load(String fileName) {
        return load(FileUtils.getFile(fileName), Charset.defaultCharset());
    }

    public static XmlReader load(String fileName, Charset charset) {
        return load(FileUtils.getFile(fileName), charset);
    }

    public static XmlReader load(File file, Charset charset) {
        XmlReader reader = new XmlReader(file, charset);
        reader.load();

        return reader;
    }

    public XmlReader(File file, Charset charset) {
        this.file = file;
        this.charset = charset;
    }

    public void load() {
        try {
            SAXParser parser = factory.newSAXParser();

            InputSource is = new InputSource(new InputStreamReader(new FileInputStream(file), charset));
            XmlReaderHandler readerHandler = new XmlReaderHandler();
            parser.getXMLReader().setProperty("http://xml.org/sax/properties/lexical-handler", readerHandler);
            parser.parse(is, readerHandler);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new ApplicationException("Xmlファイル「" + file.getAbsolutePath() + "」読込が失敗しました。", e);
        }
    }

    public XmlNode getNode(String path) {
        return XmlNode.getNode(root.getLstNode(), path);
    }

    public XmlNode getRootNode() {
        return this.root;
    }

    protected void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    protected void startDocument() throws SAXException {
        parent = null;
        comment = "";
    }

    protected void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        XmlNode node = new XmlNode(parent, qName, getAttributes(attributes));
        node.setLocator(new LocatorImpl(locator));
        node.setComment(comment);

        if (parent != null) {
            List<XmlNode> nodes = parent.getLstNode();
            if (nodes == null) {
                nodes = new ArrayList<>();
                parent.setLstNode(nodes);
            }
            nodes.add(node);
        } else {
            root = node;
        }

        parent = node;
        value = "";
    }

    protected void endElement(String uri, String localName, String qName) throws SAXException {
        parent.setValue(value);
        parent = parent.getParent();
        comment = "";
    }

    protected void characters(char[] ch, int start, int length) throws SAXException {
        value += new String(ch, start, length);
    }

    protected void endDocument() throws SAXException {

    }

    protected void startPrefixMapping(String prefix, String uri) throws SAXException {
        log.debug("startPrefixMapping: {} {}", prefix, uri);
    }

    protected void endPrefixMapping(String prefix) throws SAXException {
        log.debug("endPrefixMapping: {}", prefix);
    }

    protected void ignorableWhitespace(char ch[], int start, int length) throws SAXException {

    }

    protected void processingInstruction(String target, String data) throws SAXException {
        log.debug("processingInstruction: {} {}", target, data);
    }

    protected void skippedEntity(String name) throws SAXException {
        log.debug("skippedEntity: {}", name);
    }

    protected void startDTD(String name, String publicId, String systemId) throws SAXException {

    }

    protected void endDTD() throws SAXException {

    }

    protected void startEntity(String name) throws SAXException {

    }

    protected void endEntity(String name) throws SAXException {

    }

    protected void startCDATA() throws SAXException {

    }

    protected void endCDATA() throws SAXException {

    }

    protected void comment(char[] ch, int start, int length) throws SAXException {
        comment = new String(ch, start, length).trim();
        log.debug("comment: {}", comment);
    }

    private class XmlReaderHandler extends DefaultHandler implements LexicalHandler {

        private XmlReader self = XmlReader.this;

        public void setDocumentLocator(Locator locator) {
            self.setDocumentLocator(locator);
        }

        public void startDocument() throws SAXException {
            self.startDocument();
        }

        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            self.startElement(uri, localName, qName, attributes);
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            self.endElement(uri, localName, qName);
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            self.characters(ch, start, length);
        }

        public void endDocument() throws SAXException {
            self.endDocument();
        }

        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            self.startPrefixMapping(prefix, uri);
        }

        public void endPrefixMapping(String prefix) throws SAXException {
            self.endPrefixMapping(prefix);
        }

        public void ignorableWhitespace(char ch[], int start, int length) throws SAXException {
            self.ignorableWhitespace(ch, start, length);
        }

        public void processingInstruction(String target, String data) throws SAXException {
            self.processingInstruction(target, data);
        }

        public void skippedEntity(String name) throws SAXException {
            self.skippedEntity(name);
        }

        public void startDTD(String name, String publicId, String systemId) throws SAXException {
            self.startDTD(name, publicId, systemId);
        }

        public void endDTD() throws SAXException {
            self.endDTD();
        }

        public void startEntity(String name) throws SAXException {
            self.startEntity(name);
        }

        public void endEntity(String name) throws SAXException {
            self.endEntity(name);
        }

        public void startCDATA() throws SAXException {
            self.startCDATA();
        }

        public void endCDATA() throws SAXException {
            self.endCDATA();
        }

        public void comment(char[] ch, int start, int length) throws SAXException {
            self.comment(ch, start, length);
        }

    }

    private static List<XmlAttribute> getAttributes(Attributes attributes) {
        int len = attributes.getLength();
        if (len == 0) {
            return null;
        }

        List<XmlAttribute> lstAttributes = new ArrayList<>(len);

        for (int i = 0; i < len; i++) {
            lstAttributes.add(new XmlAttribute(attributes.getQName(i), attributes.getValue(i)));
        }

        return lstAttributes;
    }

}