import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'
import sys
import enum
from itertools import product

import joblib
import numpy as np
import tensorflow as tf

from sklearn.model_selection import train_test_split
from sklearn.neural_network import MLPClassifier
from dbn.tensorflow import UnsupervisedDBN


tf.compat.v1.logging.set_verbosity(tf.compat.v1.logging.ERROR)

DBN_NETWORK_FILENAME = 'dbn_model.pkl'
MLP_NETWORK_FILENAME = 'mlp_model.pkl'
DATA_FILENAME = '/home/ubuntu/repos/KIProjektSS19/scripts/data/data.csv'


class Square(enum.IntEnum):
    a7 = 6
    b7 = 13
    c7 = 20
    d7 = 27
    e7 = 34
    f7 = 41
    g7 = 48
    h7 = 55
    a6 = 5
    b6 = 12
    c6 = 19
    d6 = 26
    e6 = 33
    f6 = 40
    g6 = 47
    h6 = 54
    a5 = 4
    b5 = 11
    c5 = 18
    d5 = 25
    e5 = 32
    f5 = 39
    g5 = 46
    h5 = 53
    a4 = 3
    b4 = 10
    c4 = 17
    d4 = 24
    e4 = 31
    f4 = 38
    g4 = 45
    h4 = 52
    a3 = 2
    b3 = 9
    c3 = 16
    d3 = 23
    e3 = 30
    f3 = 37
    g3 = 44
    h3 = 51
    a2 = 1
    b2 = 8
    c2 = 15
    d2 = 22
    e2 = 29
    f2 = 36
    g2 = 43
    h2 = 50
    a1 = 0
    b1 = 7
    c1 = 14
    d1 = 21
    e1 = 28
    f1 = 35
    g1 = 42
    h1 = 49


def set_piece_at_square(bitboards, square, c):
    if c == 'w':
        bitboards[square.value] = 1
    elif c == 't':
        bitboards[56+square.value] = 1
    elif c == 'c':
        bitboards[2*56+square.value] = 1
    elif c == 'W':
        bitboards[3*56+square.value] = 1
    elif c == 'T':
        bitboards[4*56+square.value] = 1
    elif c == 'C':
        bitboards[5*56+square.value] = 1


def encode_position(fen: str):
    board_and_player = fen.split()
    board = board_and_player[0]
    player = board_and_player[1]

    bitboards = np.zeros(337)
    squares_fen_order = list(Square)
    squarecounter = 0
    for c in board:
        if c.isdigit():
            squarecounter += int(c)
        elif c != '/':
            set_piece_at_square(bitboards, squares_fen_order[squarecounter], c)
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
    first_positions = dbn_network.transform(X[:, 0])
    second_positions = dbn_network.transform(X[:, 1])
    X = np.concatenate((first_positions, second_positions), axis=1)
    print(f'Shape after Concatenation = {X.shape}')

    mlp_network = MLPClassifier(
        hidden_layer_sizes=(90, 45, 20, 2),
        learning_rate='constant',
        learning_rate_init=1e-2,
        max_iter=1000
    )
    mlp_network.fit(X, Y)
    joblib.dump(mlp_network, MLP_NETWORK_FILENAME)


def predict_mlp(first, second):
    First = np.array(first)
    Second = np.array(second)
    dbn_network: UnsupervisedDBN = UnsupervisedDBN.load(DBN_NETWORK_FILENAME)
    mlp_network: MLPClassifier = joblib.load(MLP_NETWORK_FILENAME)
    first_position = dbn_network.transform(First)
    second_position = dbn_network.transform(Second)
    X = np.concatenate((first_position, second_position))
    result = mlp_network.predict(X.reshape(1, -1))
    return result.flatten().tolist()


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
    return np.array(dbn_data), np.array(mlp_data), np.array(mlp_labels)


def test_mlp(X, Y):
    dbn_network: UnsupervisedDBN = UnsupervisedDBN.load(DBN_NETWORK_FILENAME)
    mlp_network: MLPClassifier = joblib.load(MLP_NETWORK_FILENAME)
    first_positions = dbn_network.transform(X[:, 0])
    second_positions = dbn_network.transform(X[:, 1])
    X = np.concatenate((first_positions, second_positions), axis=1)
    return mlp_network.score(X, Y)


def main():
    if len(sys.argv) >= 2 and sys.argv[1] == 'predict':
        first = list(map(int, sys.argv[2][1:-1].split(',')))
        second = list(map(int, sys.argv[3][1:-1].split(',')))
        result = predict_mlp(first, second)
        print(str(result))
        sys.exit(0)

    dbn_data, mlp_data, mlp_labels = gather_data()
    print("Splitting data in Training and Test Data...")
    X_train, X_test, Y_train, Y_test = train_test_split(
        mlp_data,
        mlp_labels,
        test_size=0.25
    )
    print("Splitting Done!")

    print("Training Deep Belief Network...")
    train_dbn(dbn_data)
    print("Training DBN Done!")

    print("Training Multi-Layer Perceptron...")
    train_mlp(X_train, Y_train)
    print("Training MLP Done!")

    print("Testing MLP...")
    print(f"Score = {test_mlp(X_test, Y_test)}")


if __name__ == '__main__':
    main()
