package br.com.zup.edu.cadastro.compartilhado.exception

import io.grpc.Status
import io.grpc.StatusRuntimeException
import java.lang.Exception
import io.grpc.Metadata
import io.grpc.protobuf.StatusProto

interface ExceptionHandler<E: Exception> {

    fun handle(e: E): StatusWithDetails

    fun supports(e: Exception): Boolean

    class StatusWithDetails(val status: Status, val metadata: Metadata = Metadata()) {
        constructor(exception: StatusRuntimeException): this(exception.status, exception.trailers ?: Metadata())
        constructor(status: com.google.rpc.Status): this(StatusProto.toStatusRuntimeException(status))

        fun asRuntimeException(): StatusRuntimeException = status.asRuntimeException(metadata)

    }
}


