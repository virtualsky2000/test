package system.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.eventusermodel.AbortableHSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.eventusermodel.HSSFUserException;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.CellRecord;
import org.apache.poi.hssf.record.EOFRecord;
import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.hssf.record.FontRecord;
import org.apache.poi.hssf.record.FormatRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.HyperlinkRecord;
import org.apache.poi.hssf.record.LabelRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RowRecord;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.record.StringRecord;
import org.apache.poi.hssf.record.StyleRecord;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellType;

import system.exception.ApplicationException;
import system.logging.LogManager;
import system.logging.Logger;

public class HSSFWorkbookReader extends AbstractWorkbookReader {

	private static final Logger log = LogManager.getLogger(HSSFWorkbookReader.class);

	private SSTRecord sstRecord;

	private HSSFSheet curSheet;

	private HSSFRow curRow;

	private int curSheetIndex;

	private FormulaRecord stringFormulaRecord;

	private short previousSid;

	private String curSheetName;

	private List<String> sheetNames = new ArrayList<>();

	public static HSSFWorkbookReader load(String fileName) {
		return load(FileUtils.getFile(fileName), null, 0);
	}

	public static HSSFWorkbookReader load(String fileName, int userMode) {
		return load(FileUtils.getFile(fileName), null, userMode);
	}

	public static HSSFWorkbookReader load(String fileName, List<String> lstSheetName) {
		return load(FileUtils.getFile(fileName), lstSheetName, 0);
	}

	public static HSSFWorkbookReader load(String fileName, List<String> lstSheetName, int userMode) {
		return load(FileUtils.getFile(fileName), lstSheetName, userMode);
	}

	public static HSSFWorkbookReader load(File file) {
		return load(file, null, 0);
	}

	public static HSSFWorkbookReader load(File file, int userMode) {
		return load(file, null, userMode);
	}

	public static HSSFWorkbookReader load(File file, List<String> lstSheetName) {
		return load(file, lstSheetName, 0);
	}

	public static HSSFWorkbookReader load(File file, List<String> lstSheetName, int userMode) {
		HSSFWorkbookReader reader = new HSSFWorkbookReader(file, lstSheetName, userMode);
		reader.load();

		return reader;
	}

	public HSSFWorkbookReader(File file, List<String> lstSheetName, int userMode) {
		this.file = file;
		this.userMode = userMode;
		this.lstSheetName = lstSheetName;
	}

	public void load() {
		workbook = new HSSFWorkbook();

		try {
			AbortableHSSFListener listener = new AbortableHSSFListener() {

				@Override
				public short abortableProcessRecord(Record paramRecord) throws HSSFUserException {
					return HSSFWorkbookReader.this.abortableProcessRecord(paramRecord);
				}

			};

			HSSFRequest request = new HSSFRequest();
			request.addListenerForAllRecords(listener);
			HSSFEventFactory factory = new HSSFEventFactory();
			POIFSFileSystem poifs = new POIFSFileSystem(file);
			factory.abortableProcessWorkbookEvents(request, poifs);
		} catch (HSSFUserException | IOException e) {
			throw new ApplicationException(e);
		}
	}

	public void load(short... sids) {
		workbook = new HSSFWorkbook();

		try {
			POIFSFileSystem poifs = new POIFSFileSystem(file);

			HSSFRequest request = new HSSFRequest();

			request.addListener(record -> processBOFRecord((BOFRecord) record), BOFRecord.sid);
			request.addListener(record -> processEOFRecord((EOFRecord) record), EOFRecord.sid);
			request.addListener(record -> processBoundSheetRecord((BoundSheetRecord) record), BoundSheetRecord.sid);
			request.addListener(record -> processSSTRecord((SSTRecord) record), SSTRecord.sid);

			for (short sid : sids) {

				switch (sid) {
				case RowRecord.sid:
					request.addListener(record -> processRowRecord((RowRecord) record), RowRecord.sid);
					break;
				case LabelSSTRecord.sid:
					request.addListener(record -> processLabelSSTRecord((LabelSSTRecord) record), LabelSSTRecord.sid);
					break;
				case NumberRecord.sid:
					request.addListener(record -> processNumberRecord((NumberRecord) record), NumberRecord.sid);
					break;
				case FormulaRecord.sid:
					request.addListener(record -> processFormulaRecord((FormulaRecord) record), FormulaRecord.sid);
					break;
				case StringRecord.sid:
					request.addListener(record -> processStringRecord((StringRecord) record), StringRecord.sid);
					break;
				}

			}

			HSSFEventFactory factory = new HSSFEventFactory();

			factory.abortableProcessWorkbookEvents(request, poifs);
		} catch (HSSFUserException | IOException e) {
			throw new ApplicationException(e);
		}
	}

	protected void processBOFRecord(BOFRecord record) {
		int type = record.getType();
		if (type == BOFRecord.TYPE_WORKBOOK) {
			curSheetIndex = -1;
			log.debug("in processBOFRecord");
		} else if (type == BOFRecord.TYPE_WORKSHEET) {
			curSheetIndex++;
			curSheetName = sheetNames.get(curSheetIndex);
			curSheet = (HSSFSheet) workbook.createSheet(curSheetName);
			log.debug("in processBOFRecord {}", curSheetName);
		}
	}

