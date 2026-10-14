package db.migration;

import org.flywaydb.core.api.migration.Context;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * A migration e validada contra H2 em MODE=MySQL para nao depender de MySQL real,
 * no mesmo espirito dos testes das V1/V2/V4.
 */
class V5AdicionarUsuarioTipoReferenciaTest {

    private static final AtomicInteger CONTADOR = new AtomicInteger();

    private DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource(
                "jdbc:h2:mem:notificacao_tipo_" + CONTADOR.incrementAndGet() + ";MODE=MySQL;DB_CLOSE_DELAY=-1",
                "sa", "");
        ds.setDriverClassName("org.h2.Driver");

        return ds;
    }

    private void criarTabelaComEnumAntigo(DataSource ds) throws SQLException {
        try (Connection conexao = ds.getConnection();
             Statement st = conexao.createStatement()) {
            st.execute(
                    "CREATE TABLE notificacao_envio ("
                            + "id_notificacao_envio BIGINT NOT NULL AUTO_INCREMENT, "
                            + "tipo_referencia ENUM('ESTOQUE','MALETA','REVENDEDORA') NOT NULL, "
                            + "referencia_id BIGINT NOT NULL, "
                            + "template_id BIGINT NOT NULL, "
                            + "permite_reenvio BOOLEAN NOT NULL, "
                            + "data_envio DATETIME NOT NULL, "
                            + "PRIMARY KEY (id_notificacao_envio))"
            );
        }
    }

    private void executar(DataSource ds) throws Exception {
        try (Connection conexao = ds.getConnection()) {
            Context context = mock(Context.class);
            when(context.getConnection()).thenReturn(conexao);

            new V5__adicionar_usuario_tipo_referencia().migrate(context);
        }
    }

    private void inserir(DataSource ds, String tipoReferencia) throws SQLException {
        try (Connection conexao = ds.getConnection();
             Statement st = conexao.createStatement()) {
            st.execute("INSERT INTO notificacao_envio "
                    + "(tipo_referencia, referencia_id, template_id, permite_reenvio, data_envio) "
                    + "VALUES ('" + tipoReferencia + "', 1, 12, FALSE, CURRENT_TIMESTAMP)");
        }
    }

    private int contar(DataSource ds) throws SQLException {
        try (Connection conexao = ds.getConnection();
             Statement st = conexao.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM notificacao_envio")) {
            rs.next();
            return rs.getInt(1);
        }
    }

    @Test
    @DisplayName("Deve aceitar o tipo_referencia USUARIO apos a migration")
    void deveAceitarUsuario() throws Exception {
        DataSource ds = dataSource();
        criarTabelaComEnumAntigo(ds);

        executar(ds);

        assertDoesNotThrow(() -> inserir(ds, "USUARIO"));
        assertEquals(1, contar(ds));
    }

    @Test
    @DisplayName("Deve preservar os registros existentes com valores antigos")
    void devePreservarRegistrosExistentes() throws Exception {
        DataSource ds = dataSource();
        criarTabelaComEnumAntigo(ds);
        inserir(ds, "REVENDEDORA");

        executar(ds);

        assertEquals(1, contar(ds));
        assertDoesNotThrow(() -> inserir(ds, "USUARIO"));
        assertEquals(2, contar(ds));
    }

    @Test
    @DisplayName("Deve ser idempotente quando executada mais de uma vez")
    void deveSerIdempotente() throws Exception {
        DataSource ds = dataSource();
        criarTabelaComEnumAntigo(ds);

        executar(ds);
        assertDoesNotThrow(() -> executar(ds));

        assertDoesNotThrow(() -> inserir(ds, "USUARIO"));
    }

    @Test
    @DisplayName("Deve ignorar quando a tabela nao existe")
    void deveIgnorarTabelaInexistente() {
        DataSource ds = dataSource();

        assertDoesNotThrow(() -> executar(ds));
    }
}
