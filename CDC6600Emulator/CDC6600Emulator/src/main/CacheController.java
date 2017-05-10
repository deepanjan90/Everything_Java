package main;

public class CacheController {
	private InstructionCache instructionCache = null;
	private DataCache dataCache = null;
	private int queuedInstructionForFetch = 0;
	private int queuedDataForFetch = 0;
	private boolean iCacheActive = false;
	private boolean iCacheRequestPresent = false;
	private boolean dCacheActive = false;
	private boolean dCacheRequestPresent = false;
	public int iCacheMissCount = 0;
	public int dCacheMissCount = 0;

	public CacheController(InstructionCache instructionCache, DataCache dataCache) {
		this.instructionCache = instructionCache;
		this.dataCache = dataCache;
	}

	public boolean IsIcacheActive() {
		return iCacheActive;
	}

	public boolean IsInstructionPresent(int instructionId) {
		boolean isPresent = true;
		isPresent = instructionCache.IsPresent(instructionId);
		if (!isPresent) {
			queuedInstructionForFetch = instructionId;
			iCacheRequestPresent = true;
		} else {
			queuedInstructionForFetch = 0;
		}
		return isPresent;
	}

	public boolean IsDataPresent(int dataId, boolean isWrite) {
		boolean isPresent = true;
		isPresent = dataCache.IsPresent(dataId, isWrite);
		if (!isPresent) {
			queuedDataForFetch = dataId;
			dCacheRequestPresent = true;
		} else {
			queuedDataForFetch = 0;
		}
		return isPresent;
	}

	public void UpdateCaching() {

		if (!iCacheActive && !dCacheActive) {
			if (iCacheRequestPresent) {
				iCacheActive = true;
				iCacheMissCount += 1;
			} else if (dCacheRequestPresent) {
				dCacheActive = true;
				dCacheMissCount += 1;
			}
		}

		if (iCacheActive) {
			instructionCache.GetInstructionBlock(queuedInstructionForFetch);
			if (instructionCache.IsPresent(queuedInstructionForFetch)) {
				queuedInstructionForFetch = 0;
				iCacheRequestPresent = false;
				iCacheActive = false;
				/*if (dCacheRequestPresent) {
					dCacheActive = true;
				}*/
			}
		}

		if (dCacheActive) {
			dataCache.GetDataBlock(queuedDataForFetch);
			if (dataCache.IsPresent(queuedDataForFetch, false)) {
				queuedDataForFetch = 0;
				dCacheRequestPresent = false;
				dCacheActive = false;
				/*if (iCacheRequestPresent) {
					iCacheActive = true;
				}*/
			}
		}
	}
}
