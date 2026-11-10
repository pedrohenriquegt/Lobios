# Lobios — protótipo Android em Java

Este é um protótipo acadêmico nativo para Android Studio, feito em Java e sem dependências externas.

## Funcionalidades incluídas
- Tela inicial com acesso à área do colaborador e à área do RH.
- Registro de denúncia com categoria, descrição e protocolo.
- Consulta de denúncia por protocolo.
- Painel do RH com listagem das denúncias.
- Atualização de status: Pendente, A resolver e Concluída.
- Armazenamento local no dispositivo usando SharedPreferences.

## Como abrir
1. Extraia o ZIP.
2. Abra o Android Studio.
3. Selecione **Open** e escolha a pasta `LobiosAndroid`.
4. Aguarde o Gradle sincronizar.
5. Execute em um emulador ou celular Android.

Requisitos configurados: Android Gradle Plugin 8.6.1, compileSdk 35, Java e Android SDK compatível.

## Limitações importantes
- É um protótipo local: não existe servidor nem banco de dados compartilhado.
- As denúncias ficam apenas no dispositivo em que foram registradas. O RH só verá esses dados se usar o mesmo dispositivo/instalação.
- Não use informações reais ou sensíveis. O anonimato não foi auditado e não deve ser tratado como garantia de segurança para uso real.
- Não há autenticação, sincronização entre celulares, recuperação de protocolo ou envio de notificações.

