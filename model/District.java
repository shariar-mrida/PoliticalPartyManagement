package model;
public enum District {
    // Barishal Division
    Barguna(Division.Barishal,1),
    Barishal(Division.Barishal,2),
    Bhola(Division.Barishal,3),
    Jhalokati(Division.Barishal,4),
    Patuakhali(Division.Barishal,5),
    Pirojpur(Division.Barishal,6),

    // Chattagram Division
    Brahmanbaria(Division.Chattagram,1),
    Chandpur(Division.Chattagram,2),
    Chattogram(Division.Chattagram,3),
    Cumilla(Division.Chattagram,4),
    Coxs_bazar(Division.Chattagram,5),
    Feni(Division.Chattagram,6),
    Khagrachhari(Division.Chattagram,7),
    Lakshmipur(Division.Chattagram,8),
    Noakhali(Division.Chattagram,9),
    Rangamati(Division.Chattagram,10),

    // Dhaka Division
    Dhaka(Division.Dhaka,1),
    Faridpur(Division.Dhaka,2),
    Gazipur(Division.Dhaka,3),
    Gopalganj(Division.Dhaka,4),
    Kishoreganj(Division.Dhaka,5),
    Madaripur(Division.Dhaka,6),
    Manikganj(Division.Dhaka,7),
    Munshiganj(Division.Dhaka,8),
    Narayanganj(Division.Dhaka,9),
    Narsingdi(Division.Dhaka,10),
    Rajbari(Division.Dhaka,11),
    Shariatpur(Division.Dhaka,12),
    Tangail(Division.Dhaka,13),

    // Khulna Division
    Bagerhat(Division.Khulna,1),
    Chuadanga(Division.Khulna,2),
    Jashore(Division.Khulna,3),
    Jhenaidah(Division.Khulna,4),
    Khulna(Division.Khulna,5),
    Kushtia(Division.Khulna,6),
    Magura(Division.Khulna,7),
    Narail(Division.Khulna,8),
    Satkhira(Division.Khulna,9),

    // Mymensingh Division
    Jamalpur(Division.Mymensingh,1),
    Mymensingh(Division.Mymensingh,2),
    Netrokona(Division.Mymensingh,3),
    Sherpur(Division.Mymensingh,4),

    // Rajshahi Division
    Chapainawabganj(Division.Rajshahi,1),
    Joypurhat(Division.Rajshahi,2),
    Naogaon(Division.Rajshahi,3),
    Natore(Division.Rajshahi,4),
    Pabna(Division.Rajshahi,5),
    Rajshahi(Division.Rajshahi,6),
    Sirajganj(Division.Rajshahi,7),

    // Rangpur Division
    Dinajpur(Division.Rangpur,1),
    Gaibandha(Division.Rangpur,2),
    Kurigram(Division.Rangpur,3),
    Lalmonirhat(Division.Rangpur,4),
    Nilphamari(Division.Rangpur,5),
    Panchagarh(Division.Rangpur,6),
    Rangpur(Division.Rangpur,7),
    Thakurgaon(Division.Rangpur,8),

    // Sylhet Division
    Habiganj(Division.Sylhet,1),
    Moulvibazar(Division.Sylhet,2),
    Sunamganj(Division.Sylhet,3),
    Sylhet(Division.Sylhet,4);

    private final Division division;
    private final int order;

    District(Division division, int order) {
        this.division = division;
        this.order = order;
    }

    public Division getDivision() {
        return this.division;
    }

    public int getDistrictOrder() {
        return this.order;
    }
}
