package com.caeca.estoque.client;

import com.caeca.estoque.dto.endereco.ViaCepResponseDto;
import com.caeca.estoque.exception.CepNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class ViaCepClient {

    private static final String VIA_CEP_URL = "https://viacep.com.br/ws/{cep}/json/";

    private final RestTemplate restTemplate = new RestTemplate();

    public ViaCepResponseDto buscarPorCep(String cep) {
        String cepLimpo = cep.replaceAll("[^0-9]", "");

        try {
            ViaCepResponseDto resposta = restTemplate.getForObject(
                    VIA_CEP_URL, ViaCepResponseDto.class, cepLimpo
            );

            if (resposta == null || resposta.isErro()) {
                throw new CepNotFoundException("CEP não encontrado.");
            }

            return resposta;

        } catch (HttpClientErrorException.NotFound e) {
            throw new CepNotFoundException("CEP não encontrado.");
        } catch (CepNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao consultar o serviço de CEP. Tente novamente.");
        }
    }
}
