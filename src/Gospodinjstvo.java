public class Gospodinjstvo {

    //verjetnosti kako veliko je gospodinjstvo (od 1 do 8), podatki iz statisticnega urada slo
    private static double[] verjetnosti_velikosti_gos = new double[]{ 327, 254 , 186 , 148 , 52 , 21 , 8 , 4 };

    private int clanov;
    private Posameznik[] clani_gospodinjstva;

    public Gospodinjstvo (int clanov, int povprecjeSS) {
        this.clanov = clanov;

        this.clani_gospodinjstva= new Posameznik[clanov];

        for (int i = 0; i<clanov; i++){
            clani_gospodinjstva[i] = new Posameznik(2, this, povprecjeSS);
        }
    }

    public void samoizoliraj_clane(){
        for (Posameznik p: clani_gospodinjstva){
            //10
            p.opozorilo_druzine(10);
        }
    }

    public static double[] getVerjetnosti_velikosti_gos() {
        return verjetnosti_velikosti_gos;
    }

    public static void setVerjetnosti_velikosti_gos(double[] verjetnosti_velikosti_gos) {
        Gospodinjstvo.verjetnosti_velikosti_gos = verjetnosti_velikosti_gos;
    }

    public int getClanov() {
        return clanov;
    }

    public void setClanov(int clanov) {
        this.clanov = clanov;
    }

    public Posameznik[] getClani_gospodinjstva() {
        return clani_gospodinjstva;
    }

    public void setClani_gospodinjstva(Posameznik[] clani_gospodinjstva) {
        this.clani_gospodinjstva = clani_gospodinjstva;
    }

}
