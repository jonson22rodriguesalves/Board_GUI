package br.com.dio.exception;

/**
 * Exceção lançada quando uma operação não pode ser realizada
 * porque o Card está atualmente bloqueado.
 * 
 * <p>Tipicamente ocorre nas seguintes situações:</p>
 * <ul>
 *   <li>Tentativa de mover um Card bloqueado</li>
 *   <li>Tentativa de bloquear um Card já bloqueado</li>
 *   <li>Operações que requerem Cards desbloqueados</li>
 * </ul>
 */
public class CardBlockedException extends RuntimeException {

    /**
     * Cria uma nova instância da exceção com uma mensagem descritiva.
     * 
     * @param message Mensagem detalhada que deve incluir:
     *                - ID do Card bloqueado
     *                - Operação que falhou
     *                - Motivo do bloqueio (se disponível)
     */
    public CardBlockedException(final String message) {
        super(message);
    }
}