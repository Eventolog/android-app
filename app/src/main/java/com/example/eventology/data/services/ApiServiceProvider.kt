package com.example.eventology.data.services

/**
 * Provides a globally accessible instance of [DataServiceInterface].
 *
 * This is the single point of access for your application's data service.
 * It can be set to return either a real or a mocked implementation.
 *
 * Currently, it returns a [MockDataService] instance.
 */
object ApiServiceProvider {

    /**
     * The actual instance of the [DataServiceInterface] being used.
     * You can change this to a real implementation for production use.
     */
    val service: DataServiceInterface = RealDataService

    /**
     * Returns the currently configured data service instance.
     *
     * @return the [DataServiceInterface] implementation in use.
     */
    fun getDataService(): DataServiceInterface {
        return service
    }
}