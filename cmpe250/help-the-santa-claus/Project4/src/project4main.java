import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

class Vertex {
	public String name;
	public int capacity;
	public String type;
	public String region;
	public boolean hasA = false;
	
	public List<Vertex> neighbors = new ArrayList<>();
	
	public int index = 0;
	
	public Vertex(String name , String type, String region , int capacity) {
		this.name = name;
		this.type = type;
		this.region= region;
		this.capacity = capacity;
	}
}

public class project4main {
	/**
	 * This methods doing a breadth first search through the given source vertex to terminal vertex 
	 * While searching it arranges the level of the vertices by current vertex/neighbor vertex relation.
	 * In the algorithm it runs multiple times (probably) until returning false (until there is no path from source to sink)
	 * @param source Source vertex
	 * @param terminal Sink vertex
	 * @param allVertices List of all vertices in the graph
	 * @param graph Graph
	 * @param level Level array
	 * @return Returns true if there is a path from source to sink
	 */
	static boolean levelBFS(Vertex source ,Vertex terminal, ArrayList<Vertex> allVertices,Map<Vertex,Map<Vertex,Integer>> graph, int[] level) {
		
		Arrays.fill(level, -1);
		level[source.index] = 0;
		
		Queue<Vertex> q = new LinkedList<Vertex>();
		q.add(source);
		
		while(!q.isEmpty()) {
			Vertex curr = q.poll();
			for(Vertex neighbor : curr.neighbors) {
				if(level[neighbor.index] < 0 && graph.get(curr).get(neighbor) > 0) {
					level[neighbor.index] = level[curr.index] + 1;
					q.add(neighbor);
				}
			}
		}
		return level[terminal.index] != -1;
	}
	
