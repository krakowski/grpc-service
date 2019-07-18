package io.github.krakowski.protodep.extension


import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

import javax.inject.Inject

class ProtoDepExtension {

    /* The git repository uri to pull service definitions from */
    final Property<String> repo

    /* The directory in which the plugin places the cloned repository */
    final DirectoryProperty output

    @Deprecated
    final Property<String> protoc

    @Deprecated
    final Property<String> grpc

    @Inject
    ProtoDepExtension(ObjectFactory objects) {
        repo = objects.property(String)
        output = objects.directoryProperty()
        protoc = objects.property(String)
        grpc = objects.property(String)
    }
}
