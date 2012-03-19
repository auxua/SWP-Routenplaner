package graphenbib;

import graphexceptions.NodeNotNeighbourOfPreviousElementInPathException;
import graphexceptions.PathNotFullyInitialized;

import java.util.ArrayList;

/**
 * Diese Klasse dekoriert die Pathklasse, und erlaubt das Hinzufuegen und hinten Anhaengen von
 * weiteren UIDs, die nicht als Knoten im HierarchyMapGraphen vorkommen. 
 *
 */
public class PathExtendedDec extends Path
{
	private Path origPath;
	private int additionalPathLength = 0;
	private long additionalPathTime = 0;
	private ArrayList<Integer> additionalMapNodesAtEnd = new ArrayList<Integer>();
	private ArrayList<Integer> additionalMapNodesAtStart = new ArrayList<Integer>();
	
	public PathExtendedDec(Path path)
	{
		if (path == null) {
			this.origPath = new Path();
		} else {
			this.origPath = path;
		}
	}
	
		
	public void appendPath(Path toAppend)
	{//appendPath
		try {
			ArrayList<Integer> temp = toAppend.getPathNodeIDs();
			for (int i = 0; i < temp.size(); i++) {
				this.additionalMapNodesAtEnd.add(i,temp.get(i));
			}
			this.additionalPathLength+=toAppend.getPathLength();
			this.additionalPathTime+=toAppend.getPathTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}//appendPath
	
	public void prependPath(Path toPrepend)
	{//appendPath
		try {
			ArrayList<Integer> temp = toPrepend.getPathNodeIDs();
			for (int i = 0; i < temp.size(); i++) {
				this.additionalMapNodesAtStart.add(i,temp.get(i));
				
			}
			this.additionalPathLength+=toPrepend.getPathLength();
			this.additionalPathTime+=toPrepend.getPathTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}//appendPath
	
	//====================================================================================================
	//Ab hier alles dekorierte
	//====================================================================================================
	
	public void appendNode(HierarchyMapNode node)
	{
		this.origPath.appendNode(node);
	}
	
	public long getPathWeight(HierarchyMapGraph hGraph) throws PathNotFullyInitialized, NodeNotNeighbourOfPreviousElementInPathException
	{
		if(main.Config.fastestPathMode) {
			return this.getPathTime();
		} else {
			return this.getPathLength();
		}
	}
	
	public int getPathLength() throws PathNotFullyInitialized
	{
		return this.origPath.getPathLength()+ this.additionalPathLength;
	}
	
	public ArrayList<Integer> getPathNodeIDs() throws PathNotFullyInitialized
    {
    	ArrayList<Integer> result=new ArrayList<Integer>();
    	int pos=0;
    	//Fuege Weg zur Startkreuzung ein
    	for (int i = 0; i < additionalMapNodesAtStart.size(); i++) {
			result.add(pos, additionalMapNodesAtStart.get(i));
			pos++;
		}
    	ArrayList<Integer> originalPathIDs=this.origPath.getPathNodeIDs();
    	//Um zu Verhindern, dass die Startkreuzung doppelt ausgegeben werden, loesche
    	//sie gegebenenfalls einmal
    	if(result.size()>0 && originalPathIDs.size()>0) {
    		if(result.get(result.size()-1)==originalPathIDs.get(0)) {
    			result.remove(result.size()-1);
    			pos--;
    		}
    	}
    	
    	for (int i = 0; i < originalPathIDs.size(); i++) {
			result.add(pos, originalPathIDs.get(i));
			pos++;
		}
    	//Um zu Verhindern, dass die Endkreuzung doppelt ausgegeben werden, loesche
    	//sie gegebenenfalls einmal
    	if( additionalMapNodesAtEnd.size()>0 && originalPathIDs.size()>0) {
    		if(result.get(result.size()-1)==additionalMapNodesAtEnd.get(0)) {
    			result.remove(result.size()-1);
    			pos--;
    		}
    	}
    	//Fuege nun den Weg von der Endkreuzung zum Ziel ein
    	for (int i = 0; i < additionalMapNodesAtEnd.size(); i++) {
			result.add(pos, additionalMapNodesAtEnd.get(i));
			pos++;
		}
		return result;
    }
	
	public int size()
	{
		return this.origPath.size()+additionalMapNodesAtStart.size()+additionalMapNodesAtEnd.size();
	}
	
	public long getPathTime() throws PathNotFullyInitialized
	{
		return origPath.getPathTime()+this.additionalPathTime;
	}
	
	/**
	 * In dieser Funktion verbirgt sich der eigentliche Unterschied zum Path. Es werden die Knoten, die
	 * vorne und hinten angefuegt wurden, zum Result hinzugefuegt.
	 */
	public void reconstructPath(HierarchyMapGraph hGraph) throws NodeNotNeighbourOfPreviousElementInPathException
	{
		this.origPath.reconstructPath(hGraph);
	}
	
	public void appendNode(int uid) {
		this.origPath.appendNode(uid);
	}


	public void setPathAsReconstructed() {
		this.origPath.setPathAsReconstructed();
	}


	public int getStartNodeID() throws PathNotFullyInitialized {
		if(this.origPath.size()==0) {
			throw new PathNotFullyInitialized("Feher in Path.getStartNode: Der Pfad ist bisher leer");
		} else if (this.additionalMapNodesAtStart.size()>0){
			return this.additionalMapNodesAtStart.get(0);
		} else {
			return this.origPath.getStartNodeID();
		}
	}


	public int getEndNodeID() throws PathNotFullyInitialized {
		if(this.origPath.size()==0) {
			throw new PathNotFullyInitialized("Feher in Path.getStartNode: Der Pfad ist bisher leer");
		} else if (this.additionalMapNodesAtEnd.size()>0){
			return this.additionalMapNodesAtEnd.get(additionalMapNodesAtEnd.size()-1);
		} else {
			return this.origPath.getEndNodeID();
		}
	}


	public void setLength(int length)
	{
		this.origPath.setLength(length);
	}
	
	
	public void setTime(long time)
	{
		this.origPath.setTime(time);
	}
	
	public String toString()
    {
		String result = "";
		for (int i = 0; i < additionalMapNodesAtStart.size(); i++) {
			result+=" -->"+additionalMapNodesAtStart.get(i);
		}
		result+=" --> (Startkreuzung)";
		result+=origPath.toString();
		result+=" (EndKreuzung) ";
		for (int i = 0; i < additionalMapNodesAtEnd.size(); i++) {
			result+=" -->"+additionalMapNodesAtEnd.get(i);
		}
    	return result;
    }
}
