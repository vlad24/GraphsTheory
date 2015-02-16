package com.tfpower.graphAlgorithms;

public class SegmentTree{
	
	private TreeElement[] treeElements;
	private int leafLineStart;
	private int actualElementAmount;
	private void printTree(){
		for (TreeElement t: treeElements){
			System.out.print(t.val+" ");
		}
		System.out.println();
	}
	
	private int sum(TreeElement a, TreeElement b){
		return a.val + b.val;
	}
	
	public SegmentTree(int[] numbers){
		actualElementAmount = numbers.length;
		Integer twoPowerInf = Integer.highestOneBit(actualElementAmount);
		Integer leafLineLength = ((actualElementAmount & (actualElementAmount - 1)) == 0) ? twoPowerInf : (twoPowerInf << 1) ;
		int totalCapacity = (leafLineLength << 1) - 1;
		treeElements = new TreeElement[totalCapacity];
		leafLineStart = totalCapacity - leafLineLength; 
		for (int i = totalCapacity - 1; i >= 0; i--){
			if (i > totalCapacity - (leafLineLength - actualElementAmount)){
				treeElements[i] = new TreeElement(0, i - leafLineStart, i - leafLineStart);	
			}
			else if (i >= totalCapacity - leafLineLength){
				treeElements[i] = new TreeElement(numbers[i - leafLineStart], i - leafLineStart ,i - leafLineStart);
			}
			else{
				int newVal = sum(treeElements[2 * i + 1], treeElements[2 * i + 2]);
				int newLeft = treeElements[2 * i + 1].leftControl;
				int newRight = treeElements[2 * i + 2].rightControl;
				treeElements[i] = new TreeElement(newVal, newLeft, newRight);
			}
			
		}
		printTree();
	}
	
	public int sumAtRangeFromUp(int left, int right) throws Exception{
		if ((left <= right) && (left >= 0) && (right < actualElementAmount)){
			if (left == right){
				return 0;
			}
			else{
				return sumRecursive(0, left, right);
			}
		}
		else{
			throw new Exception("Incorrect query");
		}
	}
	
	private int sumRecursive(int vertexNumber, int left, int right){
		//System.out.println("Traversing " + treeElements[vertexNumber].val);
		TreeElement currentVertex = treeElements[vertexNumber];
		//System.out.println("Its bounds " + currentVertex.leftControl + "," + currentVertex.rightControl);
		if ((currentVertex.leftControl == left) && (currentVertex.rightControl == right)){
			return currentVertex.val;
		}else{
			int leftSum = 0;
			int rightSum = 0;
			if (left <= treeElements[2*vertexNumber+1].rightControl){
				leftSum = sumRecursive(vertexNumber*2 + 1, left, treeElements[2*vertexNumber+1].rightControl);
			}
			if (right >= treeElements[2*vertexNumber+2].leftControl){
				rightSum = sumRecursive(vertexNumber*2 + 2, treeElements[2*vertexNumber+2].leftControl, right);
			}
			return leftSum + rightSum; 
		}
	}
	
}
