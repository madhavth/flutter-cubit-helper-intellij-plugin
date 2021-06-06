package com.madhavth.flutter_cubit_plugin.intention_action

object Snippets {
    val PREFIX_SELECTION = "Subject"
    val SUFFIX1 = "Cubit"
    val SUFFIX2 = "State"
    val SUFFIX3 = "Repository"
    val BLOC_SNIPPET_KEY = PREFIX_SELECTION
    val STATE_SNIPPET_KEY = PREFIX_SELECTION + SUFFIX2
    val REPOSITORY_SNIPPET_KEY = PREFIX_SELECTION + SUFFIX3
    fun getSnippet(snippetType: SnippetType?, widget: String): String {
        when (snippetType) {
            SnippetType.BlocListener -> return blocListenerSnippet(widget)
            SnippetType.BlocProvider -> return blocProviderSnippet(widget)
            SnippetType.BlocConsumer -> return blocConsumerSnippet(widget)
            SnippetType.RepositoryProvider -> return repositoryProviderSnippet(widget)
            SnippetType.MultiBlocProvider -> return multiBlocProviderSnippet(widget)
            else -> return blocBuilderSnippet(widget)
        }
    }

    private fun blocBuilderSnippet(widget: String): String {
        return String.format(
            "BlocBuilder<%1\$s, %2\$s>(\n" +
                    "  builder: (context, state) {\n" +
                    "    return %3\$s;\n" +
                    "  },\n" +
                    ")", BLOC_SNIPPET_KEY, STATE_SNIPPET_KEY, widget
        )
    }

    private fun blocListenerSnippet(widget: String): String {
        return String.format(
            "BlocListener<%1\$s, %2\$s>(\n" +
                    "  listener: (context, state) {\n" +
                    "    // TODO: implement listener}\n" +
                    "  },\n" +
                    "  child: %3\$s,\n" +
                    ")", BLOC_SNIPPET_KEY, STATE_SNIPPET_KEY, widget
        )
    }

    private fun blocProviderSnippet(widget: String): String {
        return String.format(
            "BlocProvider(\n" +
                    "  create: (context) => %1\$s(),\n" +
                    "  child: %2\$s,\n" +
                    ")", BLOC_SNIPPET_KEY, widget
        )
    }

    private fun multiBlocProviderSnippet(widget: String): String {
        return String.format(
            "MultiBlocProvider(\n" +
                    "providers: [" +
                    " BlocProvider( create: (context) => %1\$s()" + ")],\n" +
                    "  child: %2\$s,\n" +
                    ")", BLOC_SNIPPET_KEY, widget
        )
    }

    private fun blocConsumerSnippet(widget: String): String {
        return String.format(
            "BlocConsumer<%1\$s, %2\$s>(\n" +
                    "  listener: (context, state) {\n" +
                    "    // TODO: implement listener\n" +
                    "  },\n" +
                    "  builder: (context, state) {\n" +
                    "    return %3\$s;\n" +
                    "  },\n" +
                    ")", BLOC_SNIPPET_KEY, STATE_SNIPPET_KEY, widget
        )
    }

    private fun repositoryProviderSnippet(widget: String): String {
        return String.format(
            ("RepositoryProvider(\n" +
                    "  create: (context) => %1\$s(),\n" +
                    "    child: %2\$s,\n" +
                    ")"), REPOSITORY_SNIPPET_KEY, widget
        )
    }
}