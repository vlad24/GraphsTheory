import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;


public class MainProgram {
	
//	private static long startMoment = 0L;
//	private static void checkTime(boolean start){
//		if (start){
//			startMoment = System.currentTimeMillis();
//		}else{
//			System.out.println("Seconds spent: " + ((System.currentTimeMillis() - startMoment) / 1000.0));
//		}
//	}
	
	public static void main(String[] args) throws IOException{
//		System.out.println("Started");
//		checkTime(true);
		File input = new File("sort.in");
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(input)), 32768);
		int n = Integer.parseInt(reader.readLine());
		String numbers = reader.readLine();
		int[] counts = new int[1 + 1000];
		int firstDigit = 0;
		int whitespaceIndex = 0;
		for (int i = 1; i <= n; i++){
			whitespaceIndex = numbers.indexOf(' ', firstDigit);
			whitespaceIndex = (whitespaceIndex == -1) ? numbers.length() : whitespaceIndex;
			int number = Integer.parseInt(numbers.substring(firstDigit, whitespaceIndex));
			counts[number]++;
			firstDigit = whitespaceIndex + 1;
		}
		File output = new File("sort.out");
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		for (int number = 1; number <= 1000; number++){
			for (int count = 1; count <= counts[number]; count++){
				writer.write(number + " ");
			}
		}
		reader.close();
		writer.close();
//		checkTime(false);
//		System.out.println("Finished");
		
	}
	
}
