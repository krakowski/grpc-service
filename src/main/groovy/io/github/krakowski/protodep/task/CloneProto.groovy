package io.github.krakowski.protodep.task


import org.ajoberstar.grgit.Grgit
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class CloneProto extends DefaultTask {

    @Input
    final Property<String> uri = project.objects.property(String)

    @OutputDirectory
    final DirectoryProperty output = project.objects.directoryProperty()

    CloneProto() {
        outputs.upToDateWhen { isSynced() }
    }

    @TaskAction
    def action() {
        if (project.file(output.get().dir('.git')).exists()) {
            Grgit.open(dir: output.get()).pull()
        } else {
            Grgit.clone(dir: output.get(), uri: uri.get())
        }
    }

    def isSynced() {
        if (!project.file(output.get().dir('.git')).exists()) {
            return false;
        }

        def git = Grgit.open(dir: output.get())

        git.fetch()
        def remoteBranch   = git.branch.current().trackingBranch.name
        def localHead      = git.head().id
        def remoteHead     = git.resolve.toCommit(remoteBranch).id

        return localHead.equals(remoteHead)
    }
}
