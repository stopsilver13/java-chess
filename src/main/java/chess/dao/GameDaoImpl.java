package chess.dao;

import chess.domain.DBConnector;
import chess.dto.GameDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameDaoImpl implements GameDao {
    private static final DBConnector CONNECTOR = DBConnector.getInstance();
    private static final int WHITE_TURN = 2;

    private static class GameDAOImplHolder {
        private static final GameDao instance = new GameDaoImpl();
    }

    public static GameDao getInstance() {
        return GameDAOImplHolder.instance;
    }

    @Override
    public int addGame() {
        String query = "INSERT INTO game (team_id) VALUES (?)";
        int result;

        try (Connection con = CONNECTOR.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, WHITE_TURN);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                rs.next();
                result = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("게임을 생성할 수 없습니다.");
        }
        return result;
    }

    @Override
    public GameDto findById(int id) {
        String query = "SELECT id, is_end, team_id FROM game WHERE id = ?";
        GameDto game = null;

        try (Connection con = CONNECTOR.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) return game;

            game = new GameDto(rs.getInt("id"),
                    rs.getBoolean("is_end"),
                    rs.getInt("team_id"));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("게임 정보를 받아올 수 없습니다.");
        }
        return game;
    }

    @Override
    public List<GameDto> findNotEndGames() {
        String query = "SELECT id, is_end, team_id FROM game WHERE is_end = 0";
        List<GameDto> gameDtos = new ArrayList<>();

        try (Connection con = CONNECTOR.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                boolean isEnd = rs.getBoolean("is_end");
                int teamId = rs.getInt("team_id");
                gameDtos.add(new GameDto(id, isEnd, teamId));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("끝나지 않은 게임 정보를 받아올 수 없습니다.");
        }
        return gameDtos;
    }

    @Override
    public int updateGame(GameDto gameDto) {
        String query = "UPDATE game SET team_id=?, is_end=?  WHERE id=?";
        int result;

        try (Connection con = CONNECTOR.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, gameDto.getTurn().getTeamId());
            pstmt.setBoolean(2, gameDto.isEnd());
            pstmt.setInt(3, gameDto.getId());
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("게임 정보를 업데이트 할 수 없습니다.");
        }
        return result;
    }

    @Override
    public int deleteGameByid(int id) {
        String query = "DELETE FROM game WHERE id=?";
        int result;

        try (Connection con = CONNECTOR.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, id);
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("게임을 삭제할 수 없습니다.");
        }
        return result;
    }
}
