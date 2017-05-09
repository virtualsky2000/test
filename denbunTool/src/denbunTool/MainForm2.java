package denbunTool;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import system.exception.ApplicationException;
import system.utils.WorkbookReader;

public class MainForm2 extends JFrame {

    private static final String PatternPath = "第６章　ホストインタフェース設計\\6.3　電文パターン定義\\";
    private static final String DenbunPath = "第６章　ホストインタフェース設計\\6.4　電文フレーム定義\\";

    private static final Pattern pPattern = Pattern.compile("^6\\.3.+ホストインタフェース電文パターン定義\\..+");

    private JLabel label1;
    private JTextField textbox1;
    private JButton button1;
    private JLabel label2;
    private JTextField textbox2;
    private JButton button2;
    private JLabel lblType;
    private JComboBox<String> cboType;
    private JLabel lblPattern;
    private JComboBox<String> cboPattern;

    private JButton btnLoad;
    private JButton btnLoadData;
    private JButton btnInitData;
    private JButton btnMakeData;

    private JLabel lblFrame;
    private JComboBox<String> cboFrame;

    TableColumnModel tableColumnModel;
    DefaultTableModel tableModel;
    private JTable table;
    private JScrollPane scrollPane;

    private JPanel statusBar;
    private JLabel statusMsg;

    private int mode;

    private boolean eventFlag = false;

    private List<String[]> lstPattern;

    private Map<String, Integer> mapPatternIndex = new HashMap<>();
    private Map<String, List<String[]>> mapDenbunData = new HashMap<>();

    private int scrollBarPerPage;

    private JScrollBar scrollBar;

    public MainForm2() {
        super("擬似電文ツール");

        initComponet();
        initListener();
    }

    private void initComponet() {
        label1 = new JLabel("詳細設計パス：");
        label1.setBounds(20, 20, 150, 20);
        this.add(label1);

        textbox1 = new JTextField("D:\\30_詳細設計工程(C2)関連\\01_アプリ編");
        textbox1.setBounds(120, 20, 550, 20);
        this.add(textbox1);

        button1 = new JButton("参照");
        button1.setBounds(680, 20, 60, 20);
        this.add(button1);

        label2 = new JLabel("電文出力パス：");
        label2.setBounds(20, 50, 150, 20);
        this.add(label2);

        textbox2 = new JTextField("D:\\dev\\workspace\\third\\hostdumyfile");
        textbox2.setBounds(120, 50, 550, 20);
        this.add(textbox2);

        button2 = new JButton("参照");
        button2.setBounds(680, 50, 60, 20);
        this.add(button2);

        lblType = new JLabel("電文タイプ：");
        lblType.setBounds(20, 80, 80, 20);
        this.add(lblType);

        cboType = new JComboBox<>();
        cboType.setBounds(120, 80, 70, 20);
        this.add(cboType);

        lblPattern = new JLabel("電文パターン：");
        lblPattern.setBounds(210, 80, 100, 20);
        this.add(lblPattern);

        cboPattern = new JComboBox<>();
        cboPattern.setBounds(320, 80, 180, 20);
        this.add(cboPattern);

        btnLoad = new JButton("読込");
        btnLoad.setBounds(120, 110, 100, 20);
        this.add(btnLoad);

        btnLoadData = new JButton("ロード");
        btnLoadData.setBounds(240, 110, 100, 20);
        btnLoadData.setEnabled(false);
        this.add(btnLoadData);

        btnInitData = new JButton("初期化");
        btnInitData.setBounds(360, 110, 100, 20);
        btnInitData.setEnabled(false);
        this.add(btnInitData);

        btnMakeData = new JButton("作成");
        btnMakeData.setBounds(480, 110, 100, 20);
        btnMakeData.setEnabled(false);
        this.add(btnMakeData);

        lblFrame = new JLabel("フレーム識別子：");
        lblFrame.setBounds(20, 140, 110, 20);
        this.add(lblFrame);

        cboFrame = new JComboBox<>();
        cboFrame.setBounds(140, 140, 150, 20);
        this.add(cboFrame);

        String[] columnNames = { "項番", "日本語名称", "項目ID", "属性", "開始位置", "バイト数", "電文値" };
        tableModel = new DefaultTableModel(null, columnNames) {

            public boolean isCellEditable(int row, int column) {
                // 電文値列だけが編集できる
                return column == 6;
            }

        };
        table = new JTable(tableModel);
        tableColumnModel = table.getColumnModel();

        int[] columnWidth = new int[] { 50, 180, 160, 40, 60, 60, 150 };

        for (int i = 0, len = tableColumnModel.getColumnCount(); i < len; i++) {
            tableColumnModel.getColumn(i).setPreferredWidth(columnWidth[i]);
        }

        DefaultTableCellRenderer centerCellRenderer = new DefaultTableCellRenderer();
        centerCellRenderer.setHorizontalAlignment(JLabel.CENTER);

        tableColumnModel.getColumn(0).setCellRenderer(centerCellRenderer);
        tableColumnModel.getColumn(3).setCellRenderer(centerCellRenderer);
        tableColumnModel.getColumn(4).setCellRenderer(centerCellRenderer);
        tableColumnModel.getColumn(5).setCellRenderer(centerCellRenderer);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 170, 730, 350);
        this.add(scrollPane);

