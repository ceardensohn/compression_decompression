/**
 * Name: decItem
 * 
 * @author Chris Eardensohn (ceardensohn@sandiego.edu)
 * @author Thaddeus Steele (tsteele@sandiego.edu)
 *
 * Date: 4/16/2018
 * 
 * Description: This file contains code hold an item to be
 * placed into dictionary.
 */
public class decItem {
//	public String key;
	public char firstLetter;
	public String value;
	public short key;
	
	public decItem(String value, short key) {
		this.firstLetter = value.charAt(0); 
		this.value = value;
		this.key = key;
	}
}