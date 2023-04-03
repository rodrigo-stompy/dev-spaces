package app.stompy.chatgptjavadoc.chatgpt_javadoc

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiMethod
import com.theokanning.openai.completion.CompletionRequest
import com.theokanning.openai.service.OpenAiService

class ChatgptJavadocAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val element = event.getData(CommonDataKeys.PSI_ELEMENT)
        if (element !is PsiMethod) {
            throw IllegalStateException("Event element is not a method!")
        }

        // TODO: This is a blocking call. Consider how to make this async.
        val javadoc = generateJavadocWithChatGpt(element.text)

        val editor = event.getRequiredData(CommonDataKeys.EDITOR)
        val project = event.getRequiredData(CommonDataKeys.PROJECT)
        val document = editor.document

        // Must do this document change in a write action context.
        WriteCommandAction.runWriteCommandAction(project) {
            val start = element.getTextRange().startOffset
            // TODO: The indentation level is not always right depending on
            //  nesting. Consider triggering a reformat.
            document.insertString(start, javadoc)
        }
    }

    override fun update(event: AnActionEvent) {
        val element = event.getData(CommonDataKeys.PSI_ELEMENT)
        event.presentation.isEnabledAndVisible = element is PsiMethod
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    /** Uses ChatGPT to generate a JavaDoc for the selected method. */
    private fun generateJavadocWithChatGpt(methodText: String): String {
        // TODO: Read key from the environment. Please don't abuse my free quota!
        val service = OpenAiService("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")
        val completionRequest: CompletionRequest = CompletionRequest.builder()
            .model("text-davinci-003")
            // TODO: Consider adding multiple actions for other prompts, e.g.:
            //  "A concise, high quality JavaDoc"
            .prompt(buildPromptForElaborateJavaDoc(methodText))
            .echo(false)
            .user("testing")
            .n(1)
            .stop(listOf("//", "*/"))
            // TODO: Make these params editable configurable in plugin config.
            .maxTokens(150)
            .temperature(0.0)
            .build()

        // TODO: Handle no choices returned more gracefully.
        val completion = service.createCompletion(completionRequest).choices[0].text
        return formatJavaDocCompletion(completion)
    }

    private fun buildPromptForElaborateJavaDoc(methodText: String): String {
        // TODO: Remove assumption that method is indented by two spaces.
        // The trailing line-break and spaces are there to make the formatting
        // in the completion more predictable.
        return buildString {
            append("  ")
            append(methodText)
            append("\n  // An elaborate, high quality JavaDoc for the above function:")
            append("\n  /**")
            append("\n  ")
        }
    }

    private fun formatJavaDocCompletion(completion: String?): String {
        // TODO: Remove assumption that method is indented by two spaces.
        return if (completion != null) "/**\n  $completion*/\n  " else ""
    }
}
