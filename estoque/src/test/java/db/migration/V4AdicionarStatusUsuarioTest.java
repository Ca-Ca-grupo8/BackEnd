package db.migration;

import org.flywaydb.core.api.migration.Context;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Valida a migration V4 contra H2 em MODE=MySQL, cobrindo os estados de schema:
 * sem a tabela usuario, com a tabela sem a coluna status e com a coluna ja
 * criada pelo Hibernate (porem nula) — caso real do ddl-auto=update.
 */
class V4AdicionarStatusUsuarioTest {

    private static final AtomicInteger CONTADOR = new AtomicInteger();

    private static final String DDL_USUARIO_SEM_STATUS = """
            CREATE TABLE usuario (
                id BIGINT NOT NULL AUTO_INCREMENT,
                nome VARCHAR(255) DEFAULT NULL,
                email VARCHAR(255) DEFAULT NULL,
                senha VARCHAR(255) DEFAULT NULL,
                perfil VARCHAR(32) DEFAULT NULL,
                PRIMARY KEY (id)
            )
            """;

    private static final String DDL_USUARIO_COM_STATUS = """
            CREATE TABLE usuario (
                id BIGINT NOT NULL AUTO_INCREMENT,
                nome VARCHAR(255) DEFAULT NULL,
                email VARCHAR(255) DEFAULT NULL,
                senha VARCHAR(255) DEFAULT NULL,
                perfil VARCHAR(32) DEFAULT NULL,
                status VARCHAR(20) DEFAULT NULL,
                PRIMARY KEY (id)
            )
            """;

    @Test
    @DisplayName("Deve ignorar a migration quando a tabela usuario nao existe")
    void deveIgnorarSemTabelaUsuario() throws Exception {
        DataSource base = novaBase();
        try (Connection conexao = base.getConnection()) {
            assertDoesNotThrow(() -> aplicar(conexao));
        }
    }

    @Test
    @DisplayName("Deve adicionar a coluna status e backfillar os usuarios existentes como ATIVO")
    void deveAdicionarColunaEBackfillar() throws Exception {
        DataSource base = novaBase();
        try (Connection conexao = base.getConnection()) {
            executar(conexao, DDL_USUARIO_SEM_STATUS);
            executar(conexao, "INSERT INTO usuario (nome, email, senha, perfil) VALUES "
                    + "('A', 'a@x.com', 'hash', 'ROLE_ADMIN'), ('B', 'b@x.com', 'hash', 'ROLE_USER')");

            aplicar(conexao);

            assertTrue(colunaExiste(conexao, "usuario", "status"));
            assertEquals(List.of("ATIVO", "ATIVO"), statusDosUsuarios(conexao));
        }
    }

    @Test
    @DisplayName("Deve backfillar NULL como ATIVO quando a coluna status ja existe e preservar valores definidos")
    void deveBackfillarColunaExistente() throws Exception {
        DataSource base = novaBase();
        try (Connection conexao = base.getConnection()) {
            executar(conexao, DDL_USUARIO_COM_STATUS);
            executar(conexao, "INSERT INTO usuario (nome, email, senha, perfil, status) VALUES "
                    + "('A', 'a@x.com', 'hash', 'ROLE_ADMIN', NULL),"
                    + "('B', 'b@x.com', 'hash', 'ROLE_USER', 'PENDENTE')");

            aplicar(conexao);

            assertEquals(List.of("ATIVO", "PENDENTE"), statusDosUsuarios(conexao));
        }
    }

    private void aplicar(Connection conexao) throws Exception {
        Context context = mock(Context.class);
        when(context.getConnection()).thenReturn(conexao);

        new V4__adicionar_status_usuario().migrate(context);
    }

    private DataSource novaBase() {
        return new DriverManagerDataSource(
                "jdbc:h2:mem:migracao-v4-" + CONTADOR.incrementAndGet() + ";MODE=MySQL", "sa", "");
    }

    private void executar(Connection conexao, String sql) throws Exception {
        try (Statement st = conexao.createStatement()) {
            st.execute(sql);
        }
    }

    private List<String> statusDosUsuarios(Connection conexao) throws Exception {
        List<String> status = new ArrayList<>();

        try (Statement st = conexao.createStatement();
             ResultSet rs = st.executeQuery("SELECT status FROM usuario ORDER BY id")) {
            while (rs.next()) {
                status.add(rs.getString(1));
            }
        }

        return status;
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
