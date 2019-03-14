/**
 * Name: Compress
 * 
 * @author Chris Eardensohn (ceardensohn@sandiego.edu)
 * @author Thaddeus Steele (tsteele@sandiego.edu)
 *
 * Date: 4/16/2018
 * 
 * Description: This file contains code to compress a file
 * entered by a user. Creates compressed file and a compress
 * file log.
 */
import java.io.*;
import java.util.Scanner;

public class Compress {
	public static void main(String[] args) throws IOException {
		MyHashTable dic = new MyHashTable();
		
		String inputFileName = args[0];
		File inputFile = new File(inputFileName);

		Scanner in = new Scanner(System.in);
		
		while (inputFile.exists() == false) {
			System.out.println("File does not exist, please enter a new one:");
			inputFileName = in.next();
			inputFile = new File(inputFileName); 
		}
		in.close();
		
		File compressedFile = new File(inputFileName + ".zzz");
		String log = inputFileName + ".zzz.log";
		File logFile = new File(log);
		
		
		long startTime = System.nanoTime();
		
		DataOutputStream out = new DataOutputStream(new FileOutputStream(compressedFile, true));
		DataInputStream readBytes = new DataInputStream(new FileInputStream(inputFile));

		dic.buildDictionary(readBytes, compressedFile, out);
		readBytes.close();
	
		out.flush();
		out.close();

		long endTime = System.nanoTime();
		
		long time = endTime - startTime;
		double seconds = (double)time / 1000000000.0;
		
		
		dic.printCompressLog(seconds, logFile, inputFile, compressedFile);
	}
}
