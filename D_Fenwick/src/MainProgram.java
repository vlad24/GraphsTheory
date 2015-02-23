import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;


public class MainProgram {
	
	private static class FenwickTree{
		//
		private long[] elements; //the mere elements
		private long[] fenwickSums;// s[i] = sum from fenwickPrevious(i) to i
		//
		public FenwickTree(long[] newElements){
			if (newElements != null){
				elements = new long[newElements.length];
				fenwickSums = new long[elements.length];
				for (int i = 0; i < newElements.length; i++){
					updateElement(i, newElements[i]);
				}
			}
		}

		private int fenwickPrevious(int x){
			// 1110011 -> 1110000
			return x & (x + 1);
		}

		private int fenwickNext(int x){
			// 1110011 -> 1110111
			return x | (x + 1);
		}

		public long sumAtRange(int left, int right) throws Exception{
			if (left == 0){
				return sumFromStart(right);
			}else{
				return sumFromStart(right) - sumFromStart(left - 1);
			}
		}

		private long sumFromStart(int right){
			long sum = 0;
			while(right >= 0){
				sum += fenwickSums[right];
				right = fenwickPrevious(right) - 1;
			}
			return sum;
		}

		public void updateElement(int vertex, long newValue){
			long delta =  newValue - elements[vertex];
			elements[vertex] += delta;
			while (vertex < elements.length){
				fenwickSums[vertex] += delta;
				vertex = fenwickNext(vertex);
			}
		}

	}
	
	public static void main(String[] args) throws Exception {
		File input = new File("sum.in");
		File output = new File("sum.out");
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(input)), 32768);
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		String[] startParameters = reader.readLine().split(" ");
		int treeSize = Integer.parseInt(startParameters[0]);
		int queryAmount = Integer.parseInt(startParameters[1]);
		//System.out.println("Processing " + treeSize + " elements and " + queryAmount + " queries");
		long[] numbers = new long[treeSize];
		FenwickTree tree = new FenwickTree(numbers);
		for (int k = 0; k < queryAmount; k++){
			String command = reader.readLine();
			String[] commandParameters = command.split(" ");
			if (commandParameters[0].equals("A")){
				int i = Integer.parseInt(commandParameters[1]) - 1;
				int newValue = Integer.parseInt(commandParameters[2]);
				tree.updateElement(i, newValue);
			}else{
				int left = Integer.parseInt(commandParameters[1]) - 1;
				int right = Integer.parseInt(commandParameters[2]) - 1;
				long sum = tree.sumAtRange(left, right);
				if (k == queryAmount - 1) 
					writer.write(sum + "");
				else
					writer.write(sum + "\n");
			}
		}
		reader.close();
		writer.close();
		//System.out.println("Finished");
	}

}
