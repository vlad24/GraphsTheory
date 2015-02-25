import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;



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
		//private int leafLineStart;
		private int actualElementAmount;
		private TreeElement[] treeElements;
		//
		public SegmentTreeLazy(int n){
			actualElementAmount = n;
			int twoPowerInf = Integer.highestOneBit(actualElementAmount);
			int leafLineLength = ((actualElementAmount & (actualElementAmount - 1)) == 0) ? twoPowerInf : (twoPowerInf << 1) ;
			int totalCapacity = (leafLineLength << 1) - 1;
			treeElements = new TreeElement[totalCapacity];
			//leafLineStart = totalCapacity - leafLineLength;
			treeElements[0] = new TreeElement(WHITE, 0, leafLineLength-1);
		}
		/*
	    public void printBinaryTree(int vertex, int level){
	    	if (treeElements[vertex] != null){
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
		
		public void updateSegmentLazy(int leftBound, int rightBound, int newValue){
			updateSegmentLazyRecursive(0, leftBound, rightBound, newValue);
		}

		private int updateSegmentLazyRecursive(int vertex, int leftBound, int rightBound, int newValue){
			TreeElement current = treeElements[vertex];
			if ((current.leftControl == leftBound) && (current.rightControl == rightBound)){
				if (!isLeaf(vertex)){
					current.buffered = true;
					current.buffer = newValue;
					current.value = newValue;
					return current.value;
				}else{
					current.value = newValue;
					return current.value;
				}
			}else{
				int center = (current.leftControl + current.rightControl) / 2;
				int leftSon = 2 * vertex + 1; 
				int rightSon = leftSon + 1;
				//Lazy initialization, check sons
				if (treeElements[leftSon] == null)
					treeElements[leftSon] = new TreeElement(WHITE, current.leftControl, center);
				if (treeElements[rightSon] == null)
					treeElements[rightSon] = new TreeElement(WHITE, center + 1,  current.rightControl);
				pushBufferDeeper(vertex);
				//Now there are valid values(*), so we can account them.
				int newLeft = treeElements[leftSon].value;
				int newRight = treeElements[rightSon].value;
				// Look if left son needs to be updated if it intersects with target segment.
				if (treeElements[leftSon].rightControl >= leftBound)
					newLeft = updateSegmentLazyRecursive(leftSon, leftBound, Math.min(treeElements[leftSon].rightControl, rightBound), newValue);
				// Look if right son needs to be updated if it intersects with target segment.
				if (treeElements[rightSon].leftControl <= rightBound)
					newRight = updateSegmentLazyRecursive(rightSon, Math.max(treeElements[rightSon].leftControl, leftBound), rightBound, newValue);
				current.value = sum(newLeft, newRight);
				return current.value;
			}
		}
				
		private void pushBufferDeeper(int vertex){
			TreeElement current = treeElements[vertex];
			if (current.buffered){
				int leftSon = 2 * vertex + 1;
				int rightSon = leftSon + 1;
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

		public int[] calculateBlackSegments() {
			int[] result = new int[3];
			result[2] = Integer.MIN_VALUE;
			return updateResultRecursively(0, result);
		}

		private int[] updateResultRecursively(int vertex, int[] result) {
			TreeElement current = treeElements[vertex];
			if (current == null){
				return result;
			}else if (current.value == BLACK){
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
		checkTime(true);
		int[] result = new int[3];
		File input = new File(inputFileName);
		File output = new File(outputFileName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(input)), 32768);
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		int queryAmount = Integer.parseInt(reader.readLine());
		System.out.println("Processing " + queryAmount + " queries");
		int halfSize = 500000;
		SegmentTreeLazy tree = new SegmentTreeLazy(2 * halfSize + 1);
		for (int k = 0; k < queryAmount; k++){
			String query = reader.readLine();
			//System.out.println(query);
			String[] parameters = query.split(" ");
			int color = parameters[0].equals("W") ? 2 : 0;
			int leftBound = Integer.parseInt(parameters[1]) + halfSize;
			int rightBound = leftBound + Integer.parseInt(parameters[2]) - 1;
			tree.updateSegmentLazy(leftBound, rightBound, color);
			//tree.printTree();
			result = tree.calculateBlackSegments();
			if (k == queryAmount - 1)
				writer.write(result[0] + " " + result[1]);
			else
				writer.write(result[0] + " " + result[1] + "\n");	
		}
		reader.close();
		writer.close();
		checkTime(false);
		System.out.println("\nFinished.");
	}

	
}