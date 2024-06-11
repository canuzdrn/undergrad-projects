
public class Event {
	
	public String type;
	public Player player;
	double time;
	double duration;
	
	public Event(String type, Player player, double time , double duration) {
		this.type = type;
		this.player = player;
		this.time = time;
		this.duration = duration;
	}
}
