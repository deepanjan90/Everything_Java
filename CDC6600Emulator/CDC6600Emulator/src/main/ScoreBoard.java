package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import main.FunctionalUnit.UnitType;
import main.InstructionPipeline.StageType;

public class ScoreBoard {

	private ArrayList<FunctionalUnit> functionalUnitList = new ArrayList<FunctionalUnit>();
	private ArrayList<Instruction> instructionList = new ArrayList<Instruction>();
	private HashMap<String, Integer> variablesMap = new HashMap<String, Integer>();

	public void start(String filePathConfig, String filePathInstruction, String filePathData, String filePathOutPut) {

		setup(filePathConfig, filePathInstruction, filePathData);
		run(filePathOutPut);
	}

	private void setup(String filePathConfig, String filePathInstruction, String filePathData) {
		functionalUnitList = new ArrayList<FunctionalUnit>();
		instructionList = new ArrayList<Instruction>();
		ArrayList<String> variables = new ArrayList<String>();
		try {
			functionalUnitList = Initialize.initializeConfig(filePathConfig);
			instructionList = Initialize.initializeInstructions(filePathInstruction);

			for (FunctionalUnit functionalUnit : functionalUnitList) {
				System.out.println("Id: " + functionalUnit.id + " , type: " + functionalUnit.type + " , lat: "
						+ functionalUnit.latency + " , clk: " + functionalUnit.clockCycleUsed + " , isFree"
						+ functionalUnit.isFree);
			}

			for (Instruction instruction : instructionList) {
				System.out.println(instruction.instruction + "\t" + instruction.destination + "\t" + instruction.source1
						+ "\t" + instruction.source2 + "\t" + instruction.label + "\t" + instruction.isJump + "\t"
						+ instruction.isUnconditional + "\t" + instruction.fullInstruction);
			}

			for (Instruction instruction : instructionList) {
				if (!instruction.destination.equals("") && !instruction.isUnconditional) {
					if (!variables.contains(instruction.destination))
						variables.add(instruction.destination);
				}

				if (!instruction.source1.equals("")) {
					if (instruction.source1.contains("R") || instruction.source1.contains("F")) {
						if (instruction.source1.contains("\\(")) {
							if (!variables.contains(instruction.source1.split("\\(")[1].trim().split("\\)")[0].trim()))
								variables.add(instruction.source1.split("\\(")[1].trim().split("\\)")[0].trim());
						} else {
							if (!variables.contains(instruction.source1))
								variables.add(instruction.source1);
						}
					} else {
						// Discarded as Number
						try {
							Integer.parseInt(instruction.source1);
						} catch (Exception e) {
							throw new Exception("Invalid Instruction");
						}
					}
				}

				if (!instruction.source2.equals("") && !instruction.isJump) {
					if (instruction.source2.contains("R") || instruction.source2.contains("F")) {
						if (instruction.source2.contains("\\(")) {
							if (!variables.contains(instruction.source2.split("\\(")[1].trim().split("\\)")[0].trim()))
								variables.add(instruction.source2.split("\\(")[1].trim().split("\\)")[0].trim());
						} else {
							if (!variables.contains(instruction.source2))
								variables.add(instruction.source2);
						}
					} else {
						// Discarded as Number
						try {
							Integer.parseInt(instruction.source2);
						} catch (Exception e) {
							throw new Exception("Invalid Instruction");
						}
					}
				}
			}

			for (String string : variables) {
				variablesMap.put(string, 0);
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.exit(0);
		}
	}

	private void run(String filePathOutPut) {
		int instructionStartIndexMaster = 0;
		int instructionEndIndex = -1;
		ArrayList<String> workingRegisters;
		ArrayList<InstructionPipeline> workingInstructionList = new ArrayList<InstructionPipeline>();
		ArrayList<InstructionPipeline> finishedInstructionList = new ArrayList<InstructionPipeline>();
		Map<Integer, String> inProcessRegisters = new HashMap<Integer, String>();
		ArrayList<Integer> instructionIdList = new ArrayList<Integer>();
		InstructionCache instructionCache;
		int clockCycle = 1;
		boolean isFetchBusy = false;
		boolean done = false;

		for (Instruction instruction : instructionList) {
			instructionIdList.add(instruction.id);
		}

		instructionCache = new InstructionCache(Integer.parseInt(Initialize.CacheLine.split("#")[0]),
				Integer.parseInt(Initialize.CacheLine.split("#")[1]), instructionIdList, 3);

		do {
			// if (clockCycle > 33)
			// break;

			if (clockCycle == 174) {
				// BreakPoint
				// isFetchBusy = false;
				clockCycle = clockCycle;
			}

			if (workingInstructionList.size() <= 0) {
				if (clockCycle == 1) {
					workingInstructionList.add(new InstructionPipeline(instructionList.get(0)));
				} else {
					done = true;
				}
			}

			int workingInstructionIndex = 0;
			ArrayList<InstructionPipeline> finishedInstructionListTemp = new ArrayList<InstructionPipeline>();
			while (workingInstructionList.size() > workingInstructionIndex) {
				InstructionPipeline workingInstruction = workingInstructionList.get(workingInstructionIndex);

				if (workingInstruction.stageType == StageType.NS) {
					// Not Stared Pipeline
					// To push to fetch stage
					if (!isFetchBusy) {
						if (instructionCache.IsPresentForFetch(workingInstruction.instruction.id)) {
							workingInstruction.Fetch(clockCycle);
							inProcessRegisters.put(workingInstruction.id, workingInstruction.instruction.destination);
							isFetchBusy = true;
						}
					} else {

					}
					break;
				} else if (workingInstruction.stageType == StageType.FETCH) {
					// Pipeline in fetch
					// To push to issue stage

					// check if Integer Unit free
					String integerUnitId = CheckAndGetIdFreeUnit(workingInstruction.functionalUnitType);

					boolean hazardFlag = false;

					HashMap<Integer, String> registerWithoutCurrent = new HashMap<>(inProcessRegisters);
					registerWithoutCurrent.keySet().removeIf(o -> o.intValue() >= workingInstruction.id);

					if (registerWithoutCurrent.containsValue(workingInstruction.instruction.destination)) {
						// WAW Hazard
						if (!workingInstruction.instruction.isJump) {
							workingInstruction.MarkWAW();
							hazardFlag = true;
						}
					}

					if ((!integerUnitId.equals("") && !hazardFlag) || workingInstruction.instruction.isJump) {
						// Integer Unit Available
						workingInstruction.Issue(clockCycle);
						if (!workingInstruction.instruction.isJump) {
							workingInstruction.SetFunctionUnit(functionalUnitList.stream()
									.filter(o -> o.id.equals(integerUnitId)).findFirst().get());
							workingInstruction.functionalUnit.UpdateStatusToUsing();
						}
						isFetchBusy = false;

						if (workingInstruction.instruction.isHalt) {
							// Halt Instruction

							InstructionPipeline tempToCheckBranch = null;
							int tempId = workingInstruction.id - 1;
							
							if (workingInstructionList.stream().filter(o->o.id==tempId).count()>0) {
								tempToCheckBranch = workingInstructionList.get(workingInstructionIndex - 1);
							} else if (finishedInstructionList.stream().filter(o->o.id==tempId).count()>0){
								tempToCheckBranch = finishedInstructionList.stream().filter(o->o.id==tempId).findFirst().get();
							}

							if (tempToCheckBranch!=null && tempToCheckBranch.instruction.isJump) {
								if (tempToCheckBranch.branchtaken) {
									workingInstruction.Issue(0);
								} else {
									workingInstruction.Issue(clockCycle);
								}
							} else {
								workingInstruction.Issue(0);
							}
							workingInstruction.Read(0); // No Read
							workingInstruction.Execute(0); // No execute Cycle
							workingInstruction.Write(0); // No write Cycle
							workingInstruction.stageType = StageType.WRITE;
						}

					} else {
						// Structural hazard
						// Check exception for Halt
						if (!workingInstruction.instruction.isHalt) {
							workingInstruction.MarkStruct();
						}

						if (instructionStartIndexMaster + 1 < instructionList.size())
							instructionCache.IsPresentForFetch(instructionList.get(instructionStartIndexMaster + 1).id);

					}
				} else if (workingInstruction.stageType == StageType.ISSUE) {
					// Pipeline in Issue
					// To push to Read stage

					boolean hazardFlag = false;

					HashMap<Integer, String> registerWithoutCurrent = new HashMap<>(inProcessRegisters);
					registerWithoutCurrent.keySet().removeIf(o -> o.intValue() >= workingInstruction.id);

					if (workingInstruction.instruction.isJump && !workingInstruction.instruction.isUnconditional) {
						if (registerWithoutCurrent.containsValue(workingInstruction.instruction.source1)
								|| registerWithoutCurrent.containsValue(workingInstruction.instruction.destination)) {

							// RAW Hazard
							workingInstruction.MarkRAW();
							hazardFlag = true;
						}
					} else {
						if (registerWithoutCurrent.containsValue(workingInstruction.instruction.source1)
								|| registerWithoutCurrent.containsValue(workingInstruction.instruction.source2)) {

							// RAW Hazard
							workingInstruction.MarkRAW();
							hazardFlag = true;
						}
					}
					if (registerWithoutCurrent.containsValue(workingInstruction.instruction.source1)
							|| registerWithoutCurrent.containsValue(workingInstruction.instruction.source2)) {

						// RAW Hazard
						workingInstruction.MarkRAW();
						hazardFlag = true;
					}

					if (!hazardFlag) {

						if (workingInstruction.instruction.isJump) {
							workingInstruction.Read(clockCycle);
							workingInstruction.Execute(0); // No Execute Cycle
							workingInstruction.Write(0); // No write Cycle
							workingInstruction.stageType = StageType.WRITE;
							if (workingInstruction.instruction.isUnconditional) {
								// Jump Instruction Unconditional
								String label = workingInstruction.instruction.source1;
								instructionStartIndexMaster = instructionList.indexOf(
										instructionList.stream().filter(o -> o.label.equals(label)).findFirst().get())
										- 1;
								workingInstruction.branchtaken = true;

							} else {
								// Jump Instruction Conditional

								int checkValue1 = variablesMap.get(workingInstruction.instruction.destination);
								int checkValue2 = variablesMap.get(workingInstruction.instruction.source1);

								if ((workingInstruction.instruction.instruction.equals("BNE")
										&& checkValue1 != checkValue2)
										|| (workingInstruction.instruction.instruction.equals("BEQ")
												&& checkValue1 == checkValue2)) {
									String label = workingInstruction.instruction.source2;
									instructionStartIndexMaster = instructionList.indexOf(instructionList.stream()
											.filter(o -> o.label.equals(label)).findFirst().get()) - 1;
									workingInstruction.branchtaken = true;
								} else {
									workingInstruction.branchtaken = false;
								}
							}
						} else {
							workingInstruction.Read(clockCycle);
						}
					}

				} else if (workingInstruction.stageType == StageType.READ) {
					// Pipeline in Read
					// To push to Execute stage

					// Execution Start
					workingInstruction.Execute(clockCycle);

				} else if (workingInstruction.stageType == StageType.EXEC) {
					// Pipeline in Execute
					// To push to Write stage

					// Execution Still not complete
					if (!workingInstruction.functionalUnit.HasCompleted()) {
						workingInstruction.UpdateExecute(clockCycle);
					} else {
						// Execution Complete
						// Writing to clock
						workingInstruction.Write(clockCycle);
						DoInstructionWork(workingInstruction.instruction);
					}

				} else if (workingInstruction.stageType == StageType.WRITE) {
					// resetting functional Unit
					if (workingInstruction.functionalUnit != null) {
						workingInstruction.functionalUnit.ResetUnit();
					}

					// Adding to finished list
					finishedInstructionList.add(workingInstruction);

					// Marking for removal from working List
					finishedInstructionListTemp.add(workingInstruction);

					// Clearing inProcessRegisters
					inProcessRegisters.remove(workingInstruction.id);
				}

				if (workingInstructionIndex + 1 == workingInstructionList.size() && !isFetchBusy
						&& instructionStartIndexMaster + 1 < instructionList.size()) {
					// Load new instruction to fetch
					// Check if it is the last instruction for this cycle
					// Check if fetch unit is free to load new instruction
					// Check if new instruction available to load
					workingInstructionList
							.add(new InstructionPipeline(instructionList.get(++instructionStartIndexMaster)));
				}

				workingInstructionIndex++;

			}

			for (InstructionPipeline finishedInstruction : finishedInstructionListTemp) {
				workingInstructionList.remove(finishedInstruction);
			}
			
			clockCycle++;

			System.out.println(
					"************************************************************************************************************************");

			for (InstructionPipeline ip : finishedInstructionList) {
				WriteInstructionPipelineStage(ip);
			}

			System.out.println(
					"************************************************************************************************************************");

			for (InstructionPipeline ip : workingInstructionList) {
				WriteInstructionPipelineStage(ip);
			}

			System.out.println(
					"************************************************************************************************************************");

			for (FunctionalUnit functionalUnit : functionalUnitList) {
				System.out.println("Id: " + functionalUnit.id + " , type: " + functionalUnit.type + " , lat: "
						+ functionalUnit.latency + " , clk: " + functionalUnit.clockCycleUsed + " , isFree"
						+ functionalUnit.isFree);
			}

			System.out.println(
					"************************************************************************************************************************");

		} while (!done);

		finishedInstructionList.sort(new Comparator<InstructionPipeline>() {

			@Override
			public int compare(InstructionPipeline o1, InstructionPipeline o2) {
				// TODO Auto-generated method stub
				return o1.id - o2.id;
			}

		});
		;

		System.out.println("ID \t Instruction \t\t\tStatus Fetch \tIssue \tRead \tExec \tWrite \tRAW \tWAW \tStruct");
		for (InstructionPipeline ip : finishedInstructionList) {
			WriteInstructionPipelineStage(ip);
		}

		WriteToOutputFile(finishedInstructionList, filePathOutPut);

	}

	public String CheckAndGetIdFreeUnit(FunctionalUnit.UnitType unitType) {
		String id = "";
		Optional<FunctionalUnit> op = functionalUnitList.stream().filter(o -> o.type == unitType && o.isFree)
				.findFirst();
		if (op.isPresent()) {
			id = op.get().id;
		}
		return id;
	}

	public void DoInstructionWork(Instruction instruction) {
		int temp1 = 0, temp2 = 0, temp3 = 0;
		if (instruction.instruction.equals("LUI")) { // Load Upper Immediate
			temp1 = variablesMap.get(instruction.source1);
			temp2 = temp1 >> 16;
			variablesMap.remove(instruction.destination);
			variablesMap.put(instruction.destination, temp2);
		} else if (instruction.instruction.equals("LI")) { // Load Immediate
			if (instruction.source1.startsWith("F") || instruction.source1.startsWith("R")) {
				temp1 = variablesMap.get(instruction.source1);
			} else {
				temp1 = Integer.parseInt(instruction.source1);
			}
			variablesMap.remove(instruction.destination);
			variablesMap.put(instruction.destination, temp1);
		} else if (instruction.instruction.equals("LW")) { // Load Word
			/*if (instruction.source1.contains("(")) {
				temp1 = variablesMap.get(instruction.source1.split("\\(")[1].split("\\)")[0]);
				temp2 = Integer.parseInt(instruction.source1.split("\\(")[0]);
				variablesMap.remove(instruction.destination);
				variablesMap.put(instruction.destination, temp1 + temp2);
			} else {
				temp1 = variablesMap.get(instruction.source1);
				variablesMap.remove(instruction.destination);
				variablesMap.put(instruction.destination, temp1);
			}
			variablesMap.remove(instruction.destination);
			variablesMap.put(instruction.destination, temp1);*/
		} else if (instruction.instruction.equals("SW")) { // Store Word
			/*if (instruction.source1.contains("(")) {
			temp1 = variablesMap.get(instruction.source1.split("\\(")[1].split("\\)")[0]);
			temp2 = Integer.parseInt(instruction.source1.split("\\(")[0]);
			variablesMap.remove(instruction.destination);
			variablesMap.put(instruction.destination, temp1 + temp2);
			} else {
			temp1 = variablesMap.get(instruction.source1);
			variablesMap.remove(instruction.destination);
			variablesMap.put(instruction.destination, temp1);
			}
			variablesMap.remove(instruction.destination);
			variablesMap.put(instruction.destination, temp1);*/
		} else if (instruction.instruction.equals("DSUB")) { // DSUB
			temp1 = variablesMap.get(instruction.source1);
			temp2 = variablesMap.get(instruction.source2);
			variablesMap.remove(instruction.destination);
			variablesMap.put(instruction.destination, temp1 - temp2);
		} else if (instruction.instruction.equals("DSUBI")) { // DSUBI
			temp1 = variablesMap.get(instruction.source1);
			temp2 = Integer.parseInt(instruction.source2);
			variablesMap.remove(instruction.destination);
			variablesMap.put(instruction.destination, temp1 - temp2);
		} else if (instruction.instruction.equals("DADD")) { // DADD
			temp1 = variablesMap.get(instruction.source1);
			temp2 = variablesMap.get(instruction.source2);
			variablesMap.remove(instruction.destination);
			variablesMap.put(instruction.destination, temp1 + temp2);
		} else if (instruction.instruction.equals("DADDI")) { // DADDI
			temp1 = variablesMap.get(instruction.source1);
			temp2 = Integer.parseInt(instruction.source2);
			variablesMap.remove(instruction.destination);
			variablesMap.put(instruction.destination, temp1 + temp2);
		} else if (instruction.instruction.equals("AND")) { // AND
			temp1 = variablesMap.get(instruction.source1);
			temp2 = Integer.parseInt(instruction.source2);
			variablesMap.remove(instruction.destination);
			variablesMap.put(instruction.destination, temp1 & temp2);
		} else if (instruction.instruction.equals("ANDI")) { // ANDI
			temp1 = variablesMap.get(instruction.source1);
			temp2 = Integer.parseInt(instruction.source2);
			variablesMap.remove(instruction.destination);
			variablesMap.put(instruction.destination, temp1 & temp2);
		} else if (instruction.instruction.equals("OR")) { // OR
			temp1 = variablesMap.get(instruction.source1);
			temp2 = Integer.parseInt(instruction.source2);
			variablesMap.remove(instruction.destination);
			variablesMap.put(instruction.destination, temp1 | temp2);
		} else if (instruction.instruction.equals("ORI")) { // ORI
			temp1 = variablesMap.get(instruction.source1);
			temp2 = Integer.parseInt(instruction.source2);
			variablesMap.remove(instruction.destination);
			variablesMap.put(instruction.destination, temp1 | temp2);
		} else if (instruction.instruction.equals("ADD.D")) { // ADD.D
			temp1 = variablesMap.get(instruction.source1);
			temp2 = variablesMap.get(instruction.source2);
			variablesMap.remove(instruction.destination);
			variablesMap.put(instruction.destination, temp1 + temp2);
		} else if (instruction.instruction.equals("SUB.D")) { // SUB.D
			temp1 = variablesMap.get(instruction.source1);
			temp2 = variablesMap.get(instruction.source2);
			variablesMap.remove(instruction.destination);
			variablesMap.put(instruction.destination, temp1 - temp2);
		} else if (instruction.instruction.equals("MUL.D")) { // MUL.D
			temp1 = variablesMap.get(instruction.source1);
			temp2 = variablesMap.get(instruction.source2);
			variablesMap.remove(instruction.destination);
			variablesMap.put(instruction.destination, temp1 * temp2);
		} else if (instruction.instruction.equals("DIV.D")) { // DIV.D
			temp1 = variablesMap.get(instruction.source1);
			temp2 = variablesMap.get(instruction.source2);
			variablesMap.remove(instruction.destination);
			variablesMap.put(instruction.destination, temp1 / temp2);
		}
	}

	public void WriteInstructionPipelineStage(InstructionPipeline instructionPipeline) {
		System.out.println(instructionPipeline.id + "\t" + instructionPipeline.instruction.instruction + "\t"
				+ instructionPipeline.instruction.destination + "\t" + instructionPipeline.instruction.source1 + "\t"
				+ instructionPipeline.instruction.source2 + "\t" + instructionPipeline.stageType + "\t"
				+ instructionPipeline.fetch + "\t" + instructionPipeline.issue + "\t" + instructionPipeline.read + "\t"
				+ instructionPipeline.exec + "\t" + instructionPipeline.write + "\t" + instructionPipeline.raw + "\t"
				+ instructionPipeline.waw + "\t" + instructionPipeline.struct + "\t");
	}

	public void WriteToOutputFile(ArrayList<InstructionPipeline> outputList, String filePathOutPut) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePathOutPut))) {

			bw.write("Instruction \t Fetch \t Issue \t Read \t Exec \t Write \t RAW \t WAW \t Struct");
			bw.newLine();
			for (InstructionPipeline instructionPipeline : outputList) {
				bw.write(instructionPipeline.instruction.fullInstruction + "\t" + instructionPipeline.fetch + "\t"
						+ instructionPipeline.issue + "\t" + instructionPipeline.read + "\t" + instructionPipeline.exec
						+ "\t" + instructionPipeline.write + "\t" + instructionPipeline.raw + "\t"
						+ instructionPipeline.waw + "\t" + instructionPipeline.struct);
				bw.newLine();
			}

			String content = "This is the content to write into file\n";

			bw.write(content);

			// no need to close it.
			// bw.close();

			System.out.println("Done");

		} catch (Exception e) {

			e.printStackTrace();

		}

	}
}
