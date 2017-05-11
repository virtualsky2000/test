package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.ximpleware.AutoPilot;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;

import system.logging.LogManager;
import system.logging.Logger;
import system.utils.FileUtils;
import system.xml.XmlAttribute;
import system.xml.XmlNode;

public class test {

    private static Logger log = LogManager.getLogger(test.class);

    public static void main(String[] args) {
        try {
            //            log.info("test {} : {}", "a", "b", "c");
            //            log.error("error", "a", "b", "c");
            //            log.info(String.format("%d + %d = %d", 1, 2, 3));

            //            XmlReader reader = XmlReader.load("log4j2.xml");
            ////            XmlReaderCache reader = XmlReaderCache.load("log4j2.xml");
            //            log.info(reader.getNode("Configuration/Appenders/test").getValue());
            //            log.info(reader.getNode("Configuration/Appenders/Console").getAttributeValue("target"));
            //            log.info(reader.getNode("Configuration/Loggers/Root").getAttributeValue("level"));
            //            log.info(reader.getNode("Configuration/Appenders/Console").getAttributeValue("target"));

            int count = 0;

            try {
                File file = FileUtils.getFile("test.xml");
                FileInputStream fis = new FileInputStream(file);
                byte[] buf = new byte[(int) file.length()];
                fis.read(buf);

                VTDGen vg = new VTDGen();

                vg.setDoc(buf);
                vg.parse(false);
                VTDNav vn = vg.getNav();

                AutoPilot ap = new AutoPilot(vn);

                ap.selectElement("*");

                XmlNode root;

                int curDepth = 0;
                int prevDepth = 0;
                XmlNode parent = null;
                XmlNode curNode = null;
                XmlNode node = null;
                String tagName;

                while (ap.iterate()) {
                    curDepth = vn.getCurrentDepth();
                    tagName = vn.toString(vn.getCurrentIndex());

                    if (prevDepth <= curDepth) {
                        List<XmlAttribute> lstAttributes = null;
                        int attrCount = vn.getAttrCount();
                        if (attrCount > 0) {
                            lstAttributes = new ArrayList<>(vn.getAttrCount());
                            int i = vn.getCurrentIndex() + 1;
                            for (;;) {
                                if (vn.hasAttr(vn.toString(i))) {
                                    lstAttributes.add(new XmlAttribute(vn.toString(i), vn.toString(i + 1)));
                                    i += 2;
                                    attrCount--;
                                    if (attrCount == 0) {
                                        break;
                                    }
                                }
                            }
                        }

                        if (prevDepth < curDepth) {
                            parent = curNode;
                        }
                        node = new XmlNode(parent, tagName, lstAttributes);
                        curNode = node;

                        if (curDepth == 0) {
                            root = node;
                            parent = root;
                        }
                    } else {
                        parent = parent.getParent();
                    }

                    int t = vn.getText();
                    if (t != -1) {
                        String value = vn.toNormalizedString(t).trim();
                        curNode.setValue(value);
                    }

                    prevDepth = curDepth;

                    log.info(node);
                }

                //                for (int i = 0, len = lstIndex.size(); i < len; i++) {
                //                    int[] index = lstIndex.get(i);
                //                    curDepth = index[0];
                //                    tagName = vn.toString(index[1]);
                //
                //                    if (curDepth == 0) {
                //                        // root
                //                        log.info("start root");
                //                        node = new XmlNode(parent, tagName, getAttributes(vn, lstIndex, i));
                //                        root = node;
                //                        curNode = node;
                //                    } else if (prevDepth < curDepth) {
                //                        log.info("start element");
                //                        parent = curNode;
                //                        node = new XmlNode(parent, tagName, getAttributes(vn, lstIndex, i));
                //                        curNode = node;
                //                    } else if (prevDepth == curDepth) {
                //                        log.info("start element");
                //                        node = new XmlNode(parent, tagName, getAttributes(vn, lstIndex, i));
                //                        curNode = node;
                //                    } else if (prevDepth > curDepth) {
                //                        log.info("end element");
                //                        parent = parent.getParent();
                //                    }
                //
                //                    log.info("Element name ==> {} {} {} ", curDepth, index[1], tagName);
                //
                //                    int t = index[2];
                //                    if (t != -1) {
                //                        String value = vn.toNormalizedString(t).trim();
                //                        log.info("Text content ==> {} {}", t, value);
                //                        curNode.setValue(value);
                //                    }
                //
                //                    prevDepth = curDepth;
                //                    prevIndex = index;
                //                }
                //
                //                log.info(vn.toString(19));

            } catch (Exception e) {
                // TODO 自動生成された catch ブロック
                e.printStackTrace();
            }

            //            log.info("({})", StringUtils.rightPad("", 4, "　"));
            //            log.info("({})", StringUtils.rightPad("あ", 4, "　"));
            //            log.info("({})", StringUtils.rightPad("１２", 4, "　"));
            //
            //            XmlStreamReader reader = XmlStreamReader.load("test.xml");
            //
            //            log.info(XmlNode.getNode(reader.getRootNode(), "action").getPath());
            //            log.info(XmlNode.getNode(reader.getRootNode(), "/script/action").getPath());
            //
            //            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            //            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            //            Document document = documentBuilder.parse(FileUtils.getFile("test.xml"));
            //
            //            Element root = document.getDocumentElement();
            //
            //            log.info(root.getNodeName());

            //
            //            log.info(reader.getNodes("Console").size());

            //            JFrame frame = new JFrame();
            //
            //            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            //            frame.setVisible(true);

            //            frame.addWindowListener(l);

            /*           PropertiesReader reader = PropertiesReader.load("test.properties", true);

            reader.keySet().forEach(str -> System.out.println(str));

            System.out.println("\nkeys\n");
            Enumeration<Object> e = reader.keys();

            while (e.hasMoreElements()) {
                System.out.println(e.nextElement());
            }

            System.out.println("\nvalues\n");

            reader.values().forEach(System.out::println);

            System.out.println("\nentrySet:\n");
            reader.entrySet().forEach(entry -> System.out.println(entry.getKey() + "=" + entry.getValue()));*/

            //            System.out.println("========start");
            //            cache.keys();
            //            cache.list(System.out);
            //            System.out.println("========end");
            //
            //            System.out.println();
            //            System.out.println();
            //            System.out.println();
            //
            //            SortPropertyCache cache2 = SortPropertyCache.load("test.properties");
            //
            //            for (Object key : cache2.keySet()) {
            //                System.out.println(key);
            //            }
            //
            //            System.out.println("========start");
            //            cache2.list(System.out);
            //            System.out.println("========end");

            //            System.out.println("test=[" + cache.getProperty("test.zzz", true) + "]");
            //
            //            System.out.println("test=[" + cache.getProperty("test") + "]");
            //
            //            ExcelCache excel = ExcelCache.load("D:\\test.xls");
            //            HSSFSheet sheet = excel.getSheet("sheet1");
            //
            //            int row = excel.findEndRow(sheet, 1, 1);
            //            int col = excel.findEndCol(sheet, 1, 1);
            //
            //            System.out.println("row=[" + row + "]");
            //            System.out.println("col=[" + col + "]");
            //
            //            Object[][] data = excel.readData(sheet, 1, 1, row, col);
            //
            //            testBean bean = new testBean();
            //
            //            ConstraintViolation<testBean>[] violation = ValidatorUtils.validate(bean);
            //
            //            for (ConstraintViolation<testBean> v : violation) {
            //                String path = v.getPropertyPath().toString();
            //                log.info("path:{} message:{}", path, v.getMessage());
            //            }
            //
            //            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "dir");
            //            Process process = pb.start();
            //
            //            InputStream is = process.getInputStream();
            //            printInputStream(is);
            //
            //            process.waitFor();

            //            ValidatorUtils.validate(bean);

            //            try {
            //                Thread.sleep(500000);
            //            } catch (InterruptedException e) {
            //                e.printStackTrace();
            //            }

            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printInputStream(InputStream is) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "Shift_JIS"))) {
            for (;;) {
                String line = br.readLine();
                if (line == null)
                    break;
                System.out.println(line);
            }
        }
    }

}
