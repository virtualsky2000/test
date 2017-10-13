package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import system.cache.ReaderCache;
import system.excel.WorkbookReader;
import system.logging.LogManager;
import system.logging.Logger;
import system.xml.XmlReader;

public class test {

    private static Logger log = LogManager.getLogger(test.class);

    public static void main(String[] args) {
        try {
            //            log.info("test {} : {}", "a", "b", "c");
            //            log.error("error", "a", "b", "c");
            //            log.info(String.format("%d + %d = %d", 1, 2, 3));

//        	XmlReader reader = new XmlReader(FileUtils.getFile("log4j2.xml"), Charset.defaultCharset());

//                        XmlReader reader = XmlReader.load("log4j2.xml");
            ////            XmlReaderCache reader = XmlReaderCache.load("log4j2.xml");
            //            log.info(reader.getNode("Configuration/Appenders/test").getValue());
            //            log.info(reader.getNode("Configuration/Appenders/Console").getAttributeValue("target"));
            //            log.info(reader.getNode("Configuration/Loggers/Root").getAttributeValue("level"));
            //            log.info(reader.getNode("Configuration/Appenders/Console").getAttributeValue("target"));

        	ReaderCache<XmlReader> reader1 = ReaderCache.load("log4j2.xml", XmlReader.class);

        	String x = "0";
            byte[] value = x.getBytes();
            value[0] += 10;
            x = new String(value);
        	log.info("0".getBytes()[0]);
        	log.info("1".getBytes()[0]);
        	log.info("2".getBytes()[0]);
        	log.info("9".getBytes()[0]);

        	log.info("A".getBytes()[0]);
        	log.info("J".getBytes()[0]);

        	log.info("   ".compareTo("000"));

//        	FastWorkbookReader reader = FastWorkbookReader.load("C:/Users/TN0Y03/Desktop/自動作成ツール/test.xlsx");


        	System.setProperty("org.apache.poi.util.POILogger", "org.apache.poi.util.CommonsLogger" );

        	long start, end;
        	WorkbookReader reader;

//            start = System.currentTimeMillis();
//            reader = WorkbookReader.load("C:/Users/TN0Y03/Desktop/自動作成ツール/test1.xls", "リソース名");
//
//            end = System.currentTimeMillis();
//            log.info("xls {}ms", end - start);

//        	WorkbookFactory.create(new File("d:/domain1.zip"));

            start = System.currentTimeMillis();
//            reader = WorkbookReader.load("C:/Users/TN0Y03/Desktop/自動作成ツール/test1.xlsx");
//            reader = WorkbookReader.load("C:/Users/TN0Y03/Desktop/自動作成ツール/test2.xlsx", Arrays.asList("Sheet1"));
//            reader = WorkbookReader.load("C:/Users/TN0Y03/Desktop/自動作成ツール/test1.xls", Arrays.asList("Sheet1", "Sheet3"), 1);

            Map<String, List<String>> mapRange = new HashMap<>();

//            mapRange.put("Sheet1", Arrays.asList("A1:C3"));
//
//            reader = WorkbookReader.load("C:/Users/TN0Y03/Desktop/自動作成ツール/test1.xls", mapRange, 1);
//
//            log.info("Sheet1: -------------------------------");
//            reader.readTextValue(reader.getSheet("Sheet1"), "A1:I3").forEach(str -> {for (String item : str) {log.info(item);}});

//            log.info("Sheet3: -------------------------------");
//            reader.readTextValue(reader.getSheet("Sheet3"), "A1:A5").forEach(str -> {for (String item : str) {log.info(item);}});
//            reader.readData(sheet, 0, 0, 2, 7);

//            reader.readTextValue(sheet, "A1:I3").forEach(str -> {for (String item : str) {log.info(item);}});
//            end = System.currentTimeMillis();
//            log.info("xlsx {}ms", end - start);



//            mapRange.put("Sheet1", Arrays.asList("B2:D3"));

//            WorkbookReader reader2 = WorkbookReader.load("C:/Users/TN0Y03/Desktop/自動作成ツール/test2.xlsx", Arrays.asList("Sheet1"), 1);
//            WorkbookReader reader2 = WorkbookReader.load("C:/Users/TN0Y03/Desktop/自動作成ツール/test2.xlsx", mapRange, 1);
//            reader2.readTextValue(reader2.getSheet("Sheet1"), "A1:D3").forEach(str -> {for (String item : str) {log.info(item);}});



            //            do {
            //                if (parser.next() == FastXmlParser.START_TAG && parser.isMatch(root)) { // root start
            //                    processStudentElements(parser, students);
            //                    parser.next(); // root end
            //                }
            //                nextEvent = parser.getNextEvent();
            //            } while (nextEvent != FastXmlParser.END_DOCUMENT);

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

    public static class Bean1 {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    public static class Bean2 {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

}
