import com.github.mgunlogson.cuckoofilter4j.CuckooFilter;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.LinkedList;
import java.util.List;

public class AplikacijaSS {
    //for simplicity its the same as userID
    private int app_id;
    private String EID;
    private Posameznik lastnik;

    private List<StikSS> zabelezeni_stiki;

    private List<StikSS> pretekli_EID;
    /*
    status:
    1 = zdrav
    2 = izpostavljen
    3 = okužen
     */
    private int status;
    //je enak -1 ce uporabnik ni okuzen
    private int datum_okuzbe;

    //povezava do streznika za SS
    private StreznikSS streznik;


    public AplikacijaSS(int id, StreznikSS streznik, Posameznik lastnik) {
        this.app_id = id;
        this.status = 1;
        this.datum_okuzbe = -1;
        this.streznik = streznik;
        this.EID = DigestUtils.sha256Hex(Integer.toString(this.app_id) + Integer.toString(0));

        this.pretekli_EID = new LinkedList<>();
        this.zabelezeni_stiki = new LinkedList<>();
        this.lastnik = lastnik;
    }

    public void ustvari_EID(int datum){
        //aplikacija ustvari nov EID
        this.EID = DigestUtils.sha256Hex(this.app_id + Integer.toString(datum));
        //aplikacija shrani EID v lokalno shrambo
        this.pretekli_EID.add(new StikSS(datum,this.EID));
        //System.out.println(EID);
    }

    public void procesiraj_dan(int datum){


        ustvari_EID(datum);

        pocisti_stike(datum);

        //preveriOkuzbo();

        if (status == 1){
            preveriOkuzbo();
        }

    }

    private void preveriOkuzbo(){
        CuckooFilter<CharSequence> ogrozeni_EIDji_filter = streznik.prenesiFilter();
        for (StikSS stik : zabelezeni_stiki){
            if (ogrozeni_EIDji_filter.mightContain(stik.getEIDstika())){
                //PANIC
                //System.out.println("PANIC");
                this.status = 2;
                lastnik.opozorilo_aplikacije(7);

            }
        }
    }

    public void ZabeleziStik(int date,String EID){
        //zabelezi s kom smo bili v stiku
        StikSS s = new StikSS(date,EID);
        zabelezeni_stiki.add(s);
    }

    /*
    TODO:
        optimizacija -> lahko removamo samo prvih X kontaktov in ne hodimo cez celoten array
     */
    public void pocisti_stike(int datum){
        //ce je stik starejsi od 14 dni ga izbrisemo
        int odrez = 0;
        for (StikSS stikSS : zabelezeni_stiki) {
            if (stikSS.getDateStamp() + 14 < datum) {
                odrez++;
            }
        }

        if (odrez >= 0) {
            zabelezeni_stiki.subList(0, odrez).clear();
        }

        //hranimo svoje EID za 5 dni nazaj (obdobje v katerem bi bili lahko kuzni)

        odrez = 0;
        for (StikSS stikSS : pretekli_EID) {
            if (stikSS.getDateStamp() + 3 < datum) {
                odrez++;
            }
        }

        if (odrez >= 0) {
            pretekli_EID.subList(0, odrez).clear();
        }

    }

    public void javi_okuzbo(int date){
        //System.out.println(pretekli_EID);
        //sporocimo nase stike na streznik
        this.streznik.zabelezi_okuzbo(pretekli_EID);
        this.status=3;
    }

    public int getApp_id() {
        return app_id;
    }

    public void setApp_id(int app_id) {
        this.app_id = app_id;
    }

    public List<StikSS> getZabelezeni_stiki() {
        return zabelezeni_stiki;
    }

    public void setZabelezeni_stiki(List<StikSS> zabelezeni_stiki) {
        this.zabelezeni_stiki = zabelezeni_stiki;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDatum_okuzbe() {
        return datum_okuzbe;
    }

    public void setDatum_okuzbe(int datum_okuzbe) {
        this.datum_okuzbe = datum_okuzbe;
    }

    public StreznikSS getStreznik() {
        return streznik;
    }

    public void setStreznik(StreznikSS streznik) {
        this.streznik = streznik;
    }

    public String getEID() {
        return EID;
    }

    public void setEID(String EID) {
        this.EID = EID;
    }
}