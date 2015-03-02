import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Stack;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainProgramJ{

	static class Vertex{
		public int id;
		public int visited;
		public Vertex(int newNumber) {
			id = newNumber;
		}
	}
	
	static class Graph{
		
		public static int FRESH = 0;
		public static int IN_PROGRESS = 1;
		public static int VISITED = 2;
		
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
		
		public void addEdge(Vertex start, Vertex end, boolean oriented){
			if (vertexList[start.id] == null){
				adjacencyList.put(start.id, new Stack<Integer>());
			}
			if (vertexList[end.id] == null){
				adjacencyList.put(end.id, new Stack<Integer>());
			}
			vertexList[start.id] = start;
			vertexList[end.id] = end;
			adjacencyList.get(start.id).add(end.id);
			if (!oriented){
				adjacencyList.get(end.id).add(start.id);
			}
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
		
		public Stack<Integer> topSort() throws Exception{
			Stack<Integer> answer = new Stack<Integer>();
			for (int i = 1; i <= vertexAmount; i++){
				Vertex vertex = vertexList[i];
				if (vertex != null){
					if (vertex.visited == FRESH){
						updateAnswerDFS(vertex, answer);
					}
				}else{
					answer.push(i);
				}
			}
			return answer;
		}
		
		public void updateAnswerDFS(Vertex current, Stack<Integer> answer) throws Exception{
			current.visited = IN_PROGRESS;//mark the vertex
			List<Integer> neighbours = getNeighbours(current); //look at neighbours.
			if (!(neighbours == null)){
				for (Integer neighbour: neighbours){
					if ((vertexList[neighbour].visited) == FRESH){
						updateAnswerDFS(vertexList[neighbour], answer);
					}else if((vertexList[neighbour].visited) == IN_PROGRESS){
						throw new Exception("Incorrect graph");
					}
				}
			}
			current.visited = VISITED;
			answer.push(current.id); 
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
	
	
	private static String inputFileName = "topsort.in";
	private static String outputFileName = "topsort.out";
	
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
			System.out.println(query);
			String[] parameters = query.split(" ");
			Vertex start = new Vertex(Integer.parseInt(parameters[0]));
			Vertex end = new Vertex(Integer.parseInt(parameters[1]));
			graph.addEdge(start, end, true);
		}
		//System.out.println(graph);
		System.out.println("sorting...");
		try{
			Stack<Integer> answer = graph.topSort();
			int gotElements = answer.size();
			for (int i = 0; i < gotElements; i++){
				Integer vertexId = answer.pop();
				if (i == gotElements - 1){
					writer.write(vertexId + "");
				}else{
					writer.write(vertexId + " ");
				}
				System.out.print(vertexId + " ");
			}
		}catch (Exception e) {
			//System.out.println("D*mn!");
			writer.write(-1 + "");
		}
		reader.close();
		writer.close();
		checkTime(false);
		System.out.println("\nFinished.");
	}

}
