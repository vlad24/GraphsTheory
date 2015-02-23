import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;


public class MainProgramE {

	private static class TreeElement{
		//
		public long value;
		public int leftControl;
		public int rightControl;
		public long buffer;
		public boolean buffered;
		//
		public TreeElement(long newVal, int leftControl, int rightControl) {
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
		public SegmentTreeLazy(long[] result){
			actualElementAmount = result.length;
			Integer twoPowerInf = Integer.highestOneBit(actualElementAmount);
			Integer leafLineLength = ((actualElementAmount & (actualElementAmount - 1)) == 0) ? twoPowerInf : (twoPowerInf << 1) ;
			int totalCapacity = (leafLineLength << 1) - 1;
			treeElements = new TreeElement[totalCapacity];
			leafLineStart = totalCapacity - leafLineLength; 
			for (int i = totalCapacity - 1; i >= 0; i--){
				if (i >= totalCapacity - (leafLineLength - actualElementAmount)){
					treeElements[i] = new TreeElement(0, i - leafLineStart, i - leafLineStart);	
				}
				else if (i >= totalCapacity - leafLineLength){
					treeElements[i] = new TreeElement(result[i - leafLineStart], i - leafLineStart ,i - leafLineStart);
				}
				else{
					long newVal = sum(treeElements[2 * i + 1], treeElements[2 * i + 2]);
					int newLeft = treeElements[2 * i + 1].leftControl;
					int newRight = treeElements[2 * i + 2].rightControl;
					treeElements[i] = new TreeElement(newVal, newLeft, newRight);
				}

			}
		}

		/*
		private void printTree(){
			System.out.println(">> ");
			System.out.println(treeElements[0].value + "(" + treeElements[0].buffer + ") | ");
			int start = 1;
			int l = 2;
			while(l < treeElements.length){
				for (int i = start; i < start + l; i++){
					TreeElement current = treeElements[i];
					System.out.print(current.value + "(" + current.buffer + ") | ");
				}
				start += l;
				l = l << 1;
				System.out.println("\n");
			}
			System.out.print("<< \n");
		}
		*/

		private boolean isLeaf(int vertexNumber){
			return treeElements[vertexNumber].leftControl == treeElements[vertexNumber].rightControl;
		}

		private long sum(TreeElement a, TreeElement b){
			return a.value + b.value;
		}

		public long sumAtSegmentFromUp(int left, int right) throws Exception{
			//System.out.println(" + Sum query for ["+left + " ; "+ right +"]");
			if ((left <= right) && (left >= 0) && (right < actualElementAmount)){
					return sumAtSegmentRecursive(0, left, right);
			}else
				throw new Exception("Incorrect query : [" + left + ";" + right + "]");
		}

		private long sumAtSegmentRecursive(int vertexNumber, int left, int right){
			TreeElement current = treeElements[vertexNumber];
			if ((current.leftControl == left) && (current.rightControl == right)){
				if (current.buffered){
					return current.buffer * (current.rightControl - current.leftControl + 1);
				}else{
					return current.value;
				}
			}else{
				pushBufferDeeper(vertexNumber);
				long leftSum = 0;
				long rightSum = 0;
				if (left <= treeElements[2 * vertexNumber + 1].rightControl){
					leftSum = sumAtSegmentRecursive(2 * vertexNumber + 1, left, Math.min(treeElements[2*vertexNumber+1].rightControl, right));
				}
				if (right >= treeElements[2 * vertexNumber + 2].leftControl){
					rightSum = sumAtSegmentRecursive(2 * vertexNumber + 2, Math.max(treeElements[2 * vertexNumber + 2].leftControl, left), right);
				}
				return leftSum + rightSum; 
			}
		}
		
		public void updateSegmentLazy(int leftBound, int rightBound, int newValue){
			updateSegmentLazyRecursive(0, leftBound, rightBound, newValue);
		}

		private long updateSegmentLazyRecursive(int vertex, int leftBound, int rightBound, int newValue){
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
					current.buffered = true;
					current.buffer = newValue;
					current.value = current.buffer * (rightBound - leftBound + 1);
					return current.value;
				}
			//need to go deeper!
			}else{
				//To save some old changes push them down, updating values. 
				pushBufferDeeper(vertex);
				int leftSon = 2 * vertex + 1;
				int rightSon = 2 * vertex + 2;
				long newLeft = treeElements[leftSon].value;
				long newRight = treeElements[rightSon].value;
				// Look if left son needs to be updated if it intersects with target segment.
				if (treeElements[leftSon].rightControl >= leftBound){
					newLeft = updateSegmentLazyRecursive(leftSon, leftBound, Math.min(treeElements[leftSon].rightControl, rightBound), newValue);
				}
				// Look if right son needs to be updated if it intersects with target segment.
				if (treeElements[rightSon].leftControl <= rightBound){
					newRight = updateSegmentLazyRecursive(rightSon, Math.max(treeElements[rightSon].leftControl, leftBound), rightBound, newValue);
				}
				current.value = newLeft + newRight;
				return current.value;
			}
		}
				
