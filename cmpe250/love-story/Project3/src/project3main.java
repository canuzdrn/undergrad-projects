import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

class Vertex {
	public String name;
	
	public List<Vertex> neighbors = new ArrayList<>();
	
	int distanceToSource;
	
	int index;
	
	int cost;
	
	Vertex primParent;
	
	public Vertex(String name) {
		this.name = name;
	}
}

public class project3main {
	
	/**
	 * Method that adds an edge between two node with given parameters 
	 * @param from From vertex
	 * @param to To vertex
	 * @param distance Distance between two node (weight)
	 * @param matrix Graph itself
	 * @param vertexList List of all cities (vertices)
	 */
	public static void addEdge(String from , String to , int distance, Map<Integer,Map<Integer,Integer>> matrix, Vertex[] vertexList) {
		
		int row = 0;
		int column = 0;

		
		row = findCityNumC(from) - 1;
		column = findCityNumC(to) - 1;

		vertexList[row].neighbors.add(vertexList[column]);
		
		if(matrix.containsKey(row)) {
			matrix.get(row).put(column, distance);
		}
		else {
			matrix.put(row, new HashMap<Integer, Integer>());
			matrix.get(row).put(column, distance);
		}
	}
	/**
	 * Method that adds an edge between two node with given parameters 
	 * @param from From vertex
	 * @param to To vertex
	 * @param distance Distance between two node (weight)
	 * @param matrix Graph itself
	 * @param vertexList List of all cities (vertices)
	 */
	public static void addEdge2(String from , String to , int distance, Map<Integer,Map<Integer,Integer>> matrix, Vertex[] vertexList) {
		
		int row = 0;

		int column = 0;

		if(from.charAt(0) == 'd') {
			row = findCityNumD(from);
		}
		
		if(to.charAt(0) == 'd') {
			column = findCityNumD(to);
		}

		if(matrix.containsKey(row)) {
			if(matrix.get(row).containsKey(column)) {
				if(matrix.get(row).get(column) > distance) {
					if(vertexList[row].neighbors.contains(vertexList[column]) == false) {
						vertexList[row].neighbors.add(vertexList[column]);
					}
					matrix.get(row).put(column,distance);
				}
			}
			else {
				
				vertexList[row].neighbors.add(vertexList[column]);
				
				matrix.get(row).put(column,distance);
			}
		}
		else {
			
			vertexList[row].neighbors.add(vertexList[column]);
			
			matrix.put(row, new HashMap<Integer, Integer>());
			matrix.get(row).put(column, distance);
		}
	}
	
	/**
	 * Matches the city's name to it's index
	 * @param str City name
	 * @return Index
	 */
	public static int findCityNumC(String str) {
		String temp = "";
		for(int i=0;i<str.length();i++) {
			if(str.charAt(i) != 'c') {
				temp += str.charAt(i);
			}
		}
		
		
		return Integer.valueOf(temp);
	}
	
