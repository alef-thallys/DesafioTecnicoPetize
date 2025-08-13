# Desafio Técnico Petize - Gerenciador de Tarefas

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen)
![Tests](https://img.shields.io/badge/Coverage-91%25-success)
![Docker](https://img.shields.io/badge/Docker-Ready-blue)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

API RESTful para gerenciamento de tarefas e subtarefas, desenvolvida com **Spring Boot 3.4.5**, **Java 17** e **TDD**, atingindo **91% de cobertura de testes**. Foco em segurança, escalabilidade e boas práticas de mercado.

Inclui autenticação JWT, documentação interativa via Swagger e ambiente pronto para rodar com Docker.

---

## Índice

- [Principais Funcionalidades](#principais-funcionalidades)
- [Testes e Qualidade de Código](#testes-e-qualidade-de-código)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Pré-requisitos](#pré-requisitos)
- [Como Executar](#como-executar)
- [Documentação da API (Swagger)](#documentação-da-api-swagger)
- [Endpoints da API](#endpoints-principais)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Aprendizados](#aprendizados)

---

## Principais Funcionalidades

- Autenticação segura: login e registro de usuários com JWT.
- CRUD completo: criação, leitura, atualização e exclusão de tarefas.
- Subtarefas: organização avançada com tarefas principais e subtarefas.
- Priorização e status: controle personalizado para cada tarefa.
- Documentação interativa: Swagger UI para explorar e testar endpoints.
- Containerização: banco MySQL configurado com Docker Compose.

---

## Testes e Qualidade de Código

Desenvolvido com **Test-Driven Development (TDD)** para garantir robustez e confiabilidade.

| Módulo         | Cobertura de Classe | Cobertura de Método | Cobertura de Linha |
|----------------|--------------------|---------------------|-------------------|
| Geral          | 96% (32/33)        | 85% (97/113)        | 91% (290/316)     |
| assemblers     | 100% (1/1)         | 100% (1/1)          | 100% (11/11)      |
| config         | 100% (4/4)         | 100% (9/9)          | 100% (23/23)      |
| controllers    | 100% (2/2)         | 100% (9/9)          | 100% (25/25)      |
| dtos           | 100% (7/7)         | 100% (7/7)          | 100% (29/29)      |
| enums          | 100% (3/3)         | 100% (6/6)          | 100% (9/9)        |
| exceptions     | 100% (6/6)         | 92% (13/14)         | 88% (60/68)       |
| models         | 100% (3/3)         | 69% (29/42)         | 68% (33/48)       |
| repositories   | 0% (0/0)           | 100% (0/0)          | 100% (0/0)        |
| security       | 100% (2/2)         | 100% (7/7)          | 100% (17/17)      |
| services       | 100% (3/3)         | 92% (12/13)         | 98% (67/68)       |
| utils          | 100% (1/1)         | 100% (5/5)          | 100% (32/32)      |

Cobertura menor em `models` e `exceptions` é comum devido à baixa complexidade de lógica nessas camadas.

---

## Tecnologias Utilizadas

| Categoria         | Tecnologia                                                      |
|-------------------|-----------------------------------------------------------------|
| Backend           | Java 17, Spring Boot 3, Spring Security, Spring Data JPA, Maven |
| Banco de Dados    | MySQL                                                           |
| Autenticação      | JWT (JSON Web Token)                                            |
| Documentação      | SpringDoc OpenAPI (Swagger)                                     |
| Containerização   | Docker                                                          |

---

## Pré-requisitos

- Java 17+
- Maven 3.8+
- Docker
- Git

---

## Como Executar

Clone o repositório:
```bash
git clone https://github.com/alef-thallys/DesafioTecnicoPetize.git
cd DesafioTecnicoPetize
```

Suba o banco de dados com Docker:
```bash
docker-compose up -d
```

Execute a aplicação:

Com Maven Wrapper (recomendado):
```bash
./mvnw spring-boot:run
```

Ou build + execução do JAR:
```bash
./mvnw clean install
java -jar target/DesafioTecnicoPetize-0.0.1-SNAPSHOT.jar
```

A aplicação estará disponível em:  
http://localhost:8080

---

## Documentação da API (Swagger)

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

Permite testar todos os endpoints diretamente no navegador.

---

## Endpoints Principais

### Autenticação (`/auth`)
- `POST /login` — Autentica usuário e retorna JWT.
- `POST /register` — Registra novo usuário.

### Tarefas (`/tasks`)
- `GET /` — Lista tarefas do usuário autenticado.
- `GET /{id}` — Detalhes de uma tarefa.
- `POST /` — Cria nova tarefa.
- `PUT /{id}` — Atualiza tarefa.
- `DELETE /{id}` — Remove tarefa.

---

## Estrutura do Projeto

```
src/main/java/com/github/alefthallys/desafiotecnicopetize/
├── assemblers/     # Respostas HATEOAS
├── config/         # Configurações (OpenAPI, Segurança)
├── controllers/    # Controladores REST
├── dtos/           # Data Transfer Objects
├── enums/          # Enumerações
├── exceptions/     # Exceções e handlers globais
├── models/         # Entidades JPA
├── repositories/   # Repositórios Spring Data
├── security/       # JWT e segurança
└── services/       # Lógica de negócio
```

---

## Aprendizados

Durante o desenvolvimento deste projeto, aprofundei conhecimentos em:
- Spring Boot 3 e Java 17
- Segurança com JWT e Spring Security
- Test-Driven Development (TDD) e cobertura de testes
- Documentação de APIs com Swagger
- Configuração de ambiente com Docker Compose
- Boas práticas de arquitetura em aplicações RESTful
