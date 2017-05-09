package denbunTool;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;

import system.cache.PropertiesReaderCache;
import system.exception.ApplicationException;
import system.logging.LogManager;
import system.logging.Logger;
import system.utils.FileUtils;
import system.utils.WorkbookReader;

public class MainForm extends JFrame {

    private static final Charset Shift_JIS = Charset.forName("Shift-JIS");
    private static final String PatternPath = "第６章　ホストインタフェース設計\\6.3　電文パターン定義\\";
    private static final String DenbunPath = "第６章　ホストインタフェース設計\\6.4　電文フレーム定義\\";
    private static final Pattern pPattern = Pattern.compile("^6\\.3.+ホストインタフェース電文パターン定義\\..+");

    private static final Logger log = LogManager.getLogger(MainForm.class);

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

    private TableColumnModel tableColumnModel;
    private DefaultTableModel tableModel;
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

    public MainForm() {
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

        cboType.addActionListener(e -> onTypeChanged(e));
        cboPattern.addActionListener(e -> onPatternChanged(e));
        cboFrame.addActionListener(e -> onFrameChanged(e));

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
            log.info("onTableKeyPressed scrollBarPerPage=" + scrollBarPerPage);
        }
        switch (e.getKeyCode()) {
        case KeyEvent.VK_PAGE_DOWN:
            scrollBar.setValue(scrollBar.getValue() + scrollBarPerPage);
            break;
        case KeyEvent.VK_PAGE_UP:
            int row = table.getSelectedRow();
            scrollBar.setValue(scrollBar.getValue() - scrollBarPerPage);
            row += 1;
            table.setRowSelectionInterval(row, row);
            break;
        }
        log.info(scrollBar.getValue());
    }

    private void onViewInputPath(ActionEvent e) {
        openPath(textbox1);
    }

    private void onViewOutputPath(ActionEvent e) {
        openPath(textbox2);
    }

    private void onTypeChanged(ActionEvent e) {
        try {
            if (!eventFlag) {
                return;
            }
            log.info("in onTypeChanged " + cboType.getSelectedItem());

            eventFlag = false;
            cboPattern.removeAllItems();
            cboPattern.addItem("");

            if (mode == 0) {
                // 照会の場合
                switch (cboType.getSelectedIndex()) {
                case 0:
                    // 全て
                    lstPattern.forEach(data -> {
                        cboPattern.addItem(data[1]);
                    });
                    break;
                case 1:
                    // 入力
                    lstPattern.forEach(data -> {
                        if (data[1].endsWith("_IN")) {
                            cboPattern.addItem(data[1]);
                        }
                    });
                    break;
                case 2:
                    // 出力
                    lstPattern.forEach(data -> {
                        if (data[1].endsWith("_OUT")) {
                            cboPattern.addItem(data[1]);
                        }
                    });
                    break;
                case 3:
                    // 横とび
                    lstPattern.forEach(data -> {
                        if (data[1].startsWith("H_Yktb")) {
                            cboPattern.addItem(data[1]);
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
                        cboPattern.addItem((String) data[1]);
                    });
                    break;
                case 1:
                    // 要求
                    lstPattern.forEach(data -> {
                        if (data[0].endsWith("要求")) {
                            cboPattern.addItem(data[1]);
                        }
                    });
                    break;
                case 2:
                    // 結果
                    lstPattern.forEach(data -> {
                        if (data[0].endsWith("結果")) {
                            cboPattern.addItem(data[1]);
                        }
                    });
                    break;
                case 3:
                    // エラー
                    lstPattern.forEach(data -> {
                        if (data[0].endsWith("エラー")) {
                            cboPattern.addItem(data[1]);
                        }
                    });
                    break;
                }
            }

            eventFlag = true;
            cboPattern.setSelectedIndex(1);
            log.info("out onTypeChanged");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ex.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onPatternChanged(ActionEvent e) {
        try {
            if (!eventFlag) {
                return;
            }
            log.info("in onPatternChanged {}", cboPattern.getSelectedItem());

            cboFrame.removeAllItems();

            int index = mapPatternIndex.get(cboPattern.getSelectedItem());
            String[] denbun = lstPattern.get(index);

            eventFlag = false;
            List<String> lstFrameId = new ArrayList<>();
            for (int i = 6; i <= 20; i++) {
                String strFrameId = denbun[i];
                if (StringUtils.isEmpty(strFrameId)) {
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

            log.info("out onPatternChanged");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ex.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onFrameChanged(ActionEvent e) {
        try {
            if (!eventFlag) {
                return;
            }
            log.info("in onFrameChanged " + cboFrame.getSelectedItem());

            tableModel.getDataVector().clear();
            List<String[]> lstValue = mapDenbunData.get(cboFrame.getSelectedItem());

            if (lstValue != null && !lstValue.isEmpty()) {
                lstValue.forEach(item -> tableModel.addRow(item));
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

            log.info("out onFrameChanged");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ex.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showInfo(String strMsg) {
        statusMsg.setText(strMsg);
        JOptionPane.showMessageDialog(null, strMsg, "情報", JOptionPane.INFORMATION_MESSAGE);
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
            int maxRow = reader.findEndRow(sheet, "O6");
            lstPattern = reader.readTextValue(sheet, "C6:CJ" + maxRow,
                    new String[] { "C", "O", "U", "X", "AA", "AC", "AF", "AJ", "AN", "AR", "AV", "AZ", "BD", "BH", "BL",
                            "BP", "BT", "BX", "CB", "CF", "CJ" });
            lstPattern.sort(new Comparator<String[]>() {

                @Override
                public int compare(String[] o1, String[] o2) {
                    return o1[1].compareTo(o2[1]);
                }

            });

            mapPatternIndex.clear();
            for (int i = 0, len = lstPattern.size(); i < len; i++) {
                String[] data = lstPattern.get(i);
                mapPatternIndex.put(data[1], i);
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

            showInfo("読込が正常終了しました。");
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

    private List<String[]> loadCommonHeader() {
        try {
            List<String[]> lstValue = new ArrayList<>();

            FileInputStream fis = new FileInputStream(FileUtils.getFile("F_KyutuHd.txt"));
            BufferedReader br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));

            String strData;
            while ((strData = br.readLine()) != null) {
                lstValue.add(strData.split(",", -1));
            }
            br.close();

            return lstValue;
        } catch (Exception e) {
            throw new ApplicationException("loadCommonHeader error", e);
        }
    }

    private List<String[]> loadSyokaiHeader() {
        try {
            List<String[]> lstValue = new ArrayList<>();

            FileInputStream fis = new FileInputStream(FileUtils.getFile("F_SyukiKytu.txt"));
            BufferedReader br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));

            String strData;
            while ((strData = br.readLine()) != null) {
                lstValue.add(strData.split(",", -1));
            }
            br.close();

            return lstValue;
        } catch (Exception e) {
            throw new ApplicationException("loadSyokaiHeader error", e);
        }
    }

    private void loadSykFrame(String strFrameId) {
        List<String[]> lstValue = null;
        if ("F_KyutuHd".equals(strFrameId)) {
            lstValue = loadCommonHeader();
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
            maxRow = reader.findEndRow(sheet, "A8") + 1;
            lstValue = reader.readTextValue(sheet, "A8:AP" + maxRow,
                    new String[] { "A", "C", "Q", "AE", "AH", "AL", "AP" });
            lstValue.forEach(item -> item[6] = "");
        }
        mapDenbunData.put(strFrameId, lstValue);
    }

    private void loadFrame(String strFrameId) {
        List<String[]> lstValue = null;

        PropertiesReaderCache cache = PropertiesReaderCache.load("config.properties");

        if (!cache.containsKey(strFrameId)) {
            throw new ApplicationException("config.propertiesに「" + strFrameId + "」が定義されていません。");
        }

        String[] strName = cache.getProperty(strFrameId).split(",");
        File path = new File(textbox1.getText(), DenbunPath);
        String fileName = new File(path, strName[0]).getAbsolutePath();
        WorkbookReader reader = WorkbookReader.load(fileName, strName[1]);
        Sheet sheet = reader.getSheet(strName[1]);
        int maxRow = reader.findEndRow(sheet, "A8") + 1;
        lstValue = reader.readTextValue(sheet, "A8:AP" + maxRow,
                new String[] { "A", "C", "Q", "AE", "AH", "AL", "AP" });
        lstValue.forEach(item -> item[6] = "");

        mapDenbunData.put(strFrameId, lstValue);
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

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8));
            byte[] bData = br.readLine().getBytes(Shift_JIS);
            br.close();

            int offset = 0;
            for (int i = 0; i < cboFrame.getItemCount(); i++) {
                List<String[]> lstData = mapDenbunData.get(cboFrame.getItemAt(i));
                int total = 0;

                for (String[] item : lstData) {
                    int len = Integer.parseInt(item[5]);
                    item[6] = new String(bData, offset + Integer.parseInt(item[4]), len, Shift_JIS);
                    total += len;
                }

                offset += total;
                mapDenbunData.put(cboFrame.getItemAt(i), lstData);
            }

            loadData();

            showInfo("ロードが完了しました。");
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

    private void saveData() {
        String key = (String) cboFrame.getSelectedItem();
        List<String[]> lstData = mapDenbunData.get(key);

        for (int i = 0, len = lstData.size(); i < len; i++) {
            lstData.get(i)[6] = (String) tableModel.getValueAt(i, 6);
        }

        mapDenbunData.put(key, lstData);
    }

    private void loadData() {
        List<String[]> lstData = mapDenbunData.get(cboFrame.getSelectedItem());

        for (int i = 0, len = lstData.size(); i < len; i++) {
            tableModel.setValueAt(lstData.get(i)[6], i, 6);
        }
    }

    /**
     * 作成処理
     * @param e
     */
    private void onMakeData(ActionEvent e) {
        try {
            statusMsg.setText("作成中．．．");

            File f = new File(textbox2.getText(), (String) cboPattern.getSelectedItem() + ".txt");
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8));

            saveData();

            for (int i = 0; i < cboFrame.getItemCount(); i++) {
                List<String[]> lstData = mapDenbunData.get(cboFrame.getItemAt(i));

                for (String[] item : lstData) {
                    checkItem(item);
                    bw.write(item[6]);
                }
            }

            bw.close();

            showInfo("作成が完了しました。");
        } catch (Exception ex) {
            showError("作成が異常終了しました。", ex);
        }
    }

    private void checkItem(String[] item) {
        int maxLen = Integer.parseInt(item[5]);
        int len = item[6].getBytes(Shift_JIS).length;
        if (len < maxLen) {
            item[6] += StringUtils.repeat(" ", maxLen - len);
        } else if (len > maxLen) {
            throw new ApplicationException("レングスチェックエラー。");
        }
    }

    private void onWindowClosing(WindowEvent e) {
        if (JOptionPane.showConfirmDialog(null, "閉じます、宜しいですか？", "確認",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            this.dispose();
        }
    }

    public static void main(String[] args) {
        new MainForm();
    }

}
