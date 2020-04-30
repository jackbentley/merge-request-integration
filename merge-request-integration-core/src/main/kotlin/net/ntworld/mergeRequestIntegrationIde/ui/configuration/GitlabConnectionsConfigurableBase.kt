package net.ntworld.mergeRequestIntegrationIde.ui.configuration

import net.ntworld.mergeRequest.ProviderInfo
import net.ntworld.mergeRequest.api.ApiConnection
import net.ntworld.mergeRequest.api.ApiCredentials
import net.ntworld.mergeRequestIntegration.provider.gitlab.Gitlab
import net.ntworld.mergeRequestIntegration.provider.gitlab.request.GitlabFindProjectRequest
import net.ntworld.mergeRequestIntegration.provider.gitlab.request.GitlabSearchProjectsRequest
import net.ntworld.mergeRequestIntegration.provider.gitlab.transformer.GitlabProjectTransformer
import net.ntworld.mergeRequestIntegrationIde.exception.InvalidConnectionException
import net.ntworld.mergeRequestIntegrationIde.infrastructure.ProjectServiceProvider
import net.ntworld.mergeRequestIntegrationIde.infrastructure.internal.ApiCredentialsImpl

open class GitlabConnectionsConfigurableBase(
    private val projectServiceProvider: ProjectServiceProvider
) : AbstractConnectionsConfigurable(projectServiceProvider) {
    override fun makeProviderInfo(): ProviderInfo = Gitlab

    override fun findNameFromId(id: String) = Companion.findNameFromId(id)

    override fun findIdFromName(name: String) = Companion.findIdFromName(name)

    override fun makeConnection(): ConnectionUI {
        return GitlabConnection(projectServiceProvider)
    }

    override fun validateConnection(connection: ApiConnection): Boolean {
        return connection.url.isNotEmpty() && connection.token.isNotEmpty()
    }

    override fun findProject(credentials: ApiCredentials): net.ntworld.mergeRequest.Project? {
        val out = projectServiceProvider.infrastructure.serviceBus() process GitlabFindProjectRequest(
            credentials = credentials,
            projectId = credentials.projectId.toInt()
        )
        val response = out.getResponse()
        return if (response.isSuccess) {
            GitlabProjectTransformer.transform(response.project)
        } else {
            null
        }
    }

    override fun findProjectByPath(credentials: ApiCredentials, path: String): net.ntworld.mergeRequest.Project? {
        val out = projectServiceProvider.infrastructure.serviceBus() process GitlabFindProjectRequest(
            credentials = credentials,
            projectId = 0,
            projectPath = path
        )
        val response = out.getResponse()
        return if (response.isSuccess) {
            GitlabProjectTransformer.transform(response.project)
        } else {
            null
        }
    }

    override fun assertConnectionIsValid(connection: ApiConnection) {
        val out = projectServiceProvider.infrastructure.serviceBus() process GitlabSearchProjectsRequest(
            credentials = ApiCredentialsImpl(
                url = connection.url,
                login = connection.login,
                token = connection.token,
                ignoreSSLCertificateErrors = connection.ignoreSSLCertificateErrors,
                info = "",
                projectId = "",
                version = ""
            ),
            term = ""
        )

        val error = out.getResponse().error
        if (null !== error) {
            throw InvalidConnectionException(error.message)
        }
    }

    override fun getId(): String = "MRI:gitlab"

    override fun getDisplayName(): String = "Gitlab new"

    companion object {
        private const val PREFIX = "gitlab:"

        fun findNameFromId(id: String): String {
            if (id.startsWith(PREFIX)) {
                return id.substring(PREFIX.length)
            }
            return id
        }

        fun findIdFromName(name: String): String {
            return "$PREFIX$name"
        }
    }
}