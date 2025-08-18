package service;

import committees.*;
import java.io.*;
import java.util.*;
import model.*;


public class PartySystem{
    
    private final CentralCommittee centralCommittee;
    private final Map<Division, DivisionalCommittee> divisionCommittees = new EnumMap<>(Division.class);
    private final Map<District, DistrictCommittee> districtCommittees = new EnumMap<>(District.class);
    private final List<Member> pendingApplications = new ArrayList<>();
    private final Member adminUser;

    private static final String MEMBERS_FILE = "data\\data_members.csv";
    private static final String PENDING_FILE = "data\\data_pending.csv";
    private static final String DONATIONS_FILE = "data\\data_donations.txt";

    public PartySystem(){
        centralCommittee = new CentralCommittee("National Central Committee");
        buildCommitteeStructure();
            try {
                loadFromFiles();
            } catch (IOException e) {
                System.out.println("Failed to load saved data: " + e.getMessage());
            }
        
    adminUser = new Member("ADMIN", "System Administrator", "admin@party.org", "000", "admin123", "Administrator", 0, 0, false, true,
        new Address(District.Dhaka), Role.ADMIN, CommitteeLevel.CENTRAL);
    centralCommittee.addLeader(adminUser);
    }


    private void buildCommitteeStructure() {
        // Group districts by division
        Map<Division, List<District>> districtsByDivision = new EnumMap<>(Division.class);
        for (District district : District.values()) {
            Division division = district.getDivision();
            if (!districtsByDivision.containsKey(division)) {
                districtsByDivision.put(division, new ArrayList<>());
            }
            districtsByDivision.get(division).add(district);
        }
        // Create committees
        for (Division division : districtsByDivision.keySet()) {
            DivisionalCommittee divCommittee = new DivisionalCommittee(division.name() + " Divisional Committee");
            divisionCommittees.put(division, divCommittee);
            centralCommittee.addDivisional(divCommittee);
            for (District district : districtsByDivision.get(division)) {
                DistrictCommittee distCommittee = new DistrictCommittee(district.name() + " District Committee");
                divCommittee.addDistrict(distCommittee);
                districtCommittees.put(district, distCommittee);
            }
        }
    }

    public CentralCommittee getCentralCommittee() {
        return centralCommittee;
    }
    public DivisionalCommittee getDivisionalCommittee(Division division) {
        return divisionCommittees.get(division);
    }
    public DistrictCommittee getDistrictCommittee(District district) {
        return districtCommittees.get(district);
    }

   
    public Member login(String email, String password) {
        if (email == null) return null;
        Member member = findByEmail(email);
        if (member != null && member.getPassword().equals(password)) {
            return member;
        }
        return null;
    }

    public boolean isAdmin(Member member) {
        return member != null && member.getRole() == Role.ADMIN;
    }


    public Member applyForMembership(Member newMember) {
        if (newMember.getEmail() == null) return null;
        if (findById(newMember.getNationalId()) != null) return null; // duplicate ID
        if (findByEmail(newMember.getEmail()) != null) return null; // duplicate email
        pendingApplications.add(newMember);
        return newMember;
    }

    public List<Member> getAllPendingApplications() {
        return new ArrayList<>(pendingApplications);
    }

    public boolean approveApplication(String nationalId) {
        Iterator<Member> it = pendingApplications.iterator();
        while (it.hasNext()) {
            Member member = it.next();
            if (member.getNationalId().equals(nationalId)) {
                member.setApproved(true);
                it.remove();
                getDistrictCommittee(member.getAddress().getDistrict()).addMember(member);
                return true;
            }
        }
        return false;
    }
    public boolean rejectApplication(String nationalId) {
        Iterator<Member> it = pendingApplications.iterator();
        while (it.hasNext()) {
            Member member = it.next();
            if (member.getNationalId().equals(nationalId)) {
                it.remove();
                return true;
            }
        }
        return false;
    }
    public boolean terminateMembershipID(String nationalId) {
        Member member = findById(nationalId);
        if (member == null || member.getRole() == Role.ADMIN) return false;
        switch (member.getCommitteeLevel()) {
            case CENTRAL -> centralCommittee.getLeaders().remove(member);
            case DIVISIONAL -> getDivisionalCommittee(member.getAddress().getDivision()).getLeaders().remove(member);
            case DISTRICT -> {
                DistrictCommittee distCommittee = getDistrictCommittee(member.getAddress().getDistrict());
                distCommittee.getLeaders().remove(member);
                distCommittee.getMembers().remove(member);
            }
        }
        return true;
    }
    public boolean terminateMembershipEmail(String email) {
        Member member = findByEmail(email);
        if (member == null || member.getRole() == Role.ADMIN) return false;
        switch (member.getCommitteeLevel()) {
            case CENTRAL -> centralCommittee.getLeaders().remove(member);
            case DIVISIONAL -> getDivisionalCommittee(member.getAddress().getDivision()).getLeaders().remove(member);
            case DISTRICT -> {
                DistrictCommittee distCommittee = getDistrictCommittee(member.getAddress().getDistrict());
                distCommittee.getLeaders().remove(member);
                distCommittee.getMembers().remove(member);
            }
        }
        return true;
    }

