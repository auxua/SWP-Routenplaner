package graphenbib;

public enum StreetType {
	MOTORWAY,TRUNK,PRIMARY,SECONDARY,TERTIARY,RESIDENTIAL,LIVING_STREET,ROAD,UNKNOWN,SHORTESTPATH;
	
	@Override
	public String toString() {
		switch(this) {
			case MOTORWAY:
				return "Motorway";
			case TRUNK:
				return "Trunk";
			case PRIMARY:
				return "Primary";
			case SECONDARY:
				return "Secondary";
			case TERTIARY:
				return "Tertiary";
			case RESIDENTIAL:
				return "Residential";
			case LIVING_STREET:
				return "Living Street";
			case ROAD:
				return "Road";
			case SHORTESTPATH:
				return "Shortest Path";
			default:
				return "Unknown";
		}
	}
}
