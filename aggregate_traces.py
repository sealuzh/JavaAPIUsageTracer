# import pandas
import sys

# usage: python aggregate_traces logfile outfile

# Version 1 - with Pandas

# df = pandas.read_csv(sys.argv[0])
# outfile = sys.argv[1]
# lib = df.LIB[0]
# project = df.PROJECT[0]
#
# with open(outfile, 'w') as f:
#     f.write("LIB;PROJECT;METHOD;COUNT\n")
#     for method in df.METHOD.unique():
#         count = len(df[df.METHOD == method])
#         f.write("%s;%s;%s;%d\n" % (lib, project, method, count))

# Version 2 - linear time

def parse_file(file):
    with open(file, 'r') as infile:
        methods = {}
        lib = ''
        project = ''
        for line in infile:
            try:
                lib, project, method = line.strip().split(';')
                if lib == 'LIB':
                    continue    # ignore header
                if method in methods:
                    methods[method] += 1
                else:
                    methods[method] = 1
            except:
                print('Ignoring exception while parsing\n')
        return (lib, project, methods)

def append_to_results(results, lib, project, methods):
    if lib not in results:
        results[lib] = {}
    results[lib][project] = methods
    return results



outfile = sys.argv[1]
total_results = {}
for f in sys.argv[2:]:
    print("Parsing %s" % f)
    lib, project, methods = parse_file(f)
    print("Finished reading %s" % f)
    append_to_results(total_results, lib, project, methods)
print("Finished reading all")
with open(outfile, 'w') as outfile:
    outfile.write('LIB;PROJECT;METHOD;COUNT\n')
    for lib in total_results:
        for project in total_results[lib]:
            for method, count in total_results[lib][project].items():
                outfile.write("%s;%s;%s;%d\n" % (lib, project, method, count))
print('Finished writing\n')
