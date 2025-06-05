# BluetoothSCOApp

## Description
BluetoothSCOApp is an Android application that uses Bluetooth SCO (Synchronous Connection-Oriented) for audio and speech recognition. The app allows users to toggle Bluetooth SCO on and off, and it uses the device's microphone to transcribe speech in real-time.

## Setup and Usage
1. Clone the repository:
   ```bash
   git clone https://github.com/alienyang/BluetoothSCOApp.git
   ```
2. Open the project in Android Studio.
3. Build and run the app on an Android device with Bluetooth capabilities.
4. Grant the necessary permissions when prompted.
5. Use the "Start Bluetooth SCO" button to enable Bluetooth SCO and start speech recognition. The transcribed text will appear on the screen.

## Required Permissions
The app requires the following permissions, as listed in `app/src/main/AndroidManifest.xml`:
- `android.permission.BLUETOOTH`
- `android.permission.BLUETOOTH_ADMIN`
- `android.permission.BLUETOOTH_CONNECT`
- `android.permission.RECORD_AUDIO`
- `android.permission.MODIFY_AUDIO_SETTINGS`

## Architecture Diagram
```mermaid
graph TB
    %% External Systems
    subgraph "External Systems"
        BLE[智能眼鏡設備<br/>Bluetooth LE]
        OpenAI[OpenAI Realtime API<br/>WebSocket]
        Weather[Weather API<br/>HTTP]
    end

    %% UI Layer
    subgraph "UI Layer"
        Activity[QChatSampleActivity<br/>聊天界面]
    end

    %% Service Layer
    subgraph "Service Layer"
        BleService[BleClientService<br/>核心服務]
        QChatAction[QChatActionManager<br/>動作管理器]
    end

    %% AI Layer
    subgraph "AI Processing Layer"
        AiManager[AiManager<br/>AI管理器]
        RealtimeHelper[RealtimeHelper<br/>OpenAI實時助手]
        CommandOp[CommandOperator<br/>命令識別]
    end

    %% Speech Layer
    subgraph "Speech Processing Layer"
        SpeechOp[SpeechOperator<br/>語音操作器]
        RealtimeAudio[RealtimeAudio<br/>實時音頻處理]
        ScoRecorder[ScoRecorder<br/>藍牙音頻錄製]
    end

    %% Data Flow - Audio Input
    BLE -->|音頻數據| BleService
    BleService -->|啟動錄音| AiManager
    AiManager -->|控制| SpeechOp
    SpeechOp -->|音頻緩衝| RealtimeAudio
    RealtimeAudio -->|PCM數據| AiManager
    AiManager -->|音頻流| RealtimeHelper

    %% Data Flow - AI Processing
    RealtimeHelper <-->|WebSocket| OpenAI
    RealtimeHelper -->|轉錄文本| AiManager
    RealtimeHelper -->|AI響應音頻| AiManager
    AiManager -->|播放音頻| SpeechOp
    SpeechOp -->|音頻輸出| RealtimeAudio

    %% Data Flow - Command Processing
    AiManager -->|語音文本| CommandOp
    CommandOp -->|命令類型| AiManager
    AiManager -->|命令回調| BleService
    RealtimeHelper -->|功能調用| AiManager
    AiManager -->|天氣請求| Weather

    %% Data Flow - UI Updates
    AiManager -->|事件| Activity
    BleService -->|事件| Activity
    Activity -->|用戶操作| QChatAction
    QChatAction -->|控制流| BleService

    %% Data Flow - Device Communication
    BleService -->|BLE命令| BLE
    BleService -->|圖像分析| AiManager
    BLE -->|圖像數據| BleService
    BLE -->|通知數據| BleService

    %% Styling
    classDef external fill:#e1f5fe,stroke:#01579b,stroke-width:2px
    classDef ui fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
    classDef service fill:#e8f5e8,stroke:#1b5e20,stroke-width:2px
    classDef ai fill:#fff3e0,stroke:#e65100,stroke-width:2px
    classDef speech fill:#fce4ec,stroke:#880e4f,stroke-width:2px

    class BLE,OpenAI,Weather external
    class Activity ui
    class BleService,QChatAction service
    class AiManager,RealtimeHelper,CommandOp ai
    class SpeechOp,RealtimeAudio,ScoRecorder speech

```
