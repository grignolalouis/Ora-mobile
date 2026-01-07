package com.ora.app.presentation.designsystem.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString

// Syntax highlighting colors for Gruvbox theme
object SyntaxColors {
    // Dark theme (Gruvbox dark)
    object Dark {
        val keyword = Color(0xFFFB4934)      // Red
        val string = Color(0xFFB8BB26)       // Green
        val number = Color(0xFFD3869B)       // Purple
        val comment = Color(0xFF928374)      // Gray
        val function = Color(0xFF8EC07C)     // Aqua
        val type = Color(0xFFFABD2F)         // Yellow
        val operator = Color(0xFFFE8019)     // Orange
        val variable = Color(0xFF83A598)     // Blue
        val property = Color(0xFFD3869B)     // Purple
        val punctuation = Color(0xFFA89984)  // Gray
        val text = Color(0xFFEBDBB2)         // Light
    }

    // Light theme
    object Light {
        val keyword = Color(0xFFD32F2F)      // Red
        val string = Color(0xFF558B2F)       // Green
        val number = Color(0xFF7B1FA2)       // Purple
        val comment = Color(0xFF757575)      // Gray
        val function = Color(0xFF00796B)     // Teal
        val type = Color(0xFFF57C00)         // Orange
        val operator = Color(0xFFE65100)     // Deep Orange
        val variable = Color(0xFF1976D2)     // Blue
        val property = Color(0xFF7B1FA2)     // Purple
        val punctuation = Color(0xFF616161)  // Gray
        val text = Color(0xFF1A1A18)         // Dark
    }
}

// Simple regex-based syntax highlighter (doesn't require Prism4j annotation processing)
fun highlightCode(code: String, language: String?, isDark: Boolean): AnnotatedString {
    // Safety check for empty code
    if (code.isEmpty()) {
        return AnnotatedString("")
    }

    val baseTextColor = if (isDark) SyntaxColors.Dark.text else SyntaxColors.Light.text

    return try {
        buildAnnotatedString {
            append(code)

            // Apply base text color
            addStyle(SpanStyle(color = baseTextColor), 0, code.length)

            // Language-specific highlighting patterns
            val patterns = getLanguagePatterns(language)

            patterns.forEach { (pattern, colorType) ->
                val color = if (isDark) {
                    when (colorType) {
                        TokenType.KEYWORD -> SyntaxColors.Dark.keyword
                        TokenType.STRING -> SyntaxColors.Dark.string
                        TokenType.NUMBER -> SyntaxColors.Dark.number
                        TokenType.COMMENT -> SyntaxColors.Dark.comment
                        TokenType.FUNCTION -> SyntaxColors.Dark.function
                        TokenType.TYPE -> SyntaxColors.Dark.type
                        TokenType.OPERATOR -> SyntaxColors.Dark.operator
                        TokenType.VARIABLE -> SyntaxColors.Dark.variable
                        TokenType.PROPERTY -> SyntaxColors.Dark.property
                        TokenType.PUNCTUATION -> SyntaxColors.Dark.punctuation
                    }
                } else {
                    when (colorType) {
                        TokenType.KEYWORD -> SyntaxColors.Light.keyword
                        TokenType.STRING -> SyntaxColors.Light.string
                        TokenType.NUMBER -> SyntaxColors.Light.number
                        TokenType.COMMENT -> SyntaxColors.Light.comment
                        TokenType.FUNCTION -> SyntaxColors.Light.function
                        TokenType.TYPE -> SyntaxColors.Light.type
                        TokenType.OPERATOR -> SyntaxColors.Light.operator
                        TokenType.VARIABLE -> SyntaxColors.Light.variable
                        TokenType.PROPERTY -> SyntaxColors.Light.property
                        TokenType.PUNCTUATION -> SyntaxColors.Light.punctuation
                    }
                }

                try {
                    pattern.findAll(code).forEach { match ->
                        val start = match.range.first
                        val end = match.range.last + 1
                        // Safety check: ensure ranges are valid
                        if (start >= 0 && end <= code.length && start < end) {
                            addStyle(SpanStyle(color = color), start, end)
                        }
                    }
                } catch (_: Exception) {
                    // Skip this pattern if it causes issues
                }
            }
        }
    } catch (_: Exception) {
        // Fallback: return plain text with base color
        buildAnnotatedString {
            append(code)
            addStyle(SpanStyle(color = baseTextColor), 0, code.length)
        }
    }
}

