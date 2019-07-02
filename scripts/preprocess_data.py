import csv
import pathlib

from typing import List


DATA_FOLDER = '/home/ubuntu/repos/KIProjektSS19/scripts/data'
GAME_START_LINE = 7
GAME_END_LINE = -4
RESULT_START_LINE = -3


def write_game_file(game_filename: str, csv_list: List[List[str]]):
    game = csv_list[GAME_START_LINE:GAME_END_LINE]
    write_file(game_filename, game)


def write_result_file(result_filename: str, csv_list: List[List[str]]):
    result = csv_list[RESULT_START_LINE:]
    write_file(result_filename, result)


def write_file(filename: str, data):
    with open(filename, 'w', newline='') as f:
        csv_write = csv.writer(f, delimiter=';')
        csv_write.writerows(data)


def generate_files(csv_files: List[pathlib.Path]):
    for file in csv_files:
        with open(file, 'r') as f:
            csv_reader = csv.reader(f, delimiter=';')
            csv_list = list(csv_reader)
            write_game_file(str(file).replace('.csv', '_moves.csv'), csv_list)
            write_result_file(str(file).replace('.csv', '_result.csv'), csv_list)
        file.unlink()


def main():
    data_folder = pathlib.Path(DATA_FOLDER)
    csv_files = [f for f in data_folder.iterdir() if str(f).endswith('.csv')]
    generate_files(csv_files)


if __name__ == '__main__':
    main()
