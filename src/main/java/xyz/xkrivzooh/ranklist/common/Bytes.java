package xyz.xkrivzooh.ranklist.common;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import io.netty.buffer.Unpooled;

public class Bytes {

    public static byte[] of(String str) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(str), "input must not be empty");
        return str.getBytes(Charsets.UTF_8);
    }

    public static String to(byte[] bytes) {
        Preconditions.checkNotNull(bytes, "bytes must not be null");
        return Unpooled.wrappedBuffer(bytes).toString(Charsets.UTF_8);
    }
}
