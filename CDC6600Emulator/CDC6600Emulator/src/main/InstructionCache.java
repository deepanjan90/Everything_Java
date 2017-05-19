package main;

import java.util.ArrayList;


public class InstructionCache {
	private int blockCount;
	private int blockSize;
	private int clockCyclePerWordFetch;
	private int clockCycleUsed;
	private ArrayList<Integer> instructionIdList;
	private ArrayList<Block> blockList = new ArrayList<>();

	private class Block {
		ArrayList<Integer> wordList = new ArrayList<Integer>();
	}

	public InstructionCache(int blockCount, int blockSize, ArrayList<Integer> instructionList,
			int clockCyclePerWordFetch) {
		this.blockCount = blockCount;
		this.blockSize = blockSize;
		this.clockCyclePerWordFetch = clockCyclePerWordFetch;
		this.clockCycleUsed = 0;
		this.instructionIdList = instructionList;
		for (int i = 0; i < blockCount; i++) {
			blockList.add(new Block());
		}
	}

	public int GetBlockIndex(int instructionId) {
		int blockIndex = -1;
		int temp = instructionIdList.indexOf(instructionId);
		blockIndex = (temp / blockSize) % blockCount;
		return blockIndex;
	}

	public boolean IsPresent(int instructionId) {
		boolean isPresent = true;
		int blockIndex = GetBlockIndex(instructionId);
		if (blockList.get(blockIndex).wordList.contains(GetStartInstructionOfBlock(instructionId))) {
			// HIT
			isPresent = true;
		} else {
			// MISS
			isPresent = false;
		}
		return isPresent;
	}

	public void GetInstructionBlock(int instructionId) {
		clockCycleUsed += 1;
		if (clockCycleUsed == clockCyclePerWordFetch * blockSize) {
			clockCycleUsed = 0;
			int blockIndex = GetBlockIndex(instructionId);

			int startBlockIndex = GetStartInstructionOfBlock(instructionId);
			ArrayList<Integer> wordList = new ArrayList<Integer>();
			for (int i = 0; i < blockSize; i++) {
				wordList.add(startBlockIndex);
				startBlockIndex++;
			}
			blockList.get(blockIndex).wordList = wordList;

		} else {
			// Still Bringing from cache
		}

	}

	public void View() {
		for (Block block : blockList) {
			System.out.println("@@@@@@@@@@@ Block : "+blockList.indexOf(block)+" @@@@@@@@@@");
			for (int word : block.wordList) {
				System.out.print(word + "\t");
			}
			System.out.println("\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		}
	}
	
	private int GetStartInstructionOfBlock(int instructionId) {
		int indexOfInstruction = instructionIdList.indexOf(instructionId);
		int startBlockIndex = (indexOfInstruction / blockSize) * blockSize;
		return startBlockIndex;
	}
}
