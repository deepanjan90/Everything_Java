package main;

import java.util.ArrayList;

public class InstructionCache {
	private int blockCount;
	private int blockSize;
	private int clockCyclePerWordFetch;
	private int clockCycleUsed;
	private ArrayList<Integer> instructionIdList;
	private ArrayList<Integer> cache;
	private boolean gettingInstruction;

	public InstructionCache(int blockCount, int blockSize, ArrayList<Integer> instructionList,
			int clockCyclePerWordFetch) {
		this.blockCount = blockCount;
		this.blockSize = blockSize;
		this.clockCyclePerWordFetch = clockCyclePerWordFetch;
		this.clockCycleUsed = 0;
		this.instructionIdList = instructionList;
		this.cache = new ArrayList<Integer>();
		this.gettingInstruction = false;
	}

	public boolean IsPresentForFetch(int instructionId) {
		boolean isPresent = true;
		if (!gettingInstruction) {
			if (cache.contains(GetStartInstructionOfBlock(instructionId))) {
				// HIT
				isPresent = true;
				MoveInstructionFromLRUCache(instructionId);
			} else {
				// MISS
				isPresent = false;
				GetInstructionBlock(instructionId);
			}
		} else {
			// Still Bringing from cache
			isPresent = false;
			GetInstructionBlock(instructionId);
		}
		return isPresent;
	}

	public void ShowCach()
	{
		for (Integer integer : cache) {
			System.out.println("**"+integer);
		}
	}
	
	private void GetInstructionBlock(int instructionId) {
		clockCycleUsed += 1;
		if (clockCycleUsed == clockCyclePerWordFetch*blockCount) {
			gettingInstruction = false;
			clockCycleUsed = 0;
			int startBlockIndex = GetStartInstructionOfBlock(instructionId);
			if (cache.size() + 1 > blockCount) {
				// Cache full

				// Remove the Least recently used
				RemoveLRUInstructionFromCache();				
			}
			
			// Load the new one into cache
			LoadInstructionIntoCache(startBlockIndex);

		} else {
			// Still Bringing from cache
		}

	}

	private void MoveInstructionFromLRUCache(int instructionId) {
		if (cache.size() > 1) {
			int startBlockIndex = GetStartInstructionOfBlock(instructionId);
			RemoveInstructionFromCache(startBlockIndex);
			LoadInstructionIntoCache(startBlockIndex);
		} else {
			// only one item in cache
		}
	}

	private void RemoveLRUInstructionFromCache() {
		this.cache.remove(0);
	}

	private void RemoveInstructionFromCache(int startBlockIndex) {
		this.cache.remove(this.cache.indexOf(startBlockIndex));
	}

	private void LoadInstructionIntoCache(int startBlockIndex) {
		this.cache.add(startBlockIndex);
	}

	private int GetStartInstructionOfBlock(int instructionId) {
		int indexOfInstruction = instructionIdList.indexOf(instructionId);
		int startBlockIndex = (indexOfInstruction / blockSize) * blockSize;
		return startBlockIndex;
	}

	private int GetEndInstructionOfBlock(int startBlockIndex) {
		int endBlockIndex = startBlockIndex + blockSize - 1;
		return endBlockIndex;
	}
}
