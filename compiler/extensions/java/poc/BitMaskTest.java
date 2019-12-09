import bitmask_types.Emotions;

public class BitMaskTest
{
    public static void main(String args[])
    {
        System.out.println("emotion: " + new Emotions());
        System.out.println("emotion: " + new Emotions().not());

        System.out.println("emotion: " + Emotions.SAD.toString());
        System.out.println("emotion: " + Emotions.CHEERY.toString());
        System.out.println("emotion: " + Emotions.UNHAPPY.toString());
        System.out.println("emotion: " + Emotions.HAPPY.toString());
        System.out.println("emotion: " + Emotions.SANE.toString());
        System.out.println("emotion: " + Emotions.MAD.toString());
        System.out.println("emotion: " + Emotions.ALIVE.toString());
        System.out.println("emotion: " + Emotions.DEAD.toString());

        Emotions emotion = Emotions.SAD.or(Emotions.CHEERY);
        System.out.println("emotion: " + emotion.toString());

        Emotions emotion2 = Emotions.ALIVE.or(Emotions.DEAD);
        emotion = emotion.or(emotion2);
        System.out.println("emotion: " + emotion.toString());

        emotion = emotion.and(Emotions.DEAD.not());
        System.out.println("emotion: " + emotion.toString());
    }
}
