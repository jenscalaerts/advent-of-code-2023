const fs = require('node:fs');
const fn = process.argv[2];

const file = fs.readFileSync(fn, { encoding: 'utf8' }).trim();
const lines = file.split('\n');
//this keeps the 0 0 at beginning and end

console.log("solution 1:" + calculateArea(lines.map(createCommand)));
const commands = lines.map(parseHexCommand);
console.log(commands);
console.log("solution 2:" + calculateArea(commands));


function calculateArea(commands) {
    const coordinates = commands.reduce(calcNext, [{ 'x': 0, 'y': 0 }]);
    let sum = 0;
    for (let i = 0; i < coordinates.length - 1; i++) {
        const current = coordinates[i]
        const next = coordinates[i + 1]
        sum += current.x * next.y - current.y * next.x
    }
    const edge = commands.reduce((a, b) => a + Number(b.length), 0);
    return Math.floor((sum + edge) / 2 + 1);

}

function calcNext(ar, command) {
    let change;
    switch (command.direction) {
        case 'U':
            change = { 'x': 0, 'y': - command.length };
            break;
        case 'D':
            change = { 'x': 0, 'y': command.length };
            break;
        case 'R':
            change = { 'x': command.length, 'y': 0 };
            break;
        case 'L':
            change = { 'x': 0 - command.length, 'y': 0 };
            break;
    }
    ar.push(add(ar.at(-1), change));
    return ar;
}

function add(coordinate, change) {
    return { 'x': coordinate.x + change.x, 'y': coordinate.y + change.y }

}

function createCommand(line) {
    const split = line.split(' ');
    return { 'direction': split[0], 'length': Number(split[1]) }
}

function parseHexCommand(line) {
    const split = line.split(' ');
    const hexDirection = split[2].at(-2);
    let stringDirection;
    switch (hexDirection) {
        case '0':
            stringDirection = 'R';
            break;
        case '1':
            stringDirection = 'D';
            break;
        case '2':
            stringDirection = 'L';
            break;
        case '3':
            stringDirection = 'U';
            break;
    }
    length = parseInt(split[2].slice(2, -2), '16');
    return { 'direction': stringDirection, 'length': length };
}


