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
		else if(args.length == 1)
		{
			final String execDir = System.getProperty("user.dir");
			
			String fileName = args[0];
			
			String filePath= execDir + "/" + fileName;
			File myFile = new File(filePath);
			try
			{
				if(myFile.exists())
				{
					Scanner sc = new Scanner(myFile);
					int sum = 0;
					int numItems = 0;
					while(sc.hasNextLine())
					{
						sum += Integer.parseInt(sc.nextLine());
						++numItems;
					}
					int average = sum / numItems;
					System.out.println("File Name: " + fileName);
					System.out.println("File Path: " + filePath);
					System.out.println("Number of entries: " + numItems);
					System.out.println("Average: " + average);
					System.out.println("Successfully calculated average value");
					sc.close();
				}
				else
				{
					System.out.println("File doesn't exist");
				}
			}
			catch (Exception e)
			{
				System.out.print("Failed to read from file. Exception:\n" + e);
			}
		}
		else
		{
			System.out.println("Too many arguements");
		}
	}
}