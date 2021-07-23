package br.com.zup.keyManager.dto

import br.com.zup.ConsultaKeyResponse

class DetalhesChavePix(consulta: ConsultaKeyResponse){
    val tipoChave = consulta.tipoChave
    val key = consulta.key
    val nome = consulta.nome
    val cpf = consulta.cpf
    val instituicao = consulta.dadosConta.instituicao
    val agencia = consulta.dadosConta.agencia
    val numero = consulta.dadosConta.numero
    val tipoConta = consulta.dadosConta.tipoConta.toString()
    val pixId = consulta.pixId
    val idClient = consulta.idCliente
}