const fs = require('node:fs');
const fn = process.argv[2];

const file = fs.readFileSync(fn, { encoding: 'utf8' });
const lines = file.split('\n');

class LocationRange {
    constructor(y, end, number) {
        this.beginning = end - number.length + 1;
        this.end = end;
        this.y = y;
        this.number = number;
    }

    get asNumber() {
        return parseInt(this.number);
    }


    touches(location) {
        const result = (Math.abs(this.y - location.y) < 2) &&
            ((this.beginning - 1) <= location.x) &&
            (this.end + 1 >= location.x);
        return result;
    }

}

function findTokens() {
    const tokens = [];
    for (let i = 0; i < lines.length; i++) {
        const row = lines[i];
        for (let j = 0; j < row.length; j++) {
            const element = row[j];
            if (isNaN(element) && element !== '.') {
                tokens.push({ 'x': j, 'y': i, symb: element });
            }

        }
    }
    return tokens;
}

function findNumber() {
    let numberLocations = []
    for (let i = 0; i < lines.length; i++) {
        let currentNumber = '';
        const row = lines[i];
        for (let j = 0; j < row.length; j++) {
            const element = row[j];
            if (!isNaN(element))
                currentNumber += element;
            else if (currentNumber.length != 0) {
                numberLocations.push(new LocationRange(i, j - 1, currentNumber));
                currentNumber = '';
            }
        }
        if (currentNumber !== '') {
            numberLocations.push(new LocationRange(i, row.length - 1, currentNumber));
        }
    }
    return numberLocations;
}

function calculateResult1(tokenLocations, numberLocations) {

    function anyTouches(loc) {
        for (const token of tokenLocations) {
            if (loc.touches(token))
                return token;
        }
        return false;
    }

    let result = 0;

    for (const location of numberLocations) {
        let pos = anyTouches(location);
        if (pos) {
            result += location.asNumber;
        }
    }
    return result;
}


function calculateResult2(tokenLocations, numberLocations) {
    let total2 = 0
    for (const token of tokenLocations) {
        const touching = [];
        for (const loc of numberLocations) {
            if (loc.touches(token)) {
                touching.push(loc);
            }
        }


        if (token.symb === '*' && touching.length === 2)
            total2 += touching[0].asNumber * touching[1].asNumber
    }
    return total2;
}

const tokenLocations = findTokens();
const numberLocations = findNumber();
console.log("result1=" + calculateResult1(tokenLocations, numberLocations));
console.log("result2=" + calculateResult2(tokenLocations, numberLocations));
