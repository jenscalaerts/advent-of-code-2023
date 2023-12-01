tokens = {
    'one': 1,
    'two': 2,
    'three': 3,
    'four': 4,
    'five': 5,
    'six': 6,
    'seven': 7,
    'eight': 8,
    'nine': 9,
    'zero': 0,
    '1': 1,
    '2': 2,
    '3': 3,
    '4': 4,
    '5': 5,
    '6': 6,
    '7': 7,
    '8': 8,
    '9': 9,
    '0': 0
}


def convertNumbers(line, tokens):
    first = {'number': 0, 'index': len(line) + 1}
    last = {'number': 0, 'index': -1}
    for stri, number in tokens.items():
        ffirst = line.find(stri)
        if ffirst != -1 and ffirst < first['index']:
            first['index'] = ffirst
            first['number'] = number
        llast = line.rfind(stri)
        if llast != -1 and llast > last['index']:
            last['index'] = llast
            last['number'] = number
    return first['number'] * 10 + last['number']


result = 0
with open('data', 'r') as f:
    for line in f.readlines():
        conv = convertNumbers(line, tokens)
        result = result + conv
print(result)
