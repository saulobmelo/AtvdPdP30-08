# RELATÓRIO DE DIAGNÓSTICO ARQUITETURAL

## Smart Room Monitor System - Fase 2

**Curso:** Superior de Tecnologia em Análise e Desenvolvimento de Sistemas  
**Disciplina:** Padrões de Projeto  
**Data:** 23/08/2025  
**Discentes:** Saulo Melo, Joseh Teixeira, Flavio Costa, Vinicius Xavier

---

## 1. RESUMO EXECUTIVO

Este relatório apresenta uma análise crítica da arquitetura do Smart Room Monitor System, identificando problemas estruturais graves que comprometem a manutenibilidade, escalabilidade e qualidade do código. O sistema apresenta múltiplas violações dos princípios SOLID e implementa diversos anti-padrões de forma sistemática.

## 2. ARQUITETURA ATUAL

### 2.1 Visão Geral

O sistema atual concentra toda a lógica de negócio em uma única classe `SmartRoomSystem.java` com 400+ linhas, violando massivamente o princípio da responsabilidade única.

### 2.2 Estrutura de Classes

```
SmartRoomSystem.java (400+ linhas)
├── Responsabilidades HTTP (HttpHandler)
├── Lógica de Sensores
├── Persistência de Dados
├── Geração de Relatórios  
├── Controle de Dispositivos
├── Processamento de Dados
├── Parsing JSON Manual
└── Timer/Scheduling

Aplicacao.java (100 linhas)
├── Inicialização do Servidor
├── Configuração HTTP
└── Serving de Arquivos Estáticos
```

## 3. VIOLAÇÕES DOS PRINCÍPIOS SOLID

### 3.1 Single Responsibility Principle (SRP) - VIOLAÇÃO CRÍTICA

**Problema:** A classe `SmartRoomSystem` possui 15+ responsabilidades distintas:

1. **Handler HTTP** - Processar requisições web
2. **Persistência** - Salvar/carregar dados de arquivos
3. **Lógica de Sensores** - Gerenciar tipos de sensores
4. **Simulação de Dados** - Gerar valores aleatórios
5. **Controle de Dispositivos** - Ligar/desligar equipamentos
6. **Geração de Relatórios** - Criar documentos
7. **Parsing JSON** - Processar dados da API
8. **Scheduling** - Gerenciar timers
9. **Validação** - Verificar dados de entrada
10. **Log de Ações** - Registrar atividades
11. **Configuração** - Gerenciar constantes
12. **Estado Global** - Manter variáveis do sistema
13. **Formatação de Data** - Converter timestamps
14. **Processamento Automático** - Executar ações
15. **Shutdown** - Finalizar sistema

**Impacto:** Qualquer mudança em uma responsabilidade afeta todas as outras, tornando o sistema extremamente frágil.

### 3.2 Open/Closed Principle (OCP) - VIOLAÇÃO GRAVE

**Código Problemático:**

```java
// Para adicionar novo tipo de sensor, precisa MODIFICAR este código
if (!tipo.equals("temperatura") && !tipo.equals("presenca") && 
    !tipo.equals("luminosidade") && !tipo.equals("umidade")) {
    return "ERRO: Tipo inválido";
}
```

**Problema:** Impossível adicionar novos tipos de sensores sem modificar código existente.

**Impacto:** Sistema fechado para extensão, violando o princípio fundamental de flexibilidade.

### 3.3 Liskov Substitution Principle (LSP) - VIOLAÇÃO POR AUSÊNCIA

**Problema:** Sistema não utiliza herança ou polimorfismo, impossibilitando substituição de implementações.

### 3.4 Interface Segregation Principle (ISP) - VIOLAÇÃO TOTAL

**Problema:** Nenhuma interface é utilizada. Todas as dependências são concretas.

**Impacto:** Alto acoplamento e impossibilidade de testes unitários isolados.

### 3.5 Dependency Inversion Principle (DIP) - VIOLAÇÃO CRÍTICA

**Código Problemático:**

```java
// Dependências diretas de classes concretas
FileWriter writer = new FileWriter(DATA_FILE, false);
Timer timer = new Timer();
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
```

**Problema:** Dependência direta de implementações concretas ao invés de abstrações.

## 4. ANTI-PADRÕES IDENTIFICADOS

### 4.1 God Object - CRÍTICO

