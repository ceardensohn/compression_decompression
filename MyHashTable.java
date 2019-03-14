/**
 * Name: MyHashTable
 * 
 * @author Chris Eardensohn (ceardensohn@sandiego.edu)
 * @author Thaddeus Steele (tsteele@sandiego.edu)
 *
 * Date: 4/13/2018
 * 
 * Description: This file contains code to generate a hash
 * table named dictionary for the purpose of sorting 
 * strings from a user given text file. The table is 
 * separated into a key with a mapped value with the result
 * of writing a compressed file.
 */
import java.io.*;
public class MyHashTable {
	
	//fields of MyHashTable
	private Node[] dictionary;
	private int tableCapacity; 
	private int size;
	public int rehashCount = 0;

	//constructor of MyHashtable
	public MyHashTable() {
		tableCapacity = 997; //some large prime number
		size = 0; //total amount of entries
		dictionary = new Node[tableCapacity];
		initDictionary();
	}
	
	public int size() {
		return size;
	}
	
	public int tableCapacity() {
		return tableCapacity;
	}
	
	public boolean isEmpty() {
		return size() == 0;
	}
	

	/**
	 * getListIndex gets an index depending on short prefix value
	 * and the suffix char.  Uses quadratic probing to avoid 
	 * primary clustering.
	 * @param prefixValue is the prefix short of key
	 * @param suffix is the suffix char of key
	 * @return index is the index used for hash
	 */
	public int getListIndex(short prefixValue, char suffix) {
		int index;
		if(prefixValue == -1) {
			index = (int) suffix;
		} else {
			index = (int) prefixValue + (int) suffix;
			index = Math.abs(index) + (int)Math.pow(prefixValue, 2);
		}
		index = index % tableCapacity;
		return index;
	}
	
	/**
	 * checkPrime checks if value is prime.  Used for 
	 * finding next prime number for rehashing.
	 * @param x is an integer to check for prime
	 * @return a boolean if param x is prime
	 */
	public boolean checkPrime(int x) {
		//check if even
		if (x % 2 == 0)
			return false;
		//check factors using squares
		for (int i = 3; i*i <= x; i += 2) {
			if (x % i == 0)
				return false;
		}
		return true;
	}
	
	/**
	 * findNextCapacity takes the old table capacity to find
	 * a new capacity that is prime that is approximately
	 * twice the size of the old capacity.
	 * @param capacity is the old table capacity of the dictionary
	 * @return nextCapacity is the new table capcity of the dictionary
	 */
	public int findNextCapacity(int capacity) {
		int nextCapacity = capacity * 2;
		boolean isPrime = checkPrime(nextCapacity);
		while(isPrime == false) {
			nextCapacity += 1;
			isPrime = checkPrime(nextCapacity);
		}
		return nextCapacity;
	}
	
	/**
	 * contains takes a string and finds its prefix and char
	 * entry in the dictionary.
	 * @param key is the string to be checked in the dictionary
	 * @return boolean condition if string is in dictionary
	 */
	public boolean contains(String key) {
		//find node equal to prefix and suffix
		short prefix = calcPrefixValue(key);
		char suffix = key.charAt(key.length()-1);
		int hashIndex = getListIndex(prefix, suffix);
		Node node = dictionary[hashIndex];
		
		while (node != null) {
			if(node.item.prefix == prefix && node.item.suffix == suffix) {
				return true;
			}
			node = node.next;
		}
		return false;
	}
	
	/**
	 * calcPrefixValue is used to find the short + char
	 * version of a string prefix.  Builds a prefix from the first char.
	 * @param key is the prefix string to be converted to short value
	 * @return prevPrefixValue is the prefix value
	 */
	public short calcPrefixValue(String key){
		//separate key to prefix and suffix
		short prevPrefixValue = 0;

		if(key.length() == 1) {
			//single char prefix
			return -1;
		} else {
			//value finder starting from first char
			prevPrefixValue = getCharValue(key.charAt(prevPrefixValue));
			for (int i = 1; i < key.length()-1; i++) {
				//replace prevPrefixValue until prefix is built
				prevPrefixValue = getPrefixValue(prevPrefixValue, key.charAt(i));	
			}
			return prevPrefixValue;
		}
	}
	
