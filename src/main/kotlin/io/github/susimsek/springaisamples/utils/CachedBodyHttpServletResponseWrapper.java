package io.github.susimsek.springaisamples.utils;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class CachedBodyHttpServletResponseWrapper extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream cachedBody;
    private final ServletOutputStream outputStream;
    private final PrintWriter writer;
    private final HttpServletResponse originalResponse;

    public CachedBodyHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
        originalResponse = response;
        cachedBody = new ByteArrayOutputStream();
        outputStream = new CachedBodyServletOutputStream();
        writer = new PrintWriter(cachedBody, true, StandardCharsets.UTF_8);
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() {
        return writer;
    }

    public byte[] getBody() {
        writer.flush();
        return cachedBody.toByteArray();
    }

    public void copyBodyToResponse() throws IOException {
        ServletOutputStream responseOutputStream = originalResponse.getOutputStream();
        responseOutputStream.write(getBody());
        responseOutputStream.flush();
    }

    private class CachedBodyServletOutputStream extends ServletOutputStream {
        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener listener) {
        }

        @Override
        public void write(int b) throws IOException {
            cachedBody.write(b);
        }
    }
}