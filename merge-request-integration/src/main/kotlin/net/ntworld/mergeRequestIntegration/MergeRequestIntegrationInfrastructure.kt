package net.ntworld.mergeRequestIntegration

import net.ntworld.foundation.InfrastructureProvider

class MergeRequestIntegrationInfrastructure(
    private val providerStorage: ProviderStorage
) : InfrastructureProvider() {
    private val included = listOf(
        AutoGeneratedInfrastructureProvider(providerStorage)
    )

    init {
        wire(this.root, this.included)
    }
}