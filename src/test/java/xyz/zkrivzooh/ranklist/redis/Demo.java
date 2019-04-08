package xyz.zkrivzooh.ranklist.redis;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.common.util.concurrent.MoreExecutors;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.async.RedisAsyncCommands;
import xyz.xkrivzooh.ranklist.RankListEntity;
import xyz.xkrivzooh.ranklist.support.redis.RedisBasedRankList;
import xyz.xkrivzooh.ranklist.support.redis.RedisBasedRankListMaker;
import xyz.xkrivzooh.ranklist.support.redis.RedisClients;

public class Demo {
    public static void main(String[] args) throws Exception {
        RedisURI redisUri = RedisURI.Builder.redis("localhost")
                .withSsl(false)
                .build();
        RedisAsyncCommands<byte[], byte[]> redisAsyncCommands = RedisClients.asyncCommands(redisUri);

        RedisBasedRankListMaker redisBasedRankListMaker = new RedisBasedRankListMaker(redisAsyncCommands);
        RedisBasedRankList rankList = redisBasedRankListMaker.make("rankList3");

        System.out.println(rankList.identifier());

        rankList.vote("a", 1);
        rankList.vote("b", 2);
        rankList.vote("c", 3);
        rankList.vote("d", 4);

        System.out.println("main-> " + Thread.currentThread().getName());

        CompletableFuture<List<RankListEntity>> completableFuture = rankList.topN(5);


        completableFuture.thenAcceptAsync(rankListEntities -> {
            for (RankListEntity rankListEntity : rankListEntities) {
                System.out.println("onSuccess->" + Thread.currentThread().getName());
                System.out.println(rankListEntity);
            }
        }, MoreExecutors.directExecutor());


        System.out.println(rankList.viewScore("a").get());
        System.out.println(rankList.viewScore("xxxx").get());

        System.out.println("viewRank a " +  rankList.viewRank("a").get());
        System.out.println("viewRank b " +  rankList.viewRank("b").get());
        System.out.println("viewRank c " +  rankList.viewRank("c").get());
        System.out.println("viewRank d " +  rankList.viewRank("d").get());
        System.out.println("viewRank xxx " +  rankList.viewRank("xxx").get());

        System.out.println("count " + rankList.count().get());

        Thread.sleep(2000);
        RedisClients.shutdownNow();
    }

}
