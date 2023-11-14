package Entities;

public class Star {
    private String starId;
    private String FID;
    private String name;
    private int birthYear;

    public Star(){

    }

    public String getStarId() {
        return starId;
    }
    public String getFID() {
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
    public void setFID(String FID) { this.FID = FID; }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }
}
