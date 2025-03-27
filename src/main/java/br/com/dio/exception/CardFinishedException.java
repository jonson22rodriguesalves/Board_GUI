package br.com.dio.exception;

/**
 * Exceção lançada quando uma operação é inválida porque o Card já está finalizado.
 * Tipicamente ocorre ao tentar mover um Card que já está na coluna FINAL.
 */
public class CardFinishedException extends RuntimeException {

    /**
     * Cria uma nova instância da exceção com uma mensagem de erro específica.
     * 
     * @param message Mensagem detalhando a razão da exceção, deve incluir:
     *                - ID do Card
     *                - Operação que falhou
     *                - Motivo da falha (card já finalizado)
     */
    public CardFinishedException(final String message) {
        super(message);
    }
}