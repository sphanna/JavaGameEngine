package util;

public class BlockTimer {

	@SuppressWarnings("finally")
	public static double code(Runnable block){
		double startTime = Clock.getTime();
		double endTime;
		
		try{
			block.run();
		} finally {
			endTime = Clock.getTime();
			return endTime - startTime;
		}
	}
	
	public static void printTime(Runnable block){
		double startTime = Clock.getTime();
		double endTime;
		
		try{
			block.run();
		} finally {
			endTime = Clock.getTime();
			System.out.println(endTime - startTime);
		}
	}
}
