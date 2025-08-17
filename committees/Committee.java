package committees;

import service.Election;

public abstract class Committee {
    private final String name;

    public Committee(String name) {
        this.name = name;
    }

    public String getCommitteeName() {
        return name;
    }

    // public void setCommitteeName(String name) {
    //     this.name = name;
    // }

    @Override
    public String toString() {
        return "Committee Name: " + name;
    }
    public abstract void displayInfo();

    public abstract Election getElection();
}
