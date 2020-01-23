from Emotions import Emotions

if __name__ == "__main__":
    print(Emotions())
    print(~Emotions())

    print(Emotions.Values.SAD)
    print(Emotions.Values.CHEERY)
    print(Emotions.Values.UNHAPPY)
    print(Emotions.Values.HAPPY)
    print(Emotions.Values.SANE)
    print(Emotions.Values.MAD)
    print(Emotions.Values.ALIVE)
    print(Emotions.Values.DEAD)

    emotion = Emotions.Values.SAD | Emotions.Values.CHEERY
    print(emotion)

    emotion2 = Emotions.Values.ALIVE | Emotions.Values.DEAD
    emotion = emotion | emotion2
    print(emotion)

    emotion &= ~Emotions.Values.DEAD
    print(emotion)
