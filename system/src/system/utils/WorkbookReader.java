package system.utils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import system.exception.ApplicationException;

public class WorkbookReader extends AbstractWorkbookReader {

    private final static List<String> lstExtName = Arrays.asList("xls", "xlt");

    private final static List<String> lstXmlExtName = Arrays.asList("xlsx", "xlsm", "xltm", "xltx");

    private String[] sheetNames = null;

    protected int type;

    public static WorkbookReader load(String fileName) {
        return load(FileUtils.getFile(fileName));
    }

    public static WorkbookReader load(File file) {
        WorkbookReader reader = new WorkbookReader(file);
        reader.load();

        return reader;
    }

    public static WorkbookReader load(String fileName, String... sheetNames) {
        return load(FileUtils.getFile(fileName), sheetNames);
    }

    public static WorkbookReader load(File file, String... sheetNames) {
        WorkbookReader reader = new WorkbookReader(file, sheetNames);
        reader.load();

        return reader;
    }

    public WorkbookReader(File file) {
        this.file = file;
        this.fileName = file.getAbsolutePath();
        String extName = FilenameUtils.getExtension(fileName);

        if (lstXmlExtName.contains(extName)) {
            type = 1;
        } else if (lstExtName.contains(extName)) {
            type = 0;
        } else {
            throw new ApplicationException("unsupportted file format");
        }
    }

    public WorkbookReader(File file, String... sheetNames) {
        this(file);
        this.sheetNames = sheetNames;
    }

    public void load() {
        if (type == 1) {
            workbook = XSSFWorkbookReader.load(file, sheetNames).workbook;
        } else {
            workbook = HSSFWorkbookReader.load(file, sheetNames).workbook;
        }
    }

}
