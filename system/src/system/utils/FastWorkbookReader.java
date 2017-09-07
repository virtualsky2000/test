package system.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FilenameUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import system.exception.ApplicationException;
import system.logging.LogManager;
import system.logging.Logger;

public class FastWorkbookReader {

    private static final Logger log = LogManager.getLogger(FastWorkbookReader.class);

    private final static List<String> lstExtName = Arrays.asList("xlsx", "xlsm", "xltm", "xltx");

    protected File file;

    protected String fileName;

    private String[] sharedStrings;

    private List<String> lstSheetId = new ArrayList<>();

    private List<String> lstSheetName = new ArrayList<>();

    public static FastWorkbookReader load(String fileName) {
        return load(FileUtils.getFile(fileName));
    }

    public static FastWorkbookReader load(File file) {
        FastWorkbookReader reader = new FastWorkbookReader(file);
        reader.load();

        return reader;
    }

    public FastWorkbookReader(File file) {
        this.file = file;
        this.fileName = file.getAbsolutePath();
        String extName = FilenameUtils.getExtension(fileName);

        if (!lstExtName.contains(extName)) {
            throw new ApplicationException("unsupportted file format");
        }
    }

    public void load() {
        try (ZipFile zip = new ZipFile(file)) {
            ZipEntry entry = zip.getEntry("xl/workbook.xml");

            getSheetInfo(zip.getInputStream(entry));

            entry = zip.getEntry("xl/sharedStrings.xml");
            if (entry != null) {
                InputStream in = zip.getInputStream(entry);
                sharedStrings = getSharedStrings(in);
            }

            Workbook book = new Workbook();

            for (int i = 0, len = lstSheetId.size(); i < len; i++) {
                String fileName = "xl/worksheets/sheet" + lstSheetId.get(i) + ".xml";
                log.info("{}: {}", lstSheetName.get(i), fileName);
                entry = zip.getEntry(fileName);
                InputStream in = zip.getInputStream(entry);
                Sheet sheet = book.createSheet(lstSheetName.get(i));
                getSheet(in, sheet);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Excel読込が失敗しました。", e);
        }

    }

    protected void getSheetInfo(InputStream in) {
        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            InputSource is = new InputSource(new InputStreamReader(in, StandardCharsets.UTF_8));
            SheetInfoReaderHandler readerHandler = new SheetInfoReaderHandler();
            parser.parse(is, readerHandler);
            in.close();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new ApplicationException(e);
        }
    }

    protected String[] getSharedStrings(InputStream in) {
        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            InputSource is = new InputSource(new InputStreamReader(in, StandardCharsets.UTF_8));
            SharedStringsReaderHandler readerHandler = new SharedStringsReaderHandler();
            parser.parse(is, readerHandler);
            in.close();
            return readerHandler.getSharedStrings();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new ApplicationException(e);
        }
    }

    protected void getSheet(InputStream in, Sheet sheet) {
        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            InputSource is = new InputSource(new InputStreamReader(in, StandardCharsets.UTF_8));
            SheetReaderHandler readerHandler = new SheetReaderHandler(sheet);
            parser.parse(is, readerHandler);
            in.close();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new ApplicationException(e);
        }
    }

    private class SheetInfoReaderHandler extends DefaultHandler {

        private int level = 0;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            level++;
            if (level == 3 && "sheet".equals(qName)) {
                lstSheetId.add(attributes.getValue("sheetId"));
                lstSheetName.add(attributes.getValue("name"));
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            level--;
        }

    }

    private class SharedStringsReaderHandler extends DefaultHandler {

        private int level = 0;

        private String value = "";

        private String tagName = "";

        private List<String> lstSharedStrings = new ArrayList<>();

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            level++;
            tagName = qName;
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (level == 3 && "t".equals(qName)) {
                lstSharedStrings.add(value);
                value = "";
            }
            level--;
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (level == 3 && "t".equals(tagName)) {
                value += new String(ch, start, length);
            }
        }

        public String[] getSharedStrings() {
            return lstSharedStrings.toArray(new String[0]);
        }

    }

    private class SheetReaderHandler extends DefaultHandler {

        private int level = 0;

        private String value = "";

        private String tagName = "";

        private AttributesImpl curAttributes;

        private Sheet sheet;

        private Row curRow;

        private Cell curCell;

        public SheetReaderHandler(Sheet sheet) {
            this.sheet = sheet;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            level++;
            tagName = qName;
            // if (level == 3 && "row".equals(qName)) {
            // int rowIndex = Integer.parseInt(attributes.getValue("r")) - 1;
            // curRow = sheet.createRow(rowIndex);
            // curCell = null;
            // } else if (level == 4 && "c".equals(qName)) {
            // int columnIndex = getColumnIndex(attributes.getValue("r"));
            // curCell = curRow.createCell(columnIndex);
            // curAttributes.clear();
            // curAttributes.setAttributes(attributes);
            // }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (level == 3 && "t".equals(qName)) {
                // lstSharedStrings.add(value);
                value = "";
            }
            level--;
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (level == 3 && "t".equals(tagName)) {
                value += new String(ch, start, length);
            }
        }

    }

    public static class Workbook {

        private LinkedHashMap<String, Sheet> lstSheet = new LinkedHashMap<>();

        public Sheet createSheet(String sheetName) {
            Sheet sheet = new Sheet();
            lstSheet.put(sheetName, sheet);
            return sheet;
        }

    }

    public static class Sheet {

        public Row createRow(int rowIndex) {

            return null;
        }

    }

    public static class Row {

        public Cell createCell(int columnIndex) {

            return null;
        }

    }

    public static class Cell {

    }

    public static int getColumnIndex(String str) {
        int col = 0;
        for (int i = 0, len = str.length(); i < len; i++) {
            char c = str.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                col = col * 26 + c - 64;
            } else {
                break;
            }
        }

        return col - 1;
    }

}
