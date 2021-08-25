import org.apache.commons.math3.distribution.GammaDistribution;

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
    public static void naredi_stik(Posameznik oseba, int id_stika, double verjetnost_okuzbe, int date){

        Random rand = new Random();
        Posameznik clen1 = oseba;
        Posameznik clen2 = novModel.getPosamezniki()[id_stika];

        //zabelezimo stik ce imata oba aplikacojo za ss
        if(clen1.isContact_tracing() && clen2.isContact_tracing()){
            clen1.getAplikacija_za_sledenje_stikom().ZabeleziStik(date, clen2.getAplikacija_za_sledenje_stikom().getEID());
            clen2.getAplikacija_za_sledenje_stikom().ZabeleziStik(date, clen1.getAplikacija_za_sledenje_stikom().getEID());
        }

        //procesiramo okuzbo
        if(clen1.isOkuzen() && clen2.isOkuzen()){
        }
        else if(clen1.isKuzen() && !clen2.isImun()){
            //verjetnost da se clen2 okuzi
            if(rand.nextInt(1000) <= verjetnost_okuzbe){
                //clen2 se okuzi
                clen2.setOkuzen(true);
                clen2.setDni_okuzen(0);
            }
        }
        else if(!clen1.isImun() && clen2.isKuzen()){
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
        int[] household_contacts = new Random().ints(0, oseba.getGospodinjstvo().getClani_gospodinjstva().length).distinct().limit((long)dejanskih_kontaktov).toArray();

        //System.out.println(Arrays.toString(old_contacts));

        //gremo cez vse kontakte in procesirao stike

        if(!oseba.isSamoizolacija()){
            for (int i = 0; i<old_contacts.length; i++){
                naredi_stik(oseba,oseba.getPogosti_stiki()[old_contacts[i]],infection_outside,date);

            }

            for (int i = 0; i<random_contacts.length; i++){
                naredi_stik(oseba,random_contacts[i],infection_outside,date);
            }

        }

        for (int i = 0; i<household_contacts.length; i++){
            naredi_stik(oseba,oseba.getGospodinjstvo().getClani_gospodinjstva()[household_contacts[i]].getId(),infection_family,date);
        }

    }

    public static void okuzi_posameznike(int st_okuzenih){
        int[] random_okuzeni = new Random().ints(0, novModel.getPosamezniki().length).distinct().limit(st_okuzenih).toArray();

        for(int i=0; i<st_okuzenih; i++){
            novModel.getPosamezniki()[random_okuzeni[i]].setOkuzen(true);
            novModel.getPosamezniki()[random_okuzeni[i]].setDni_okuzen(0);
        }
    }


    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        int st_gospodinjstev = sc.nextInt();
        int st_dni = sc.nextInt();
        novModel = new Model(st_gospodinjstev);


        /*
        Potek sirjenja bolezni simuliramo po dneh
        vsak dan se nasi akterji ki niso v izolaciji gibljejo po svojih opravkih in prihajajo v kontakt z drugimi osebami

        en dan v nasi simulaciji predstavlja en potek for zanke
         */
        okuzi_posameznike(15);

        int[] okuzenih = new int[st_dni];
        for (int i = 0; i < st_dni; i++){

            int st_okuzenih = 0;
            novModel.getStreznik().procesiraj_streznik(i);

            //en dan pojdi od zacetka, en dan od konca za lepso razporeditev
            for (int o = 0; o<novModel.getPosamezniki().length; o++){

                Posameznik oseba = novModel.getPosamezniki()[o];
                oseba.opravi_dan(i);
                stiki(oseba,i);

                if(oseba.isOkuzen()){
                    st_okuzenih++;
                }
            }

            okuzenih[i]=st_okuzenih;

        }
        System.out.println(Arrays.toString(okuzenih));

    }
}