    public boolean promoteToLeader(String nationalId, CommitteeLevel level, Role newRole, Division division, District district) {
        if (newRole == Role.MEMBER || newRole == Role.ADMIN) return false;
    Member member = findById(nationalId);
        if (member == null || !member.isApproved()) return false;
        if (district != null && member.getAddress().getDistrict() != district) {
            member.setAddress(new Address(district));
        }
        member.setCommitteeLevel(level);
        member.setRole(newRole);
        switch (level) {
            case CENTRAL -> centralCommittee.addLeader(member);
            case DIVISIONAL -> {
                if (division == null) division = member.getAddress().getDivision();
                getDivisionalCommittee(division).addLeader(member);
            }
            case DISTRICT -> {
                if (district == null) district = member.getAddress().getDistrict();
                getDistrictCommittee(district).addLeader(member);
            }
        }
        return true;
    }

    public boolean demoteLeader(String email) {
    Member member = findByEmail(email);
        if (member == null || member.getRole() == Role.ADMIN) return false;
        switch (member.getCommitteeLevel()) {
            case CENTRAL -> centralCommittee.removeLeader(member);
            case DIVISIONAL -> getDivisionalCommittee(member.getAddress().getDivision()).removeLeader(member);
            case DISTRICT -> getDistrictCommittee(member.getAddress().getDistrict()).removeLeader(member);
        }
        member.setRole(Role.MEMBER);
        member.setCommitteeLevel(CommitteeLevel.DISTRICT);
        return true;
    }


    public boolean declareElection(Committee committee,Member currentUser) {
        if (committee instanceof CentralCommittee){
            for(Member leader: centralCommittee.getLeaders()){
                demoteLeader(leader.getEmail());
            }
            centralCommittee.getLeaders().clear();
            return centralCommittee.getElection().declareElection(currentUser);
        }else if (committee instanceof DivisionalCommittee) {
            DivisionalCommittee divCommittee = (DivisionalCommittee) committee;
            for(Member leader: divCommittee.getLeaders()){
                demoteLeader(leader.getEmail());
            }
            divCommittee.getLeaders().clear();
            return divCommittee.getElection().declareElection(currentUser);
        } else if (committee instanceof DistrictCommittee) {
            DistrictCommittee distCommittee = (DistrictCommittee) committee;
            for(Member leader: distCommittee.getLeaders()){
                demoteLeader(leader.getEmail());
            }
            distCommittee.getLeaders().clear();
            return distCommittee.getElection().declareElection(currentUser);
        }
        return false;
    }
    public boolean closeElection(Committee committee,Member currentUser) {
        if (committee instanceof CentralCommittee){ 
            return centralCommittee.getElection().closeElection(currentUser);
        }else if (committee instanceof DivisionalCommittee) {
            DivisionalCommittee divCommittee = (DivisionalCommittee) committee;
            return divCommittee.getElection().closeElection(currentUser);
        } else if (committee instanceof DistrictCommittee) {
            DistrictCommittee distCommittee = (DistrictCommittee) committee;
            return distCommittee.getElection().closeElection(currentUser);
        }
        return false;
        
    }

    public boolean vote(Committee committee, Role role, Member candidate, Member voter) {
        if (committee instanceof CentralCommittee) {
            return ((CentralCommittee) committee).getElection().vote(voter, role, candidate);
        } else if (committee instanceof DivisionalCommittee) {
            return ((DivisionalCommittee) committee).getElection().vote(voter, role, candidate);
        } else if (committee instanceof DistrictCommittee) {
            return ((DistrictCommittee) committee).getElection().vote(voter, role, candidate);
        }
        return false;
    }
    
