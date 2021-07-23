package br.com.zup.keyManager

import br.com.zup.*
import br.com.zup.keyManager.dto.CadastraChaveRequest
import br.com.zup.keyManager.dto.DadosChaveResponse
import br.com.zup.keyManager.dto.DetalhesChavePix
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
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
    
    @Delete("/{idClient}/pix")
    fun deletaChavePix(@PathVariable idClient: String, @QueryValue idPix: Long): HttpResponse<Any>{

        try {
            val grpcRequest = DeletaKeyRequest.newBuilder()
                .setIdCliente(idClient)
                .setPixId(idPix)
                .build()

            val response = pixClient.deletaChavePix(grpcRequest)
            println(response)
            return HttpResponse.ok()
        }catch (e: StatusRuntimeException){

            val status = e.status
            val statusCode = status.code
            val description = status.description

            if (Status.NOT_FOUND.code == statusCode){
                throw  HttpStatusException(HttpStatus.NOT_FOUND, description)
            }
            if (Status.INVALID_ARGUMENT.code == statusCode ||
                    Status.ABORTED.code == statusCode){
                throw HttpStatusException(HttpStatus.BAD_REQUEST, description)
            }

            throw HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }

    @Get("/{idClient}/pix")
    fun consultaChavePix(@PathVariable idClient: String, @QueryValue idPix: Long): HttpResponse<Any>{

        try {
            val request = ConsultaKeyRequest.newBuilder()
                .setIdCliente(idClient)
                .setPixId(idPix)
                .build()

            val grpcResponse = pixClient.consultaChavePix(request)
            val response = DetalhesChavePix(grpcResponse)

            return HttpResponse.ok(response)
        }catch (e: StatusRuntimeException){

            val status = e.status
            val statusCode = status.code
            val description = status.description

            if (Status.NOT_FOUND.code == statusCode){
                throw HttpStatusException(HttpStatus.NOT_FOUND, description)
            }
            if(Status.INVALID_ARGUMENT.code == statusCode ||
                    Status.FAILED_PRECONDITION.code == statusCode){
                throw HttpStatusException(HttpStatus.BAD_REQUEST, description)
            }

            throw HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }

    @Get("/list/{idClient}")
    fun listaChaves(@PathVariable idClient: String): HttpResponse<Any>{

        try {
            val grpcRequest = ListaKeyRequest.newBuilder().setIdCliente(idClient).build()
            val grpcResponse = pixClient.listaChavesPix(grpcRequest)
            val response = grpcResponse.dadosChaveList.map { DadosChaveResponse(it) }
            return HttpResponse.ok(response)
        }catch (e: StatusRuntimeException){

            val status = e.status
            val statusCode = status.code
            val description = status.description

            if (Status.INVALID_ARGUMENT.code == statusCode){
                throw HttpStatusException(HttpStatus.BAD_REQUEST, description)
            }

            throw HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }
}