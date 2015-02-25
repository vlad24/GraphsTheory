import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Arrays;

import com.sun.javafx.scene.traversal.TopMostTraversalEngine;


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

	public static final int BLACK = 0;
	public static final int WHITE = 2;
	

	private static class SegmentTreeLazy{
		//
		private int leafLineStart;
		private int actualElementAmount;
		private TreeElement[] treeElements;
		//
		public SegmentTreeLazy(int[] result){
			
			actualElementAmount = result.length;
			int twoPowerInf = Integer.highestOneBit(actualElementAmount);
			int leafLineLength = ((actualElementAmount & (actualElementAmount - 1)) == 0) ? twoPowerInf : (twoPowerInf << 1) ;
			int totalCapacity = (leafLineLength << 1) - 1;
			treeElements = new TreeElement[totalCapacity];
			System.out.println(totalCapacity);
			leafLineStart = totalCapacity - leafLineLength;
			long g = System.currentTimeMillis();
			for (int i = totalCapacity - 1; i >= 0; i--){
				if (i >= totalCapacity - (leafLineLength - actualElementAmount)){
					treeElements[i] = new TreeElement(2, i - leafLineStart, i - leafLineStart);
				}
				else if (i >= totalCapacity - leafLineLength){
					treeElements[i] = new TreeElement(result[i - leafLineStart], i - leafLineStart , i - leafLineStart);
				}
				else{
					int newVal = sum(treeElements[2 * i + 1].value, treeElements[2 * i + 2].value);
					int newLeft = treeElements[2 * i + 1].leftControl;
					int newRight = treeElements[2 * i + 2].rightControl;
					treeElements[i] = new TreeElement(newVal, newLeft, newRight);
				}

			}
			System.out.println((System.currentTimeMillis() - g) / 1000.0);
		}
		/*
	    public void printBinaryTree(int vertex, int level){
	        if(vertex >= treeElements.length)
	             return;
	        printBinaryTree(2 * vertex + 2, level + 1);
	        if(level != 0){
	            for(int i = 0; i < level - 1; i++){
	                System.out.print("|\t");
	            }
	            String index = isLeaf(vertex) ? (" ~ " + treeElements[vertex].leftControl) : ("");
	            System.out.println("|-------" + treeElements[vertex].value + "(" + treeElements[vertex].buffer + ")"  + index);
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
		*/

		private boolean isLeaf(int vertexNumber){
			return treeElements[vertexNumber].leftControl == treeElements[vertexNumber].rightControl;
		}

		private int sum(int a, int b){
			return (a == b) ? a : 1;
		}
		
		/*
		public double sumAtSegmentFromUp(int left, int right) throws Exception{
			//System.out.println(" + Sum query for ["+left + " ; "+ right +"]");
			if ((left <= right) && (left >= 0) && (right < actualElementAmount)){
					return sumAtSegmentRecursive(0, left, right);
			}else
				throw new Exception("Incorrect query : [" + left + ";" + right + "]");
		}

		private int sumAtSegmentRecursive(int vertexNumber, int left, int right){
			TreeElement current = treeElements[vertexNumber];
			//the needed segment
			if ((current.leftControl == left) && (current.rightControl == right)){
				if (current.buffered){
					return current.buffer; //in normal case buffer * len
				}else{
					return current.value;
				}
			}else{
				pushBufferDeeper(vertexNumber);
				int leftSum = 0;
				int rightSum = 0;
				int leftSon = 2 * vertexNumber+ 1; 
				int rightSon = 2 * vertexNumber+ 2;
				if (left <= treeElements[leftSon].rightControl){
					leftSum = sumAtSegmentRecursive(leftSon, left, Math.min(treeElements[leftSon].rightControl, right));
				}
				if (right >= treeElements[2 * vertexNumber + 2].leftControl){
					rightSum = sumAtSegmentRecursive(rightSon, Math.max(treeElements[rightSon].leftControl, left), right);
				}
				return sum(leftSum, rightSum); 
			}
		}
		*/
		public void updateSegmentLazy(int leftBound, int rightBound, int newValue){
			updateSegmentLazyRecursive(0, leftBound, rightBound, newValue);
		}

		private int updateSegmentLazyRecursive(int vertex, int leftBound, int rightBound, int newValue){
			TreeElement current = treeElements[vertex];
			//look if current node is the target segment
			if ((current.leftControl == leftBound) && (current.rightControl == rightBound)){
				//here we use lazy approach
				if (!isLeaf(vertex)){
					//set the buffer that will be pushed down later if needed! SUPER LAZY!
					current.buffered = true;
					current.buffer = newValue;
					//update the value to hold correct information 
					current.value = newValue;
					return current.value;
				}else{
					//no buffer possible here, update the value without troubles
					current.value = newValue;
					return current.value;
				}
			}else{//oh no, need to go deeper!
				//To save old changes push them down, updating values there(*). 
				pushBufferDeeper(vertex);
				int leftSon = 2 * vertex + 1; 
				int rightSon = 2 * vertex + 2;
				//Now there are valid values(*), so we can account them.
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
				current.value = sum(newLeft, newRight);
				return current.value;
			}
		}
				
		private void pushBufferDeeper(int vertex){
			TreeElement current = treeElements[vertex];
			if (current.buffered){
				//System.out.println("Pushing deeper from " + vertex);
				int leftSon = 2 * vertex + 1;
				int rightSon = 2 * vertex + 2;
				//if children are leaves - no need to buffer them, set the values from parent buffer
				if (isLeaf(leftSon)){
					treeElements[leftSon].value = current.buffer;
					treeElements[rightSon].value = current.buffer;	
				}else{
					//push changes to left son and set the actual value from the buffer 
					treeElements[leftSon].buffered = true;
					treeElements[leftSon].buffer = current.buffer;
					treeElements[leftSon].value = current.buffer;
					// buffer the right son, set the actual value from the buffer
					treeElements[rightSon].buffered = true;
					treeElements[rightSon].buffer = current.buffer;
					treeElements[rightSon].value = current.buffer;
				}
				//unbuffer the parent, it's value is already correct
				current.buffered = false;
				current.buffer = -1;	
			}
		}
			
/*
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
*/
		public int[] calculateBlackSegments() {
			int[] result = new int[3];
			result[2] = Integer.MIN_VALUE;
			return updateResultRecursively(0, result);
		}

		private int[] updateResultRecursively(int vertex, int[] result) {
			TreeElement current = treeElements[vertex];
			if (current.value == BLACK){
				if (current.leftControl != result[2] + 1){ // if it is not a continuing
					result[0]++;
				}
				result[1] += current.rightControl - current.leftControl + 1;
				result[2] = current.rightControl; // move the right border
			}else if(!isLeaf(vertex) && current.value != WHITE){
				updateResultRecursively(2*vertex + 1, result);
				updateResultRecursively(2*vertex + 2, result);
			}
			return result;
		}
		
	}

	private static String inputFileName = "painter2.in";
	private static String outputFileName = "painter.out";
	
	
	private static long startMoment = 0L;
	
	private static void checkTime(boolean start){
		if (start){
			startMoment = System.currentTimeMillis();
		}else{
			System.out.println("\nSeconds spent: " + ((System.currentTimeMillis() - startMoment) / 1000.0));
		}
	}
	
	
	public static void main(String[] args) throws Exception{
		int[] result = new int[3];
		File input = new File(inputFileName);
		File output = new File(outputFileName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(input)), 32768);
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		int queryAmount = Integer.parseInt(reader.readLine());
		System.out.println("Processing " + queryAmount + " queries");
		int halfSize = 500000;
		int line[] = new int[halfSize + 1 + halfSize];
		Arrays.fill(line, WHITE);
		System.out.println("Creation of the tree");
			checkTime(true);
		SegmentTreeLazy tree = new SegmentTreeLazy(line);
			checkTime(false);
		//tree.printTree();
		System.out.println("The loop");checkTime(true);
		for (int k = 0; k < queryAmount; k++){
			String query = reader.readLine();
			//System.out.println(query);
			String[] parameters = query.split(" ");
			int color = parameters[0].equals("W") ? 2 : 0;
			int leftBound = Integer.parseInt(parameters[1]) + halfSize;
			int rightBound = leftBound + Integer.parseInt(parameters[2]) - 1;
			//System.out.println("Updating [" + leftBound + ";" + rightBound  + "] to " + color);
			tree.updateSegmentLazy(leftBound, rightBound, color);
			//tree.printTree();
			result = tree.calculateBlackSegments();
			if (k == queryAmount - 1){
				writer.write(result[0] + " " + result[1]);
			}else{
				writer.write(result[0] + " " + result[1] + "\n");	
			}
			//System.out.println(result[0] + " " + result[1]);
		}
		checkTime(false);
		//tree.finalizeUpdates();
		//System.out.println("Answer is ready.");
		//tree.printTree();
		reader.close();
		writer.close();
		System.out.println("Finished.");
	}

	
}
