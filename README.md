# LLM powered development for IntelliJ

> [!IMPORTANT]
> This is currently a work in progress, expect things to be broken!

<!-- Plugin description -->
**llm-intellij** is a plugin for all things LLM. It uses [**llm-ls**](https://github.com/huggingface/llm-ls) as a backend.

## Features

### Code completion

This plugin supports "ghost-text" code completion, à la Copilot.

### Choose your model

Requests for code generation are made via an HTTP request.

You can use the Hugging Face [Inference API](https://huggingface.co/inference-api) or your own HTTP endpoint, provided it adheres to the API specified [here](https://huggingface.co/docs/api-inference/detailed_parameters#text-generation-task) or [here](https://huggingface.github.io/text-generation-inference/#/Text%20Generation%20Inference/generate).

### Always fit within the context window

The prompt sent to the model will always be sized to fit within the context window, with the number of tokens determined using [tokenizers](https://github.com/huggingface/tokenizers).

## Configuration

### Endpoint

#### With Inference API

1. Create and get your API token from here https://huggingface.co/settings/tokens.

2. Define how the plugin will read your token. For this you have multiple options, in order of precedence:
    1. Set `API token = <your token>` in plugin settings
    2. You can define your `HF_HOME` environment variable and create a file containing your token at `$HF_HOME/token`
    3. Install the [huggingface-cli](https://huggingface.co/docs/huggingface_hub/quick-start) and run `huggingface-cli login` - this will prompt you to enter your token and set it at the right path

3. Choose your model on the [Hugging Face Hub](https://huggingface.co/), and set `Model = <model identifier>` in plugin settings

#### With your own HTTP endpoint

All of the above still applies, but note:

* When an API token is provided, it will be passed as a header: `Authorization: Bearer <api_token>`.

* Instead of setting a Hugging Face model identifier in `model`, set the URL for your HTTP endpoint.

### [**llm-ls**](https://github.com/huggingface/llm-ls)

By default, **llm-ls** is bundled with **llm-intellij**.

When developing locally or if you built your own binary because your platform is not supported, you can set the `LSP binary path` setting.

### Tokenizer

**llm-ls** uses [**tokenizers**](https://github.com/huggingface/tokenizers) to make sure the prompt fits the `context_window`.

To configure it, you have a few options:
* No tokenization, **llm-ls** will count the number of characters instead:
* from a local file on your disk:
* from a Hugging Face repository, **llm-ls** will attempt to download `tokenizer.json` at the root of the repository:
* from an HTTP endpoint, **llm-ls** will attempt to download a file via an HTTP GET request:
<!-- Plugin description end -->
