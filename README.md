# LLM powered development for IntelliJ

<!-- Plugin description -->
**llm-intellij** is a plugin for all things LLM. It uses [**llm-ls**](https://github.com/huggingface/llm-ls) as a backend.

> [!NOTE]
> When using the Inference API, you will probably encounter some limitations. Subscribe to the *PRO* plan to avoid getting rate limited in the free tier.
>
> https://huggingface.co/pricing#pro

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
    2. *(not supported yet)* You can define your `HF_HOME` environment variable and create a file containing your token at `$HF_HOME/token`
    3. *(not supported yet)* Install the [huggingface-cli](https://huggingface.co/docs/huggingface_hub/quick-start) and run `huggingface-cli login` - this will prompt you to enter your token and set it at the right path

3. Choose your model on the [Hugging Face Hub](https://huggingface.co/), and set `Model = <model identifier>` in plugin settings

#### With your own HTTP endpoint

All of the above still applies, but note:

* When an API token is provided, it will be passed as a header: `Authorization: Bearer <api_token>`.

* Instead of setting a Hugging Face model identifier in `model`, set the URL for your HTTP endpoint

### Models

**llm-intellij** is assumed to be compatible with any model that generates code.

Here are some configs for popular models in JSON format that you can put in your Settings (`Cmd+,` > `LLM Settings`)

#### [Starcoder](https://huggingface.co/bigcode/starcoder)

```json
{
   "tokensToClear": [
      "<|endoftext|>"
   ],
   "fim": {
      "enabled": true,
      "prefix": "<fim_prefix>",
      "middle": "<fim_middle>",
      "suffix": "<fim_suffix>"
   },
   "model": "bigcode/starcoder",
   "context_window": 8192,
   "tokenizer": {
     "repository": "bigcode/starcoder"
   }
}
```

> [!NOTE]
> These are the default config values

#### [CodeLlama](https://huggingface.co/codellama/CodeLlama-13b-hf)

```json
{
   "tokensToClear": [
      "<EOT>"
   ],
   "fim": {
      "enabled": true,
      "prefix": "<PRE> ",
      "middle": " <MID>",
      "suffix": " <SUF>"
   },
   "model": "codellama/CodeLlama-13b-hf",
   "context_window": 4096,
   "tokenizer": {
      "repository": "codellama/CodeLlama-13b-hf"
   }
}
```

> [!NOTE]
> Spaces are important here


### [**llm-ls**](https://github.com/huggingface/llm-ls)

By default, **llm-ls** is installed by **llm-intellij** the first time it is loaded. The binary is downloaded from the [release page](https://github.com/huggingface/llm-ls/releases) and stored in:
```shell
"$HOME/.cache/llm_intellij/bin"
```

When developing locally or if you built your own binary because your platform is not supported, you can set the `llm-ls` > `Binary path` setting to the path of the binary.

`llm-ls` > `Version` is used only when **llm-intellij** downloads **llm-ls** from the release page.

You can also set the log level for **llm-ls** with `llm-ls` > `Log level`, which can take any of the usual `info`, `warn`, `error`, etc as a value.
The log file is located in:
```shell
"$HOME/.cache/llm_ls/llm-ls.log"
```

### Tokenizer

**llm-ls** uses [**tokenizers**](https://github.com/huggingface/tokenizers) to make sure the prompt fits the `context_window`.

To configure it, you have a few options:
* No tokenization, **llm-ls** will count the number of characters instead:
* from a local file on your disk:
* from a Hugging Face repository, **llm-ls** will attempt to download `tokenizer.json` at the root of the repository:
* from an HTTP endpoint, **llm-ls** will attempt to download a file via an HTTP GET request:
<!-- Plugin description end -->
