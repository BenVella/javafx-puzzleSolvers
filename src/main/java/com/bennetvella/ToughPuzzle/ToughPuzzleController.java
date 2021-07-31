package com.bennetvella.ToughPuzzle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import org.graalvm.compiler.java.GraphBuilderPhase.Instance;

class GameBoard {
    enum shapes {
        heart, spade, club, diamond
    }

    LinkedList<Piece> qPieces = new LinkedList<>();
    Piece[][] solution = new Piece[3][3];
    int currentX = 0, currentY = 0; // Origin is top left

    ArrayList<String> piecesOrderInput = new ArrayList<String>(
            Arrays.asList("1,heart,spade,spade,club", "2,spade,spade,heart,club", "3,spade,diamond,spade,heart",
                    "4,heart,diamond,diamond,heart", "5,diamond,club,club,diamond", "6,spade,diamond,heart,diamond",
                    "7,club,heart,spade,heart", "8,club,heart,diamond,club", "9,heart,diamond,club,club"));

    public GameBoard() {
        PopulateBoard();
    }

    public GameBoard(ArrayList<String> piecesInput) throws IllegalArgumentException {
        if (Math.sqrt(piecesInput.size()) % 1 != 0)
            throw new IllegalArgumentException("Input must be a squarable value");

        if (piecesInput.size() != 9)
            throw new UnsupportedOperationException("Only a Tough Puzzle board of size 9 is currently supported");

        this.piecesOrderInput = piecesInput;
    }

    void PopulateBoard() {
        piecesOrderInput.forEach((val) -> {
            var tempPiece = new Piece(val);
            if (!qPieces.contains(tempPiece))
                qPieces.add(new Piece(val));
            else
                System.err.println("Repeated Piece");
        });
    }

    public boolean SolvePuzzle() {
        // Poll queue for next Piece
        // Try to fit piece in Grid
        // -- Automatically fully rotates piece until it fits or doesn't and backs out
        // If fits, move on to next slot and next piece
        // If not, even without rotations, add piece to end of queue and poll next piece
        var currentPiece = qPieces.poll();
        int count = 0;
        int pieceRotations = 0;
        do {
            if (FitPiece(currentPiece)) {
                pieceRotations = 0;
                currentPiece = qPieces.poll();
            } else if (pieceRotations < 4) {
                pieceRotations++;
                currentPiece.RotateClockwise();
                FitPiece(currentPiece);
            } else {
                currentPiece.ResetRotation();
                qPieces.add(currentPiece);
            }
        } while (count++ < 100);

        return false; // Couldn't solve it or can't be solved
    }

    boolean FitPiece(Piece p) {
        // Check existing neighbours
        if (
            (currentX == 0 || CheckLocks(solution[currentX-1][currentY].east, p.west)) && // West of Piece
            (currentX >= solution[0].length - 1 || CheckLocks(solution[currentX-1][currentY].west, p.east)) && // East of Piece
            (currentY == 0 || CheckLocks(solution[currentX][currentY-1].south, p.north)) && // North of Piece
            (currentY >= solution.length - 1 || CheckLocks(solution[currentX][currentY-1].north, p.south)) // South of Piece
        )
            return true;
        return false;
    }

    boolean CheckLocks (Interlock first, Interlock second) {
        if ((first instanceof MaleLock && second instanceof FemaleLock) ||
        (first instanceof FemaleLock && second instanceof MaleLock)) 
            return first.shape == second.shape;

        return false;
    }
}

class Piece {
    int id;
    Interlock west, north, east, south;
    int rotations = 0;

    public Piece(int id, GameBoard.shapes west, GameBoard.shapes north, GameBoard.shapes east, GameBoard.shapes south) {
        this.id = id;
        this.west = new MaleLock(west);
        this.north = new MaleLock(north);
        this.east = new FemaleLock(east);
        this.south = new FemaleLock(south);
    }

    public Piece(String csvSource) {
        var csvSplit = csvSource.split(",");

        this.id = Integer.parseInt(csvSplit[0]);
        this.west = new MaleLock(StringToShape(csvSplit[1]));
        this.north = new MaleLock(StringToShape(csvSplit[2]));
        this.east = new FemaleLock(StringToShape(csvSplit[3]));
        this.south = new FemaleLock(StringToShape(csvSplit[4]));
    }

    GameBoard.shapes StringToShape(String val) {
        return GameBoard.shapes.valueOf(val);
    }

    public boolean RotateClockwise() {
        rotations = (4 + rotations + 1) % 4;
        Interlock temp = this.west;
        this.west = this.south;
        this.south = this.east;
        this.east = this.north;
        this.north = temp;
        return true;
    }

    public boolean RotateCounterClockwise() {
        rotations = (4 + rotations - 1) % 4;
        Interlock temp = this.west;
        this.west = this.north;
        this.north = this.east;
        this.east = this.south;
        this.south = temp;
        return true;
    }

    public void ResetRotations() {
        for (int i = rotations; i > 0; i--) {
            RotateCounterClockwise();
        }
        System.out.println("Reset rotations to: " + rotations);
    }

    // Used for comparators in queue contains
    public boolean equals(Object other) {
        return other instanceof Piece && ((Piece) other).id == this.id;
    }
}

class Interlock {
    GameBoard.shapes shape;

    protected Interlock(GameBoard.shapes shape) {
        this.shape = shape;
    }
}

class MaleLock extends Interlock {
    public MaleLock(GameBoard.shapes shape) {
        super(shape);
    }
}

class FemaleLock extends Interlock {
    public FemaleLock(GameBoard.shapes shape) {
        super(shape);
    }
}

/**
 * Entry point to Tough Puzzle Controls Launches a GameBoard with hardcoded
 * defaults resolves and pushes out the output top to bottom, left to right
 */
public class ToughPuzzleController {
    GameBoard board;

    public ToughPuzzleController() {
        board = new GameBoard();
    }
}