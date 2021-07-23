package br.com.zup.keyManager.dto

import br.com.zup.ListaKeyResponse
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class DadosChaveResponse(item: ListaKeyResponse.DadosChave) {

    val pixId = item.pixId
    val tipoChave = item.tipoChave
    val chave = item.chave
    val tipoConta = item.tipoConta.toString()
    val datacriacao = item.datacriacao.let {
        LocalDateTime.ofInstant(Instant.ofEpochSecond(it.seconds, it.nanos.toLong()), ZoneOffset.UTC)
    }
}
