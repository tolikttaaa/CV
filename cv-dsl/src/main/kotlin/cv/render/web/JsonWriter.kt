package cv.render.web

/**
 * A minimal JSON document tree with a pretty-printing writer.
 *
 * Kept dependency-free on purpose: the generator emits one small, fixed-schema
 * file, which does not justify pulling in a serialization library.
 */
internal sealed interface Json {
    data class Str(val value: String) : Json
    data class Arr(val items: List<Json>) : Json
    data class Obj(val fields: List<Pair<String, Json>>) : Json
}

internal fun obj(vararg fields: Pair<String, Json>) = Json.Obj(fields.toList())
internal fun arr(items: List<Json>) = Json.Arr(items)
internal fun str(value: String) = Json.Str(value)

/** Serializes a [Json] tree to two-space-indented JSON text. */
internal object JsonWriter {

    fun write(root: Json): String {
        val sb = StringBuilder()
        write(root, sb, 0)
        sb.append('\n')
        return sb.toString()
    }

    private fun write(node: Json, sb: StringBuilder, indent: Int) {
        val pad = "  ".repeat(indent)
        val childPad = "  ".repeat(indent + 1)
        when (node) {
            is Json.Str -> sb.append('"').append(escape(node.value)).append('"')
            is Json.Arr ->
                if (node.items.isEmpty()) sb.append("[]")
                else {
                    sb.append("[\n")
                    node.items.forEachIndexed { i, item ->
                        sb.append(childPad)
                        write(item, sb, indent + 1)
                        sb.append(if (i < node.items.lastIndex) ",\n" else "\n")
                    }
                    sb.append(pad).append(']')
                }
            is Json.Obj ->
                if (node.fields.isEmpty()) sb.append("{}")
                else {
                    sb.append("{\n")
                    node.fields.forEachIndexed { i, (key, value) ->
                        sb.append(childPad).append('"').append(escape(key)).append("\": ")
                        write(value, sb, indent + 1)
                        sb.append(if (i < node.fields.lastIndex) ",\n" else "\n")
                    }
                    sb.append(pad).append('}')
                }
        }
    }

    private fun escape(s: String): String = buildString {
        for (c in s) when (c) {
            '"' -> append("\\\"")
            '\\' -> append("\\\\")
            '\n' -> append("\\n")
            '\r' -> append("\\r")
            '\t' -> append("\\t")
            else -> if (c < ' ') append("\\u%04x".format(c.code)) else append(c)
        }
    }
}
