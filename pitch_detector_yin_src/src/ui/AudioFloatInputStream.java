package ui;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ShortBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;



// Referenced classes of package com.sun.media.sound:
//            AudioFloatConverter

public abstract class AudioFloatInputStream
{
    private static class BytaArrayAudioFloatInputStream extends AudioFloatInputStream
    {

        @Override
		public AudioFormat getFormat()
        {
            return format;
        }

        @Override
		public long getFrameLength()
        {
            return buffer_len;
        }

        @Override
		public int read(float b[], int off, int len)
            throws IOException
        {
            if(b == null)
                throw new NullPointerException();
            if(off < 0 || len < 0 || len > b.length - off)
                throw new IndexOutOfBoundsException();
            if(pos >= buffer_len)
                return -1;
            if(len == 0)
                return 0;
            if(pos + len > buffer_len)
                len = buffer_len - pos;
            converter.toFloatArray(buffer, buffer_offset + pos * framesize_pc, b, off, len);
            pos += len;
            return len;
        }

        @Override
		public long skip(long len)
            throws IOException
        {
            if(pos >= buffer_len)
                return -1L;
            if(len <= 0L)
                return 0L;
            if(pos + len > buffer_len)
                len = buffer_len - pos;
            pos += len;
            return len;
        }

        @Override
		public int available()
            throws IOException
        {
            return buffer_len - pos;
        }

        @Override
		public void close()
            throws IOException
        {
        }

        @Override
		public void mark(int readlimit)
        {
            markpos = pos;
        }

        @Override
		public boolean markSupported()
        {
            return true;
        }

        @Override
		public void reset()
            throws IOException
        {
            pos = markpos;
        }

        private int pos;
        private int markpos;
        private final AudioFloatConverter converter;
        private final AudioFormat format;
        private final byte buffer[];
        private final int buffer_offset;
        private final int buffer_len;
        private final int framesize_pc;

        public BytaArrayAudioFloatInputStream(AudioFloatConverter converter, byte buffer[], int offset, int len)
        {
            pos = 0;
            markpos = 0;
            this.converter = converter;
            format = converter.getFormat();
            this.buffer = buffer;
            buffer_offset = offset;
            framesize_pc = format.getFrameSize() / format.getChannels();
            buffer_len = len / framesize_pc;
        }
    }

    private static class DirectAudioFloatInputStream extends AudioFloatInputStream
    {

        @Override
		public AudioFormat getFormat()
        {
            return stream.getFormat();
        }

        @Override
		public long getFrameLength()
        {
            return stream.getFrameLength();
        }

        @Override
		public int read(float b[], int off, int len)
            throws IOException
        {
            int b_len = len * framesize_pc;
            if(buffer == null || buffer.length < b_len)
                buffer = new byte[b_len];
            int ret = stream.read(buffer, 0, b_len);
            if(ret == -1)
            {
                return -1;
            } else
            {
                converter.toFloatArray(buffer, b, off, ret / framesize_pc);
                return ret / framesize_pc;
            }
        }

        @Override
		public long skip(long len)
            throws IOException
        {
            long b_len = len * framesize_pc;
            long ret = stream.skip(b_len);
            if(ret == -1L)
                return -1L;
            else
                return ret / framesize_pc;
        }

        @Override
		public int available()
            throws IOException
        {
            return stream.available() / framesize_pc;
        }

        @Override
		public void close()
            throws IOException
        {
            stream.close();
        }

        @Override
		public void mark(int readlimit)
        {
            stream.mark(readlimit * framesize_pc);
        }

        @Override
		public boolean markSupported()
        {
            return stream.markSupported();
        }

        @Override
		public void reset()
            throws IOException
        {
            stream.reset();
        }

        private final AudioInputStream stream;
        private AudioFloatConverter converter;
        private final int framesize_pc;
        private byte buffer[];

