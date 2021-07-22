package br.com.zup.keyManager.dto

import io.micronaut.core.annotation.Introspected
import org.jetbrains.annotations.NotNull
import javax.validation.constraints.NotBlank

@Introspected
data class CadastraChaveRequest(
    @field:NotNull val idCliente: String,
    @field:NotNull val tipoChave: Int,
    @field:NotNull val key: String,
    @field:NotNull val tipoConta: Int
)
