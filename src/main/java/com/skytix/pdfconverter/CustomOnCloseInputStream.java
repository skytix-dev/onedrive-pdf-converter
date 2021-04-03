package com.skytix.pdfconverter;

import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.requests.GraphServiceClient;
import okhttp3.Request;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CustomOnCloseInputStream extends InputStream {
    private final InputStream wrappedStream;
    private final CloseableHandle closeHandle;

    public interface CloseableHandle {
        void close();
    }

    CustomOnCloseInputStream(InputStream aWrappedStream, CloseableHandle aCloseableHandle) {
        wrappedStream = aWrappedStream;
        closeHandle = aCloseableHandle;
    }

    @Override
    public void close() throws IOException {
        wrappedStream.close();
        closeHandle.close();
    }

    @Override
    public int read() throws IOException {
        return wrappedStream.read();
    }

    @Override
    public int read(@NotNull byte[] b) throws IOException {
        return wrappedStream.read(b);
    }

    @Override
    public int read(@NotNull byte[] b, int off, int len) throws IOException {
        return wrappedStream.read(b, off, len);
    }

    @Override
    public byte[] readAllBytes() throws IOException {
        return wrappedStream.readAllBytes();
    }

    @Override
    public byte[] readNBytes(int len) throws IOException {
        return wrappedStream.readNBytes(len);
    }

    @Override
    public int readNBytes(byte[] b, int off, int len) throws IOException {
        return wrappedStream.readNBytes(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return wrappedStream.skip(n);
    }

    @Override
    public void skipNBytes(long n) throws IOException {
        wrappedStream.skipNBytes(n);
    }

    @Override
    public int available() throws IOException {
        return wrappedStream.available();
    }

    @Override
    public void mark(int readlimit) {
        wrappedStream.mark(readlimit);
    }

    @Override
    public void reset() throws IOException {
        wrappedStream.reset();
    }

    @Override
    public boolean markSupported() {
        return wrappedStream.markSupported();
    }

    @Override
    public long transferTo(OutputStream out) throws IOException {
        return wrappedStream.transferTo(out);
    }
}
