package tictactoe;

import java.util.InputMismatchException;
import java.util.Scanner;

enum PLAYER_WON {
    X,
    O,
    NONE
}

enum CURRENT_USER {
    X,
    O
}

public class Main {
    static PLAYER_WON winner = PLAYER_WON.NONE;
    static Scanner scanner = new Scanner(System.in);
    static CURRENT_USER currentUser = CURRENT_USER.X;
    static char[][] gameTemplate = {
            {' ', ' ', ' '},
            {' ', ' ', ' '},
            {' ', ' ', ' '},
    };

    public static void main(String[] args) {
        runGame();
    }

    public static void runGame() {
        boolean runGame = true;

        while (runGame) {
            printGrid();
            runGame = shouldGameContinue();
            if (runGame) {
                getUserInput();
            }
        }
    }

    public static char[][] convertGameTemplateToChars(String gameTemplate) {
        char[][] result = new char[3][3];
        for (int i = 0; i < 3; i++) {
            String gameTemplateRowAsString = gameTemplate.substring(i * 3, i * 3 + 3);
            char[] gameTemplateCharRow = new char[3];
            for (int j = 0; j < gameTemplateRowAsString.length(); j++) {
                gameTemplateCharRow[j] = gameTemplateRowAsString.charAt(j);
            }
            result[i] = gameTemplateCharRow;
        }

        return result;
    }


    public static void getUserInput() {
        boolean userPromptedWrongData = true;

        while (userPromptedWrongData) {
            System.out.println("Enter the coordinates([row][column], range: (1 - 3)):");
            int firstCoordinate, secondCoordinate;

            try {
                firstCoordinate = scanner.nextInt() - 1;
                secondCoordinate = scanner.nextInt() - 1;
            } catch (InputMismatchException err) {
                System.out.println("You should enter only numbers!");
                scanner.nextLine();
                continue;
            }

            scanner.nextLine();

            boolean userPromptedValidValues = userEnteredCoordinatesFromRange(firstCoordinate) && userEnteredCoordinatesFromRange(secondCoordinate);

            if (!userPromptedValidValues) {
                continue;
            }

            boolean cellAlreadyOccupied = checkCellAlreadyOccupied(firstCoordinate, secondCoordinate);


            if (cellAlreadyOccupied) {
                System.out.println("This cell is occupied! Choose another one!");
            } else {
                gameTemplate[firstCoordinate][secondCoordinate] = getCurrentUser();
                userPromptedWrongData = false;
                changeCurrentPlayer();
            }
        }
    }

    public static void changeCurrentPlayer() {
        currentUser = currentUser == CURRENT_USER.X ? CURRENT_USER.O : CURRENT_USER.X;
    }

    public static char getCurrentUser() {
        if (currentUser == CURRENT_USER.X) return 'X';
        return 'O';
    }

    public static boolean checkCellAlreadyOccupied(int firstCoordinate, int secondCoordinate) {
        char gameTemplateCharFromGivenCoordinates = gameTemplate[firstCoordinate][secondCoordinate];
        return gameTemplateCharFromGivenCoordinates == 'X' || gameTemplateCharFromGivenCoordinates == 'O';
    }

    public static boolean userEnteredCoordinatesFromRange(int coordinate) {
        if (coordinate == 0 || coordinate == 1 || coordinate == 2) return true;

        if (coordinate > 2) {
            System.out.println("Coordinates should be from 1 to 3!");
        }

        return false;
    }

    public static void printGrid() {
        printDashes();
        printAllRows();
        printDashes();
    }

    public static void printDashes() {
        System.out.print("---------\n");
    }

    public static void printAllRows() {
        for (int i = 0; i < gameTemplate.length; i++) {
            char[] templateRowCharacters = gameTemplate[i];
            printTemplateRow(templateRowCharacters);
        }
    }

    public static void printTemplateRow(char[] templateRowCharacters) {
        System.out.printf("| %c %c %c |\n", templateRowCharacters[0], templateRowCharacters[1], templateRowCharacters[2]);
    }

    public static boolean shouldGameContinue() {
        if (checkImpossible()) {
            System.out.println("Impossible");
        } else if (checkSomeoneWon()) {
            System.out.printf("%s wins", winner);
        } else if (checkDraw()) {
            System.out.println("Draw");
        } else {
            System.out.println("Game not finished");
            return true;
        }

        return false;
    }

