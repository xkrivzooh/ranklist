package xyz.xkrivzooh.ranklist.support.redis;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.codec.ByteArrayCodec;

public class RedisClients {

    private final static LoadingCache<RedisURI, RedisClient> REDIS_CLIENT_CACHE = CacheBuilder.newBuilder()
            .build(new CacheLoader<RedisURI, RedisClient>() {
                @Override
                public RedisClient load(RedisURI redisURI) {
                    return RedisClient.create(redisURI);
                }
            });

    private final static LoadingCache<RedisURI, RedisAsyncCommands<byte[], byte[]>> ASYNC_COMMANDS_CACHE =
            CacheBuilder.newBuilder().build(new CacheLoader<RedisURI, RedisAsyncCommands<byte[], byte[]>>() {
                @Override
                public RedisAsyncCommands<byte[], byte[]> load(RedisURI redisURI) {
                    RedisClient client = REDIS_CLIENT_CACHE.getUnchecked(redisURI);
                    return client.connect(new ByteArrayCodec()).async();
                }
            });


    public static RedisClient redisClient(RedisURI redisURI) {
        return REDIS_CLIENT_CACHE.getUnchecked(redisURI);
    }

    public static RedisAsyncCommands<byte[], byte[]> asyncCommands(RedisURI redisURI) {
        return ASYNC_COMMANDS_CACHE.getUnchecked(redisURI);
    }

    public static void shutdownNow() {
        ASYNC_COMMANDS_CACHE.asMap().values().parallelStream().forEach(command -> command.getStatefulConnection().close());
        REDIS_CLIENT_CACHE.asMap().values().parallelStream().forEach(AbstractRedisClient::shutdown);
    }

}
