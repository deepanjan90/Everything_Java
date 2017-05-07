package main;

import java.util.ArrayList;
import java.util.Arrays;

public class Simulator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length<4)
		{
			//Error
			System.out.println("Error");
		}
		else
		{
			//Innitialize
			ScoreBoard sb = new ScoreBoard();
			sb.start(args[0], args[1], args[2], args[3]);
			
			/*InstructionCache ic = new InstructionCache(3, 2, new ArrayList<Integer>(Arrays.asList(0,1,2,3,4,5,6,7,8,9)), 1);
			System.out.println(ic.IsPresentForFetch(1));//a
			ic.ShowCach();
			System.out.println(ic.IsPresentForFetch(1));//a
			System.out.println("#####");
			System.out.println(ic.IsPresentForFetch(0));//a
			ic.ShowCach();
			System.out.println(ic.IsPresentForFetch(0));//a
			System.out.println("#####");
			System.out.println(ic.IsPresentForFetch(1));//a
			ic.ShowCach();
			System.out.println(ic.IsPresentForFetch(1));//a
			System.out.println("#####");
			System.out.println(ic.IsPresentForFetch(5));//c
			ic.ShowCach();
			System.out.println(ic.IsPresentForFetch(5));//a
			System.out.println("#####");
			System.out.println(ic.IsPresentForFetch(2));//b
			ic.ShowCach();
			System.out.println(ic.IsPresentForFetch(2));//a
			System.out.println("#####");
			System.out.println(ic.IsPresentForFetch(4));//c
			ic.ShowCach();
			System.out.println(ic.IsPresentForFetch(4));//a
			System.out.println("#####");
			System.out.println(ic.IsPresentForFetch(6));//d
			ic.ShowCach();
			System.out.println(ic.IsPresentForFetch(6));//a
			System.out.println("#####");
			System.out.println(ic.IsPresentForFetch(8));//e
			ic.ShowCach();
			System.out.println(ic.IsPresentForFetch(8));//a
			System.out.println("#####");*/
		}
	}

}
