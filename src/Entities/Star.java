package Entities;

public class Star {
    private final String starId;
    private final String name;
    private final int birthYear;

    public Star(String id, String name, int birthYear){
        this.starId = id;
        this.name = name;
        this.birthYear = birthYear;
    }

    public String getStarId() {
        return starId;
    }

    public String getName() {
        return name;
    }

    public int getBirthYear() {
        return birthYear;
    }
}
