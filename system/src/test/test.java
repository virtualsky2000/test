package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import system.logging.LogManager;
import system.logging.Logger;
import system.utils.FileUtils;
import system.xml.XmlNode;
import system.xml.XmlStreamReader;

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

            XmlStreamReader reader = XmlStreamReader.load("test.xml");

            log.info(XmlNode.getNode(reader.getRootNode(), "action").getPath());
            log.info(XmlNode.getNode(reader.getRootNode(), "/script/action").getPath());

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document document = documentBuilder.parse(FileUtils.getFile("test.xml"));

            Element root = document.getDocumentElement();

            log.info(root.getNodeName());




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
