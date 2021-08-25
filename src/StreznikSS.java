import com.github.mgunlogson.cuckoofilter4j.CuckooFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StreznikSS {

    Set<StikSS> ogrozeni_EIDji_trenutni;
    CuckooFilter<CharSequence> ogrozeni_EIDji_filter;

    public StreznikSS(){
        this.ogrozeni_EIDji_trenutni = new HashSet<>();
    }


    public void procesiraj_streznik(int datum){
        pocisti_stike(datum);
        narediFilter();
    }

    public CuckooFilter<CharSequence> prenesiFilter(){
        return this.ogrozeni_EIDji_filter;
    }

    private void pocisti_stike(int datum){
        //ce je stik starejsi od 6 dni ga izbrisemo
        ogrozeni_EIDji_trenutni.removeIf(s -> s.getDateStamp() + 6 < datum);
    }


    public void zabelezi_okuzbo(List<StikSS> ogrozeniEID){
        this.ogrozeni_EIDji_trenutni.addAll(ogrozeniEID);
    }

    private void narediFilter(){

        //SUSS
        ogrozeni_EIDji_filter = new CuckooFilter.Builder<>(Funnels.stringFunnel(Charset.defaultCharset()), 2000000).build();

        for (StikSS stik : ogrozeni_EIDji_trenutni){
            ogrozeni_EIDji_filter.put(stik.getEIDstika());
        }

    }

}