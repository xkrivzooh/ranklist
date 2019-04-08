package xyz.xkrivzooh.ranklist.support.redis;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.ScoredValue;
import io.lettuce.core.api.async.RedisAsyncCommands;
import xyz.xkrivzooh.ranklist.RankList;
import xyz.xkrivzooh.ranklist.RankListEntity;
import xyz.xkrivzooh.ranklist.common.Bytes;

public class RedisBasedRankList implements RankList {

    private final String identifier;

    private final byte[] identifierBytes;

    private final RedisAsyncCommands<byte[], byte[]> redisAsyncCommands;

    private final ExecutorService executorService;

    public RedisBasedRankList(String identifier, final RedisAsyncCommands<byte[], byte[]> redisAsyncCommands,
            ExecutorService executorService) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(identifier),
                "identifier must not be null");
        Preconditions.checkNotNull(redisAsyncCommands, "redisAsyncCommands must not be null");
        this.identifier = identifier;
        this.identifierBytes = Bytes.of(identifier);
        this.redisAsyncCommands = redisAsyncCommands;
        this.executorService = executorService;
    }

    @Override
    public String identifier() {
        return identifier;
    }

    @Override
    public void vote(String item, double score) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(item), "item must not be null");
        RedisFuture<Double> redisFuture = redisAsyncCommands.zincrby(identifierBytes, score, Bytes.of(item));
        try {
            redisFuture.get();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompletableFuture<Double> viewScore(String item) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(item), "item must not be null");
        RedisFuture<Double> redisFuture = redisAsyncCommands.zscore(identifierBytes, Bytes.of(item));
        CompletableFuture<Double> completableFuture = new CompletableFuture<>();

        redisFuture.thenAcceptAsync(completableFuture::complete, executorService)
                .exceptionally(new Function<Throwable, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable Throwable input) {
                        completableFuture.completeExceptionally(input);
                        return null;
                    }
                });
        return completableFuture;
    }

    @Override
    public CompletableFuture<Long> viewRank(String item) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(item), "item must not be null");
        RedisFuture<Long> redisFuture = redisAsyncCommands.zrevrank(identifierBytes, Bytes.of(item));
        CompletableFuture<Long> completableFuture = new CompletableFuture<>();

        redisFuture.thenAcceptAsync(completableFuture::complete, executorService)
                .exceptionally(new Function<Throwable, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable Throwable input) {
                        completableFuture.completeExceptionally(input);
                        return null;
                    }
                });
        return completableFuture;
    }

    @Override
    public CompletableFuture<List<RankListEntity>> topN(int count) {
        Preconditions.checkArgument(count > 0, "count must great than 0");
        RedisFuture<List<ScoredValue<byte[]>>> redisFuture = redisAsyncCommands.
                zrevrangeWithScores(identifierBytes, 0, count);

        CompletableFuture<List<RankListEntity>> completableFuture = new CompletableFuture<>();
        redisFuture.thenAcceptAsync(scoredValues -> {
            List<RankListEntity> listEntities = Lists.newArrayList();
            for (ScoredValue<byte[]> scoredValue : scoredValues) {
                String item = Bytes.to(scoredValue.getValue());
                RankListEntity entity = new RankListEntity();
                entity.setItem(item);
                entity.setScore(scoredValue.getScore());
                listEntities.add(entity);
            }
            System.out.println("topN" + Thread.currentThread().getName());
            completableFuture.complete(listEntities);
        }, executorService).exceptionally(new Function<Throwable, Void>() {
            @Nullable
            @Override
            public Void apply(@Nullable Throwable throwable) {
                completableFuture.completeExceptionally(throwable);
                return null;
            }
        });

        return completableFuture;
    }

    @Override
    public CompletableFuture<Long> count() {
        RedisFuture<Long> redisFuture = redisAsyncCommands.zcard(identifierBytes);
        CompletableFuture<Long> completableFuture = new CompletableFuture<>();

        redisFuture.thenAcceptAsync(completableFuture::complete).exceptionally(new Function<Throwable, Void>() {
            @Nullable
            @Override
            public Void apply(@Nullable Throwable input) {
                completableFuture.completeExceptionally(input);
                return null;
            }
        });
        return completableFuture;
    }
}
