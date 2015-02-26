import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;


public class MainProgramF{


	private static class TreeElement{
		//
		public int color;
		public int leftControl;
		public int rightControl;
		public int colorBuffer;
		public boolean buffered;
		public int blacks;
		public boolean lBlack;
		public boolean rBlack;
		public int blacksLength;
		//
		public TreeElement(int newVal, int newLeftControl, int newRightControl) {
			color = newVal;
			colorBuffer = -1;
			buffered = false;
			leftControl = newLeftControl;
			rightControl = newRightControl;
			setBlacks();
		}

		public void setBlacks(){
			if (color == WHITE){
				lBlack = false;
				rBlack = false;
				blacks = 0;
				blacksLength = 0;
			}else{
				lBlack = true;
				rBlack = true;
				blacks = 1;
				blacksLength = rightControl - leftControl + 1;
			}
		}

		public void bufferize(int newColor){
			buffered = true;
			colorBuffer = newColor;
			color = newColor;
		}

		public void unbufferize() {
			buffered = false;
			colorBuffer = -1;	
		}

		public boolean isFullyControlling(int leftBound, int rightBound){
			return (leftControl == leftBound) && (rightControl == rightBound);
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
			treeElements[0] = new TreeElement(WHITE, 0, leafLineLength - 1);
		}

		/*
		private void printTree(){
			System.out.println(">> ");
			System.out.println(treeElements[0].color + "(" + treeElements[0].colorBuffer + ") " + "[" + treeElements[0].blacks + ", " + treeElements[0].blacksLength + "]");
			int start = 1;
			int l = 2;
			while(l < treeElements.length){
				for (int i = start; i < start + l; i++){
					TreeElement current = treeElements[i];
					if (current != null){
						System.out.print(current.color + "(" + current.colorBuffer + ")[" + current.blacks + ", " + current.blacksLength + "] /");				
					}
					else{
						System.out.print(" x ");
					}
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

		private int colorSum(TreeElement leftSegment, TreeElement rightSegment){
			return (leftSegment.color == rightSegment.color) ? leftSegment.color : 1;
		}

		private int blacksSum(TreeElement left, TreeElement right){
			return left.blacks + right.blacks - ((left.rBlack && right.lBlack) ? 1 : 0);
		}

		private int blacksLengthSum(TreeElement left, TreeElement right){
			return left.blacksLength + right.blacksLength;
		}

		public void updateSegmentLazy(int leftBound, int rightBound, int newValue){
			updateSegmentLazyRecursive(0, leftBound, rightBound, newValue);
		}

		private void updateSegmentLazyRecursive(int vertex, int leftBound, int rightBound, int newValue){
			TreeElement current = treeElements[vertex];
			if (current.isFullyControlling(leftBound, rightBound)){
				if (!isLeaf(vertex))
					current.bufferize(newValue);
				else
					current.color = newValue;
				current.setBlacks();
				return;
			}
			else{
				int center = (current.leftControl + current.rightControl) / 2;
				int leftIndex = 2 * vertex + 1;
				int rightIndex = leftIndex + 1; 
				//Lazy initialization, check sons
				if (treeElements[leftIndex] == null)
					treeElements[leftIndex] = new TreeElement(WHITE, current.leftControl, center);
				if (treeElements[rightIndex] == null)
					treeElements[rightIndex] = new TreeElement(WHITE, center + 1,  current.rightControl);
				//push old changes
				pushBufferDeeper(vertex);
				// Look if left son needs to be updated if it intersects with target segment.
				if (treeElements[leftIndex].rightControl >= leftBound)
					updateSegmentLazyRecursive(leftIndex, leftBound, Math.min(treeElements[leftIndex].rightControl, rightBound), newValue);
				// Look if right son needs to be updated if it intersects with target segment.
				if (treeElements[rightIndex].leftControl <= rightBound)
					updateSegmentLazyRecursive(rightIndex, Math.max(treeElements[rightIndex].leftControl, leftBound), rightBound, newValue);
				//Sons are updated now, can assign true value
				current.color = colorSum(treeElements[leftIndex], treeElements[rightIndex]);
				current.rBlack = treeElements[rightIndex].rBlack;
				current.lBlack = treeElements[leftIndex].lBlack;
				current.blacks = blacksSum(treeElements[leftIndex], treeElements[rightIndex]);
				current.blacksLength = blacksLengthSum(treeElements[leftIndex], treeElements[rightIndex]);
				return;
			}
		}

		private void pushBufferDeeper(int vertex){
			TreeElement current = treeElements[vertex];
			if (current.buffered){
				TreeElement leftSon = treeElements[2 * vertex + 1];
				TreeElement rightSon = treeElements[2 * vertex + 2];
				//if children are leaves - no need to buffer them, set the values from parent buffer
				if (isLeaf(2 * vertex + 1)){
					leftSon.color = current.colorBuffer;
					leftSon.setBlacks();
					rightSon.color = current.colorBuffer;
					rightSon.setBlacks();
				}else{
					leftSon.bufferize(current.colorBuffer);
					leftSon.setBlacks();
					rightSon.bufferize(current.colorBuffer);
					rightSon.setBlacks();
				}
				current.unbufferize();
			}
		}

		public int[] calculateBlackSegmentsFast(){
			int[] result = new int[2];
			result[0] = treeElements[0].blacks;
			result[1] = treeElements[0].blacksLength;
			return result;
		}

		
	}

	
	private static String inputFileName = "painter.in";
	private static String outputFileName = "painter.out";
	//private static long startMoment = 0L;

	/*
	private static void checkTime(boolean start){
		if (start){
			startMoment = System.currentTimeMillis();
		}else{
			System.out.println("\nSeconds spent: " + ((System.currentTimeMillis() - startMoment) / 1000.0));
		}
	}
	*/

	public static void main(String[] args) throws Exception{
		//checkTime(true);
		File input = new File(inputFileName);
		File output = new File(outputFileName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(input)), 32768);
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		int queryAmount = Integer.parseInt(reader.readLine());
		//System.out.println("Processing " + queryAmount + " queries");
		int halfSize = 500000;
		SegmentTreeLazy tree = new SegmentTreeLazy(2 * halfSize + 1);
		for (int k = 0; k < queryAmount; k++){
			String query = reader.readLine();
			System.out.println(query);
			String[] parameters = query.split(" ");
			int color = parameters[0].equals("W") ? 2 : 0;
			int leftBound = Integer.parseInt(parameters[1]) + halfSize;
			int rightBound = leftBound + Integer.parseInt(parameters[2]) - 1;
			tree.updateSegmentLazy(leftBound, rightBound, color);
			//tree.printTree();
			int[] result = tree.calculateBlackSegmentsFast();
			//System.out.println(result[0] + " " + result[1] + "\n");
			if (k == queryAmount - 1)
				writer.write(result[0] + " " + result[1]);
			else
				writer.write(result[0] + " " + result[1] + "\n");	
		}
		reader.close();
		writer.close();
		//checkTime(false);
		System.out.println("\nFinished.");
	}


	
}