package com.madhavth.flutter_cubit_plugin.generator.components

import com.madhavth.flutter_cubit_plugin.generator.CubitGenerator

class CubitStateGenerator(
    name: String,
    templateName: String,
modelName: String?
) : CubitGenerator(name,templateName, modelName = modelName) {
    override fun fileName() = "${snakeCase()}_state.${fileExtension()}"
}