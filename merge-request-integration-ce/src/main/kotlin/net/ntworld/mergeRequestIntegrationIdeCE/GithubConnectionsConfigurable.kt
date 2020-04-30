package net.ntworld.mergeRequestIntegrationIdeCE

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import net.ntworld.mergeRequestIntegrationIde.ui.configuration.GithubConnectionsConfigurableBase

class GithubConnectionsConfigurable(project: Project): GithubConnectionsConfigurableBase(
    ServiceManager.getService(project, CommunityProjectServiceProvider::class.java)
) {
    override fun getId(): String = "MRI:github-ce"

    override fun getDisplayName(): String = "Github"
}