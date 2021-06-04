package br.com.zup.edu.carrega

import br.com.zup.edu.CarregaChavePixRequest
import br.com.zup.edu.CarregaChavePixResponse
import br.com.zup.edu.ChavePixRepository
import br.com.zup.edu.KeyManagerCarregaGrpcServiceGrpc
import br.com.zup.edu.bcb.BancoCentralClient
import br.com.zup.edu.compartilhados.handlers.ErrorHandler
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Validator

@Singleton
@ErrorHandler
class CarregaChaveEndpoint(
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject private val bcbClient: BancoCentralClient,
    @Inject private val validator: Validator,
) :
    KeyManagerCarregaGrpcServiceGrpc.KeyManagerCarregaGrpcServiceImplBase() {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    override fun carrega(
        request: CarregaChavePixRequest,
        responseObserver: StreamObserver<CarregaChavePixResponse>,
    ) {
        val filtro = request.toModel(validator)
        val chaveInfo = filtro.filtra(repository = chavePixRepository, bcbClient = bcbClient)

        responseObserver.onNext(CarregaChavePixResponseConverter().convert(chaveInfo))
        responseObserver.onCompleted()
    }

}