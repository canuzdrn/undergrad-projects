/**
 * House class
 * @author Can
 *
 */
public class House implements Comparable<House>{
	
	public int id;
	public int duration;
	public double rating;
	
	/**
	 * Constructor of the House class
	 * @param id
	 * @param duration
	 * @param rating
	 */
	public House(int id,int duration,double rating) {
		this.id = id;
		this.duration = duration;
		this.rating = rating;
	}
	
	@Override
	public int compareTo(House o) {
		if(this.id < o.id) {
			return -1;
		}
		else if(this.id > o.id){
			return 1;
		}
		return 0;
	}
}
