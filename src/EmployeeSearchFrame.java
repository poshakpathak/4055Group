/**
 * Author: Lon Smith, Ph.D.
 * Modified by: Sabin Chalise, Poshak Pathak, Avery Bailey, Sumnima Rai
 * Description: Database program for searching Employees by Department and Project.
 */

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class EmployeeSearchFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtDatabase;
    private JList<String> lstDepartment;
    private DefaultListModel<String> department = new DefaultListModel<String>();
    private JList<String> lstProject;
    private DefaultListModel<String> project = new DefaultListModel<String>();
    private JTextArea textAreaEmployee;
    private JCheckBox chckbxNotDept;
    private JCheckBox chckbxNotProject;

    private static final String SERVER_ADDRESS = "192.168.1.55";
    private static final String DB_PORT = "3306";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    EmployeeSearchFrame frame = new EmployeeSearchFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public EmployeeSearchFrame() {
        setTitle("Employee Search");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 400); // Increased height slightly to fit scroll panes comfortably
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblNewLabel = new JLabel("Database:");
        lblNewLabel.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblNewLabel.setBounds(21, 23, 59, 14);
        contentPane.add(lblNewLabel);

        txtDatabase = new JTextField();
        txtDatabase.setBounds(90, 20, 193, 20);
        contentPane.add(txtDatabase);
        txtDatabase.setColumns(10);

        JButton btnDBFill = new JButton("Fill");

        //implementing jdbc connection
        btnDBFill.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Get the database name entered by Dr. Smith
                String dbName = txtDatabase.getText().trim();

                if (dbName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter a database name.");
                    return;
                }

                // construct connection url
                String url = "jdbc:mysql://" + SERVER_ADDRESS + ":" + DB_PORT + "/" + dbName;

                try {
                    // Establish Connection
                    Connection conn = DriverManager.getConnection(url, DB_USER, DB_PASS);
                    Statement stmt = conn.createStatement();

                    // 1. Fill Department List
                    department.clear(); // Clear old data
                    ResultSet rsDept = stmt.executeQuery("SELECT Dname FROM DEPARTMENT");
                    while (rsDept.next()) {
                        department.addElement(rsDept.getString("Dname"));
                    }
                    rsDept.close();

                    // 2. Fill Project List
                    project.clear(); // Clear old data
                    ResultSet rsProj = stmt.executeQuery("SELECT Pname FROM PROJECT");
                    while (rsProj.next()) {
                        project.addElement(rsProj.getString("Pname"));
                    }
                    rsProj.close();

                    conn.close();

                } catch (SQLException ex) {
                    // Requirement #5: Pop-up alert on failure
                    JOptionPane.showMessageDialog(null, "Database could not be opened: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        btnDBFill.setFont(new Font("Times New Roman", Font.BOLD, 12));
        btnDBFill.setBounds(307, 19, 68, 23);
        contentPane.add(btnDBFill);

        JLabel lblDepartment = new JLabel("Department");
        lblDepartment.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblDepartment.setBounds(52, 63, 89, 14);
        contentPane.add(lblDepartment);

        JLabel lblProject = new JLabel("Project");
        lblProject.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblProject.setBounds(255, 63, 47, 14);
        contentPane.add(lblProject);

        // Scrollable lists (requirement 4)

        // Project List
        lstProject = new JList<String>(project);
        lstProject.setFont(new Font("Tahoma", Font.PLAIN, 12));
        // Wrap in ScrollPane
        JScrollPane scrollProject = new JScrollPane(lstProject);
        scrollProject.setBounds(225, 84, 150, 42);
        contentPane.add(scrollProject);

        // Department List
        lstDepartment = new JList<String>(department);
        lstDepartment.setFont(new Font("Tahoma", Font.PLAIN, 12));
        // Wrap in ScrollPane
        JScrollPane scrollDepartment = new JScrollPane(lstDepartment);
        scrollDepartment.setBounds(36, 84, 172, 40);
        contentPane.add(scrollDepartment);

        chckbxNotDept = new JCheckBox("Not");
        chckbxNotDept.setBounds(71, 133, 59, 23);
        contentPane.add(chckbxNotDept);

        chckbxNotProject = new JCheckBox("Not");
        chckbxNotProject.setBounds(270, 133, 59, 23);
        contentPane.add(chckbxNotProject);

        JLabel lblEmployee = new JLabel("Employee");
        lblEmployee.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblEmployee.setBounds(52, 179, 89, 14);
        contentPane.add(lblEmployee);

        JButton btnSearch = new JButton("Search");

        // Search button logic (requirement 3 & 7)
        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String dbName = txtDatabase.getText().trim();
                String url = "jdbc:mysql://" + SERVER_ADDRESS + ":" + DB_PORT + "/" + dbName;

                try {
                    Connection conn = DriverManager.getConnection(url, DB_USER, DB_PASS);


                    StringBuilder sql = new StringBuilder("SELECT DISTINCT Fname, Lname FROM EMPLOYEE e");

                    // Get Selections
                    List<String> selectedDepts = lstDepartment.getSelectedValuesList();
                    List<String> selectedProjs = lstProject.getSelectedValuesList();

                    boolean filterDept = !selectedDepts.isEmpty();
                    boolean filterProj = !selectedProjs.isEmpty();

                    // If filtering by Department, we need the Department table
                    if (filterDept) {
                        sql.append(" JOIN DEPARTMENT d ON e.Dno = d.Dnumber");
                    }
                    // If filtering by Project, we need Works_On and Project tables
                    if (filterProj) {
                        sql.append(" JOIN WORKS_ON w ON e.Ssn = w.Essn");
                        sql.append(" JOIN PROJECT p ON w.Pno = p.Pnumber");
                    }

                    sql.append(" WHERE 1=1");

                    if (filterDept) {
                        String operator = chckbxNotDept.isSelected() ? " NOT IN " : " IN ";
                        sql.append(" AND d.Dname").append(operator).append("(");

                        for (int i = 0; i < selectedDepts.size(); i++) {
                            sql.append("'").append(selectedDepts.get(i)).append("'");
                            if (i < selectedDepts.size() - 1) sql.append(",");
                        }
                        sql.append(")");
                    }

                    if (filterProj) {
                        String operator = chckbxNotProject.isSelected() ? " NOT IN " : " IN ";
                        sql.append(" AND p.Pname").append(operator).append("(");

                        for (int i = 0; i < selectedProjs.size(); i++) {
                            sql.append("'").append(selectedProjs.get(i)).append("'");
                            if (i < selectedProjs.size() - 1) sql.append(",");
                        }
                        sql.append(")");
                    }

                    // Debug: Print query to console to verify it looks right
                    System.out.println("Executing Query: " + sql.toString());

                    // Execute
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql.toString());

                    // Update UI
                    textAreaEmployee.setText(""); // Clear previous results
                    while (rs.next()) {
                        textAreaEmployee.append(rs.getString("Fname") + " " + rs.getString("Lname") + "\n");
                    }

                    conn.close();

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Search Error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        btnSearch.setBounds(80, 276, 89, 23);
        contentPane.add(btnSearch);

        JButton btnClear = new JButton("Clear");

        // Clear button (requirement 6)
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textAreaEmployee.setText("");           // Clear text
                lstDepartment.clearSelection();         // Clear list selection
                lstProject.clearSelection();            // Clear list selection
                chckbxNotDept.setSelected(false);       // Uncheck boxes
                chckbxNotProject.setSelected(false);    // Uncheck boxes
            }
        });

        btnClear.setBounds(236, 276, 89, 23);
        contentPane.add(btnClear);

        textAreaEmployee = new JTextArea();
        // Wrap text area in ScrollPane (Requirement #4)
        JScrollPane scrollEmployee = new JScrollPane(textAreaEmployee);
        scrollEmployee.setBounds(36, 197, 339, 68);
        contentPane.add(scrollEmployee);
    }
}