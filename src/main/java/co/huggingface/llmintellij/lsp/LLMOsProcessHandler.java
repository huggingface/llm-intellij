package co.huggingface.llmintellij.lsp;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.util.io.BaseOutputReader;
import com.intellij.util.io.BaseOutputReader.Options;
import org.jetbrains.annotations.NotNull;


class LLMOsProcessHandler extends OSProcessHandler {
    public LLMOsProcessHandler(@NotNull GeneralCommandLine commandLine) throws ExecutionException {
        super(commandLine);
    }

    protected @NotNull Options readerOptions() {
        return BaseOutputReader.Options.forMostlySilentProcess();
    }
}
