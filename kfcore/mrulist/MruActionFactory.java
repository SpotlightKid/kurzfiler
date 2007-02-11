/**
 * 
 */
package kfcore.mrulist;

import javax.swing.Action;

public interface MruActionFactory {
	Action CreateMRUListAction(MRUList list, int i);
}