import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainProgramB {

	static void putInHeap(int array[], int target, int rightBound){
		int largestNode = target ;
		int leftSon = 2 * target + 1 ;
		int rightSon = 2 * target + 2 ;
		if (rightSon <= rightBound){
			//look whether our target is not the main
			if ((array[target] < array[leftSon]) || (array[target] < array[rightSon])){
				//choose main
				largestNode = (array[leftSon] <= array[rightSon])?  rightSon : leftSon;
			}
			if (target != largestNode){
				int t = array[target];
				array[target] = array[largestNode];
				array[largestNode] = t;
				putInHeap(array,largestNode,rightBound ) ;
			}
		}//if right son does not exist, compare with left son
		else if ((rightSon == (rightBound + 1)) && (array[target] < array[leftSon])){
			int u = array[leftSon];
			array[leftSon] = array[target];
			array[target] = u;
		}
	}

	static void buildheap (int array[]){
		int last = array.length - 1;
		int j = (last % 2 == 0) ? last / 2 - 1 : last / 2 ;
		for (int k = j ; k >= 0 ; k-- )
			putInHeap(array, k, last) ;
	}
	
	static void heapsort(int array[]){
		buildheap(array);
		int lastIndex = array.length - 1;
		for (int i = 0; i < lastIndex; i++){
			int topElement = array[0] ;
			array[0] = array[lastIndex - i];
			array[lastIndex - i] = topElement;
			putInHeap(array, 0, lastIndex - i - 1);
		}
	}

	
	private static int[] fastReadArray(String fileName) throws IOException{
		File input = new File(fileName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(input)), 32768);
		int n = Integer.parseInt(reader.readLine());
		int[] result = new int[n];
		String numbers = reader.readLine();
		int firstDigit = 0;
		int whitespaceIndex = 0;
		for (int i = 0; i < n; i++){
			whitespaceIndex = numbers.indexOf(' ', firstDigit);
			whitespaceIndex = (whitespaceIndex == -1) ? numbers.length() : whitespaceIndex;
			int number = Integer.parseInt(numbers.substring(firstDigit, whitespaceIndex));
			result[i] = number;
			firstDigit = whitespaceIndex + 1;
		}
		reader.close();
		return result;
	}

	public static void main(String[] args) throws NumberFormatException, IOException {
		int[] array = fastReadArray("sort.in");
		//long t1 = System.currentTimeMillis();
		heapsort(array);
		File output = new File("sort.out");
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		for (int number:array){
			writer.write(number + " ");
		}
		//long t2 = System.currentTimeMillis();
		//System.out.println("Seconds elapsed:" + ((t2 - t1)/1000.0));
		writer.close();
	}

}
