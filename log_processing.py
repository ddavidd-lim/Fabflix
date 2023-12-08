import sys

TS_times = []
TJ_times = []

def average(list):
    return sum(list)/len(list)

if __name__ == "__main__":
    filename = sys.argv[1]
    with open(filename) as file:
        lines = file.readlines()
        for line in lines:
            if len(line) > 0:
                line_array = line.split(",")
                TS_times.append(int(line_array[1]))
                TJ_times.append(int(line_array[3]))
    TS_average = average(TS_times)
    TJ_average = average(TJ_times)

    print(f"TS average: {TS_average/1000000} ms")
    print(f"TJ average: {TJ_average/1000000} ms")