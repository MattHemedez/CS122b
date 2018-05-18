public class StarsInMovies {
	private String id;
	
	private String name;
	


	public StarsInMovies(){
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
	


	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Star In Movies Details - ");
		sb.append("MovieId:" + getId());
		sb.append(", ");
		sb.append("Name:" + getName());
		sb.append(".");
		
		return sb.toString();
	}
}
