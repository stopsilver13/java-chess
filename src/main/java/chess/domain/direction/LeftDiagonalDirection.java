package chess.domain.direction;

import chess.domain.Board;
import chess.domain.Position;

public class LeftDiagonalDirection implements Direction{
    private LeftDiagonalDirection () {}

    private static class LeftDiagonalDirectionHolder {
        private static final LeftDiagonalDirection instance = new LeftDiagonalDirection();
    }

    public static LeftDiagonalDirection getInstance () {
        return LeftDiagonalDirectionHolder.instance;
    }

    @Override
    public Position simulateUnitMove(Board board, Position position, boolean isReverseDirection) {
        if(isReverseDirection){
            Position newPosition = position.movePosition(-1,1);
            board.checkUnOccupiedPosition(newPosition.toString());
            return newPosition;
        }

        Position newPosition = position.movePosition(1,-1);
        board.checkUnOccupiedPosition(newPosition.toString());
        return newPosition;
    }

    @Override
    public int matchMoveCount(int rowDifference, int columnDifference) {
        return rowDifference;
    }
}
