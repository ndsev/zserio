package bitmask_types;

import java.util.BitSet;
//import zserio.runtime.io.BitStreamReader;

public class Emotions
{
    public Emotions()
    {
        this(0);
    }

    /*public Emotions(BitStreamReader reader)
    {
        this(reader.readBits(NUM_BITS));
    }*/

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        if (this.and(Emotions.SAD).equals(Emotions.SAD))
            builder.append("SAD");
        if (this.and(Emotions.CHEERY).equals(Emotions.CHEERY))
            builder.append(builder.length() == 0 ? "CHEERY" : " | CHEERY");
        if (this.and(Emotions.UNHAPPY).equals(Emotions.UNHAPPY))
            builder.append(builder.length() == 0 ? "UNHAPPY" : " | UNHAPPY");
        if (this.and(Emotions.HAPPY).equals(Emotions.HAPPY))
            builder.append(builder.length() == 0 ? "HAPPY" : " | HAPPY");
        if (this.and(Emotions.SANE).equals(Emotions.SANE))
            builder.append(builder.length() == 0 ? "SANE" : " | SANE");
        if (this.and(Emotions.MAD).equals(Emotions.MAD))
            builder.append(builder.length() == 0 ? "MAD" : " | MAD");
        if (this.and(Emotions.ALIVE).equals(Emotions.ALIVE))
            builder.append(builder.length() == 0 ? "ALIVE" : " | ALIVE");
        if (this.and(Emotions.DEAD).equals(Emotions.DEAD))
            builder.append(builder.length() == 0 ? "DEAD" : " | DEAD");

        if (builder.length() == 0)
            builder.append("NONE");

        return builder.toString() + " (" + value.toString() + ")";
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof Emotions))
            return false;

        final Emotions otherEmotions = (Emotions)other;
        return value.equals(otherEmotions.value);
    }

    long bitSizeOf()
    {
        return NUM_BITS;
    }

    long bitSizeOf(long bitPosition)
    {
        return bitSizeOf();
    }

    long initializeOffsets(long bitPosition)
    {
        return bitPosition + bitSizeOf(bitPosition);
    }

    /*public static Emotions read(BitStreamReader reader)
    {

    }*/

    /*public void write(BitStreamWriter writer)
    {

    }*/

    public Emotions or(Emotions other)
    {
        final BitSet newValue = (BitSet)value.clone();
        newValue.or(other.value);
        return new Emotions(newValue);
    }

    public Emotions and(Emotions other)
    {
        final BitSet newValue = (BitSet)value.clone();
        newValue.and(other.value);
        return new Emotions(newValue);
    }

    public Emotions not()
    {
        final BitSet newValue = (BitSet)value.clone();
        newValue.flip(0, NUM_BITS);
        return new Emotions(newValue);
    }

    public Emotions xor(Emotions other)
    {
        final BitSet newValue = (BitSet)value.clone();
        newValue.xor(other.value);
        return new Emotions(newValue);
    }

    public static final Emotions SAD = new Emotions(0x01);
    public static final Emotions CHEERY = new Emotions(0x02);
    public static final Emotions UNHAPPY = new Emotions(0x04);
    public static final Emotions HAPPY = new Emotions(0x08);
    public static final Emotions SANE = new Emotions(0x10);
    public static final Emotions MAD = new Emotions(0x20);
    public static final Emotions ALIVE = new Emotions(0x40);
    public static final Emotions DEAD = new Emotions(0x80);

    private Emotions(long rawValue)
    {
        long[] array = new long[1];
        array[0] = rawValue;
        value = BitSet.valueOf(array);
    }

    private Emotions(BitSet bitSet)
    {
        value = (BitSet)bitSet.clone();
    }

    private static final int NUM_BITS = 8;
    private final BitSet value;
};
