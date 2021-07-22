package br.com.zup.keyManager

import br.com.zup.*
import br.com.zup.keyManager.dto.CadastraChaveRequest
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.validation.Valid

@Controller("/chavePix")
@Validated
class ChaveController (@Inject val pixClient: PixServiceGrpc.PixServiceBlockingStub){

    @Post("/cadastra")
    fun cadastraChavePix(@Body @Valid request: CadastraChaveRequest): HttpResponse<Any>{

        try {
            val grpcRequest = NovaPixKeyRequest.newBuilder()
                .setIdCliente(request.idCliente)
                .setTipoChave(TipoChave.forNumber(request.tipoChave))
                .setKey(request.key)
                .setTipoConta(TipoConta.forNumber(request.tipoConta))
                .build()
            val response = pixClient.novaChavePix(grpcRequest)
            println(response.pixId)
            return HttpResponse.created(HttpResponse.uri("/chavePix/${request.idCliente}/pix/${response.pixId}"))
        }catch (e: StatusRuntimeException){

            val status = e.status
            val statusCode = status.code
            val description = status.description

            if(statusCode == Status.Code.ALREADY_EXISTS || statusCode == Status.Code.INVALID_ARGUMENT){
                throw HttpStatusException(HttpStatus.BAD_REQUEST, description)
            }

            if (statusCode == Status.Code.ABORTED){
                throw HttpStatusException(HttpStatus.NOT_FOUND, description)
            }

            throw HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }
}