package committees;
import java.util.ArrayList;
import model.Member;
import model.Role;
public interface CommitteeOperations {
    void addLeader(Member m);
    boolean removeLeader(Member m);
    ArrayList<Member> getLeaders();
    void displayInfo();
    void assignRole(Member m, Role role);
}
