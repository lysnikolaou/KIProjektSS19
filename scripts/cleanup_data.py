import pathlib


DATA_FILE = '/home/ubuntu/repos/KIProjektSS19/scripts/data/data.csv'


def write_distinct_lines(distinct_lines):
    ready_data_file = DATA_FILE.replace('.csv', '_ready.csv')
    with open(ready_data_file, 'w') as f:
        for line in distinct_lines:
            f.write(line)


def remove_duplicate_lines(file):
    with open(file, 'r') as f:
        distinct_lines = set(f.readlines())
        write_distinct_lines(distinct_lines)


def main():
    file = pathlib.Path(DATA_FILE)
    remove_duplicate_lines(file)
    file.unlink()


if __name__ == '__main__':
    main()
