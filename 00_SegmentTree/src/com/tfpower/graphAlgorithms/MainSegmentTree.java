package com.tfpower.graphAlgorithms;

public class MainSegmentTree {

	public static void main(String[] args) throws Exception {
		int[] numbers = {1,2,3,4}; 
		SegmentTree tree = new SegmentTree(numbers);
		System.out.println(tree.sumAtRangeFromUp(0, 3));
	}

}
