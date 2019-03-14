/**
 * Name: Individual
 * 
 * @author Chris Eardensohn (ceardensohn@sandiego.edu)
 * @author Thaddeus Steele (tsteele@sandiego.edu)
 *
 * Date: 4/16/2018
 * 
 * Description: This file contains code to hold individual item information
 * and a link to the next node in the list. 
 */
public class Individual {
		public decItem item;
		public Individual next;

		public Individual(decItem item, Individual next) {
			this.item = item;
			this.next = next;
		}

	}
