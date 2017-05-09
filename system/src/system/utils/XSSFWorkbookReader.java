package system.utils;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFReader.SheetIterator;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import system.exception.ApplicationException;

public class XSSFWorkbookReader extends AbstractWorkbookReader {

    private final static List<String> lstExtName = Arrays.asList("xlsx", "xlsm", "xltm", "xltx");

    private List<String> lstSheetName = null;

    private SharedStringsTable sst;

    private XSSFSheet curSheet;

    private XSSFRow curRow;

    private XSSFCell curCell;

    private String curValue;

    private AttributesImpl curAttributes = new AttributesImpl();

    public static XSSFWorkbookReader load(String fileName) {
        return load(FileUtils.getFile(fileName));
    }

    public static XSSFWorkbookReader load(File file) {
        XSSFWorkbookReader reader = new XSSFWorkbookReader(file);
        reader.load();

        return reader;
    }

    public static XSSFWorkbookReader load(String fileName, String... sheetNames) {
        return load(FileUtils.getFile(fileName), sheetNames);
    }

    public static XSSFWorkbookReader load(File file, String... sheetNames) {
        XSSFWorkbookReader reader = new XSSFWorkbookReader(file, sheetNames);
        reader.load();

        return reader;
    }

    public XSSFWorkbookReader(File file) {
        this.file = file;
        this.fileName = file.getAbsolutePath();
        String extName = FilenameUtils.getExtension(fileName);

        if (!lstExtName.contains(extName)) {
            throw new ApplicationException("unsupportted file format");
        }
    }

    public XSSFWorkbookReader(File file, String... sheetNames) {
        this(file);
        if (sheetNames != null) {
            this.lstSheetName = Arrays.asList(sheetNames);
        }
    }

    public void load() {
        try {
            workbook = new XSSFWorkbook();
            OPCPackage pkg = OPCPackage.open(fileName, PackageAccess.READ);
            try {

                XSSFReader reader = new XSSFReader(pkg);
                sst = reader.getSharedStringsTable();
                SheetIterator sheetIterator = (SheetIterator) reader.getSheetsData();

                while (sheetIterator.hasNext()) {
                    InputStream is = sheetIterator.next();
                    String sheetName = sheetIterator.getSheetName();
                    if (lstSheetName == null || lstSheetName.contains(sheetName)) {
                        curSheet = (XSSFSheet) workbook.createSheet(sheetName);
                        XMLReader parser = XMLReaderFactory.createXMLReader();
                        ContentHandler handler = new SheetHandler();
                        parser.setContentHandler(handler);
                        parser.parse(new InputSource(is));
                        is.close();
                    }
                }

            } catch (OpenXML4JException | SAXException e) {
                throw e;
            } finally {
                pkg.close();
            }

        } catch (Exception e) {
            throw new ApplicationException("Excelファイル読込が失敗しました。", e);
        }
    }

    protected void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        curValue = "";

        if (qName.equals("row")) {
            // row
            int rownum = Integer.parseInt(attributes.getValue("r"));
            curRow = curSheet.createRow(rownum - 1);
            curCell = null;
        } else if (qName.equals("c")) {
            // cell
            int columnIndex = getColumnIndex(attributes.getValue("r"));
            curCell = curRow.createCell(columnIndex);
            curAttributes.clear();
            curAttributes.setAttributes(attributes);
        }
    }

    protected void endElement(String uri, String localName, String qName) throws SAXException {
        if (curCell != null) {
            String cellType = curAttributes.getValue("t");
            switch (qName) {
            case "v":
                if (cellType == null) {
                    String s = curAttributes.getValue("s");

                    if (s == null) {
                        curCell.setCellType(CellType.STRING);
                        curCell.setCellValue(curValue);
                    } else {

                        switch (s) {
                        case "1":

                            break;
                        case "2":

                            break;
                        case "3":
                            //
                            curCell.setCellType(CellType.STRING);

                            break;
                        case "4":
                            //
                            curCell.setCellType(CellType.STRING);

                            break;
                        case "5":
                            // 時刻 (hh:mm:ss)
                            curCell.setCellType(CellType.STRING);
                            int sec = (int) (86400 * Double.parseDouble(curValue));
                            curCell.setCellValue(getSec(sec));
                            break;

                        case "9":
                            curCell.setCellType(CellType.STRING);
                            curCell.setCellValue(curValue);
                            break;
                        default:
                            curCell.setCellType(CellType.STRING);
                            curCell.setCellValue(curValue);
                            break;
                        }
                    }
                } else {
                    switch (cellType) {
                    case "s":
                        curCell.setCellType(CellType.STRING);
                        int index = Integer.parseInt(curValue);
                        curCell.setCellValue(new XSSFRichTextString(sst.getEntryAt(index)).toString());
                        break;
                    case "str":
                        curCell.setCellType(CellType.STRING);
                        curCell.setCellValue(curValue);
                        break;
                    case "e":
                        // エラー
                        curCell.setCellType(CellType.ERROR);
                        curCell.setCellErrorValue(FormulaError.forString(curValue));
                        break;
                    case "b":
                        // Boolean
                        curCell.setCellType(CellType.BOOLEAN);
                        curCell.setCellValue("1".equals(curValue));
                        break;
                    default:

                    }
                }
                break;
            case "f":
                if (curValue != null && curValue.length() > 0) {
                    curCell.setCellFormula(curValue);
                }
                break;
            default:

            }
        }
    }

    protected void characters(char[] ch, int start, int length) throws SAXException {
        curValue += new String(ch, start, length);
    }

    private class SheetHandler extends DefaultHandler {

        private XSSFWorkbookReader self = XSSFWorkbookReader.this;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            self.startElement(uri, localName, qName, attributes);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            self.endElement(uri, localName, qName);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            self.characters(ch, start, length);
        }

    }

}
