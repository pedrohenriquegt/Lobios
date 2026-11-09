# Lobios Android — projeto Java

Projeto Android nativo em Java, preparado para abrir como projeto Gradle no Android Studio.

## O que contém
- Formulário de denúncia com nome opcional, categoria e descrição.
- Geração de protocolo e opção de copiar.
- Consulta local de denúncia por protocolo.
- Área do RH removida da navegação.
- Ícone visual próprio do Lobios.
- Dados de demonstração armazenados localmente no aparelho.

## Abrir corretamente no Android Studio
1. Extraia o ZIP inteiro para uma pasta fácil de encontrar.
2. No Android Studio, use **File → Open**.
3. Selecione a pasta **LobiosAndroid_atualizado** que contém diretamente `settings.gradle`, `build.gradle` e a pasta `app`.
4. Confirme a abertura como projeto.
5. Aguarde a sincronização do Gradle e aceite a instalação/atualização dos componentes do Android SDK se solicitado.
6. Para compilar, use **Build → Build Project**. Para gerar APK, procure **Build → Generate App Bundles or APKs → Build APK(s)**.

Se as opções de build ficarem desativadas, verifique se a sincronização do Gradle terminou e se a pasta aberta é a que contém `settings.gradle` e `app/build.gradle`.

## Requisitos
- Android Studio atualizado.
- JDK 17 (normalmente o JBR/JDK embutido do Android Studio).
- Android SDK Platform 35 instalado (o Android Studio pode oferecer instalá-lo automaticamente).

## Limitação
Este é um protótipo acadêmico: os registros ficam apenas no dispositivo em que foram criados. Não há servidor nem sincronização com RH. Não use dados pessoais reais.
