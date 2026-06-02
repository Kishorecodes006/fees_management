package project;

import javax.swing.SwingUtilities;
import java.math.BigDecimal;
import java.util.*;

public class FeeMain {
    public static void main(String[] args) {

        // UI mode: java FeeMain ui
        if (args.length > 0 && args[0].equalsIgnoreCase("ui")) {
            SwingUtilities.invokeLater(FeeUI::new);
            return;
        }

        // Default: launch UI directly (remove this block if you want console only)
        SwingUtilities.invokeLater(FeeUI::new);

        /* ── CONSOLE MODE (kept for reference) ─────────────────────────────
         * To use console mode, comment out the SwingUtilities line above
         * and uncomment the block below.
         *
        Scanner sc = new Scanner(System.in);
        FeeDAO dao = new FeeDAO();
        while (true) {
            System.out.println("\n=== FEES MANAGEMENT SYSTEM ===");
            System.out.println("1. Add Student");
            System.out.println("2. Get Student Details");
            System.out.println("3. Pay Fees");
            System.out.println("4. Payment History");
            System.out.println("5. Exit");
            System.out.print("Choose: ");
            int ch = Integer.parseInt(sc.nextLine());
            try {
                if (ch == 1) {
                    System.out.print("Name: ");       String n = sc.nextLine();
                    System.out.print("Course: ");     String c = sc.nextLine();
                    System.out.print("Total Fee: ");  BigDecimal f = new BigDecimal(sc.nextLine());
                    int id = dao.addStudent(n, c, f);
                    System.out.println("Student Added. ID = " + id);
                }
                else if (ch == 2) {
                    System.out.print("Enter ID: ");   int id = Integer.parseInt(sc.nextLine());
                    Student s = dao.getStudent(id);
                    System.out.println(s == null ? "Not found" : s);
                }
                else if (ch == 3) {
                    System.out.print("Enter Student ID: "); int id = Integer.parseInt(sc.nextLine());
                    System.out.print("Amount: ");           BigDecimal amt = new BigDecimal(sc.nextLine());
                    System.out.print("Method: ");           String m = sc.nextLine();
                    dao.pay(id, amt, m);
                    System.out.println("Payment Successful!");
                }
                else if (ch == 4) {
                    System.out.print("Enter ID: ");   int id = Integer.parseInt(sc.nextLine());
                    List<String> pay = dao.getPayments(id);
                    if (pay.isEmpty()) System.out.println("No payments found.");
                    else pay.forEach(System.out::println);
                }
                else if (ch == 5) { break; }
            } catch (Exception e) { e.printStackTrace(); }
        }
        sc.close();
        *
        * ─────────────────────────────────────────────────────────────────── */
    }
}