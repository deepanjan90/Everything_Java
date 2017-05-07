package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Initialize {
	private static String ADDER = "FP ADDER";
	private static String MULTIPLIER = "FP MULTIPLIER";
	private static String DIVIDER = "FP DIVIDER";
	private static String CACHE = "I-CACHE";
	public static String CacheLine = "";

	public static ArrayList<FunctionalUnit> initializeConfig(String filePath) {
		ArrayList<FunctionalUnit> FUList = new ArrayList<FunctionalUnit>();
		if (!checkFile(filePath)) {
			// no / empty file
		} else {
			// continue
			try {
				ArrayList<FunctionalUnit> adderFUList = new ArrayList<>(), multiFUList = new ArrayList<>(),
						diviFUList = new ArrayList<>();

				BufferedReader br = new BufferedReader(new FileReader(filePath));
				String line, subLine, fUnit;
				int fUnitCount = 0, fCycleCount = 0, lineCount = 0;
				while ((line = br.readLine()) != null) {
					if (lineCount > 4) {
						throw new Exception("Invalid configuration file!!!");
					}
					fUnit = line.trim().split(":")[0].trim().toUpperCase();
					subLine = line.trim().split(":")[1].trim();
					fUnitCount = Integer.parseInt(subLine.split(",")[0].trim());
					fCycleCount = Integer.parseInt(subLine.split(",")[1].trim());
					lineCount++;
					if (fUnit.equals(ADDER))
						for (int i = 0; i < fUnitCount; i++)
							adderFUList.add(new FunctionalUnit(FunctionalUnit.UnitType.ADDER, "A" + i, fCycleCount));

					if (fUnit.equals(MULTIPLIER))
						for (int i = 0; i < fUnitCount; i++)
							multiFUList
									.add(new FunctionalUnit(FunctionalUnit.UnitType.MULTIPLIER, "M" + i, fCycleCount));

					if (fUnit.equals(DIVIDER))
						for (int i = 0; i < fUnitCount; i++)
							diviFUList.add(new FunctionalUnit(FunctionalUnit.UnitType.DIVIDER, "D" + i, fCycleCount));
					
					if(fUnit.equals(CACHE))
						CacheLine = fUnitCount+"#"+fCycleCount;

				}
				br.close();

				FUList.addAll(adderFUList);
				FUList.addAll(multiFUList);
				FUList.addAll(diviFUList);
				FUList.add(new FunctionalUnit(FunctionalUnit.UnitType.INTEGER, "I0", 1));
				FUList.add(new FunctionalUnit(FunctionalUnit.UnitType.DOUBLE, "DD0", 2));
				FUList.add(new FunctionalUnit(FunctionalUnit.UnitType.WORD, "W0", 1));
			} catch (Exception e) {

			}
		}

		return FUList;
	}

	public static ArrayList<Instruction> initializeInstructions(String filePath) {
		ArrayList<Instruction> instructionList = new ArrayList<Instruction>();
		if (!checkFile(filePath)) {
			// no/empty file
		} else {
			// continue
			try {
				BufferedReader br = new BufferedReader(new FileReader(filePath));
				String line, subLine, fUnit, jLabel, instructionLine, fullInstruction, instruction, destination,
						source1, source2;
				String lineArr[];
				boolean isJump, isHalt, isUnconditional;

				while ((line = br.readLine()) != null) {

					// reset variables
					instructionLine = "";
					fullInstruction = "";
					instruction = "";
					destination = "";
					source1 = "";
					source2 = "";
					jLabel = "";
					isJump = false;
					isHalt = false;
					isUnconditional = false;
					
					line = line.toUpperCase();

					if (line.trim().split(":").length > 1) {
						// Instructions with Jump label
						jLabel = line.trim().split(":")[0].trim().toUpperCase();
						instructionLine = line.trim().split(":")[1].trim().toUpperCase();
					} else {
						// All other instructions
						instructionLine = line.trim().split(":")[0].trim().toUpperCase();
					}

					fullInstruction = jLabel.equals("") ? instructionLine : jLabel + ":" + instructionLine;

					if (instructionLine.contains(",")) {
						// All instruction except jump and halt
						lineArr = instructionLine.split(",");
						// index 0 should have instruction and destination
						// index 1 should have source
						// index 2 should have source 2 if present

						instruction = lineArr[0].split(" ")[0].trim().toUpperCase();
						destination = lineArr[0].split(" ")[1].trim().toUpperCase();
						source1 = lineArr[1].trim().toUpperCase();
						source2 = "";

						if (lineArr.length > 2) {
							// two source
							source2 = lineArr[2].trim().toUpperCase();
						}

						if (instruction.equals("BNE") || instruction.equals("BEQ")) {
							isJump = true;
						}

					} else {
						// Jump and halt instruction
						instruction = instructionLine.split(" ")[0].trim().toUpperCase();
						if (instruction.equalsIgnoreCase("J")) {
							// Jump Instruction
							destination = instructionLine.split(" ")[0].trim().toUpperCase();
							isJump = true;
							isUnconditional = true;
						} else {
							// Halt Instruction
							if (instruction.equalsIgnoreCase("HLT")) {
								isHalt = true;
							}
						}
					}

					instructionList.add(new Instruction(fullInstruction, instruction, destination, source1, source2,
							jLabel, isJump, isHalt, isUnconditional));

				}
				br.close();
			} catch (Exception e) {

			}
		}

		return instructionList;
	}

	public static void initializeData(String filePath) {
		if (!checkFile(filePath)) {
			// no/empty file
		} else {
			// continue
		}
	}

	public static void initializeResult(String filePath) {
		if (!checkFile(filePath)) {
			// no/empty file
		} else {
			// continue
		}
	}

	public static boolean checkFile(String filePath) {
		boolean status = false;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			if (br.readLine() != null) {
				status = true;
			}
			br.close();
		} catch (Exception e) {

		}
		return status;
	}
}
