package cz.zcu.kiv.nlp.ir.utils;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    private Utils() {
        //private
    }


    //https://weka.wikispaces.com/Creating%20an%20ARFF%20file - add
    //https://weka.wikispaces.com/Programmatic+Use
    //https://weka.wikispaces.com/Adding%20attributes%20to%20a%20dataset

    /**
     * Create {@link Instances} from given list of arrays with tweets
     * array[0] tweet text, array[1] class of the tweet - positive, negative
     * example ["This is tweet", "positive"]
     */
    public static Instances createInstances(List<String[]> tweets, boolean classMissing) {
        //define data structure for arff file - we have
        // - tweet text attribute (content]
        // - tweet polarity class attribute  (positive, negative)

        //this is how the attribute with string is defined and created
        Attribute tweetTextAttr = new Attribute("content", (List<String>) null);

        List<String> classesList = new ArrayList<>();
        //TODO DONE add positive and negative nominal values
        classesList.add("positive");
        classesList.add("negative");

        //create nominal attribute
        Attribute classAttr = new Attribute("polarity_class", classesList);

        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(tweetTextAttr);
        attributes.add(classAttr);

        //TODO DONE
        //create instances object
        Instances instances = new Instances("my-name", attributes, attributes.size() );

        //fill instanes with data
        for (String[] tweet : tweets) {

            //because weka internally stores values as double and we have two attributes
            double[] values = new double[2];

            //TODO DONE  https://weka.wikispaces.com/Creating%20an%20ARFF%20file
            //get double value for string
            values[0] = instances.attribute("content").addStringValue(tweet[0]);

            //get double value for class
            values[1] = classesList.indexOf(tweet[1]);


            //if we dont know class
            if (classMissing) {
                values[1] = weka.core.Utils.missingValue();
            }

            //create new instance and add it to instancesS
            Instance inst = new DenseInstance(1.0, values);
            instances.add(inst);

            //alternatively
//            Instance inst = new DenseInstance(2);
//            inst.setValue(attributes.get(0), tweet[0]);
//            inst.setValue(attributes.get(1), tweet[1]);
//            instances.add(inst);
        }

        return instances;

    }

    /**
     * Extract tweets text and class from raw data (lines)
     *
     * @param lines lines from raw data
     * @param maxInstances maximum instances per class (positive, negative) that will be extracted
     *                     -1 for all instances
     *
     * @return  list with array of strings,
     *          array[0] tweet text, array[1] class of the tweet - positive, negative
     *          example ["This is tweet", "positive"]
     */
    public static List<String[]> extractTweet(List<String> lines, long maxInstances) {

        if (maxInstances < 1) {
            maxInstances = Long.MAX_VALUE;
        }

        long positiveClasses = 0;
        long negativeClasses = 0;
        String polarity;
        List<String[]> tweets = new ArrayList<>(lines.size());
        for (String line : lines) {
            //split by comma
            String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

            //remove double quotes - begin,end
            parts[0] = parts[0].replaceAll("^\"|\"$", "");
            //0 - negative, 4 positive, 2 - neutral
            if (parts[0].equalsIgnoreCase("0")) {
                polarity = "negative";
                negativeClasses++;
                if (negativeClasses > maxInstances) {
                    continue;
                }

            } else if (parts[0].equalsIgnoreCase("4")) {
                polarity = "positive";
                positiveClasses++;
                if (positiveClasses > maxInstances) {
                    continue;
                }
            } else {
                //neutral - we dont want neutral classes
                continue;
            }


            parts[5] = parts[5].replaceAll("^\"|\"$", "");
            //remove double quotes
            parts[5] = parts[5].replaceAll("\"\"", "\"");

            //add
            tweets.add(new String[]{parts[5], polarity});
            //extract only tweets text and class see http://help.sentiment140.com/for-students
        }
        return tweets;
    }

}
