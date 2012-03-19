/**
 * 
 */
package main;

import java.util.Iterator;


public interface ViewportCallback {
	void updateComplete(Iterator<Street2Draw> streetIt, Iterator<Street2Draw> shortestPathit);
}
