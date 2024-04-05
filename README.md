# Preparamento o ambiente

## Ferramentas
 - ScyllaDB: 
 ```shell
 docker pull scylladb/scylla:latest
 ```

## Install
```shell
./gradlew build
```

# Rodando a aplicação

## Iniciando o ScyllaDB
```shell
docker run scylladb/scylla:latest
docker ps
docker exec -it <CONTAINER_ID> cqlsh
```
```sql
cqlsh> CREATE KEYSPACE busca
    WITH REPLICATION = {
        'class' : 'SimpleStrategy',
        'replication_factor' : 1 
    };
cqlsh> exit;
```

## Iniciando a aplicação
```shell
./gradlew run
```

## Configuração da aplicação
Arquivo: `application.properties`:
```properties
app.url=http://localhost:8081
app.env=${ENV:prod}
app.name=${NAME:simple}
app.database.csv.user.file=data/users.csv
app.database.scylladb.endpoints=172.17.0.2:9042
app.database.scylladb.localdatacenter=datacenter1
app.database.scylladb.keyspace=busca
app.database.scylladb.autocreatetable=true
```

## Acessando a aplicação
- REST: `http://localhost:8081`
  - Lista todos os usuários: `GET /user`
  - Obtem um usuário pelo *id*: `GET /user/{id}`
  - Remove o usuário pelo *id*: `DELETE /user/{id}`
  - Cria um usuário: `POST /user` - Corpo:
    ```json
    {
        "id": 1,
        "name": "novo_nome",
        "surname": "novo_sobrenome"
    }
    ```