	/**
	 * getCharValue gets the value of a single char without a prefix
	 * @param suffix is the char suffix following a short value in key
	 * @return the key's value for single char
	 */
	public short getCharValue (char suffix) {
		int index = getListIndex((short)-1, suffix);
		Node curr = dictionary[index];
		while (curr != null) {
			if (curr.item.prefix == -1 && curr.item.suffix == suffix)
				return curr.item.value;
			curr = curr.next;
		}
		return -1;
	}
		
	/**
	 * getPrefixValue finds value from the dictionary of
	 * prefix and suffix parameters.
	 * @param prevPrefixValue is the prefix value key for lookup
	 * @param suffix is the suffix char key for look up
	 * @return the value of the item in dictionary
	 */
	public short getPrefixValue(short prevPrefixValue, char suffix) {
		int index = getListIndex(prevPrefixValue, suffix);
		Node curr = dictionary[index];
		while (curr != null) {
			if(curr.item.prefix == prevPrefixValue && curr.item.suffix == suffix) {
				return curr.item.value;
			}
			curr = curr.next;
		}
		return -1;
	}
	
	/**
	 * findItem takes a string and finds the matching prefix suffix
	 * item.
	 * @param key is the string from the user file to be used to search dictionary
	 * @return result is the item associated with key
	 */
	public Item findItem(String key) {
		//get short+char version of key
		short prefix = calcPrefixValue(key);
		char suffix = key.charAt(key.length()-1);

		int index = getListIndex(prefix, suffix);
		Item result = null;
		Node node = dictionary[index];
		
		while (node != null) {
			if(node.item.prefix == prefix && node.item.suffix == suffix) {
				result = node.item;
			}
			node = node.next;
		}
		return result;
	}
	

	/**
	 * add function adds the item to the dictionary. Also rehashes if
	 * load factor is above 0.7
	 * @param item is the item to be added into dictionary
	 */
	public void add(Item item) {
		int index = getListIndex(item.prefix, item.suffix);
		Node node = dictionary[index];
		
		//find head of list
		while (node != null) {
			if(node.item.prefix == item.prefix && node.item.suffix == item.suffix) {
				node.item.value = item.value;
				return;
			}
			node = node.next;
		}
		
		size = size + 1;
		node = dictionary[index];
		Node newEntry = new Node(item, node);
		dictionary[index] = newEntry;
		
		//check for load factor. If above 0.7, rehash.
		if ((size()/(double)tableCapacity()) >= 0.7) {
			rehashCount++;
			tableCapacity = findNextCapacity(tableCapacity);
			//copy dictionary
			Node[] temp = dictionary;
			dictionary = new Node[tableCapacity];
			size = 0;
			//fill new dictionary
			for(Node curr : temp) {
				while(curr != null) {
					add(curr.item);
					curr = curr.next;
				}
			}
		}
	}
	
	/*
	 * initDictionary initializes the dictionary with ascii
	 * characters
	 */
	public void initDictionary() {
		for(int i = 0; i < 256; i++){	
			char charKey = (char) i;
			//String key = Character.toString(charKey);
			Item item = new Item(charKey, (short)size); //suffix, size only
			Node newNode = new Node(item, null);
			int index = getListIndex(item.prefix, item.suffix);
			dictionary[index] = newNode;
			size = size + 1;
		}
	}
	
