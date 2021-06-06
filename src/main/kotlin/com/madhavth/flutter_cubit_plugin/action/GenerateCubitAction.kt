package com.madhavth.flutter_cubit_plugin.action

import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.showOkCancelDialog
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.codeStyle.CodeStyleManager
import com.madhavth.flutter_cubit_plugin.generator.*


class GenerateCubitAction : AnAction(), GenerateBlocDialog.Listener {

    private lateinit var dataContext: DataContext


    override fun actionPerformed(e: AnActionEvent) {

        val view = LangDataKeys.IDE_VIEW.getData(dataContext)
        val rootDirectory = view?.orChooseDirectory

        if (rootDirectory?.name != "lib") {
            showOkCancelDialog(title = "Error", message = "call only allowed from lib folder", okText = "ok")
            return
        }

        val modelNames = mutableListOf<String>()
        modelNames.add("")

        try {
            val modelDirectory = view.orChooseDirectory?.findSubdirectory("models")
            modelDirectory?.files?.forEach {
                modelNames.add(removeFileNameFromString(it.name))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val dialog = GenerateBlocDialog(this, modelNames)
        dialog.show()
    }

    private fun removeFileNameFromString(text: String): String {
        if (text.endsWith(".dart")) {
            return try {
                val textWithoutExt = text.split(".dart")
                textWithoutExt[0]
            } catch (e: Exception) {
                ""
            }
        }

        return text
    }

    fun String.pascalCase(): String = this.toCamelCase().replace("Cubit", "")

    fun String.snakeCase() = this.toSnakeCase().replace("_cubit", "")

    private fun String.toSnakeCase() = replace(humps, "_").toLowerCase()
    private val humps = "(?<=.)(?=\\p{Upper})".toRegex()


    override fun onGenerateBlocClicked(
        blocName: String?,
        shouldUseEquatable: Boolean,
        modelName: String?,
        generateUiCode: Boolean,
        addBlocProvider: Boolean,
        apiUrl: String
    ) {
        blocName?.let { it ->
            val generators = CubitGeneratorFactory.getCubitGenerators(it, true, modelName)
            generate(generators, GeneratorType.GenerateCubit)

            if (!modelName.isNullOrBlank()) {
                val repositoryGenerator = RepositoryGeneratorFactory.getRepositoryGenerators(it, modelName)
                generate(repositoryGenerator, GeneratorType.GenerateRepository)
            }

            val utilsGenerator = UtilsGeneratorGeneratorFactory.getRepositoryGenerators(it)
            generate(utilsGenerator, GeneratorType.GenerateUtils)

            if (generateUiCode) {
                val uiCodeGenerator = UiCodeGeneratorFactory.getUiCodeGenerators(it)
                generate(uiCodeGenerator, GeneratorType.GenerateUiCode)

                val extraCodeGenerator = ExtraWidgetGeneratorFactory.getExtraWidgetGenerators(it)
                generate(extraCodeGenerator, GeneratorType.GenerateExtraWidgets)
            }

            if (addBlocProvider)
                addBlocProviderToMainFile(it)

            if(!modelName.isNullOrBlank())
                addUrlToConstants(apiUrl,it, modelName)
        }
    }


    private fun addUrlToConstants(url:String,blocName: String, modelName:String)
    {
        try{
            val view = LangDataKeys.IDE_VIEW.getData(dataContext)
            val rootDirectory = view?.orChooseDirectory
            val file = rootDirectory?.findSubdirectory("utils")?.findFile("constant.dart")

            val project = CommonDataKeys.PROJECT.getData(dataContext)
            val document = PsiDocumentManager.getInstance(project!!).getDocument(file!!)

            val searchString = "static const ${modelName.uppercase()} ="
            val apiUrlExists = document?.text?.indexOf(searchString) !=-1

            if(!apiUrlExists)
            {
                val apiClass = "class Api"
                val apiClassStartIndex=  document?.text?.indexOf(apiClass)
                val searchBracket = document?.text?.indexOf("}", startIndex = apiClassStartIndex!!)

                WriteCommandAction.runWriteCommandAction(
                    project
                ) {
                    document?.insertString(searchBracket!!, "\t$searchString '$url';\n")
                    reformatFile(project, document!!, file)
                }
            }

        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }




    private fun addBlocProviderToMainFile(name: String) {
        try {
            val view = LangDataKeys.IDE_VIEW.getData(dataContext)
            val rootDirectory = view?.orChooseDirectory
            val file = rootDirectory?.findFile("main.dart") ?: return

            val project = CommonDataKeys.PROJECT.getData(dataContext)

            val document = PsiDocumentManager.getInstance(project!!).getDocument(file)

            val providersString = "MultiBlocProvider"
            val startMultiBlocProvider = document?.text?.indexOf(providersString)

            var endMultiBlocProviderIndex: Int? = null

            if (startMultiBlocProvider != null && startMultiBlocProvider != -1) {
                endMultiBlocProviderIndex = document.text.indexOf("[", startMultiBlocProvider)
            }


            val myAppString = "MyApp()"

            val myAppstartIndex = document?.text?.indexOf(myAppString) ?: return

            val myAppEndIndex = document.text.indexOf(")", myAppstartIndex) + 1

            // wrap the widget:
            WriteCommandAction.runWriteCommandAction(
                project
            ) {

                if (startMultiBlocProvider == -1)
                    document.replaceString(myAppstartIndex, myAppEndIndex, blocProviderSnippet(name))
                else
                    document.replaceString(
                        startMultiBlocProvider!!,
                        endMultiBlocProviderIndex!! + 1,
                        blocProviderAppendSnippet(name)
                    )

                document.insertString(0, "import 'bloc/${name.snakeCase()}/${name.snakeCase()}_cubit.dart';\n")

                reformatFile(project, document, file)
            }

//            val element: PsiElement = file.children.first()
//            element.text
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun blocProviderAppendSnippet(name: String): String {
        return String.format(
            """MultiBlocProvider(
                providers: [
                BlocProvider(
                create: (context) => ${name.pascalCase()}Cubit(),
                ),
"""
        )
    }


    private fun blocProviderSnippet(name: String): String {
        return String.format(
            """MultiBlocProvider(
                providers: [
                BlocProvider(
                create: (context) => ${name.pascalCase()}Cubit(),
                )
                ],
                child: MyApp(),
)"""
        )
    }


    private fun reformatFile(project: Project, document: Document, currentFile: PsiFile?) {
        // reformat file:

        // reformat file:
        ApplicationManager.getApplication().runWriteAction {
            PsiDocumentManager.getInstance(project).commitDocument(document)
            if (currentFile != null) {
                val unformattedText: String = document.getText()
                val unformattedLineCount: Int = document.getLineCount()
                CodeStyleManager.getInstance(project).reformat(currentFile)
                val formattedLineCount: Int = document.getLineCount()

                // file was incorrectly formatted, revert formatting
                if (formattedLineCount > unformattedLineCount + 3) {
                    document.setText(unformattedText)
                    PsiDocumentManager.getInstance(project).commitDocument(document)
                }
            }
        }
    }


    override fun update(e: AnActionEvent) {
        e.dataContext.let {
            this.dataContext = it
            val presentation = e.presentation
            presentation.isEnabled = true
        }
    }

    protected fun generate(
        mainSourceGenerators: List<CubitGenerator>,
        generatorType: GeneratorType = GeneratorType.GenerateCubit
    ) {
        val project = CommonDataKeys.PROJECT.getData(dataContext)
        val view = LangDataKeys.IDE_VIEW.getData(dataContext)
        val rootDirectory = view?.orChooseDirectory


        if (rootDirectory?.name != "lib") {
            createNotification("Error", "call from lib folder", NotificationType.ERROR)
            return
        }


        ApplicationManager.getApplication().runWriteAction {
            CommandProcessor.getInstance().executeCommand(
                project,
                {
                    when (generatorType) {
                        GeneratorType.GenerateCubit -> {
                            val cubitDirectory = getSubdirectory(rootDirectory, "bloc")
                            val directory =
                                getSubdirectory(cubitDirectory, mainSourceGenerators[0].getName().toSnakeCase())
                            mainSourceGenerators.forEach { createSourceFile(project!!, it, directory) }
                        }
                        GeneratorType.GenerateRepository -> {
                            val repositoryDirectory = getSubdirectory(rootDirectory, "repository")
                            mainSourceGenerators.forEach {
                                createSourceFile(project!!, it, repositoryDirectory)
                            }
                        }
                        GeneratorType.GenerateUtils -> {
                            val utilsDirectory = getSubdirectory(rootDirectory, "utils")
                            mainSourceGenerators.forEach {
                                createSourceFile(project!!, it, utilsDirectory)
                            }
                        }
                        GeneratorType.GenerateUiCode -> {
                            val uiDirectory = getSubdirectory(rootDirectory, "ui")
                            val directory =
                                getSubdirectory(uiDirectory, mainSourceGenerators[0].getName().toSnakeCase())
                            mainSourceGenerators.forEach {
                                createSourceFile(project!!, it, directory)
                            }
                        }
                        GeneratorType.GenerateExtraWidgets -> {
                            val uiDirectory = getSubdirectory(rootDirectory, "ui")
                            val extraDirectory = getSubdirectory(uiDirectory, "extra")
                            mainSourceGenerators.forEach {
                                createSourceFile(project!!, it, extraDirectory)
                            }
                        }
                    }

                },
                "Generate a new Cubit/ Repository From Model",
                null
            )
        }
    }

    private fun getSubdirectory(directory: PsiDirectory, folderName: String): PsiDirectory {
        return try {
            directory.findSubdirectory(folderName)!!
        } catch (e: Exception) {
            directory.createSubdirectory(folderName.lowercase())
        }
    }


    private fun createNotification(title: String, content: String, type: NotificationType) {
//        NotificationGroupManagerImpl().getNotificationGroup("1").createNotification(
//            title,
//            content,
//            type
//        )

    }


    private fun createSourceFile(project: Project, generator: CubitGenerator, directory: PsiDirectory) {
        val fileName = generator.fileName()

        val existingPsiFile = directory.findFile(fileName)
        if (existingPsiFile != null) {
//            val document = PsiDocumentManager.getInstance(project).getDocument(existingPsiFile)
//            document?.insertString(document.textLength, "\n" + generator.generate())
            return
        }
        val psiFile = PsiFileFactory.getInstance(project)
            .createFileFromText(fileName, PlainTextLanguage.INSTANCE, generator.generate())
        directory.add(psiFile)
    }
//    JavaLanguage.INSTANCE
}

enum class GeneratorType {
    GenerateCubit, GenerateRepository, GenerateUtils, GenerateUiCode, GenerateExtraWidgets
}