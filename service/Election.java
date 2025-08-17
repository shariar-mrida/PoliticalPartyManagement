package service;

import committees.CentralCommittee;
import committees.Committee;
import committees.DistrictCommittee;
import committees.DivisionalCommittee;
import java.util.*;
import model.CommitteeLevel;
import model.Member;
import model.Role;

public class Election {
    private Committee committee;
    private Map<Role, List<Member>> candidates;
    private ArrayList<Member> winners;
    private Map<Role, Map<Member, Integer>> votes;
    private Map<Member, Set<Role>> voterRoleVotes;
    private boolean declared;

    public Election(Committee committee) {
        this.committee=committee;
        this.candidates = new EnumMap<>(Role.class);
        this.winners = new ArrayList<>();
        this.votes = new EnumMap<>(Role.class);
        this.voterRoleVotes = new HashMap<>();
        this.declared = false;
    }

    public boolean registerCandidate(Role role, Member candidate) {
        if (!declared) {
            return false;
        }
        if (role == Role.MEMBER || role == Role.ADMIN) {
            return false;
        }
        if (!candidates.containsKey(role)) {
            candidates.put(role, new ArrayList<>());
        }
        if (candidates.get(role).contains(candidate)) {
            return false;
        }
        candidates.get(role).add(candidate);
        return true;
    }

    public boolean declareElection(Member currentUser) {
        CommitteeLevel level = null;
        if (committee instanceof CentralCommittee) {
            level = CommitteeLevel.CENTRAL;
        } else if (committee instanceof DivisionalCommittee) {
            level = CommitteeLevel.DIVISIONAL;
        } else if (committee instanceof DistrictCommittee) {
            level = CommitteeLevel.DISTRICT;
        }
        if (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.PRESIDENT) {
            return false;
        }
        if (level == CommitteeLevel.CENTRAL && currentUser.getCommitteeLevel() != CommitteeLevel.CENTRAL) {
            return false;
        }
        if (level == CommitteeLevel.DIVISIONAL && currentUser.getCommitteeLevel() != CommitteeLevel.CENTRAL) {
            return false;
        }
        if (level == CommitteeLevel.DISTRICT && (currentUser.getCommitteeLevel() != CommitteeLevel.DIVISIONAL && currentUser.getCommitteeLevel() != CommitteeLevel.CENTRAL)) {
            return false;
        }
        declared = true;
        return true;
    }

    public boolean vote(Member voter, Role role, Member candidate) {
        if (!declared) {
            return false;
        }
        if (role == Role.MEMBER) {
            return false;
        }
        List<Member> list = candidates.get(role);
        if (list == null || !list.contains(candidate)) {
            return false;
        }
        // making sure not voting multiple times
        if (!voterRoleVotes.containsKey(voter)) {
            voterRoleVotes.put(voter, new HashSet<>());
        }
        if (voterRoleVotes.get(voter).contains(role)) {
            return false;
        }
        if (!votes.containsKey(role)) {
            votes.put(role, new HashMap<>());
        }
        votes.get(role).merge(candidate, 1, Integer::sum);
        voterRoleVotes.get(voter).add(role);
        return true;
    }

    public void countAndSetWinners() {
        if (!declared) {
            throw new IllegalStateException("Election not active for counting!");
        }
        for (Map.Entry<Role, List<Member>> entry : candidates.entrySet()) {
            Role role = entry.getKey();
            List<Member> cands = entry.getValue();
            if (cands.isEmpty()) {
                continue;
            }
            Map<Member, Integer> roleVotes = votes.get(role);
            Member best = null;
            int bestVotes = -1;
            for (Member m : cands) {
                int v = roleVotes.getOrDefault(m, 0);
                if (v > bestVotes) {
                    best = m;
                    bestVotes = v;
                } else if (v == bestVotes && best != null) {
                    //if tie do nothing, get choosen by the order
                    //or here we can put other logic
                }
            }
            if (best != null) {
                CommitteeLevel level = null;
                if (committee instanceof CentralCommittee) {
                    level = CommitteeLevel.CENTRAL;
                } else if (committee instanceof DivisionalCommittee) {
                    level = CommitteeLevel.DIVISIONAL;
                } else if (committee instanceof DistrictCommittee) {
                    level = CommitteeLevel.DISTRICT;
                }
                best.setCommitteeLevel(level);
                best.setRole(role);
                winners.add(best);
            }
        }
    }
    public ArrayList<Member> getWinners(){
        return winners;
    }
    public boolean closeElection(Member currentUser){
        CommitteeLevel level = null;
        if (committee instanceof CentralCommittee) {
            level = CommitteeLevel.CENTRAL;
        } else if (committee instanceof DivisionalCommittee) {
            level = CommitteeLevel.DIVISIONAL;
        } else if (committee instanceof DistrictCommittee) {
            level = CommitteeLevel.DISTRICT;
        }
        if (!declared) {
            throw new IllegalStateException("Election not declared or Already closed");
        }
        if (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.PRESIDENT) {
            return false;
        }
        if (level == null) {
            return false;
        }
        if (level == CommitteeLevel.CENTRAL && currentUser.getCommitteeLevel() != CommitteeLevel.CENTRAL) {
            return false;
        }
        if (level == CommitteeLevel.DIVISIONAL && currentUser.getCommitteeLevel() != CommitteeLevel.CENTRAL) {
            return false;
        }
        if (level == CommitteeLevel.DISTRICT && (currentUser.getCommitteeLevel() != CommitteeLevel.DIVISIONAL && currentUser.getCommitteeLevel() != CommitteeLevel.CENTRAL)) {
            return false;
        }
        winners.clear();
        countAndSetWinners();
        if (committee instanceof CentralCommittee) {
            for(Member leader: winners){
           ((CentralCommittee)committee).addLeader(leader);
            }
        } else if (committee instanceof DivisionalCommittee) {
            for(Member leader: winners){
           ((DivisionalCommittee)committee).addLeader(leader);
            }
        } else if (committee instanceof DistrictCommittee) {
            for(Member leader: winners){
           ((DistrictCommittee)committee).addLeader(leader);
            }
        }
        declared = false;
        return true;

    }

    public Map<Role, List<Member>> getAllCandidates() {
        return candidates;
    }

    public boolean isDeclared() {
        return declared;
    }

}
