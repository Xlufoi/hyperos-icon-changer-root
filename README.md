# BlueOS Translucent Glass Icons Port

Порт полупрозрачных стеклянных значков («стекло» / «прлзр») из BlueOS для HyperOS (Xiaomi / POCO) и любых Android-устройств.

## Особенности
* **Оригинальный стиль BlueOS:**
  - Полупрозрачная матовая стеклянная подложка (frosted glass) с динамическим градиентом и световым бликом на верхней кромке.
  - Минималистичные белые монохромные значки с мягкой подложкой для идеальной читаемости на любых светлых и тёмных обоях.
  - Автоматическая адаптация сторонних приложений на стеклянную подложку.
* **Работа на HyperOS БЕЗ приложения «Темы» (ThemeManager):**
  - Поддерживается прямая установка через Root в 1 тап.
  - Поддерживается модуль для Magisk / KernelSU / APatch.
* **Поддержка сторонних лаунчеров:**
  - Работает как стандартный Icon Pack (POCO Launcher, Nova Launcher, Lawnchair, Smart Launcher и др.).

## Способы установки на HyperOS

### Способ 1: Через приложение BlueOS Icons (Root / 1 тап)
1. Установите [BlueOS-Glass-Icons.apk](https://github.com/Xlufoi/BlueOS-icons-port/releases/download/v1.0.0/BlueOS-Glass-Icons.apk).
2. Откройте приложение и нажмите **«Применить в HyperOS (Root / 1 тап)»**.
3. Предоставьте Root-доступ — значки мгновенно применятся на рабочем столе без перезагрузки телефона!

### Способ 2: Через Magisk / KernelSU / APatch
1. Скачайте модуль [BlueOS-Glass-Icons-Module.zip](https://github.com/Xlufoi/BlueOS-icons-port/releases/download/v1.0.0/BlueOS-Glass-Icons-Module.zip).
2. Прошейте в менеджере модулей (Magisk / KernelSU / APatch) и перезагрузите устройство.

### Способ 3: Через терминал / скрипт (Root)
Выполните в Termux с root-правами:
```bash
su -c "/sdcard/Download/BlueOS_HyperOS/apply_blueos_icons.sh"
```
