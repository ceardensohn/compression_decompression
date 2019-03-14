/**
 * Name: Node
 * 
 * @author Chris Eardensohn (ceardensohn@sandiego.edu)
 * @author Thaddeus Steele (tsteele@sandiego.edu)
 *
 * Date: 4/16/2018
 * 
 * Description: This file contains code to hold item information
 * and a link to the next node in the list. 
 */
public class Node {
		public Item item; //private
		public Node next; //private
		
		//node constructor
		public Node(Item item, Node next) {
			this.item = item;
			this.next = next;
		}
		
		public Item getItem() {
			return this.item;
		}
		public Node getNextNode() {
			return next;
		}
		
	}