        public DirectAudioFloatInputStream(AudioInputStream stream)
        {
            converter = AudioFloatConverter.getConverter(stream.getFormat());
            if(converter == null)
            {
                AudioFormat format = stream.getFormat();
                AudioFormat formats[] = AudioSystem.getTargetFormats(javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED, format);
                AudioFormat newformat;
                if(formats.length != 0)
                {
                    newformat = formats[0];
                } else
                {
                    float samplerate = format.getSampleRate();
                    int samplesizeinbits = format.getSampleSizeInBits();
                    int framesize = format.getFrameSize();
                    float framerate = format.getFrameRate();
                    samplesizeinbits = 16;
                    framesize = format.getChannels() * (samplesizeinbits / 8);
                    framerate = samplerate;
                    newformat = new AudioFormat(javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED, samplerate, samplesizeinbits, format.getChannels(), framesize, framerate, false);
                }
                stream = AudioSystem.getAudioInputStream(newformat, stream);
                converter = AudioFloatConverter.getConverter(stream.getFormat());
            }
            framesize_pc = stream.getFormat().getFrameSize() / stream.getFormat().getChannels();
            this.stream = stream;
        }
    }

    private static class ShortArrayAudioFloatInputStream extends AudioFloatInputStream
    {

        @Override
		public AudioFormat getFormat()
        {
            return format;
        }

        @Override
		public long getFrameLength()
        {
            return buffer_len;
        }

        @Override
		public int read(float b[], int off, int len)
            throws IOException
        {
            if(b == null)
                throw new NullPointerException();
            if(off < 0 || len < 0 || len > b.length - off)
                throw new IndexOutOfBoundsException();
            if(pos >= buffer_len)
                return -1;
            if(len == 0)
                return 0;
            if(pos + len > buffer_len)
                len = buffer_len - pos;
            int i = pos + buffer_offset;
            int endoffset = off + len;
            if(normalized)
            {
                for(int o = off; o < endoffset; o++)
                    b[o] = buffer[i++] * 3.051851E-05F;

            } else
            {
                for(int o = off; o < endoffset; o++)
                    b[o] = buffer[i++];

            }
            pos += len;
            return len;
        }

        @Override
		public float getSampleScale()
        {
            return !normalized ? 32767F : 1.0F;
        }

        @Override
		public long skip(long len)
            throws IOException
        {
            if(pos >= buffer_len)
                return -1L;
            if(len <= 0L)
                return 0L;
            if(pos + len > buffer_len)
                len = buffer_len - pos;
            pos += len;
            return len;
        }

        @Override
		public int available()
            throws IOException
        {
            return buffer_len - pos;
        }

        @Override
		public void close()
            throws IOException
        {
        }

        @Override
		public void mark(int readlimit)
        {
            markpos = pos;
        }

        @Override
		public boolean markSupported()
        {
            return true;
        }

        @Override
		public void reset()
            throws IOException
        {
            pos = markpos;
        }

        private int pos;
        private int markpos;
        private final AudioFormat format;
        private final short buffer[];
        private final int buffer_offset;
        private final int buffer_len;

        public ShortArrayAudioFloatInputStream(AudioFormat format, short buffer[], int offset, int len)
        {
            pos = 0;
            markpos = 0;
            this.format = format;
            this.buffer = buffer;
            buffer_offset = offset;
            buffer_len = len;
        }
    }

    private static class ShortBufferFloatInputStream extends AudioFloatInputStream
    {

        @Override
		public int available()
            throws IOException
        {
            return buffer.limit() - buffer.position();
        }

        @Override
		public void close()
            throws IOException
        {
        }

        @Override
		public AudioFormat getFormat()
        {
            return format;
        }

        @Override
		public long getFrameLength()
        {
            return buffer.limit();
        }

        @Override
		public void mark(int readlimit)
        {
            buffer.mark();
        }

        @Override
		public boolean markSupported()
        {
            return true;
        }

        @Override
		public float getSampleScale()
        {
            return !normalized ? 32767F : 1.0F;
        }

