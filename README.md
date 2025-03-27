# Decola Tech 2025
#Board_GUI

## Diagrama de Classes

````mermaid

classDiagram
    class Board {
        +Long id
        +String name
        +List~BoardColumn~ columns
        +getId() Long
        +getName() String
        +getColumns() List~BoardColumn~
    }

    class BoardColumn {
        +Long id
        +String name
        +int order
        +BoardColumnKindEnum kind
        +Board board
        +List~Card~ cards
        +getId() Long
        +getName() String
        +getOrder() int
        +getKind() BoardColumnKindEnum
        +getBoard() Board
        +getCards() List~Card~
    }

    class Card {
        +Long id
        +String title
        +String description
        +BoardColumn boardColumn
        +List~Block~ blocks
        +getId() Long
        +getTitle() String
        +getDescription() String
        +getBoardColumn() BoardColumn
        +getBlocks() List~Block~
    }

    class Block {
        +Long id
        +OffsetDateTime blockedAt
        +String blockReason
        +OffsetDateTime unblockedAt
        +String unblockReason
        +Card card
        +getId() Long
        +getBlockedAt() OffsetDateTime
        +getBlockReason() String
        +getUnblockedAt() OffsetDateTime
        +getUnblockReason() String
        +getCard() Card
    }

    class BoardColumnKindEnum {
        <<enumeration>>
        INITIAL
        PENDING
        FINAL
        CANCEL
        +findByName(String name) BoardColumnKindEnum
    }

    class BoardDetailsDTO {
        +Long id
        +String name
        +List~BoardColumnDTO~ columns
    }

    class BoardColumnDTO {
        +Long id
        +String name
        +BoardColumnKindEnum kind
        +int cardsAmount
    }

    class CardDetailsDTO {
        +Long id
        +String title
        +String description
        +boolean blocked
        +OffsetDateTime blockedAt
        +String blockReason
        +int blocksAmount
        +Long columnId
        +String columnName
    }

    Board "1" -- "N" BoardColumn : contém
    BoardColumn "1" -- "N" Card : contém
    Card "1" -- "N" Block : possui
    BoardColumn "1" -- "1" BoardColumnKindEnum : tipo

````
===========================================================================
Decola Tech 2025 - Board_GUI
📌 Visão Geral do Projeto
Este projeto representa a evolução de uma aplicação originalmente desenvolvida para console, que agora incorpora uma interface gráfica moderna sem alterar o código base existente. Mantivemos todas as funcionalidades originais enquanto adicionamos a nova interface gráfica.

Código Base Original:

Aplicação de gerenciamento de quadros Kanban via terminal

Desenvolvida com Java 17 e Spring Boot 3

Persistência de dados com JPA e MySQL 8

Arquitetura limpa e bem definida

Novas Implementações:

Interface gráfica desenvolvida com Java Swing

Coexistência perfeita entre versão GUI e console

Código original totalmente preservado sem modificações

Integração não-invasiva entre os módulos


🛠️ Tecnologias Utilizadas
Tecnologia	Versão	Uso no Projeto
Java	17	Linguagem base para ambas interfaces
Spring Boot	3.x	Framework backend principal
Spring Data JPA	-	Camada de persistência
Swing	-	Implementação da interface gráfica
MySQL	8	Banco de dados relacional
Liquibase	-	Controle de migrações de schema
Maven	-	Gerenciamento de dependências
OpenAPI/Swagger	-	Documentação de APIs
✨ Melhorias Implementadas
Interface Gráfica
Visualização interativa dos quadros Kanban

Operações drag-and-drop intuitivas

Janelas modais para detalhamento

Sistema de temas personalizáveis

Feedback visual imediato das ações

Compatibilidade
100% das funcionalidades originais preservadas

Mesma base de código compartilhada

Mesmos endpoints e modelos de dados

Possibilidade de execução em modo console (--console)

Novos Recursos
Visualização em tempo real das alterações

Histórico gráfico de movimentações

Filtros avançados por tipo e status

Exportação visual (PNG/PDF/CSV)

Atalhos de teclado personalizáveis

⚙️ Fluxo Principal
Criação de Board com múltiplas colunas

Adição de Cards na coluna INITIAL

Movimentação entre colunas (INITIAL → PENDING → FINAL)

Bloqueio/desbloqueio de Cards quando necessário

Cancelamento de Cards (movimento para CANCEL)

Utilização de DTOs para transferência entre camadas

===========================================================================

https://github.com/jonson22rodriguesalves/Board_GUI.git
https://github.com/digitalinnovationone/board

===========================================================================

Diagrama de Relacionamentos:
Board (Quadro)

Possui múltiplas BoardColumn (colunas)

Representa um quadro completo de trabalho

BoardColumn (Coluna)

Pertence a um único Board

Pode conter múltiplos Cards

Tem um tipo definido por BoardColumnKindEnum

Card (Cartão)

Pertence a uma BoardColumn

Pode ter múltiplos Blocks (histórico de bloqueios)

Block (Bloqueio)

Registra o histórico de bloqueios de um Card

Armazena motivos e timestamps

DTOs (Data Transfer Objects)

BoardDetailsDTO: Visão consolidada de Board com colunas

BoardColumnDTO: Resumo de coluna com quantidade de cards

CardDetailsDTO: Detalhes completos de um Card

BoardColumnKindEnum

Define os tipos de colunas:

INITIAL: Coluna inicial

PENDING: Coluna de pendências

FINAL: Coluna final

CANCEL: Coluna de cancelados

Fluxo Principal:
Um Board é criado com várias BoardColumns

Cards são criados na coluna INITIAL

Cards podem ser movidos entre colunas (fluxo: INITIAL → PENDING → FINAL)

Cards podem ser bloqueados (Block) e cancelados (movidos para CANCEL)

DTOs são usados para transferir dados entre camadas