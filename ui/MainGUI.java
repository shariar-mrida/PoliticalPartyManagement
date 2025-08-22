package ui;

import committees.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import model.*;
import service.*;

public class MainGUI {

    private final PartySystem system = new PartySystem();
    private JFrame frame;
    private CardLayout card;
    private JPanel cards;

    private Member currentUser;

    private StartPanel startPanel;
    private ApplyPanel applyPanel;
    private LoginPanel loginPanel;
    private MemberPanel memberPanel;
    private LeaderPanel leaderPanel;
    private AdminPanel adminPanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainGUI().init());
    }

    private void init() {
        ImageIcon image = new ImageIcon(getClass().getResource("/ui/ppm.png"));
        frame = new JFrame("Political Party Management System");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1100, 550);
        frame.setLocationRelativeTo(null);
        frame.setMinimumSize(new Dimension(800, 400));
        frame.setIconImage(image.getImage());

        card = new CardLayout();
        cards = new JPanel(card);

        startPanel = new StartPanel();
        applyPanel = new ApplyPanel();
        loginPanel = new LoginPanel();
        memberPanel = new MemberPanel();
        leaderPanel = new LeaderPanel();
        adminPanel = new AdminPanel();
    
        cards.add(startPanel, "START");
        cards.add(applyPanel, "APPLY");
        cards.add(loginPanel, "LOGIN");
        cards.add(memberPanel, "MEMBER");
        cards.add(leaderPanel, "LEADER");
        cards.add(adminPanel, "ADMIN");

        frame.setContentPane(cards);
        frame.setVisible(true);

        Runtime.getRuntime().addShutdownHook(new Thread(system::saveToFiles));
    }

    private void switchCard(String name) {
        card.show(cards, name);
    }

    // ================= Panels =================
    private class StartPanel extends JPanel {

        StartPanel() {
            setLayout(new GridBagLayout());
            GridBagConstraints gc = new GridBagConstraints();
            gc.insets = new Insets(10, 10, 10, 10);
            gc.gridx = 0;
            gc.gridy = 0;
            add(new JLabel("Political Party Management System"), gc);
            gc.gridy++;
            JButton applyBtn = new JButton("Apply for Membership");
            add(applyBtn, gc);
            gc.gridy++;
            JButton loginBtn = new JButton("Login");
            add(loginBtn, gc);
            gc.gridy++;
            JButton guestDonateBtn = new JButton("Guest Donate");
            add(guestDonateBtn, gc);
            gc.gridy++;
            JButton exitBtn = new JButton("Exit & Save");
            add(exitBtn, gc);

            applyBtn.addActionListener(e -> {
                e.getWhen();
                switchCard("APPLY");
            });
            loginBtn.addActionListener(e -> {
                e.getWhen();
                switchCard("LOGIN");
            });
            guestDonateBtn.addActionListener(e -> {
                e.getWhen();
                guestDonate();
            });
            exitBtn.addActionListener(e -> {
                e.getWhen();
                system.saveToFiles();
                frame.dispose();
            });
        }
    }

    private class ApplyPanel extends JPanel {

        private final JTextField nidF = new JTextField(15);
        private final JTextField nameF = new JTextField(15);
        private final JTextField emailF = new JTextField(15);
        private final JTextField phoneF = new JTextField(15);
        private final JPasswordField passF = new JPasswordField(15);
        private final JPasswordField passFC = new JPasswordField(15);
        private final JTextField profF = new JTextField(15);
        private final JTextField incomeF = new JTextField(15);
        private final JComboBox<Division> divisionBox = new JComboBox<>(Division.values());
        private final JComboBox<District> districtBox = new JComboBox<>();

        ApplyPanel() {
            setLayout(new BorderLayout());
            JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
            form.add(new JLabel("National ID:"));
            form.add(nidF);
            form.add(new JLabel("Name:"));
            form.add(nameF);
            form.add(new JLabel("Email:"));
            form.add(emailF);
            form.add(new JLabel("Phone:"));
            form.add(phoneF);
            form.add(new JLabel("Password:"));
            form.add(passF);
            form.add(new JLabel("Confirm Password:"));
            form.add(passFC);
            form.add(new JLabel("Profession:"));
            form.add(profF);
            form.add(new JLabel("Yearly Income:"));
            form.add(incomeF);
            form.add(new JLabel("Division:"));
            form.add(divisionBox);
            form.add(new JLabel("District:"));
            form.add(districtBox);
            add(new JScrollPane(form), BorderLayout.CENTER);
            JPanel buttons = new JPanel();
            JButton submit = new JButton("Submit Application");
            JButton back = new JButton("Back");
            buttons.add(submit);
            buttons.add(back);
            add(buttons, BorderLayout.SOUTH);

            divisionBox.addActionListener(e -> {
                e.getWhen();
                reloadDistricts();
            });
            reloadDistricts();

            submit.addActionListener(e -> {
                e.getWhen();
                doApply();
            });
            back.addActionListener(e -> {
                e.getWhen();
                switchCard("START");
            });
        }

        private void reloadDistricts() {
            Division div = (Division) divisionBox.getSelectedItem();
            districtBox.removeAllItems();
            if (div != null) {
                for (District d : District.values()) {
                    if (d.getDivision() == div) {
                        districtBox.addItem(d);
                    }
                }
            }
        }

        private void doApply() {
            String nid = nidF.getText().trim();
            String name = nameF.getText().trim();
            String email = emailF.getText().trim();
            String phone = phoneF.getText().trim();
            String pass = new String(passF.getPassword());
            String passc = new String(passFC.getPassword());
            String prof = profF.getText().trim();
            District dist = (District) districtBox.getSelectedItem();
            double income = 0;
            try {
                income = Double.parseDouble(incomeF.getText().trim());
            } catch (NumberFormatException ignored) {
                System.out.println("Number Format Problem");
            }
            if (nid.isEmpty() || name.isEmpty() || email.isEmpty() || pass.isEmpty() || dist == null || income == 0) {
                JOptionPane.showMessageDialog(this, "Fill required fields");
                return;
            }
            if (!passc.equals(pass)) {
                JOptionPane.showMessageDialog(this, "Passwords are not same!");
                return;
            }
            Member newMember = system.applyForMembership(new Member(nid, name, email, phone, pass, prof, income, false, false, new Address(dist), Role.MEMBER, CommitteeLevel.DISTRICT));
            if (newMember == null) {
                JOptionPane.showMessageDialog(this, "Duplicate National ID or Email"); 
            }else {
                JOptionPane.showMessageDialog(this, "Application submitted. Await approval.");
            }
        }
    }

    private class LoginPanel extends JPanel {

        private final JTextField emailF = new JTextField(18);
        private final JPasswordField passF = new JPasswordField(18);

        LoginPanel() {
            setLayout(new GridBagLayout());
            GridBagConstraints gc = new GridBagConstraints();
            gc.insets = new Insets(5, 5, 5, 5);
            gc.gridx = 0;
            gc.gridy = 0;
            add(new JLabel("Email:"), gc);
            gc.gridx = 1;
            add(emailF, gc);
            gc.gridx = 0;
            gc.gridy++;
            add(new JLabel("Password:"), gc);
            gc.gridx = 1;
            add(passF, gc);
            gc.gridy++;
            gc.gridx = 0;
            gc.gridwidth = 2;
            JButton loginBtn = new JButton("Login");
            add(loginBtn, gc);
            gc.gridy++;
            JButton backBtn = new JButton("Back");
            add(backBtn, gc);

            loginBtn.addActionListener(e -> {
                e.getWhen();
                doLogin();
            });
            backBtn.addActionListener(e -> {
                e.getWhen();
                switchCard("START");
            });
        }

        private void doLogin() {
            String email = emailF.getText().trim();
            String pass = new String(passF.getPassword());
            Member m = system.login(email, pass);
            if (m == null) {
                JOptionPane.showMessageDialog(this, "Login failed");
                return;
            }
            if (!m.isApproved()) {
                JOptionPane.showMessageDialog(this, "Not approved yet");
                return;
            }
            currentUser = m;
            switch (m.getRole()) {
                case ADMIN -> {
                    adminPanel.refresh();
                    switchCard("ADMIN");
                }
                case MEMBER -> {
                    memberPanel.refresh();
                    switchCard("MEMBER");
                }
                default -> {
                    leaderPanel.refresh();
                    switchCard("LEADER");
                }
            }
        }
    }

    private abstract class BaseUserPanel extends JPanel {

        protected JTextArea output = new JTextArea();
        protected JLabel header = new JLabel();
        protected JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));

        BaseUserPanel() {
            setLayout(new BorderLayout());
            output.setEditable(false);
            add(header, BorderLayout.NORTH);
            add(new JScrollPane(output), BorderLayout.CENTER);
            add(buttons, BorderLayout.SOUTH);
        }

        protected final JButton btn(String label, Runnable action) {
            JButton b = new JButton(label);
            b.addActionListener(e -> {
                e.getWhen();
                action.run();
            });
            buttons.add(b);
            return b;
        }

        @SuppressWarnings("unused")
        abstract void refresh();

        @SuppressWarnings("unused")
        protected void append(String s) {
            output.append(s + "\n");
        }
    }

    private class MemberPanel extends BaseUserPanel {

        MemberPanel() {
            btn("Donate 5%", () -> donate());
            btn("Apply Leadership", () -> applyLeadership());
            btn("Vote", () -> vote());
            btn("Logout", () -> {
                currentUser = null;
                switchCard("START");
            });
        }

        @Override
        void refresh() {
            header.setText("Member: " + currentUser.getName());
            output.setText(profile(currentUser));
        }
    }

    private class LeaderPanel extends BaseUserPanel {

        LeaderPanel() {
            btn("Announce Election", () -> announceElection());
            btn("Stop Election", () -> stopElection());
            btn("Vote", () -> vote());
            btn("Donations", () -> donations());
            btn("Donate 5%", () -> donate());
            btn("Approve Member", () -> approveMember());
            btn("Terminate Member", () -> terminateMember());
            btn("Apply Leadership", () -> applyLeadership());
            btn("Logout", () -> {
                currentUser = null;
                switchCard("START");
            });
        }

        @Override
        void refresh() {
            header.setText("Leader: " + currentUser.getName() + " (" + currentUser.getRole() + "/" + currentUser.getCommitteeLevel() + ")");
            output.setText(profile(currentUser));
        }
    }

    private class AdminPanel extends BaseUserPanel {

        AdminPanel() {
            btn("Approve / Reject", () -> approveMember());
            btn("Promote", () -> promote());
            btn("Demote", () -> demote());
            btn("Announce Election", () -> announceElection());
            btn("Close Election", () -> stopElection());
            btn("Logout", () -> {
                currentUser = null;
                switchCard("START");
            });
        }

        @Override
        void refresh() {
            header.setText("Admin: " + currentUser.getName());
            output.setText("Admin capabilities loaded.\n");
        }
    }

    // ============= Actions =============
    private void donations(){
        if(currentUser.getRole()!=Role.PRESIDENT || currentUser.getCommitteeLevel()!=CommitteeLevel.CENTRAL){
            JOptionPane.showMessageDialog(frame, "Only central president can see");
            return;
        }
        BigDecimal bd = new BigDecimal(system.getDonations());
        JOptionPane.showMessageDialog(frame, "Donations : "+bd.toPlainString());
    }
    
    private void donate() {
        double base = currentUser.getYearlyIncome() * 0.05;
        String amtS = JOptionPane.showInputDialog(frame, "Default 5% = " + base + "\nPress OK to confirm or enter custom amount:", base);
        if (amtS == null) {
            return;
        }
        try {
            double amt = Double.parseDouble(amtS.trim());
            if (amt <= 0) {
                throw new NumberFormatException();
            }
            currentUser.setDonation(currentUser.getDonation() + amt);
            currentUser.setHasDonated(true);
            system.getCentralCommittee().addDonation(amt);
            JOptionPane.showMessageDialog(frame, "Donation recorded: " + amt);
            switch (currentUser.getRole()) {
                case MEMBER ->
                    memberPanel.refresh();
                case ADMIN ->
                    adminPanel.refresh();
                default ->
                    leaderPanel.refresh();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid amount");
        }
    }

    private void guestDonate() {
        String email = JOptionPane.showInputDialog(frame, "Enter member email (or leave blank for anonymous):");
        if (email == null) {
            return;
        
        }email = email.trim();
        Member found = null;
        if (!email.isEmpty()) {
            try {
                found = system.findByEmail(email);
            } catch (Exception ignored) {
            }
        }
        double suggestion = (found != null) ? found.getYearlyIncome() * 0.05 : 0;
        String prompt = (found != null) ? ("Suggested 5% donation = " + suggestion + "\nEnter amount:") : "Enter donation amount:";
        String amtS = JOptionPane.showInputDialog(frame, prompt, suggestion > 0 ? suggestion : null);
        if (amtS == null) {
            return;
        }
        try {
            double amt = Double.parseDouble(amtS.trim());
            if (amt <= 0) {
                throw new NumberFormatException();
            }
            system.getCentralCommittee().addDonation(amt);
            if (found != null) {
                found.setDonation(found.getDonation() + amt);
                found.setHasDonated(true);
            }
            JOptionPane.showMessageDialog(frame, "Thank you for donating." + (found != null ? " Applied to member " + found.getName() : " (anonymous)"));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid amount");
        }
    }

    private void applyLeadership() {
        Role role = chooseRole();
        if (role == null) {
            return;
        }
        CommitteeLevel targetLevel = chooseCommitteeLevel();
        boolean ok = system.applyForLeadership(currentUser, role, targetLevel);
        JOptionPane.showMessageDialog(frame, ok ? "Registered as candidate" : "Failed to register");
    }

    private void vote() {
        CommitteeLevel level = chooseCommitteeLevel(); 
        if (level == null) {
            return;
        }
        // Select committee (division/district) if needed
        Division division = currentUser.getAddress().getDivision();
        District district = currentUser.getAddress().getDistrict();
        Map<Role, List<Member>> cmap = new HashMap<>();
        switch (level) {
            case CENTRAL -> {
                cmap = system.getCentralCommittee().getElection().getAllCandidates();
            }
            case DIVISIONAL -> {
                cmap = system.getDivisionalCommittee(division).getElection().getAllCandidates();
            }
            case DISTRICT -> {
                cmap = system.getDistrictCommittee(district).getElection().getAllCandidates();
            }
            default -> {
                JOptionPane.showMessageDialog(frame, "Invalid committee level");
                return;
            }
        }
        if (cmap.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No active candidates");
            return;
        }
        Role role = (Role) JOptionPane.showInputDialog(frame, "Select Role", "Vote", JOptionPane.PLAIN_MESSAGE, null, cmap.keySet().toArray(), null);
        if (role == null) {
            return;
        }
        List<Member> list = cmap.get(role);
        if (list == null || list.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No candidates for selected role");
            return;
        }
        Member cand = (Member) JOptionPane.showInputDialog(frame, "Candidate", "Vote", JOptionPane.PLAIN_MESSAGE, null, list.toArray(), null);
        if (cand == null) {
            return;
        }
        boolean ok = false;
        switch (level) {
            case CENTRAL -> ok = system.getCentralCommittee().getElection().vote(currentUser, role, cand);
            case DIVISIONAL -> ok = system.getDivisionalCommittee(division).getElection().vote(currentUser, role, cand);
            case DISTRICT -> ok = system.getDistrictCommittee(district).getElection().vote(currentUser, role, cand);
            default -> ok = false;
        }
        JOptionPane.showMessageDialog(frame, ok ? "Vote recorded" : "Vote failed");
    }

    private void announceElection() {
        if (currentUser.getRole() != Role.PRESIDENT && currentUser.getRole() != Role.ADMIN) {
            JOptionPane.showMessageDialog(frame, "Only presidents or admins can declare elections");
            return;
        }
        Committee target = pickTargetCommittee();
        if (target == null) {
            return;
        }
        boolean ok = system.declareElection(target, currentUser);
        JOptionPane.showMessageDialog(frame, ok ? "Election declared" : "Failed");
    }

    private void stopElection() {
        if (currentUser.getRole() != Role.PRESIDENT && currentUser.getRole() != Role.ADMIN) {
            JOptionPane.showMessageDialog(frame, "Only presidents or admins can close elections");
            return;
        }
        Committee target = pickTargetCommittee();
        if (target == null) {
            return;
        }
        boolean ok = system.closeElection(target, currentUser);
        JOptionPane.showMessageDialog(frame, ok ? "Election closed" : "Failed");
    }

    private void approveMember() {
    List<Member> pend = system.getAllPendingApplications();
    if (pend.isEmpty()) {
        JOptionPane.showMessageDialog(frame, "No pending");
        return;
    }

    // Create checkboxes for each pending member
    JCheckBox[] checkBoxes = new JCheckBox[pend.size()];
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    for (int i = 0; i < pend.size(); i++) {
        Member m = pend.get(i);
        checkBoxes[i] = new JCheckBox(m.toString()); // or m.getName()
        panel.add(checkBoxes[i]);
    }

    // Put the panel in a scroll pane
    JScrollPane scrollPane = new JScrollPane(panel);
    scrollPane.setPreferredSize(new Dimension(700, 200)); // make it scrollable

    // Show dialog with Approve and Reject buttons
    Object[] options = {"Approve", "Reject", "Cancel"};
    int opt = JOptionPane.showOptionDialog(
            frame,
            scrollPane,
            "Select members to process",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[0]
    );

    if (opt == JOptionPane.CANCEL_OPTION || opt == JOptionPane.CLOSED_OPTION) {
        return;
    }

    boolean approve = (opt == JOptionPane.YES_OPTION);

    StringBuilder result = new StringBuilder();
    for (int i = 0; i < checkBoxes.length; i++) {
        if (checkBoxes[i].isSelected()) {
            Member m = pend.get(i);
            boolean ok = approve ?
                    system.approveApplication(m.getNationalId()) :
                    system.rejectApplication(m.getNationalId());

            result.append(m.getName())
                  .append(": ")
                  .append(ok ? (approve ? "Approved" : "Rejected") : "Failed")
                  .append("\n");
        }
    }

    if (result.length() == 0) {
        JOptionPane.showMessageDialog(frame, "No members selected. Pending list unchanged.");
    } else {
        JOptionPane.showMessageDialog(frame, result.toString());
    }

    // Refresh leader panel to reflect changes
    leaderPanel.refresh();
}

    private void terminateMember() {
        String[] options = {"By National ID", "By Email", "Cancel"};
        int choice = JOptionPane.showOptionDialog(frame, "Terminate member by:", "Terminate Membership", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice == 2 || choice == -1) {
            return;
        }
        Member member = null;
        if (choice == 0) {
            String nid = JOptionPane.showInputDialog(frame, "Enter National ID:");
            if (nid == null || nid.isBlank()) {
                return;
            }
            member = system.findById(nid);
            if (member == null) {
                member = system.findByEmail(nid);
            }
        } else if (choice == 1) {
            String email = JOptionPane.showInputDialog(frame, "Enter Email:");
            if (email == null || email.isBlank()) {
                return;
            }
            member = system.findByEmail(email.trim());
        }
        if (member == null) {
            JOptionPane.showMessageDialog(frame, "Member not found.");
            return;
        }
        // Show member details
        int confirm = JOptionPane.showConfirmDialog(frame, profile(member), "Confirm Termination", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        boolean ok = false;
        if (choice == 0) {
            ok = system.terminateMembershipID(member.getNationalId());
        } else if (choice == 1) {
            ok = system.terminateMembershipEmail(member.getEmail());
        }
        JOptionPane.showMessageDialog(frame, ok ? "Terminated" : "Failed to terminate");
    }

    private void promote() {
        if(currentUser.getRole()!=Role.ADMIN && currentUser.getRole()!=Role.PRESIDENT && currentUser.getCommitteeLevel()!=CommitteeLevel.CENTRAL){
            JOptionPane.showMessageDialog(frame, "Only admins or presidents can promote");
            return;
        }
        String nid = JOptionPane.showInputDialog(frame, "Member National ID:");
        if (nid == null) {
            return;
        }
        Role role = chooseRole();
        if (role == null) {
            return;
        }
        CommitteeLevel level = chooseCommitteeLevel();
        if (level == null) {
            return;
        }
        Division div = null;
        District dist = null;
        if (level == CommitteeLevel.DIVISIONAL) {
            div = chooseDivision();
        }
        if (level == CommitteeLevel.DISTRICT) {
            div = chooseDivision();
            if (div == null) {
                return;
            }
            dist = chooseDistrictInDivision(div);
        }
        boolean ok = system.promoteToLeader(nid.trim(), level, role, div, dist);
        JOptionPane.showMessageDialog(frame, ok ? "Promoted" : "Failed");
    }

    private void demote() {
        if(currentUser.getRole()!=Role.ADMIN && currentUser.getRole()!=Role.PRESIDENT && currentUser.getCommitteeLevel()!=CommitteeLevel.CENTRAL){
            JOptionPane.showMessageDialog(frame, "Only admins or presidents can promote");
            return;
        }
        String email = JOptionPane.showInputDialog(frame, "Leader Email:");
        if (email == null) {
            return;
        }
        boolean ok = system.demoteLeader(email.trim());
        JOptionPane.showMessageDialog(frame, ok ? "Demoted" : "Failed");
    }

    // ============= Selection Helpers =============
    private Role chooseRole() {
        List<Role> allowed = new ArrayList<>();
        for (Role r : Role.values()) {
            if (r != Role.MEMBER && r != Role.ADMIN) {
                allowed.add(r);
            }
        }
        return (Role) JOptionPane.showInputDialog(frame, "Role", "Role", JOptionPane.PLAIN_MESSAGE, null, allowed.toArray(), null);
    }

    private CommitteeLevel chooseCommitteeLevel() {
        List<CommitteeLevel> levels = new ArrayList<>();
        for (CommitteeLevel cl : CommitteeLevel.values()) {
            levels.add(cl);
        }
        return (CommitteeLevel) JOptionPane.showInputDialog(frame, "Committee Level", "Level", JOptionPane.PLAIN_MESSAGE, null, levels.toArray(), null);
    }


    private Division chooseDivision() {
        return (Division) JOptionPane.showInputDialog(frame, "Division", "Division", JOptionPane.PLAIN_MESSAGE, null, Division.values(), null);
    }

    private District chooseDistrictInDivision(Division div) {
        List<District> list = new ArrayList<>();
        for (District d : District.values()) {
            if (d.getDivision() == div) {
                list.add(d);
            }
        }
        return (District) JOptionPane.showInputDialog(frame, "District", "District", JOptionPane.PLAIN_MESSAGE, null, list.toArray(), null);
    }

    private Committee pickTargetCommittee() {
        String[] opts = {"Central","Divisional", "District", "Cancel"};
        int choice = JOptionPane.showOptionDialog(frame, "Target committee type", "Election Target", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opts, opts[0]);
        if (choice == 3 || choice == -1) {
            return null;
        }
        if(choice==0){
            return system.getCentralCommittee();
        }
        if (choice == 1) {
            Division div = chooseDivision();
            return div == null ? null : system.getDivisionalCommittee(div);
        }
        if (choice == 2) {
            Division div = chooseDivision();
            if (div == null) {
                return null;
            }
            District dist = chooseDistrictInDivision(div);
            return dist == null ? null : system.getDistrictCommittee(dist);
        }
        return null;
    }

    private String profile(Member m) {
        return "Name: " + m.getName() + "\n"
                + "Role: " + m.getRole() + "\n"
                + "Level: " + m.getCommitteeLevel() + "\n"
                + "Email: " + m.getEmail() + "\n"
                + "NationId: "+m.getNationalId()+"\n"
                + "Division: " + m.getAddress().getDivision() +"\n"
                + "District: " + m.getAddress().getDistrict() + "\n"
                + "Yearly Income: " + m.getYearlyIncome() + "\n"
                + "Donation (total): " + m.getDonation() + "\n";
    }
}
