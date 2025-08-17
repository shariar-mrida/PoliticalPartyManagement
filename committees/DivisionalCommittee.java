package committees;
import java.util.ArrayList;
import model.CommitteeLevel;
import model.Member;
import model.Role;
import service.Election;

public class DivisionalCommittee extends Committee implements CommitteeOperations {
    private ArrayList<DistrictCommittee> district;
    private ArrayList<Member> divisionalCommitteeLeader;
    private final CommitteeLevel level = CommitteeLevel.DIVISIONAL;
    private Election election;

    public DivisionalCommittee(String name) {
        super(name);
        this.district = new ArrayList<>();
        this.divisionalCommitteeLeader = new ArrayList<>();
        this.election = new Election(this);
    }

    public  CommitteeLevel getCommitteeLevel(){
        return level;
    }
    
    public void addDistrict(DistrictCommittee d) {
        district.add(d);
    }

    public ArrayList<DistrictCommittee> getDistricts() {
        return district;
    }

    @Override
    public void addLeader(Member m) {
        divisionalCommitteeLeader.add(m);
    }

    @Override
    public boolean removeLeader(Member m) {
        return divisionalCommitteeLeader.remove(m);
    }

    @Override
    public ArrayList<Member> getLeaders() {
        return divisionalCommitteeLeader;
    }

    @Override
    public void assignRole(Member m, Role role) {
        if (divisionalCommitteeLeader.contains(m)) {
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

    @Override
    public void displayInfo() {
        divisionalCommitteeLeader.sort(null);
        System.out.println("Divisional Committee: " + getCommitteeName());
        if (divisionalCommitteeLeader.isEmpty()) {
            System.out.println("No leaders in this divisional committee.");
        } else {
            divisionalCommitteeLeader.sort(null);
            for (Member member : divisionalCommitteeLeader) {
                System.out.println(member.ProfessionalInfo());
            }
        }
    }
}
