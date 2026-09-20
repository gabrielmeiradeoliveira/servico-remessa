package com.desafio.remessa.transferencia.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CriarRemessaRequest(
    @NotNull(message = "Remetente é obrigatório") Long remetenteId,
    @NotNull(message = "Destinatário é obrigatório") Long destinatarioId,
    @NotNull(message = "Valor é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
        BigDecimal valorReal) {}
