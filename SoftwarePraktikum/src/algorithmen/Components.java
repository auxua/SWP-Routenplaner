package algorithmen;

import graphenbib.HierarchyMapGraph;
import graphenbib.HierarchyMapNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Meine schnelle Hilfsklasse um Komponeten zu berechnen.
 * Diese klasse ist naiv und bei Fehlern v"ollig hilflos
 *
 */
public class Components
{
    private HierarchyMapGraph graph;
    private int component;
    private byte level;

    private HashMap<Integer,Integer> marks; //Mappe: UID -> Komponente

    public Components(HierarchyMapGraph graph_)
    {
        graph=graph_;
        //n=graph.getSize();
        marks=new HashMap<Integer,Integer>();
        //computeComponents();
    }
    
    private class CrapIterator implements Iterator<HierarchyMapNode> {

    	Iterator<HierarchyMapNode> forward;
    	Iterator<HierarchyMapNode> backward;
    	
    	
    	public CrapIterator(HierarchyMapNode v, byte level) {
    		forward = v.getNeighbours(level).iterator();
    		backward = v.getPredecessors(level).iterator();
    	}
    	
		@Override
		public boolean hasNext() {
			return (forward.hasNext() || backward.hasNext());
		}

		@Override
		public HierarchyMapNode next() {
			if (forward.hasNext())
				return forward.next();
			if (backward.hasNext())
				return backward.next();
			
			return null;
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			
		}
    	
    }

    public void computeComponents(byte level)
    {
        // resete alles
        reset();
        this.level = level;

        component=0;

        Iterator<HierarchyMapNode> it = graph.getNodeIt();
        while(it.hasNext()) {
            HierarchyMapNode v = it.next();
            
        	if ((v.getLevel() >= level) && (notVisited(v)))
            {
                component=component+1;    // neue Kompenten
                depthFirstSearch(v);
            }
        }
    }
    
    

    private void depthFirstSearch(HierarchyMapNode v)
    {
    	marks.put(v.getUID(), component); //markiere v mit c
        
        //hall nachbarn!
        HierarchyMapNode w;
        //Iterator<Integer> it=new NeighbourIterator(graph, v);
        //Iterator<HierarchyMapNode> it = v.getNeighbours((byte) level).iterator();
        Iterator<HierarchyMapNode> it = new CrapIterator(v,level);
        while (it.hasNext())
        {
            w=it.next();
            if (notVisited(w))
                depthFirstSearch(w);
        }
    }



    private boolean notVisited(HierarchyMapNode v)
    {
    	return marks.get(v.getUID())==null;
    }


    private void reset()
    {
        marks.clear();
    }

    /**
     * Anzahl der Komonenten
     */
    public int getNumberOfComponents()
    {
        return component;
    }

    public ArrayList<Integer> getComponent(int i)
    {
        ArrayList<Integer> ausgabe=new ArrayList<Integer>();
        Iterator<Integer> nodesIT = marks.keySet().iterator();
        while(nodesIT.hasNext()) {
        	int  node = nodesIT.next();
        	if (marks.get(node) == i) {
        		ausgabe.add(node);
        	}
        }
        return ausgabe;
    }

} 