import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;


public class MainProgramC {

	private static class TreeElement{
		public long value;
		public int leftControl;
		public int rightControl;
		public TreeElement(long newVal, int leftControl, int rightControl) {
			super();
			this.value = newVal;
			this.leftControl = leftControl;
			this.rightControl = rightControl;
		}

	}

	private static class SegmentTree{
		private int leafLineStart;
		private int actualElementAmount;
		private TreeElement[] treeElements;
		
		public SegmentTree(int[] numbers){
			actualElementAmount = numbers.length;
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
					treeElements[i] = new TreeElement(numbers[i - leafLineStart], i - leafLineStart ,i - leafLineStart);
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
		private long bruteSum(int a, int b){
			long s = 0;
			for (int i = a; i <= b; i++){
				s += treeElements[leafLineStart + i].value;
			}
			return s;
		}
		*/
		/*
		private void printTree(){
			System.out.print("| ");
			for (int i = 0; i < treeElements.length; i++){
				System.out.print(treeElements[i].value+" ");
				if (i == leafLineStart - 1){
					System.out.print("# ");
				}
			}
			System.out.print("|\n");
		}*/
		
		private boolean isLeaf(int vertexNumber){
			return treeElements[vertexNumber].leftControl == treeElements[vertexNumber].rightControl;
		}
		
		private long sum(TreeElement a, TreeElement b){
			return a.value + b.value;
		}
		
		public long sumAtRangeFromUp(int left, int right) throws Exception{
			//System.out.print("Summing  from ("+left + " to "+ right +") in "); printTree();
			if ((left <= right) && (left >= 0) && (right < actualElementAmount))
				if (left == right)
					return treeElements[left + leafLineStart].value;
				else{
					return sumRecursive(0, left, right);
				}
			else
				throw new Exception("Incorrect query " + left + " " + right);
		}

		private long sumRecursive(int vertexNumber, int left, int right){
			TreeElement currentVertex = treeElements[vertexNumber];
			if ((currentVertex.leftControl == left) && (currentVertex.rightControl == right)){
				return currentVertex.value;
			}else{
				long leftSum = 0;
				long rightSum = 0;
				if (left <= treeElements[2 * vertexNumber + 1].rightControl){
					leftSum = sumRecursive(2 * vertexNumber + 1, left, Math.min(treeElements[2*vertexNumber+1].rightControl, right));
				}
				if (right >= treeElements[2 * vertexNumber + 2].leftControl){
					rightSum = sumRecursive(2 * vertexNumber + 2, Math.max(treeElements[2 * vertexNumber + 2].leftControl, left), right);
				}
				return leftSum + rightSum; 
			}
		}
		
		public void updateElement(int number, int newValue){
			//System.out.print("Updating (" + number +"," + newValue +"):"); printTree();
			update(0, leafLineStart + number, newValue);
			//System.out.print("Updated:"); printTree();
		}
		
		private void update (int current, int target, int newValue) {
			if (isLeaf(current))
				treeElements[current].value = newValue;
			else {
				int middleAxis = (treeElements[current].leftControl + treeElements[current].rightControl) / 2;
				int leftSon = 2 * current + 1;
				int rightSon = 2 * current + 2;
				if (target - leafLineStart <= middleAxis) // go to the left subtree
					update (leftSon, target, newValue);
				else // go to the right subtree
					update (rightSon, target, newValue);
				// after everything is done with sons update yourself
				treeElements[current].value = treeElements[leftSon].value + treeElements[rightSon].value;
			}
		}
	}



//	private static long startMoment = 0L;
//	private static void checkTime(boolean start){
//		if (start){
//			startMoment = System.currentTimeMillis();
//		}else{
//			System.out.println("Seconds spent: " + ((System.currentTimeMillis() - startMoment) / 1000.0));
//		}
//	}
	
	
	public static void main(String[] args) throws Exception{
//		System.out.println("here");
//		int[] ns = new int[100];
//		for (int i=0; i < 100; i++){
//			ns[i] = new Random().nextInt(100) - 20;
//		}
//		System.out.println("here");
//		SegmentTree t = new SegmentTree(ns);
//		for (int i = 0; i < ns.length; i++){
//			for (int j = i; j < ns.length; j++){
//				long s1 = t.sumAtRangeFromUp(i, j);
//				long s2 = t.bruteSum(i, j);
//				if (!(s1 == s2)){
//					System.out.println("BAD\n\n");
//				}
//			}
//		}
//		System.out.println("OK");
//		
//		for (int i = 0; i < ns.length; i++){
//			int y = new Random().nextInt(100);
//			if (y > 50){
//				t.updateElement(i, y);
//			}
//		}
//		System.out.println("here");
//		for (int i = 0; i < ns.length; i++){
//			for (int j = i; j < ns.length; j++){
//				long s1 = t.sumAtRangeFromUp(i, j);
//				long s2 = t.bruteSum(i, j);
//				if (!(s1 == s2)){
//					System.out.println("BAD\n\n");
//				}
//			}
//		}
		File input = new File("sum.in");
		File output = new File("sum.out");
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(input)), 32768);
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		////
		String[] startParameters = reader.readLine().split(" ");
		int treeSize = Integer.parseInt(startParameters[0]);
		int queryAmount = Integer.parseInt(startParameters[1]);
		//System.out.println("Processing " + treeSize + " elements and " + queryAmount + " queries");
		int[] numbers = new int[treeSize];
		SegmentTree tree = new SegmentTree(numbers);
		for (int k = 0; k < queryAmount; k++){
			String command = reader.readLine();
			String[] commandParameters = command.split(" ");
			if (commandParameters[0].equals("A")){
				int i = Integer.parseInt(commandParameters[1]) - 1;
				int newValue = Integer.parseInt(commandParameters[2]);
				//System.out.println("upd query: set " + i + " as "+ newValue);
				tree.updateElement(i, newValue);
				//tree.printTree();
			}else{
				int left = Integer.parseInt(commandParameters[1]) - 1;
				int right = Integer.parseInt(commandParameters[2]) - 1;
				long sum = tree.sumAtRangeFromUp(left, right);
				//System.out.println("sum query:" + left + " "+ right + "," + sum);
				if (k == queryAmount-1) 
					writer.write(sum + "");
				else
					writer.write(sum + "\n");
			}
		}
		
		reader.close();
		writer.close();
		//checkTime(false);
		//System.out.println("Finished");
		
	}


}
