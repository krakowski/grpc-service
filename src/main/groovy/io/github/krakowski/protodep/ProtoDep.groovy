package io.github.krakowski.protodep

import com.google.protobuf.gradle.ProtobufPlugin
import io.github.krakowski.protodep.extension.ProtoDepExtension
import io.github.krakowski.protodep.task.CloneProto
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.plugins.ide.eclipse.EclipsePlugin
import org.gradle.plugins.ide.idea.IdeaPlugin

class ProtoDep implements Plugin<Project> {

    private static final String PROTO_VERSION = '3.9.0'
    private static final String GRPC_VERSION = '1.22.1'
    private static final String CLONE_DIR = 'cloned-protos'

    void apply(Project project) {

        project.pluginManager.apply(IdeaPlugin)
        project.pluginManager.apply(EclipsePlugin)
        project.pluginManager.apply(JavaPlugin)
        project.pluginManager.apply(ProtobufPlugin)

        def extension = project.extensions.create('protodep', ProtoDepExtension)
        extension.output.convention(project.layout.buildDirectory.dir(CLONE_DIR))
        extension.protoc.convention(PROTO_VERSION)
        extension.grpc.convention(GRPC_VERSION)

        project.tasks.create('cloneProto', CloneProto) {
            it.uri.set(extension.repo)
            it.output.set(extension.output)
        }

        // TODO(krakowski)
        //  Find a way to configure artifacts dynamically. Configuring the
        //  protobuf extension after the project evaluated does not seem
        //  to work.
        project.protobuf {
            protoc {
                artifact = "com.google.protobuf:protoc:${PROTO_VERSION}"
            }
            plugins {
                grpc {
                    artifact = "io.grpc:protoc-gen-grpc-java:${GRPC_VERSION}"
                }
            }
            generateProtoTasks {
                all()*.plugins {
                    grpc {}
                }
            }
        }

        project.afterEvaluate {
            project.tasks.findByName('generateProto').dependsOn('cloneProto')

            project.dependencies {
                implementation "com.google.protobuf:protobuf-java:${PROTO_VERSION}"
                implementation "io.grpc:grpc-netty-shaded:${GRPC_VERSION}"
                implementation "io.grpc:grpc-protobuf:${GRPC_VERSION}"
                implementation "io.grpc:grpc-stub:${GRPC_VERSION}"
            }

            project.sourceSets {
                main {
                    proto {
                        srcDir extension.output.get()
                    }
                }
            }
        }
    }
}
