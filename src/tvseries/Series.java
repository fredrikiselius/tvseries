package tvseries;

public class Series {
    private int tvdbID;
    private String name;

    public int getTvdbID() {
	return tvdbID;
    }

    public String getName() {
	return name;
    }

    public Series(String name) {
	this.name = name;

    }

}
