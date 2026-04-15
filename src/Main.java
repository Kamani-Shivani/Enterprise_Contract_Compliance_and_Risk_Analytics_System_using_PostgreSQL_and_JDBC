// =========================
// MAIN APPLICATION
// =========================
// This is the main driver class of the system.
// It provides a menu-driven interface to perform
// various contract risk analytics operations.


import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){

        try(Connection con = DBConnection.getConnection();
            Scanner sc = new Scanner(System.in)){

            while(true){
                System.out.println("\n===== ENTERPRISE CONTRACT COMPLIANCE & RISK ANALYTICS SYSTEM =====");
                System.out.println("1. High-Risk Contract Analysis");
                System.out.println("2. Vendor Compliance & Performance Analysis");
                System.out.println("3. Contract Expiry Risk Monitoring");
                System.out.println("4. SLA Compliance & Violation Analysis");
                System.out.println("5. Pending Payment Risk Assessment");
                System.out.println("6. Risk Distribution Analytics");
                System.out.println("7. High-Risk Vendor Identification");
                System.out.println("8. Contract Delay Risk Analysis");
                System.out.println("9. Contract Value & Financial Risk Insights");
                System.out.println("10. Calculate Risk Score (Stored Procedure)");
                System.out.println("11. Exit System");
                System.out.print("Select an option: ");

                if(!sc.hasNextInt()){
                    System.out.println("Invalid input!!");
                    sc.next();
                    continue;
                }
                int choice = sc.nextInt();

                switch(choice){
                    // =========================
                    // 1. HIGH RISK CONTRACTS
                    // =========================
                    case 1:
                        System.out.println("\n=== HIGH RISK CONTRACT REPORT ===");
                        try(PreparedStatement ps = con.prepareStatement(
                                "select contract_id, vendor_name, contract_value, score, risk_level from high_risk_contracts");
                            ResultSet rs = ps.executeQuery()){

                            boolean found = false;

                            System.out.println("---------------------------------------------------------------------");
                            System.out.printf("%-12s %-18s %-10s %-8s %-10s\n",
                                    "Contract ID", "Vendor", "Contract Value", "Score", "Risk");
                            System.out.println("---------------------------------------------------------------------");
                            while (rs.next()){
                                found = true;
                                System.out.printf("%-12d %-18s %-10.2f %-8d %-10s\n",
                                        rs.getInt("contract_id"),
                                        rs.getString("vendor_name"),
                                        rs.getDouble("contract_value"),
                                        rs.getInt("score"),
                                        rs.getString("risk_level"));
                            }
                            if(!found){
                                System.out.println("No records found.");
                            }
                            System.out.println("---------------------------------------------------------------------");
                        }
                        break;

                    // =========================
                    // 2. VENDOR PERFORMANCE
                    // =========================
                    case 2:
                        System.out.println("\n=== VENDOR PERFORMANCE REPORT ===");
                        try(PreparedStatement ps = con.prepareStatement(
                                "select v.name, avg(r.score) as average_risk " +
                                        "from vendors v " +
                                        "join contracts c on v.vendor_id = c.vendor_id " +
                                        "join risk_scores r on c.contract_id = r.contract_id " +
                                        "group by v.name order by average_risk desc");
                            ResultSet rs = ps.executeQuery()){

                            boolean found = false;

                            System.out.println("---------------------------------------------");
                            System.out.printf("%-25s %-15s\n", "Vendor", "Average Risk");
                            System.out.println("---------------------------------------------");

                            while (rs.next()){
                                found = true;
                                System.out.printf("%-25s %-15.2f\n",
                                        rs.getString(1),
                                        rs.getDouble(2));
                            }
                            if(!found){
                                System.out.println("No records found.");
                            }
                            System.out.println("---------------------------------------------");
                        }
                        break;

                    // =========================
                    // 3. CONTRACT EXPIRY
                    // =========================
                    case 3:
                        System.out.println("\n=== CONTRACT EXPIRY REPORT ===");
                        try(PreparedStatement ps = con.prepareStatement(
                                "select contract_id, end_date " +
                                        "from contracts " +
                                        "where end_date between current_date and current_date + interval '30 days'");
                            ResultSet rs = ps.executeQuery()){

                            boolean found = false;

                            System.out.println("--------------------------------------");
                            System.out.printf("%-15s %-15s\n", "Contract ID", "Expiry Date");
                            System.out.println("--------------------------------------");

                            while (rs.next()){
                                found = true;
                                System.out.printf("%-15d %-15s\n",
                                        rs.getInt(1),
                                        rs.getDate(2));
                            }
                            if(!found){
                                System.out.println("No records found.");
                            }
                            System.out.println("--------------------------------------");
                        }
                        break;

                    // =========================
                    // 4. SLA VIOLATIONS
                    // =========================
                    case 4:
                        System.out.println("\n=== SLA VIOLATION REPORT ===");
                        try(PreparedStatement ps = con.prepareStatement(
                                "select contract_id, count(*) as violations " +
                                        "from obligations " +
                                        "where status = 'DELAYED' " +
                                        "group by contract_id");
                            ResultSet rs = ps.executeQuery()){

                            boolean found = false;

                            System.out.println("--------------------------------------");
                            System.out.printf("%-15s %-15s\n", "Contract ID", "Violations");
                            System.out.println("--------------------------------------");

                            while (rs.next()){
                                found = true;
                                System.out.printf("%-15d %-15d\n",
                                        rs.getInt(1),
                                        rs.getInt(2));
                            }
                            if(!found){
                                System.out.println("No records found.");
                            }
                            System.out.println("--------------------------------------");
                        }
                        break;

                    // =========================
                    // 5. PAYMENT RISK
                    // =========================
                    case 5:
                        System.out.println("\n=== PAYMENT RISK REPORT ===");
                        try(PreparedStatement ps = con.prepareStatement(
                                "select contract_id, coalesce(sum(amount),0) as pending_amount " +
                                        "from payments " +
                                        "where status in ('PENDING','LATE') " +
                                        "group by contract_id");
                            ResultSet rs = ps.executeQuery()){

                            boolean found = false;

                            System.out.println("-------------------------------------------");
                            System.out.printf("%-15s %-20s\n", "Contract ID", "Pending Amount");
                            System.out.println("-------------------------------------------");

                            while (rs.next()){
                                found = true;
                                System.out.printf("%-15d %-20.2f\n",
                                        rs.getInt(1),
                                        rs.getDouble(2));
                            }
                            if(!found){
                                System.out.println("No records found.");
                            }
                            System.out.println("-------------------------------------------");
                        }
                        break;

                    // =========================
                    // 6. RISK DISTRIBUTION
                    // =========================
                    case 6:
                        System.out.println("\n=== RISK DISTRIBUTION REPORT ===");
                        try(PreparedStatement ps = con.prepareStatement(
                                "select risk_level, count(*) from risk_scores group by risk_level");
                            ResultSet rs = ps.executeQuery()){

                            boolean found = false;

                            System.out.println("--------------------------------------");
                            System.out.printf("%-15s %-10s\n", "Risk Level", "Count");
                            System.out.println("--------------------------------------");

                            while (rs.next()){
                                found = true;
                                System.out.printf("%-15s %-10d\n",
                                        rs.getString(1),
                                        rs.getInt(2));
                            }
                            if(!found){
                                System.out.println("No records found.");
                            }
                            System.out.println("--------------------------------------");
                        }
                        break;

                    // =========================
                    // 7. HIGH RISK VENDORS
                    // =========================
                    case 7:
                        System.out.println("\n=== HIGH RISK VENDOR REPORT ===");
                        try(PreparedStatement ps = con.prepareStatement(
                                "select v.name, sum(r.score) as total_risk " +
                                        "from vendors v " +
                                        "join contracts c on v.vendor_id = c.vendor_id " +
                                        "join risk_scores r on c.contract_id = r.contract_id " +
                                        "group by v.name order by total_risk desc limit 3");
                            ResultSet rs = ps.executeQuery()){

                            boolean found = false;

                            System.out.println("--------------------------------------");
                            System.out.printf("%-25s %-15s\n", "Vendor", "Total Risk");
                            System.out.println("--------------------------------------");

                            while (rs.next()){
                                found = true;
                                System.out.printf("%-25s %-15d\n",
                                        rs.getString(1),
                                        rs.getInt(2));
                            }
                            if(!found){
                                System.out.println("No records found.");
                            }
                            System.out.println("--------------------------------------");
                        }
                        break;

                    // =========================
                    // 8. DELAY ANALYSIS
                    // =========================
                    case 8:
                        System.out.println("\n=== CONTRACT DELAY REPORT ===");
                        try(PreparedStatement ps = con.prepareStatement(
                                "select contract_id, count(*) as delays " +
                                        "from obligations " +
                                        "where status = 'DELAYED' " +
                                        "group by contract_id order by delays desc");
                            ResultSet rs = ps.executeQuery()){

                            boolean found = false;

                            System.out.println("--------------------------------------");
                            System.out.printf("%-15s %-10s\n", "Contract ID", "Delays");
                            System.out.println("--------------------------------------");

                            while (rs.next()){
                                found = true;
                                System.out.printf("%-15d %-10d\n",
                                        rs.getInt(1),
                                        rs.getInt(2));
                            }
                            if(!found){
                                System.out.println("No records found.");
                            }
                            System.out.println("--------------------------------------");
                        }
                        break;

                    // =========================
                    // 9. FINANCIAL INSIGHTS
                    // =========================
                    case 9:
                        System.out.println("\n=== FINANCIAL INSIGHTS REPORT ===");
                        try(PreparedStatement ps = con.prepareStatement(
                                "select status, sum(contract_value) as total_value " +
                                        "from contracts group by status");
                            ResultSet rs = ps.executeQuery()){

                            boolean found = false;

                            System.out.println("--------------------------------------");
                            System.out.printf("%-15s %-20s\n", "Status", "Total Value");
                            System.out.println("--------------------------------------");

                            while (rs.next()){
                                found = true;
                                System.out.printf("%-15s %-20.2f\n",
                                        rs.getString(1),
                                        rs.getDouble(2));
                            }
                            if(!found){
                                System.out.println("No records found.");
                            }
                            System.out.println("--------------------------------------");
                        }
                        break;

                    // =========================
                    // 10. STORED PROCEDURE
                    // =========================
                    case 10:
                        System.out.println("Enter Contract ID: ");
                        int id = sc.nextInt();

                        try (PreparedStatement ps = con.prepareStatement("CALL calculate_risk_score(?)")) {
                            ps.setInt(1, id);
                            ps.execute();
                            System.out.println("Risk Calculated Successfully!");
                        }
                        break;

                    // =========================
                    // 11. EXIT
                    // =========================
                    case 11:
                        System.out.println("Exiting the application...");
                        return;

                    default:
                        System.out.println("Invalid choice!");
                }
            }
        } catch (SQLException e){
            System.out.println("Database Error: " + e.getMessage());
        }
    }
}