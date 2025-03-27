package br.com.dio.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import br.com.dio.dto.CardDetailsDTO;
import br.com.dio.persistence.dao.CardDAO;

/**
 * Serviço de consulta de cards (cartões) que fornece operações de leitura
 * para recuperar informações detalhadas sobre cards do sistema.
 * 
 * <p>Oferece flexibilidade para ser instanciado tanto com uma conexão direta
 * quanto com um DAO previamente configurado.</p>
 */
public class CardQueryService {
    
    // Conexão com o banco de dados (opcional)
    private final Connection connection;
    
    // DAO (Data Access Object) para operações com cards (opcional)
    private CardDAO cardDAO;
  
    /**
     * Constrói o serviço utilizando uma conexão JDBC diretamente.
     * O CardDAO será instanciado internamente quando necessário.
     * 
     * @param connection Conexão ativa com o banco de dados
     */
    public CardQueryService(Connection connection) {
        this.connection = connection;
    }

    /**
     * Constrói o serviço utilizando um CardDAO previamente configurado.
     * Útil para injeção de dependências ou testes.
     * 
     * @param cardDAO Instância de CardDAO configurada e pronta para uso
     */
    public CardQueryService(CardDAO cardDAO) {
        this.cardDAO = cardDAO;
        this.connection = null; // Conexão não é necessária quando o DAO é fornecido
    }

    /**
     * Busca um card pelo seu ID, retornando um objeto Optional que pode conter
     * os detalhes do card se encontrado.
     * 
     * @param id ID do card a ser buscado
     * @return Optional contendo CardDetailsDTO se encontrado, ou vazio caso contrário
     * @throws SQLException Em caso de erros de acesso ao banco de dados
     */
    public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
        CardDAO dao;
        
        // Decide qual DAO utilizar - o injetado ou um novo criado a partir da conexão
        if (cardDAO != null) {
            dao = cardDAO;
        } else {
            dao = new CardDAO(connection);
        }
        
        return dao.findById(id);
    }
}