	/**
	 * largestListSize goes through each start of the 
	 * linked list and counts the number of linked nodes.
	 * Saves the largest count of linked nodes.
	 * @return maxSize is the count of largest linked list
	 */
	public int largestListSize() {
		int maxSize = 0;
		for (int i = 0; i < tableCapacity; i++) {
			int listSize = 0;
			Node curr = dictionary[i];
			while(curr != null) {
				listSize = listSize + 1;
				curr = curr.next;
			}
			if (listSize > maxSize) {
				maxSize = listSize;
			}
		}
		return maxSize;
	}
	
	/**
	 * averageListSize returns the average size of linked list in dictionary
	 * @return average the average list size
	 */
	public double averageListSize() {
		int sumListSize = 0;
		for (int i = 0; i < tableCapacity; i++) {
			int listSize = 0;
			Node curr = dictionary[i];
			while(curr != null) {
				listSize = listSize + 1;
				curr = curr.next;
			}
			sumListSize = sumListSize + listSize;
		}
		double average = (double)sumListSize / tableCapacity;
		return average;
	}
	
	/*
	 * printDictionary will print the dictionary information
	 * starting from the first to last index and all linked nodes.
	 */
	public void printDictionary() {
		for (int i = 0; i < tableCapacity; i++) {
			Node curr = dictionary[i];
			if (curr != null) {
				System.out.println("");
				System.out.print("index: " + i + " | key: " + curr.item.prefix + curr.item.suffix + " | value: " + curr.item.value);
				while(curr != null) {
					curr = curr.next;
					if (curr != null) {
						System.out.print(" --> key: " + curr.item.prefix + curr.item.suffix + " | value: "+ curr.item.value);
					}
				}	
			}
		}
		System.out.println("");
	}
	
	/**
	 * buildDictionary takes in string from the user input files.
	 * the function adds items to dictionary and prints items into
	 * compressed form using DataOutputStream.
	 * @param line is the string from the input file
	 * @param fileName is the new compressed file
	 */
	public void buildDictionary(DataInputStream in, File fileName, DataOutputStream out) {
		String pConcatC = ""; //largest prefix + last char
		String p = "";
		char c;
		boolean endOfFile = false;
		try {
			while(!endOfFile) {
				c = (char)in.readByte();
				pConcatC += Character.toString(c);
				if(contains(pConcatC)) {
					p = pConcatC; //largest prefix
				}
				else {
					//output largest prefix to new file
					Item item = findItem(p);
					
					out.writeShort((int)item.value);
					
					//add prefix plus next char to dictionary
					item = new Item(item.value, c, (short)size);
					add(item);
					pConcatC = Character.toString(c);
					p = Character.toString(c);
				}
			}
		} catch (IOException e) {
			try {
				Item item = findItem(p);
				out.writeShort((int)item.value);
			} 
			catch (IOException exc) {
					System.out.println("Error with read");
			} 
			endOfFile = true;
		}
	}
	
	/**
	 * printCompressLog prints the log file for compression
	 * @param time the time taken to complete compression
	 * @param logFile the newly written file
	 * @param inputFile the file entered by user
	 * @param compressedFile the file compressed by program
	 * @throws IOException
	 */
	public void printCompressLog(double time, File logFile, File inputFile, File compressedFile) throws IOException {
		int loadFactor = (int)(size()/(double)tableCapacity()*100.0); 
		PrintWriter writer = new PrintWriter(logFile);
		writer.println("Compression of " + inputFile.toString());
		writer.println("Compressed from " + (inputFile.length()/1000) + " Kilobytes to " + (compressedFile.length()/1000) + " Kilobytes.");
		writer.println("Compression took " + ((double)Math.round(time * 1000.0) / 1000.0) + " seconds." );
		writer.println("Hash Table is " + loadFactor + "% full.");
		writer.println("The average linked list is " + ((double)Math.round(averageListSize() * 1000.0) / 1000.0) + " elements long.");
		writer.println("The longest linked list contains " + largestListSize() + " elements.");
		writer.println("The dictionary contains " + size() + " entries.");
		writer.println("The table was rehashed " + rehashCount + " times.");
		writer.close();
	}
}