        scrollBar = scrollPane.getVerticalScrollBar();

        statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setLayout(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.setBackground(Color.LIGHT_GRAY);

        statusMsg = new JLabel(" ");
        statusBar.add(statusMsg, BorderLayout.WEST);
        this.setLayout(new BorderLayout());
        this.add(BorderLayout.SOUTH, statusBar);

        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setVisible(true);
    }

    private void initListener() {
        textbox1.addFocusListener(new FocusAdapter() {

            public void focusLost(FocusEvent e) {
                onTextbox1LostFocus(e);
            }

        });
        button1.addActionListener(e -> onViewInputPath(e));
        button2.addActionListener(e -> onViewOutputPath(e));

        cboType.addItemListener(e -> onTypeChanged(e));
        cboPattern.addItemListener(e -> onPatternChanged(e));
        cboFrame.addItemListener(e -> onFrameChanged(e));

        btnLoad.addActionListener(e -> onLoad(e));
        btnLoadData.addActionListener(e -> onLoadData(e));
        btnInitData.addActionListener(e -> onInitData(e));
        btnMakeData.addActionListener(e -> onMakeData(e));

        table.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                onTableKeyPressed(e);
            }

        });

        this.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                onWindowClosing(e);
            }

        });
    }

    private void onTextbox1LostFocus(FocusEvent e) {

    }

    private void openPath(JTextField textField) {
        JFileChooser chooser = new JFileChooser();
        File dir = new java.io.File(textField.getText());
        if (dir.isDirectory()) {
            chooser.setCurrentDirectory(dir);
        } else {
            chooser.setCurrentDirectory(new java.io.File("."));
        }
        chooser.setSelectedFile(new File(""));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (chooser.showOpenDialog(this) == JFileChooser.OPEN_DIALOG) {
            textField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void onTableKeyPressed(KeyEvent e) {
        if (tableModel.getRowCount() > 0) {
            scrollBarPerPage = scrollBar.getMaximum() / tableModel.getRowCount() * 20;
            System.out.println("onTableKeyPressed scrollBarPerPage=" + scrollBarPerPage);
        }
        switch (e.getKeyCode()) {
        case KeyEvent.VK_PAGE_DOWN:
            scrollBar.setValue(scrollBar.getValue() + scrollBarPerPage);
            break;
        case KeyEvent.VK_PAGE_UP:
            scrollBar.setValue(scrollBar.getValue() - scrollBarPerPage);
            //            int row = table.getSelectedRow();
            //            row -= 19;
            //            table.setRowSelectionInterval(row, row);
            break;
        }
        System.out.println(scrollBar.getValue());
    }

    private void onViewInputPath(ActionEvent e) {
        openPath(textbox1);
    }

    private void onViewOutputPath(ActionEvent e) {
        openPath(textbox2);
    }

    private void onTypeChanged(ItemEvent e) {
        try {
            if (e.getStateChange() != ItemEvent.SELECTED || !eventFlag) {
                return;
            }
            System.out.println("in onTypeChanged " + cboType.getSelectedItem());

            eventFlag = false;
            cboPattern.removeAllItems();
            cboPattern.addItem("");

            if (mode == 0) {
                // 照会の場合
                switch (cboType.getSelectedIndex()) {
                case 0:
                    // 全て
                    lstPattern.forEach(data -> {
                        cboPattern.addItem(data[0]);
                    });
                    break;
                case 1:
                    // 入力
                    lstPattern.forEach(data -> {
                        if (data[0].endsWith("_IN")) {
                            cboPattern.addItem(data[0]);
                        }
                    });
                    break;
                case 2:
                    // 出力
                    lstPattern.forEach(data -> {
                        if (data[0].endsWith("_OUT")) {
                            cboPattern.addItem(data[0]);
                        }
                    });
                    break;
                case 3:
                    // 横とび
                    lstPattern.forEach(data -> {
                        if (data[0].startsWith("H_Yktb")) {
                            cboPattern.addItem(data[0]);
                        }
                    });
                    break;
                }
            } else {
                // 自動車の場合
                switch (cboType.getSelectedIndex()) {
                case 0:
                    // 全て
                    lstPattern.forEach(data -> {
                        cboPattern.addItem((String) data[0]);
                    });
                    break;
                case 1:
                    // 要求
                    lstPattern.forEach(data -> {
                        if (data[0].endsWith("要求")) {
                            cboPattern.addItem(data[0]);
                        }
                    });
                    break;
                case 2:
                    // 結果
                    lstPattern.forEach(data -> {
                        if (data[0].endsWith("結果")) {
                            cboPattern.addItem(data[0]);
                        }
                    });
                    break;
                case 3:
                    // エラー
                    lstPattern.forEach(data -> {
                        if (data[0].endsWith("エラー")) {
                            cboPattern.addItem(data[0]);
                        }
                    });
                    break;
                }
            }

            eventFlag = true;
            cboPattern.setSelectedIndex(1);
            System.out.println("out onTypeChanged");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ex.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onPatternChanged(ItemEvent e) {
        try {
            if (e.getStateChange() != ItemEvent.SELECTED || !eventFlag) {
                return;
            }
            System.out.println("in onPatternChanged " + cboPattern.getSelectedItem());

            cboFrame.removeAllItems();

            int index = mapPatternIndex.get(cboPattern.getSelectedItem());
            String[] denbun = lstPattern.get(index);

            eventFlag = false;
            List<String> lstFrameId = new ArrayList<>();
            for (int i = 17; i <= 73; i += 4) {
                String strFrameId = denbun[i];
                if (strFrameId == null) {
                    break;
                }
                lstFrameId.add(strFrameId);
                loadDenbunFrame(strFrameId);
            }
            lstFrameId.forEach(item -> cboFrame.addItem(item));
            eventFlag = true;

            if (mode == 0 && cboFrame.getItemCount() > 2) {
                cboFrame.setSelectedIndex(2);
            } else {
                cboFrame.setSelectedIndex(cboFrame.getItemCount() - 1);
            }

            System.out.println("out onPatternChanged");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ex.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onFrameChanged(ItemEvent e) {
        try {
            if (e.getStateChange() != ItemEvent.SELECTED || !eventFlag) {
                return;
            }
            System.out.println("in onFrameChanged " + cboFrame.getSelectedItem());

            tableModel.getDataVector().clear();
            List<String[]> lstValue = mapDenbunData.get(cboFrame.getSelectedItem());

            if (lstValue != null && !lstValue.isEmpty()) {

                if (lstValue.get(0).length > 6) {
                    lstValue.forEach(item -> {

                        String[] rowData = new String[] { String.valueOf(tableModel.getRowCount() + 1), // 項番
                                item[0], // 日本語名称
                                item[14], // 項目ID
                                item[28], // 属性
                                item[31], // 開始位置.
                                item[35], // バイト数
                                "" // 電文値
                        };
                        tableModel.addRow(rowData);
                    });
                } else {
                    lstValue.forEach(item -> tableModel.addRow(item));
                }
            }

            if (tableModel.getRowCount() <= 20) {
                scrollBar.setVisible(false);
            } else {
                if (scrollBar != null) {
                    scrollBar.setValue(0);
                }
            }

            table.invalidate();

            btnLoadData.setEnabled(true);
            btnInitData.setEnabled(true);
            btnMakeData.setEnabled(true);

            System.out.println("out onFrameChanged");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ex.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showError(String errMsg, Exception ex) {
        ex.printStackTrace();
        statusMsg.setText(errMsg);
        JOptionPane.showMessageDialog(null, ex.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * 電文パターン定義読込
     */
    private void loadDenbunPattern() {
        try {
            File path = new File(textbox1.getText(), PatternPath);
            FilenameFilter filter = (dir, name) -> {
                return pPattern.matcher(name).find();
            };
            File[] files = path.listFiles(filter);

            if (files.length != 1) {
                throw new ApplicationException("電文パターン定義が見つかりませんでした。");
            }

            String strExt = FilenameUtils.getExtension(files[0].getName());
            if ("xlsm".equals(strExt)) {
                mode = 1;
            } else {
                mode = 0;
            }

            WorkbookReader reader = WorkbookReader.load(files[0], "電文パターン定義");
            Sheet sheet = reader.getSheet("電文パターン定義");
            int maxRow = reader.findEndRow(sheet, 5, 14);
            lstPattern = reader.readTextValue(sheet, 5, 14, maxRow - 1, 87);
            lstPattern.sort(new Comparator<Object[]>() {

                @Override
                public int compare(Object[] o1, Object[] o2) {
                    String id1 = (String) o1[0];
                    String id2 = (String) o2[0];

                    return id1.compareTo(id2);
                }

            });

            mapPatternIndex.clear();
            for (int i = 0, len = lstPattern.size(); i < len; i++) {
                Object[] data = lstPattern.get(i);
                mapPatternIndex.put((String) data[0], i);
            }

            eventFlag = false;
            cboType.removeAllItems();
            cboType.addItem("全て");
            if (mode == 0) {
                cboType.addItem("入力");
                cboType.addItem("出力");
                cboType.addItem("横とび");
            } else {
                cboType.addItem("要求");
                cboType.addItem("結果");
                cboType.addItem("エラー");
            }
            eventFlag = true;

            cboType.setSelectedIndex(2);

            statusMsg.setText("読込が正常終了しました。");
        } catch (Exception e) {
            showError("読込が異常終了しました。", e);
        }
    }

    /**
     * 電文定義読込
     * @param strFrameId 電文ID
     */
    private void loadDenbunFrame(String strFrameId) {
        if (!mapDenbunData.containsKey(strFrameId)) {
            if (mode == 0) {
                // 照会の場合
                loadSykFrame(strFrameId);
            } else {
                // 自動車の場合
                loadFrame(strFrameId);
            }
        }
    }

    private List<String[]> loadSyokaiHeader() {
        List<String[]> lstValue = new ArrayList<>();

        lstValue.add(new String[] { "1", "処理区分_1", "UTS10000_1", "C", "0", "1" });
        lstValue.add(new String[] { "2", "処理区分_2", "UTS10000_2", "C", "1", "1" });
        lstValue.add(new String[] { "3", "処理区分_3", "UTS10000_3", "C", "2", "1" });
        lstValue.add(new String[] { "4", "処理区分_4", "UTS10000_4", "C", "3", "1" });
        lstValue.add(new String[] { "5", "処理区分_5", "UTS10000_5", "C", "4", "1" });
        lstValue.add(new String[] { "6", "処理区分_6", "UTS10000_6", "C", "5", "1" });
        lstValue.add(new String[] { "7", "処理区分_7", "UTS10000_7", "C", "6", "1" });
        lstValue.add(new String[] { "8", "処理区分_8", "UTS10000_8", "C", "7", "1" });
        lstValue.add(new String[] { "9", "横とび先業務コード", "DTF47400", "C", "8", "5" });
        lstValue.add(new String[] { "10", "代理店コード", "UTI68300", "C", "13", "15" });
        lstValue.add(new String[] { "11", "証券番号", "UTI67200", "C", "28", "12" });
        lstValue.add(new String[] { "12", "枝番", "UTI67300", "C", "40", "5" });
        lstValue.add(new String[] { "13", "予備", "FILLER_1", "C", "45", "155" });

        return lstValue;
    }

    private void loadSykFrame(String strFrameId) {
        List<String[]> lstValue = null;
        if ("F_KyutuHd".equals(strFrameId)) {

        } else if ("F_SyukiKytu".equals(strFrameId)) {
            lstValue = loadSyokaiHeader();
        } else {
            final String strGamenId = ((String) cboPattern.getSelectedItem()).split("_")[1].toUpperCase();
            final char lastChar = strGamenId.charAt(strGamenId.length() - 1);

            File path = new File(textbox1.getText(), DenbunPath);
            Pattern pFrame1 = Pattern.compile(".*フレーム定義_.*" + strGamenId + ".*\\.xlsx");
            Pattern pFrame2 = Pattern.compile(".*フレーム定義_.*" + strGamenId.substring(0, 5) + "\\d~\\d.*\\.xlsx");
            FilenameFilter filter = (dir, name) -> {
                if (pFrame1.matcher(name).find()) {
                    if (name.endsWith("I.xlsx")) {
                        return strFrameId.endsWith("InInfo");
                    }
                    if (name.endsWith("O.xlsx")) {
                        return strFrameId.endsWith("OutInfo");
                    }

                    File f = new File(dir, name);
                    return !f.isHidden();
                } else if (lastChar >= '0' && lastChar <= '9' && pFrame2.matcher(name).find()) {
                    int pos = name.indexOf("~");
                    int min = Integer.parseInt(name.substring(pos - 1, pos));
                    int max = Integer.parseInt(name.substring(pos + 1, pos + 2));
                    int num = Integer.parseInt(strGamenId.substring(strGamenId.length() - 1));

                    return min <= num && num <= max;
                }
                return false;
            };
            File[] files = path.listFiles(filter);

            if (files.length != 1) {
                throw new ApplicationException("電文定義が見つかりませんでした。");
            }

            WorkbookReader reader;
            Sheet sheet = null;
            int maxRow = 0;
            String fileName = files[0].getName();

            reader = WorkbookReader.load(files[0]);
            if (fileName.endsWith("I.xlsx") || fileName.endsWith("O.xlsx")) {
                sheet = reader.getSheet(0);
            } else if (fileName.indexOf("~") < 0) {
                if (strFrameId.endsWith("_OUT")) {
                    sheet = reader.getSheet(0);
                } else {
                    sheet = reader.getSheet(1);
                }
            } else {
                Iterator<Sheet> ite = reader.sheetIterator();

                while (ite.hasNext()) {
                    sheet = ite.next();
                    if (strFrameId.equals(reader.getCellValue(sheet, "K6"))) {
                        break;
                    }
                }

            }
            maxRow = reader.findEndRow(sheet, "C8") + 1;
            lstValue = reader.readTextValue(sheet, "C8:AP" + maxRow);

        }
        mapDenbunData.put(strFrameId, lstValue);
    }

    private void loadFrame(String strFrameId) {

    }

    /**
     * 読込処理
     * @param e
     */
    private void onLoad(ActionEvent e) {
        statusMsg.setText("読込が開始しました．．．");

        Thread t = new Thread(() -> {
            loadDenbunPattern();
        });
        t.start();
    }

    /**
     * ロード処理
     * @param e
     */
    private void onLoadData(ActionEvent e) {
        try {
            File f = new File(textbox2.getText(), (String) cboPattern.getSelectedItem() + ".txt");
            if (!f.exists() || !f.isFile()) {
                throw new ApplicationException("ファイルが見つかりませんでした。");
            }

            statusMsg.setText("ロード中．．．");

            statusMsg.setText("ロードが完了しました。");
        } catch (Exception ex) {
            showError("ロードが異常終了しました。", ex);
        }
    }

    /**
     * 初期化処理
     * @param e
     */
    private void onInitData(ActionEvent e) {
        try {

            statusMsg.setText("初期化中．．．");

            statusMsg.setText("初期化が完了しました。");
        } catch (Exception ex) {
            showError("初期化が異常終了しました。", ex);
        }
    }

    /**
     * 作成処理
     * @param e
     */
    private void onMakeData(ActionEvent e) {
        try {

            statusMsg.setText("作成中．．．");

            statusMsg.setText("作成が完了しました。");
        } catch (Exception ex) {
            showError("作成が異常終了しました。", ex);
        }
    }

    private void onWindowClosing(WindowEvent e) {
        if (JOptionPane.showConfirmDialog(null, "閉じます、宜しいですか？", "確認",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            this.dispose();
        }
    }

    private void loadFlowInfo(String fileName) {

        WorkbookReader reader = WorkbookReader.load(fileName);
        //        Sheet sheet = reader.getSheet("リソース名");
        //
        //        int maxRow = reader.findEndRow(sheet, 3, 1);
        //        flowInfo = reader.readData(sheet, 3, 1, maxRow, 26);

        //        HSSFWorkbookReader reader = HSSFWorkbookReader.load(fileName, LabelSSTRecord.sid, NumberRecord.sid);
        Sheet sheet1 = reader.getSheet("Sheet1");
        sheet1.rowIterator().forEachRemaining(row -> row.cellIterator().forEachRemaining(cell -> printCell(cell)));

    }

    @SuppressWarnings("deprecation")
    private void printCell(Cell cell) {
        switch (cell.getCellTypeEnum()) {
        case NUMERIC:
            System.out.println(cell.getRowIndex() + "," + cell.getColumnIndex() + "=" + cell.getNumericCellValue());
            break;
        case STRING:
            System.out.println(cell.getRowIndex() + "," + cell.getColumnIndex() + "=" + cell.getStringCellValue());
            break;
        }
    }

    public static void main(String[] args) {
        new MainForm2();
    }

}
