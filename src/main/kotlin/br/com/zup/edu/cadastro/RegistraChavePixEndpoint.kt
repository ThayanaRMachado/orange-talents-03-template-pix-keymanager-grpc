package br.com.zup.edu.cadastro

import br.com.zup.edu.ChavePixRepository
import br.com.zup.edu.KeyManagerRegistraGrpcServiceGrpc
import br.com.zup.edu.RegistraChavePixRequest
import br.com.zup.edu.RegistraChavePixResponse
import br.com.zup.edu.compartilhados.handlers.ErrorHandler
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.http.client.exceptions.HttpClientResponseException
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
@ErrorHandler
class RegistraChaveEndpoint(@Inject val service: NovaChavePixService, @Inject val repository: ChavePixRepository) :
    KeyManagerRegistraGrpcServiceGrpc.KeyManagerRegistraGrpcServiceImplBase() {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    override fun registra(
        request: RegistraChavePixRequest,
        responseObserver: StreamObserver<RegistraChavePixResponse>
    ) {
        val novaChave = request.toModel()
        val chaveCriada: ChavePix = try {
            service.registra(novaChave)
        } catch  (e: ConstraintViolationException){
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(e.message).asRuntimeException())
            return
        } catch (e: HttpClientResponseException) {
            println(e.message)
            println(e.status)
            return
        }

        responseObserver.onNext(
            RegistraChavePixResponse.newBuilder()
                .setPixId(chaveCriada.id.toString())
                .setIdTitular(request.idTitular)
                .build()
        )
        responseObserver.onCompleted()
    }
}