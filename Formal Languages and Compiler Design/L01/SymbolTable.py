class SymbolTable:
    def __init__(self):
        self.__elements = []
        self.__addresses = dict()
        self.__id = 0

    def add(self, value):
        self.__elements += [str(value)]
        self.__addresses[str(value)] = self.__id
        self.__id += 1
        self.__elements = sorted(self.__elements)

    def remove(self, value):
        try:
            self.__elements.remove(str(value))
            return True
        except ValueError as e:
            return False

    def __contains__(self, value):
        left = 0
        right = len(self.__elements) - 1
        while left <= right:
            m = (left + right) // 2
            if self.__elements[m] == str(value):
                return True
            elif self.__elements[m] < str(value):
                left = m + 1
            else:
                right = m - 1
        return False

    def poz(self, symbol):
        if not self.__contains__(symbol):
            self.add(symbol)
        return self.__addresses[str(symbol)]

    def at(self, index):
        if index < 0 or index >= len(self.__elements):
            return None
        return self.__elements[index]

    def __str__(self):
        s = ''
        s += '| %-5s | %-5s |\n' % ('Pos', 'Symb')
        s += '|-%-5s-+-%-5s-+\n' % ('-' * 5, '-' * 5)
        for i in range(len(self.__elements)):
            s += '| %-5s | %-5s |\n' % (self.__addresses[self.__elements[i]], self.__elements[i])
        s += '|-%-5s-+-%-5s-+\n' % ('-' * 5, '-' * 5)
        return s
