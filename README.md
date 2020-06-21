# Cloud Drive

## Como executar

Certifique-se de possuir o JDK 11 ou mais recente instalado em sua máquina.

Abra a pasta raiz ou o projeto (`Cloud.iml`) no Intellij IDEA Community Edition e clique em *Build > Build Project*.

Os pacotes `.jar` serão criados em `out/artifacts/`.
Para rodar o *servidor* no terminal, faça:

```bash
$ cd out/artifacts/cloud_server/
$ rmiregistry -J-classpath -Jcloud-server.jar &     # Serviço de registro do RMI em segundo plano
$ java -jar cloud-server.jar /tmp/cloud-server      # Servidor Cloud em segundo plano
```

Para rodar o *cliente* no terminal, faça:
```bash
$ cd out/artifacts/cloud_client/
$ java -jar cloud-client.jar localhost
```
