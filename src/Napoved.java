import org.apache.commons.math3.distribution.GammaDistribution;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Napoved {

    static Model novModel;

    //gamma porazdelitev stevila kontaktov
    public static double shape = 1.64;
    public static double scale = 4.08;

    //kontaktov dnevno znotraj druzine
    public static double family_contacts = 2;

    //odstotek starih kontaktov na dan
    public static double repeating_contacts = 0.8;

    //odstotek novih kontaktov na dan
    public static double new_contacts = 0.2;

    //verjetnost okuzbe zunanjega
    public static double infection_outside = 43;

    //verjetnost okuzbe druzina
    public static double infection_family = 83;


    //procesiramo stik osebe z nekim drugim akterjev v modelu
    public static void naredi_stik(Posameznik oseba, Posameznik id_stika, double verjetnost_okuzbe, int date){

        Random rand = new Random();
        Posameznik clen1 = oseba;
        Posameznik clen2 = id_stika;


        //zabelezimo stik ce imata oba aplikacojo za ss
        if(clen1.isContact_tracing() && clen2.isContact_tracing()){
            clen1.getAplikacija_za_sledenje_stikom().ZabeleziStik(date, clen2.getAplikacija_za_sledenje_stikom().getEID());
            clen2.getAplikacija_za_sledenje_stikom().ZabeleziStik(date, clen1.getAplikacija_za_sledenje_stikom().getEID());
        }

        //procesiramo okuzbo
        if(clen1.isOkuzen() && clen2.isOkuzen()){
        }
        else if(clen1.isKuzen() && !clen2.isImun() && !clen2.isOkuzen()){
            if (clen1.isImun()){
                System.out.println("ERROR");
            }
            //verjetnost da se clen2 okuzi
            if(rand.nextInt(1000) <= verjetnost_okuzbe){
                //clen2 se okuzi
                clen2.setOkuzen(true);
                clen2.setDni_okuzen(0);
            }
        }
        else if(!clen1.isImun() && clen2.isKuzen() && !clen1.isOkuzen()){
            if (clen2.isImun()){
                System.out.println("ERROR");
            }
            //verjetnost da se clen1 okuzi
            if(rand.nextInt(1000) <= verjetnost_okuzbe){
                //clen1 se okuzi
                clen1.setOkuzen(true);
                clen1.setDni_okuzen(0);
            }
        }


    }

    //ugotovimo koliko in katere stike ima oseba tekom dneva


    public static void stiki(Posameznik oseba, int date){

        double st_kontaktov = new GammaDistribution(1.64, 4.08).sample();
        //starih kontatov

        /*
        Poskrbimo da ne pride do tezav, ce oseba sreca vec ljudi kot jih pozna.
        Ce je v dnevu srecala vec ljudi, se dodatni ljudje povlecejo iz nakjucnih ljudi in ne iz znancev
        */
        int starih_kontaktov = (int)Math.round(st_kontaktov * repeating_contacts);
        int prenos = 0;
        if (starih_kontaktov>20){
            prenos=starih_kontaktov-20;
            starih_kontaktov = 20;
        }

        //kontakti ki jih poznamo
        int[] old_contacts = new Random().ints(0, oseba.getPogosti_stiki().length).distinct().limit(starih_kontaktov).toArray();

        //novih kontaktov
        int[] random_contacts = new Random().ints(0, novModel.getPosamezniki().length).distinct().limit(Math.round(st_kontaktov * new_contacts+prenos)).toArray();

        //druzinskih kontaktov
        int dejanskih_kontaktov = (int)family_contacts;
        if(family_contacts>oseba.getGospodinjstvo().getClanov()){
            dejanskih_kontaktov = oseba.getGospodinjstvo().getClanov();
        }
        int[] household_contacts = new Random().ints(0, oseba.getGospodinjstvo().getClani_gospodinjstva().length).distinct().limit(dejanskih_kontaktov).toArray();

        //System.out.println(Arrays.toString(old_contacts));

        //gremo cez vse kontakte in procesirao stike



        if(!oseba.isSamoizolacija()){
            for (int i = 0; i<old_contacts.length; i++){
                Posameznik clen2 = novModel.getPosamezniki()[oseba.getPogosti_stiki()[old_contacts[i]]];
                if (!clen2.isSamoizolacija()){
                    naredi_stik(oseba,clen2,infection_outside,date);
                }

            }

            for (int i = 0; i<random_contacts.length; i++){
                Posameznik clen2 = novModel.getPosamezniki()[random_contacts[i]];
                if (!clen2.isSamoizolacija()) {
                    naredi_stik(oseba, clen2, infection_outside, date);
                }
            }

        }

        for (int i = 0; i<household_contacts.length; i++){
            Posameznik clen2 = novModel.getPosamezniki()[oseba.getGospodinjstvo().getClani_gospodinjstva()[household_contacts[i]].getId()];
            naredi_stik(oseba,clen2,infection_family,date);

        }

    }

    public static void okuzi_posameznike(int st_okuzenih){
        int[] random_okuzeni = new Random().ints(0, novModel.getPosamezniki().length).distinct().limit(st_okuzenih).toArray();

        for(int i=0; i<st_okuzenih; i++){
            novModel.getPosamezniki()[random_okuzeni[i]].setOkuzen(true);
            novModel.getPosamezniki()[random_okuzeni[i]].setDni_okuzen(0);
        }
    }


    public static void izvedi_simulacijo(int st_gospodinjstev, int st_dni, int povprecjeSS){

        novModel = new Model(st_gospodinjstev,povprecjeSS);


        /*
        Potek sirjenja bolezni simuliramo po dneh
        vsak dan se nasi akterji ki niso v izolaciji gibljejo po svojih opravkih in prihajajo v kontakt z drugimi osebami

        en dan v nasi simulaciji predstavlja en potek for zanke
         */
        okuzi_posameznike(600);

        int[] okuzenih = new int[st_dni];

        int[] izoliranih = new int[st_dni];

        int[] imunih = new int[st_dni];

        for (int i = 0; i < st_dni; i++){
            //System.out.println("------------------------ " + i + "----------------------------" );
            int st_okuzenih = 0;
            int st_izoliranih = 0;
            int st_imunih = 0;
            novModel.getStreznik().procesiraj_streznik(i);

            //en dan pojdi od zacetka, en dan od konca za lepso razporeditev

            //vsaka aplikacija naredi svoj cikel
            for (int o = 0; o<novModel.getPosamezniki().length; o++){
                if (novModel.getPosamezniki()[o].isContact_tracing()){
                    novModel.getPosamezniki()[o].getAplikacija_za_sledenje_stikom().procesiraj_dan(i);
                }
            }


            for (int o = 0; o<novModel.getPosamezniki().length; o++){

                Posameznik oseba = novModel.getPosamezniki()[o];
                oseba.opravi_dan(i);
                stiki(oseba,i);

                if(oseba.isOkuzen()){
                    st_okuzenih++;
                }

                if(oseba.isSamoizolacija()){
                    st_izoliranih++;
                }

                if(oseba.isImun()){
                    st_imunih++;
                }

            }


            /*
            if (i%2 == 0){

            }
            else{
                for (int o = novModel.getPosamezniki().length-1; o>=0 ; o--){

                    Posameznik oseba = novModel.getPosamezniki()[o];
                    oseba.opravi_dan(i);
                    stiki(oseba,i);

                    if(oseba.isOkuzen()){
                        st_okuzenih++;
                    }
                }
            }
*/

            okuzenih[i]=st_okuzenih;
            izoliranih[i]=st_izoliranih;
            imunih[i] = st_imunih;

        }
        System.out.println(Arrays.toString(okuzenih));
        System.out.println(Arrays.toString(izoliranih));
        System.out.println(Arrays.toString(imunih));

    }

    public static void main(String[] args) {
/*
        Scanner sc = new Scanner(System.in);
        int st_gospodinjstev = sc.nextInt();
        int st_dni = sc.nextInt();
 */
        izvedi_simulacijo(110000,150,10);
        izvedi_simulacijo(110000,150,30);
        izvedi_simulacijo(110000,150,50);

/*

        izvedi_simulacijo(110000,150,0);
        izvedi_simulacijo(110000,150,20);
        izvedi_simulacijo(110000,150,40);
        izvedi_simulacijo(110000,150,60);
        izvedi_simulacijo(110000,150,70);
        izvedi_simulacijo(110000,150,80);
        izvedi_simulacijo(110000,150,90);
        izvedi_simulacijo(110000,150,100);

 */
    }
}
