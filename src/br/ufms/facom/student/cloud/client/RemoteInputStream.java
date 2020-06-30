package br.ufms.facom.student.cloud.client;

import br.ufms.facom.student.cloud.rmi.Drive;

import java.io.IOException;
import java.io.InputStream;

public class RemoteInputStream extends InputStream {
    private static final int BUFFER_SIZE = 4 * 1048576;

    private Drive mDrive;
    private String mFilename;
    private long mPosition;

    private byte[] mBuffer;
    private int mBufferPosition;

    public RemoteInputStream(Drive drive, String filename) {
        mDrive = drive;
        mFilename = filename;
        mPosition = 0;

        mBuffer = null;
        mBufferPosition = 0;
    }

    private void updateBuffer() throws IOException {
        if (mBuffer == null) {
            mPosition += mBufferPosition;
            mBuffer = mDrive.getChunk(mFilename, mPosition, BUFFER_SIZE);
            mBufferPosition = 0;
        }
    }

    @Override
    public int read() throws IOException {
        updateBuffer();

        if (mBuffer == null) { // null even after update means EOF
            return -1;
        }

        int data = mBuffer[mBufferPosition];
        mBufferPosition++;

        if (mBufferPosition >= mBuffer.length) {
            // Buffer fully read: invalidate
            mBuffer = null;
        }

        return data & 0xFF;
    }
}
