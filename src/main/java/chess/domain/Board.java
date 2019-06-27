package chess.domain;

import chess.domain.piece.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Board {
    private Map<Position, Piece> pieces = new HashMap<>();
    private Aliance thisTurn = Aliance.WHITE;

    public Board() {}

    public void initBoard() {
        pieces.put(Position.valueOf("a1"), new Rook(Aliance.WHITE, PieceValue.ROOK));
        pieces.put(Position.valueOf("b1"), new Knight(Aliance.WHITE, PieceValue.KNIGHT));
        pieces.put(Position.valueOf("c1"), new Bishop(Aliance.WHITE, PieceValue.BISHOP));
        pieces.put(Position.valueOf("d1"), new Queen(Aliance.WHITE, PieceValue.QUEEN));
        pieces.put(Position.valueOf("e1"), new King(Aliance.WHITE, PieceValue.KING));
        pieces.put(Position.valueOf("f1"), new Bishop(Aliance.WHITE, PieceValue.BISHOP));
        pieces.put(Position.valueOf("g1"), new Knight(Aliance.WHITE, PieceValue.KNIGHT));
        pieces.put(Position.valueOf("h1"), new Rook(Aliance.WHITE, PieceValue.ROOK));

        pieces.put(Position.valueOf("a8"), new Rook(Aliance.BLACK, PieceValue.ROOK));
        pieces.put(Position.valueOf("b8"), new Knight(Aliance.BLACK, PieceValue.KNIGHT));
        pieces.put(Position.valueOf("c8"), new Bishop(Aliance.BLACK, PieceValue.BISHOP));
        pieces.put(Position.valueOf("d8"), new Queen(Aliance.BLACK, PieceValue.QUEEN));
        pieces.put(Position.valueOf("e8"), new King(Aliance.BLACK, PieceValue.KING));
        pieces.put(Position.valueOf("f8"), new Bishop(Aliance.BLACK, PieceValue.BISHOP));
        pieces.put(Position.valueOf("g8"), new Knight(Aliance.BLACK, PieceValue.KNIGHT));
        pieces.put(Position.valueOf("h8"), new Rook(Aliance.BLACK, PieceValue.ROOK));

        List<Position> whitePawnPositions = Position.getRowPositions("2");
        List<Position> blackPawnPositions = Position.getRowPositions("7");

        for (Position whitePawnPosition : whitePawnPositions) {
            pieces.put(whitePawnPosition, new Pawn(Aliance.WHITE, PieceValue.PAWN));
        }

        for (Position blackPawnPosition : blackPawnPositions) {
            pieces.put(blackPawnPosition, new Pawn(Aliance.BLACK, PieceValue.PAWN));
        }
    }

    public void putPiece(String position, int teamId, int kindId) {
        if (kindId == 1) {
            pieces.put(Position.valueOf(position), new King(Aliance.valueOf(teamId), PieceValue.valueOf(kindId)));
        }
        if (kindId == 2) {
            pieces.put(Position.valueOf(position), new Queen(Aliance.valueOf(teamId), PieceValue.valueOf(kindId)));
        }
        if (kindId == 3) {
            pieces.put(Position.valueOf(position), new Rook(Aliance.valueOf(teamId), PieceValue.valueOf(kindId)));
        }
        if (kindId == 4) {
            pieces.put(Position.valueOf(position), new Knight(Aliance.valueOf(teamId), PieceValue.valueOf(kindId)));
        }
        if (kindId == 5) {
            pieces.put(Position.valueOf(position), new Bishop(Aliance.valueOf(teamId), PieceValue.valueOf(kindId)));
        }
        if (kindId == 6) {
            pieces.put(Position.valueOf(position), new Pawn(Aliance.valueOf(teamId), PieceValue.valueOf(kindId)));
        }
    }

    public Piece pieceValueOf(String position) {
        if(position ==null){
            return null;
        }
        return pieces.get(Position.valueOf(position));
    }

    public Aliance switchTurn() {
        if (thisTurn == Aliance.WHITE) {
            thisTurn = Aliance.BLACK;
            return thisTurn;
        }
        thisTurn = Aliance.WHITE;
        return thisTurn;
    }

    public void movePiece(String start, String end) {
        checkValidStartPosition(start);
        checkValidEndPosition(end);

        Position startPosition = Position.valueOf(start);
        Position endPosition = Position.valueOf(end);

        Piece piece = pieces.get(startPosition);

        Navigator navigator = new Navigator(startPosition, endPosition);
        piece.checkPossibleMove(this, startPosition, navigator);
        navigator.simulateMove(this, startPosition);

        pieces.remove(startPosition);
        pieces.put(endPosition, piece);
    }

    private void checkValidEndPosition(String position) {
        if (pieceValueOf(position) == null) {
            return;
        }

        if(!pieceValueOf(position).isDifferentTeam(thisTurn)){
            throw new IllegalArgumentException("우리팀 말을 공격할 수 없습니다.");
        }
    }

    private void checkValidStartPosition(String position) {
        if (pieceValueOf(position) == null) {
            throw new IllegalArgumentException("해당 위치에 말이 없습니다.");
        }

        if(pieceValueOf(position).isDifferentTeam(thisTurn)){
            throw new IllegalArgumentException("상대팀 말은 움직일 수 없습니다.");
        }
    }

    public void checkUnOccupiedPosition(String position){
        if (pieceValueOf(position) != null) {
            throw new IllegalArgumentException("이동경로에 다른 말이 있습니다.");
        }
    }

    public long getDuplicatePawnCount(Aliance aliance){
        List<Position> pawnPositions = pieces.keySet().stream()
                .filter(k -> pieces.get(k).getAliance() == aliance)
                .filter(k -> pieces.get(k).getPieceValue().getName() == "P")
                .collect(Collectors.toList());

        List<Character> pawnColumns = pawnPositions.stream()
                .map(p -> p.toString().charAt(0))
                .collect(Collectors.toList());

        Map<Character, Long> pawnColumnCount = pawnColumns.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return pawnColumnCount.values().stream()
                .filter(i -> i > 1)
                .reduce((long) 0, (a, b) -> a + b);
    }

    public boolean isKingAlive(Aliance aliance){
        return pieces.values().stream()
                .filter(p -> p.getAliance() == aliance)
                .filter(p -> p.getPieceValue().getName() == "K")
                .count() != 0;
    }

    public Map<Position, Piece> getPieces(){
        return pieces;
    }

    public Aliance getThisTurn() {
        return thisTurn;
    }

    public void setThisTurn(Aliance thisTurn) {
        this.thisTurn = thisTurn;
    }
}
