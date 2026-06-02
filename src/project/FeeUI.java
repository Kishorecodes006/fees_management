package project;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.List;

public class FeeUI extends JFrame {

    private FeeDAO dao = new FeeDAO();
    private Color PRIMARY = new Color(24, 95, 165);
    private Color BG      = new Color(245, 247, 250);
    private Color CARD    = Color.WHITE;
    private Color TEXT    = new Color(30, 30, 30);
    private Color MUTED   = new Color(120, 120, 130);
    private Color SUCCESS = new Color(59, 109, 17);
    private Color DANGER  = new Color(163, 45, 45);

    private JButton[] navBtns;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Dashboard live references
    private JLabel statCount, statPaid, statBal;
    private DefaultTableModel dashModel;

    public FeeUI() {
        setTitle("Fees Management System");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG);

        add(buildSidebar(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG);
        contentPanel.add(buildDashboard(),  "dashboard");
        contentPanel.add(buildAddStudent(), "add");
        contentPanel.add(buildDetails(),    "details");
        contentPanel.add(buildPayFees(),    "pay");
        contentPanel.add(buildHistory(),    "history");
        add(contentPanel, BorderLayout.CENTER);

        // Load real DB data on startup
        refreshDashboard();

        setVisible(true);
    }

    // ─── SIDEBAR ─────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(190, 600));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 225)));

