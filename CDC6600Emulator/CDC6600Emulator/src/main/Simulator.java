package main;


public class Simulator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length<4)
		{
			//Error
			System.out.println("Usage: simulator inst.txt data.txt config.txt result.txt");
		}
		else
		{
			//Innitialize
			ScoreBoard sb = new ScoreBoard();
			sb.start(args[0], args[1], args[2], args[3]);
		}
	}

}
