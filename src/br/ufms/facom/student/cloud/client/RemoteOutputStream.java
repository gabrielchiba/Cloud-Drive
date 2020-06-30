package br.ufms.facom.student.cloud.client;

import br.ufms.facom.student.cloud.rmi.Drive;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class RemoteOutputStream extends OutputStream implements Closeable {
    private static final int BUFFER_SIZE = 1 * 1024576;

    private Drive mDrive;
    private String mFilename;
    private int mPosition;

    private byte[] mBuffer;
    private int mBufferPosition;

    public RemoteOutputStream(Drive drive, String filename) {
        mDrive = drive;
        mFilename = filename;
        mPosition = 0;

        mBuffer = new byte[BUFFER_SIZE];
        mBufferPosition = 0;
    }

    @Override
    public void close() throws IOException {
        super.close();
        flushBuffer();
    }

    private void flushBuffer() throws IOException {
        mDrive.putChunk(mFilename, mPosition, Arrays.copyOf(mBuffer, mBufferPosition));
        mBufferPosition = 0;

        mPosition += mBufferPosition;
    }

    @Override
    public void write(int i) throws IOException {
        if (mBufferPosition >= mBuffer.length) { // Buffer full?
            flushBuffer();
        }

        mBuffer[mBufferPosition] = (byte) i;
        mBufferPosition++;
    }
}
