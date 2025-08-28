言語: 日本語 | [English](./README_en.md)

# Jackson-Databind-Jsonc

このプロジェクトは、Jackson の `JsonMapper` を拡張し、JSONC（コメント付きJSON）を扱える `JsoncMapper` を提供します。

## 特長

- JSONC 形式をサポート（ブロックコメント `/* */` と 行末コメント `//`）
- **新機能**: Builder パターンによるオプションのJSON5機能
  - シングルクォート文字列（`'text'` → `"text"`）
  - 16進数リテラル（`0xFF` → `255`）
  - プラス記号付き数値（`+123` → `123`）
  - 無限大やNaN（`Infinity`/`NaN` → `null`）
  - 複数行文字列とエスケープされていない制御文字
- Jackson の `JsonMapper` を拡張
- 複数の Java バージョンをサポート（Java 8, 11, 17, 21, 24）
- 利用シーンに応じた 2 つの配布形式を提供
- ReDoS攻撃に対する保護機能
- トレーリングカンマ除去機能（オプション）

## サポートされるコメント形式

### ✅ 完全サポート
- **ブロックコメント**: `/* コメント */` - 複数行対応
- **行末コメント**: `// コメント` - 行末まで
- **マルチラインコメント**: 改行を含むコメント
- **文字列内コメント保護**: JSON文字列内のコメントは保持

```javascript
{
    /* 設定ファイルのメインセクション */
    "database": {
        "host": "localhost", // デフォルトホスト
        "port": 5432,
        /* 複数行コメント
           データベース設定の
           詳細説明 */
        "timeout": 30
    },
    "message": "This /* is not removed */ from string" // 文字列内は保護
}
```

### ❌ 未対応機能
- **ネストコメント**: `/* 外側 /* 内側 */ 外側 */` - サポートされていません
- **JSON5のその他機能**: オブジェクトキーの引用符省略等

### 🔧 オプションのJSON5機能
すべてのJSON5機能はデフォルトで無効になっており、Builder パターンで個別に有効化できます：

#### トレーリングカンマ除去
```java
JsoncMapper mapper = new JsoncMapper.Builder()
    .allowTrailingCommas(true)
    .build();
```

#### シングルクォート文字列
シングルクォート文字列をダブルクォートJSON形式に変換：
```java
JsoncMapper mapper = new JsoncMapper.Builder()
    .allowSingleQuotes(true)
    .build();

// 入力: { 'key': 'value' }
// 出力: { "key": "value" }
```

#### 16進数リテラル
16進数リテラルを10進数形式に変換：
```java
JsoncMapper mapper = new JsoncMapper.Builder()
    .allowHexNumbers(true)
    .build();

// 入力: { "value": 0xFF }
// 出力: { "value": 255 }
```

#### プラス記号付き数値
正の数値から明示的なプラス記号を除去：
```java
JsoncMapper mapper = new JsoncMapper.Builder()
    .allowPlusNumbers(true)
    .build();

// 入力: { "value": +123 }
// 出力: { "value": 123 }
```

#### 無限大とNaN
JavaScript形式のInfinityとNaNをJSONのnullに変換：
```java
JsoncMapper mapper = new JsoncMapper.Builder()
    .allowInfinityAndNaN(true)
    .build();

// 入力: { "inf": Infinity, "nan": NaN }
// 出力: { "inf": null, "nan": null }
```

#### すべての機能を組み合わせ
複数のJSON5機能を同時に有効化：
```java
JsoncMapper mapper = new JsoncMapper.Builder()
    .allowTrailingCommas(true)
    .allowSingleQuotes(true)
    .allowHexNumbers(true)
    .allowPlusNumbers(true)
    .allowInfinityAndNaN(true)
    .allowMultilineStrings(true)
    .allowUnescapedControlChars(true)
    .build();

// 複雑なJSON5入力をパース
String json5 = """
{
    /* 設定 */
    'name': 'My App',        // シングルクォート
    "version": 0xFF,         // 16進数
    'port': +8080,           // プラス記号付き数値
    "maxValue": Infinity,    // 無限大
    'enabled': true,         // トレーリングカンマ
}
""";

MyConfig config = mapper.readValue(json5, MyConfig.class);
```

## 要件

### Java バージョン対応

本ライブラリは次の Java バージョンをサポートします。

- Java 8（LTS）: 最低要件
- Java 11（LTS）: フル対応
- Java 17（LTS）: フル対応
- Java 21（LTS）: フル対応
- Java 24: 利用可能（リリース後）

