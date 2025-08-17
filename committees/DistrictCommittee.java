package committees;
import java.util.ArrayList;
import model.CommitteeLevel;
import model.Member;
import model.Role;
import service.Election;

public class DistrictCommittee extends Committee implements CommitteeOperations {
    private ArrayList<Member> districtCommitteeleader;
    private ArrayList<Member> member;
    private final CommitteeLevel level = CommitteeLevel.DISTRICT;
    private Election election;

    public DistrictCommittee(String name) {
        super(name);
        this.member = new ArrayList<>();
        this.districtCommitteeleader = new ArrayList<>();
        this.election = new Election(this);
    }
    public  CommitteeLevel getCommitteeLevel(){
        return level;
    }
    public void addMember(Member m) {
        member.add(m);
    }
    public boolean removeMember(Member m) {
        return member.remove(m);
    }
    public ArrayList<Member> getMembers() {
        return member;
    }
    @Override
    public void addLeader(Member m) {
        districtCommitteeleader.add(m);
    }

    @Override
    public boolean removeLeader(Member m) {
        return districtCommitteeleader.remove(m);
    }

    @Override
    public ArrayList<Member> getLeaders() {
        return districtCommitteeleader;
    }

    @Override
    public void assignRole(Member m, Role role) {
        if (districtCommitteeleader.contains(m)) {
            m.setRole(role);
        }
    }

    // Election-related methods
    @Override
    public Election getElection() {
        return election;
    }
    public void startNewElection() {
        this.election = new Election(this);
    }

    @Override
    public void displayInfo() {
        districtCommitteeleader.sort(null);
        member.sort(null);
        System.out.println("Divisional Committee: " + getCommitteeName());
        if (districtCommitteeleader.isEmpty()) {
            System.out.println("No leaders in this divisional committee.");
        } else {
            for (Member m : districtCommitteeleader) {
                System.out.println(m.ProfessionalInfo());
            }
        }
        System.out.println("District Committee: " + getCommitteeName());
        if (member.isEmpty()) {
            System.out.println("No members in this district.");
        }
        else {
            for (Member m : member) {
                if(m.isApproved()){
                System.out.println(m.ProfessionalInfo());
                }
            }
        }
    }
}