        JLabel logo = new JLabel("  FeeManager");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        logo.setForeground(PRIMARY);
        logo.setBorder(new EmptyBorder(20, 10, 20, 10));
        logo.setAlignmentX(LEFT_ALIGNMENT);
        sidebar.add(logo);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(230, 230, 235));
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(8));

        String[] labels = {"Dashboard", "Add Student", "Student Details", "Pay Fees", "Payment History"};
        String[] panels = {"dashboard", "add", "details", "pay", "history"};
        navBtns = new JButton[labels.length];

        for (int i = 0; i < labels.length; i++) {
            final String panel = panels[i];
            final int idx = i;
            JButton btn = new JButton(labels[i]);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            btn.setForeground(MUTED);
            btn.setBackground(Color.WHITE);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            btn.setBorder(new EmptyBorder(8, 20, 8, 10));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> {
                cardLayout.show(contentPanel, panel);
                setActiveNav(idx);
                if (panel.equals("dashboard")) refreshDashboard();
            });
            navBtns[i] = btn;
            sidebar.add(btn);
        }

        sidebar.add(Box.createVerticalGlue());
        setActiveNav(0);
        return sidebar;
    }

    private void setActiveNav(int idx) {
        for (int i = 0; i < navBtns.length; i++) {
            if (i == idx) {
                navBtns[i].setBackground(new Color(230, 241, 251));
                navBtns[i].setForeground(PRIMARY);
                navBtns[i].setFont(new Font("Segoe UI", Font.BOLD, 13));
            } else {
                navBtns[i].setBackground(Color.WHITE);
                navBtns[i].setForeground(MUTED);
                navBtns[i].setFont(new Font("Segoe UI", Font.PLAIN, 13));
            }
        }
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────────
    private JPanel card(String title) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 225), 1, true),
            new EmptyBorder(18, 20, 18, 20)));
        if (title != null) {
            JLabel lbl = new JLabel(title);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lbl.setForeground(TEXT);
            lbl.setAlignmentX(LEFT_ALIGNMENT);
            p.add(lbl);
            p.add(Box.createVerticalStrut(14));
        }
        return p;
    }

    private JTextField field(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        tf.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(210, 210, 215), 1, true),
            new EmptyBorder(5, 10, 5, 10)));
        tf.setText(placeholder);
        tf.setForeground(MUTED);
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (tf.getText().equals(placeholder)) { tf.setText(""); tf.setForeground(TEXT); }
            }
            public void focusLost(FocusEvent e) {
                if (tf.getText().isEmpty()) { tf.setText(placeholder); tf.setForeground(MUTED); }
            }
        });
        return tf;
    }

    private String realVal(JTextField tf, String placeholder) {
        String v = tf.getText().trim();
        return v.equals(placeholder) ? "" : v;
    }

    private JButton primaryBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBackground(PRIMARY);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(8, 20, 8, 20));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        return b;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(MUTED);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JPanel wrapPage(String heading, JPanel body) {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(BG);
        page.setBorder(new EmptyBorder(24, 24, 24, 24));
        JLabel h = new JLabel(heading);
        h.setFont(new Font("Segoe UI", Font.BOLD, 18));
        h.setForeground(TEXT);
        h.setBorder(new EmptyBorder(0, 0, 16, 0));
        page.add(h, BorderLayout.NORTH);
        page.add(body, BorderLayout.CENTER);
        return page;
    }

    private void showMsg(JLabel lbl, String msg, boolean ok) {
        lbl.setText(msg);
        lbl.setForeground(ok ? SUCCESS : DANGER);
        lbl.setVisible(true);
        Timer t = new Timer(3000, e -> lbl.setVisible(false));
        t.setRepeats(false);
        t.start();
    }

    // ─── DASHBOARD ───────────────────────────────────────────────────────────
    private JPanel buildDashboard() {
        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setBackground(BG);

        // Stat cards
        JPanel stats = new JPanel(new GridLayout(1, 3, 12, 0));
        stats.setBackground(BG);

        statCount = makeStatCard(stats, "Total Students",    new Color(24,95,165));
        statPaid  = makeStatCard(stats, "Amount Collected",  SUCCESS);
        statBal   = makeStatCard(stats, "Pending Balance",   DANGER);

        // Table
        String[] cols = {"ID", "Name", "Course", "Total Fee", "Paid", "Balance", "Status"};
        dashModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(dashModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(245, 247, 250));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(230, 241, 251));

        // Color status column
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String s = v == null ? "" : v.toString();
                l.setForeground("Paid".equals(s) ? SUCCESS : "Partial".equals(s) ? new Color(133,79,11) : DANGER);
                l.setFont(l.getFont().deriveFont(Font.BOLD));
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 225)));

        JPanel tableCard = new JPanel(new BorderLayout(0, 10));
        tableCard.setBackground(CARD);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220,220,225),1,true),
            new EmptyBorder(14,16,14,16)));
        JLabel tTitle = new JLabel("All Students");
        tTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableCard.add(tTitle, BorderLayout.NORTH);
        tableCard.add(scroll, BorderLayout.CENTER);

        JButton refresh = primaryBtn("Refresh Dashboard");
        refresh.addActionListener(e -> refreshDashboard());

        body.add(stats,     BorderLayout.NORTH);
        body.add(tableCard, BorderLayout.CENTER);
        body.add(refresh,   BorderLayout.SOUTH);

        return wrapPage("Dashboard", body);
    }

    private JLabel makeStatCard(JPanel parent, String title, Color valColor) {
        JPanel sc = new JPanel(new BorderLayout());
        sc.setBackground(CARD);
        sc.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220,220,225),1,true),
            new EmptyBorder(14,16,14,16)));
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(MUTED);
        JLabel val = new JLabel("...");
        val.setFont(new Font("Segoe UI", Font.BOLD, 22));
        val.setForeground(valColor);
        sc.add(lbl, BorderLayout.NORTH);
        sc.add(val, BorderLayout.CENTER);
        parent.add(sc);
        return val;
    }

    // ─── REAL DB REFRESH ─────────────────────────────────────────────────────
    private void refreshDashboard() {
        SwingWorker<List<Student>, Void> worker = new SwingWorker<>() {
            protected List<Student> doInBackground() throws Exception {
                return dao.getAllStudents();
            }
            protected void done() {
                try {
                    List<Student> list = get();
                    dashModel.setRowCount(0);
                    long totalFeeSum = 0, paidSum = 0;
                    for (Student s : list) {
                        long tf  = s.getTotalFee().longValue();
                        long p   = s.getPaid().longValue();
                        long bal = s.getBalance().longValue();
                        totalFeeSum += tf;
                        paidSum     += p;
                        String status = bal == 0 ? "Paid" : p > 0 ? "Partial" : "Pending";
                        dashModel.addRow(new Object[]{
                            "#" + s.getId(),
                            s.getName(),
                            s.getCourse(),
                            "₹" + String.format("%,d", tf),
                            "₹" + String.format("%,d", p),
                            "₹" + String.format("%,d", bal),
                            status
                        });
                    }
                    statCount.setText(String.valueOf(list.size()));
                    statPaid.setText("₹" + String.format("%,d", paidSum));
                    statBal.setText("₹"  + String.format("%,d", totalFeeSum - paidSum));
                } catch (Exception ex) {
                    statCount.setText("DB Error");
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    // ─── ADD STUDENT ─────────────────────────────────────────────────────────
    private JPanel buildAddStudent() {
        JPanel c = card("New Student Registration");
        c.setMaximumSize(new Dimension(500, 400));

        JTextField tfName   = field("Student Name");
        JTextField tfCourse = field("Course (e.g. B.E CSE)");
        JTextField tfFee    = field("Total Fee (e.g. 75000)");
        JLabel     msgLbl   = new JLabel(" ");
        msgLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        msgLbl.setVisible(false);

        JButton btn = primaryBtn("Add Student");
        btn.setAlignmentX(LEFT_ALIGNMENT);

        ActionListener addAction = e -> {
            String name   = realVal(tfName,   "Student Name");
            String course = realVal(tfCourse, "Course (e.g. B.E CSE)");
            String feeStr = realVal(tfFee,    "Total Fee (e.g. 75000)");
            if (name.isEmpty() || course.isEmpty() || feeStr.isEmpty()) {
                showMsg(msgLbl, "Please fill all fields!", false); return;
            }
            try {
                int id = dao.addStudent(name, course, new BigDecimal(feeStr));
                showMsg(msgLbl, "Student added! ID = " + id, true);
                tfName.setText("Student Name");             tfName.setForeground(MUTED);
                tfCourse.setText("Course (e.g. B.E CSE)"); tfCourse.setForeground(MUTED);
                tfFee.setText("Total Fee (e.g. 75000)");   tfFee.setForeground(MUTED);
            } catch (Exception ex) { showMsg(msgLbl, "Error: " + ex.getMessage(), false); }
        };

        btn.addActionListener(addAction);
        tfName.addActionListener(e -> tfCourse.requestFocus());
        tfCourse.addActionListener(e -> tfFee.requestFocus());
        tfFee.addActionListener(addAction);

        c.add(label("Student Name"));    c.add(Box.createVerticalStrut(4));
        c.add(tfName);                   c.add(Box.createVerticalStrut(12));
        c.add(label("Course"));          c.add(Box.createVerticalStrut(4));
        c.add(tfCourse);                 c.add(Box.createVerticalStrut(12));
        c.add(label("Total Fee (₹)"));   c.add(Box.createVerticalStrut(4));
        c.add(tfFee);                    c.add(Box.createVerticalStrut(18));
        c.add(btn);                      c.add(Box.createVerticalStrut(10));
        c.add(msgLbl);

        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrap.setBackground(BG);
        wrap.add(c);
        return wrapPage("Add Student", wrap);
    }

    // ─── STUDENT DETAILS ─────────────────────────────────────────────────────
    private JPanel buildDetails() {
        JPanel c = card("Search Student by ID");

        JTextField tfId = field("Enter Student ID");
        JButton btn     = primaryBtn("Search");
        JPanel result   = new JPanel();
        result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
        result.setBackground(new Color(245, 247, 250));
        result.setBorder(new EmptyBorder(12, 12, 12, 12));
        result.setVisible(false);

        ActionListener searchAction = e -> {
            String idStr = realVal(tfId, "Enter Student ID");
            if (idStr.isEmpty()) return;
            try {
                Student s = dao.getStudent(Integer.parseInt(idStr));
                result.removeAll();
                if (s == null) {
                    JLabel err = new JLabel("Student not found.");
                    err.setForeground(DANGER);
                    result.add(err);
                } else {
                    long bal = s.getBalance().longValue();
                    String[][] rows = {
                        {"ID",        "#" + s.getId()},
                        {"Name",      s.getName()},
                        {"Course",    s.getCourse()},
                        {"Total Fee", "₹" + String.format("%,d", s.getTotalFee().longValue())},
                        {"Paid",      "₹" + String.format("%,d", s.getPaid().longValue())},
                        {"Balance",   "₹" + String.format("%,d", bal)}
                    };
                    for (String[] row : rows) {
                        JPanel rowP = new JPanel(new BorderLayout());
                        rowP.setBackground(new Color(245, 247, 250));
                        rowP.setBorder(new EmptyBorder(4, 0, 4, 0));
                        JLabel k = new JLabel(row[0]);
                        k.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                        k.setForeground(MUTED);
                        JLabel v = new JLabel(row[1]);
                        v.setFont(new Font("Segoe UI", Font.BOLD, 13));
                        v.setForeground(row[0].equals("Balance") ? (bal > 0 ? DANGER : SUCCESS) : TEXT);
                        rowP.add(k, BorderLayout.WEST);
                        rowP.add(v, BorderLayout.EAST);
                        rowP.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
                        result.add(rowP);
                    }
                }
                result.setVisible(true);
                result.revalidate();
                result.repaint();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        btn.addActionListener(searchAction);
        tfId.addActionListener(searchAction);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setBackground(CARD);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        tfId.setPreferredSize(new Dimension(200, 34));
        row.add(tfId); row.add(btn);

        c.add(row);
        c.add(Box.createVerticalStrut(14));
        c.add(result);

        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrap.setBackground(BG);
        wrap.add(c);
        return wrapPage("Student Details", wrap);
    }

    // ─── PAY FEES ────────────────────────────────────────────────────────────
    private JPanel buildPayFees() {
        JPanel c = card("Record Fee Payment");
        c.setMaximumSize(new Dimension(500, 420));

        JTextField tfId  = field("Student ID");
        JTextField tfAmt = field("Amount (₹)");
        JComboBox<String> cbMethod = new JComboBox<>(new String[]{"Cash","UPI","Bank Transfer","Cheque","DD"});
        cbMethod.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbMethod.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        JLabel infoLbl = new JLabel(" ");
        infoLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLbl.setForeground(MUTED);
        infoLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel msgLbl = new JLabel(" ");
        msgLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        msgLbl.setVisible(false);

        // Load student info when ID entered
        FocusAdapter loadInfo = new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                String idStr = realVal(tfId, "Student ID");
                if (idStr.isEmpty()) return;
                try {
                    Student s = dao.getStudent(Integer.parseInt(idStr));
                    if (s != null)
                        infoLbl.setText(s.getName() + " — " + s.getCourse() +
                            "  |  Balance: ₹" + String.format("%,d", s.getBalance().longValue()));
                    else infoLbl.setText("Student not found");
                } catch (Exception ex) { infoLbl.setText(""); }
            }
        };
        tfId.addFocusListener(loadInfo);

        JButton btn = primaryBtn("Record Payment");
        btn.setAlignmentX(LEFT_ALIGNMENT);

        ActionListener payAction = e -> {
            String idStr  = realVal(tfId,  "Student ID");
            String amtStr = realVal(tfAmt, "Amount (₹)");
            if (idStr.isEmpty() || amtStr.isEmpty()) {
                showMsg(msgLbl, "Please fill all fields!", false); return;
            }
            try {
                dao.pay(Integer.parseInt(idStr), new BigDecimal(amtStr), (String) cbMethod.getSelectedItem());
                showMsg(msgLbl, "Payment of ₹" + amtStr + " recorded via " + cbMethod.getSelectedItem() + "!", true);
                tfId.setText("Student ID");   tfId.setForeground(MUTED);
                tfAmt.setText("Amount (₹)");  tfAmt.setForeground(MUTED);
                infoLbl.setText(" ");
            } catch (Exception ex) { showMsg(msgLbl, "Error: " + ex.getMessage(), false); }
        };

        btn.addActionListener(payAction);
        tfId.addActionListener(e -> tfAmt.requestFocus());
        tfAmt.addActionListener(payAction);

        c.add(label("Student ID"));      c.add(Box.createVerticalStrut(4));
        c.add(tfId);                     c.add(Box.createVerticalStrut(4));
        c.add(infoLbl);                  c.add(Box.createVerticalStrut(10));
        c.add(label("Amount (₹)"));      c.add(Box.createVerticalStrut(4));
        c.add(tfAmt);                    c.add(Box.createVerticalStrut(12));
        c.add(label("Payment Method"));  c.add(Box.createVerticalStrut(4));
        c.add(cbMethod);                 c.add(Box.createVerticalStrut(18));
        c.add(btn);                      c.add(Box.createVerticalStrut(10));
        c.add(msgLbl);

        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrap.setBackground(BG);
        wrap.add(c);
        return wrapPage("Pay Fees", wrap);
    }

    // ─── PAYMENT HISTORY ─────────────────────────────────────────────────────
    private JPanel buildHistory() {
        JPanel c = card("Payment History");

        JTextField tfId = field("Enter Student ID");
        JButton btn     = primaryBtn("View History");
        JLabel msgLbl   = new JLabel(" ");
        msgLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        String[] cols = {"#", "Amount", "Method", "Date & Time"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c2) { return false; }
        };
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(230, 241, 251));

        ActionListener histAction = e -> {
            String idStr = realVal(tfId, "Enter Student ID");
            if (idStr.isEmpty()) return;
            try {
                int id = Integer.parseInt(idStr);
                List<String> pays = dao.getPayments(id);
                model.setRowCount(0);
                if (pays.isEmpty()) {
                    msgLbl.setText("No payments found for ID " + id);
                    msgLbl.setForeground(MUTED);
                } else {
                    msgLbl.setText(pays.size() + " payment(s) found");
                    msgLbl.setForeground(SUCCESS);
                    int i = 1;
                    for (String p : pays) {
                        String[] parts = p.split(" \\| ");
                        model.addRow(new Object[]{
                            i++,
                            parts.length > 0 ? "₹" + parts[0] : "",
                            parts.length > 1 ? parts[1] : "",
                            parts.length > 2 ? parts[2] : ""
                        });
                    }
                }
            } catch (Exception ex) {
                msgLbl.setText("Error: " + ex.getMessage());
                msgLbl.setForeground(DANGER);
            }
        };

        btn.addActionListener(histAction);
        tfId.addActionListener(histAction);

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchRow.setBackground(CARD);
        searchRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        tfId.setPreferredSize(new Dimension(200, 34));
        searchRow.add(tfId); searchRow.add(btn);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 225)));
        scroll.setAlignmentX(LEFT_ALIGNMENT);
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        c.add(searchRow);
        c.add(Box.createVerticalStrut(8));
        c.add(msgLbl);
        c.add(Box.createVerticalStrut(10));
        c.add(scroll);

        return wrapPage("Payment History", c);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FeeUI::new);
    }
}