INSERT into banho (nome) VALUES ('ouro');
INSERT into categoria (nome) VALUES ('colar');
INSERT into material (nome) VALUES ('ouro 24k');
INSERT INTO tipo_movimentacao (descricao) VALUES ('ENTRADA');
INSERT INTO tipo_movimentacao (descricao) VALUES ('SAIDA');
INSERT INTO endereco (cep, logradouro, numero, complemento, bairro, cidade, uf) VALUES ('09370800', 'Rua General Carneiro', 100, 'Apto 12', 'Vila Guarani', 'Mauá', 'SP');

INSERT INTO endereco (cep, logradouro, numero, complemento, bairro, cidade, uf) VALUES ('01310100', 'Avenida Paulista', 1578, 'MASP', 'Bela Vista', 'São Paulo', 'SP');

INSERT INTO fornecedor (nome, telefone, instagram, is_ativo, data_cadastro, data_atualizacao) VALUES ('Lumina Semijoias', '11988887777', '@lumina_semijoias', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO fornecedor (nome, telefone, instagram, is_ativo, data_cadastro, data_atualizacao) VALUES ('Brilho Eterno Atacado', '11977776666', '@brilho_eterno', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO revendedora (nome, email, telefone, is_ativo, fk_endereco, data_cadastro, data_atualizacao) VALUES ('Ana Silva', 'ana.revenda@email.com', '11999998888', true, 1, CURRENT_DATE, CURRENT_DATE);

INSERT INTO revendedora (nome, email, telefone, is_ativo, fk_endereco, data_cadastro, data_atualizacao) VALUES ('Beatriz Souza', 'bia.joias@email.com', '11988887777', true, 2, CURRENT_DATE, CURRENT_DATE);