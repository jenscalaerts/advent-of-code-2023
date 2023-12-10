const fs = require('node:fs');
const fn = process.argv[2];
const file = fs.readFileSync(fn, { encoding: 'utf8' });

const charMap = {
    'A': 14,
    'K': 13,
    'Q': 12,
    'J': 1,
    'T': 10
}

function getScore(description) {
    const counts = new Array(14);
    counts.fill(0);
    for (const char of description) {
        const index = toValue(char);
        counts[index - 1]++
    }
    const score = [[], [], [], [], [], []];

    for (let i = 1; i < counts.length; i++)
        score[counts[i]].push(i);

    score.forEach(ar => ar.sort((a, b) => b - a));
    score.splice(0, 1);
    if(counts[0]===0)
        return score
    for (let i = score.length - 1; i >= 0; i--){
        if (score[i].length !== 0) {
            score[i + counts[0]] = [score[i].shift()];
            counts[0] = 0;
            break;
        }
    }
    if(counts[0] !== 0)
        score[counts[0]-1] = [1];
    console.log(score); 
    return score;
}

function toValue(char) {
    return charMap[char] ? charMap[char] : char;
}

function compare(left, right) {
    const leftAr = left.score
    const rightAr = right.score
    for (let i = leftAr.length - 1; i >= 0; i--) {
        if (leftAr[i] == 0 && rightAr[i] == 0)
            continue;
        const numberOfLeftmatches = leftAr[i].length
        const numberOfRightmatches = rightAr[i].length
        if (numberOfLeftmatches !== numberOfRightmatches)
            return numberOfLeftmatches - numberOfRightmatches;
        if (i == 2 && numberOfRightmatches !== 0 && numberOfLeftmatches === numberOfRightmatches
            && leftAr[i - 1].length != rightAr[i - 1].length)
            return leftAr[i - 1].length - rightAr[i - 1].length;
        for (let j = 0; j < left.hand.length; j++) {
            let diff = toValue(left.hand[j]) - toValue(right.hand[j]);
            if (diff != 0)
                return diff;
        }
    }
    return 0;
}


const pairs = file.split('\n')
    .filter(i => i)
    .map(line => line.split(' '))
    .map(line => { return { 'hand': line[0], 'score': getScore(line[0]), 'bid': line[1] } })
    .sort((left, right) => compare(left, right));
pairs.forEach(pair => console.log(pair.hand));
const result = pairs.reduce((current, next, index) => current + (index + 1) * next.bid, 0)

console.log(result)
