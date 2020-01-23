import bitmask_types.Emotions;

public class BitMaskTest
{
    public static void main(String args[])
    {
        System.out.println(new Emotions());
        System.out.println(new Emotions().not());

        System.out.println(Emotions.Values.SAD.toString());
        System.out.println(Emotions.Values.CHEERY.toString());
        System.out.println(Emotions.Values.UNHAPPY.toString());
        System.out.println(Emotions.Values.HAPPY.toString());
        System.out.println(Emotions.Values.SANE.toString());
        System.out.println(Emotions.Values.MAD.toString());
        System.out.println(Emotions.Values.ALIVE.toString());
        System.out.println(Emotions.Values.DEAD.toString());

        Emotions emotion = Emotions.Values.SAD.or(Emotions.Values.CHEERY);
        System.out.println(emotion.toString());

        Emotions emotion2 = Emotions.Values.ALIVE.or(Emotions.Values.DEAD);
        emotion = emotion.or(emotion2);
        System.out.println(emotion.toString());

        emotion = emotion.and(Emotions.Values.DEAD.not());
        System.out.println(emotion.toString());
    }
}
