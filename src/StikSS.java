public class StikSS {
    private int dateStamp;
    private String EIDstika;

    public StikSS(int date, String EID){
        this.dateStamp = date;
        this.EIDstika = EID;
    }

    public int getDateStamp() {
        return dateStamp;
    }

    public void setDateStamp(int dateStamp) {
        this.dateStamp = dateStamp;
    }

    public String getEIDstika() {
        return EIDstika;
    }

    public void setEIDstika(String EIDstika) {
        this.EIDstika = EIDstika;
    }
}


