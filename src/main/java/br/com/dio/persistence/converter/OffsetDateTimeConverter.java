package br.com.dio.persistence.converter;

import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;
import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PRIVATE;

/**
 * Classe utilitária para conversão entre OffsetDateTime e Timestamp.
 * Facilita a persistência de datas com offset em bancos de dados SQL.
 */
@NoArgsConstructor(access = PRIVATE)
public final class OffsetDateTimeConverter {

    /**
     * Converte um Timestamp do JDBC para OffsetDateTime.
     * @param value Timestamp a ser convertido (pode ser null)
     * @return OffsetDateTime correspondente ou null se o input for null
     */
    public static OffsetDateTime toOffsetDateTime(final Timestamp value) {
        return nonNull(value) ? OffsetDateTime.ofInstant(value.toInstant(), UTC) : null;
    }

    /**
     * Converte um OffsetDateTime para Timestamp do JDBC.
     * @param value OffsetDateTime a ser convertido (pode ser null)
     * @return Timestamp correspondente ou null se o input for null
     */
    public static Timestamp toTimestamp(final OffsetDateTime value) {
        return nonNull(value) ? Timestamp.valueOf(value.atZoneSameInstant(UTC).toLocalDateTime()) : null;
    }
}