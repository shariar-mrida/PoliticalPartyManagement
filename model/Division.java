package model;
public enum Division {
    Dhaka(1), Chattagram(2), Rajshahi(3), Khulna(4), Rangpur(5), Mymensingh(6), Sylhet(7), Barishal(8);
    private final int order;
    Division(int order){
        this.order = order;
    }
    public int getDivisionOrder(){
        return this.order;
    }
}
