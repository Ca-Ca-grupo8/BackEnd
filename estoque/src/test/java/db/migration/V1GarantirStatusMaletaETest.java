package db.migration;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.migration.Context;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * A migration e validada contra H2 em MODE=MySQL para nao depender de MySQL real.
 * Os cenarios cobrem os tres estados de schema que a base pode ter: legada
 * (com is_finalizada), intermediaria (sem a coluna) e sem tabela de vendas.
 */
class V1GarantirStatusMaletaETest {

    private static final AtomicInteger CONTADOR = new AtomicInteger();

    private static final String DDL_STATUS = """
            CREATE TABLE status_maleta (
                id BIGINT NOT NULL AUTO_INCREMENT,
                nome VARCHAR(32) DEFAULT NULL,
                PRIMARY KEY (id)
            )
            """;

    private static final String DDL_VENDA_COM_COLUNA_LEGADA = """
            CREATE TABLE movimentacao_revendedora (
                id_movimentacao_revendedora BIGINT NOT NULL AUTO_INCREMENT,
                nome VARCHAR(255) DEFAULT NULL,
                is_finalizada BIT(1) NOT NULL,
                status_maleta_id BIGINT DEFAULT NULL,
                PRIMARY KEY (id_movimentacao_revendedora)
            )
            """;

    private static final String DDL_VENDA_SEM_COLUNA_LEGADA = """
            CREATE TABLE movimentacao_revendedora (
                id_movimentacao_revendedora BIGINT NOT NULL AUTO_INCREMENT,
                nome VARCHAR(255) DEFAULT NULL,
                status_maleta_id BIGINT DEFAULT NULL,
                PRIMARY KEY (id_movimentacao_revendedora)
            )
            """;

    @Test
    @DisplayName("Deve semear os cinco status e preencher status nulo a partir da coluna legada")
    void deveSemearStatusEPreencherStatusNuloPelaColunaLegada() throws Exception {
        DataSource base = novaBase();
        try (Connection conexao = base.getConnection()) {
            executar(conexao, DDL_STATUS);
            executar(conexao, DDL_VENDA_COM_COLUNA_LEGADA);
            executar(conexao, "INSERT INTO status_maleta (id, nome) VALUES (0, 'ABERTO')");
            executar(conexao, """
                    INSERT INTO movimentacao_revendedora
                        (id_movimentacao_revendedora, nome, is_finalizada, status_maleta_id)
                    VALUES
                        (1, 'maleta-1', TRUE, NULL),
                        (2, 'maleta-2', TRUE, NULL),
                        (3, 'maleta-3', FALSE, NULL)
                    """);

            aplicar(conexao);

            assertEquals(List.of("ABERTO", "EM_ACERTO", "EM_CONSIGNACAO", "ERRO", "FINALIZADA"),
                    nomesDosStatus(conexao));
            assertEquals(List.of("FINALIZADA", "FINALIZADA", "ABERTO"),
                    statusDasMaletas(conexao));
            assertFalse(colunaExiste(conexao, "movimentacao_revendedora", "is_finalizada"),
                    "A coluna legada is_finalizada deve ser removida");
        }
    }

    @Test
    @DisplayName("Deve preencher com ABERTO quando a base nao tem a coluna legada")
    void devePreencherComAbertoQuandoNaoHaColunaLegada() throws Exception {
        DataSource base = novaBase();
        try (Connection conexao = base.getConnection()) {
            executar(conexao, DDL_STATUS);
            executar(conexao, DDL_VENDA_SEM_COLUNA_LEGADA);
            executar(conexao, """
                    INSERT INTO movimentacao_revendedora (id_movimentacao_revendedora, nome, status_maleta_id)
                    VALUES (1, 'maleta-1', NULL), (2, 'maleta-2', NULL)
                    """);

            aplicar(conexao);

            assertEquals(5, contar(conexao, "status_maleta"));
            assertEquals(List.of("ABERTO", "ABERTO"), statusDasMaletas(conexao));
            assertTrue(colunaExiste(conexao, "movimentacao_revendedora", "status_maleta_id"));
        }
    }

    @Test
    @DisplayName("Deve manter maletas que ja possuem status e nao duplicar status cadastrados")
    void deveManterStatusExistentes() throws Exception {
        DataSource base = novaBase();
        try (Connection conexao = base.getConnection()) {
            executar(conexao, DDL_STATUS);
            executar(conexao, DDL_VENDA_COM_COLUNA_LEGADA);
            for (String nome : List.of("ABERTO", "EM_CONSIGNACAO", "EM_ACERTO", "FINALIZADA", "ERRO")) {
                executar(conexao, "INSERT INTO status_maleta (nome) VALUES ('" + nome + "')");
            }
            executar(conexao, """
                    INSERT INTO movimentacao_revendedora (id_movimentacao_revendedora, nome, is_finalizada, status_maleta_id)
                    VALUES (1, 'maleta-1', FALSE, 2)
                    """);

            aplicar(conexao);

            assertEquals(5, contar(conexao, "status_maleta"), "Nao deve duplicar status ja cadastrados");
            assertEquals(List.of("EM_CONSIGNACAO"), statusDasMaletas(conexao),
                    "Nao deve sobrescrever maleta que ja tem status");
        }
    }

