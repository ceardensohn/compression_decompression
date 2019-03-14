/**
 * Name: Decompress
 * 
 * @author Chris Eardensohn (ceardensohn@sandiego.edu)
 * @author Thaddeus Steele (tsteele@sandiego.edu)
 *
 * Date: 4/16/2018
 * 
 * Description: This file contains code to decompress a file
 * entered by a user. Creates decompressed file and a decompress
 * file log.
 */
import java.io.*;
import java.util.Scanner;

public class Decompress {
	public static void main(String[] args) throws IOException {
		decHashtable dht = new decHashtable();
		
		String inFile = args[0];
		
		File compressedFile = new File(inFile);	//compressed file user wants decompressed

		Scanner in = new Scanner(System.in);
		
		while (compressedFile.exists() == false) {
			System.out.println("File does not exist, please enter a new one:");
			inFile = in.next();
			compressedFile = new File(inFile); 
		}
		in.close();
		

		long startTime = System.nanoTime();
		dht.buildDictionaryDecompress(inFile);
		long endTime = System.nanoTime();
		double seconds = ((double) endTime-startTime)/1000000000.0; 
		
		String outputString = "";
		
		if (inFile.endsWith(".zzz")) {
			outputString = inFile.substring(0, (int)(inFile.length() - 4));
		}
		File outputFile = new File(outputString + ".log");
	
		
		dht.printDecompressLog((double)seconds, outputFile, compressedFile, dht.rehashCount);
	}
}

