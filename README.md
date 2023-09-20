# Content service

## Package structure

The service is structured similar to the microservice template.

## Environment variables

### Relevant for deployment

| Name                       | Description                        | Value in Dev Environment                         | Value in Prod Environment                                            |
|----------------------------|------------------------------------|--------------------------------------------------|----------------------------------------------------------------------|
| spring.datasource.url      | PostgreSQL database URL            | jdbc:postgresql://localhost:4032/content-service | jdbc:postgresql://content-service-db-postgresql:5432/content-service |
| spring.datasource.username | Database username                  | root                                             | gits                                                                 |
| spring.datasource.password | Database password                  | root                                             | *secret*                                                             |
| DAPR_HTTP_PORT             | Dapr HTTP Port                     | 4000                                             | 3500                                                                 |
| server.port                | Port on which the application runs | 4001                                             | 4001                                                                 |

### Other properties
| Name                                      | Description                               | Value in Dev Environment                  | Value in Prod Environment               |
|-------------------------------------------|-------------------------------------------|-------------------------------------------|-----------------------------------------|
| spring.graphql.graphiql.enabled           | Enable GraphiQL web interface for GraphQL | true                                      | true                                    |
| spring.graphql.graphiql.path              | Path for GraphiQL when enabled            | /graphiql                                 | /graphiql                               |
| spring.profiles.active                    | Active Spring profile                     | dev                                       | prod                                    |
| spring.jpa.properties.hibernate.dialect   | Hibernate dialect for PostgreSQL          | org.hibernate.dialect.PostgreSQLDialect** | org.hibernate.dialect.PostgreSQLDialect |
| spring.datasource.driver-class-name       | JDBC driver class                         | org.postgresql.Driver                     | org.postgresql.Driver                   |
| spring.sql.init.mode                      | SQL initialization mode                   | always                                    | always                                  |
| spring.jpa.show-sql                       | Show SQL queries in logs                  | true                                      | true                                    |
| spring.sql.init.continue-on-error         | Continue on SQL init error                | true                                      | true                                    |
| spring.jpa.hibernate.ddl-auto             | Hibernate DDL auto strategy               | create                                    | update                                  |
| hibernate.create_empty_composites.enabled | Enable empty composite types in Hibernate | true                                      | true                                    |
| DAPR_GRPC_PORT                            | Dapr gRPC Port                            | -                                         | 50001                                   |

## Integration tests

For integration tests, a H2 database is used instead of a separate Postgresql database server because of the following
reasons:

- the database content is easier to manage as it is kept in memory and created from scratch
- it is faster
- database is automatically started and stopped when tests are executed or the application is started

For Postgresql and H2, two different configurations are required.
For this purpose, a separate configuration file `application-dev.properties` is used which contains all the configuration
settings for development. To activate this, the following environment variable has to be set: `SPRING_CONFIG_NAME=dev`.

## API description

The GraphQL API is described in the [api.md file](api.md).

The endpoint for the GraphQL API is `/graphql`. The GraphQL Playground is available at `/graphiql`.

## How to run

How to run services locally is described in
the [wiki](https://gits-enpro.readthedocs.io/en/latest/dev-manuals/backend/get-started.html).

