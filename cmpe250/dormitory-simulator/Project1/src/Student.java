/**
 * Student Class
 * @author Can
 *
 */
public class Student implements Comparable<Student>{
	public int id;
	public String name;
	public int duration;
	public double rating;
	/**
	 * Constructor of the Student class
	 * @param id
	 * @param name
	 * @param duration
	 * @param rating
	 */
	public Student(int id, String name,int duration,double rating) {
		this.id = id;
		this.name = name;
		this.duration = duration;
		this.rating = rating;
	}
	
	@Override
	public int compareTo(Student o) {
		if(this.id < o.id) {
			return -1;
		}
		else if(this.id > o.id){
			return 1;
		}
		return 0;
	}
}
