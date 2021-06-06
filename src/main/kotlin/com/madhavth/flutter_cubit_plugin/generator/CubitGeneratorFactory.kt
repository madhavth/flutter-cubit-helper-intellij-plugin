package com.madhavth.flutter_cubit_plugin.generator

import com.madhavth.flutter_cubit_plugin.generator.components.*
import com.madhavth.flutter_cubit_plugin.generator.components.CubitGenerator

object CubitGeneratorFactory {
    fun getCubitGenerators(name: String, useEquatable: Boolean,modelName: String?): List<com.madhavth.flutter_cubit_plugin.generator.CubitGenerator> {
        val templateName = if(modelName.isNullOrBlank()) "cubit" else "cubit_repo"
        val cubit = CubitGenerator(name,templateName, modelName)
        val state = CubitStateGenerator(name, "cubit_state", null)
        return listOf(cubit, state)
    }
}

object RepositoryGeneratorFactory {
    fun getRepositoryGenerators(name:String, modelName: String): List<com.madhavth.flutter_cubit_plugin.generator.CubitGenerator>
    {
        val repository = RepositoryGenerator(name, modelName)
        return listOf(repository)
    }
}


object UtilsGeneratorGeneratorFactory {
    fun getRepositoryGenerators(name:String): List<com.madhavth.flutter_cubit_plugin.generator.CubitGenerator>
    {
        val utilsGenerator = ConstantsGenerator(name)
        val errorHelperGenerator = ErrorHelperGenerator(name)
        return listOf(utilsGenerator, errorHelperGenerator)
    }
}



object UiCodeGeneratorFactory {
    fun getUiCodeGenerators(name:String): List<com.madhavth.flutter_cubit_plugin.generator.CubitGenerator>
    {
        val uiCodeGenerator = UiCodeGenerator(name, append="_screen", templateName = "ui_code")
        val uiLoadedCodeGenerator = UiCodeGenerator(name, append="_loaded_screen", templateName = "ui_code_loaded")
        return listOf(uiCodeGenerator,uiLoadedCodeGenerator)
    }
}

object ExtraWidgetGeneratorFactory {
    fun getExtraWidgetGenerators(name:String): List<com.madhavth.flutter_cubit_plugin.generator.CubitGenerator>
    {
        val errorExtraGenerator = ExtraWidgetGenerator(name, "error_screen",templateName = "error_screen")
        val loadingExtraGenerator = ExtraWidgetGenerator(name, "loading","loading")
        return listOf(errorExtraGenerator,loadingExtraGenerator)
    }
}
