from Emotions import Emotions

if __name__ == "__main__":
    print(Emotions())
    print(~Emotions())

    print(Emotions.SAD)
    print(Emotions.CHEERY)
    print(Emotions.UNHAPPY)
    print(Emotions.HAPPY)
    print(Emotions.SANE)
    print(Emotions.MAD)
    print(Emotions.ALIVE)
    print(Emotions.DEAD)

    emotion = Emotions.SAD | Emotions.CHEERY
    print(emotion)

    emotion2 = Emotions.ALIVE | Emotions.DEAD
    emotion = emotion | emotion2
    print(emotion)

    emotion = emotion & ~Emotions.DEAD
    print(emotion)
