package model;
public enum CommitteeLevel {
    CENTRAL(1), DIVISIONAL(2), DISTRICT(3);
    private final int order;
    CommitteeLevel(int order){
        this.order = order;
    }
    public int getCommitteLevelOrder(){
        return this.order;
    }
}