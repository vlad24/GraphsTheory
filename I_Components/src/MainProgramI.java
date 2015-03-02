import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainProgramI{

	static class Vertex{
		public int id;
		public boolean visited;
		public Vertex(int newNumber) {
			id = newNumber;
		}
	}
	
	static class Graph{
		
		public int vertexAmount = 0;
		public int edgesAmount = 0;
		public Vertex[] vertexList;
		public Map<Integer, List<Integer>> adjacencyList;
		
		public Graph(int vertices, int edges){
			vertexAmount = vertices;
			edgesAmount = edges;
			vertexList = new Vertex[1 + vertexAmount];
			adjacencyList = new HashMap<Integer, List<Integer>>();
		}
		
		public void addEdge(Vertex start, Vertex end){
			if (vertexList[start.id] == null){
				adjacencyList.put(start.id, new ArrayList<Integer>());
			}
			if (vertexList[end.id] == null){
				adjacencyList.put(end.id, new ArrayList<Integer>());
			}
			vertexList[start.id] = start;
			vertexList[end.id] = end;
			adjacencyList.get(start.id).add(end.id);
			adjacencyList.get(end.id).add(start.id);
		}
		
		public String toString(){
			String s = "~Graph:\n";
			for (Integer x: adjacencyList.keySet()){
				s += vertexList[x].id + "|";
				for (Integer n: adjacencyList.get(x)){
					s += vertexList[n].id + " ";
				}
				s += "\n";
			}
			return s;
		}
		
		
		public List<Integer> getNeighbours(Vertex vertex){
			return adjacencyList.get(vertex.id);
		}
		
		public int[] findComponents(){
			int[] answer = new int[1 + vertexAmount];
			int[] seekInfo = {0}; //maxComponentNumber
			for (int i = 1; i < vertexList.length; i++){
				Vertex vertex = vertexList[i];
				if (vertex != null){
					if (!vertex.visited){
						++seekInfo[0];
						updateAnswerDFS(vertex, seekInfo, answer);
					}
				}else{
					++seekInfo[0];
					answer[i] = seekInfo[0];
				}
				
			}
			answer[0] = seekInfo[0];
			return answer;
		}
		
		public void updateAnswerDFS(Vertex current, int[] seekInfo, int[] answer){
			current.visited = true;//visit the vertex
			answer[current.id] = seekInfo[0];//bind the vertex to some component 
			List<Integer> neighbours = getNeighbours(current); //done here. look at neighbours.
			if (!(neighbours == null)){
				for (Integer neighbour: neighbours){
					if (!(vertexList[neighbour].visited)){
						updateAnswerDFS(vertexList[neighbour], seekInfo, answer);
					}
				}
			}
			return;
		}
		
		
	}
	
	private static long startMoment = 0L;

	
	private static void checkTime(boolean start){
		if (start){
			startMoment = System.currentTimeMillis();
		}else{
			System.out.println("\nSeconds spent: " + ((System.currentTimeMillis() - startMoment) / 1000.0));
		}
	}
	
	
	
	private static String inputFileName = "connect.in";
	private static String outputFileName = "connect.out";
	
	public static void main(String[] args) throws IOException {
		checkTime(true);
		File input = new File(inputFileName);
		File output = new File(outputFileName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(input)), 32768);
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		String[] nm = reader.readLine().split(" ");
		int n = Integer.parseInt(nm[0]); 
		int m = Integer.parseInt(nm[1]);
		System.out.println("Processing " + n + " vertices and " + m + " edges.");
		Graph graph = new Graph(n, m);
		for (int k = 0; k < m; k++){
			String query = reader.readLine();	
			//System.out.println(query);
			String[] parameters = query.split(" ");
			Vertex start = new Vertex(Integer.parseInt(parameters[0]));
			Vertex end = new Vertex(Integer.parseInt(parameters[1]));
			graph.addEdge(start, end);
		}
		//System.out.println(graph);
		System.out.println("Seeking for components...");
		int[] answer = graph.findComponents();
		writer.write(answer[0] + "\n");
		System.out.println(answer[0] + " component\nAnswer: ");
		for (int i = 1; i < answer.length; i++){
			if (i == answer.length - 1){
				writer.write(answer[i] + "");
			}else{
				writer.write(answer[i] + " ");
			}
			System.out.print(answer[i] + " ");
		}
		reader.close();
		writer.close();
		checkTime(false);
		System.out.println("\nFinished.");
	}

}