	/**
	 * This method runs a depth first search through source to sink (if there is a path)
	 * While searching it finds the blocking flow and arrange the graph according to path and the blocking flow using backtracking
	 * Also there is an optimization in order not to check the same dead end multiple times (with storing a dead end array and reseting the values of the list every single time after bfs)
	 * @param graph Graph
	 * @param curr Selected current vertex
	 * @param destination Destination Vertex
	 * @param myMinFlow Founded min flow
	 * @param level Level list
	 * @param deadEndList Dead end array for the dead end optimization
	 * @return returns the founded dfs value (0 (zero) if there is none)
	 */
	static int augmentingDFS(Map<Vertex,Map<Vertex,Integer>> graph,Vertex curr, Vertex destination,int myMinFlow , int[] level,int[] deadEndList) {
		
		if(curr == destination) {
			return myMinFlow;
		}
		
		for(;deadEndList[curr.index]<curr.neighbors.size();deadEndList[curr.index]++) {
			Vertex neighbor = curr.neighbors.get(deadEndList[curr.index]);
			if(level[neighbor.index] == level[curr.index] +1 && graph.get(curr).get(neighbor) > 0) {
				int nextFlow = Math.min(myMinFlow, graph.get(curr).get(neighbor));
				
				int dfsVal = augmentingDFS(graph,neighbor,destination,nextFlow,level,deadEndList);
				
				if(dfsVal > 0) {
					int oldcap = graph.get(curr).get(neighbor);
					int newcap = oldcap - dfsVal;
					graph.get(curr).replace(neighbor, newcap);
					
					int prevback = graph.get(neighbor).get(curr);
					int newback = prevback + dfsVal;
					graph.get(neighbor).replace(curr, newback);
					
					return dfsVal;
				}
			}
		}
		return 0;
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		
		Scanner in = new Scanner(new File(args[0]));
		PrintStream out = new PrintStream(new File(args[1]));
		
		ArrayList<Vertex> allVertices = new ArrayList<Vertex>();
		ArrayList<Vertex> bags = new ArrayList<Vertex>();
		ArrayList<Vertex> vehicles = new ArrayList<Vertex>();
		
		int numOfGifts = 0;
		
		int numOfGreenTrain = in.nextInt();
		
		for(int i=0; i<numOfGreenTrain;i++) {
			int capacity = in.nextInt();
			if(capacity != 0) {
				Vertex v = new Vertex("v" + vehicles.size(), "train", "green", capacity);
				vehicles.add(v);
			}
		}
		
		int numOfRedTrain = in.nextInt();
		
		for(int i=0; i<numOfRedTrain;i++) {
			int capacity = in.nextInt();
			if(capacity != 0) {
				Vertex v = new Vertex("v" + vehicles.size(), "train", "red", capacity);
				vehicles.add(v);
			}
		}
		
		int numOfGreenReindeer = in.nextInt();
		
		for(int i=0; i<numOfGreenReindeer;i++) {
			int capacity = in.nextInt();
			if(capacity != 0) {
				Vertex v = new Vertex("v" + vehicles.size(), "reindeer", "green", capacity);
				vehicles.add(v);
			}
		}
		
		int numOfRedReindeer = in.nextInt();
		
		for(int i=0; i<numOfRedReindeer;i++) {
			int capacity = in.nextInt();
			if(capacity != 0) {
				Vertex v = new Vertex("v" + vehicles.size(), "reindeer", "red", capacity);
				vehicles.add(v);
			}
		}

		
		int numOfBags = in.nextInt();
		
		String type = "";
		String region = "";
		for(int i=0; i<numOfBags;i++) {
			String name = in.next();
			int gifts = in.nextInt();
			boolean found = false;
			for(Vertex vertex : bags) {
				if(vertex.name.equals(name) && name.contains("a") == false) {
					vertex.capacity += gifts;
					found = true;
				}
			}
			if (found == false) {
				if (name.contains("d") == false && name.contains("e") == false) {
					type = "both";
				}
				else {
					if(name.contains("d")) {
						type = "train";
					}
					else if(name.contains("e")) {
						type = "reindeer";
					}
				}
					
				if (name.contains("b") == false && name.contains("c") == false) {
					region = "both";
				}
				else {
					if(name.contains("b")) {
						region = "green";
					}
					else if(name.contains("c")) {
						region = "red";
					}
				}
				if(gifts != 0) {
					Vertex b = new Vertex(name,type,region,gifts);
					if(b.name.contains("a")) {
						b.hasA = true;
					}
					bags.add(b);
				}
			}
		}

		Map<Vertex,Map<Vertex,Integer>> graph = new HashMap<Vertex,Map<Vertex,Integer>>();
		
		Vertex source = new Vertex("source", "", "", 0);
		graph.put(source,new HashMap<Vertex,Integer>());
		source.index = allVertices.size();
		allVertices.add(source);
		
		for(Vertex bag : bags) {
			bag.index = allVertices.size();
			allVertices.add(bag);
			numOfGifts += bag.capacity;
			graph.get(source).put(bag, bag.capacity);
			source.neighbors.add(bag);
			graph.put(bag,new HashMap<Vertex,Integer>());
			graph.get(bag).put(source, 0);
			bag.neighbors.add(source);
			if(bag.type.equals("both") == false && bag.region.equals("both") == false) {
				for(Vertex vehicle : vehicles) {
					if(vehicle.type.equals(bag.type) && vehicle.region.equals(bag.region)) {
						if(bag.hasA == false) {
							graph.get(bag).put(vehicle, bag.capacity);
							bag.neighbors.add(vehicle);
							if(!graph.containsKey(vehicle)) {
								graph.put(vehicle, new HashMap<Vertex,Integer>());
								graph.get(vehicle).put(bag, 0);
							}
							else{
								graph.get(vehicle).put(bag, 0);
							}
							vehicle.neighbors.add(bag);
						}
						else {
							graph.get(bag).put(vehicle, 1);
							bag.neighbors.add(vehicle);
							if(!graph.containsKey(vehicle)) {
								graph.put(vehicle, new HashMap<Vertex,Integer>());
								graph.get(vehicle).put(bag, 0);
							}
							else{
								graph.get(vehicle).put(bag, 0);
							}
							vehicle.neighbors.add(bag);
						}
					}
				}
			}
			if(bag.type.equals("both") == false && bag.region.equals("both") == true) {
				for(Vertex vehicle : vehicles) {
					if(vehicle.type.equals(bag.type)) {
						if(bag.hasA == false) {
							graph.get(bag).put(vehicle, bag.capacity);
							bag.neighbors.add(vehicle);
							if(!graph.containsKey(vehicle)) {
								graph.put(vehicle, new HashMap<Vertex,Integer>());
								graph.get(vehicle).put(bag, 0);
							}
							else{
								graph.get(vehicle).put(bag, 0);
							}
							vehicle.neighbors.add(bag);
						}
						else {
							graph.get(bag).put(vehicle, 1);
							bag.neighbors.add(vehicle);
							if(!graph.containsKey(vehicle)) {
								graph.put(vehicle, new HashMap<Vertex,Integer>());
								graph.get(vehicle).put(bag, 0);
							}
							else{
								graph.get(vehicle).put(bag, 0);
							}
							vehicle.neighbors.add(bag);
						}
					}
				}
			}
			if(bag.type.equals("both") == true && bag.region.equals("both") == false) {
				for(Vertex vehicle : vehicles) {
					if(vehicle.region.equals(bag.region)) {
						if(bag.hasA == false) {
							graph.get(bag).put(vehicle, bag.capacity);
							bag.neighbors.add(vehicle);
							if(!graph.containsKey(vehicle)) {
								graph.put(vehicle, new HashMap<Vertex,Integer>());
								graph.get(vehicle).put(bag, 0);
							}
							else{
								graph.get(vehicle).put(bag, 0);
							}
							vehicle.neighbors.add(bag);
						}
						else {
							graph.get(bag).put(vehicle, 1);
							bag.neighbors.add(vehicle);
							if(!graph.containsKey(vehicle)) {
								graph.put(vehicle, new HashMap<Vertex,Integer>());
								graph.get(vehicle).put(bag, 0);
							}
							else{
								graph.get(vehicle).put(bag, 0);
							}
							vehicle.neighbors.add(bag);
						}
					}
				}
			}
			if(bag.type.equals("both") == true && bag.region.equals("both") == true) {
				for(Vertex vehicle : vehicles) {
					if(bag.hasA == false) {
						graph.get(bag).put(vehicle, bag.capacity);
						bag.neighbors.add(vehicle);
						if(!graph.containsKey(vehicle)) {
							graph.put(vehicle, new HashMap<Vertex,Integer>());
							graph.get(vehicle).put(bag, 0);
						}
						else{
							graph.get(vehicle).put(bag, 0);
						}
						vehicle.neighbors.add(bag);
					}
					else {
						graph.get(bag).put(vehicle, 1);
						bag.neighbors.add(vehicle);
						if(!graph.containsKey(vehicle)) {
							graph.put(vehicle, new HashMap<Vertex,Integer>());
							graph.get(vehicle).put(bag, 0);
						}
						else{
							graph.get(vehicle).put(bag, 0);
						}
						vehicle.neighbors.add(bag);
					}
				}
			}
		}
		
		Vertex terminal = new Vertex("terminal", "", "", 0);
		graph.put(terminal,new HashMap<Vertex,Integer>());
		for(Vertex vehicle : vehicles) {
		vehicle.index = allVertices.size();
		allVertices.add(vehicle);
		if(!graph.containsKey(vehicle)) {
			graph.put(vehicle,new HashMap<Vertex,Integer>());
		}
		graph.get(vehicle).put(terminal, vehicle.capacity);
		vehicle.neighbors.add(terminal);
		graph.get(terminal).put(vehicle, 0);
		terminal.neighbors.add(vehicle);
		}
		terminal.index = allVertices.size();
		allVertices.add(terminal);

		int[] level = new int[allVertices.size()];
		int maxFlow = 0;
		
		while(levelBFS(source, terminal, allVertices, graph, level)) {
			int[] deadEndList = new int[allVertices.size()];
			int flow = augmentingDFS(graph, source, terminal, Integer.MAX_VALUE, level,deadEndList);
			while(flow > 0) {
				maxFlow += flow;
				flow = augmentingDFS(graph, source, terminal, Integer.MAX_VALUE, level,deadEndList);
			}
		}
		out.print(numOfGifts - maxFlow);
	}
}
