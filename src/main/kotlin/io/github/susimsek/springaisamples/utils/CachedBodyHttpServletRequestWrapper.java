package io.github.susimsek.springaisamples.utils;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.springframework.util.StreamUtils;

public class CachedBodyHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private byte[] cachedBody;

    public CachedBodyHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        cachedBody = StreamUtils.copyToByteArray(request.getInputStream());
    }

    @Override
    public ServletInputStream getInputStream() {
        return new CachedBodyServletInputStream(this.cachedBody);
    }

    @Override
    public BufferedReader getReader() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cachedBody);
        return new BufferedReader(new InputStreamReader(byteArrayInputStream, StandardCharsets.UTF_8));
    }

    @Override
    public String getCharacterEncoding() {
        String enc = super.getCharacterEncoding();
        return enc != null ? enc : StandardCharsets.UTF_8.name();
    }

    public byte[] getContentAsByteArray() {
        return this.cachedBody;
    }

    public String getContentAsString() {
        return new String(this.cachedBody, Charset.forName(this.getCharacterEncoding()));
    }

    public void setBody(byte[] body) {
        this.cachedBody = body;
    }

    private static class CachedBodyServletInputStream extends ServletInputStream {

        private final ByteArrayInputStream byteArrayInputStream;

        public CachedBodyServletInputStream(byte[] cachedBody) {
            this.byteArrayInputStream = new ByteArrayInputStream(cachedBody);
        }

        @Override
        public boolean isFinished() {
            return byteArrayInputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int read() {
            return byteArrayInputStream.read();
        }
    }
}