**Localização:** `SmartRoomSystem.java` (linhas 1-400+)

**Evidências:**

- Classe com 400+ linhas
- 15+ responsabilidades diferentes
- 30+ métodos públicos e privados
- Controla todo o ciclo de vida da aplicação

**Justificativa:** Uma única classe gerencia sensores, HTTP, persistência, relatórios, dispositivos e scheduling simultaneamente.

### 4.2 Spaghetti Code - GRAVE

**Localização:** Método `processAutomaticActions()` (linhas 150-200)

**Código Problemático:**

```java
if (presenca == true) {
    if (luminosidade < 300) {
        if (lightStatus == false) {
            lightStatus = true;
            // Lógica aninhada profundamente
        }
    } else {
        if (lightStatus == true) {
            // Mais aninhamento...
        }
    }
} else {
    // Duplicação de lógica...
}
```

**Justificativa:** Condicionais aninhadas profundamente com lógica interdependente e confusa.

### 4.3 Magic Numbers - MODERADO

**Localizações Identificadas:**

```java
private static final int MAX_TEMP = 30;        // Linha 12
private static final int MIN_TEMP = 18;        // Linha 13  
int lux = 200 + random.nextInt(600);           // Linha 134
boolean presenca = random.nextInt(100) < 30;   // Linha 127
```

**Justificativa:** Valores numéricos hardcoded sem explicação contextual.

### 4.4 Copy-Paste Programming - GRAVE

**Localização:** Método `setupDefaultSensors()` (linhas 70-90)

**Código Duplicado:**

```java
// Padrão repetido 5 vezes
sensorsData.add("TEMP001|Sensor Temperatura 1|temperatura|22.5|true");
sensorCount++;

sensorsData.add("TEMP002|Sensor Temperatura 2|temperatura|22.0|true");
sensorCount++;
```

**Justificativa:** Mesmo padrão de código copiado e colado com mínimas variações.

### 4.5 Lava Flow - BAIXO

**Localização:** Método `obsoleteMethod()` (linha 380)

**Código Morto:**

```java
public void obsoleteMethod() {
    System.out.println("Este método nunca é usado mas está aqui");
    unusedCounter++;
    deprecatedConfig = "still_here";
}
```

**Justificativa:** Método e variáveis nunca utilizados mantidos no código.

## 5. PROBLEMAS DE ACOPLAMENTO E COESÃO

### 5.1 Alto Acoplamento

**Problema 1: Acoplamento Temporal**

```java
timer.scheduleAtFixedRate(new TimerTask() {
    @Override
    public void run() {
        collectSensorData();        // Depende de currentValues
        processAutomaticActions();  // Depende de collectSensorData()
        saveDataToFile();          // Depende de todos os anteriores
        generateReport();          // Depende de saveDataToFile()
    }
}, 0, 5000);
```

**Problema 2: Acoplamento de Dados**

- Todas as funcionalidades compartilham as mesmas estruturas globais
- Modificação em uma estrutura afeta múltiplas funcionalidades

### 5.2 Baixa Coesão

**Evidências:**

- Métodos de HTTP misturados com lógica de negócio
- Persistência de dados junto com processamento
- Formatação de JSON junto com controle de dispositivos

## 6. PROBLEMAS DE DESIGN ESPECÍFICOS

### 6.1 Responsabilidades Misturadas

**Trecho Crítico 1:** Método `handle()` (linhas 250-300)

```java
@Override
public void handle(HttpExchange exchange) throws IOException {
    // HTTP handling misturado com lógica de negócio
    if (path.equals("/api/sensors") && method.equals("GET")) {
        response = getSensorsJson(); // Serialização JSON aqui
    }
    // ... mais 20 linhas de if/else
}
```

**Trecho Crítico 2:** Método `saveDataToFile()` (linhas 180-220)

```java
public void saveDataToFile() {
    // Formatação misturada com persistência
    writer.write("Temperatura: " + currentValues.get("temperatura") + "°C\n");
    writer.write("Presença: " + currentValues.get("presenca") + "\n");
    // Lógica de apresentação dentro da persistência
}
```

### 6.2 Parsing JSON Primitivo

**Problema:** Método `extractJsonValue()` (linhas 360-370)

```java
private String extractJsonValue(String json, String key) {
    String searchKey = "\"" + key + "\"";
    int startIndex = json.indexOf(searchKey);
    // Parsing manual sujeito a erros
}
```

