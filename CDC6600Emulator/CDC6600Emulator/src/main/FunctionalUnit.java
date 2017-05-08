package main;

public class FunctionalUnit {
	public enum UnitType {
		INTEGER, ADDER, MULTIPLIER, DIVIDER, LOADSTORE, JMPHLT
	};

	public UnitType type;
	public String id;
	public int latency;
	public int clockCycleUsed;
	public boolean isFree;

	FunctionalUnit(UnitType type, String id, int latency) {
		this.type = type;
		this.id = id;
		this.latency = latency;
		this.clockCycleUsed = 0;
		this.isFree = true;
	}

	void UpdateStatusToUsing() {
		this.isFree = false;
	}

	void UpdateClockCycle() {
		this.clockCycleUsed += 1;
		if (this.isFree)
			this.isFree = false;
	}

	boolean HasCompleted() {
		return ((this.latency - this.clockCycleUsed) == 0);
	}

	void ResetUnit() {
		this.clockCycleUsed = 0;
		this.isFree = true;
	}
}