    public static boolean checkImpossible() {
        return checkTooManyWinningPatterns() || checkTooManySameCharacters();
    }

    public static boolean checkTooManyWinningPatterns() {
        int numberOfEqualThreeCharsInRows = getNumberOfThreeEqualCharsInRow();
        int numberOfEqualThreeCharsInColumns = getNumberOfThreeEqualCharsInColumn();
        int numberOfEqualThreeCharsInDiagonals = getNumberOfThreeEqualCharsInDiagonal();

        return numberOfEqualThreeCharsInRows > 1 || numberOfEqualThreeCharsInColumns > 1 || numberOfEqualThreeCharsInDiagonals > 1;
    }

    public static boolean checkTooManySameCharacters() {
        int numberOfOCharacters = getNumberOfGivenChar('X');
        int numberOfXCharacters = getNumberOfGivenChar('O');
        int numberOfCharactersDifference = Math.abs(numberOfXCharacters - numberOfOCharacters);

        return numberOfCharactersDifference > 1;
    }

    public static int getNumberOfGivenChar(char characterToBeFound) {
        int charOccurrences = 0;
        for (int i = 0; i < gameTemplate.length; i++) {
            for (int j = 0; j < gameTemplate[i].length; j++)
                if (gameTemplate[i][j] == characterToBeFound) {
                    charOccurrences++;
                }
        }

        return charOccurrences;
    }

    public static boolean checkDraw() {
        return !checkSomeoneWon() && !checkGameTemplateContainsSpace();
    }

    public static boolean checkGameTemplateContainsSpace() {
        for (int i = 0; i < gameTemplate.length; i++) {
            for (int j = 0; j < gameTemplate[i].length; j++) {
                if (gameTemplate[i][j] == ' ')
                    return true;
            }
        }
        return false;
    }


    public static boolean checkSomeoneWon() {
        return getNumberOfThreeEqualCharsInRow() == 1 || getNumberOfThreeEqualCharsInColumn() == 1 || getNumberOfThreeEqualCharsInDiagonal() == 1;
    }

    public static int getNumberOfThreeEqualCharsInRow() {
        int threeSameCharsInRowCounter = 0;
        for (int i = 0; i < 3; i++) {
            boolean rowSomeoneWon = threeEqualChars(gameTemplate[i]);
            if (rowSomeoneWon) {
                changeWinner(gameTemplate[i][0]);
                threeSameCharsInRowCounter++;
            }
        }

        return threeSameCharsInRowCounter;
    }

    public static int getNumberOfThreeEqualCharsInColumn() {
        int threeSameCharsInColumnCounter = 0;
        for (int i = 0; i < gameTemplate.length; i++) {
            char[] column = new char[3];
            for (int j = 0; j < gameTemplate.length; j++) {
                column[j] = gameTemplate[j][i];
            }
            boolean columnSomeoneWon = threeEqualChars(column);

            if (columnSomeoneWon) {
                changeWinner(column[0]);
                threeSameCharsInColumnCounter++;
            }
        }

        return threeSameCharsInColumnCounter;
    }

    public static int getNumberOfThreeEqualCharsInDiagonal() {
        int threeSameCharsInDiagonalCounter = 0;
        char[] diagonal1 = {
                gameTemplate[0][0],
                gameTemplate[1][1],
                gameTemplate[2][2]
        };
        char[] diagonal2 = {
                gameTemplate[0][2],
                gameTemplate[1][1],
                gameTemplate[2][0]
        };

        if (threeEqualChars(diagonal1)) {
            threeSameCharsInDiagonalCounter++;
            changeWinner(diagonal1[0]);
        }
        if (threeEqualChars(diagonal2)) {
            threeSameCharsInDiagonalCounter++;
            changeWinner(diagonal2[0]);
        }

        return threeSameCharsInDiagonalCounter;
    }

    public static boolean threeEqualChars(char[] charactersToBeChecked) {
        return charactersToBeChecked[0] == charactersToBeChecked[1] && charactersToBeChecked[1] == charactersToBeChecked[2] && charactersToBeChecked[0] != ' ';
    }

    public static void changeWinner(char winnerCharacter) {
        if (winnerCharacter == 'O') {
            winner = PLAYER_WON.O;
        } else {
            winner = PLAYER_WON.X;
        }
    }
}
