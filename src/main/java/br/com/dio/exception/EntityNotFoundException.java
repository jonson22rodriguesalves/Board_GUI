package br.com.dio.exception;

/**
 * Exceção lançada quando uma entidade não é encontrada no sistema.
 * Utilizada principalmente em operações de busca por ID quando
 * o registro solicitado não existe na base de dados.
 */
public class EntityNotFoundException extends RuntimeException {

    /**
     * Cria uma nova instância da exceção com uma mensagem específica.
     * @param message Mensagem detalhando qual entidade não foi encontrada
     */
    public EntityNotFoundException(String message) {
        super(message);
    }
}