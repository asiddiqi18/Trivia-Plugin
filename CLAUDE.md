# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build

```bash
mvn clean package    # Build and produce JAR in target/
```

The build uses the Maven Shade plugin to relocate bStats. Output JAR goes to `target/`.

Java source/target compatibility is set to 9. Dependencies: Spigot API 1.17.1 (provided), Vault (provided/optional), PlaceholderAPI (provided/optional), bStats (shaded).

## Architecture

This is a Spigot/Bukkit Minecraft plugin. Entry point is `TriviaTreasure.java` (extends `JavaPlugin`), which initializes all components in `onEnable()` in this order: questions → messages → rewards → listeners → commands → integrations.

**Game flow:** `CommandManager` routes `/trivia` subcommands. `GameManager` owns game lifecycle (`startGame`, `stopGame`, `isGameRunning`, `getGame`, `clearGame`) — all commands and listeners go through `triviaTreasure.getGameManager()`. `Game.java` orchestrates trivia rounds — selecting questions from `QuestionContainer`, managing timers, processing chat answers via `ChatEvent` listener, scoring via `PlayerScoreHolder`/`PlayerScore`, and distributing rewards via `Rewards`.

**Key subsystems:**
- **Game lifecycle:** `GameManager` — single owner of the active `Game` instance; extracted from `TriviaTreasure` to isolate the one piece of global mutable state
- **Config:** `TriviaConfig` — typed snapshot of `config.yml` values, created in `loadConfigFile()` and replaced on reload; a running `Game` captures its own snapshot at construction so mid-game reloads don't affect it
- **Questions:** `Question` + `QuestionContainer` (random/unique selection), backed by `questions.yml`
- **Scoring:** `PlayerScore`, `PlayerScoreHolder`; fuzzy answer matching via `StringSimilarity` (configurable threshold, default 0.85)
- **Rewards:** `Rewards[4]` array — index 0=per-round, 1=1st, 2=2nd, 3=3rd place; supports money (Vault), XP, items, commands
- **Persistent stats:** `PlayerDataContainer` uses Bukkit's `PersistentDataContainer` API on the player
- **UI/Menus:** Abstract `Menu` (implements `InventoryHolder`) → concrete subclasses (MainMenu, ParameterMenu, ListMenu, ViewMenu, RewardsMainMenu, etc.); `InventoryClick` listener routes all menu interactions
- **Localization:** `Lang` enum defines all message keys with defaults; resolved at runtime from `messages.yml`. Placeholder substitution lives on `Placeholder.apply(String)` — `Lang.fillPlaceholders` is a delegate to it. `Placeholder` fields `questionNum`/`totalQuestionNum`/`points` are boxed `Integer`; substitution only occurs when explicitly set by the caller.
- **Automation:** `AutomatedGameManager` runs scheduled trivia via BukkitScheduler with configurable interval and min-player requirement
- **Integrations:** Vault (economy rewards), PlaceholderAPI (`TriviaPlaceholder`)

**Dependency injection pattern:** `TriviaTreasure` plugin instance is passed to most components as a constructor argument.

**Async safety:** `ChatEvent` fires on an async thread. `Game.playerAnswer()` uses `AtomicBoolean roundAnswered` (`compareAndSet`) to ensure only one answer is accepted per round, preventing two near-simultaneous correct answers from both advancing the round.

## Configuration files (in `src/main/resources/`)

| File | Purpose |
|------|---------|
| `config.yml` | Timing, similarity threshold, boss bar, sounds, scheduler settings |
| `questions.yml` | Question database (ID → question/answers/author) |
| `rewards.yml` | Reward definitions for per-round and top-3 placements |
| `messages.yml` | All player-facing strings (supports `&` codes and `#RRGGBB` hex) |
| `plugin.yml` | Plugin metadata, permissions (`trivia.admin`, `trivia.player`) |

Config versioning is handled in `TriviaTreasure` — old configs are migrated on load (currently at v6).

## Permissions

- `trivia.admin` — OP-only, full control
- `trivia.player` — default true, play and view stats
