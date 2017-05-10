package main;

import java.util.ArrayList;
import java.util.Arrays;

public class Simulator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length<4)
		{
			//Error
			System.out.println("Please provide 4 arguments containg paths of instruction, data, config and output files respectively");
		}
		else
		{
			//Innitialize
			ScoreBoard sb = new ScoreBoard();
			sb.start(args[0], args[1], args[2], args[3]);
			/*InstructionCache ic = new InstructionCache(4, 4, new ArrayList<Integer>(Arrays.asList(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21)), 3);
			System.out.println(ic.GetBlockIndex(21));*/
		}
	}

}
