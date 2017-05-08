package main;


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
			
			/*DataCache dataCache = new DataCache(2, 4, 2, 3);
			System.out.println(dataCache.IsPresent(64));
			System.out.println("View");
			dataCache.View();
			for(int i=0;i<12;i++){dataCache.GetDataBlock(64);}
			System.out.println("View");
			dataCache.View();
			System.out.println(dataCache.IsPresent(64));
			
			for(int i=0;i<12;i++){dataCache.GetDataBlock(68);}
			System.out.println("View");
			dataCache.View();
			for(int i=0;i<12;i++){dataCache.GetDataBlock(72);}
			System.out.println("View");
			dataCache.View();
			for(int i=0;i<12;i++){dataCache.GetDataBlock(76);}
			System.out.println("View");
			dataCache.View();

			System.out.println(dataCache.IsPresent(64));
			System.out.println("View");
			dataCache.View();
			
			for(int i=0;i<12;i++){dataCache.GetDataBlock(80);}
			System.out.println("View");
			dataCache.View();*/
		}
	}

}
