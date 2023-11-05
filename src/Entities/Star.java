package Entities;

public class Star {
    private String starId;
    private String name;
    private int birthYear;

    public Star(){

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

    public void setStarId(String starId) {
        this.starId = starId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }
}