最大互換性のため、バイトコードは Java 8（major version 52）でコンパイルされ、すべてのサポート対象 Java でビルド・テストされます。

### テスト方針

- ビルドテスト: Gradle（Java 17+が必要）でビルド・テスト
- 実行時互換性: 生成された JAR は Java 8+ で動作確認
- バイトコード: すべてのクラスを Java 8 バイトコードでコンパイル

## 配布形式

さまざまな環境要件に対応するため、2 つの配布形式を提供します。

### Slim JAR（推奨）
- ファイル: `jackson-databind-jsonc-1.0.0.jar`（約 5KB）
- 用途: モダンな Jackson 環境、Maven/Gradle プロジェクト
- 依存関係: Jackson 2.18.4+ が必要
- 特徴: 軽量で依存関係管理が柔軟

### All-in-One JAR（エンタープライズ）
- ファイル: `jackson-databind-jsonc-1.0.0-all.jar`（約 7.8MB）
- 用途: エンタープライズ Java アプリ、レガシー環境、依存関係競合回避
- 依存関係: 自己完結（Jackson 同梱）
- 特徴: 単一ファイル配布、依存関係競合なし

## インストール

> **✅ Maven Central 公開状況**: このプロジェクトは Maven Central Portal を使用した自動公開に対応しています。最新バージョンは Maven Central から取得可能です。

### Maven（Slim JAR）
```xml
<dependency>
    <groupId>jp.vemi</groupId>
    <artifactId>jackson-databind-jsonc</artifactId>
    <version>1.0.5</version>
</dependency>
```

### Gradle（Slim JAR）
```groovy
implementation 'jp.vemi:jackson-databind-jsonc:1.0.5'
```

