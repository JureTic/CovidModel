import java.util.List;
import java.util.Random;

public class Posameznik {
    private boolean okuzen;
    private boolean kuzen;
    private boolean imun;
    private boolean samoizolacija;
    private boolean contact_tracing;

    /*
    1 = otrok
    2 = odrasel
    3 = starejsi
     */
    private int starost;

    //je enak -1 ce oseba ni okuzena
    private int dni_okuzen;
    private int dni_v_samoiz;
    private int id;
    private Gospodinjstvo gospodinjstvo;
    private int[] pogosti_stiki;
    private AplikacijaSS aplikacija_za_sledenje_stikom;

    final private int trajanje_bolezni = 14;
    //vzamemo povprecje
    final private int cas_do_simptomov = 5;
    final private int cas_do_kuznosti = 2;

    //povprecno stevilo ljudi z aplikacijo za sledenje stikom
    final private int ss_povprecje = 50;

    //po koliko casa gremo iz samoizolacije
    final private int izhod_iz_samoiz = 7;

    public Posameznik(int starost, Gospodinjstvo gospodinjstvo, StreznikSS streznik){
        this.starost = starost;
        this.okuzen = false;
        this.kuzen = false;
        this.contact_tracing = false;
        this.imun = false;
        this.samoizolacija = false;
        this.gospodinjstvo = gospodinjstvo;
        this.dni_okuzen = -1;
        this.dni_v_samoiz = 0;
        Random rand = new Random();

        if(rand.nextInt(100) <= this.ss_povprecje){
            this.aplikacija_za_sledenje_stikom=new AplikacijaSS(this.id, streznik, this);
            this.contact_tracing = true;
        }

    }

    public void opozorilo_aplikacije(){
        if(!this.imun){
            this.samoizolacija = true;
            this.dni_v_samoiz = 0;
        }
        else{
            this.aplikacija_za_sledenje_stikom.setStatus(1);
        }

    }


    public void opravi_dan(int datum){
        if(okuzen){
            this.dni_okuzen = this.dni_okuzen+1;

            if(this.dni_okuzen>this.cas_do_kuznosti){
                this.kuzen=true;
            }
            if(this.dni_okuzen>=this.cas_do_simptomov){
                //predvidevamo da ko oseba opazi da je bolan se gre testirati in v samoizolacijo
                this.samoizolacija=true;
                if(this.contact_tracing){
                    this.aplikacija_za_sledenje_stikom.javi_okuzbo(datum);
                }
            }
            if(this.dni_okuzen>trajanje_bolezni) {
                this.okuzen = false;
                this.samoizolacija = false;
                this.dni_v_samoiz = 0;
                this.imun = true;
                this.dni_okuzen = -1;
                if(contact_tracing){
                    this.aplikacija_za_sledenje_stikom.setStatus(1);
                }
            }
        }
        //this is acting wierd
        if(this.samoizolacija){
            if(this.dni_v_samoiz > this.izhod_iz_samoiz && this.dni_okuzen < this.cas_do_simptomov){
                this.samoizolacija = false;
                this.dni_v_samoiz = 0;
                if (contact_tracing){
                    this.aplikacija_za_sledenje_stikom.setStatus(1);
                }
            }
            else{
                this.dni_v_samoiz++;
            }

        }

        //aplikacija naredi svoj cikel
        if (this.contact_tracing){
            this.aplikacija_za_sledenje_stikom.procesiraj_dan(datum);
        }


    }

    public boolean isOkuzen() {
        return okuzen;
    }

    public void setOkuzen(boolean okuzen) {
        this.okuzen = okuzen;
    }

    public boolean isKuzen() {
        return kuzen;
    }

    public void setKuzen(boolean kuzen) {
        this.kuzen = kuzen;
    }

    public boolean isImun() {
        return imun;
    }

    public void setImun(boolean imun) {
        this.imun = imun;
    }

    public boolean isSamoizolacija() {
        return samoizolacija;
    }

    public void setSamoizolacija(boolean samoizolacija) {
        this.samoizolacija = samoizolacija;
    }

    public boolean isContact_tracing() {
        return contact_tracing;
    }

    public void setContact_tracing(boolean contact_tracing) {
        this.contact_tracing = contact_tracing;
    }

    public int getStarost() {
        return starost;
    }

    public void setStarost(int starost) {
        this.starost = starost;
    }

    public int getDni_okuzen() {
        return dni_okuzen;
    }

    public void setDni_okuzen(int dni_okuzen) {
        this.dni_okuzen = dni_okuzen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Gospodinjstvo getGospodinjstvo() {
        return gospodinjstvo;
    }

    public void setGospodinjstvo(Gospodinjstvo gospodinjstvo) {
        this.gospodinjstvo = gospodinjstvo;
    }

    public int[] getPogosti_stiki() {
        return pogosti_stiki;
    }

    public void setPogosti_stiki(int[] pogosti_stiki) {
        this.pogosti_stiki = pogosti_stiki;
    }

    public AplikacijaSS getAplikacija_za_sledenje_stikom() {
        return aplikacija_za_sledenje_stikom;
    }

    public void setAplikacija_za_sledenje_stikom(AplikacijaSS aplikacija_za_sledenje_stikom) {
        this.aplikacija_za_sledenje_stikom = aplikacija_za_sledenje_stikom;
    }
}
