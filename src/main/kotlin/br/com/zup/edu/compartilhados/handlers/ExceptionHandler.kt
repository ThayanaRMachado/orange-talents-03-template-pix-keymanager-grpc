package br.com.zup.edu.compartilhados.handlers

import com.google.rpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.Metadata
import io.grpc.protobuf.StatusProto


interface ExceptionHandler<T: Throwable> {

    fun handle(exception: T): Status//StatusWithDetails

    fun supports(exception: Exception): Boolean


}
