import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

/**
 * Main class that handles the input output operations and implements some of the functions
 * @author Can
 *
 */
public class project1main { 
	/**
	 * Finds the max rating belongs in available houses
	 * @param ts TreeSet of available houses
	 * @return maximum rating
	 */
	public static double findTheMaxRating(TreeSet<House> ts) {
		PriorityQueue<Double> ratings = new PriorityQueue<Double>(Collections.reverseOrder());
		
		for(House hs : ts) {
			ratings.add(hs.rating);
		}
		return ratings.peek();
	}
/**
 * Handles the operations on the arranged house
 * @param ts TreeSet of available houses
 * @param st The student who is eligible to attend to a house
 * @param filled TreeSet of filled houses
 */
	public static void arrangeHouse(TreeSet<House> ts , Student st ,TreeSet<House> filled) {
		Iterator<House> ith = ts.iterator();
		while(ith.hasNext()) {
			House hs = ith.next();
			if(st.rating <= hs.rating) {
				hs.duration += st.duration;
				filled.add(hs);
				ith.remove();
				break;
			}
		}
	}
	/**
	 * The method that checks the zero durations at the starting semester of the program
	 * @param ts0 TreeSet of waiting students 
	 * @param ts1 TreeSet of graduates
	 */
	public static void gradCheck(TreeSet<Student> ts0,TreeSet<Student> ts1) {
		Iterator<Student> itstudent = ts0.iterator();
		
		while(itstudent.hasNext()) {
			Student s = itstudent.next();
			if(s.duration == 0) {
				ts1.add(s);
				itstudent.remove();
			}
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		
		Scanner in = new Scanner(new File(args[0]));
		PrintStream out = new PrintStream(new File(args[1]));
		
		PriorityQueue<Integer> semesters = new PriorityQueue<Integer>(Collections.reverseOrder());
		
		TreeSet<House> availableHouses = new TreeSet<House>();
		TreeSet<House> filledHouses = new TreeSet<House>();
		
		TreeSet<Student> waitingStudents = new TreeSet<Student>();
		TreeSet<Student> waitedGraduates = new TreeSet<Student>();
		
		while(in.hasNextLine()) {
			String[] line = in.nextLine().split(" ");
			if(line[0].equals("h")) {
				House h = new House(Integer.parseInt(line[1]), Integer.parseInt(line[2]), Double.parseDouble(line[3]));
				if(line[2].equals("0")) {
					availableHouses.add(h);
				}
				else {
					filledHouses.add(h);
				}
			}
			else{
				Student s = new Student(Integer.parseInt(line[1]),line[2], Integer.parseInt(line[3]), Double.parseDouble(line[4]));
				semesters.add(Integer.parseInt(line[3]));
				waitingStudents.add(s);
			}
		}
		int maxSemester = semesters.peek();
		
		gradCheck(waitingStudents,waitedGraduates);
		
		for(int i=0;i<maxSemester;i++) {
			//***************************************************	START OF THE SEMESTER	***************************************************
			Iterator<Student> its = waitingStudents.iterator();
				
			while(its.hasNext() && availableHouses.size() != 0) {
				Student st = its.next();
				if(st.rating <= findTheMaxRating(availableHouses)) {
					arrangeHouse(availableHouses, st, filledHouses);
					its.remove();
				}
			}
			//************************************************************************************************************************

			//***************************************************	END OF THE SEMESTER	***************************************************
			Iterator<Student> iterS = waitingStudents.iterator();
			
			while(iterS.hasNext()) {
				Student st = iterS.next();
				st.duration--;
				if(st.duration == 0) {
					waitedGraduates.add(st);
					iterS.remove();
				}
				else {
					continue;
				}
			}
	
			Iterator<House> iterH = filledHouses.iterator();
		
			while(iterH.hasNext()) {
				House hs = iterH.next();
				hs.duration--;
				if(hs.duration == 0) {
					availableHouses.add(hs);
					iterH.remove();
				}
				else {
					continue;
				}
			}
		}
		for(Student st : waitedGraduates) {
			out.println(st.name);
		}
	}
}
