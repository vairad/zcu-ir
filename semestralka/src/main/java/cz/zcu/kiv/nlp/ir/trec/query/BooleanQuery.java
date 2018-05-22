package cz.zcu.kiv.nlp.ir.trec.query;

import cz.zcu.kiv.nlp.ir.trec.Index;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Třída vyčleňující metody pro boolean parsování dotazů.
 * @author Radek Vais
 */
public class BooleanQuery {


    /**
     * Method simple parse query to AND OR NOR operators
     * @param query AND OR NOT query
     * @param index index link
     * @return set of related documets according query.
     */
    public static Set<String> searchBoolean(String query, Index index) {
        String[] andBranches = query.split("OR");
        List<Set<String>> andResults = new LinkedList<>();

        for (String andBranch : andBranches) {
            String[] queries = andBranch.split("AND");
            List<Set<String>> partialResults = new LinkedList<>();

            for (String singleQuery: queries) {
                if(singleQuery.contains("NOT")){
                    singleQuery = singleQuery.replaceAll("NOT", "");
                    List<String> tokens = index.tokenQuery(singleQuery);
                    partialResults.add(index.allNotConnectedDocuments(tokens));
                }else{
                    List<String> tokens = index.tokenQuery(singleQuery);
                    partialResults.add(index.allConnectedDocuments(tokens));
                }
            }
            andResults.add(andMergeSets(partialResults));
        }
        return orMergeSets(andResults);
    }

    /**
     * Metoda spojí kolekce ve smyslu operace AND (průnik)
     * @param list seznam kolekcí pro spojení
     * @return spojenoá kolekce
     */
    private static Set<String> andMergeSets(List<Set<String>> list){
        Iterator<Set<String>> it = list.iterator();
        if(!it.hasNext()){
            return null;
        }
        Set<String> intersection = it.next();
        while (it.hasNext()) {
            intersection.retainAll(it.next());
        }
        return intersection;
    }

    /**
     * Metoda spojí kolekce ve smyslu operace OR (nebo) (Nejedná se o XOR, tedy vzájemné vyloučení)
     * @param list seznam kolekcí pro spojení
     * @return spojená kolekce
     */
    private static Set<String> orMergeSets(List<Set<String>> list){
        Iterator<Set<String>> it = list.iterator();
        if(!it.hasNext()){
            return null;
        }
        Set<String> intersection = it.next();
        while (it.hasNext()) {
            intersection.addAll(it.next());
        }
        return intersection;
    }
}
