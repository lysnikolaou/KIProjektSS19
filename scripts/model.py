from itertools import product

import numpy as np

from sklearn.model_selection import train_test_split
from sklearn.neural_network import MLPClassifier
from sklearn.externals import joblib
from dbn.tensorflow import UnsupervisedDBN


DBN_NETWORK_FILENAME = 'dbn_model.pkl'
MLP_NETWORK_FILENAME = 'mlp_model.pkl'
DATA_FILENAME = '/home/ubuntu/repos/KIProjektSS19/scripts/data/data.csv'


def set_piece_at_square(bitboards, squarecounter, c):
    if c == 'w':
        bitboards[squarecounter] = 1
    elif c == 't':
        bitboards[56+squarecounter] = 1
    elif c == 'c':
        bitboards[2*56+squarecounter] = 1
    elif c == 'W':
        bitboards[3*56+squarecounter] = 1
    elif c == 'T':
        bitboards[4*56+squarecounter] = 1
    elif c == 'C':
        bitboards[5*56+squarecounter] = 1


def encode_position(fen: str):
    board_and_player = fen.split()
    board = board_and_player[0]
    player = board_and_player[1]

    bitboards = np.zeros(337)
    squarecounter = 0
    for c in board:
        if c.isdigit():
            squarecounter += int(c)
        elif c != '/':
            set_piece_at_square(bitboards, squarecounter, c)
            squarecounter += 1
    bitboards[336] = 0 if player == 'r' else 1
    return bitboards


def train_dbn(X):
    dbn_network = UnsupervisedDBN(
        hidden_layers_structure=[337, 260, 170, 85, 45],
        activation_function='relu',
        learning_rate_rbm=5*1e-3,
        n_epochs_rbm=200
    )
    dbn_network.fit(X)
    dbn_network.save(DBN_NETWORK_FILENAME)


def train_mlp(X, Y):
    dbn_network = UnsupervisedDBN.load(DBN_NETWORK_FILENAME)
    X = dbn_network.transform(X)

    mlp_network = MLPClassifier(
        hidden_layer_sizes=(90, 45, 20, 2),
        learning_rate='constant',
        learning_rate_init=1e-2,
        max_iter=1000
    )
    mlp_network.fit(X, Y)
    joblib.dump(mlp_network, MLP_NETWORK_FILENAME)


def predict_mlp(X):
    dbn_network: UnsupervisedDBN = UnsupervisedDBN.load(DBN_NETWORK_FILENAME)
    mlp_network: MLPClassifier = joblib.load(MLP_NETWORK_FILENAME)
    X = dbn_network.transform(X)
    result = mlp_network.predict(X)
    return result


def extract_mlp_data(data):
    returned_data = list()
    returned_labels = list()
    for data_point in data:
        first_position = encode_position(data_point[0].split(';')[0])
        second_position = encode_position(data_point[1].split(';')[0])

        returned_data.append((first_position, second_position))
        returned_labels.append((1, 0))

        returned_data.append((second_position, first_position))
        returned_labels.append((0, 1))

    return returned_data, returned_labels


def extract_dbn_data(raw_data):
    return list(map(lambda e: encode_position(e.split(';')[0]), raw_data))


def gather_data():
    with open(DATA_FILENAME, 'r') as file:
        raw_data = list(map(str.strip, file.readlines()))
    
    white_wins = list(filter(lambda e: '1;0' in e, raw_data))
    black_wins = list(filter(lambda e: '0;1' in e, raw_data))
    data = list(product(white_wins, black_wins))
    mlp_data, mlp_labels = extract_mlp_data(data)

    dbn_data = extract_dbn_data(raw_data[1:]) # [1:] so that header line gets removed
    return dbn_data, mlp_data, mlp_labels


def main():
    dbn_data, mlp_data, mlp_labels = gather_data()
    X_train, X_test, Y_train, Y_test = train_test_split(
        mlp_data,
        mlp_labels,
        test_size=0.25
    )
    train_dbn(dbn_data)
    train_mlp(X_train, Y_train)


if __name__ == '__main__':
    main()
