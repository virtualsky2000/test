package system.xml;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;

public class XmlUtils {

    public static Document loadDocument(String filename) {
        Document doc = new Document() {

            @Override
            public String getNodeName() {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public String getNodeValue() throws DOMException {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public void setNodeValue(String nodeValue) throws DOMException {
                // TODO 自動生成されたメソッド・スタブ

            }

            @Override
            public short getNodeType() {
                // TODO 自動生成されたメソッド・スタブ
                return 0;
            }

            @Override
            public Node getParentNode() {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public NodeList getChildNodes() {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public Node getFirstChild() {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public Node getLastChild() {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public Node getPreviousSibling() {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public Node getNextSibling() {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public NamedNodeMap getAttributes() {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public Document getOwnerDocument() {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public Node insertBefore(Node newChild, Node refChild) throws DOMException {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public Node removeChild(Node oldChild) throws DOMException {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public Node appendChild(Node newChild) throws DOMException {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public boolean hasChildNodes() {
                // TODO 自動生成されたメソッド・スタブ
                return false;
            }

            @Override
            public Node cloneNode(boolean deep) {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public void normalize() {
                // TODO 自動生成されたメソッド・スタブ

            }

            @Override
            public boolean isSupported(String feature, String version) {
                // TODO 自動生成されたメソッド・スタブ
                return false;
            }

            @Override
            public String getNamespaceURI() {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public String getPrefix() {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public void setPrefix(String prefix) throws DOMException {
                // TODO 自動生成されたメソッド・スタブ

            }

            @Override
            public String getLocalName() {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public boolean hasAttributes() {
                // TODO 自動生成されたメソッド・スタブ
                return false;
            }

            @Override
            public String getBaseURI() {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public short compareDocumentPosition(Node other) throws DOMException {
                // TODO 自動生成されたメソッド・スタブ
                return 0;
            }

            @Override
            public String getTextContent() throws DOMException {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public void setTextContent(String textContent) throws DOMException {
                // TODO 自動生成されたメソッド・スタブ

            }

            @Override
            public boolean isSameNode(Node other) {
                // TODO 自動生成されたメソッド・スタブ
                return false;
            }

            @Override
            public String lookupPrefix(String namespaceURI) {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public boolean isDefaultNamespace(String namespaceURI) {
                // TODO 自動生成されたメソッド・スタブ
                return false;
            }

            @Override
            public String lookupNamespaceURI(String prefix) {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public boolean isEqualNode(Node arg) {
                // TODO 自動生成されたメソッド・スタブ
                return false;
            }

            @Override
            public Object getFeature(String feature, String version) {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public Object setUserData(String key, Object data, UserDataHandler handler) {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public Object getUserData(String key) {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public DocumentType getDoctype() {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public DOMImplementation getImplementation() {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public Element getDocumentElement() {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public Element createElement(String tagName) throws DOMException {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public DocumentFragment createDocumentFragment() {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public Text createTextNode(String data) {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public Comment createComment(String data) {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public CDATASection createCDATASection(String data) throws DOMException {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public Attr createAttribute(String name) throws DOMException {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public EntityReference createEntityReference(String name) throws DOMException {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public NodeList getElementsByTagName(String tagname) {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public Node importNode(Node importedNode, boolean deep) throws DOMException {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public Element getElementById(String elementId) {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public String getInputEncoding() {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public String getXmlEncoding() {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public boolean getXmlStandalone() {
                // TODO 自動生成されたメソッド・スタブ
                return false;
            }

            @Override
            public void setXmlStandalone(boolean xmlStandalone) throws DOMException {
                // TODO 自動生成されたメソッド・スタブ

            }

            @Override
            public String getXmlVersion() {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public void setXmlVersion(String xmlVersion) throws DOMException {
                // TODO 自動生成されたメソッド・スタブ

            }

            @Override
            public boolean getStrictErrorChecking() {
                // TODO 自動生成されたメソッド・スタブ
                return false;
            }

            @Override
            public void setStrictErrorChecking(boolean strictErrorChecking) {
                // TODO 自動生成されたメソッド・スタブ

            }

            @Override
            public String getDocumentURI() {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public void setDocumentURI(String documentURI) {
                // TODO 自動生成されたメソッド・スタブ

            }

            @Override
            public Node adoptNode(Node source) throws DOMException {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public DOMConfiguration getDomConfig() {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }

            @Override
            public void normalizeDocument() {
                // TODO 自動生成されたメソッド・スタブ

            }

            @Override
            public Node renameNode(Node n, String namespaceURI, String qualifiedName) throws DOMException {
                // TODO 自動生成されたメソッド・スタブ
                return null;
            }};

        return doc;
    }

    public static Document loadDocument(String filename, NodeFilter filter) {

        return null;
    }

    public interface NodeFilter {

        public boolean accept(Node node);

    }

}
