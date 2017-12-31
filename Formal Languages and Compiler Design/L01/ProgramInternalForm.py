class ProgramInternalForm:
    def __init__(self):
        self.__list = []

    def add(self, tokenCode, stIndex=None):
        pair = (tokenCode, stIndex)
        self.__list.append(pair)

    def __str__(self):
        s = ''
        s += '| %-5s | %-5s |\n' % ('Code', 'Addr')
        s += '|-%-5s-+-%-5s-+\n' % ('-' * 5, '-' * 5)
        for entry in self.__list:
            s += '| %-5s | %-5s |\n' % (str(entry[0]), str(entry[1]))
        s += '|-%-5s-+-%-5s-+\n' % ('-' * 5, '-' * 5)
        return s
