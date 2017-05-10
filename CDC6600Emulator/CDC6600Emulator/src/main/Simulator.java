package main;


public class Simulator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length<4)
		{
			//Error
			System.out.println("Please provide 4 arguments containg paths of config, instruction data and output files respectively");
		}
		else
		{
			//Innitialize
			ScoreBoard sb = new ScoreBoard();
			sb.start(args[0], args[1], args[2], args[3]);
		}
	}

}
