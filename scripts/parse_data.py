import csv
import os
import pathlib

from typing import List


DATA_FOLDER = '/home/ubuntu/repos/KIProjektSS19/scripts/data'
WHITE_LINE = 1
BLACK_LINE = 2
POSITION_RECORD_INDEX = 4
PLAYER_RECORD_INDEX_GAME_FILE = 1
PLAYER_RECORD_INDEX_RESULT_FILE = 0
POINTS_RECORD_INDEX = 4
IMPORTANT_POSITIONS_START = 11


def generate_data(important_positions, winner):
    data = list()
    data.append(['POSITION', 'WHITE_WINS', 'BLACK_WINS'])
    for position in important_positions:
        fen = position[POSITION_RECORD_INDEX]
        white_wins = 1 if winner == '0' else 0
        black_wins = 1 - white_wins
        data.append([fen, white_wins, black_wins])
    return data


def write_file(data):
    filename = os.path.join(DATA_FOLDER, 'data.csv')
    with open(filename, 'a', newline='') as f:
        csv_writer = csv.writer(f, delimiter=';')
        csv_writer.writerows(data)


def write_data_csv(game_csv_reader, winner):
    game_csv_list = list(game_csv_reader)
    important_positions = game_csv_list[IMPORTANT_POSITIONS_START:]
    data = generate_data(important_positions, winner)
    write_file(data)


def get_game_winner(result_csv_reader):
    result_csv_list = list(result_csv_reader)
    winning_line = (WHITE_LINE 
                    if result_csv_list[WHITE_LINE][POINTS_RECORD_INDEX] == '1'
                    else BLACK_LINE)
    return result_csv_list[winning_line][PLAYER_RECORD_INDEX_RESULT_FILE]


def extract_data(csv_game_files, csv_result_files):
    for game_file, result_file in zip(csv_game_files, csv_result_files):
        with open(game_file, 'r') as game_f:
            with open(result_file, 'r') as result_f:
                game_csv_reader = csv.reader(game_f, delimiter=';')
                result_csv_reader = csv.reader(result_f, delimiter=';')
                winner = get_game_winner(result_csv_reader)
                write_data_csv(game_csv_reader, winner)


def main():
    data_folder = pathlib.Path(DATA_FOLDER)
    csv_game_files = [f for f in data_folder.iterdir() if str(f).endswith('_moves.csv')]
    csv_game_files.sort(key=str)
    csv_result_files = [f for f in data_folder.iterdir() if str(f).endswith('_result.csv')]
    csv_result_files.sort(key=str)
    extract_data(csv_game_files, csv_result_files)


if __name__ == '__main__':
    main()
