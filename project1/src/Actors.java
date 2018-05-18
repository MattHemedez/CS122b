public class Actors {
	private String id;
	
	private String name;
	
	private String dob;

	public Actors(){
	}
	

	public String getId(){
		return id;
	}

	public void setId(String id){
		this.id=id;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name=name;
	}
	
	public String getDOB(){
		return dob;
	}
	
	public void setDOB(String year){
		this.dob = year;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Star Details - ");
		sb.append("Id:" + getId());
		sb.append(", ");
		sb.append("Name:" + getName());
		sb.append(", ");
		sb.append("DOB:" + getDOB());
		sb.append(".");
		
		return sb.toString();
	}
}
