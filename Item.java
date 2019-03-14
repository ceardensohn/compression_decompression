/**
 * Name: Item
 * 
 * @author Chris Eardensohn (ceardensohn@sandiego.edu)
 * @author Thaddeus Steele (tsteele@sandiego.edu)
 *
 * Date: 4/16/2018
 * 
 * Description: This file contains code hold an item to be
 * placed into dictionary.
 */
public class Item {
//	public String key; //decompress value
	public short prefix; //decompress null
	public char suffix; //decompress null
	public short value; //decompress key
	
	public Item(char suffix, short value) {
		this.prefix = -1;
		this.suffix = suffix;
		this.value = value;
	}
	
	public Item(short prefix, char suffix, short value) {
		//this.key = key;
		this.prefix = prefix;
		this.suffix = suffix;
		this.value = value;
	}
	
}