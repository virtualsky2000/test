package system.utils;

import java.io.File;
import java.util.List;

public class WorkbookReader extends AbstractWorkbookReader {

	public static WorkbookReader load(String fileName) {
		return load(FileUtils.getFile(fileName), null, 0);
	}

	public static WorkbookReader load(String fileName, int userMode) {
		return load(FileUtils.getFile(fileName), null, userMode);
	}

	public static WorkbookReader load(String fileName, List<String> lstSheetName) {
		return load(FileUtils.getFile(fileName), lstSheetName, 0);
	}

	public static WorkbookReader load(String fileName, List<String> lstSheetName, int userMode) {
		return load(FileUtils.getFile(fileName), lstSheetName, userMode);
	}

	public static WorkbookReader load(File file) {
		return load(file, null, 0);
	}

	public static WorkbookReader load(File file, int userMode) {
		return load(file, null, userMode);
	}

	public static WorkbookReader load(File file, List<String> lstSheetName) {
		return load(file, lstSheetName, 0);
	}

	public static WorkbookReader load(File file, List<String> lstSheetName, int userMode) {
		WorkbookReader reader = new WorkbookReader(file, lstSheetName, userMode);
		reader.load();

		return reader;
	}

	public WorkbookReader(File file, List<String> lstSheetName, int userMode) {
		this.file = file;
		this.userMode = userMode;
		this.lstSheetName = lstSheetName;
	}

	public void load() {
		if (isOfficeXml()) {
			workbook = XSSFWorkbookReader.load(file, lstSheetName, userMode).workbook;
		} else {
			workbook = HSSFWorkbookReader.load(file, lstSheetName, userMode).workbook;
		}
	}

}
