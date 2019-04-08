package xyz.xkrivzooh.ranklist.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO SPI
public class GzipCompressor {

	private static final Logger logger = LoggerFactory.getLogger(GzipCompressor.class);

	public byte[] compress(Object object) {
		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream o = new ObjectOutputStream(bo);
			o.writeObject(object);
			o.flush();
			return compress(bo.toByteArray());
		}
		catch (Exception var4) {
			logger.error("compress obj error", var4);
			throw Throwables.propagate(var4);
		}
	}

	public Object decompress(byte[] arr) {
		try {
			byte[] src = gzipDecompress(arr);
			ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(src));
			return inputStream.readObject();
		}
		catch (Exception var4) {
			logger.warn("decompress arr error", var4);
			throw new RuntimeException(var4);
		}
	}

	private static byte[] compress(byte[] data) {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			compress(bais, baos);
			return baos.toByteArray();
		}
		catch (IOException var7) {
			logger.error("", var7);
		}
		finally {
			close(bais, baos);
		}
		return null;
	}

	private static byte[] gzipDecompress(byte[] data) {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			decompress(bais, baos);
			return baos.toByteArray();
		}
		catch (IOException var7) {
			logger.error("", var7);
		}
		finally {
			close(bais, baos);
		}
		return null;
	}

	private static void compress(InputStream is, OutputStream os) throws IOException {
		GZIPOutputStream gos = null;
		try {
			gos = new GZIPOutputStream(os);
			byte[] data = new byte[1024];

			int count;
			while ((count = is.read(data, 0, 1024)) != -1) {
				gos.write(data, 0, count);
			}
		}
		finally {
			close(gos);
		}
	}

	private static void decompress(InputStream is, OutputStream os) throws IOException {
		GZIPInputStream gis = null;
		try {
			gis = new GZIPInputStream(is);
			byte[] data = new byte[1024];
			int count;
			while ((count = gis.read(data, 0, 1024)) != -1) {
				os.write(data, 0, count);
			}
		}
		finally {
			close(gis);
		}
	}

	public static void close(Closeable... cs) {
		if (cs == null) {
			return;
		}
		for (Closeable c : cs) {
			if (c != null) {
				try {
					c.close();
				}
				catch (Throwable var6) {

				}
			}
		}
	}

}
