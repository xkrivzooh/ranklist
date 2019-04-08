package xyz.xkrivzooh.ranklist;

public class RankListEntity {
    private String item;

    private double score;

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "RankListEntity{" +
                "item='" + item + '\'' +
                ", score=" + score +
                '}';
    }
}
