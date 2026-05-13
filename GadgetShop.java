import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

public class GadgetShop extends JFrame implements ActionListener {

    private ArrayList<Gadget> gadgets;

    // ── Input fields ─────────────────────────────────────────────────────────
    private JTextField tfModel, tfPrice, tfWeight, tfSize;
    private JTextField tfCredit, tfPhoneNumber, tfDuration;
    private JTextField tfMemory, tfDownloadSize;
    private JTextField tfDisplayNumber;

    // ── Output & status ──────────────────────────────────────────────────────
    private JTextArea  taOutput;
    private JLabel     lblStatus;
    private JLabel     lblCount;
    private DefaultListModel<String> gadgetListModel;
    private JList<String>            gadgetJList;

    // ── Buttons ──────────────────────────────────────────────────────────────
    private JButton btnAddMobile, btnAddMP3, btnDisplayAll;
    private JButton btnMakeCall,  btnAddCredit;
    private JButton btnDownloadMusic, btnDeleteMusic;
    private JButton btnClear;
    private JButton btnFull, btnCompact;

    // ── Dark palette ─────────────────────────────────────────────────────────
    private static final Color BG_BASE    = new Color(15,  17,  26);   // deepest bg
    private static final Color BG_SURFACE = new Color(22,  26,  40);   // card/panel
    private static final Color BG_RAISED  = new Color(30,  36,  54);   // raised element
    private static final Color BG_INPUT   = new Color(12,  14,  22);   // input fields
    private static final Color BORDER_COL = new Color(45,  55,  80);   // subtle borders

    private static final Color ACC_CYAN   = new Color( 0, 212, 180);   // primary accent
    private static final Color ACC_PURPLE = new Color(120,  80, 230);   // mobile
    private static final Color ACC_AMBER  = new Color(240, 165,  30);   // mobile actions
    private static final Color ACC_GREEN  = new Color( 40, 200, 100);   // mp3
    private static final Color ACC_RED    = new Color(230,  60,  70);   // danger

    private static final Color TXT_PRIMARY = new Color(215, 220, 240);
    private static final Color TXT_MUTED   = new Color(100, 115, 155);
    private static final Color TXT_DIM     = new Color( 60,  72, 105);

    private static final Font  FONT_MONO   = new Font("Monospaced", Font.PLAIN, 12);
    private static final Font  FONT_BODY   = new Font("SansSerif",  Font.PLAIN, 12);
    private static final Font  FONT_BOLD   = new Font("SansSerif",  Font.BOLD,  12);
    private static final Font  FONT_TITLE  = new Font("SansSerif",  Font.BOLD,  20);
    private static final Font  FONT_SMALL  = new Font("SansSerif",  Font.PLAIN, 11);

    private static final Dimension DIM_FULL    = new Dimension(1100, 760);
    private static final Dimension DIM_COMPACT = new Dimension(800,  590);

    // ═════════════════════════════════════════════════════════════════════════
    public GadgetShop() {
        gadgets          = new ArrayList<>();
        gadgetListModel  = new DefaultListModel<>();
        buildUI();
    }

    // ─── BUILD UI ─────────────────────────────────────────────────────────────
    private void buildUI() {
        setTitle("Gadget Shop — CS4001");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(DIM_FULL);
        setMinimumSize(DIM_COMPACT);
        setResizable(true);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_BASE);