	public static int findCityNumD(String str) {
		String temp = "";
		for(int i=0;i<str.length();i++) {
			if(str.charAt(i) != 'd') {
				temp += str.charAt(i);
			}
		}
		
		
		return Integer.valueOf(temp);
	}
	
	
	public static void main(String[] args) throws FileNotFoundException {
		

		
		Scanner in = new Scanner(new File(args[0]));
		PrintStream out = new PrintStream(new File(args[1]));
		
		
		int timeLimit = Integer.parseInt(in.nextLine());
		int totalTime = 0;
		
		boolean theyMarry = true;
		
		int sidewalkTax = 0;
		
		int numberOfVertices = Integer.parseInt(in.nextLine());
		
		String mecnunLeylaNames = in.nextLine();
		
		String mecnunCityName = mecnunLeylaNames.split(" ")[0];
		Vertex mecnun = new Vertex("");
		
		String leylaCityName =  mecnunLeylaNames.split(" ")[1];
		Vertex leyla = new Vertex("");
		
		int numOfLeftCities = findCityNumC(leylaCityName);
		
		int numOfRightCities = numberOfVertices - numOfLeftCities;
		
		Vertex[] leftCities = new Vertex[numOfLeftCities];
		
		String[] leftCityNames = new String[numOfLeftCities];
		
		for(int i=1;i<=numOfLeftCities;i++) {
			Vertex city = new Vertex("c"+i);
			city.index = i-1;
			leftCities[city.index] = city;
			leftCityNames[city.index] = city.name;
		}
		
		for(Vertex vertex : leftCities) {
			if(vertex.name.equals(mecnunCityName)) {
				mecnun = vertex;
			}
			else if(vertex.name.equals(leylaCityName)) {
				leyla = vertex;
			}
		}
		
		Map<Integer,Map<Integer,Integer>> graph = new HashMap<Integer, Map<Integer,Integer>>();


		for(int i=0;i<numOfLeftCities-1;i++) {
			String line = in.nextLine();
			String[] lineElements = line.split(" ");
			String from = lineElements[0];
			int ind = 1;
			for(int j=0;j<(lineElements.length-1)/2;j++) {
				String to = lineElements[ind];
				int distance = Integer.parseInt(lineElements[ind+1]);
				
				addEdge(from, to, distance, graph, leftCities);
				
				ind += 2;
			}
		}

		
		int[] visited = new int[numOfLeftCities];
		Arrays.fill(visited, 0);
	
		int[] distance = new int[numOfLeftCities];
		distance[0] = 0;
		for(int i=1;i<distance.length;i++) {
			distance[i] = Integer.MAX_VALUE;
		}
		
		Vertex[] prevCity = new Vertex[numOfLeftCities];
		
		List<Vertex> pathHolder = new ArrayList<Vertex>();
		
		Comparator<Vertex> VertexDistanceComparator = new Comparator<Vertex>() {
		    @Override
		    public int compare(Vertex v1, Vertex v2) {
		       if(v1.distanceToSource < v2.distanceToSource) {
		    	   return -1;
		       }
		       else if (v1.distanceToSource > v2.distanceToSource){
		    	   return 1;
		       }
		       else {
		    	   if(v1.index < v2.index) {
			    	   return -1;
			       }
			       else if (v1.index > v2.index){
			    	   return 1;
			       }
		       }
		       return 0;
		    }
		};
		
		PriorityQueue<Vertex> minPQ = new PriorityQueue<Vertex>(VertexDistanceComparator);
		
		Vertex source = mecnun;
		source.distanceToSource = 0;
		
		minPQ.add(source);
		
		while(!minPQ.isEmpty()) {
			Vertex currVertex = minPQ.poll();
			visited[currVertex.index] = 1;
			for(Vertex neighbor : currVertex.neighbors) {
				if(visited[neighbor.index] == 0) {
					int dist = currVertex.distanceToSource + graph.get(currVertex.index).get(neighbor.index);
					if(dist < distance[neighbor.index]) {
						if(minPQ.contains(neighbor)) {
							minPQ.remove(neighbor);
							distance[neighbor.index] = dist;
							neighbor.distanceToSource = dist;
							prevCity[neighbor.index] = currVertex;
							minPQ.add(neighbor);
						}
						else {
							distance[neighbor.index] = dist;
							neighbor.distanceToSource = dist;
							
							prevCity[neighbor.index] = currVertex;
							minPQ.add(neighbor);
						}
					}
					
				}
			}
		}

			
		Vertex current = leyla;
		pathHolder.add(current);
		
		while(current != null) {
			Vertex prev = prevCity[current.index];
			if(prev != null) {
				pathHolder.add(prev);
			}
			
			current = prev;
		}
		
		
		
		if(pathHolder.size() == 1) {
			out.print(-1);
		}
		else {
			Collections.reverse(pathHolder);
			for(Vertex vertex : pathHolder) {
				out.print(vertex.name + " ");
			}
		}
		
		
		totalTime = leyla.distanceToSource;
		
		if((totalTime > timeLimit) || (pathHolder.size() == 1)) {
			theyMarry = false;
		}
		
		if(theyMarry == true) {
			Map<Integer,Map<Integer,Integer>> graph2 = new HashMap<Integer, Map<Integer,Integer>>();
			
			Vertex[] rightCities = new Vertex[numOfRightCities + 1];
			String[] rightCityNames = new String[numOfRightCities + 1];
			leyla.index = 0;
			rightCities[leyla.index] = leyla;
			rightCityNames[leyla.index] = leyla.name;
			
			
			for(int i=1;i<=numOfRightCities;i++) {
				Vertex city = new Vertex("d"+i);
				city.index = i;
				rightCities[city.index] = city;
				rightCityNames[city.index] = city.name;
			}
			
			for(int i=0;i<numOfRightCities+1;i++) {
				String line = in.nextLine();
				String[] lineElements = line.split(" ");
				String from = lineElements[0];
				int index = 1;
				for(int j=0;j<(lineElements.length-1)/2;j++) {
					String to = lineElements[index];
					int weight = Integer.parseInt(lineElements[index+1]);
					
					addEdge2(from, to, weight, graph2, rightCities);
					addEdge2(to, from, weight, graph2, rightCities);
					
					index += 2;
				}
			}
			
			
			
			int[] known = new int[numOfRightCities+1];
			Arrays.fill(known, 0);
			
			int[] knownCondition = new int[numOfRightCities+1];
			Arrays.fill(knownCondition, 1);
			
			int[] cost = new int[numOfRightCities+1];
			Vertex src = leyla;
			src.cost = 0;
			cost[0] = 0;
			for(int i=1;i<cost.length;i++) {
				cost[i] = Integer.MAX_VALUE;
			}
			
			Comparator<Vertex> VertexCostComparator = new Comparator<Vertex>() {
			    @Override
			    public int compare(Vertex v1, Vertex v2) {
			       if(v1.cost < v2.cost) {
			    	   return -1;
			       }
			       else if (v1.cost > v2.cost){
			    	   return 1;
			       }
			       else {
			    	   if(v1.index < v2.index) {
				    	   return -1;
				       }
				       else if (v1.index > v2.index){
				    	   return 1;
				       }
			       }
			       return 0;
			    }
			};
			
			PriorityQueue<Vertex> costPQ = new PriorityQueue<Vertex>(VertexCostComparator);

			costPQ.add(src);
			
			while(!costPQ.isEmpty()) {
				Vertex curr = costPQ.poll();
				
				if(known[curr.index] == 0) {
					known[curr.index] = 1;
					for(Vertex neighbor : curr.neighbors) {
						if(graph2.get(curr.index).get(neighbor.index) < cost[neighbor.index] && known[neighbor.index] == 0) {
							if(costPQ.contains(neighbor)) {
								costPQ.remove(neighbor);
								cost[neighbor.index] = graph2.get(curr.index).get(neighbor.index);
								neighbor.cost = graph2.get(curr.index).get(neighbor.index);
								neighbor.primParent = curr;
								costPQ.add(neighbor);			
							}
							else {
								cost[neighbor.index] = graph2.get(curr.index).get(neighbor.index);
								neighbor.cost = graph2.get(curr.index).get(neighbor.index);
								neighbor.primParent = curr;
								costPQ.add(neighbor);						
							}
						}
						
						if(known == knownCondition) {
							break;
						}
					}
				}
			}
		
			int validCityNum = 0;
			for(Vertex vertex : rightCities) {
				if(vertex != src && vertex.cost != 0) {
					validCityNum++;
					sidewalkTax += vertex.cost;
				}	
			}
			int totalTax = sidewalkTax*2;

			if(validCityNum < numOfRightCities) {
				out.print("\n" + -2);
			}
			else {
				out.print("\n" + totalTax);
			}
		}
		else {
			out.print("\n" + -1);
		}
		
		System.out.println(graph.get(0));
	}
}
