package app.stompy.chatgptjavadoc.chatgpt_javadoc

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiMethod
import com.theokanning.openai.completion.CompletionRequest
import com.theokanning.openai.service.OpenAiService
import org.jetbrains.annotations.NotNull

class ChatgptJavadocAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val element = event.getData(CommonDataKeys.PSI_ELEMENT)
        if (element !is PsiMethod) {
            throw IllegalStateException("Event element is not a method!")
        }

        val start = element.getTextRange().startOffset
        val methodText = element.text

        // TODO: This is a blocking call. Consider how to make this async.
        val javadoc = generateJavadoc(methodText)

        val editor = event.getRequiredData(CommonDataKeys.EDITOR)
        val project = event.getRequiredData(CommonDataKeys.PROJECT)
        val document = editor.document

        // Must do this document change in a write action context.
        WriteCommandAction.runWriteCommandAction(
            project
        ) {
            // TODO: The indentation in the code is not right. Consider
            //  triggering a reformat.
            document.insertString(start, "/**\n $javadoc*/\n")
        }
    }

    override fun update(event: @NotNull AnActionEvent) {
        val element = event.getData(CommonDataKeys.PSI_ELEMENT)
        event.presentation.isEnabledAndVisible = element is PsiMethod
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    private fun generateJavadoc(methodText: String): String? {
        // TODO: Read key from the environment. Please don't abuse my free quota!
        val service = OpenAiService("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")
        val completionRequest: CompletionRequest = CompletionRequest.builder()
            .model("text-davinci-003")
            .prompt(
    """
    $methodText
    // An elaborate, high quality JavaDoc for the above function:
    /**
    
    """.trimIndent()
            )
            .echo(false)
            .user("testing")
            .n(1)
            .maxTokens(150)
            .stop(listOf("//", "*/"))
            .temperature(0.0)
            .build()

        // TODO: Handle no choices returned.
        return service.createCompletion(completionRequest).choices[0].text
    }
}
