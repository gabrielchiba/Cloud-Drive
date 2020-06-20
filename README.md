# Cloud Drive

## Como executar

Certifique-se de possuir o JDK 11 ou mais recente instalado em sua máquina.

Abra a pasta raiz ou o projeto (`Cloud.iml`) no Intellij IDEA Community Edition e clique em *Build > Build Project*.

O executável será criado em `out/artifacts/Cloud/Cloud.jar`. No terminal faça:

```bash
$ cd out/artifacts/Cloud/
$ rmiregistry -J-classpath -JCloud.jar &    # Serviço de registro do RMI em segundo plano
$ java -jar Cloud.jar --server &            # Servidor Cloud em segundo plano
$ java -jar Cloud.jar localhost             # Cliente Cloud
```

Ou se preferir, abra três terminais na pasta `out/artifacts/Cloud/` e execute uma linha de comando por terminal:

- `$ rmiregistry -J-classpath -JCloud.jar`
- `java -jar Cloud.jar --server`
- `java -jar Cloud.jar localhost`


