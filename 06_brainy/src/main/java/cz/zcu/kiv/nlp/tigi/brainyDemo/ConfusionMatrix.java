package cz.zcu.kiv.nlp.tigi.brainyDemo;

import java.util.*;

/**
 * (c) 2013 Ivan Habernal
 */
public class ConfusionMatrix {

    int total = 0;
    int correct = 0;
    private Map<String, Map<String, Integer>> map;
    private int numberOfDecimalPlaces = 3;
    private TreeSet<String> allGoldLabels = new TreeSet<String>();
    private TreeSet<String> allExpectedLabels = new TreeSet<String>();
    private List<String> labelSeries = new ArrayList<String>();

    public ConfusionMatrix() {
        this.map = new TreeMap<String, Map<String, Integer>>();

    }

    private String getFormat() {
        return "%." + numberOfDecimalPlaces + "f";
    }

    public void increaseValue(String expectedValue, String actualValue) {
        increaseValue(expectedValue, actualValue, 1);
    }

    /**
     * Increases value of expectedValue x actualValue n times
     *
     * @param expectedValue exp
     * @param actualValue   ac
     * @param times         n-times
     */
    public void increaseValue(String expectedValue, String actualValue, int times) {
        allGoldLabels.add(expectedValue);
        allExpectedLabels.add(actualValue);

        for (int i = 0; i < times; i++) {
            labelSeries.add(actualValue);
        }

        if (!map.containsKey(expectedValue)) {
            map.put(expectedValue, new TreeMap<String, Integer>());
        }

        if (!map.get(expectedValue).containsKey(actualValue)) {
            map.get(expectedValue).put(actualValue, 0);
        }

        int currentValue = this.map.get(expectedValue).get(actualValue);
        this.map.get(expectedValue).put(actualValue, currentValue + times);

        total += times;

        if (expectedValue.equals(actualValue)) {
            correct += times;
        }
    }

    public double getAccuracy() {
        return ((double) correct / (double) total);
    }

    public int getTotalSum() {
        return total;
    }

    public int getRowSum(String label) {
        int result = 0;

        for (Integer i : map.get(label).values()) {
            result += i;
        }

        return result;
    }

    public int getColSum(String label) {
        int result = 0;

        for (Map<String, Integer> row : this.map.values()) {
            if (row.containsKey(label)) {
                result += row.get(label);
            }
        }

        return result;
    }

    /**
     * Micro-averaged F-measure gives equal weight to each document and is therefore
     * considered as an average over all the document/category pairs. It tends to be
     * dominated by the classifier’s performance on common categories. (It's actually the accuracy).
     * <p/>
     * (from Ozgur et al., 2005. Text Categorization with Class-Based and Corpus-Based Keyword Selection.)
     *
     * @return double
     */
    public double getMicroFMeasure() {
        int allTruePositives = 0;
        int allTruePositivesAndFalsePositives = 0;
        int allTruePositivesAndFalseNegatives = 0;

        for (String label : map.keySet()) {
            if (map.containsKey(label) && map.get(label).containsKey(label)) {
                allTruePositives += this.map.get(label).get(label);
            }
            allTruePositivesAndFalsePositives += getColSum(label);
            allTruePositivesAndFalseNegatives += getRowSum(label);
        }

        double precision = (double) allTruePositives / (double) allTruePositivesAndFalsePositives;
        double recall = (double) allTruePositives / (double) allTruePositivesAndFalseNegatives;

        return (2.0 * precision * recall) / (precision + recall);
    }

}
