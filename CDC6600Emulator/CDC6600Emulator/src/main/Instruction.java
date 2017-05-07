package main;

import java.util.concurrent.atomic.AtomicInteger;

public class Instruction {

	private static AtomicInteger INSTRUCTION_ID_GENERATOR = new AtomicInteger(101);
	
	public int id;
	public String fullInstruction;
	public String instruction;
	public String destination;
	public String source1;
	public String source2;
	public String label;
	public boolean isJump;
	public boolean isHalt;
	public boolean isUnconditional;

	Instruction(String fullInstruction, String instruction, String destination, String source1, String source2,
			String label, boolean isJump, boolean isHalt, boolean isUnconditional) {
		this.fullInstruction = fullInstruction;
		this.instruction = instruction;
		this.destination = destination;
		this.source1 = source1;
		this.source2 = source2;
		this.label = label;
		this.isJump = isJump;
		this.isHalt = isHalt;
		this.isUnconditional = isUnconditional;
		this.id = INSTRUCTION_ID_GENERATOR.getAndIncrement();
	}
}
