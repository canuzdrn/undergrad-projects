
public class Physiotherapist implements Comparable<Physiotherapist>{
	public int id;
	public double serviceDuration;
	
	public boolean busy = false;
	
	public Physiotherapist(int id, double serviceDuration){
		this.id = id;
		this.serviceDuration = serviceDuration;
	}


	@Override
	public int compareTo(Physiotherapist o) {
		if(this.id < o.id) {
			return -1;
		}
		else if(this.id > o.id) {
			return 1;
		}
		return 0;
	}
}
