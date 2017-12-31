import re

CODE_IDENTIFIER = 0
CODE_CONSTANT = 1
CODE_STRING = 2

M_IDENTIFIER = 'identifier'
M_CONSTANT = 'constant'
M_STRING = 'string'


class Codification:
    def __init__(self, file):
        self.__map = dict()  # (string, int)
        self.__rulesMap = dict()  # (int, lambda(string))
        self.__partialRulesMap = dict()  # --//--
        self.__initFromFile(file)
        self.__initIdAndConstRules()

    def __initFromFile(self, file):
        with open(file, 'r') as f:
            for line in f:
                line = line.strip()
                if line == '':
                    continue
                if line[0] == '#':
                    self.__currentType = line.split('#')[-1].strip()
                    continue
                if line[0] == '~':
                    continue  # we add identifier and constant rules manually
                tokens = line.split('@')
                tokens = [t.strip() for t in tokens]
                code = None
                self.__map[str(tokens[0])] = int(tokens[1])
                self.__rulesMap[int(tokens[1])] = lambda t: str(t) == str(tokens[0])
                self.__partialRulesMap[int(tokens[1])] = lambda t: str(tokens[0]).startswith(str(t))
        return True

    def __initIdAndConstRules(self):
        # identifier rules: b. arbitrary length, no more than 250 characters
        self.__map[M_IDENTIFIER] = CODE_IDENTIFIER
        self.__rulesMap[CODE_IDENTIFIER] = lambda t: re.match(r'^[a-zA-Z_]([a-zA-Z0-9_]{,249})$', str(t))
        self.__partialRulesMap[CODE_IDENTIFIER] = lambda t: re.match(r'^[a-zA-Z_]([a-zA-Z0-9_]{,249})$', str(t))

        # constant rules: integers (real) with sign
        self.__map[M_CONSTANT] = CODE_CONSTANT
        self.__rulesMap[CODE_CONSTANT] = lambda t: re.match(r'(^0$)|(^(\+|\-)?0\.[0-9]*[1-9]$)|(^(\+|\-)?[1-9][0-9]*(\.[0-9]*[1-9])?$)', str(t))
        self.__partialRulesMap[CODE_CONSTANT] = lambda t: re.match(r'^(\+|\-)?[0-9]*(\.)?[0-9]*$', str(t))

        # string rules
        self.__map[M_STRING] = CODE_STRING
        self.__rulesMap[CODE_STRING] = lambda t: re.match(r'^\"([^\"]*)\"$', str(t))
        self.__partialRulesMap[CODE_STRING] = lambda t: (re.match(r'^\"([^\"]*)\"$', str(t)) or re.match(r'^\"([^\"]*)$', str(t)))

        return True

    def getCodeOfAtom(self, atom):
        return self.__map[atom]

    def getCodeOfIdentifier(self):
        return CODE_IDENTIFIER

    def getCodeOfConstant(self):
        return CODE_CONSTANT

    def getCodeOfString(self):
        return CODE_STRING

    def getRuleOfCode(self, code):
        return self.__rulesMap[code]

    def getPartialRuleOfCode(self, code):
        return self.__partialRulesMap[code]

    def isAccepted(self, string):
        if string in self.__map.keys():
            if string not in [M_IDENTIFIER, M_CONSTANT, M_STRING]:
                return True
        for r in self.__partialRulesMap.values():
            if r(string):
                return True
        return False

    def getCodeOf(self, string):
        for k in self.__rulesMap.keys():
            if k not in [CODE_IDENTIFIER, CODE_CONSTANT, CODE_STRING]:
                if self.__rulesMap[k](string):
                    return k
        for k in [CODE_IDENTIFIER, CODE_CONSTANT, CODE_STRING]:
            if self.__rulesMap[k](string):
                return k

    def isReserved(self, token):
        if token in self.__map.keys():
            if token not in [M_IDENTIFIER, M_CONSTANT, M_STRING]:
                return True
        for k in self.__rulesMap.keys():
            if k not in [CODE_IDENTIFIER, CODE_CONSTANT, CODE_STRING]:
                if self.__rulesMap[k](token):
                    return True
        return False

    def isIdentifier(self, token):
        return self.__rulesMap[CODE_IDENTIFIER](token)

    def isConstant(self, token):
        return self.__rulesMap[CODE_CONSTANT](token)

    def isString(self, token):
        return self.__rulesMap[CODE_STRING](token)