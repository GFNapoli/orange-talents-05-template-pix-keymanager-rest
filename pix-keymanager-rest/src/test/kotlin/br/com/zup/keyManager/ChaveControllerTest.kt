package br.com.zup.keyManager

import br.com.zup.*
import br.com.zup.keyManager.dto.CadastraChaveRequest
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.annotation.GrpcService
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
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

//    @Factory
//    @Replaces(factory = PixServiceGrpc.PixServiceBlockingStub::class)
//    internal class MockitoFactory {
//        @Singleton
//        fun stubMock() = Mockito.mock(PixServiceGrpc.PixServiceBlockingStub::class.java)
//    }

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

}