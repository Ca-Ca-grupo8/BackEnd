package db.migration;

import com.caeca.estoque.database.model.statusMaleta.StatusMaletaEnum;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class V1__garantir_status_maleta_e_backfill extends BaseJavaMigration {

    private static final Logger LOG = Logger.getLogger(V1__garantir_status_maleta_e_backfill.class.getName());

    private static final String TABELA_STATUS = "status_maleta";
    private static final String TABELA_VENDA = "movimentacao_revendedora";
    private static final String COLUNA_VENDA_ID = "id_movimentacao_revendedora";
    private static final String COLUNA_VENDA_STATUS = "status_maleta_id";
    private static final String COLUNA_LEGADA = "is_finalizada";

    private record MaletaPendente(long id, long statusId) {
    }

    @Override
    public void migrate(Context context) throws Exception {
        Connection conexao = context.getConnection();
        DatabaseMetaData meta = conexao.getMetaData();

        if (!tabelaExiste(meta, TABELA_STATUS)) {
            LOG.warning("Tabela " + TABELA_STATUS + " nao existe; carga inicial de status ignorada.");
            return;
        }

        Map<StatusMaletaEnum, Long> ids = garantirStatusPadrao(conexao);

        if (!tabelaExiste(meta, TABELA_VENDA) || !colunaExiste(meta, TABELA_VENDA, COLUNA_VENDA_STATUS)) {
            LOG.info("Tabela " + TABELA_VENDA + " sem dado de status; backfill ignorado.");
            return;
        }

        preencherStatusNulo(conexao, meta, ids);
        removerColunaLegada(conexao, meta);
    }

    /**
     * Cadastra os status do enum que ainda não existam e devolve o id de cada um.
     */
    private Map<StatusMaletaEnum, Long> garantirStatusPadrao(Connection conexao) throws SQLException {
        Map<StatusMaletaEnum, Long> ids = new EnumMap<>(StatusMaletaEnum.class);
        int inseridos = 0;

        for (StatusMaletaEnum nome : StatusMaletaEnum.values()) {
            Long id = buscarId(conexao, nome);

            if (id == null) {
                id = inserir(conexao, nome);
                inseridos++;
            }

            ids.put(nome, id);
        }

        LOG.info("Status de maleta cadastrados por esta migration: " + inseridos);

        return ids;
    }

    private void preencherStatusNulo(Connection conexao, DatabaseMetaData meta,
                                     Map<StatusMaletaEnum, Long> ids) throws SQLException {
        boolean temColunaLegada = colunaExiste(meta, TABELA_VENDA, COLUNA_LEGADA);

        long idAberto = ids.get(StatusMaletaEnum.ABERTO);
        long idFinalizada = ids.get(StatusMaletaEnum.FINALIZADA);

        String consulta = "SELECT " + COLUNA_VENDA_ID
                + (temColunaLegada ? ", " + COLUNA_LEGADA : "")
                + " FROM " + TABELA_VENDA
                + " WHERE " + COLUNA_VENDA_STATUS + " IS NULL";

        List<MaletaPendente> pendentes = new ArrayList<>();

        try (PreparedStatement ps = conexao.prepareStatement(consulta);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                boolean estavaFinalizada = temColunaLegada && rs.getBoolean(2);

                pendentes.add(new MaletaPendente(rs.getLong(1),
                        estavaFinalizada ? idFinalizada : idAberto));
            }
        }

        for (MaletaPendente pendente : pendentes) {
            try (PreparedStatement ps = conexao.prepareStatement(
                    "UPDATE " + TABELA_VENDA + " SET " + COLUNA_VENDA_STATUS + " = ?"
                            + " WHERE " + COLUNA_VENDA_ID + " = ?")) {
                ps.setLong(1, pendente.statusId());
                ps.setLong(2, pendente.id());
                ps.executeUpdate();
            }
        }

        LOG.info("Maletas com status preenchido por esta migration: " + pendentes.size()
                + (temColunaLegada ? " (derivados de " + COLUNA_LEGADA + ")" : ""));
    }

    private void removerColunaLegada(Connection conexao, DatabaseMetaData meta) throws SQLException {
        if (!colunaExiste(meta, TABELA_VENDA, COLUNA_LEGADA)) {
            return;
        }

        try (Statement st = conexao.createStatement()) {
            st.execute("ALTER TABLE " + TABELA_VENDA + " DROP COLUMN " + COLUNA_LEGADA);
        }

        LOG.info("Coluna legada " + TABELA_VENDA + "." + COLUNA_LEGADA + " removida.");
    }

    private Long buscarId(Connection conexao, StatusMaletaEnum nome) throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement(
                "SELECT id FROM " + TABELA_STATUS + " WHERE nome = ?")) {
            ps.setString(1, nome.name());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : null;
            }
        }
    }

    private long inserir(Connection conexao, StatusMaletaEnum nome) throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement(
                "INSERT INTO " + TABELA_STATUS + " (nome) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nome.name());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        }
    }

    private boolean tabelaExiste(DatabaseMetaData meta, String tabela) throws SQLException {
        try (ResultSet rs = meta.getTables(null, null, "%", null)) {
            while (rs.next()) {
                if (tabela.equalsIgnoreCase(rs.getString("TABLE_NAME"))) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean colunaExiste(DatabaseMetaData meta, String tabela, String coluna) throws SQLException {
        try (ResultSet rs = meta.getColumns(null, null, "%", "%")) {
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