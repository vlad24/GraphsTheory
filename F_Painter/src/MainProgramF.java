import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;


public class MainProgramF{

	private static class TreeElement{
		//
		public int value;
		public int leftControl;
		public int rightControl;
		public int buffer;
		public boolean buffered;
		//
		public TreeElement(int newVal, int leftControl, int rightControl) {
			super();
			this.value = newVal;
			this.leftControl = leftControl;
			this.rightControl = rightControl;
			this.buffered = false;
			this.buffer = -1;
		}
	}
	

	private static class SegmentTreeLazy{
		//
		private int leafLineStart;
		private int actualElementAmount;
		private TreeElement[] treeElements;
		//
		public SegmentTreeLazy(int[] result){
			actualElementAmount = result.length;
			Integer twoPowerInf = Integer.highestOneBit(actualElementAmount);
			Integer leafLineLength = ((actualElementAmount & (actualElementAmount - 1)) == 0) ? twoPowerInf : (twoPowerInf << 1) ;
			int totalCapacity = (leafLineLength << 1) - 1;
			treeElements = new TreeElement[totalCapacity];
			leafLineStart = totalCapacity - leafLineLength; 
			for (int i = totalCapacity - 1; i >= 0; i--){
				if (i >= totalCapacity - (leafLineLength - actualElementAmount)){
					treeElements[i] = new TreeElement(2, i - leafLineStart, i - leafLineStart);	
				}
				else if (i >= totalCapacity - leafLineLength){
					treeElements[i] = new TreeElement(result[i - leafLineStart], i - leafLineStart ,i - leafLineStart);
				}
				else{
					int newVal = sum(treeElements[2 * i + 1], treeElements[2 * i + 2]);
					int newLeft = treeElements[2 * i + 1].leftControl;
					int newRight = treeElements[2 * i + 2].rightControl;
					treeElements[i] = new TreeElement(newVal, newLeft, newRight);
				}

			}
		}
		

	    public void printBinaryTree(int vertex, int level){
	        if(vertex >= treeElements.length)
	             return;
	        printBinaryTree(2 * vertex + 2, level + 1);
	        if(level != 0){
	            for(int i = 0; i < level - 1;i++)
	                System.out.print("|\t");
	                System.out.println("|-------" + treeElements[vertex].value + "(" + treeElements[vertex].buffer + ")" );
	        }
	        else
	            System.out.println(treeElements[vertex].value + "(" + treeElements[vertex].buffer + ")");
	        printBinaryTree(2 * vertex + 1, level+1);
	    }  
		
		private void printTree(){
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
			printBinaryTree(0, 0);
			System.out.print("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< \n");
		}
		

		private boolean isLeaf(int vertexNumber){
			return treeElements[vertexNumber].leftControl == treeElements[vertexNumber].rightControl;
		}

		private int sum(TreeElement a, TreeElement b){
			return (a.value + b.value) / 2;
		}

		public double sumAtSegmentFromUp(int left, int right) throws Exception{
			//System.out.println(" + Sum query for ["+left + " ; "+ right +"]");
			if ((left <= right) && (left >= 0) && (right < actualElementAmount)){
					return sumAtSegmentRecursive(0, left, right);
			}else
				throw new Exception("Incorrect query : [" + left + ";" + right + "]");
		}

		private double sumAtSegmentRecursive(int vertexNumber, int left, int right){
			TreeElement current = treeElements[vertexNumber];
			if ((current.leftControl == left) && (current.rightControl == right)){
				if (current.buffered){
					return sum(treeElements[2 * vertexNumber + 1], treeElements[2 * vertexNumber + 2]);
				}else{
					return current.value;
				}
			}else{
				pushBufferDeeper(vertexNumber);
				double leftSum = 0;
				double rightSum = 0;
				if (left <= treeElements[2 * vertexNumber + 1].rightControl){
					leftSum = sumAtSegmentRecursive(2 * vertexNumber + 1, left, Math.min(treeElements[2*vertexNumber+1].rightControl, right));
				}
				if (right >= treeElements[2 * vertexNumber + 2].leftControl){
					rightSum = sumAtSegmentRecursive(2 * vertexNumber + 2, Math.max(treeElements[2 * vertexNumber + 2].leftControl, left), right);
				}
				return (leftSum + rightSum) / 2.0; 
			}
		}
		
		public void updateSegmentLazy(int leftBound, int rightBound, int newValue){
			updateSegmentLazyRecursive(0, leftBound, rightBound, newValue);
		}

