package system.xml;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.ximpleware.AutoPilot;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;

import system.exception.ApplicationException;
import system.logging.LogManager;
import system.logging.Logger;
import system.utils.FileUtils;

public class VtdXmlReader {

    private static final Logger log = LogManager.getLogger(VtdXmlReader.class);

    protected File file;

    protected Charset charset;

    private XmlNode root;

    public static VtdXmlReader load(String fileName) {
        return load(FileUtils.getFile(fileName), Charset.defaultCharset());
    }

    public static VtdXmlReader load(String fileName, Charset charset) {
        return load(FileUtils.getFile(fileName), charset);
    }

    public static VtdXmlReader load(File file, Charset charset) {
        VtdXmlReader reader = new VtdXmlReader(file, charset);
        reader.load();

        return reader;
    }

    public VtdXmlReader(File file, Charset charset) {
        this.file = file;
        this.charset = charset;
    }

    public void load() {
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] buf = new byte[(int) file.length()];
            fis.read(buf);
            fis.close();

            VTDGen vg = new VTDGen();

            vg.setDoc(buf);
            vg.parse(false);
            VTDNav vn = vg.getNav();

            AutoPilot ap = new AutoPilot(vn);
            ap.selectElement("*");

            int curDepth = 0;
            int prevDepth = 0;
            XmlNode parent = null;
            XmlNode curNode = null;
            XmlNode node = null;
            String tagName;

            log.debug(file.getName());

            while (ap.iterate()) {
                curDepth = vn.getCurrentDepth();
                tagName = vn.toString(vn.getCurrentIndex());

                List<XmlAttribute> lstAttributes = null;
                int attrCount = vn.getAttrCount();
                if (attrCount > 0) {
                    lstAttributes = new ArrayList<>(vn.getAttrCount());
                    int i = vn.getCurrentIndex() + 1;
                    while (attrCount > 0) {
                        if (vn.hasAttr(vn.toString(i))) {
                            lstAttributes.add(new XmlAttribute(vn.toString(i), vn.toString(i + 1)));
                            i += 2;
                            attrCount--;
                        }
                    }
                }

                if (prevDepth <= curDepth) {

                    if (prevDepth < curDepth) {
                        parent = curNode;
                    }
                    node = new XmlNode(parent, tagName, lstAttributes);

                    if (curDepth == 0) {
                        root = node;
                        parent = root;
                    }

                    curNode = node;
                } else {
                    parent = parent.getParent();

                    node = new XmlNode(parent, tagName, lstAttributes);
                    curNode = node;
                }

                int index = vn.getText();
                if (index != -1) {
                    curNode.setValue(vn.toNormalizedString(index).trim());
                }

                prevDepth = curDepth;

                log.debug(node);
            }

            vg.clear();
        } catch (Exception e) {
            throw new ApplicationException("Xmlファイル「" + file.getAbsolutePath() + "」読込が失敗しました。", e);
        }

    }

    public XmlNode getRootNode() {
        return root;
    }

}
