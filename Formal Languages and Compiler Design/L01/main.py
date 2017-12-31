import sys

from SymbolTable import SymbolTable
from Codification import Codification
from ProgramInternalForm import ProgramInternalForm

codificationFile = 'codification.txt'

WHITESPACES = [' ', '\t', '\n']
SEPARATORS = [';', ',']
DELIMITERS = ['{', '}', '(', ')']


class Scanner:
    def __init__(self, codificationFile, sourceCodeFile):
        self.__codification = Codification(codificationFile)
        self.__st = SymbolTable()
        self.__pif = ProgramInternalForm()
        self.__sourceCodeFD = open(sourceCodeFile, 'r')
        self.__lineNo = 0
        self.__currentLine = ''

    def __getNextToken(self):
        token = ''

        while self.__currentLine == '':
            if self.__currentLine == '':
                self.__currentLine = self.__sourceCodeFD.readline()
                # print('|' + self.__currentLine + '|')
                if len(self.__currentLine) == 0:      # end of file
                    return None, None
                self.__currentLine = self.__currentLine.strip()
                self.__lineNo += 1

        # pass whitespaces
        while self.__currentLine != '' and self.__currentLine[0] in WHITESPACES:
            self.__currentLine = self.__currentLine[1:]

        # append 1 char at a time and verify if we can get a valid token or not
        while self.__currentLine != '':
            token += self.__currentLine[0]
            if not self.__codification.isAccepted(token):
                if len(token) < 2:
                    raise Exception('[ERROR] Unexpected token: %s at line %s' % (str(token), str(self.__lineNo)))
                token = token[:-1]  # remove last character
                break
            else:
                self.__currentLine = self.__currentLine[1:]

        if token == '':
            return None, None

        ln = 0 + self.__lineNo
        return token, ln

    def initPIFandST(self):
        while True:
            token, ln = self.__getNextToken()
            if token is None and ln is None:
                break
            if self.__codification.isReserved(token):
                code = self.__codification.getCodeOfAtom(token)
                self.__pif.add(code, None)
            else:
                ind = self.__st.poz(token)
                if self.__codification.isIdentifier(token):
                    code = self.__codification.getCodeOfIdentifier()
                    self.__pif.add(code, ind)
                elif self.__codification.isConstant(token):
                    code = self.__codification.getCodeOfConstant()
                    self.__pif.add(code, ind)
                elif self.__codification.isString(token):
                    code = self.__codification.getCodeOfString()
                    self.__pif.add(code, ind)
                else:
                    raise Exception('Line %d: unexpected token: %s' % (ln, token))

    def getPIF(self):
        return self.__pif

    def getST(self):
        return self.__st


if __name__ == '__main__':
    if len(sys.argv) < 2:
        scriptFileName = sys.argv[0].split('\\')[-1]
        print('Usage: %s f\n\tf: source code file' % (scriptFileName))
        exit(0)

    scanner = Scanner(codificationFile, sys.argv[1])
    try:
        scanner.initPIFandST()
    except Exception as e:
        print(str(e))

    pif = scanner.getPIF()
    st = scanner.getST()

    print('PIF:')
    print(str(pif))
    print('\n')
    print('ST:')
    print(str(st))