### 手動インストール
1. [Releases ページ](https://github.com/vemic/jackson-databind-jsonc/releases) から JAR をダウンロード
2. プロジェクトのクラスパスに追加

**📋 リリース情報:**
- 自動公開: Maven Central Portal 経由で自動化済み
- 手動取得: GitHub Releases から即座にダウンロード可能  
- 詳細情報: [PUBLISHING.md](./PUBLISHING.md) を参照

## どちらの JAR を使うべきか

### Slim JAR を選ぶ場合
- ✅ Jackson 2.18.x+ を利用可能
- ✅ 依存関係を Maven/Gradle で管理できる
- ✅ 他ライブラリとの競合がない
- ✅ JAR サイズを最小化したい

### All-in-One JAR を選ぶ場合
- ✅ エンタープライズ Java アプリケーション環境
- ✅ 既存の Jackson バージョンが固定されている
- ✅ 依存関係の競合を避けたい
- ✅ 単一 JAR 配布が必要
- ✅ 複雑な依存関係管理を避けたい

## 使い方

### 基本的な使用方法（両 JAR 共通）
```java
import jp.vemi.jsoncmapper.JsoncMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

public class Example {
    public static void main(String[] args) {
        // JSONC はブロックコメントと行末コメントの両方をサポート
        String jsonWithComments = """
            {
                /* Block comment */
                "name": "example", // End-of-line comment
                "value": 42
            }
            """;
        JsoncMapper mapper = new JsoncMapper();
        
        try {
            MyClass obj = mapper.readValue(jsonWithComments, MyClass.class);
            System.out.println(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
```

### Spring Boot 環境（Slim JAR）
```java
import jp.vemi.jsoncmapper.JsoncMapper;

@Component
public class ModernJsoncProcessor {
    private final JsoncMapper mapper = new JsoncMapper();
    
    public <T> T parseJsonc(String jsoncContent, Class<T> valueType) 
            throws JsonProcessingException {
        return mapper.readValue(jsoncContent, valueType);
    }
}
```

### エンタープライズ Java アプリケーション（All-in-One JAR）
```java
import jp.vemi.jsoncmapper.JsoncMapper;

public class IntraMartJsoncProcessor {
    private final JsoncMapper mapper = new JsoncMapper();
    
    public <T> T parseJsonc(String jsoncContent, Class<T> valueType) 
            throws JsonProcessingException {
        return mapper.readValue(jsoncContent, valueType);
    }
}
```

### ファイルからの読み込み
```java
import jp.vemi.jsoncmapper.JsoncMapper;
import java.io.File;

JsoncMapper mapper = new JsoncMapper();

// ファイルから直接読み込み
MyConfig config = mapper.readValue(new File("config.jsonc"), MyConfig.class);

// InputStreamから読み込み
try (InputStream is = new FileInputStream("config.jsonc")) {
    MyConfig config = mapper.readValue(is, MyConfig.class);
}
```

### TypeReferenceを使用した型指定
```java
import com.fasterxml.jackson.core.type.TypeReference;

String jsoncArray = """
    [
        /* ユーザーリスト */
        { "name": "Alice", "age": 30 }, // ユーザー1
        { "name": "Bob", "age": 25 }    // ユーザー2
    ]
    """;

List<User> users = mapper.readValue(jsoncArray, new TypeReference<List<User>>() {});
```

### Builder パターンによる設定
```java
// トレーリングカンマを許可する設定
JsoncMapper mapper = new JsoncMapper.Builder()
    .allowTrailingCommas(true)
    .build();

String jsoncWithTrailingCommas = """
    {
        "items": [
            "item1",
            "item2", // トレーリングカンマも除去
        ],
        "enabled": true, // オブジェクトでも除去
    }
    """;

MyClass obj = mapper.readValue(jsoncWithTrailingCommas, MyClass.class);
```

### JsonNodeを使用した柔軟な処理
```java
import com.fasterxml.jackson.databind.JsonNode;

String jsoncData = """
    {
        /* 動的な設定 */
        "settings": {
            "theme": "dark", // UIテーマ
            "notifications": true
        }
    }
    """;

JsonNode node = mapper.readTree(jsoncData);
String theme = node.get("settings").get("theme").asText(); // "dark"
```

## パフォーマンス考慮事項

### 通常のファイルサイズ（< 10MB）
- 高速な処理が可能
- メモリ使用量は元ファイルサイズの約1.5倍程度

### 大きなファイル（> 10MB）
- **推奨**: 事前にコメント除去してファイルサイズを削減
- **代替案**: ストリーミング処理の検討

```java
// 大きなファイルの場合の推奨パターン
public class LargeFileProcessor {
    public <T> T processLargeJsonc(File largeFile, Class<T> valueType) 
            throws IOException {
        
        // 1. ファイルサイズをチェック
        if (largeFile.length() > 10 * 1024 * 1024) { // 10MB
            // 2. 事前にコメントを除去してテンポラリファイルに保存
            String content = Files.readString(largeFile.toPath());
            String cleaned = JsoncUtils.removeComments(content);
            
            // 3. 標準のJacksonで処理
            return new JsonMapper().readValue(cleaned, valueType);
        }
        
        // 通常サイズならJsoncMapperで直接処理
        return new JsoncMapper().readValue(largeFile, valueType);
    }
}
```

### パフォーマンス最適化のヒント
- **Slim JAR**: 起動時間とメモリ使用量で有利
- **All-in-One JAR**: 依存関係解決時間を短縮
- **Builder設定**: 不要な機能は無効化

## セキュリティ機能

### ReDoS攻撃からの保護
正規表現を使用しない線形時間アルゴリズムにより、悪意のある入力に対する保護を実装しています。

```java
// 悪意のある入力でもタイムアウトしない
String maliciousInput = "/*" + "/*".repeat(10000) + "*/";
String result = JsoncUtils.removeComments(maliciousInput); // 安全に処理
```

### 入力検証
- **null入力**: `IllegalArgumentException` で適切にエラー処理
- **不正な形式**: `JsonProcessingException` で詳細なエラー情報を提供
- **文字列保護**: JSON文字列内のコメント風テキストは保持

### 信頼できないソースからの入力
```java
public class SecureProcessor {
    private final JsoncMapper mapper = new JsoncMapper();
    
    public <T> Optional<T> safeParseJsonc(String untrustedInput, Class<T> valueType) {
        try {
            // 入力検証は自動で実行される
            T result = mapper.readValue(untrustedInput, valueType);
            return Optional.of(result);
        } catch (JsonProcessingException | IllegalArgumentException e) {
            // ログ出力やモニタリング
            logger.warn("Failed to parse JSONC input: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
```

## 環境別の推奨

### モダンな Spring Boot
- 推奨: Slim JAR
- 理由: Spring Boot が最新の Jackson を管理

### 従来型 Web アプリケーションサーバ
- 推奨: All-in-One JAR
- 理由: サーバ側 Jackson との競合を回避

### エンタープライズ Java 環境
- 推奨: All-in-One JAR
- 理由: 固定バージョンの Jackson を利用するケースが多い

### マイクロサービス
- 推奨: Slim JAR
- 理由: コンテナサイズ最適化と依存性注入

### モダンな Spring Boot
- 推奨: Slim JAR
- 理由: Spring Boot が最新の Jackson を管理

### 従来型 Web アプリケーションサーバ
- 推奨: All-in-One JAR
- 理由: サーバ側 Jackson との競合を回避

### エンタープライズ Java 環境
- 推奨: All-in-One JAR
- 理由: 固定バージョンの Jackson を利用するケースが多い

### マイクロサービス
- 推奨: Slim JAR
- 理由: コンテナサイズ最適化と依存性注入

## 開発

### ビルドとテスト
本プロジェクトは Gradle を使用します。ビルドには Java 21 が必要です。

```bash
# Build the project
./gradlew build

# Run all tests
./gradlew test

# Run specific test classes
./gradlew test --tests "jp.vemi.jsoncmapper.JsoncUtilsTest"
./gradlew test --tests "jp.vemi.jsoncmapper.SecurityTest"
./gradlew test --tests "jp.vemi.jsoncmapper.JsoncMapperTest"
```

### GitHub ワークフロー

#### PR テスト実行
手動または PR イベントで実行できるテスト実行ワークフローを含みます。

【手動実行】
1. GitHub の Actions タブへ
2. "PR Test Execution" ワークフローを選択
3. "Run workflow" をクリック
4. オプションを設定:
   - Test Scope: `all`, `unit-only`, `security-only`, `integration-only`
   - Run Build: テスト前にビルドを実行するか
   - PR Number: テスト対象の PR を指定可能（任意）

【自動実行】
PR の opened/synchronize/ready_for_review で自動的に実行（フルスコープ）

【特徴】
- スコープ別の選択実行
- 詳細なテスト結果とサマリー
- PR への結果コメント
- Gradle のキャッシュによる高速化
- タイムアウト保護（最大 10 分）

## トラブルシューティング

### 依存関係の競合
**症状**: `NoSuchMethodError`, `ClassCastException` など

**解決方法**:
1. All-in-One JAR に切替
```xml
<!-- Maven の場合 -->
<dependency>
    <groupId>jp.vemi</groupId>
    <artifactId>jackson-databind-jsonc</artifactId>
    <version>1.0.0</version>
    <classifier>all</classifier>
</dependency>
```

2. 除外設定で Jackson の競合を解消
```xml
<dependency>
    <groupId>other-library</groupId>
    <artifactId>with-old-jackson</artifactId>
    <exclusions>
        <exclusion>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

### ClassNotFoundException
**症状**: `jp.vemi.jsoncmapper.JsoncMapper` が見つからない

**解決方法**:
1. **Slim JAR**: Jackson 2.18.4+ 依存を確認
2. **All-in-One JAR**: JAR が classpath にあるか確認
3. Maven/Gradle の依存関係を再取得: `./gradlew --refresh-dependencies`

### コメント解析エラー
**症状**: コメントが正しく除去されない、または例外が発生

**原因と解決**:
1. **ネストコメント**: サポートされていません
```java
// ❌ 動作しません
String invalid = "{ /* 外側 /* 内側 */ 外側 */ \"key\": \"value\" }";

// ✅ 代替案
String valid = "{ /* 外側コメント */ \"key\": \"value\" /* 内側コメント */ }";
```

2. **文字列内のエスケープ**: 適切にエスケープしてください
```java
// ✅ 正しい方法
String jsonc = "{ \"path\": \"C:\\\\\\\\/* not comment */\\\\\\\\file\" }";
```

### パフォーマンス問題
**症状**: 大きなファイルでメモリ不足やタイムアウト

**解決方法**:
1. **ファイルサイズ確認**: 10MB 以上の場合は事前処理を検討
```java
if (file.length() > 10_000_000) {
    // JsoncUtils で事前にコメントを除去
    String cleaned = JsoncUtils.removeComments(content);
    return new JsonMapper().readValue(cleaned, MyClass.class);
}
```

2. **JVM ヒープサイズ増加**: `-Xmx2g` など

### ビルド関連
**症状**: ビルドエラーや Gradle 問題

**解決方法**:
1. **Java バージョン確認**: ビルドには Java 21 が必要
```bash
java -version # Java 21 であることを確認
export JAVA_HOME=/path/to/java21
```

2. **Gradle キャッシュクリア**:
```bash
./gradlew clean build --refresh-dependencies
```

3. **テスト環境の確認**: 実行時は Java 8+ で動作
