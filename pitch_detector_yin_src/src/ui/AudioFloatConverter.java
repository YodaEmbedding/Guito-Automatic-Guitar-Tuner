package ui;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

import javax.sound.sampled.AudioFormat;

public abstract class AudioFloatConverter
{
    private static class AudioFloatConversion16SB extends AudioFloatConverter
    {

        @Override
		public float[] toFloatArray(byte in_buff[], int in_offset, float out_buff[], int out_offset, int out_len)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < out_len; i++)
                out_buff[ox++] = (short)(in_buff[ix++] << 8 | in_buff[ix++] & 0xff) * 3.051851E-05F;

            return out_buff;
        }

        @Override
		public byte[] toByteArray(float in_buff[], int in_offset, int in_len, byte out_buff[], int out_offset)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < in_len; i++)
            {
                int x = (int)(in_buff[ix++] * 32767D);
                out_buff[ox++] = (byte)(x >>> 8);
                out_buff[ox++] = (byte)x;
            }

            return out_buff;
        }

        private AudioFloatConversion16SB()
        {
        }

        AudioFloatConversion16SB(AudioFloatConversion16SB audiofloatconversion16sb)
        {
            this();
        }
    }

    private static class AudioFloatConversion16SL extends AudioFloatConverter
    {

        @Override
		public float[] toFloatArray(byte in_buff[], int in_offset, float out_buff[], int out_offset, int out_len)
        {
            int ix = in_offset;
            int len = out_offset + out_len;
            for(int ox = out_offset; ox < len; ox++)
                out_buff[ox] = (short)(in_buff[ix++] & 0xff | in_buff[ix++] << 8) * 3.051851E-05F;

            return out_buff;
        }

        @Override
		public byte[] toByteArray(float in_buff[], int in_offset, int in_len, byte out_buff[], int out_offset)
        {
            int ox = out_offset;
            int len = in_offset + in_len;
            for(int ix = in_offset; ix < len; ix++)
            {
                int x = (int)(in_buff[ix] * 32767D);
                out_buff[ox++] = (byte)x;
                out_buff[ox++] = (byte)(x >>> 8);
            }

            return out_buff;
        }

        private AudioFloatConversion16SL()
        {
        }

        AudioFloatConversion16SL(AudioFloatConversion16SL audiofloatconversion16sl)
        {
            this();
        }
    }

    private static class AudioFloatConversion16UB extends AudioFloatConverter
    {

        @Override
		public float[] toFloatArray(byte in_buff[], int in_offset, float out_buff[], int out_offset, int out_len)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < out_len; i++)
            {
                int x = (in_buff[ix++] & 0xff) << 8 | in_buff[ix++] & 0xff;
                out_buff[ox++] = (x - 32767) * 3.051851E-05F;
            }

            return out_buff;
        }

        @Override
		public byte[] toByteArray(float in_buff[], int in_offset, int in_len, byte out_buff[], int out_offset)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < in_len; i++)
            {
                int x = 32767 + (int)(in_buff[ix++] * 32767D);
                out_buff[ox++] = (byte)(x >>> 8);
                out_buff[ox++] = (byte)x;
            }

            return out_buff;
        }

        private AudioFloatConversion16UB()
        {
        }

        AudioFloatConversion16UB(AudioFloatConversion16UB audiofloatconversion16ub)
        {
            this();
        }
    }

    private static class AudioFloatConversion16UL extends AudioFloatConverter
    {

        @Override
		public float[] toFloatArray(byte in_buff[], int in_offset, float out_buff[], int out_offset, int out_len)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < out_len; i++)
            {
                int x = in_buff[ix++] & 0xff | (in_buff[ix++] & 0xff) << 8;
                out_buff[ox++] = (x - 32767) * 3.051851E-05F;
            }

            return out_buff;
        }

        @Override
		public byte[] toByteArray(float in_buff[], int in_offset, int in_len, byte out_buff[], int out_offset)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < in_len; i++)
            {
                int x = 32767 + (int)(in_buff[ix++] * 32767D);
                out_buff[ox++] = (byte)x;
                out_buff[ox++] = (byte)(x >>> 8);
            }

            return out_buff;
        }

        private AudioFloatConversion16UL()
        {
        }

        AudioFloatConversion16UL(AudioFloatConversion16UL audiofloatconversion16ul)
        {
            this();
        }
    }

    private static class AudioFloatConversion24SB extends AudioFloatConverter
    {

        @Override
		public float[] toFloatArray(byte in_buff[], int in_offset, float out_buff[], int out_offset, int out_len)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < out_len; i++)
            {
                int x = (in_buff[ix++] & 0xff) << 16 | (in_buff[ix++] & 0xff) << 8 | in_buff[ix++] & 0xff;
                if(x > 0x7fffff)
                    x -= 0x1000000;
                out_buff[ox++] = x * 1.192093E-07F;
            }

            return out_buff;
        }

        @Override
		public byte[] toByteArray(float in_buff[], int in_offset, int in_len, byte out_buff[], int out_offset)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < in_len; i++)
            {
                int x = (int)(in_buff[ix++] * 8388607F);
                if(x < 0)
                    x += 0x1000000;
                out_buff[ox++] = (byte)(x >>> 16);
                out_buff[ox++] = (byte)(x >>> 8);
                out_buff[ox++] = (byte)x;
            }

            return out_buff;
        }

        private AudioFloatConversion24SB()
        {
        }

        AudioFloatConversion24SB(AudioFloatConversion24SB audiofloatconversion24sb)
        {
            this();
        }
    }

    private static class AudioFloatConversion24SL extends AudioFloatConverter
    {

        @Override
		public float[] toFloatArray(byte in_buff[], int in_offset, float out_buff[], int out_offset, int out_len)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < out_len; i++)
            {
                int x = in_buff[ix++] & 0xff | (in_buff[ix++] & 0xff) << 8 | (in_buff[ix++] & 0xff) << 16;
                if(x > 0x7fffff)
                    x -= 0x1000000;
                out_buff[ox++] = x * 1.192093E-07F;
            }

            return out_buff;
        }

        @Override
		public byte[] toByteArray(float in_buff[], int in_offset, int in_len, byte out_buff[], int out_offset)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < in_len; i++)
            {
                int x = (int)(in_buff[ix++] * 8388607F);
                if(x < 0)
                    x += 0x1000000;
                out_buff[ox++] = (byte)x;
                out_buff[ox++] = (byte)(x >>> 8);
                out_buff[ox++] = (byte)(x >>> 16);
            }

            return out_buff;
        }

        private AudioFloatConversion24SL()
        {
        }

        AudioFloatConversion24SL(AudioFloatConversion24SL audiofloatconversion24sl)
        {
            this();
        }
    }

    private static class AudioFloatConversion24UB extends AudioFloatConverter
    {

        @Override
		public float[] toFloatArray(byte in_buff[], int in_offset, float out_buff[], int out_offset, int out_len)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < out_len; i++)
            {
                int x = (in_buff[ix++] & 0xff) << 16 | (in_buff[ix++] & 0xff) << 8 | in_buff[ix++] & 0xff;
                x -= 0x7fffff;
                out_buff[ox++] = x * 1.192093E-07F;
            }

            return out_buff;
        }

        @Override
		public byte[] toByteArray(float in_buff[], int in_offset, int in_len, byte out_buff[], int out_offset)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < in_len; i++)
            {
                int x = (int)(in_buff[ix++] * 8388607F);
                x += 0x7fffff;
                out_buff[ox++] = (byte)(x >>> 16);
                out_buff[ox++] = (byte)(x >>> 8);
                out_buff[ox++] = (byte)x;
            }

            return out_buff;
        }

        private AudioFloatConversion24UB()
        {
        }

        AudioFloatConversion24UB(AudioFloatConversion24UB audiofloatconversion24ub)
        {
            this();
        }
    }

    private static class AudioFloatConversion24UL extends AudioFloatConverter
    {

        @Override
		public float[] toFloatArray(byte in_buff[], int in_offset, float out_buff[], int out_offset, int out_len)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < out_len; i++)
            {
                int x = in_buff[ix++] & 0xff | (in_buff[ix++] & 0xff) << 8 | (in_buff[ix++] & 0xff) << 16;
                x -= 0x7fffff;
                out_buff[ox++] = x * 1.192093E-07F;
            }

            return out_buff;
        }

        @Override
		public byte[] toByteArray(float in_buff[], int in_offset, int in_len, byte out_buff[], int out_offset)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < in_len; i++)
            {
                int x = (int)(in_buff[ix++] * 8388607F);
                x += 0x7fffff;
                out_buff[ox++] = (byte)x;
                out_buff[ox++] = (byte)(x >>> 8);
                out_buff[ox++] = (byte)(x >>> 16);
            }

            return out_buff;
        }

        private AudioFloatConversion24UL()
        {
        }

        AudioFloatConversion24UL(AudioFloatConversion24UL audiofloatconversion24ul)
        {
            this();
        }
    }

    private static class AudioFloatConversion32B extends AudioFloatConverter
    {

        @Override
		public float[] toFloatArray(byte in_buff[], int in_offset, float out_buff[], int out_offset, int out_len)
        {
            int in_len = out_len * 4;
            if(bytebuffer == null || bytebuffer.capacity() < in_len)
            {
                bytebuffer = ByteBuffer.allocate(in_len).order(ByteOrder.BIG_ENDIAN);
                floatbuffer = bytebuffer.asFloatBuffer();
            }
            bytebuffer.position(0);
            floatbuffer.position(0);
            bytebuffer.put(in_buff, in_offset, in_len);
            floatbuffer.get(out_buff, out_offset, out_len);
            return out_buff;
        }

        @Override
		public byte[] toByteArray(float in_buff[], int in_offset, int in_len, byte out_buff[], int out_offset)
        {
            int out_len = in_len * 4;
            if(bytebuffer == null || bytebuffer.capacity() < out_len)
            {
                bytebuffer = ByteBuffer.allocate(out_len).order(ByteOrder.BIG_ENDIAN);
                floatbuffer = bytebuffer.asFloatBuffer();
            }
            floatbuffer.position(0);
            bytebuffer.position(0);
            floatbuffer.put(in_buff, in_offset, in_len);
            bytebuffer.get(out_buff, out_offset, out_len);
            return out_buff;
        }

        ByteBuffer bytebuffer;
        FloatBuffer floatbuffer;

        private AudioFloatConversion32B()
        {
            bytebuffer = null;
            floatbuffer = null;
        }

        AudioFloatConversion32B(AudioFloatConversion32B audiofloatconversion32b)
        {
            this();
        }
    }

    private static class AudioFloatConversion32L extends AudioFloatConverter
    {

        @Override
		public float[] toFloatArray(byte in_buff[], int in_offset, float out_buff[], int out_offset, int out_len)
        {
            int in_len = out_len * 4;
            if(bytebuffer == null || bytebuffer.capacity() < in_len)
            {
                bytebuffer = ByteBuffer.allocate(in_len).order(ByteOrder.LITTLE_ENDIAN);
                floatbuffer = bytebuffer.asFloatBuffer();
            }
            bytebuffer.position(0);
            floatbuffer.position(0);
            bytebuffer.put(in_buff, in_offset, in_len);
            floatbuffer.get(out_buff, out_offset, out_len);
            return out_buff;
        }

        @Override
		public byte[] toByteArray(float in_buff[], int in_offset, int in_len, byte out_buff[], int out_offset)
        {
            int out_len = in_len * 4;
            if(bytebuffer == null || bytebuffer.capacity() < out_len)
            {
                bytebuffer = ByteBuffer.allocate(out_len).order(ByteOrder.LITTLE_ENDIAN);
                floatbuffer = bytebuffer.asFloatBuffer();
            }
            floatbuffer.position(0);
            bytebuffer.position(0);
            floatbuffer.put(in_buff, in_offset, in_len);
            bytebuffer.get(out_buff, out_offset, out_len);
            return out_buff;
        }

        ByteBuffer bytebuffer;
        FloatBuffer floatbuffer;

        private AudioFloatConversion32L()
        {
            bytebuffer = null;
            floatbuffer = null;
        }

        AudioFloatConversion32L(AudioFloatConversion32L audiofloatconversion32l)
        {
            this();
        }
    }

    private static class AudioFloatConversion32SB extends AudioFloatConverter
    {

        @Override
		public float[] toFloatArray(byte in_buff[], int in_offset, float out_buff[], int out_offset, int out_len)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < out_len; i++)
            {
                int x = (in_buff[ix++] & 0xff) << 24 | (in_buff[ix++] & 0xff) << 16 | (in_buff[ix++] & 0xff) << 8 | in_buff[ix++] & 0xff;
                out_buff[ox++] = x * 4.656613E-10F;
            }

            return out_buff;
        }

        @Override
		public byte[] toByteArray(float in_buff[], int in_offset, int in_len, byte out_buff[], int out_offset)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < in_len; i++)
            {
                int x = (int)(in_buff[ix++] * 2.147484E+09F);
                out_buff[ox++] = (byte)(x >>> 24);
                out_buff[ox++] = (byte)(x >>> 16);
                out_buff[ox++] = (byte)(x >>> 8);
                out_buff[ox++] = (byte)x;
            }

            return out_buff;
        }

        private AudioFloatConversion32SB()
        {
        }

        AudioFloatConversion32SB(AudioFloatConversion32SB audiofloatconversion32sb)
        {
            this();
        }
    }

    private static class AudioFloatConversion32SL extends AudioFloatConverter
    {

        @Override
		public float[] toFloatArray(byte in_buff[], int in_offset, float out_buff[], int out_offset, int out_len)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < out_len; i++)
            {
                int x = in_buff[ix++] & 0xff | (in_buff[ix++] & 0xff) << 8 | (in_buff[ix++] & 0xff) << 16 | (in_buff[ix++] & 0xff) << 24;
                out_buff[ox++] = x * 4.656613E-10F;
            }

            return out_buff;
        }

        @Override
		public byte[] toByteArray(float in_buff[], int in_offset, int in_len, byte out_buff[], int out_offset)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < in_len; i++)
            {
                int x = (int)(in_buff[ix++] * 2.147484E+09F);
                out_buff[ox++] = (byte)x;
                out_buff[ox++] = (byte)(x >>> 8);
                out_buff[ox++] = (byte)(x >>> 16);
                out_buff[ox++] = (byte)(x >>> 24);
            }

            return out_buff;
        }

        private AudioFloatConversion32SL()
        {
        }

        AudioFloatConversion32SL(AudioFloatConversion32SL audiofloatconversion32sl)
        {
            this();
        }
    }

    private static class AudioFloatConversion32UB extends AudioFloatConverter
    {

        @Override
		public float[] toFloatArray(byte in_buff[], int in_offset, float out_buff[], int out_offset, int out_len)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < out_len; i++)
            {
                int x = (in_buff[ix++] & 0xff) << 24 | (in_buff[ix++] & 0xff) << 16 | (in_buff[ix++] & 0xff) << 8 | in_buff[ix++] & 0xff;
                x -= 0x7fffffff;
                out_buff[ox++] = x * 4.656613E-10F;
            }

            return out_buff;
        }

        @Override
		public byte[] toByteArray(float in_buff[], int in_offset, int in_len, byte out_buff[], int out_offset)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < in_len; i++)
            {
                int x = (int)(in_buff[ix++] * 2.147484E+09F);
                x += 0x7fffffff;
                out_buff[ox++] = (byte)(x >>> 24);
                out_buff[ox++] = (byte)(x >>> 16);
                out_buff[ox++] = (byte)(x >>> 8);
                out_buff[ox++] = (byte)x;
            }

            return out_buff;
        }

        private AudioFloatConversion32UB()
        {
        }

        AudioFloatConversion32UB(AudioFloatConversion32UB audiofloatconversion32ub)
        {
            this();
        }
    }

    private static class AudioFloatConversion32UL extends AudioFloatConverter
    {

        @Override
		public float[] toFloatArray(byte in_buff[], int in_offset, float out_buff[], int out_offset, int out_len)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < out_len; i++)
            {
                int x = in_buff[ix++] & 0xff | (in_buff[ix++] & 0xff) << 8 | (in_buff[ix++] & 0xff) << 16 | (in_buff[ix++] & 0xff) << 24;
                x -= 0x7fffffff;
                out_buff[ox++] = x * 4.656613E-10F;
            }

            return out_buff;
        }

        @Override
		public byte[] toByteArray(float in_buff[], int in_offset, int in_len, byte out_buff[], int out_offset)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < in_len; i++)
            {
                int x = (int)(in_buff[ix++] * 2.147484E+09F);
                x += 0x7fffffff;
                out_buff[ox++] = (byte)x;
                out_buff[ox++] = (byte)(x >>> 8);
                out_buff[ox++] = (byte)(x >>> 16);
                out_buff[ox++] = (byte)(x >>> 24);
            }

            return out_buff;
        }

        private AudioFloatConversion32UL()
        {
        }

        AudioFloatConversion32UL(AudioFloatConversion32UL audiofloatconversion32ul)
        {
            this();
        }
    }

    private static class AudioFloatConversion32xSB extends AudioFloatConverter
    {

        @Override
		public float[] toFloatArray(byte in_buff[], int in_offset, float out_buff[], int out_offset, int out_len)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < out_len; i++)
            {
                int x = (in_buff[ix++] & 0xff) << 24 | (in_buff[ix++] & 0xff) << 16 | (in_buff[ix++] & 0xff) << 8 | in_buff[ix++] & 0xff;
                ix += xbytes;
                out_buff[ox++] = x * 4.656613E-10F;
            }

            return out_buff;
        }

        @Override
		public byte[] toByteArray(float in_buff[], int in_offset, int in_len, byte out_buff[], int out_offset)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < in_len; i++)
            {
                int x = (int)(in_buff[ix++] * 2.147484E+09F);
                out_buff[ox++] = (byte)(x >>> 24);
                out_buff[ox++] = (byte)(x >>> 16);
                out_buff[ox++] = (byte)(x >>> 8);
                out_buff[ox++] = (byte)x;
                for(int j = 0; j < xbytes; j++)
                    out_buff[ox++] = 0;

            }

            return out_buff;
        }

        final int xbytes;

        public AudioFloatConversion32xSB(int xbytes)
        {
            this.xbytes = xbytes;
        }
    }

    private static class AudioFloatConversion32xSL extends AudioFloatConverter
    {

        @Override
		public float[] toFloatArray(byte in_buff[], int in_offset, float out_buff[], int out_offset, int out_len)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < out_len; i++)
            {
                ix += xbytes;
                int x = in_buff[ix++] & 0xff | (in_buff[ix++] & 0xff) << 8 | (in_buff[ix++] & 0xff) << 16 | (in_buff[ix++] & 0xff) << 24;
                out_buff[ox++] = x * 4.656613E-10F;
            }

            return out_buff;
        }

        @Override
		public byte[] toByteArray(float in_buff[], int in_offset, int in_len, byte out_buff[], int out_offset)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < in_len; i++)
            {
                int x = (int)(in_buff[ix++] * 2.147484E+09F);
                for(int j = 0; j < xbytes; j++)
                    out_buff[ox++] = 0;

                out_buff[ox++] = (byte)x;
                out_buff[ox++] = (byte)(x >>> 8);
                out_buff[ox++] = (byte)(x >>> 16);
                out_buff[ox++] = (byte)(x >>> 24);
            }

            return out_buff;
        }

        final int xbytes;

        public AudioFloatConversion32xSL(int xbytes)
        {
            this.xbytes = xbytes;
        }
    }

    private static class AudioFloatConversion32xUB extends AudioFloatConverter
    {

        @Override
		public float[] toFloatArray(byte in_buff[], int in_offset, float out_buff[], int out_offset, int out_len)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < out_len; i++)
            {
                int x = (in_buff[ix++] & 0xff) << 24 | (in_buff[ix++] & 0xff) << 16 | (in_buff[ix++] & 0xff) << 8 | in_buff[ix++] & 0xff;
                ix += xbytes;
                x -= 0x7fffffff;
                out_buff[ox++] = x * 4.656613E-10F;
            }

            return out_buff;
        }

        @Override
		public byte[] toByteArray(float in_buff[], int in_offset, int in_len, byte out_buff[], int out_offset)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < in_len; i++)
            {
                int x = (int)(in_buff[ix++] * 2147483647D);
                x += 0x7fffffff;
                out_buff[ox++] = (byte)(x >>> 24);
                out_buff[ox++] = (byte)(x >>> 16);
                out_buff[ox++] = (byte)(x >>> 8);
                out_buff[ox++] = (byte)x;
                for(int j = 0; j < xbytes; j++)
                    out_buff[ox++] = 0;

            }

            return out_buff;
        }

        final int xbytes;

        public AudioFloatConversion32xUB(int xbytes)
        {
            this.xbytes = xbytes;
        }
    }

    private static class AudioFloatConversion32xUL extends AudioFloatConverter
    {

        @Override
		public float[] toFloatArray(byte in_buff[], int in_offset, float out_buff[], int out_offset, int out_len)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < out_len; i++)
            {
                ix += xbytes;
                int x = in_buff[ix++] & 0xff | (in_buff[ix++] & 0xff) << 8 | (in_buff[ix++] & 0xff) << 16 | (in_buff[ix++] & 0xff) << 24;
                x -= 0x7fffffff;
                out_buff[ox++] = x * 4.656613E-10F;
            }

            return out_buff;
        }

        @Override
		public byte[] toByteArray(float in_buff[], int in_offset, int in_len, byte out_buff[], int out_offset)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < in_len; i++)
            {
                int x = (int)(in_buff[ix++] * 2.147484E+09F);
                x += 0x7fffffff;
                for(int j = 0; j < xbytes; j++)
                    out_buff[ox++] = 0;

                out_buff[ox++] = (byte)x;
                out_buff[ox++] = (byte)(x >>> 8);
                out_buff[ox++] = (byte)(x >>> 16);
                out_buff[ox++] = (byte)(x >>> 24);
            }

            return out_buff;
        }

        final int xbytes;

        public AudioFloatConversion32xUL(int xbytes)
        {
            this.xbytes = xbytes;
        }
    }

    private static class AudioFloatConversion64B extends AudioFloatConverter
    {

        @Override
		public float[] toFloatArray(byte in_buff[], int in_offset, float out_buff[], int out_offset, int out_len)
        {
            int in_len = out_len * 8;
            if(bytebuffer == null || bytebuffer.capacity() < in_len)
            {
                bytebuffer = ByteBuffer.allocate(in_len).order(ByteOrder.BIG_ENDIAN);
                floatbuffer = bytebuffer.asDoubleBuffer();
            }
            bytebuffer.position(0);
            floatbuffer.position(0);
            bytebuffer.put(in_buff, in_offset, in_len);
            if(double_buff == null || double_buff.length < out_len + out_offset)
                double_buff = new double[out_len + out_offset];
            floatbuffer.get(double_buff, out_offset, out_len);
            int out_offset_end = out_offset + out_len;
            for(int i = out_offset; i < out_offset_end; i++)
                out_buff[i] = (float)double_buff[i];

            return out_buff;
        }

        @Override
		public byte[] toByteArray(float in_buff[], int in_offset, int in_len, byte out_buff[], int out_offset)
        {
            int out_len = in_len * 8;
            if(bytebuffer == null || bytebuffer.capacity() < out_len)
            {
                bytebuffer = ByteBuffer.allocate(out_len).order(ByteOrder.BIG_ENDIAN);
                floatbuffer = bytebuffer.asDoubleBuffer();
            }
            floatbuffer.position(0);
            bytebuffer.position(0);
            if(double_buff == null || double_buff.length < in_offset + in_len)
                double_buff = new double[in_offset + in_len];
            int in_offset_end = in_offset + in_len;
            for(int i = in_offset; i < in_offset_end; i++)
                double_buff[i] = in_buff[i];

            floatbuffer.put(double_buff, in_offset, in_len);
            bytebuffer.get(out_buff, out_offset, out_len);
            return out_buff;
        }

        ByteBuffer bytebuffer;
        DoubleBuffer floatbuffer;
        double double_buff[];

        private AudioFloatConversion64B()
        {
            bytebuffer = null;
            floatbuffer = null;
            double_buff = null;
        }

        AudioFloatConversion64B(AudioFloatConversion64B audiofloatconversion64b)
        {
            this();
        }
    }

    private static class AudioFloatConversion64L extends AudioFloatConverter
    {

        @Override
		public float[] toFloatArray(byte in_buff[], int in_offset, float out_buff[], int out_offset, int out_len)
        {
            int in_len = out_len * 8;
            if(bytebuffer == null || bytebuffer.capacity() < in_len)
            {
                bytebuffer = ByteBuffer.allocate(in_len).order(ByteOrder.LITTLE_ENDIAN);
                floatbuffer = bytebuffer.asDoubleBuffer();
            }
            bytebuffer.position(0);
            floatbuffer.position(0);
            bytebuffer.put(in_buff, in_offset, in_len);
            if(double_buff == null || double_buff.length < out_len + out_offset)
                double_buff = new double[out_len + out_offset];
            floatbuffer.get(double_buff, out_offset, out_len);
            int out_offset_end = out_offset + out_len;
            for(int i = out_offset; i < out_offset_end; i++)
                out_buff[i] = (float)double_buff[i];

            return out_buff;
        }

        @Override
		public byte[] toByteArray(float in_buff[], int in_offset, int in_len, byte out_buff[], int out_offset)
        {
            int out_len = in_len * 8;
            if(bytebuffer == null || bytebuffer.capacity() < out_len)
            {
                bytebuffer = ByteBuffer.allocate(out_len).order(ByteOrder.LITTLE_ENDIAN);
                floatbuffer = bytebuffer.asDoubleBuffer();
            }
            floatbuffer.position(0);
            bytebuffer.position(0);
            if(double_buff == null || double_buff.length < in_offset + in_len)
                double_buff = new double[in_offset + in_len];
            int in_offset_end = in_offset + in_len;
            for(int i = in_offset; i < in_offset_end; i++)
                double_buff[i] = in_buff[i];

            floatbuffer.put(double_buff, in_offset, in_len);
            bytebuffer.get(out_buff, out_offset, out_len);
            return out_buff;
        }

        ByteBuffer bytebuffer;
        DoubleBuffer floatbuffer;
        double double_buff[];

        private AudioFloatConversion64L()
        {
            bytebuffer = null;
            floatbuffer = null;
            double_buff = null;
        }

        AudioFloatConversion64L(AudioFloatConversion64L audiofloatconversion64l)
        {
            this();
        }
    }

    private static class AudioFloatConversion8S extends AudioFloatConverter
    {

        @Override
		public float[] toFloatArray(byte in_buff[], int in_offset, float out_buff[], int out_offset, int out_len)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < out_len; i++)
                out_buff[ox++] = in_buff[ix++] * 0.007874016F;

            return out_buff;
        }

        @Override
		public byte[] toByteArray(float in_buff[], int in_offset, int in_len, byte out_buff[], int out_offset)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < in_len; i++)
                out_buff[ox++] = (byte)(int)(in_buff[ix++] * 127F);

            return out_buff;
        }

        private AudioFloatConversion8S()
        {
        }

        AudioFloatConversion8S(AudioFloatConversion8S audiofloatconversion8s)
        {
            this();
        }
    }

    private static class AudioFloatConversion8U extends AudioFloatConverter
    {

        @Override
		public float[] toFloatArray(byte in_buff[], int in_offset, float out_buff[], int out_offset, int out_len)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < out_len; i++)
                out_buff[ox++] = ((in_buff[ix++] & 0xff) - 127) * 0.007874016F;

            return out_buff;
        }

        @Override
		public byte[] toByteArray(float in_buff[], int in_offset, int in_len, byte out_buff[], int out_offset)
        {
            int ix = in_offset;
            int ox = out_offset;
            for(int i = 0; i < in_len; i++)
                out_buff[ox++] = (byte)(int)(127F + in_buff[ix++] * 127F);

            return out_buff;
        }

        private AudioFloatConversion8U()
        {
        }

        AudioFloatConversion8U(AudioFloatConversion8U audiofloatconversion8u)
        {
            this();
        }
    }

    private static class AudioFloatLSBFilter extends AudioFloatConverter
    {

        @Override
		public byte[] toByteArray(float in_buff[], int in_offset, int in_len, byte out_buff[], int out_offset)
        {
            byte ret[] = converter.toByteArray(in_buff, in_offset, in_len, out_buff, out_offset);
            int out_offset_end = in_len * stepsize;
            for(int i = out_offset + offset; i < out_offset_end; i += stepsize)
                out_buff[i] = (byte)(out_buff[i] & mask);

            return ret;
        }

        @Override
		public float[] toFloatArray(byte in_buff[], int in_offset, float out_buff[], int out_offset, int out_len)
        {
            if(mask_buffer == null || mask_buffer.length < in_buff.length)
                mask_buffer = new byte[in_buff.length];
            System.arraycopy(in_buff, 0, mask_buffer, 0, in_buff.length);
            int in_offset_end = out_len * stepsize;
            for(int i = in_offset + offset; i < in_offset_end; i += stepsize)
                mask_buffer[i] = (byte)(mask_buffer[i] & mask);

            float ret[] = converter.toFloatArray(mask_buffer, in_offset, out_buff, out_offset, out_len);
            return ret;
        }

        private final AudioFloatConverter converter;
        private final int offset;
        private final int stepsize;
        private final byte mask;
        private byte mask_buffer[];

        public AudioFloatLSBFilter(AudioFloatConverter converter, AudioFormat format)
        {
            int bits = format.getSampleSizeInBits();
            boolean bigEndian = format.isBigEndian();
            this.converter = converter;
            stepsize = (bits + 7) / 8;
            offset = bigEndian ? stepsize - 1 : 0;
            int lsb_bits = bits % 8;
            if(lsb_bits == 0)
                mask = 0;
            else
            if(lsb_bits == 1)
                mask = -128;
            else
            if(lsb_bits == 2)
                mask = -64;
            else
            if(lsb_bits == 3)
                mask = -32;
            else
            if(lsb_bits == 4)
                mask = -16;
            else
            if(lsb_bits == 5)
                mask = -8;
            else
            if(lsb_bits == 6)
                mask = -4;
            else
            if(lsb_bits == 7)
                mask = -2;
            else
                mask = -1;
        }
    }


    public AudioFloatConverter()
    {
    }

    public static AudioFloatConverter getConverter(AudioFormat format)
    {
        AudioFloatConverter conv = null;
        if(format.getFrameSize() == 0)
            return null;
        if(format.getFrameSize() != ((format.getSampleSizeInBits() + 7) / 8) * format.getChannels())
            return null;
        if(format.getEncoding().equals(javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED))
        {
            if(format.isBigEndian())
            {
                if(format.getSampleSizeInBits() <= 8)
                    conv = new AudioFloatConversion8S(null);
                else
                if(format.getSampleSizeInBits() > 8 && format.getSampleSizeInBits() <= 16)
                    conv = new AudioFloatConversion16SB(null);
                else
                if(format.getSampleSizeInBits() > 16 && format.getSampleSizeInBits() <= 24)
                    conv = new AudioFloatConversion24SB(null);
                else
                if(format.getSampleSizeInBits() > 24 && format.getSampleSizeInBits() <= 32)
                    conv = new AudioFloatConversion32SB(null);
                else
                if(format.getSampleSizeInBits() > 32)
                    conv = new AudioFloatConversion32xSB((format.getSampleSizeInBits() + 7) / 8 - 4);
            } else
            if(format.getSampleSizeInBits() <= 8)
                conv = new AudioFloatConversion8S(null);
            else
            if(format.getSampleSizeInBits() > 8 && format.getSampleSizeInBits() <= 16)
                conv = new AudioFloatConversion16SL(null);
            else
            if(format.getSampleSizeInBits() > 16 && format.getSampleSizeInBits() <= 24)
                conv = new AudioFloatConversion24SL(null);
            else
            if(format.getSampleSizeInBits() > 24 && format.getSampleSizeInBits() <= 32)
                conv = new AudioFloatConversion32SL(null);
            else
            if(format.getSampleSizeInBits() > 32)
                conv = new AudioFloatConversion32xSL((format.getSampleSizeInBits() + 7) / 8 - 4);
        } else
        if(format.getEncoding().equals(javax.sound.sampled.AudioFormat.Encoding.PCM_UNSIGNED))
        {
            if(format.isBigEndian())
            {
                if(format.getSampleSizeInBits() <= 8)
                    conv = new AudioFloatConversion8U(null);
                else
                if(format.getSampleSizeInBits() > 8 && format.getSampleSizeInBits() <= 16)
                    conv = new AudioFloatConversion16UB(null);
                else
                if(format.getSampleSizeInBits() > 16 && format.getSampleSizeInBits() <= 24)
                    conv = new AudioFloatConversion24UB(null);
                else
                if(format.getSampleSizeInBits() > 24 && format.getSampleSizeInBits() <= 32)
                    conv = new AudioFloatConversion32UB(null);
                else
                if(format.getSampleSizeInBits() > 32)
                    conv = new AudioFloatConversion32xUB((format.getSampleSizeInBits() + 7) / 8 - 4);
            } else
            if(format.getSampleSizeInBits() <= 8)
                conv = new AudioFloatConversion8U(null);
            else
            if(format.getSampleSizeInBits() > 8 && format.getSampleSizeInBits() <= 16)
                conv = new AudioFloatConversion16UL(null);
            else
            if(format.getSampleSizeInBits() > 16 && format.getSampleSizeInBits() <= 24)
                conv = new AudioFloatConversion24UL(null);
            else
            if(format.getSampleSizeInBits() > 24 && format.getSampleSizeInBits() <= 32)
                conv = new AudioFloatConversion32UL(null);
            else
            if(format.getSampleSizeInBits() > 32)
                conv = new AudioFloatConversion32xUL((format.getSampleSizeInBits() + 7) / 8 - 4);
        } else
        if(format.getEncoding().equals(PCM_FLOAT))
            if(format.getSampleSizeInBits() == 32)
            {
                if(format.isBigEndian())
                    conv = new AudioFloatConversion32B(null);
                else
                    conv = new AudioFloatConversion32L(null);
            } else
            if(format.getSampleSizeInBits() == 64)
                if(format.isBigEndian())
                    conv = new AudioFloatConversion64B(null);
                else
                    conv = new AudioFloatConversion64L(null);
        if((format.getEncoding().equals(javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED) || format.getEncoding().equals(javax.sound.sampled.AudioFormat.Encoding.PCM_UNSIGNED)) && format.getSampleSizeInBits() % 8 != 0)
            conv = new AudioFloatLSBFilter(conv, format);
        if(conv != null)
            conv.format = format;
        return conv;
    }

    public AudioFormat getFormat()
    {
        return format;
    }

    public abstract float[] toFloatArray(byte abyte0[], int i, float af[], int j, int k);

    public float[] toFloatArray(byte in_buff[], float out_buff[], int out_offset, int out_len)
    {
        return toFloatArray(in_buff, 0, out_buff, out_offset, out_len);
    }

    public float[] toFloatArray(byte in_buff[], int in_offset, float out_buff[], int out_len)
    {
        return toFloatArray(in_buff, in_offset, out_buff, 0, out_len);
    }

    public float[] toFloatArray(byte in_buff[], float out_buff[], int out_len)
    {
        return toFloatArray(in_buff, 0, out_buff, 0, out_len);
    }

    public float[] toFloatArray(byte in_buff[], float out_buff[])
    {
        return toFloatArray(in_buff, 0, out_buff, 0, out_buff.length);
    }

    public abstract byte[] toByteArray(float af[], int i, int j, byte abyte0[], int k);

    public byte[] toByteArray(float in_buff[], int in_len, byte out_buff[], int out_offset)
    {
        return toByteArray(in_buff, 0, in_len, out_buff, out_offset);
    }

    public byte[] toByteArray(float in_buff[], int in_offset, int in_len, byte out_buff[])
    {
        return toByteArray(in_buff, in_offset, in_len, out_buff, 0);
    }

    public byte[] toByteArray(float in_buff[], int in_len, byte out_buff[])
    {
        return toByteArray(in_buff, 0, in_len, out_buff, 0);
    }

    public byte[] toByteArray(float in_buff[], byte out_buff[])
    {
        return toByteArray(in_buff, 0, in_buff.length, out_buff, 0);
    }

    public static final javax.sound.sampled.AudioFormat.Encoding PCM_FLOAT = new javax.sound.sampled.AudioFormat.Encoding("PCM_FLOAT");
    private AudioFormat format;

}
