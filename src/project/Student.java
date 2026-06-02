	package project;
	
	import java.math.BigDecimal;
	
	public class Student {
	    private int id;
	    private String name;
	    private String course;
	    private BigDecimal totalFee;
	    private BigDecimal paid;
	
	    public Student(int id, String name, String course, BigDecimal totalFee, BigDecimal paid) {
	        this.id=id; this.name=name; this.course=course; this.totalFee=totalFee; this.paid=paid;
	    }
	
	    public int getId(){ return id; }
	    public String getName(){ return name; }
	    public String getCourse(){ return course; }
	    public BigDecimal getTotalFee(){ return totalFee; }
	    public BigDecimal getPaid(){ return paid; }
	    public BigDecimal getBalance(){ return totalFee.subtract(paid); }
	
	    @Override
	    public String toString(){
	        return "ID: "+id+" | Name: "+name+" | Course: "+course+
	               " | Total Fee: "+totalFee+" | Paid: "+paid+" | Balance: "+getBalance();
	    }
	}
