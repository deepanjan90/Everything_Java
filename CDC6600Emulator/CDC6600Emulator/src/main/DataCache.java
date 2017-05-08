package main;

import java.util.ArrayList;

public class DataCache {

	int blockPerSet;
	int wordsPerBlock;
	int setPerCache;
	int clockCyclePerWordFetch;
	int clockCycleUsed;
	ArrayList<Set> setList;

	private class Set {
		ArrayList<Block> blockList = new ArrayList<Block>();
	}

	private class Block {
		ArrayList<Integer> wordList = new ArrayList<Integer>();
	}

	DataCache(int blockPerSet, int wordsPerBlock, int setPerCache, int clockCyclePerWordFetch) {
		this.blockPerSet = blockPerSet;
		this.wordsPerBlock = wordsPerBlock;
		this.setPerCache = setPerCache;
		this.clockCyclePerWordFetch = clockCyclePerWordFetch;
		this.clockCycleUsed = 0;
		Configure();
	}

	private void Configure() {
		setList = new ArrayList<>();
		for (int i = 0; i < setPerCache; i++) {
			setList.add(new Set());
		}
	}

	public boolean IsPresent(int dataId) {
		boolean isPresent = false;
		Set set = setList.get(GetSeTLocation(dataId));
		if (set.blockList.size() > 0) {
			for (Block block : set.blockList) {
				if (block.wordList.contains(dataId)) {
					isPresent = true;
					UpdateLRUBlock(set, block);
					break;
				}
			}
		} else {
			// no block present
		}
		return isPresent;
	}

	private int GetWordLocation(int dataId) {
		return dataId % wordsPerBlock;
	}

	private int GetSeTLocation(int dataId) {
		return (dataId / wordsPerBlock) % 2;
	}

	public void UpdateLRUBlock(Set set, Block block) {
		set.blockList.remove(block);
		set.blockList.add(block);
	}

	public void GetDataBlock(int dataId) {
		clockCycleUsed += 1;
		if (clockCycleUsed == clockCyclePerWordFetch * wordsPerBlock) {
			clockCycleUsed = 0;
			Set set = setList.get(GetSeTLocation(dataId));
			int startDataId = dataId - GetWordLocation(dataId);
			Block block = new Block();
			for (int i = startDataId; i < startDataId + wordsPerBlock; i++) {
				block.wordList.add(i);
			}
			if (set.blockList.size() < blockPerSet) {
				// space to insert, no eviction needed
				set.blockList.add(block);
			} else {
				// eviction to be performed

				set.blockList.remove(0);
				set.blockList.add(block);
			}
		} else {
			// still working
		}
	}

	public void View() {
		for (Set set : setList) {
			System.out.println("###################");
			for (Block block : set.blockList) {
				System.out.println("******************");
				for (int word : block.wordList) {
					System.out.print(word + "\t");
				}
				System.out.println("\n******************");
			}
			System.out.println("###################");
		}
	}
}
