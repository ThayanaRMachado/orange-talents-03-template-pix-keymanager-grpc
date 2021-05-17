package br.com.zup.edu.cadastro

import br.com.zup.edu.NovaChaveRequest

fun NovaChaveRequest.toModel(): NovaChave = NovaChave(
    clienteId = idCliente,
    tipo = tipoChave,
    valor = valorChave,
    tipoConta = tipoConta
)