package graphenbib;

import java.util.Arrays;
import java.util.Iterator;

public class MapEdgeIterator implements Iterator<MapEdge> {
	private Iterator<MapNode> nodeIt;
	private Iterator<MapEdge> edgeIt;
	boolean oneIteratorNull;
	
	public MapEdgeIterator(MapGraph mapgraph) {
		this.nodeIt=mapgraph.getNodeIt();
		if(nodeIt.hasNext()) {
			oneIteratorNull=false;
			this.edgeIt=Arrays.asList(nodeIt.next().getOutgoingEdges()).iterator();
		} else {
			edgeIt=null;
			oneIteratorNull=true;
		}
	}

	@Override
	public boolean hasNext() {
		if(oneIteratorNull) {
			return false;
		} else {
			return (nodeIt.hasNext() || edgeIt.hasNext());
		}
	}

	@Override
	public MapEdge next() {
		if(!this.hasNext()) {
			return null;
		} else {
			if(edgeIt.hasNext()) {
				return edgeIt.next();
			} else {
				this.edgeIt=Arrays.asList(nodeIt.next().getOutgoingEdges()).iterator();
				return this.next();
			}
		}
	}

	@Override
	public void remove() {		
	}
	
}
