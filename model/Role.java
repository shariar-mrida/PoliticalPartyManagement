package model;
public enum Role {
    ADMIN(0), PRESIDENT(1), VICE_PRESIDENT(2), GENERAL_SECRETARY(3), OFFICE_SECRETARY(4), MEMBERSHIP_SECRETARY(5), OTHER_SECRETARY(6), MEMBER(7);
    private final int order;
    Role(int order){
        this.order = order;
    }
    public int gerRoleOrder(){
        return this.order;
    }
}