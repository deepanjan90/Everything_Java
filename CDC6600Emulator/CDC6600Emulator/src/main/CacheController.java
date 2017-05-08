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

	public CacheController(InstructionCache instructionCache, DataCache dataCache) {
		this.instructionCache = instructionCache;
		this.dataCache = dataCache;
	}
	
	public boolean IsIcacheActive(){
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

	public boolean IsDataPresent(int dataId) {
		boolean isPresent = true;
		isPresent = dataCache.IsPresent(dataId);
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
			} else if (dCacheRequestPresent) {
				dCacheActive = true;
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
			if (dataCache.IsPresent(queuedDataForFetch)) {
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