**Justificativa:** Implementação manual de parsing JSON ignorando bibliotecas estabelecidas.

### 6.3 Gestão de Estado Global

**Problema:** Variáveis estáticas públicas (linhas 15-35)

```java
public static List<String> sensorsData = new ArrayList<>();
public static Map<String, Object> currentValues = new HashMap<>();
public static boolean lightStatus = false;
```

**Justificativa:** Estado global acessível por qualquer parte do código, violando encapsulamento.

## 7. PROBLEMAS DE MANUTENIBILIDADE

### 7.1 Testabilidade Impossível

- Dependências hardcoded impedem testes unitários
- Estado global torna testes não determinísticos
- Múltiplas responsabilidades impedem testes isolados

### 7.2 Extensibilidade Limitada

- Adicionar novo tipo de sensor requer modificação em múltiplos locais
- Não há pontos de extensão definidos
- Lógica de negócio acoplada à infraestrutura

### 7.3 Legibilidade Comprometida

- Métodos extremamente longos (até 80 linhas)
- Nomes de variáveis não expressivos
- Lógica de negócio misturada com detalhes técnicos

## 8. DIAGRAMA UML DA ESTRUTURA ATUAL

```java
┌─────────────────────────────────────────┐
│             SmartRoomSystem             │
├─────────────────────────────────────────┤
│ - sensorsData: List<String>             │
│ - currentValues: Map<String, Object>    │
│ - actionLog: List<String>               │
│ - lightStatus: boolean                  │
│ - fanStatus: boolean                    │
│ - timer: Timer                          │
├─────────────────────────────────────────┤
│ + setupDefaultSensors(): void           │
│ + collectSensorData(): void             │
│ + processAutomaticActions(): void       │
│ + saveDataToFile(): void                │
│ + generateReport(): void                │
│ + loadDataFromFile(): void              │
│ + cadastrarSensor(...): String          │
│ + handle(HttpExchange): void            │
│ + getCurrentDataJson(): String          │
│ + getSensorsJson(): String              │
│ + handleDeviceControl(String): String   │
│ + extractJsonValue(...): String         │
│ + obsoleteMethod(): void                │
└─────────────────────────────────────────┘
```

## 9. IMPACTOS DOS PROBLEMAS IDENTIFICADOS

### 9.1 Impactos Técnicos

- **Manutenibilidade:** Extremamente baixa devido ao acoplamento
- **Testabilidade:** Praticamente impossível fazer testes unitários  
- **Performance:** Ineficiente devido a parsing manual e loops desnecessários
- **Escalabilidade:** Limitada pela arquitetura monolítica

### 9.2 Impactos de Negócio

- **Time-to-Market:** Lento devido à complexidade de mudanças
- **Custo de Desenvolvimento:** Alto devido à dificuldade de manutenção
- **Qualidade:** Baixa devido à propensão a bugs
- **Flexibilidade:** Limitada para novos requisitos

## 10. RECOMENDAÇÕES PARA REFATORAÇÃO

### 10.1 Padrões GOF Recomendados

1. **Factory Method:** Para criação de diferentes tipos de sensores
2. **Observer:** Para notificação de mudanças de estado
3. **Strategy:** Para diferentes algoritmos de processamento
4. **Command:** Para ações de controle de dispositivos
5. **Facade:** Para simplificar interface com subsistemas

### 10.2 Princípios a Aplicar

1. **Separação de Responsabilidades:** Dividir em classes específicas
2. **Injeção de Dependência:** Desacoplar implementações
3. **Interface Segregation:** Criar interfaces específicas
4. **Single Purpose:** Uma responsabilidade por classe

## 11. CONCLUSÃO

O Smart Room Monitor System apresenta graves problemas arquiteturais que classificam o código como **legado técnico** antes mesmo de estar em produção. A concentração de responsabilidades em uma única classe, combinada com múltiplas violações dos princípios SOLID, cria um sistema extremamente frágil e difícil de manter.

A refatoração usando padrões de projeto é **criticamente necessária** para tornar o sistema:

- Testável
- Manutenível  
- Extensível
- Legível
- Performático

Sem essas mudanças, o sistema se tornará progressivamente mais difícil de manter e evoluir, resultando em custos crescentes de desenvolvimento e alta probabilidade de bugs em produção.
