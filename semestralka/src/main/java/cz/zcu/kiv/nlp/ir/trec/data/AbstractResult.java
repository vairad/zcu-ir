package cz.zcu.kiv.nlp.ir.trec.data;

import java.util.Objects;

/**
 * Created by Tigi on 6.1.2015.
 */
public abstract class AbstractResult implements Result {
    String documentID;
    int rank = -1;
    float score = -1;

    public String getDocumentID() {
        return documentID;
    }

    public float getScore() {
        return score;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

    public void setScore(float score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Result{" +
                "documentID='" + documentID + '\'' +
                ", rank=" + rank +
                ", score=" + score +
                '}';
    }

    public String toString(String topic) {
        return topic + " Q0 " + documentID + " " + rank + " " + score + " runindex1";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractResult)) return false;
        AbstractResult that = (AbstractResult) o;
        return Objects.equals(documentID, that.documentID);
    }

    @Override
    public int hashCode() {

        return Objects.hash(documentID);
    }
}
