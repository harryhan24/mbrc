package com.kelsos.mbrc.networking

import com.kelsos.mbrc.networking.client.IClientConnectionManager


class ClientConnectionUseCaseImpl

constructor(
  private val connectionManager: IClientConnectionManager
) :
  ClientConnectionUseCase {
  override fun connect() {
    connectionManager.stop()
    connectionManager.start()
  }
}