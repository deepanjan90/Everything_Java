package main;

import java.io.BufferedReader;
import java.io.FileReader;

public class Initialize {
	public static void initializeConfig(String filePath) {
		if (!checkFile(filePath)) {
			// no/empty file
		} else {
			// continue
			try {
				BufferedReader br = new BufferedReader(new FileReader("path_to_some_file"));
				String line, subLine, fUnit;
				int fUnitCount = 0, fCycleCount = 0, lineCount = 0;
				while ((line = br.readLine()) != null) {
					if (lineCount > 4) {
						throw new Exception("Invalid configuration file!!!");
					}
					fUnit = line.trim().split(":")[0].trim().toLowerCase();
					subLine = line.trim().split(":")[1].trim();
					fUnitCount = Integer.parseInt(subLine.split(",")[0].trim());
					fCycleCount = Integer.parseInt(subLine.split(",")[1].trim());
					lineCount++;

				}
				br.close();
			} catch (Exception e) {

			}
		}
	}

	public static void initializeInstructions(String filePath) {
		if (!checkFile(filePath)) {
			// no/empty file
		} else {
			// continue
		}
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
			BufferedReader br = new BufferedReader(new FileReader("path_to_some_file"));
			if (br.readLine() != null) {
				status = true;
			}
			br.close();
		} catch (Exception e) {

		}
		return status;
	}
}
