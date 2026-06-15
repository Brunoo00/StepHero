# StepHero

**Disciplina:** Dispositivos Móveis 2  
**Alunos:** Bruno Ferreira e Igor Ralha  
**IFSP - Campus Araraquara | Análise e Desenvolvimento de Sistemas**

---

## Descrição

StepHero é um aplicativo Android de missões diárias de caminhada com gamificação. O usuário recebe missões para andar uma determinada quantidade de passos por dia. Conforme caminha, acumula XP, sobe de nível, registra fotos dos marcos do percurso e desbloqueia conquistas.

A proposta é transformar a atividade física diária em uma experiência de RPG, incentivando o usuário a se movimentar mais de forma divertida.

---

## Recursos Utilizados

### Sensores e recursos multimídia
- **Acelerômetro** — detecta movimento e conta os passos durante a caminhada em tempo real
- **Magnetômetro (Bússola)** — exibe a direção cardeal durante a caminhada usando o campo magnético do dispositivo
- **Câmera** — permite registrar fotos dos marcos durante a missão e uma foto final ao concluir

### Banco de dados
- **Firebase Firestore** — armazena usuários, missões e conquistas na nuvem

### Autenticação
- **Firebase Authentication** — login e cadastro com e-mail e senha

### Outros recursos
- **Notificações** — notifica o usuário ao registrar um marco e ao concluir uma missão
- **Sistema de XP e Níveis** — o usuário ganha 50 XP por missão concluída e sobe de nível
- **Conquistas automáticas** — desbloqueadas conforme o progresso do usuário

---

## Telas do App

1. **Login** — autenticação com e-mail e senha
2. **Cadastro** — criação de conta com nome do herói
3. **Home** — missão do dia, XP e navegação
4. **Caminhada** — passos em tempo real + bússola + registro de marco
5. **Foto do Marco** — câmera para foto durante ou ao concluir a missão
6. **Conclusão da Missão** — tela de celebração com stats e XP ganho
7. **Perfil** — dados do herói, nível e histórico
8. **Conquistas** — lista de badges desbloqueados

---

## Organização do Código

O projeto segue boas práticas de desenvolvimento Android com separação em pacotes:

- `ui/` — Activities (telas do app)
- `model/` — Classes de dados (Usuario, Missao, Conquista)
- `dao/` — Acesso ao Firebase Firestore
- `auth/` — Autenticação com Firebase
- `adapter/` — Adapter do RecyclerView
- `helper/` — Classes auxiliares (Camera, Bússola, Pedômetro, Notificação, Conquista, Base64)

---

## Demonstração

[Vídeo demonstrativo]() — em breve

---

## Como Instalar
