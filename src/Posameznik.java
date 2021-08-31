import java.util.List;
import java.util.Random;

public class Posameznik {
    private boolean okuzen;
    private boolean kuzen;
    private boolean imun;
    private boolean samoizolacija;
    private boolean contact_tracing;
    private boolean pozitivenTest;

    private boolean asimptomatski;

    private boolean debuging;

    /*
    1 = otrok
    2 = odrasel
    3 = starejsi
     */
    private int starost;

    //je enak -1 ce oseba ni okuzena
    private int dni_okuzen;
    private int id;
    private Gospodinjstvo gospodinjstvo;
    private int[] pogosti_stiki;
    private AplikacijaSS aplikacija_za_sledenje_stikom;
    private int dodeljena_samoizolacija;

    final private int trajanje_bolezni = 15;
    //vzamemo povprecje
    final private int cas_do_simptomov = 5;
    final private int cas_do_kuznosti = 2;

    //povprecno stevilo ljudi z aplikacijo za sledenje stikom
    private int ss_povprecje;

    //po koliko casa gremo iz samoizolacije
    final private int izhod_iz_samoiz = 6;

    public Posameznik(int starost, Gospodinjstvo gospodinjstvo, int ss_nastavljeni_povprecje){
        this.starost = starost;
        this.okuzen = false;
        this.kuzen = false;
        this.contact_tracing = false;
        this.imun = false;
        this.samoizolacija = false;
        this.gospodinjstvo = gospodinjstvo;
        this.dni_okuzen = -1;
        this.ss_povprecje=ss_nastavljeni_povprecje;
        Random rand = new Random();

        if(rand.nextInt(100) < this.ss_povprecje){
            this.contact_tracing = true;
        }

        //moznost da bo okuzb posameznika asimptomatska
        if(rand.nextInt(100) < 40){
            this.asimptomatski = true;
        }
        else{
            this.asimptomatski = false;
        }

    }

    public void opozorilo_aplikacije(int dodeljena_samoizolacija){
        if(!this.imun){
            this.samoizolacija = true;
            if(this.dodeljena_samoizolacija < dodeljena_samoizolacija){
                this.dodeljena_samoizolacija = dodeljena_samoizolacija;
            }
        }
        else{
            this.aplikacija_za_sledenje_stikom.setStatus(1);
        }

    }

    public void opozorilo_druzine(int dodeljena_samoizolacija){
        if (!this.imun){
            this.samoizolacija = true;
            if(this.dodeljena_samoizolacija < dodeljena_samoizolacija){
                this.dodeljena_samoizolacija = dodeljena_samoizolacija;
            }
            if (this.contact_tracing){
                this.aplikacija_za_sledenje_stikom.setStatus(2);
            }
        }
        this.debuging = true;
    }

    public void opravi_dan(int datum){
        //System.out.println(this.dodeljena_samoizolacija + " | " + this.samoizolacija + " | " + this.imun);

        if(okuzen){
            this.dni_okuzen = this.dni_okuzen+1;

            if(this.dni_okuzen>this.cas_do_kuznosti){
                this.kuzen=true;
            }
            //uporabnik se je testiral pozitivno
            if(simptomi() && !pozitivenTest && !asimptomatski){
                this.pozitivenTest = true;
                //celo gospodinjstvo gre v samoizolacijo
                this.gospodinjstvo.samoizoliraj_clane();

                if(this.contact_tracing){
                    this.aplikacija_za_sledenje_stikom.javi_okuzbo(datum);
                }
            }
            //bolezen se je koncala
            if(this.dni_okuzen>=trajanje_bolezni) {

                if (this.dodeljena_samoizolacija!=0){
/*
                    System.out.println(this.dni_okuzen + " | " + this.dodeljena_samoizolacija + " | " + this.isImun() + " | " + this.debuging);
                    System.out.println(this.gospodinjstvo.getClanov());
*/
                }

                debuging=false;
                this.kuzen = false;
                this.pozitivenTest = false;
                this.okuzen = false;
                this.samoizolacija = false;
                this.imun = true;
                this.dodeljena_samoizolacija = -1;
                this.dni_okuzen = -1;
                if(this.contact_tracing){
                    this.aplikacija_za_sledenje_stikom.setStatus(1);
                }
            }
        }

        //this is acting wierd
        if(this.samoizolacija){
            if(this.dodeljena_samoizolacija == 0){
                this.samoizolacija = false;
                if (this.contact_tracing){
                    this.aplikacija_za_sledenje_stikom.setStatus(1);
                }
                this.debuging = false;
            }
            else{
                this.dodeljena_samoizolacija--;
            }

        }
/*
        //aplikacija naredi svoj cikel
        if (this.contact_tracing){
            this.aplikacija_za_sledenje_stikom.procesiraj_dan(datum);
        }
*/

    }
    public boolean simptomi(){
        if (this.dni_okuzen>=this.cas_do_simptomov){
            return true;
        }
        else{
            return false;
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
