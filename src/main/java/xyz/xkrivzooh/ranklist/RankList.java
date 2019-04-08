package xyz.xkrivzooh.ranklist;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface RankList {

    /**
     * 排行榜的标识符
     * @return 返回排行榜的标识符
     */
    String identifier();

    /**
     * 为某个参与者进行投票
     * @param item 参与者的名称
     * @param score 投票数，可以是负数
     */
    void vote(String item, double score);

    /**
     * 查看参与者的投票数
     * @param item 参与者的名称
     * @return 返回参与者的分数 如果参与者不存在，则返回的分数值为null
     */
    CompletableFuture<Double> viewScore(String item);

    /**
     * 查看参与者在排行榜的排名
     * @param item 参与者的名称
     * @return 返回参与者在排行榜的排名, 如果参与者不存在，则返回的排名值为null。排名值从0开始
     */
    CompletableFuture<Long> viewRank(String item);

    /**
     * 返回排行榜的TopN
     * @param count topN
     * @return 返回排行榜的TopN的参与者和对应的排名。已经按照排行榜的顺序排序
     */
    CompletableFuture<List<RankListEntity>> topN(int count);

    /**
     * 返回参与排行的参与者数量
     * @return 返回参与排行的参与者数量
     */
    CompletableFuture<Long> count();
}
