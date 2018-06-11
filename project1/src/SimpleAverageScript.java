import java.io.File;
import java.util.Scanner;

public class SimpleAverageScript 
{
	public static void main(String[] args) 
	{
		if(args.length < 1)
		{
			System.out.println("Error. Enter the name of the file to read...");
		}
		else
		{
			final String execDir = System.getProperty("user.dir");
			
			long sum = 0;
			long totalNumItems = 0;
			long numItems = 0;
			
			for(int i = 0; i < args.length; ++i)
			{
				String fileName = args[i];
				
				String filePath= execDir + "/" + fileName;
				File myFile = new File(filePath);
				try
				{
					if(myFile.exists())
					{
						numItems = 0;
						Scanner sc = new Scanner(myFile);
						while(sc.hasNextLine())
						{
							sum += Long.parseLong(sc.nextLine());
							++totalNumItems;
							++numItems;
						}
						System.out.println("File Name: " + fileName);
						System.out.println("\tFile Path: " + filePath);
						System.out.println("\tNumber of entries: " + numItems);
						System.out.println("\tSuccessfully calculated sum value");
						sc.close();
					}
					else
					{
						System.out.println("File doesn't exist");
					}
				}
				catch (Exception e)
				{
					System.out.println("Failed to read from file. Exception:\n" + e);
				}
			}
			double average = sum / totalNumItems;
			System.out.println("End Results\n~~~~~~~~~~~~");
			System.out.println("Average: " + average + " nano seconds");
			System.out.println("Sum: " + sum);
			System.out.println("Total Entries: " + totalNumItems);
			
		}
	}
}
