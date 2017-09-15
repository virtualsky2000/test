package system.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFReader.SheetIterator;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheet;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import system.exception.ApplicationException;
import system.logging.LogManager;
import system.logging.Logger;

public class XSSFWorkbookReader extends AbstractWorkbookReader {

	private static final Logger log = LogManager.getLogger(XSSFWorkbookReader.class);

	private SharedStringsTable sst;

	private XSSFSheet curSheet;

	private XSSFRow curRow;

	private XSSFCell curCell;

	private String curValue;

	private AttributesImpl curAttributes = new AttributesImpl();

	public static XSSFWorkbookReader load(String fileName) {
		return load(FileUtils.getFile(fileName), null, 0);
	}

	public static XSSFWorkbookReader load(String fileName, int userMode) {
		return load(FileUtils.getFile(fileName), null, userMode);
	}

	public static XSSFWorkbookReader load(String fileName, List<String> lstSheetName) {
		return load(FileUtils.getFile(fileName), lstSheetName, 0);
	}

	public static XSSFWorkbookReader load(String fileName, List<String> lstSheetName, int userMode) {
		return load(FileUtils.getFile(fileName), lstSheetName, userMode);
	}

	public static XSSFWorkbookReader load(File file) {
		return load(file, null, 0);
	}

	public static XSSFWorkbookReader load(File file, int userMode) {
		return load(file, null, userMode);
	}

	public static XSSFWorkbookReader load(File file, List<String> lstSheetName) {
		return load(file, lstSheetName, 0);
	}

	public static XSSFWorkbookReader load(File file, List<String> lstSheetName, int userMode) {
		XSSFWorkbookReader reader = new XSSFWorkbookReader(file, lstSheetName, userMode);
		reader.load();

		return reader;
	}

	public XSSFWorkbookReader(File file, List<String> lstSheetName, int userMode) {
		this.file = file;
		this.userMode = userMode;
		this.lstSheetName = lstSheetName;
	}

	public void load() {
		try {
			if (userMode == 0) {
				InputStream inp = new FileInputStream(file);
				if (!inp.markSupported()) {
					inp = new PushbackInputStream(inp, 8);
				}
				if (lstSheetName == null) {
					workbook = new XSSFWorkbook(inp);
				} else {
					workbook = new XSSFWorkbook(inp) {

						@Override
						public void parseSheet(Map<String, XSSFSheet> shIdMap, CTSheet ctSheet) {
							if (XSSFWorkbookReader.this.lstSheetName.contains(ctSheet.getName())) {
								super.parseSheet(shIdMap, ctSheet);
							}
						}

					};
				}
			} else {
				try (OPCPackage pkg = OPCPackage.open(file, PackageAccess.READ)) {
					workbook = new XSSFWorkbook();

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
				}
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
		} else {
			// log.info("in startElement... qName=({}), localName=({})", qName,
			// localName);
		}
	}

	protected void endElement(String uri, String localName, String qName) throws SAXException {
		if (curCell != null) {
			String t = curAttributes.getValue("t");
			switch (qName) {
			case "v":
				if (t != null) {
					log.info("in endElement... t=({})", t);
					switch (t) {
					case "s":
						curCell.setCellType(CellType.STRING);
						int index = Integer.parseInt(curValue);
						curCell.setCellValue(sst.getEntryAt(index).getT());
						log.info(sst.getEntryAt(index).getT());
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
						log.error("in endElement... t=({})", t);
						break;
					}

				} else {
					String s = curAttributes.getValue("s");
					log.info("in endElement... s=({}) v=({})", s, curValue);

					if (s == null) {
						curCell.setCellType(CellType.STRING);
						curCell.setCellValue(curValue);
					} else {

						switch (s) {
						case WorkbookUtils.NUMERIC:
							curCell.setCellType(CellType.NUMERIC);
							curCell.setCellValue(curValue);
							break;
						case WorkbookUtils.DATE_YYYYMD: // 日付(yyyy/m/d)
						case WorkbookUtils.DATE_YYYYMMDD: // 日付(yyyy/mm/dd)
						case "8": // 日付(yyyy年mm月dd日)
							// 日付
							curCell.setCellType(CellType.STRING);
							curCell.setCellValue(WorkbookUtils.getDay(Integer.parseInt(curValue), s));
							break;
						case "4":
							//
							curCell.setCellType(CellType.STRING);

							break;
						case "5":
							// 時刻 (hh:mm:ss)
							curCell.setCellType(CellType.STRING);
							int sec = (int) (86400 * Double.parseDouble(curValue));
							curCell.setCellValue(WorkbookUtils.getSec(sec));
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

				}
				break;
			case "f":
				if (curValue != null && curValue.length() > 0) {
					curCell.setCellFormula(curValue);
				}
				break;
			default:
				log.debug("in endElement... qName=({})", qName);
				break;
			}
		}
	}

	protected void characters(char[] ch, int start, int length) throws SAXException {
		curValue += new String(ch, start, length);
	}

	private class SheetHandler extends DefaultHandler {

		private XSSFWorkbookReader self = XSSFWorkbookReader.this;

		private int level = 0;

		private boolean flag = false;

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes)
				throws SAXException {
			level++;
			if (level == 2) {
				flag = "sheetData".equals(qName);
			}
			if (flag) {
				self.startElement(uri, localName, qName, attributes);
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			level--;
			if (flag) {
				self.endElement(uri, localName, qName);
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if (flag) {
				self.characters(ch, start, length);
			}
		}

	}

}
