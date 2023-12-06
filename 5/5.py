import sys


def create_mapping_group(f):
    # skip header
    f.readline()
    line = f.readline()
    mapping_group = []
    while line != '\n':
        parts = line.split(' ')
        mapping = {
            'source_begin': int(parts[1]),
            'source_end': int(parts[1]) + int(parts[2]),
            'destination_begin': int(parts[0]),
            'length': int(parts[2].strip())}
        mapping_group.append(mapping)
        line = f.readline()
        if not line.endswith('\n'):
            return mapping_group, True
    return mapping_group, False


def parse_file(filename):
    with open(filename, 'r') as file:
        li = file.readline().strip()
        seeds = list(map(int, li.split(': ')[1].split(' ')))
        file.readline()
        group, _ = create_mapping_group(file)
        groups = [group]
        islast = False
        while not islast:
            group, islast = create_mapping_group(file)
            groups.append(group)
        for groupi in groups:
            groupi.sort(key=lambda x: x['source_begin'])
        return groups, seeds


def calculate_offset(group, location):
    end = group['source_begin'] + group['length']
    if group['source_begin'] <= location < end:
        return group['destination_begin'] - group['source_begin']
    return 0


def find_end_location(seed, groups):
    next_location = seed
    for group in groups:
        location = next_location
        for mapping in group:
            next_location = next_location + calculate_offset(mapping, location)
    return next_location


def find_min_location(seeds, groups):
    locations = []
    for seed in seeds:
        locations.append(find_end_location(seed, groups))
    locations.sort()
    return locations[0]


def has_no_overlap(span, mapping):
    return mapping['source_begin'] > span['end'] or mapping['source_end'] <= span['begin']


def apply_group(span, group):
    new_spans = []
    for mapping in group:
        if has_no_overlap(span, mapping):
            continue
        if span['begin'] < mapping['source_begin']:
            new_spans.append({'begin': span['begin'], 'end': mapping['source_begin'] - 1})
            span['begin'] = mapping['source_begin']
        diff = mapping['destination_begin'] - mapping['source_begin']
        if span['end'] >= mapping['source_end']:
            new_spans.append({'begin': span['begin'] + diff, 'end': mapping['source_end'] - 1 + diff})
            span['begin'] = mapping['source_end']
        else:
            new_spans.append({'begin': span['begin'] + diff, 'end': span['end'] + diff})
            return new_spans
    new_spans.append(span)
    return new_spans


def find_location_spans_in_range(span, groups):
    spans = [span]
    for group in groups:
        new_spans = []
        for span in spans:
            new_spans = new_spans + apply_group(span, group)
        spans = new_spans
    return spans


groups, seeds = parse_file(sys.argv[1])
print("result1 = " + str(find_min_location(seeds, groups)))

all_spans = []
for i in range(0, len(seeds), 2):
    span = {'begin': seeds[i], 'end': seeds[i] + seeds[i + 1]}
    all_spans = all_spans + find_location_spans_in_range(span, groups)
beginnings = list(map(lambda x: x['begin'], all_spans))
beginnings.sort()
print(beginnings[0])

