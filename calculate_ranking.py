# import pandas
import sys

def parse_file(file):
    parsed = {}
    with open(file, 'r') as infile:
        for line in infile:
            try:
                lib, project, method, count = line.strip().split(';')
                if lib == 'LIB':
                    continue    # ignore header
                if lib not in parsed:
                    parsed[lib] = {}
                if project not in parsed[lib]:
                    parsed[lib][project] = {}
                parsed[lib][project][method] = int(count)
            except:
                print('Ignoring exception while parsing\n')
    return parsed

def get_all_methods(ranking):
    methods = []
    for project in ranking:
        for method in ranking[project]:
            if method not in methods:
                methods += [method]
    return methods

def weights_for_project(methods):
    result = {}
    _sum = sum(methods.values())
    for method in methods:
        result[method] = methods[method] / float(_sum)
    return result

def select_top_n(ranking, n):
    aggregated_weights = {}
    methods = get_all_methods(ranking)
    for method in methods:
        agg = 0
        for project in ranking:
            if method in ranking[project]:
                agg += ranking[project][method]
        aggregated_weights[method] = agg
    sorted_list = sorted(aggregated_weights.items(), key=lambda x: -1 * x[1])
    return sorted_list[:n+1]

infile = sys.argv[1]
top_n = int(sys.argv[2])
data = parse_file(infile)

for lib in data:
    ranking = {}
    for project in data[lib]:
        ranking[project] =  weights_for_project(data[lib][project])
    top = select_top_n(ranking, top_n)
    print("Ranking of methods for %s:" % lib)
    for t in top:
        print("%s (weight %0.5f)" % (t[0], t[1]))
