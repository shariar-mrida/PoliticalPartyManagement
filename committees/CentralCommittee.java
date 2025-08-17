package committees;

import java.util.*;
import model.CommitteeLevel;
import model.Member;
import model.Role;
import service.Election;

public class CentralCommittee extends Committee implements CommitteeOperations {

    private ArrayList<DivisionalCommittee> divisional;
    private ArrayList<Member> centralCommitteeLeader;
    private final CommitteeLevel level = CommitteeLevel.CENTRAL;
    private Election election;
    private double donations;

    public CentralCommittee(String name) {
        super(name);
        this.divisional = new ArrayList<>();
        this.centralCommitteeLeader = new ArrayList<>();
        this.election = new Election(this);
        this.donations = 0.0;
    }

    public void addDivisional(DivisionalCommittee d) {
        divisional.add(d);
    }

    public ArrayList<DivisionalCommittee> getDivisionals() {
        return divisional;
    }

    public  CommitteeLevel getCommitteeLevel(){
        return level;
    }
    @Override
    public void addLeader(Member m) {
        centralCommitteeLeader.add(m);
    }
    public void addLeader(Member m, Role role, CommitteeLevel level){
        m.setRole(role);
        m.setCommitteeLevel(level);
        centralCommitteeLeader.add(m);
    }
    @Override
    public boolean removeLeader(Member m) {
        return centralCommitteeLeader.remove(m);
    }

    @Override
    public ArrayList<Member> getLeaders() {
        return centralCommitteeLeader;
    }

    @Override
    public void assignRole(Member m, Role role) {
        if (centralCommitteeLeader.contains(m)) {
            m.setRole(role);
        }
    }

    @Override
    public Election getElection() {
        return election;
    }

    public void startNewElection() {
        this.election = new Election(this);
    }

    public void addDonation(double amount) {
        if (amount > 0) {
            donations += amount;
        }
    }

    public double getTotalDonations() {
        return donations;
    }

    @Override
    public void displayInfo() {
        centralCommitteeLeader.sort(null);
        System.out.println("Central Committee: " + getCommitteeName());
        System.out.println("Total Donations: " + donations);
        if (centralCommitteeLeader.isEmpty()) {
            System.out.println("No leaders in the central committee.");
        } else {
            for (Member member : centralCommitteeLeader) {
                System.out.println(member.ProfessionalInfo());
            }
        }
    }
}
