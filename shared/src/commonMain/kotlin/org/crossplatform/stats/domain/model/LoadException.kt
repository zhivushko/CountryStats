package org.crossplatform.stats.domain.model

sealed class LoadException : Exception() {
    class Network : LoadException()
    class Server : LoadException()
}