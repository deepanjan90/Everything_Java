package main;

import java.util.concurrent.atomic.AtomicInteger;

import javax.sound.midi.Track;

import main.FunctionalUnit.UnitType;

public class InstructionPipeline {
	public enum StageType {
		NS, ISSUE, FETCH, READ, EXEC, WRITE
	};

	private static AtomicInteger INSTRUCTIONPIPELINE_ID_GENERATOR = new AtomicInteger(1001);

	int id;
	StageType stageType;
	Instruction instruction;
	int fetch;
	int issue;
	int read;
	int exec;
	int write;
	char raw;
	char waw;
	char struct;
	boolean branchtaken;
	FunctionalUnit functionalUnit;
	FunctionalUnit.UnitType functionalUnitType;

	public InstructionPipeline(Instruction instruction) {
		this.instruction = instruction;
		this.functionalUnitType = GetFunctionalUnitType(this.instruction.instruction);
		this.functionalUnit = null;
		this.stageType = StageType.NS;
		this.raw = 'N';
		this.waw = 'N';
		this.struct = 'N';
		this.id = INSTRUCTIONPIPELINE_ID_GENERATOR.getAndIncrement();
		this.branchtaken = false;
	}

	private FunctionalUnit.UnitType GetFunctionalUnitType(String instruction) {
		FunctionalUnit.UnitType unitType = UnitType.INTEGER;
		if (instruction.equals("ADD.D") || instruction.equals("SUB.D")) {
			unitType = UnitType.ADDER;
		} else if (instruction.equals("MUL.D")) {
			unitType = UnitType.MULTIPLIER;
		} else if (instruction.equals("DIV.D")) {
			unitType = UnitType.DIVIDER;
		} else if (instruction.equals("L.D") || instruction.equals("S.D") || instruction.equals("LW") || instruction.equals("SW")) {
			unitType = UnitType.LOADSTORE;
		} else if (instruction.equals("BNE") || instruction.equals("BEQ") || instruction.equals("HLT")) {
			unitType = UnitType.JMPHLT;
		}
		return unitType;
	}

	public String GetFucntionalUnitId() {
		String fucntionalUnitId = "";
		if (functionalUnit != null) {
			fucntionalUnitId = this.functionalUnit.id;
		}
		return fucntionalUnitId;
	}

	public void SetFunctionUnit(FunctionalUnit functionalUnit) {
		this.functionalUnit = functionalUnit;
	}

	public void Fetch(int clockCycle) {
		this.stageType = StageType.FETCH;
		this.fetch = clockCycle;
	}

	public void Issue(int clockCycle) {
		this.stageType = StageType.ISSUE;
		this.issue = clockCycle;
	}

	public void Read(int clockCycle) {
		this.stageType = StageType.READ;
		this.read = clockCycle;
	}

	public void Execute(int clockCycle) {
		this.stageType = StageType.EXEC;
		this.exec = clockCycle;
		if (this.functionalUnit != null) {
			this.UpdateExecute(clockCycle);
		}
	}

	public void UpdateExecute(int clockCycle) {
		this.exec = clockCycle;
		this.functionalUnit.UpdateClockCycle();
	}

	public void Write(int clockCycle) {
		this.stageType = StageType.WRITE;
		this.write = clockCycle;
	}

	public StageType GetCurrentStage() {
		return this.stageType;
	}

	public void MarkRAW() {
		this.raw = 'Y';
	}

	public void MarkWAW() {
		this.waw = 'Y';
	}

	public void MarkStruct() {
		this.struct = 'Y';
	}
}
