# 🔐 Diffie-Hellman em Java

> Implementação do algoritmo de troca de chaves **Diffie-Hellman** com criptografia **AES** para comunicação segura entre Cliente e Servidor via sockets.

---

## 📋 Índice

- [Visão Geral](#-visão-geral)
- [Como Funciona o Diffie-Hellman](#-como-funciona-o-diffie-hellman)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Pré-requisitos](#-pré-requisitos)
- [Compilação e Execução](#-compilação-e-execução)
- [Passo a Passo da Execução](#-passo-a-passo-da-execução)
- [Comandos e suas Funções](#-comandos-e-suas-funções)
- [Fluxo de Comunicação](#-fluxo-de-comunicação)
- [Detalhes Técnicos](#-detalhes-técnicos)
- [Exemplo Standalone](#-exemplo-standalone)
- [Aviso](#-aviso)

---

## 🌐 Visão Geral

Este projeto demonstra como duas partes (Cliente e Servidor) podem estabelecer um **segredo compartilhado** através de um canal inseguro, utilizando o algoritmo **Diffie-Hellman**, e então usar esse segredo para **criptografar e descriptografar mensagens** com o algoritmo **AES-256**.

### Conceitos fundamentais

| Conceito | Descrição |
|----------|-----------|
| **Diffie-Hellman** | Algoritmo de troca de chaves que permite a duas partes gerarem um segredo compartilhado sem nunca transmiti-lo |
| **AES-256** | Algoritmo de criptografia simétrica usado para cifrar as mensagens com o segredo derivado |
| **SHA-256** | Função hash usada para derivar uma chave AES de 256 bits a partir do segredo DH |
| **CBC/PKCS5Padding** | Modo de operação AES com padding para garantir que a mensagem tenha tamanho múltiplo do bloco |
| **IV (Vetor de Inicialização)** | Valor aleatório usado no modo CBC para garantir que mensagens iguais produzam cifras diferentes |

---

## 🔄 Como Funciona o Diffie-Hellman

O algoritmo Diffie-Hellman baseia-se na propriedade matemática da **exponenciação modular**:

```
Segredo Compartilhado = (g^a mod p)^b mod p = (g^b mod p)^a mod p
```

### Analogia da Tinta

> Imagine que Alice e Bob querem combinar uma cor secreta:
> 1. Ambos começam com uma cor pública (amarelo)
> 2. Cada um adiciona uma cor secreta diferente
> 3. Trocam as misturas resultantes
> 4. Cada um adiciona sua própria cor secreta à mistura recebida
> 5. Ambos chegam à mesma cor final — o segredo compartilhado!

---

## 📁 Estrutura do Projeto

```
diffiehellman/
├── app/
│   └── src/main/java/br/com/pvv/
│       ├── App.java              → Classe principal (main)
│       ├── DiffieHellman.java    → Núcleo: troca de chaves + AES
│       ├── Client.java           → Cliente de socket
│       ├── Server.java           → Servidor de socket
│       ├── MensagemCifrada.java  → DTO (IV + mensagem cifrada)
│       └── Example.java          → Exemplo standalone (sem sockets)
├── build/
├── gradle/
├── gradlew / gradlew.bat
├── settings.gradle
└── gradle.properties
```

---

## ⚙️ Pré-requisitos

| Ferramenta | Versão Mínima |
|------------|---------------|
| **JDK** | 17+ |
| **Gradle** | 8.x (incluído via wrapper) |

---

## 🚀 Compilação e Execução

### 1. Compilar o projeto

```bash
./gradlew build
```

### 2. Executar o Servidor

```bash
./gradlew run --args="--server"
```

### 3. Executar o Cliente (em outro terminal)

```bash
./gradlew run --args="--client"
```

---

## 📝 Passo a Passo da Execução

### Fluxo completo da comunicação

```
┌──────────┐                              ┌──────────┐
│  Client  │                              │  Server  │
└────┬─────┘                              └────┬─────┘
     │                                         │
     │  1. Gera par de chaves DH (2048 bits)   │
     │ ──────────────────────────────────────► │
     │         Envia PublicKey do Cliente       │
     │                                         │
     │         2. Gera par de chaves DH        │
     │ ◄────────────────────────────────────── │
     │         Envia PublicKey do Servidor     │
     │                                         │
     │  3. Calcula segredo compartilhado       │
     │     (usando chave privada + chave       │
     │      pública recebida)                   │
     │                                         │
     │  4. Deriva chave AES-256 via SHA-256     │
     │                                         │
     │  5. Cifra mensagem com AES/CBC          │
     │ ──────────────────────────────────────► │
     │         Envia MensagemCifrada            │
     │         (IV + ciphertext)                │
     │                                         │
     │                          6. Deriva chave │
     │                             AES-256     │
     │                                         │
     │                          7. Decifra     │
     │                             mensagem    │
     │                                         │
```

### Detalhamento dos passos

| Passo | Ação | Classe/Método |
|-------|------|---------------|
| **1** | Cliente gera par de chaves DH | `new DiffieHellman(2048)` |
| **2** | Cliente envia chave pública ao Servidor | `client.sendPhase(publicKey)` |
| **3** | Servidor gera par de chaves DH | `new DiffieHellman(2048)` |
| **4** | Servidor envia chave pública ao Cliente | `server.sendPhase(publicKey)` |
| **5** | Ambos calculam o segredo compartilhado | `dh.generateSecret(otherPublicKey)` |
| **6** | Ambos derivam chave AES-256 com SHA-256 | `MessageDigest.getInstance("SHA-256")` |
| **7** | Cliente cifra a mensagem | `dh.cifrar(mensagem)` |
| **8** | Cliente envia mensagem cifrada | `client.send(mensagemCifrada)` |
| **9** | Servidor recebe e decifra | `dh.decifrar(mensagemCifrada)` |

---

## 🛠️ Comandos e suas Funções

### Comandos Gradle

| Comando | Função |
|---------|--------|
| `./gradlew build` | Compila o projeto e gera o JAR executável em `app/build/libs/` |
| `./gradlew run --args="--server"` | Inicia o servidor na porta `50000` e aguarda conexão |
| `./gradlew run --args="--client"` | Inicia o cliente, conecta ao servidor em `127.0.0.1:50000` |
| `./gradlew clean` | Remove o diretório `build/` e todos os artefatos compilados |
| `./gradlew tasks` | Lista todas as tarefas disponíveis do Gradle |

### Parâmetros de Linha de Comando

| Parâmetro | Descrição |
|-----------|-----------|
| `--server` | Executa a aplicação no modo **Servidor** (aguarda conexão na porta 50000) |
| `--client` | Executa a aplicação no modo **Cliente** (conecta ao servidor local) |

### Métodos principais da classe `DiffieHellman`

| Método | Função |
|--------|--------|
| `DiffieHellman(int keysize)` | Construtor: gera par de chaves DH com o tamanho especificado (2048 bits) |
| `getPublic()` | Retorna a chave pública para enviar à outra parte |
| `generateSecret(PublicKey otherKey)` | Calcula o segredo compartilhado usando a chave privada local e a chave pública recebida |
| `cifrar(String mensagem)` | Cifra uma mensagem usando AES-256/CBC com o segredo derivado |
| `decifrar(MensagemCifrada mc)` | Decifra uma mensagem recebida usando AES-256/CBC |

### Métodos de comunicação (Client/Server)

| Método | Função |
|--------|--------|
| `sendPhase(PublicKey msg)` | Envia a chave pública DH para a outra parte |
| `receivePhase()` | Recebe a chave pública DH da outra parte |
| `send(MensagemCifrada msg)` | Envia a mensagem cifrada (IV + ciphertext) |
| `receive()` | Recebe a mensagem cifrada da outra parte |

---

## 🔁 Fluxo de Comunicação

### Sequência de mensagens

```
Cliente                              Servidor
   │                                    │
   │  ──── PublicKey (Client) ────►     │
   │                                    │
   │  ◄──── PublicKey (Server) ────     │
   │                                    │
   │  [Calcula segredo DH]              │
   │  [Deriva chave AES via SHA-256]    │
   │                                    │
   │  ──── MensagemCifrada ────►        │
   │       (IV + ciphertext)            │
   │                                    │
   │                    [Deriva chave]  │
   │                    [Decifra]       │
   │                                    │
```

---

## 🔬 Detalhes Técnicos

### Geração de chaves DH

```java
KeyPairGenerator generator = KeyPairGenerator.getInstance("DH");
generator.initialize(2048);  // 2048 bits de segurança
KeyPair keyPair = generator.generateKeyPair();
```

### Derivação da chave AES

```java
MessageDigest sha = MessageDigest.getInstance("SHA-256");
byte[] aesKeyBytes = sha.digest(secret);  // 32 bytes = 256 bits
SecretKeySpec aesKey = new SecretKeySpec(aesKeyBytes, "AES");
```

### Criptografia AES

```java
Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
cipher.init(Cipher.ENCRYPT_MODE, aesKey);
byte[] iv = cipher.getIV();  // IV gerado automaticamente
byte[] ciphertext = cipher.doFinal(mensagem.getBytes(StandardCharsets.UTF_8));
```

### Descriptografia AES

```java
Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv));
byte[] plaintext = cipher.doFinal(ciphertext);
String mensagem = new String(plaintext, StandardCharsets.UTF_8);
```

---

## 📦 Exemplo Standalone

O arquivo `Example.java` demonstra o algoritmo Diffie-Hellman **sem utilizar sockets**, executando tudo em um único processo. Para executá-lo, basta rodar diretamente a classe `Example`:

```bash
./gradlew build
java -cp app/build/classes/java/main br.com.pvv.Example
```

> **Nota:** O `Example.java` simula duas entidades (Alice e Bob) na mesma JVM, mostrando que ambas chegam ao mesmo segredo compartilhado.

---

## ⚠️ Aviso

> **Este tutorial foi produzido utilizando Inteligência Artificial (I.A.).**
>
> Embora o conteúdo tenha sido revisado e baseado em práticas reais de criptografia, recomenda-se sempre consultar a documentação oficial e realizar testes de segurança antes de utilizar qualquer implementação criptográfica em ambiente de produção.

---

## 📚 Referências

- [Diffie-Hellman Key Exchange — Wikipedia](https://en.wikipedia.org/wiki/Diffie%E2%80%93Hellman_key_exchange)
- [Java Cryptography Architecture (JCA)](https://docs.oracle.com/javase/8/docs/technotes/guides/security/crypto/CryptoSpec.html)
- [AES (Advanced Encryption Standard) — NIST](https://csrc.nist.gov/projects/advanced-encryption-standard)

---

<div align="center">

**🔒 A segurança da informação é um processo contínuo.**

</div>