private enum class TokenType {
    KEYWORD, STRING, NUMBER, COMMENT, FUNCTION, TYPE, OPERATOR, VARIABLE, PROPERTY, PUNCTUATION
}

private fun getLanguagePatterns(language: String?): List<Pair<Regex, TokenType>> {
    val lang = language?.lowercase() ?: ""

    // Common patterns
    val stringDouble = Regex("\"[^\"\\\\]*(\\\\.[^\"\\\\]*)*\"")
    val stringSingle = Regex("'[^'\\\\]*(\\\\.[^'\\\\]*)*'")
    val stringTemplate = Regex("`[^`]*`")
    val number = Regex("\\b\\d+(\\.\\d+)?\\b")
    val singleLineComment = Regex("//.*")
    val hashComment = Regex("#.*")

    return when {
        lang in listOf("kotlin", "kt") -> listOf(
            Regex("//.*") to TokenType.COMMENT,
            Regex("/\\*[\\s\\S]*?\\*/") to TokenType.COMMENT,
            stringDouble to TokenType.STRING,
            stringTemplate to TokenType.STRING,
            number to TokenType.NUMBER,
            Regex("\\b(fun|val|var|class|object|interface|enum|sealed|data|open|abstract|override|private|public|protected|internal|suspend|inline|infix|operator|companion|init|constructor|return|if|else|when|for|while|do|try|catch|finally|throw|import|package|is|as|in|out|where|typealias|lateinit|by|get|set|annotation|actual|expect)\\b") to TokenType.KEYWORD,
            Regex("\\b(String|Int|Long|Float|Double|Boolean|Char|Unit|Any|Nothing|List|Map|Set|Array|Pair|Triple)\\b") to TokenType.TYPE,
            Regex("\\b[A-Z][a-zA-Z0-9]*\\b") to TokenType.TYPE,
            Regex("\\b[a-z][a-zA-Z0-9]*(?=\\s*\\()") to TokenType.FUNCTION,
            Regex("[+\\-*/%=<>!&|^~?:]") to TokenType.OPERATOR,
        )

        lang in listOf("java") -> listOf(
            singleLineComment to TokenType.COMMENT,
            Regex("/\\*[\\s\\S]*?\\*/") to TokenType.COMMENT,
            stringDouble to TokenType.STRING,
            number to TokenType.NUMBER,
            Regex("\\b(public|private|protected|static|final|abstract|class|interface|extends|implements|new|return|if|else|switch|case|default|for|while|do|try|catch|finally|throw|throws|import|package|void|null|true|false|this|super|instanceof|synchronized|volatile|transient|native|strictfp|assert|enum|break|continue)\\b") to TokenType.KEYWORD,
            Regex("\\b(String|int|long|float|double|boolean|char|byte|short|void|Integer|Long|Float|Double|Boolean|Character|Byte|Short|Object|List|Map|Set|Array)\\b") to TokenType.TYPE,
            Regex("\\b[A-Z][a-zA-Z0-9]*\\b") to TokenType.TYPE,
            Regex("\\b[a-z][a-zA-Z0-9]*(?=\\s*\\()") to TokenType.FUNCTION,
            Regex("@[A-Za-z]+") to TokenType.PROPERTY,
        )

        lang in listOf("python", "py") -> listOf(
            hashComment to TokenType.COMMENT,
            Regex("\"\"\"[\\s\\S]*?\"\"\"") to TokenType.STRING,
            Regex("'''[\\s\\S]*?'''") to TokenType.STRING,
            stringDouble to TokenType.STRING,
            stringSingle to TokenType.STRING,
            number to TokenType.NUMBER,
            Regex("\\b(def|class|if|elif|else|for|while|try|except|finally|with|as|import|from|return|yield|raise|pass|break|continue|lambda|and|or|not|in|is|True|False|None|self|async|await|global|nonlocal)\\b") to TokenType.KEYWORD,
            Regex("\\b[A-Z][a-zA-Z0-9]*\\b") to TokenType.TYPE,
            Regex("\\b[a-z_][a-zA-Z0-9_]*(?=\\s*\\()") to TokenType.FUNCTION,
            Regex("@[a-zA-Z_][a-zA-Z0-9_]*") to TokenType.PROPERTY,
        )

        lang in listOf("javascript", "js", "typescript", "ts") -> listOf(
            singleLineComment to TokenType.COMMENT,
            Regex("/\\*[\\s\\S]*?\\*/") to TokenType.COMMENT,
            stringDouble to TokenType.STRING,
            stringSingle to TokenType.STRING,
            stringTemplate to TokenType.STRING,
            number to TokenType.NUMBER,
            Regex("\\b(const|let|var|function|class|extends|return|if|else|switch|case|default|for|while|do|try|catch|finally|throw|import|export|from|as|new|delete|typeof|instanceof|void|null|undefined|true|false|this|super|async|await|yield|break|continue|interface|type|enum|implements|private|public|protected|static|readonly|abstract|declare|namespace|module)\\b") to TokenType.KEYWORD,
            Regex("\\b[A-Z][a-zA-Z0-9]*\\b") to TokenType.TYPE,
            Regex("\\b[a-z_$][a-zA-Z0-9_$]*(?=\\s*\\()") to TokenType.FUNCTION,
            Regex("=>" ) to TokenType.OPERATOR,
        )

        lang in listOf("json") -> listOf(
            stringDouble to TokenType.STRING,
            number to TokenType.NUMBER,
            Regex("\\b(true|false|null)\\b") to TokenType.KEYWORD,
            Regex("\"[^\"]+\"(?=\\s*:)") to TokenType.PROPERTY,
        )

        lang in listOf("html", "xml") -> listOf(
            Regex("<!--[\\s\\S]*?-->") to TokenType.COMMENT,
            Regex("</?[a-zA-Z][a-zA-Z0-9-]*") to TokenType.KEYWORD,
            Regex("/>|>") to TokenType.KEYWORD,
            stringDouble to TokenType.STRING,
            stringSingle to TokenType.STRING,
            Regex("\\b[a-zA-Z-]+(?==)") to TokenType.PROPERTY,
        )

        lang in listOf("css", "scss", "sass") -> listOf(
            Regex("/\\*[\\s\\S]*?\\*/") to TokenType.COMMENT,
            singleLineComment to TokenType.COMMENT,
            stringDouble to TokenType.STRING,
            stringSingle to TokenType.STRING,
            number to TokenType.NUMBER,
            Regex("#[a-fA-F0-9]{3,8}\\b") to TokenType.NUMBER,
            Regex("\\.[a-zA-Z_-][a-zA-Z0-9_-]*") to TokenType.TYPE,
            Regex("#[a-zA-Z_-][a-zA-Z0-9_-]*") to TokenType.VARIABLE,
            Regex("@[a-zA-Z-]+") to TokenType.KEYWORD,
            Regex("\\b[a-zA-Z-]+(?=\\s*:)") to TokenType.PROPERTY,
        )

        lang in listOf("bash", "sh", "shell", "zsh") -> listOf(
            hashComment to TokenType.COMMENT,
            stringDouble to TokenType.STRING,
            stringSingle to TokenType.STRING,
            number to TokenType.NUMBER,
            Regex("\\$[a-zA-Z_][a-zA-Z0-9_]*") to TokenType.VARIABLE,
            Regex("\\$\\{[^}]+}") to TokenType.VARIABLE,
            Regex("\\b(if|then|else|elif|fi|for|while|do|done|case|esac|function|return|exit|export|source|alias|unset|readonly|local|declare|typeset|shift|break|continue|in|select|until)\\b") to TokenType.KEYWORD,
        )

        lang in listOf("sql") -> listOf(
            Regex("--.*") to TokenType.COMMENT,
            Regex("/\\*[\\s\\S]*?\\*/") to TokenType.COMMENT,
            stringDouble to TokenType.STRING,
            stringSingle to TokenType.STRING,
            number to TokenType.NUMBER,
            Regex("\\b(SELECT|FROM|WHERE|AND|OR|NOT|IN|LIKE|BETWEEN|JOIN|LEFT|RIGHT|INNER|OUTER|ON|AS|ORDER|BY|GROUP|HAVING|LIMIT|OFFSET|INSERT|INTO|VALUES|UPDATE|SET|DELETE|CREATE|TABLE|INDEX|VIEW|DROP|ALTER|ADD|COLUMN|PRIMARY|KEY|FOREIGN|REFERENCES|UNIQUE|NULL|DEFAULT|CONSTRAINT|CASCADE|DISTINCT|COUNT|SUM|AVG|MAX|MIN|UNION|ALL|EXISTS|CASE|WHEN|THEN|ELSE|END|IS|TRUE|FALSE)\\b", RegexOption.IGNORE_CASE) to TokenType.KEYWORD,
        )

        lang in listOf("go", "golang") -> listOf(
            singleLineComment to TokenType.COMMENT,
            Regex("/\\*[\\s\\S]*?\\*/") to TokenType.COMMENT,
            stringDouble to TokenType.STRING,
            stringTemplate to TokenType.STRING,
            number to TokenType.NUMBER,
            Regex("\\b(break|case|chan|const|continue|default|defer|else|fallthrough|for|func|go|goto|if|import|interface|map|package|range|return|select|struct|switch|type|var)\\b") to TokenType.KEYWORD,
            Regex("\\b(string|int|int8|int16|int32|int64|uint|uint8|uint16|uint32|uint64|float32|float64|complex64|complex128|bool|byte|rune|error|nil|true|false|iota)\\b") to TokenType.TYPE,
            Regex("\\b[A-Z][a-zA-Z0-9]*\\b") to TokenType.TYPE,
            Regex("\\b[a-z][a-zA-Z0-9]*(?=\\s*\\()") to TokenType.FUNCTION,
        )

        lang in listOf("rust", "rs") -> listOf(
            singleLineComment to TokenType.COMMENT,
            Regex("/\\*[\\s\\S]*?\\*/") to TokenType.COMMENT,
            stringDouble to TokenType.STRING,
            number to TokenType.NUMBER,
            Regex("\\b(as|async|await|break|const|continue|crate|dyn|else|enum|extern|false|fn|for|if|impl|in|let|loop|match|mod|move|mut|pub|ref|return|self|Self|static|struct|super|trait|true|type|unsafe|use|where|while)\\b") to TokenType.KEYWORD,
            Regex("\\b(i8|i16|i32|i64|i128|isize|u8|u16|u32|u64|u128|usize|f32|f64|bool|char|str|String|Vec|Option|Result|Box|Rc|Arc|Some|None|Ok|Err)\\b") to TokenType.TYPE,
            Regex("\\b[A-Z][a-zA-Z0-9]*\\b") to TokenType.TYPE,
            Regex("\\b[a-z_][a-zA-Z0-9_]*(?=\\s*\\()") to TokenType.FUNCTION,
            Regex("#\\[[^\\]]+\\]") to TokenType.PROPERTY,
        )

        lang in listOf("swift") -> listOf(
            singleLineComment to TokenType.COMMENT,
            Regex("/\\*[\\s\\S]*?\\*/") to TokenType.COMMENT,
            stringDouble to TokenType.STRING,
            number to TokenType.NUMBER,
            Regex("\\b(class|struct|enum|protocol|extension|func|var|let|if|else|guard|switch|case|default|for|while|repeat|do|try|catch|throw|throws|rethrows|return|break|continue|fallthrough|in|where|as|is|nil|true|false|self|Self|super|init|deinit|get|set|willSet|didSet|subscript|static|override|final|lazy|weak|unowned|private|fileprivate|internal|public|open|import|typealias|associatedtype|inout|mutating|nonmutating|convenience|required|optional|async|await)\\b") to TokenType.KEYWORD,
            Regex("\\b(String|Int|Double|Float|Bool|Character|Array|Dictionary|Set|Optional|Any|AnyObject|Void|Never)\\b") to TokenType.TYPE,
            Regex("\\b[A-Z][a-zA-Z0-9]*\\b") to TokenType.TYPE,
            Regex("\\b[a-z_][a-zA-Z0-9_]*(?=\\s*\\()") to TokenType.FUNCTION,
            Regex("@[a-zA-Z]+") to TokenType.PROPERTY,
        )

        // Default - basic highlighting
        else -> listOf(
            singleLineComment to TokenType.COMMENT,
            hashComment to TokenType.COMMENT,
            Regex("/\\*[\\s\\S]*?\\*/") to TokenType.COMMENT,
            stringDouble to TokenType.STRING,
            stringSingle to TokenType.STRING,
            stringTemplate to TokenType.STRING,
            number to TokenType.NUMBER,
        )
    }
}
