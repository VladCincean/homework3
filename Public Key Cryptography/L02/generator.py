from random import randint
import sys

if __name__ == '__main__':
    minim = 10 ** (int(sys.argv[1]) - 1)
    maxim = 10 ** int(sys.argv[1])
    print('%d %d' % (randint(minim, maxim), randint(minim, maxim)))
