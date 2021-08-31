import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Model {
    //private static int id = 0;
    private Gospodinjstvo[] gospodinjstva;
    private Posameznik[] posamezniki;
    private StreznikSS streznik;
    private int povprecjeSS;

    private int st_gospodinjstev;


    public Model(int st_gospodinjstev, int povprecjeSS){
        this.povprecjeSS = povprecjeSS;
        this.streznik = new StreznikSS();
        this.st_gospodinjstev = st_gospodinjstev;
        this.gospodinjstva = narediGospodinjstva(st_gospodinjstev);
        this.posamezniki = indeksiraj_uporabnike(gospodinjstva);



    }
    //ta funkcija uporabnikom podeli IDje in jim pove v kateri druzini so
    public Posameznik[] indeksiraj_uporabnike(Gospodinjstvo[] gospodinjstva){
        int st_clanov = 0;
        for(int i = 0; i<gospodinjstva.length; i++){
            st_clanov = st_clanov + gospodinjstva[i].getClanov();
        }
        System.out.println(st_clanov);
        posamezniki = new Posameznik[st_clanov];
        int id = 0;
        for(int i = 0; i<gospodinjstva.length; i++){
            for(int k = 0; k<gospodinjstva[i].getClani_gospodinjstva().length; k++){
                posamezniki[id] = gospodinjstva[i].getClani_gospodinjstva()[k];
                gospodinjstva[i].getClani_gospodinjstva()[k].setId(id);
                if (gospodinjstva[i].getClani_gospodinjstva()[k].isContact_tracing()){
                    gospodinjstva[i].getClani_gospodinjstva()[k].setAplikacija_za_sledenje_stikom(new AplikacijaSS(id,this.streznik,gospodinjstva[i].getClani_gospodinjstva()[k]));
                }

                gospodinjstva[i].getClani_gospodinjstva()[k].setPogosti_stiki(new Random().ints(0, st_clanov).distinct().limit(20).toArray());
                id++;
            }
        }

        return posamezniki;

    }

    //funkcija ki izracuna koliko velika bodo gospodinjstva in jih naredi.
    public Gospodinjstvo[] narediGospodinjstva(int st_gospodinjstev) {
        Gospodinjstvo[] gospodinjstva = new Gospodinjstvo[st_gospodinjstev];

        for (int i = 0; i<st_gospodinjstev; i++){
            int randomNum = ThreadLocalRandom.current().nextInt(0, 1000 + 1);

            Gospodinjstvo novo;

            //en clan
            if(randomNum < 327){

                novo = new Gospodinjstvo(1, this.povprecjeSS);

            }
            //dva clana
            else if (randomNum<581 ){

                novo = new Gospodinjstvo(2, this.povprecjeSS);

            }
            //trije clani
            else if (randomNum<767){

                novo = new Gospodinjstvo(3, this.povprecjeSS);

            }
            //stirje clani
            else if (randomNum<915){

                novo = new Gospodinjstvo(4, this.povprecjeSS);

            }
            //pet clanov
            else if (randomNum<967){

                novo = new Gospodinjstvo(5, this.povprecjeSS);

            }
            //sest clanov
            else if (randomNum<988){

                novo = new Gospodinjstvo(6, this.povprecjeSS);

            }
            //sedem clanov
            else if (randomNum<996){

                novo = new Gospodinjstvo(7, this.povprecjeSS);

            }
            //osem clanov
            else{

                novo = new Gospodinjstvo(8, this.povprecjeSS);

            }

            gospodinjstva[i] = novo;

        }

        //en clan


        return gospodinjstva;

    }
/*
    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        Model.id = id;
    }
*/
    public Gospodinjstvo[] getGospodinjstva() {
        return gospodinjstva;
    }

    public void setGospodinjstva(Gospodinjstvo[] gospodinjstva) {
        this.gospodinjstva = gospodinjstva;
    }

    public Posameznik[] getPosamezniki() {
        return posamezniki;
    }

    public void setPosamezniki(Posameznik[] posamezniki) {
        this.posamezniki = posamezniki;
    }

    public int getSt_gospodinjstev() {
        return st_gospodinjstev;
    }

    public void setSt_gospodinjstev(int st_gospodinjstev) {
        this.st_gospodinjstev = st_gospodinjstev;
    }

    public StreznikSS getStreznik() {
        return streznik;
    }

    public void setStreznik(StreznikSS streznik) {
        this.streznik = streznik;
    }
}