	protected void processEOFRecord(EOFRecord record) {
		if (curSheetName != null) {
			log.debug("in processEOFRecord {}", curSheetName);
		} else {
			log.debug("in processEOFRecord");
		}
		curSheet = null;
		curSheetName = null;
	}

	protected void processBoundSheetRecord(BoundSheetRecord record) {
		log.debug("in processBoundSheetRecord");
		sheetNames.add(record.getSheetname());
	}

	protected void processSSTRecord(SSTRecord record) {
		log.debug("in processSSTRecord");
		sstRecord = record;
	}

	protected void processRowRecord(RowRecord record) {
		log.debug("in processRowRecord");
		curSheet.createRow(record.getRowNumber());
	}

	protected void processFontRecord(FontRecord record) {
		log.debug("in processFontRecord");
	}

	protected void processStyleRecord(StyleRecord record) {
		log.debug("in processStyleRecord");
	}

	protected void processFormatRecord(FormatRecord record) {
		log.debug("in processFormatRecord");
	}

	protected void processExtendedFormatRecord(ExtendedFormatRecord record) {
		log.debug("in processExtendedFormatRecord");
	}

	protected void processLabelSSTRecord(LabelSSTRecord record) {
		log.debug("in processLabelSSTRecord");
		HSSFCell cell = newCell(record);
		cell.setCellType(CellType.STRING);
		cell.setCellValue(sstRecord.getString(record.getSSTIndex()).getString());
	}

	protected void processNumberRecord(NumberRecord record) {
		log.debug("in processNumberRecord");
		HSSFCell cell = newCell(record);

		switch (record.getXFIndex()) {
		case 65:
			cell.setCellType(CellType.STRING);
			cell.setCellValue(WorkbookUtils.getDay((int) record.getValue(), "3"));
			break;
		case 66:
		case 67:
			cell.setCellType(CellType.STRING);
			cell.setCellValue(WorkbookUtils.getSec((int) (86400 * record.getValue())));
			break;
		default:
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(record.getValue());
		}
	}

	protected void processFormulaRecord(FormulaRecord record) {
		log.debug("in processFormulaRecord");
		if (record.hasCachedResultString()) {
			// The String itself should be the next record
			stringFormulaRecord = record;
		} else {
			// addTextCell(record, formatListener.formatNumberDateCell(formula));
		}
	}

	protected void processStringRecord(StringRecord record) {
		log.debug("in processStringRecord");
		if (previousSid == FormulaRecord.sid) {
			// Cached string value of a string formula
			HSSFCell cell = newCell(stringFormulaRecord);
			cell.setCellType(CellType.STRING);
			cell.setCellValue(record.getString());
		} else {
			// Some other string not associated with a cell, skip
		}
	}

	protected void processLabelRecord(LabelRecord record) {
		log.debug("in processLabelRecord");
	}

	protected void processHyperlinkRecord(HyperlinkRecord record) {
		log.debug("in processHyperlinkRecord");
	}

	protected short abortableProcessRecord(Record record) throws HSSFUserException {
		short sid = record.getSid();
		switch (sid) {
		case BOFRecord.sid:
			// start of workbook, worksheet etc. records
			processBOFRecord((BOFRecord) record);
			break;
		case EOFRecord.sid:
			// end of workbook, worksheet etc. records
			processEOFRecord((EOFRecord) record);
			break;
		case BoundSheetRecord.sid:
			// Worksheet index record
			processBoundSheetRecord((BoundSheetRecord) record);
			break;
		case SSTRecord.sid:
			// holds all the strings for LabelSSTRecords
			processSSTRecord((SSTRecord) record);
			break;
		case RowRecord.sid:
			if (!lstSheetName.contains(curSheetName)) {
				break;
			}
			processRowRecord((RowRecord) record);
			break;
		case FontRecord.sid:
			processFontRecord((FontRecord) record);
			break;
		case StyleRecord.sid:
			processStyleRecord((StyleRecord) record);
			break;
		case FormatRecord.sid:
			processFormatRecord((FormatRecord) record);
			break;
		case ExtendedFormatRecord.sid:
			processExtendedFormatRecord((ExtendedFormatRecord) record);
			break;
		case LabelSSTRecord.sid:
			// Ref. a string in the shared string table
			if (!lstSheetName.contains(curSheetName)) {
				break;
			}
			processLabelSSTRecord((LabelSSTRecord) record);
			break;
		case NumberRecord.sid:
			// Contains a numeric cell value
			if (!lstSheetName.contains(curSheetName)) {
				break;
			}
			processNumberRecord((NumberRecord) record);
			break;
		case FormulaRecord.sid:
			// Cell value from a formula
			processFormulaRecord((FormulaRecord) record);
			break;
		case StringRecord.sid:
			processStringRecord((StringRecord) record);
			break;
		case LabelRecord.sid:
			processLabelRecord((LabelRecord) record);
			break;
		case HyperlinkRecord.sid:
			// holds a URL associated with a cell
			processHyperlinkRecord((HyperlinkRecord) record);
			break;
		default:
			// log.debug("sid=" + sid);
			// log.debug(record);
			break;
		}

		return 0;

	}

	private HSSFCell newCell(CellRecord record) {
		int rowIndex = record.getRow();
		curRow = curSheet.getRow(rowIndex);
		if (curRow == null) {
			curRow = curSheet.createRow(rowIndex);
		}

		return curRow.createCell(record.getColumn());
	}

}
