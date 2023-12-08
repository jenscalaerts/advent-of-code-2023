const fs = require('node:fs');
const fn = process.argv[2];

function findFirstMatching(game) {
    for (let i = 0; i < game.time; i++) {
        if ((game.time - i) * (i) > game.distance) {
            return i;
        }
    }
    return 0;
}

function findLastMatching(game) {
    for (let i = game.time; i > 0; i--) {
        if ((game.time - i) * i > game.distance) {
            return i + 1;
        }
    }
    return -1;
}

function findEdges(game) {
    return { 'begin': findFirstMatching(game), 'end': findLastMatching(game) }
}

function calculateResult(games) {
    console.log(games);
    return games.map(findEdges)
        .map(gameResult => gameResult.end - gameResult.begin)
        .reduce((prev, next) => prev * next, 1)
}

function read1Style() {
    const lines = file.split('\n').map((item) => {
        const parts = item.replace(/\w+:\s+/, '');
        return parts.split(/\s+/);
    })
    return lines[0].map((item, index) => { return { 'time': item, 'distance': lines[1][index] } });
}

function read2Style(){

    const lines = file.split('\n').map((item) => item.replace(/\w+:\s+/, '').replace(/\s/g, ''));
    return [{ 'time': lines[0], 'distance': lines[1]}];
}

const file = fs.readFileSync(fn, { encoding: 'utf8' });
console.log(calculateResult(read1Style()))
console.log(calculateResult(read2Style()))
