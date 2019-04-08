package xyz.xkrivzooh.ranklist.support.redis;

import java.util.concurrent.Executors;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import io.lettuce.core.api.async.RedisAsyncCommands;

//TODO SPI
public class RedisBasedRankListMaker {

    private final RedisAsyncCommands<byte[], byte[]> redisAsyncCommands;

    public RedisBasedRankListMaker(RedisAsyncCommands<byte[], byte[]> redisAsyncCommands) {
        this.redisAsyncCommands = redisAsyncCommands;
    }

    public RedisBasedRankList make(String rankListIdentifier) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(rankListIdentifier),
                "rankListIdentifier must not be null");
        return new RedisBasedRankList(rankListIdentifier, redisAsyncCommands, Executors.newCachedThreadPool());
    }
}
