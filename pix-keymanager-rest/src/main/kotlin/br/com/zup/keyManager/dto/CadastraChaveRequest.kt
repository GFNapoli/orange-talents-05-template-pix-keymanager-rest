package br.com.zup.keyManager.dto

import br.com.zup.TipoChave
import br.com.zup.TipoConta
import io.micronaut.core.annotation.Introspected
import org.jetbrains.annotations.NotNull
import javax.validation.constraints.NotBlank

@Introspected
data class CadastraChaveRequest(
    @field:NotNull @field:NotBlank val idCliente: String,
    @field:NotNull @field:NotBlank val tipoChave: Int,
    @field:NotNull val key: String,
    @field:NotNull @field:NotBlank val tipoConta: Int
)