		private void pushBufferDeeper(int vertex){
			TreeElement current = treeElements[vertex];
			if (current.buffered){
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
					treeElements[leftSon].value = current.buffer * (treeElements[leftSon].rightControl - treeElements[leftSon].leftControl + 1);
					// buffer the right son, set the correct value now
					treeElements[rightSon].buffered = true;
					treeElements[rightSon].buffer = current.buffer;
					treeElements[rightSon].value = current.buffer * (treeElements[rightSon].rightControl - treeElements[rightSon].leftControl + 1);
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

	private static String inputFileName = "middle.in";
	private static String outputFileName = "middle.out";
	private static long startMoment = 0L;
	private static void checkTime(boolean start){
		if (start){
			startMoment = System.currentTimeMillis();
		}else{
			System.out.println("\nSeconds spent: " + ((System.currentTimeMillis() - startMoment) / 1000.0));
		}
	}

	public static long[] nextArray(int size, BufferedReader reader) throws IOException{
		long[] array = new long[size];
		String numbers = reader.readLine();
		int firstDigit = 0;
		int whitespaceIndex = 0;
		for (int i = 0; i < size; i++){
			whitespaceIndex = numbers.indexOf(' ', firstDigit);
			whitespaceIndex = (whitespaceIndex == -1) ? numbers.length() : whitespaceIndex;
			long number = Long.parseLong(numbers.substring(firstDigit, whitespaceIndex));
			array[i] = number;
			firstDigit = whitespaceIndex + 1;
		}
		return array;
	}

	
	public static void main(String[] args) throws Exception{
		checkTime(true);
		File input = new File(inputFileName);
		File output = new File(outputFileName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(input)), 32768);
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		String[] startParameters = reader.readLine().split(" ");
		int elementAmount = Integer.parseInt(startParameters[0]);
		int queryAmount = Integer.parseInt(startParameters[1]);
		System.out.println("Processing " + elementAmount + " elements and " + queryAmount + " queries");
		long[] array = nextArray(elementAmount, reader);
		SegmentTreeLazy tree = new SegmentTreeLazy(array);
		long initialGlobalSum = tree.sumAtSegmentFromUp(0, elementAmount - 1);
		System.out.println("Initial sum:" + initialGlobalSum);
		for (int k = 0; k < queryAmount; k++){
			String query = reader.readLine();
			String[] bounds = query.split(" ");
			int leftBound = Integer.parseInt(bounds[0]) - 1;
			int rightBound = Integer.parseInt(bounds[1]) - 1;
			long currentGlobalSum = tree.sumAtSegmentFromUp(0, elementAmount - 1);
			long segmentSum = tree.sumAtSegmentFromUp(leftBound, rightBound);
			double newValue = 1.0 * segmentSum / (rightBound - leftBound + 1);
			if (currentGlobalSum <= initialGlobalSum){
				tree.updateSegmentLazy(leftBound, rightBound, ((Double)Math.ceil(newValue)).intValue());
			}else{
				tree.updateSegmentLazy(leftBound, rightBound, ((Double)Math.floor(newValue)).intValue());
			}
		}
		tree.finalizeUpdates();
		System.out.println("FINAL ARRAY IS READY.");
		for (int offset = 0; offset < elementAmount; offset++){
			if (offset == elementAmount - 1){
				writer.write((tree.treeElements[tree.leafLineStart + offset].value) + "");
			}else{
				writer.write(tree.treeElements[tree.leafLineStart + offset].value + " ");
			}
		}
		reader.close();
		writer.close();
		checkTime(false);
		System.out.println("Finished.");
	}

	
}
