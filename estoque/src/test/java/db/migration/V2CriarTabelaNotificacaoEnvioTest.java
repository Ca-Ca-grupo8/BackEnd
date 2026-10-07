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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * A migration e validada contra H2 em MODE=MySQL para nao depender de MySQL real,
 * no mesmo espirito do teste da V1.
 */
class V2CriarTabelaNotificacaoEnvioTest {

    private static final AtomicInteger CONTADOR = new AtomicInteger();

    private DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource(
                "jdbc:h2:mem:notificacao_envio_" + CONTADOR.incrementAndGet() + ";MODE=MySQL;DB_CLOSE_DELAY=-1",
                "sa", "");
        ds.setDriverClassName("org.h2.Driver");

        return ds;
    }

    private void executar(DataSource ds) throws Exception {
        try (Connection conexao = ds.getConnection()) {
            Context context = mock(Context.class);
            when(context.getConnection()).thenReturn(conexao);

            new V2__criar_tabela_notificacao_envio().migrate(context);
        }
    }

    private boolean tabelaExiste(DataSource ds, String tabela) throws SQLException {
        try (Connection conexao = ds.getConnection();
             ResultSet rs = conexao.getMetaData().getTables(null, null, "%", null)) {

            while (rs.next()) {
                if (tabela.equalsIgnoreCase(rs.getString("TABLE_NAME"))) {
                    return true;
                }
            }
        }

        return false;
    }

    private void inserir(DataSource ds, long maletaId, long templateId) throws SQLException {
        try (Connection conexao = ds.getConnection();
             Statement st = conexao.createStatement()) {
            st.execute("INSERT INTO notificacao_envio (maleta_id, template_id, data_envio) VALUES ("
                    + maletaId + ", " + templateId + ", CURRENT_TIMESTAMP)");
        }
    }

    @Test
    @DisplayName("Deve criar a tabela de envios quando ela ainda nao existe")
    void deveCriarTabela() throws Exception {
        DataSource ds = dataSource();

        executar(ds);

        assertTrue(tabelaExiste(ds, "notificacao_envio"));
    }

    @Test
    @DisplayName("Deve permitir gravar um envio por maleta e template")
    void devePermitirGravarEnvio() throws Exception {
        DataSource ds = dataSource();
        executar(ds);

        inserir(ds, 1L, 3L);

        try (Connection conexao = ds.getConnection();
             Statement st = conexao.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM notificacao_envio WHERE maleta_id = 1")) {

            assertTrue(rs.next());
            assertEquals(1, rs.getInt(1));
        }
    }

    @Test
    @DisplayName("Deve permitir o mesmo template para maletas diferentes")
    void devePermitirMesmoTemplateEmMaletasDiferentes() throws Exception {
        DataSource ds = dataSource();
        executar(ds);

        inserir(ds, 1L, 3L);
        inserir(ds, 2L, 3L);

        try (Connection conexao = ds.getConnection();
             Statement st = conexao.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM notificacao_envio WHERE template_id = 3")) {

            assertTrue(rs.next());
            assertEquals(2, rs.getInt(1));
        }
    }

    @Test
    @DisplayName("Deve permitir templates diferentes para a mesma maleta")
    void devePermitirTemplatesDiferentesNaMesmaMaleta() throws Exception {
        DataSource ds = dataSource();
        executar(ds);

        inserir(ds, 1L, 3L);
        inserir(ds, 1L, 4L);

        try (Connection conexao = ds.getConnection();
             Statement st = conexao.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM notificacao_envio WHERE maleta_id = 1")) {

            assertTrue(rs.next());
            assertEquals(2, rs.getInt(1));
        }
    }

    @Test
    @DisplayName("Deve rejeitar envio duplicado do mesmo template para a mesma maleta")
    void deveRejeitarDuplicidade() throws Exception {
        DataSource ds = dataSource();
        executar(ds);
        inserir(ds, 1L, 3L);

        assertThrows(SQLException.class, () -> inserir(ds, 1L, 3L));
    }

    @Test
    @DisplayName("Deve nao falhar quando a tabela ja existe")
    void deveSerIdempotente() throws Exception {
        DataSource ds = dataSource();

        executar(ds);
        assertDoesNotThrow(() -> executar(ds));

        assertTrue(tabelaExiste(ds, "notificacao_envio"));
    }
}