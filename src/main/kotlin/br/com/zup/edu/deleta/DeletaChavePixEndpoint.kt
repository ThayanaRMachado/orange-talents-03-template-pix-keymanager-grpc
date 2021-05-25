package br.com.zup.edu.deleta

import br.com.zup.edu.DeletaChavePixRequest
import br.com.zup.edu.DeletaChavePixResponse
import br.com.zup.edu.KeyManagerDeletaGrpcServiceGrpc
import io.grpc.Status
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeletaChavePixEndpoint(@Inject val deletaChavePixService: DeletaChavePixService):
KeyManagerDeletaGrpcServiceGrpc.KeyManagerDeletaGrpcServiceImplBase() {

    private val logger = LoggerFactory.getLogger(DeletaChavePixEndpoint::class.java)

    override fun deleta(request: DeletaChavePixRequest, responseObserver: StreamObserver<DeletaChavePixResponse>) {
        deletaChavePixService.buscaChaveDelete(request.pixId, request.idTitular)

        responseObserver.onNext(
            DeletaChavePixResponse.newBuilder()
                .setStatus(Status.Code.OK.toString())
                .build()
        )
        responseObserver.onCompleted()
    }
}