        setLayout(new BorderLayout(0, 0));
        add(buildTopBar(),   BorderLayout.NORTH);
        add(buildMain(),     BorderLayout.CENTER);
        add(buildStatusBar(),BorderLayout.SOUTH);
    }

    // ─── TOP BAR ──────────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout(0, 0));
        bar.setBackground(BG_SURFACE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COL),
                new EmptyBorder(10, 20, 10, 20)));

        // Left: logo + title
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        JLabel logo = new JLabel("◈");
        logo.setFont(new Font("SansSerif", Font.BOLD, 22));
        logo.setForeground(ACC_CYAN);

        JLabel title = new JLabel("Gadget Shop");
        title.setFont(FONT_TITLE);
        title.setForeground(TXT_PRIMARY);

        JLabel badge = new JLabel("CS4001");
        badge.setFont(FONT_SMALL);
        badge.setForeground(BG_SURFACE);
        badge.setBackground(ACC_CYAN);
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(2, 7, 2, 7));

        left.add(logo);
        left.add(title);
        left.add(badge);

        // Right: count + view toggles
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        lblCount = new JLabel("0 items");
        lblCount.setFont(FONT_SMALL);
        lblCount.setForeground(TXT_MUTED);

        btnFull    = topBarBtn("⛶  Full",    ACC_CYAN);
        btnCompact = topBarBtn("▭  Compact", TXT_MUTED);

        right.add(lblCount);
        right.add(Box.createHorizontalStrut(10));
        right.add(btnFull);
        right.add(btnCompact);

        bar.add(left,  BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JButton topBarBtn(String text, Color fg) {
        JButton b = new JButton(text);
        b.setFont(FONT_SMALL);
        b.setForeground(fg);
        b.setBackground(BG_RAISED);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(5, 12, 5, 12));
        b.addActionListener(this);
        return b;
    }

    // ─── MAIN AREA (three-column) ─────────────────────────────────────────────
    private JPanel buildMain() {
        JPanel main = new JPanel(new BorderLayout(8, 8));
        main.setBackground(BG_BASE);
        main.setBorder(new EmptyBorder(10, 10, 6, 10));

        // Left column: gadget list sidebar
        main.add(buildSidebar(),     BorderLayout.WEST);
        // Center: tabbed input + action buttons
        main.add(buildCenterPanel(), BorderLayout.CENTER);
        // Right: console output
        main.add(buildConsole(),     BorderLayout.EAST);

        return main;
    }

    // ─── SIDEBAR ──────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel panel = darkCard(180, -1);
        panel.setLayout(new BorderLayout(0, 6));

        // Header
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setOpaque(false);
        hdr.setBorder(new EmptyBorder(0, 0, 6, 0));

        JLabel lbl = new JLabel("▦  Inventory");
        lbl.setFont(FONT_BOLD);
        lbl.setForeground(ACC_CYAN);
        hdr.add(lbl, BorderLayout.WEST);
        panel.add(hdr, BorderLayout.NORTH);

        // List
        gadgetJList = new JList<>(gadgetListModel);
        gadgetJList.setBackground(BG_INPUT);
        gadgetJList.setForeground(TXT_PRIMARY);
        gadgetJList.setFont(FONT_SMALL);
        gadgetJList.setSelectionBackground(new Color(40, 55, 90));
        gadgetJList.setSelectionForeground(ACC_CYAN);
        gadgetJList.setBorder(new EmptyBorder(4, 6, 4, 6));
        gadgetJList.setFixedCellHeight(26);

        JScrollPane scroll = darkScroll(gadgetJList);
        panel.add(scroll, BorderLayout.CENTER);

        // Index field
        JPanel idxRow = new JPanel(new BorderLayout(6, 0));
        idxRow.setOpaque(false);
        idxRow.setBorder(new EmptyBorder(6, 0, 0, 0));
        JLabel idxLbl = new JLabel("#");
        idxLbl.setFont(FONT_BOLD);
        idxLbl.setForeground(TXT_MUTED);
        idxLbl.setPreferredSize(new Dimension(14, 24));
        tfDisplayNumber = darkField();
        tfDisplayNumber.setToolTipText("Gadget index for actions");
        idxRow.add(idxLbl,           BorderLayout.WEST);
        idxRow.add(tfDisplayNumber,  BorderLayout.CENTER);
        panel.add(idxRow, BorderLayout.SOUTH);

        return panel;
    }

    // ─── CENTER PANEL (tabs + action buttons) ────────────────────────────────
    private JPanel buildCenterPanel() {
        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setBackground(BG_BASE);

        // Tabbed input area
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(BG_SURFACE);
        tabs.setForeground(TXT_PRIMARY);
        tabs.setFont(FONT_BOLD);
        UIManager.put("TabbedPane.selected",    BG_RAISED);
        UIManager.put("TabbedPane.background",  BG_SURFACE);
        UIManager.put("TabbedPane.foreground",  TXT_PRIMARY);
        UIManager.put("TabbedPane.contentAreaColor", BG_SURFACE);
        UIManager.put("TabbedPane.light",       BORDER_COL);
        UIManager.put("TabbedPane.darkShadow",  BG_BASE);
        UIManager.put("TabbedPane.shadow",      BORDER_COL);
        UIManager.put("TabbedPane.highlight",   BG_RAISED);
        tabs.updateUI();

        tabs.addTab("⚙  General",  buildGeneralTab());
        tabs.addTab("📱  Mobile",   buildMobileTab());
        tabs.addTab("🎵  MP3",      buildMP3Tab());

        tabs.setBackgroundAt(0, BG_SURFACE);
        tabs.setForegroundAt(0, ACC_CYAN);
        tabs.setBackgroundAt(1, BG_SURFACE);
        tabs.setForegroundAt(1, ACC_PURPLE);
        tabs.setBackgroundAt(2, BG_SURFACE);
        tabs.setForegroundAt(2, ACC_GREEN);

        center.add(tabs, BorderLayout.CENTER);
        center.add(buildActionPanel(), BorderLayout.SOUTH);
        return center;
    }

    private JScrollPane buildGeneralTab() {
        JPanel p = tabPanel();
        sectionLabel(p, "Gadget Details", ACC_CYAN);
        addRow(p, "Model",       tfModel  = darkField());
        addRow(p, "Price (£)",   tfPrice  = darkField());
        addRow(p, "Weight (g)",  tfWeight = darkField());
        addRow(p, "Size",        tfSize   = darkField());
        return wrapTab(p);
    }

    private JScrollPane buildMobileTab() {
        JPanel p = tabPanel();
        sectionLabel(p, "Add Mobile Phone", ACC_PURPLE);
        addRow(p, "Initial Credit (min)", tfCredit      = darkField());
        vGap(p, 16);
        sectionLabel(p, "Phone Actions", ACC_AMBER);
        addRow(p, "Phone Number",         tfPhoneNumber = darkField());
        addRow(p, "Call Duration (min)",  tfDuration    = darkField());
        return wrapTab(p);
    }

    private JScrollPane buildMP3Tab() {
        JPanel p = tabPanel();
        sectionLabel(p, "Add MP3 Player", ACC_GREEN);
        addRow(p, "Available Memory (MB)", tfMemory       = darkField());
        vGap(p, 16);
        sectionLabel(p, "Music Actions", new Color(100, 200, 255));
        addRow(p, "Track Size (MB)",       tfDownloadSize = darkField());
        return wrapTab(p);
    }

    // ─── ACTION PANEL ─────────────────────────────────────────────────────────
    private JPanel buildActionPanel() {
        JPanel outer = new JPanel();
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBackground(BG_BASE);

        // Row 1: add & view
        JPanel r1 = actionRow();
        rowLabel(r1, "ADD");
        btnAddMobile  = actionBtn("+ Mobile",    ACC_PURPLE);
        btnAddMP3     = actionBtn("+ MP3",       ACC_GREEN);
        btnDisplayAll = actionBtn("Display All", ACC_CYAN);
        r1.add(btnAddMobile);
        r1.add(Box.createHorizontalStrut(6));
        r1.add(btnAddMP3);
        r1.add(Box.createHorizontalStrut(14));
        r1.add(btnDisplayAll);

        // Row 2: mobile & mp3 actions
        JPanel r2 = actionRow();
        rowLabel(r2, "ACT");
        btnMakeCall      = actionBtn("Make Call",      ACC_AMBER);
        btnAddCredit     = actionBtn("Add Credit",     ACC_AMBER);
        btnDownloadMusic = actionBtn("↓ Download",     new Color(80, 160, 255));
        btnDeleteMusic   = actionBtn("✕ Delete Track", new Color(180, 80, 210));
        r2.add(btnMakeCall);
        r2.add(Box.createHorizontalStrut(6));
        r2.add(btnAddCredit);
        r2.add(Box.createHorizontalStrut(14));
        r2.add(btnDownloadMusic);
        r2.add(Box.createHorizontalStrut(6));
        r2.add(btnDeleteMusic);

        // Clear button (full width)
        btnClear = new JButton("⌫   Clear All Fields & Output");
        btnClear.setFont(FONT_BOLD);
        btnClear.setBackground(new Color(60, 18, 22));
        btnClear.setForeground(ACC_RED);
        btnClear.setFocusPainted(false);
        btnClear.setBorderPainted(false);
        btnClear.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClear.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(80, 25, 30)),
                new EmptyBorder(8, 0, 8, 0)));
        btnClear.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnClear.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnClear.addActionListener(this);

        outer.add(r1);
        outer.add(Box.createRigidArea(new Dimension(0, 4)));
        outer.add(r2);
        outer.add(Box.createRigidArea(new Dimension(0, 6)));
        outer.add(btnClear);
        return outer;
    }

    private JPanel actionRow() {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setBackground(BG_SURFACE);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COL),
                new EmptyBorder(7, 10, 7, 10)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        return row;
    }

    private void rowLabel(JPanel row, String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Monospaced", Font.BOLD, 10));
        l.setForeground(TXT_DIM);
        l.setPreferredSize(new Dimension(36, 24));
        l.setMinimumSize(new Dimension(36, 24));
        l.setMaximumSize(new Dimension(36, 24));
        row.add(l);
        row.add(Box.createHorizontalStrut(6));
    }

    private JButton actionBtn(String text, Color accent) {
        JButton b = new JButton(text);
        b.setFont(FONT_BOLD);
        b.setBackground(darken(accent, 0.15f));
        b.setForeground(accent);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(darken(accent, 0.4f), 1),
                new EmptyBorder(5, 14, 5, 14)));
        b.addActionListener(this);

        b.addMouseListener(new MouseAdapter() {
            final Color normalBg = b.getBackground();
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(darken(accent, 0.25f)); }
            @Override public void mouseExited(MouseEvent e)  { b.setBackground(normalBg); }
        });
        return b;
    }

    // ─── CONSOLE OUTPUT ────────────────────────────────────────────────────────
    private JPanel buildConsole() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setPreferredSize(new Dimension(280, 0));
        panel.setBackground(BG_SURFACE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COL, 1),
                new EmptyBorder(0, 0, 0, 0)));

        // Console header
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setBackground(BG_RAISED);
        hdr.setBorder(new EmptyBorder(8, 12, 8, 12));

        JPanel dots = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        dots.setOpaque(false);
        dots.add(dot(new Color(255, 90, 80)));
        dots.add(dot(new Color(255, 185, 0)));
        dots.add(dot(new Color(50, 200, 80)));

        JLabel lbl = new JLabel("Console Output");
        lbl.setFont(FONT_BOLD);
        lbl.setForeground(TXT_MUTED);

        hdr.add(dots, BorderLayout.WEST);
        hdr.add(lbl,  BorderLayout.EAST);
        panel.add(hdr, BorderLayout.NORTH);

        taOutput = new JTextArea();
        taOutput.setEditable(false);
        taOutput.setFont(FONT_MONO);
        taOutput.setBackground(BG_INPUT);
        taOutput.setForeground(new Color(170, 255, 200));
        taOutput.setCaretColor(ACC_CYAN);
        taOutput.setLineWrap(true);
        taOutput.setWrapStyleWord(true);
        taOutput.setBorder(new EmptyBorder(10, 12, 10, 12));

        JScrollPane scroll = darkScroll(taOutput);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JLabel dot(Color c) {
        JLabel d = new JLabel("●");
        d.setFont(new Font("SansSerif", Font.PLAIN, 10));
        d.setForeground(c);
        return d;
    }

    // ─── STATUS BAR ───────────────────────────────────────────────────────────
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_SURFACE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COL),
                new EmptyBorder(4, 14, 4, 14)));
        lblStatus = new JLabel("● Ready");
        lblStatus.setFont(FONT_SMALL);
        lblStatus.setForeground(ACC_CYAN);
        bar.add(lblStatus, BorderLayout.WEST);

        JLabel hint = new JLabel("Enter gadget # in sidebar to target an action");
        hint.setFont(FONT_SMALL);
        hint.setForeground(TXT_DIM);
        bar.add(hint, BorderLayout.EAST);
        return bar;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  HELPERS — dark UI widgets
    // ═════════════════════════════════════════════════════════════════════════

    private JPanel darkCard(int prefW, int prefH) {
        JPanel p = new JPanel();
        p.setBackground(BG_SURFACE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COL, 1),
                new EmptyBorder(10, 10, 10, 10)));
        if (prefW > 0) p.setPreferredSize(new Dimension(prefW, prefH > 0 ? prefH : 100));
        return p;
    }

    private JTextField darkField() {
        JTextField f = new JTextField();
        f.setBackground(BG_INPUT);
        f.setForeground(TXT_PRIMARY);
        f.setCaretColor(ACC_CYAN);
        f.setFont(FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COL, 1),
                new EmptyBorder(4, 8, 4, 8)));
        f.setSelectionColor(new Color(40, 80, 140));
        return f;
    }

    private JScrollPane darkScroll(JComponent view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_COL, 1));
        sp.setBackground(BG_INPUT);
        sp.getViewport().setBackground(BG_INPUT);
        sp.getVerticalScrollBar().setUnitIncrement(14);
        sp.getVerticalScrollBar().setBackground(BG_SURFACE);
        sp.getHorizontalScrollBar().setBackground(BG_SURFACE);
        return sp;
    }

    private JPanel tabPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG_SURFACE);
        p.setBorder(new EmptyBorder(14, 16, 14, 16));
        return p;
    }

    private JScrollPane wrapTab(JPanel p) {
        JScrollPane sp = new JScrollPane(p);
        sp.setBorder(null);
        sp.setBackground(BG_SURFACE);
        sp.getViewport().setBackground(BG_SURFACE);
        sp.getVerticalScrollBar().setUnitIncrement(12);
        return sp;
    }

    private void sectionLabel(JPanel p, String text, Color accent) {
        JPanel row = new JPanel(new BorderLayout(0, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, darken(accent, 0.5f)));

        JLabel lbl = new JLabel("  " + text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(accent);
        row.add(lbl, BorderLayout.WEST);
        p.add(row);
        p.add(Box.createRigidArea(new Dimension(0, 8)));
    }

    private void addRow(JPanel p, String label, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setBorder(new EmptyBorder(2, 0, 2, 0));

        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_BODY);
        lbl.setForeground(TXT_MUTED);
        lbl.setPreferredSize(new Dimension(160, 26));

        row.add(lbl,   BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        p.add(row);
        p.add(Box.createRigidArea(new Dimension(0, 4)));
    }

    private void vGap(JPanel p, int h) {
        p.add(Box.createRigidArea(new Dimension(0, h)));
    }

    private Color darken(Color c, float factor) {
        return new Color(
                Math.max(0, (int)(c.getRed()   * (1 - factor))),
                Math.max(0, (int)(c.getGreen() * (1 - factor))),
                Math.max(0, (int)(c.getBlue()  * (1 - factor))));
    }

    private void refreshCount() {
        int n = gadgets.size();
        lblCount.setText(n + " item" + (n == 1 ? "" : "s"));
    }

    private void setStatus(String msg) {
        lblStatus.setText("● " + msg);
    }

    private void refreshSidebar() {
        gadgetListModel.clear();
        for (int i = 0; i < gadgets.size(); i++) {
            Gadget g = gadgets.get(i);
            String tag  = (g instanceof Mobile) ? "📱" : "🎵";
            String name = g.getModel().isEmpty() ? "(unnamed)" : g.getModel();
            gadgetListModel.addElement(String.format(" %d  %s  %s", i, tag, name));
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  INPUT READERS — identical logic to original
    // ═════════════════════════════════════════════════════════════════════════

    private String readModel()       { return tfModel.getText().trim(); }
    private String readSize()        { return tfSize.getText().trim(); }
    private String readPhoneNumber() { return tfPhoneNumber.getText().trim(); }

    private double readPrice() {
        try { return Double.parseDouble(tfPrice.getText().trim()); }
        catch (NumberFormatException e) { appendOutput("Error: Price must be a decimal number."); return 0.0; }
    }
    private int readWeight() {
        try { return Integer.parseInt(tfWeight.getText().trim()); }
        catch (NumberFormatException e) { appendOutput("Error: Weight must be a whole number."); return 0; }
    }
    private int readCredit() {
        try {
            int v = Integer.parseInt(tfCredit.getText().trim());
            if (v < 0) { appendOutput("Error: Credit cannot be negative."); return 0; }
            return v;
        } catch (NumberFormatException e) { appendOutput("Error: Credit must be a whole number."); return 0; }
    }
    private int readMemory() {
        try {
            int v = Integer.parseInt(tfMemory.getText().trim());
            if (v < 0) { appendOutput("Error: Memory cannot be negative."); return 0; }
            return v;
        } catch (NumberFormatException e) { appendOutput("Error: Memory must be a whole number."); return 0; }
    }
    private int readDuration() {
        try { return Integer.parseInt(tfDuration.getText().trim()); }
        catch (NumberFormatException e) { appendOutput("Error: Duration must be a whole number."); return 0; }
    }
    private int readDownloadSize() {
        try { return Integer.parseInt(tfDownloadSize.getText().trim()); }
        catch (NumberFormatException e) { appendOutput("Error: Download size must be a whole number."); return 0; }
    }

    private int getDisplayNumber() {
        int displayNumber = -1;
        try {
            int input = Integer.parseInt(tfDisplayNumber.getText().trim());
            if (input >= 0 && input < gadgets.size()) {
                displayNumber = input;
            } else {
                JOptionPane.showMessageDialog(this,
                        "Display number " + input + " is out of range.\n"
                        + "Please enter a value between 0 and " + (gadgets.size() - 1) + ".",
                        "Out of Range", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "The display number must be a whole number (integer).",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
        return displayNumber;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  ACTION HANDLER
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if      (src == btnAddMobile)     addMobile();
        else if (src == btnAddMP3)        addMP3();
        else if (src == btnDisplayAll)    displayAll();
        else if (src == btnMakeCall)      makeCall();
        else if (src == btnAddCredit)     addCredit();
        else if (src == btnDownloadMusic) downloadMusic();
        else if (src == btnDeleteMusic)   deleteMusic();
        else if (src == btnClear)         clearFields();
        else if (src == btnFull)          setFullDisplay();
        else if (src == btnCompact)       setCompactDisplay();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  BUTTON HANDLERS — identical logic to original
    // ═════════════════════════════════════════════════════════════════════════

    private void addMobile() {
        Mobile m = new Mobile(readModel(), readPrice(), readWeight(), readSize(), readCredit());
        gadgets.add(m);
        appendOutput("Mobile added at index " + (gadgets.size() - 1) + ":");
        captureDisplay(m);
        appendSep();
        refreshCount();
        refreshSidebar();
        setStatus("Mobile phone added — index " + (gadgets.size() - 1));
    }

    private void addMP3() {
        MP3 mp3 = new MP3(readModel(), readPrice(), readWeight(), readSize(), readMemory());
        gadgets.add(mp3);
        appendOutput("MP3 added at index " + (gadgets.size() - 1) + ":");
        captureDisplay(mp3);
        appendSep();
        refreshCount();
        refreshSidebar();
        setStatus("MP3 player added — index " + (gadgets.size() - 1));
    }

    private void clearFields() {
        for (JTextField f : new JTextField[]{
                tfModel, tfPrice, tfWeight, tfSize,
                tfCredit, tfPhoneNumber, tfDuration,
                tfMemory, tfDownloadSize, tfDisplayNumber})
            f.setText("");
        taOutput.setText("");
        setStatus("All fields and output cleared.");
    }

    private void displayAll() {
        if (gadgets.isEmpty()) {
            appendOutput("No gadgets in the shop yet.");
            setStatus("No gadgets to display.");
            return;
        }
        appendOutput("=== All Gadgets (" + gadgets.size() + " item"
                + (gadgets.size() == 1 ? "" : "s") + ") ===");
        for (int i = 0; i < gadgets.size(); i++) {
            appendOutput("Gadget #" + i + "  ["
                    + (gadgets.get(i) instanceof Mobile ? "Mobile" : "MP3") + "]:");
            captureDisplay(gadgets.get(i));
            appendSep();
        }
        setStatus("Displayed all " + gadgets.size() + " gadget(s).");
    }

    private void makeCall() {
        int idx = getDisplayNumber();
        if (idx == -1) return;
        Gadget g = gadgets.get(idx);
        if (!(g instanceof Mobile)) { appendOutput("Error: Gadget #" + idx + " is not a Mobile phone."); return; }
        String ph = readPhoneNumber(); int dur = readDuration();
        redirectAndCall(() -> ((Mobile) g).makeCall(ph, dur));
        appendSep();
        setStatus("Make A Call executed on gadget #" + idx);
    }

    private void addCredit() {
        int idx = getDisplayNumber();
        if (idx == -1) return;
        Gadget g = gadgets.get(idx);
        if (!(g instanceof Mobile)) { appendOutput("Error: Gadget #" + idx + " is not a Mobile phone."); return; }
        int amt = readCredit();
        redirectAndCall(() -> ((Mobile) g).addCredit(amt));
        appendSep();
        setStatus("Add Credit executed on gadget #" + idx);
    }

    private void downloadMusic() {
        int idx = getDisplayNumber();
        if (idx == -1) return;
        Gadget g = gadgets.get(idx);
        if (!(g instanceof MP3)) { appendOutput("Error: Gadget #" + idx + " is not an MP3 player."); return; }
        int ds = readDownloadSize();
        redirectAndCall(() -> ((MP3) g).downloadMusic(ds));
        appendSep();
        setStatus("Download Music executed on gadget #" + idx);
    }

    private void deleteMusic() {
        int idx = getDisplayNumber();
        if (idx == -1) return;
        Gadget g = gadgets.get(idx);
        if (!(g instanceof MP3)) { appendOutput("Error: Gadget #" + idx + " is not an MP3 player."); return; }
        int mf = readDownloadSize();
        redirectAndCall(() -> ((MP3) g).deleteMusic(mf));
        appendSep();
        setStatus("Delete Music executed on gadget #" + idx);
    }

    private void setFullDisplay() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setStatus("Full Display mode.");
    }

    private void setCompactDisplay() {
        setExtendedState(JFrame.NORMAL);
        setSize(DIM_COMPACT);
        setLocationRelativeTo(null);
        setStatus("Compact Display mode.");
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  UTILITIES — identical to original
    // ═════════════════════════════════════════════════════════════════════════

    private void captureDisplay(Gadget gadget) {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.PrintStream old = System.out;
        System.setOut(new java.io.PrintStream(baos));
        gadget.display();
        System.out.flush();
        System.setOut(old);
        taOutput.append(baos.toString());
        if (!baos.toString().endsWith("\n")) taOutput.append("\n");
    }

    private void redirectAndCall(Runnable action) {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.PrintStream old = System.out;
        System.setOut(new java.io.PrintStream(baos));
        action.run();
        System.out.flush();
        System.setOut(old);
        taOutput.append(baos.toString());
        if (!baos.toString().endsWith("\n")) taOutput.append("\n");
    }

    private void appendOutput(String text) {
        taOutput.append(text + "\n");
        taOutput.setCaretPosition(taOutput.getDocument().getLength());
    }

    private void appendSep() {
        taOutput.append("─────────────────────────────\n");
        taOutput.setCaretPosition(taOutput.getDocument().getLength());
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  ENTRY POINT
    // ═════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GadgetShop().setVisible(true));
    }
}