    public boolean applyForLeadership(Member member, Role desiredRole, CommitteeLevel level) {
        if (desiredRole == Role.MEMBER || !member.isApproved()) return false;
        switch (level) {
            case CENTRAL:
                return getCentralCommittee().getElection().registerCandidate(desiredRole, member);
            case DIVISIONAL: {
                Division division = member.getAddress().getDivision();
                return getDivisionalCommittee(division).getElection().registerCandidate(desiredRole, member);
            }
            case DISTRICT: {
                District district = member.getAddress().getDistrict();
                return getDistrictCommittee(district).getElection().registerCandidate(desiredRole, member);
            }
            default:
                return false;
        }
    }

    public Member findByEmail(String email) {
        if (email == null) return null;
            // Search all committees for a member with this email
            String emailLower = email.toLowerCase();
            // Central leaders
            for (Member m : centralCommittee.getLeaders()) {
                if (m.getEmail() != null && m.getEmail().toLowerCase().equals(emailLower)) return m;
            }
            // Divisional leaders
            for (DivisionalCommittee div : divisionCommittees.values()) {
                for (Member m : div.getLeaders()) {
                    if (m.getEmail() != null && m.getEmail().toLowerCase().equals(emailLower)) return m;
                }
            }
            // District leaders and members
            for (DistrictCommittee dist : districtCommittees.values()) {
                for (Member m : dist.getLeaders()) {
                    if (m.getEmail() != null && m.getEmail().toLowerCase().equals(emailLower)) return m;
                }
                for (Member m : dist.getMembers()) {
                    if (m.getEmail() != null && m.getEmail().toLowerCase().equals(emailLower)) return m;
                }
            }
            return null;
    }

    public Member findById(String nationalId) {
        // Central leaders
        for (Member m : centralCommittee.getLeaders()) {
            if (m.getNationalId().equals(nationalId)) return m;
        }
        // Divisional leaders
        for (DivisionalCommittee div : divisionCommittees.values()) {
            for (Member m : div.getLeaders()) {
                if (m.getNationalId().equals(nationalId)) return m;
            }
        }
        // District leaders and members
        for (DistrictCommittee dist : districtCommittees.values()) {
            for (Member m : dist.getLeaders()) {
                if (m.getNationalId().equals(nationalId)) return m;
            }
            for (Member m : dist.getMembers()) {
                if (m.getNationalId().equals(nationalId)) return m;
            }
        }
        return null;
    }

    public double getDonations(){
        return centralCommittee.getTotalDonations();
    }

