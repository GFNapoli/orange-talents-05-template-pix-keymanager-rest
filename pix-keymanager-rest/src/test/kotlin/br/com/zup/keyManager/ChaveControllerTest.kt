package br.com.zup.keyManager

import br.com.zup.*
import br.com.zup.keyManager.dto.CadastraChaveRequest
import com.google.protobuf.Timestamp
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.core.type.Argument
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.annotation.GrpcService
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.jetbrains.annotations.NotNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class ChaveControllerTest {

    @field:Inject
    lateinit var grpcCLient: PixServiceGrpc.PixServiceBlockingStub

    @field:Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient


    @Singleton
    @Replaces(bean = PixServiceGrpc.PixServiceBlockingStub::class)
    fun grpcMock() = Mockito.mock(PixServiceGrpc.PixServiceBlockingStub::class.java)

    @Test
    internal fun `deve cadastrar uma chave pix`() {

        val pixRequest = NovaPixKeyRequest.newBuilder()
            .setIdCliente("ae93a61c-0642-43b3-bb8e-a17072295955")
            .setTipoChave(TipoChave.ALEATORIA)
            .setKey("")
            .setTipoConta(TipoConta.CONTA_POUPANCA)
            .build()

        val pixResponse = NovaPixKeyResponse.newBuilder()
            .setPixId("2".toLong())
            .build()

        BDDMockito.given(grpcCLient.novaChavePix(Mockito.any(NovaPixKeyRequest::class.java))).willReturn(pixResponse)

        val request = HttpRequest.POST(
            "/chavePix/cadastra",
            CadastraChaveRequest("ae93a61c-0642-43b3-bb8e-a17072295955", 4, " ", 2)
        )
        val response = httpClient.toBlocking().exchange(request, CadastraChaveRequest::class.java)

        assertEquals(HttpStatus.CREATED, response.status)
    }

    @Test
    internal fun `deve deletar uma chave pix`() {

        val deleteResponse = DeletaKeyResponse.newBuilder().setMensagem("OK").build()
        BDDMockito.given(grpcCLient.deletaChavePix(Mockito.any(DeletaKeyRequest::class.java))).willReturn(deleteResponse)

        val request = HttpRequest.DELETE<Any>("/chavePix/ae93a61c-0642-43b3-bb8e-a17072295955/pix?idPix=1")
        val response = httpClient.toBlocking().exchange(request, DeletaKeyRequest::class.java)

        assertEquals(HttpStatus.OK, response.status)
    }

    @Test
    internal fun `deve consultar chave pix`() {

        val pixResponse = ConsultaKeyResponse.newBuilder()
            .setTipoChave("CPF")
            .setKey("11129686541")
            .setNome("Luiza")
            .setCpf("11129686541")
            .setDadosConta(ConsultaKeyResponse.DadosConta.newBuilder()
                .setInstituicao("ITAU")
                .setAgencia("0001")
                .setNumero("1234")
                .setTipoConta(TipoConta.CONTA_POUPANCA)
                .build())
            .setDatacriacao(Timestamp.newBuilder().setSeconds(1235465456465465).setNanos(12332).build())
            .setPixId(1)
            .setIdCliente("ae93a61c-0642-43b3-bb8e-a17072295955")
            .build()

        BDDMockito.given(grpcCLient.consultaChavePix(Mockito.any())).willReturn(pixResponse)

        val request = HttpRequest.GET<Any>("/chavePix/ae93a61c-0642-43b3-bb8e-a17072295955/pix?idPix=1")
        val response = httpClient.toBlocking().exchange(request, Any::class.java)

        assertEquals(HttpStatus.OK, response.status)
        assertNotNull(response.body())
    }

    @Test
    internal fun `consultar lista de chaves`() {

        val pixResponse = ListaKeyResponse.newBuilder()
            .setIdCliente("ae93a61c-0642-43b3-bb8e-a17072295955")
            .addDadosChave(ListaKeyResponse.DadosChave.newBuilder()
                .setPixId(1)
                .setTipoChave("CPF")
                .setChave("12345678902")
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .setDatacriacao(Timestamp.newBuilder()
                    .setSeconds(1231321321)
                    .setNanos(121).build())
                .build())
            .build()

        BDDMockito.given(grpcCLient.listaChavesPix(Mockito.any())).willReturn(pixResponse)

        val request = HttpRequest.GET<Any>("/chavePix/list/ae93a61c-0642-43b3-bb8e-a17072295955")
        val response = httpClient.toBlocking().exchange(request, Any::class.java)

        assertEquals(HttpStatus.OK, response.status)
        assertNotNull(response.body())
    }
}