        @Override
		public int read(float b[], int off, int len)
            throws IOException
        {
            int avail = available();
            if(avail <= 0)
                return -1;
            if(len > avail)
                len = avail;
            if(shortbuff == null || shortbuff.length < len)
                shortbuff = new short[off + len];
            buffer.get(shortbuff, off, len);
            int offlen = off + len;
            if(normalized)
            {
                for(int i = off; i < offlen; i++)
                    b[i] = shortbuff[i] * 3.051851E-05F;

            } else
            {
                for(int i = off; i < offlen; i++)
                    b[i] = shortbuff[i];

            }
            return len;
        }

        @Override
		public void reset()
            throws IOException
        {
            buffer.reset();
        }

        @Override
		public long skip(long len)
            throws IOException
        {
            int avail = available();
            if(avail <= 0)
                return -1L;
            if(len > avail)
                len = avail;
            buffer.position(buffer.position() + (int)len);
            return len;
        }

        AudioFormat format;
        ShortBuffer buffer;
        short shortbuff[];

        public ShortBufferFloatInputStream(AudioFormat format, ShortBuffer buffer)
        {
            this.buffer = buffer;
            this.format = format;
        }
    }


    public AudioFloatInputStream()
    {
        normalized = true;
    }

    public static AudioFloatInputStream getInputStream(URL url)
        throws UnsupportedAudioFileException, IOException
    {
        return new DirectAudioFloatInputStream(AudioSystem.getAudioInputStream(url));
    }

    public static AudioFloatInputStream getInputStream(File file)
        throws UnsupportedAudioFileException, IOException
    {
        return new DirectAudioFloatInputStream(AudioSystem.getAudioInputStream(file));
    }

    public static AudioFloatInputStream getInputStream(InputStream stream)
        throws UnsupportedAudioFileException, IOException
    {
        return new DirectAudioFloatInputStream(AudioSystem.getAudioInputStream(stream));
    }

    public static AudioFloatInputStream getInputStream(AudioInputStream stream)
    {
        return new DirectAudioFloatInputStream(stream);
    }

    public static AudioFloatInputStream getInputStream(AudioFormat format, ShortBuffer buffer)
    {
        return new ShortBufferFloatInputStream(format, buffer);
    }

    public static AudioFloatInputStream getInputStream(AudioFormat format, byte buffer[], int offset, int len)
    {
        AudioFloatConverter converter = AudioFloatConverter.getConverter(format);
        if(converter != null)
        {
            return new BytaArrayAudioFloatInputStream(converter, buffer, offset, len);
        } else
        {
            InputStream stream = new ByteArrayInputStream(buffer, offset, len);
            long aLen = format.getFrameSize() != -1 ? len / format.getFrameSize() : -1;
            AudioInputStream astream = new AudioInputStream(stream, format, aLen);
            return getInputStream(astream);
        }
    }

    public static AudioFloatInputStream getInputStream(AudioFormat format, short buffer[], int offset, int len)
    {
        return new ShortArrayAudioFloatInputStream(format, buffer, offset, len);
    }

    public float getSampleScale()
    {
        return 1.0F;
    }

    public void setNormalized(boolean normalized)
    {
        this.normalized = normalized;
    }

    public boolean isNormalized()
    {
        return normalized;
    }

    public abstract AudioFormat getFormat();

    public abstract long getFrameLength();

    public abstract int read(float af[], int i, int j)
        throws IOException;

    public int read(float b[])
        throws IOException
    {
        return read(b, 0, b.length);
    }

    public float read()
        throws IOException
    {
        float b[] = new float[1];
        int ret = read(b, 0, 1);
        if(ret == -1 || ret == 0)
            return 0.0F;
        else
            return b[0];
    }

    public abstract long skip(long l)
        throws IOException;

    public abstract int available()
        throws IOException;

    public abstract void close()
        throws IOException;

    public abstract void mark(int i);

    public abstract boolean markSupported();

    public abstract void reset()
        throws IOException;

    protected boolean normalized;
}