    public void saveToFiles() {
        try {
            File membersFile = new File(MEMBERS_FILE);
            File pendingFile = new File(PENDING_FILE);
            File donationsFile = new File(DONATIONS_FILE);
            File parent = membersFile.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }

            List<Member> approvedMembers = new ArrayList<>();
            // Central leaders
            for (Member m : centralCommittee.getLeaders()) {
                if (m.getRole() != Role.ADMIN && m.isApproved()) approvedMembers.add(m);
            }
            // Divisional leaders
            for (DivisionalCommittee div : divisionCommittees.values()) {
                for (Member m : div.getLeaders()) {
                    if (m.getRole() != Role.ADMIN && m.isApproved()) approvedMembers.add(m);
                }
            }
            // District leaders and members
            for (DistrictCommittee dist : districtCommittees.values()) {
                for (Member m : dist.getLeaders()) {
                    if (m.getRole() != Role.ADMIN && m.isApproved()) approvedMembers.add(m);
                }
                for (Member m : dist.getMembers()) {
                    if (m.getRole() != Role.ADMIN && m.isApproved() && m.getRole()==Role.MEMBER) approvedMembers.add(m);
                }
            }
            approvedMembers.sort(null);
            try (PrintWriter out = new PrintWriter(membersFile)) {
                for (Member member : approvedMembers) {
                    out.print(member.getNationalId()); out.print(",");
                    out.print(member.getName()); out.print(",");
                    out.print(member.getEmail()); out.print(",");
                    out.print(member.getPhone()); out.print(",");
                    out.print(member.getPassword()); out.print(",");
                    out.print(member.getProfession()); out.print(",");
                    out.print(member.getYearlyIncome()); out.print(",");
                    out.print(member.getDonation()); out.print(",");
                    out.print(member.hasDonated()); out.print(",");
                    out.print(member.isApproved()); out.print(",");
                    out.print(member.getAddress().getDistrict().name()); out.print(",");
                    out.print(member.getAddress().getDivision().name()); out.print(",");
                    out.print(member.getRole().name()); out.print(",");
                    out.print(member.getCommitteeLevel().name()); out.println();
                }
            }

            // Sort pending applications by name
            List<Member> pendingSorted = new ArrayList<>(pendingApplications);
            pendingSorted.sort(null);
            try (PrintWriter out = new PrintWriter(pendingFile)) {
                for (Member member : pendingSorted) {
                    out.print(member.getNationalId()); out.print(",");
                    out.print(member.getName()); out.print(",");
                    out.print(member.getEmail()); out.print(",");
                    out.print(member.getPhone()); out.print(",");
                    out.print(member.getPassword()); out.print(",");
                    out.print(member.getProfession()); out.print(",");
                    out.print(member.getYearlyIncome()); out.print(",");
                    out.print(member.getDonation()); out.print(",");
                    out.print(member.hasDonated()); out.print(",");
                    out.print(member.isApproved()); out.print(",");
                    out.print(member.getAddress().getDivision().name()); out.print(",");
                    out.print(member.getAddress().getDistrict().name()); out.print(",");
                    out.print(member.getRole().name()); out.print(",");
                    out.print(member.getCommitteeLevel().name()); out.println();
                }
            }
            try(PrintWriter out = new PrintWriter(donationsFile)){
                out.print(centralCommittee.getTotalDonations());
            }
        } catch (IOException e) {
            System.out.println("Save failed: " + e.getMessage());
        }
    }

    public final void loadFromFiles() throws IOException {
        centralCommittee.getLeaders().clear();
        for (DivisionalCommittee div : divisionCommittees.values()) {
            div.getLeaders().clear();
        }
        for (DistrictCommittee dist : districtCommittees.values()) {
            dist.getLeaders().clear();
            dist.getMembers().clear();
        }
        pendingApplications.clear();

        File membersFile = new File(MEMBERS_FILE);
        try (Scanner scanner = new Scanner(membersFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.isBlank()) {
                    continue;
                }
                String[] p = line.split(",", -1);
                if (p.length < 14) {
                    continue;
                }
                try {
                    String nid = p[0];
                    String name = p[1];
                    String email = p[2];
                    String phone = p[3];
                    String password = p[4];
                    String profession = p[5];
                    double yearlyIncome = Double.parseDouble(p[6]);
                    double donation = Double.parseDouble(p[7]);
                    boolean hasDonated = Boolean.parseBoolean(p[8]);
                    boolean approved = Boolean.parseBoolean(p[9]);
                    Division division = Division.valueOf(p[10]);
                    District district = District.valueOf(p[11]);
                    Role role = Role.valueOf(p[12]);
                    CommitteeLevel cl = CommitteeLevel.valueOf(p[13]);
                    Member member = new Member(nid, name, email, phone, password, profession, yearlyIncome, donation, hasDonated, approved, new Address(district), role, cl);
                    if (approved) {
                        switch (cl) {
                            case CENTRAL ->
                                centralCommittee.addLeader(member);
                            case DIVISIONAL ->
                                getDivisionalCommittee(division).addLeader(member);
                            case DISTRICT -> {
                                if (role == Role.MEMBER) {
                                    getDistrictCommittee(district).addMember(member);
                                } else {
                                    getDistrictCommittee(district).addLeader(member);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Harye Genjan!File load e genjan hoise! oida kintu Party System e!");
                }
            }
        }

        File pendingFile = new File(PENDING_FILE);
        try (Scanner scanner = new Scanner(pendingFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.isBlank()) {
                    continue;
                }
                String[] p = line.split(",", -1);
                if (p.length < 14) {
                    continue;
                }
                try {
                    String nid = p[0];
                    String name = p[1];
                    String email = p[2];
                    String phone = p[3];
                    String password = p[4];
                    String profession = p[5];
                    double yearlyIncome = Double.parseDouble(p[6]);
                    double donation = Double.parseDouble(p[7]);
                    boolean hasDonated = Boolean.parseBoolean(p[8]);
                    boolean approved = Boolean.parseBoolean(p[9]);
                    Division division = Division.valueOf(p[10]);
                    District district = District.valueOf(p[11]);
                    Role role = Role.valueOf(p[12]);
                    CommitteeLevel cl = CommitteeLevel.valueOf(p[13]);
                    Member member = new Member(nid, name, email, phone, password, profession, yearlyIncome, donation, hasDonated, approved, new Address(district), role, cl);
                    if (!approved) {
                        pendingApplications.add(member);
                    }
                } catch (Exception e) {
                    System.out.println("Harye Genjan!File load e genjan hoise! oida kintu Party System e!");
                }
            }
        }
        File donatinosFile = new File(DONATIONS_FILE);
        try(Scanner scanner = new Scanner(donatinosFile)){
            double donations = scanner.nextDouble();
            centralCommittee.setTotalDonations(donations);
        }
        catch(Exception e){
            System.out.println("Donations file e loading e genjam hoise!");
        }

    }

}