    @Test
    @DisplayName("Deve ignorar a carga quando a tabela de status ainda nao existe")
    void deveIgnorarCargaSemTabelaDeStatus() throws Exception {
        DataSource base = novaBase();
        try (Connection conexao = base.getConnection()) {
            executar(conexao, DDL_VENDA_COM_COLUNA_LEGADA);

            assertDoesNotThrow(() -> aplicar(conexao));

            assertTrue(colunaExiste(conexao, "movimentacao_revendedora", "is_finalizada"),
                    "Sem tabela de status a migration nao deve tocar na tabela de vendas");
        }
    }

    @Test
    @DisplayName("Deve semear os status mesmo sem tabela de vendas")
    void deveSemearStatusSemTabelaDeVendas() throws Exception {
        DataSource base = novaBase();
        try (Connection conexao = base.getConnection()) {
            executar(conexao, DDL_STATUS);

            assertDoesNotThrow(() -> aplicar(conexao));

            assertEquals(5, contar(conexao, "status_maleta"));
        }
    }

    @Test
    @DisplayName("Deve rodar pelo Flyway com baseline em schema ja povoado")
    void deveRodarPeloFlywayComBaseline() throws Exception {
        DataSource base = novaBase();
        try (Connection conexao = base.getConnection()) {
            executar(conexao, DDL_STATUS);
            executar(conexao, DDL_VENDA_COM_COLUNA_LEGADA);
            executar(conexao, "INSERT INTO status_maleta (nome) VALUES ('ABERTO')");
            executar(conexao, """
                    INSERT INTO movimentacao_revendedora (id_movimentacao_revendedora, nome, is_finalizada, status_maleta_id)
                    VALUES (1, 'maleta-1', TRUE, NULL)
                    """);

            Flyway flyway = Flyway.configure()
                    .dataSource(base)
                    .locations("classpath:db/migration")
                    .baselineOnMigrate(true)
                    .baselineVersion("0")
                    .load();

            flyway.migrate();

            assertEquals(5, contar(conexao, "status_maleta"));
            assertEquals(List.of("FINALIZADA"), statusDasMaletas(conexao));
            assertEquals(0, flyway.info().pending().length,
                    "Nao deve sobrar migration pendente, senao a carga nao e idempotente");
            assertTrue(Arrays.stream(flyway.info().all())
                            .anyMatch(migrada -> "1".equals(migrada.getVersion().getVersion())),
                    "A migration V1 deve ficar registrada no historico do Flyway");
        }
    }

    private void aplicar(Connection conexao) throws Exception {
        Context context = mock(Context.class);
        when(context.getConnection()).thenReturn(conexao);

        new V1__garantir_status_maleta_e_backfill().migrate(context);
    }

    private DataSource novaBase() {
        return new DriverManagerDataSource(
                "jdbc:h2:mem:migracao-" + CONTADOR.incrementAndGet() + ";MODE=MySQL", "sa", "");
    }

    private void executar(Connection conexao, String sql) throws Exception {
        try (Statement st = conexao.createStatement()) {
            st.execute(sql);
        }
    }

    private int contar(Connection conexao, String tabela) throws Exception {
        try (Statement st = conexao.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + tabela)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private List<String> nomesDosStatus(Connection conexao) throws Exception {
        List<String> nomes = new ArrayList<>();

        try (Statement st = conexao.createStatement();
             ResultSet rs = st.executeQuery("SELECT nome FROM status_maleta ORDER BY nome")) {
            while (rs.next()) {
                nomes.add(rs.getString(1));
            }
        }

        return nomes;
    }

    private List<String> statusDasMaletas(Connection conexao) throws Exception {
        List<String> nomes = new ArrayList<>();

        try (Statement st = conexao.createStatement();
             ResultSet rs = st.executeQuery("""
                     SELECT s.nome FROM movimentacao_revendedora m
                     JOIN status_maleta s ON s.id = m.status_maleta_id
                     ORDER BY m.id_movimentacao_revendedora
                     """)) {
            while (rs.next()) {
                nomes.add(rs.getString(1));
            }
        }

        return nomes;
    }

    private boolean colunaExiste(Connection conexao, String tabela, String coluna) throws Exception {
        try (ResultSet rs = conexao.getMetaData().getColumns(null, null, "%", "%")) {
            while (rs.next()) {
                if (tabela.equalsIgnoreCase(rs.getString("TABLE_NAME"))
                        && coluna.equalsIgnoreCase(rs.getString("COLUMN_NAME"))) {
                    return true;
                }
            }
        }

        return false;
    }
}