# srx-shared-data
Student Record Exchange data I/O functions shared by SRX components and services.

## Enivronment variables
These are necessary for unit testing.

For local development, create a file named 'env-local.properties' in the project root and add them there.

Following is a description of each variable:

Variable 					            | Description 												| Example
--------- 					            | ----------- 												| -------
DATASOURCE_CLASS_NAME                   | Class name of data source type                            |org.postgresql.ds.PGSimpleDataSource
DATASOURCE_MAX_CONNECTIONS              | Maximum allowed connections                               | 1
DATASOURCE_TIMEOUT                      | Connection timeout                                        | 10000
DATASOURCE_URL                          | Connection string                                         | postgres://user:password@example.com:port/dbName