		private int updateSegmentLazyRecursive(int vertex, int leftBound, int rightBound, int newValue){
			TreeElement current = treeElements[vertex];
			//look if current node is the target segment
			if ((current.leftControl == leftBound) && (current.rightControl == rightBound)){
				//here we use lazy approach
				if (isLeaf(vertex)){
					//no need to buffer, update straight
					current.value = newValue;
					return current.value;
				}else{
					//update the value, set the buffer that will be pushed later! LAZY!
					System.out.println(vertex + " is now buffered");
					current.buffered = true;
					current.buffer = newValue;
					current.value = newValue;
					return current.value;
				}
			//need to go deeper!
			}else{
				//To save some old changes push them down, updating values. 
				pushBufferDeeper(vertex);
				int leftSon = 2 * vertex + 1;
				int rightSon = 2 * vertex + 2;
				int newLeft = treeElements[leftSon].value;
				int newRight = treeElements[rightSon].value;
				// Look if left son needs to be updated if it intersects with target segment.
				if (treeElements[leftSon].rightControl >= leftBound){
					newLeft = updateSegmentLazyRecursive(leftSon, leftBound, Math.min(treeElements[leftSon].rightControl, rightBound), newValue);
				}
				// Look if right son needs to be updated if it intersects with target segment.
				if (treeElements[rightSon].leftControl <= rightBound){
					newRight = updateSegmentLazyRecursive(rightSon, Math.max(treeElements[rightSon].leftControl, leftBound), rightBound, newValue);
				}
				current.value = (newLeft + newRight) / 2;
				return current.value;
			}
		}
				
		private void pushBufferDeeper(int vertex){
			TreeElement current = treeElements[vertex];
			if (current.buffered){
				System.out.println("Pushing deeper from " + vertex);
				int leftSon = 2 * vertex + 1;
				int rightSon = 2 * vertex + 2;
				//if children are leaves - no need to buffer them, set the values from parent buffer
				if (isLeaf(leftSon)){
					treeElements[leftSon].value = current.buffer;
					treeElements[rightSon].value = current.buffer;	
				}else{
					//push changes to left son and set the correct value here
					treeElements[leftSon].buffered = true;
					treeElements[leftSon].buffer = current.buffer;
					treeElements[leftSon].value = current.buffer;
					// buffer the right son, set the correct value now
					treeElements[rightSon].buffered = true;
					treeElements[rightSon].buffer = current.buffer;
					treeElements[rightSon].value = current.buffer;
				}
				//unbuffer the parent, it's value is already correct
				current.buffered = false;
				current.buffer = -1;	
			}
		}
		//	

		public void finalizeUpdates() {
			finalizeUpdatesRecusively(0);
		}
		
		private void finalizeUpdatesRecusively(int vertex){
			if (isLeaf(vertex)){
				return;
			}
			else if (treeElements[vertex].buffered){
				int segmentStart = treeElements[vertex].leftControl;
				int segmentEnd = treeElements[vertex].rightControl;
				for (int j = segmentStart; j <= segmentEnd; j++){
					treeElements[leafLineStart + j].value = treeElements[vertex].buffer;
				}
				treeElements[vertex].buffered = false;
				treeElements[vertex].buffer = -1;
			}else{
				finalizeUpdatesRecusively(2 * vertex + 1);
				finalizeUpdatesRecusively(2 * vertex + 2);
			}
		}
	}

	private static String inputFileName = "painter.in";
	private static String outputFileName = "painter.out";
	private static long startMoment = 0L;
	private static void checkTime(boolean start){
		if (start){
			startMoment = System.currentTimeMillis();
		}else{
			System.out.println("\nSeconds spent: " + ((System.currentTimeMillis() - startMoment) / 1000.0));
		}
	}
	
	/*
	public static int[] nextArray(int size, BufferedReader reader) throws IOException{
		int[] array = new int[size];
		String numbers = reader.readLine();
		int firstDigit = 0;
		int whitespaceIndex = 0;
		for (int i = 0; i < size; i++){
			whitespaceIndex = numbers.indexOf(' ', firstDigit);
			whitespaceIndex = (whitespaceIndex == -1) ? numbers.length() : whitespaceIndex;
			int number = int.parseint(numbers.substring(firstDigit, whitespaceIndex));
			array[i] = number;
			firstDigit = whitespaceIndex + 1;
		}
		return array;
	}
	*/

	
	public static void main(String[] args) throws Exception{
		checkTime(true);
		File input = new File(inputFileName);
		File output = new File(outputFileName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(input)), 32768);
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		Integer queryAmount = Integer.parseInt(reader.readLine());
		System.out.println("Processing " + queryAmount + " queries");
		int size = 10;
		int array[] = new int[size];
		Arrays.fill(array, 2);
		SegmentTreeLazy tree = new SegmentTreeLazy(array);
		tree.printTree();
		for (int k = 0; k < queryAmount; k++){
			String query = reader.readLine();
			String[] parameters = query.split(" ");
			int color = parameters[0].equals("W") ? 2 : 0;
			int leftBound = Integer.parseInt(parameters[1]);
			int rightBound = leftBound + Integer.parseInt(parameters[2]);
			System.out.println("Updating [" + leftBound + ";" + rightBound  + "] to" + color);
			tree.updateSegmentLazy(leftBound, rightBound, color);
			tree.printTree();
		}
		tree.finalizeUpdates();
		System.out.println("FINAL ARRAY IS READY.");
		/*
		for (int offset = 0; offset < elementAmount; offset++){
			if (offset == elementAmount - 1){
				writer.write((tree.treeElements[tree.leafLineStart + offset].value) + "");
			}else{
				writer.write(tree.treeElements[tree.leafLineStart + offset].value + " ");
			}
		}
		*/
		reader.close();
		writer.close();
		checkTime(false);
		System.out.println("Finished.");
	}

	
}
