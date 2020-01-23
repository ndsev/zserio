package bitmask_types;

import java.util.BitSet;
//import zserio.runtime.io.BitStreamReader;

public class Emotions
{
    public Emotions()
    {
        this((short)0);
    }

    /*public Emotions(BitStreamReader reader)
    {
        this(reader.readBits(NUM_BITS));
    }*/

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        if (this.and(Emotions.Values.SAD).equals(Emotions.Values.SAD))
            builder.append("SAD");
        if (this.and(Emotions.Values.CHEERY).equals(Emotions.Values.CHEERY))
            builder.append(builder.length() == 0 ? "CHEERY" : " | CHEERY");
        if (this.and(Emotions.Values.UNHAPPY).equals(Emotions.Values.UNHAPPY))
            builder.append(builder.length() == 0 ? "UNHAPPY" : " | UNHAPPY");
        if (this.and(Emotions.Values.HAPPY).equals(Emotions.Values.HAPPY))
            builder.append(builder.length() == 0 ? "HAPPY" : " | HAPPY");
        if (this.and(Emotions.Values.SANE).equals(Emotions.Values.SANE))
            builder.append(builder.length() == 0 ? "SANE" : " | SANE");
        if (this.and(Emotions.Values.MAD).equals(Emotions.Values.MAD))
            builder.append(builder.length() == 0 ? "MAD" : " | MAD");
        if (this.and(Emotions.Values.ALIVE).equals(Emotions.Values.ALIVE))
            builder.append(builder.length() == 0 ? "ALIVE" : " | ALIVE");
        if (this.and(Emotions.Values.DEAD).equals(Emotions.Values.DEAD))
            builder.append(builder.length() == 0 ? "DEAD" : " | DEAD");

        if (builder.length() == 0)
            builder.append("NONE");

        return builder.toString();
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof Emotions))
            return false;

        final Emotions otherEmotions = (Emotions)other;
        return value == otherEmotions.value;
    }

    long bitSizeOf()
    {
        return 8;
    }

    long bitSizeOf(long bitPosition)
    {
        return bitSizeOf();
    }

    long initializeOffsets(long bitPosition)
    {
        return bitPosition + bitSizeOf(bitPosition);
    }

    /*public void write(BitStreamWriter writer)
    {

    }*/

    public Emotions or(Emotions other)
    {
        return new Emotions((short)(value | other.value));
    }

    public Emotions and(Emotions other)
    {
        return new Emotions((short)(value & other.value));
    }

    public Emotions not()
    {
        return new Emotions((short)(~value & ((1 << NUM_BITS) -1)));
    }

    public Emotions xor(Emotions other)
    {
        return new Emotions((short)(value ^ other.value));
    }

    public static final class Values
    {
        public static final Emotions SAD = new Emotions((short)0x01);
        public static final Emotions CHEERY = new Emotions((short)0x02);
        public static final Emotions UNHAPPY = new Emotions((short)0x04);
        public static final Emotions HAPPY = new Emotions((short)0x08);
        public static final Emotions SANE = new Emotions((short)0x10);
        public static final Emotions MAD = new Emotions((short)0x20);
        public static final Emotions ALIVE = new Emotions((short)0x40);
        public static final Emotions DEAD = new Emotions((short)0x80);
    }

    private Emotions(short value)
    {
        this.value = value;
    }

    private final static byte NUM_BITS = 8;
    private final short value; // type according to the zserio type
};
