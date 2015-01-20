package nl.gridshore.dwes;

/**
 * Request object to create a new Repository
 */
public class CreateRepositoryrequest {
    private String name;
    private String location;
    private String type;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
