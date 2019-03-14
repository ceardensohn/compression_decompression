/**
 * Name: decHashTable
 * 
 * @author Chris Eardensohn (ceardensohn@sandiego.edu)
 * @author Thaddeus Steele (tsteele@sandiego.edu)
 *
 * Date: 4/13/2018
 * 
 * Description: This file contains code to generate a hash
 * table named dict for the purpose of sorting shorts 
 * from a user given text file. The table is separated into a key 
 * with a mapped value with the result of writing a text file.
 */
import java.io.*;

public class decHashtable {

	private Individual[] dict;
	private int tableCapacity;
	private int size;
	public int rehashCount = 0;

	public decHashtable() {
		tableCapacity = 997; // some large prime number
		size = 0; // total amount of entries
		dict = new Individual[tableCapacity];
		initDecompressDictionary();
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

	// initializes dictionary with ascii characters
	public void initDecompressDictionary() { 
											
		for (int i = 0; i < 256; i++) {
			char character = (char) i;
			String key = Character.toString(character);
			// String key = Character.toString(charKey);
			decItem item = new decItem(key, (short) size); // suffix, size only
			Individual newNode = new Individual(item, null);
			int index = getListIndex(item.key);
			dict[index] = newNode;
			size = size + 1;
		}
	}

	/**
	 * oneMore function adds the item to the dictionary. Also rehashes if load
	 * factor is above 0.7
	 * 
	 * @param item is the item to be added into dictionary
	 */
	public void oneMore(decItem item) { // NEW METHOD
		int index = getListIndex(item.key);
		Individual node = dict[index];

		// find head of list
		while (node != null) {
			if (node.item.value == item.value) {
				node.item.value = item.value;
				return;
			}
			node = node.next;
		}

		size = size + 1;
		node = dict[index];
		Individual newEntry = new Individual(item, node);
		dict[index] = newEntry;

		// check for load factor. If above 0.7, rehash.
		if ((size() / (double) tableCapacity()) >= 0.7) {
			rehashCount++;
			tableCapacity = findNextCapacity(tableCapacity);
			// copy dictionary
			Individual[] temp = dict;
			dict = new Individual[tableCapacity];
			size = 0;
			// fill new dictionary
			for (Individual curr : temp) {
				while (curr != null) {
					oneMore(curr.item);
					curr = curr.next;
				}
			}
		}
	}

	/*
	 * pqAdd function adds Q + First character of p to 
	 * dictionary at in array location [size]
	 */
	public void pqAdd(short nextKey, decItem q, char firstCharP) {

		decItem newItem = new decItem(q.value + firstCharP, nextKey);

		int index = getListIndex(newItem.key);
		Individual node = dict[index];

		while (node != null) {
			if (node.item.value == newItem.value) {
				node.item.value = newItem.value;
				return;
			}
			node = node.next;
		}

		size = size + 1;
		node = dict[index];

		Individual newEntry = new Individual(newItem, node);
		dict[index] = newEntry;
	}

	/*
	 * pqAdd function adds Q + First character of q to dictionary 
	 * at in array location [size]
	 */
	public void qqAdd(decItem q, char firstCharQ, short readShort) {
		decItem newItem = new decItem(q.value + firstCharQ, readShort);

		int index = getListIndex(newItem.key);
		Individual node = dict[index];

		while (node != null) {
			if (node.item.value == newItem.value) {
				node.item.value = newItem.value;
				return;
			}
			node = node.next;
		}

		size = size + 1;
		node = dict[index];

		Individual newEntry = new Individual(newItem, node);
		dict[index] = newEntry;
	}

	/* 
	 * buildDictionaryDecompress takes in string from the user input files. the
	 * function adds items to dictionary and prints items into text form using
	 * DataOutputStream.
	 * 
	 * @param compressedFile is the new compressed file
	 */
	public void buildDictionaryDecompress(String compressedFile)
			throws IOException {

		DataInputStream in = new DataInputStream(new FileInputStream(
				compressedFile));
		String outputString = "";
		if (compressedFile.endsWith(".zzz")) {
			outputString = compressedFile.substring(0,
					compressedFile.length() - 4);
		}

		File outputFile = new File(outputString);

		DataOutputStream out = new DataOutputStream(new FileOutputStream(outputFile));

		short q = in.readShort();
		short p = in.readShort();
		decItem firstItem = get(q);
		out.writeBytes(firstItem.value);
		while (in.available() > 0) {
			if (contains(p) == true) {
				decItem qItem = get(q);
				decItem pItem = get(p);
				out.writeBytes(pItem.value); 
				
				pqAdd((short) size, qItem, pItem.value.charAt(0));
				q = p;
				p = in.readShort();
			} else {
				decItem item = get(q);
				out.writeBytes((item.value + item.value.charAt(0)));

				qqAdd(item, item.value.charAt(0), p);
				q = p;
				p = in.readShort();
			}
		}
		// last case

		if (contains(p) == true) {
			decItem qItem = get(q);
			decItem pItem = get(p);
			out.writeBytes(pItem.value); 
			pqAdd((short) size, qItem, pItem.value.charAt(0));
		} else {
			decItem item = get(q);
			out.writeBytes((item.value + item.value.charAt(0)));
			qqAdd(item, item.value.charAt(0), p);
		}

		in.close();
		out.flush();
		out.close();
	}

	/*
	 * get takes in short to determine the index. It then searches
	 * the index for the node and places it in dict[hashIndex] 
	 */
	public decItem get(short value) { // NEW METHOD

		decItem result = null;
		int hashIndex = getListIndex(value);

		Individual node = dict[hashIndex];

		while (node != null) {
			if (node.item.key == (value)) {
				result = node.item;
			}
			node = node.next;
		}
		return result;
	}

	/*
	 * getListIndex gets an index depending on short value Uses linear probing to
	 * avoid primary clustering.
	 */
	public int getListIndex(short value) {
		int index;
		if (value == -1) {
			index = (int) value;
		} else {
			index = value;
			index = Math.abs(index);
		}
		index = index % tableCapacity;
		return index;
	}

	/*
	 * contains takes a short and finds its value in the dictionary.
	 */
	public boolean contains(short value) { // NEW METHOD
		// find node equal to prefix and suffix

		int hashIndex = getListIndex(value);

		Individual node = dict[hashIndex];

		while (node != null) {
			if (node.item.key == (value)) {
				return true;
			}
			node = node.next;
		}
		return false;
	}

	public boolean checkPrime(int x) {
		// check if even
		if (x % 2 == 0)
			return false;
		// check factors using squares
		for (int i = 3; i * i <= x; i += 2) {
			if (x % i == 0)
				return false;
		}
		return true;
	}

	/*
	 * findNextCapacity takes the old table capacity to find
	 * a new capacity that is prime that is approximately
	 * twice the size of the old capacity.
	 */
	public int findNextCapacity(int capacity) {
		int nextCapacity = capacity * 2;
		boolean isPrime = checkPrime(nextCapacity);
		while (isPrime == false) {
			nextCapacity += 1;
			isPrime = checkPrime(nextCapacity);
		}
		return nextCapacity;
	}

	public void printDictionary() {
		for (int i = 0; i < tableCapacity; i++) {
			Individual curr = dict[i];

			if (curr != null) {
				System.out.println("");
				System.out.print("index: " + i + " | key: " + curr.item.key
						+ " | value: " + curr.item.value);
				while (curr != null) {
					curr = curr.next;
					if (curr != null) {
						System.out.print(" --> index: " + i + " | key: "
								+ curr.item.key + " | value: "
								+ curr.item.value);
					}
				}
			}
		}
		System.out.println("");
	}

	/*
	 * printCompressLog prints the log file for compression
	 * @param time the time taken to complete compression
	 * @param logFile the newly written file
	 * @param inputFile the file entered by user
	 * @param compressedFile the file compressed by program
	 * @throws IOException
	 */
	public void printDecompressLog(double time, File logFile,
			File compressedFile, int rehashCount) throws IOException {
		PrintWriter writer = new PrintWriter(logFile);
		writer.println("Decompression for file " + compressedFile);
		writer.println("Decompression took "
				+ ((double) Math.round(time * 1000.0) / 1000.0) + " seconds.");
		writer.println("The table was rehashed " + rehashCount + " times.");
		writer.close();
	}

}
