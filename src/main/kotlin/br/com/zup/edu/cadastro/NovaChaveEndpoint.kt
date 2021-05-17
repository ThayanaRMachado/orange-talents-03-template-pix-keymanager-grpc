package br.com.zup.edu.cadastro

import br.com.zup.edu.NovaChaveRequest
import br.com.zup.edu.NovaChaveResponse
import br.com.zup.edu.KeyManagerRegistraGrpcServiceGrpc
import br.com.zup.edu.cadastro.compartilhado.exception.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class NovaChaveEndpoint(
    @Inject private val service: NovaChaveService
) : KeyManagerRegistraGrpcServiceGrpc.KeyManagerRegistraGrpcServiceImplBase() {

    override fun cria(request: NovaChaveRequest, responseObserver: StreamObserver<NovaChaveResponse>) {
        val novaChave = request.toModel()
        val chavePix = service.salva(novaChave)

        responseObserver.onNext(
            NovaChaveResponse.newBuilder()
                .setClienteId(novaChave.clienteId.toString())
                .setPixId(chavePix.id.toString())
                .build()
        )
        responseObserver.onCompleted()
    }
}