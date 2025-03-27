package br.com.dio.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.dio.dto.BoardDetailsDTO;
import br.com.dio.persistence.dao.BoardColumnDAO;
import br.com.dio.persistence.dao.BoardDAO;
import br.com.dio.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

/**
 * Serviço de consulta de Boards (Quadros) responsável por recuperar
 * informações de boards e suas colunas associadas do banco de dados.
 */
@AllArgsConstructor
public class BoardQueryService {

    // Conexão com o banco de dados
    private final Connection connection;

    /**
     * Recupera todos os boards cadastrados no sistema.
     * @return Lista de BoardEntity contendo os dados básicos dos boards
     * @throws SQLException Em caso de erro no banco de dados
     */
    public List<BoardEntity> findAll() throws SQLException {
        String sql = "SELECT * FROM boards";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            List<BoardEntity> boards = new ArrayList<>();
            while (rs.next()) {
                BoardEntity board = new BoardEntity();
                board.setId(rs.getLong("id"));
                board.setName(rs.getString("name"));
                // Carrega apenas dados básicos (colunas podem ser carregadas posteriormente se necessário)
                boards.add(board);
            }
            return boards;
        }
    }

    /**
     * Busca um board pelo seu ID, incluindo todas as suas colunas associadas.
     * @param id ID do board a ser buscado
     * @return Optional contendo o BoardEntity completo se encontrado, ou vazio caso contrário
     * @throws SQLException Em caso de erro no banco de dados
     */
    public Optional<BoardEntity> findById(final Long id) throws SQLException {
        var dao = new BoardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);
        var optional = dao.findById(id);
        
        if (optional.isPresent()) {
            var entity = optional.get();
            // Carrega as colunas associadas ao board
            entity.setBoardColumns(boardColumnDAO.findByBoardId(entity.getId()));
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    /**
     * Recupera os detalhes completos de um board, incluindo informações resumidas de suas colunas.
     * @param id ID do board a ser consultado
     * @return Optional contendo BoardDetailsDTO com informações consolidadas se encontrado, ou vazio caso contrário
     * @throws SQLException Em caso de erro no banco de dados
     */
    public Optional<BoardDetailsDTO> showBoardDetails(final Long id) throws SQLException {
        var dao = new BoardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);
        var optional = dao.findById(id);
        
        if (optional.isPresent()) {
            var entity = optional.get();
            // Obtém as colunas com detalhes adicionais
            var columns = boardColumnDAO.findByBoardIdWithDetails(entity.getId());
            var dto = new BoardDetailsDTO(entity.getId(), entity.getName(), columns);
            return Optional.of(dto);
        }
        return Optional.empty();
    }
}