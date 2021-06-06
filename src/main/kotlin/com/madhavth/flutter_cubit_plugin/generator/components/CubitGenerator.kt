package com.madhavth.flutter_cubit_plugin.generator.components

import com.madhavth.flutter_cubit_plugin.action.GeneratorType
import com.madhavth.flutter_cubit_plugin.generator.CubitGenerator
import com.madhavth.flutter_cubit_plugin.generator.toCamelCase
import com.madhavth.flutter_cubit_plugin.generator.toSnakeCase

class CubitGenerator(
    name: String,
    templateName:String,
    private val modelName:String?
) : CubitGenerator(name,templateName,modelName = modelName) {
    override fun fileName(): String {
        return "${snakeCase()}_cubit.${fileExtension()}"
    }
}

class RepositoryGenerator(
    name:String,
    private val modelName: String
):CubitGenerator(name, templateName = "repository",modelName =modelName)  {

    override fun fileName(): String {
        return "${modelName.toSnakeCase()}_repository.${fileExtension()}"
    }
}

class ConstantsGenerator(
    name:String
): CubitGenerator(name, templateName = "utils_constant")
{
    override fun fileName(): String {
        return "constant.${fileExtension()}"
    }

}
class ErrorHelperGenerator(
    name:String
): CubitGenerator(name, templateName = "utils_error_helper")
{
    override fun fileName(): String {
        return "error_helper.${fileExtension()}"
    }

}

class UiCodeGenerator(
    val blocName: String,
    val append: String = "",
    templateName: String = "ui_code"
): CubitGenerator(blocName, templateName = templateName)
{
    override fun fileName(): String {
        return "${blocName.toSnakeCase()}${append}.dart"
    }
}

class ExtraWidgetGenerator(val blocName: String,val fileName:String, templateName: String): CubitGenerator(blocName, templateName = templateName)
{
    override fun fileName(): String {
        return "${fileName}.dart"
    }

}