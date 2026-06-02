package project;
import java.sql.*;
import java.math.BigDecimal;
import java.util.*;
public class FeeDAO {
    public FeeDAO(){ initTables(); }
    private void initTables(){
        String s1="CREATE TABLE IF NOT EXISTS students (id INT AUTO_INCREMENT PRIMARY KEY,name VARCHAR(100),course VARCHAR(100),total_fee DECIMAL(10,2),paid DECIMAL(10,2) DEFAULT 0)";
        String s2="CREATE TABLE IF NOT EXISTS payments (id INT AUTO_INCREMENT PRIMARY KEY,student_id INT,amount DECIMAL(10,2),method VARCHAR(50),pay_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,FOREIGN KEY(student_id) REFERENCES students(id) ON DELETE CASCADE)";
        try(Connection c=DBConnection.getConnection(); Statement st=c.createStatement()){
            st.execute(s1); st.execute(s2);
        }catch(Exception e){ e.printStackTrace(); }
    }
    public int addStudent(String name,String course,BigDecimal fee) throws Exception{
        String sql="INSERT INTO students(name,course,total_fee,paid) VALUES(?,?,?,0)";
        Connection c=DBConnection.getConnection();
        PreparedStatement ps=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
        ps.setString(1,name); ps.setString(2,course); ps.setBigDecimal(3,fee);
        ps.executeUpdate();
        ResultSet rs=ps.getGeneratedKeys();
        return rs.next()? rs.getInt(1):-1;
    }
    public Student getStudent(int id) throws Exception{
        String sql="SELECT * FROM students WHERE id=?";
        Connection c=DBConnection.getConnection();
        PreparedStatement ps=c.prepareStatement(sql);
        ps.setInt(1,id);
        ResultSet r=ps.executeQuery();
        if(r.next()){
            return new Student(r.getInt("id"),r.getString("name"),r.getString("course"),
                               r.getBigDecimal("total_fee"),r.getBigDecimal("paid"));
        }
        return null;
    }
    public void pay(int id, BigDecimal amt, String method) throws Exception{
        Connection c=DBConnection.getConnection();
        c.setAutoCommit(false);
        try{
            PreparedStatement ps1=c.prepareStatement("INSERT INTO payments(student_id,amount,method) VALUES(?,?,?)");
            ps1.setInt(1,id); ps1.setBigDecimal(2,amt); ps1.setString(3,method);
            ps1.executeUpdate();
            PreparedStatement ps2=c.prepareStatement("UPDATE students SET paid=paid+? WHERE id=?");
            ps2.setBigDecimal(1,amt); ps2.setInt(2,id);
            ps2.executeUpdate();
            c.commit();
        }catch(Exception e){ c.rollback(); throw e; }
    }
    public List<String> getPayments(int id) throws Exception{
        List<String> list=new ArrayList<>();
        Connection c=DBConnection.getConnection();
        PreparedStatement ps=c.prepareStatement("SELECT amount,method,pay_time FROM payments WHERE student_id=? ORDER BY pay_time");
        ps.setInt(1,id);
        ResultSet rs=ps.executeQuery();
        while(rs.next()){
            list.add(rs.getBigDecimal("amount")+" | "+rs.getString("method")+" | "+rs.getTimestamp("pay_time"));
        }
        return list;
    }
    public List<Student> getAllStudents() throws Exception{
        List<Student> list=new ArrayList<>();
        Connection c=DBConnection.getConnection();
        PreparedStatement ps=c.prepareStatement("SELECT * FROM students ORDER BY id");
        ResultSet r=ps.executeQuery();
        while(r.next()){
            list.add(new Student(r.getInt("id"),r.getString("name"),r.getString("course"),
                                 r.getBigDecimal("total_fee"),r.getBigDecimal("paid")));
        }
        return list;
    }
}