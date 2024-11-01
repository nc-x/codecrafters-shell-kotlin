import Command.Cd
import Command.Echo
import Command.Exit
import Command.None
import Command.Pwd
import Command.Type
import Command.Unknown
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.system.exitProcess

var workingDirectory = "."

fun main() {
    while (true) {
        print("$ ")
        val cmd = readln()
        handleCommands(cmd)
    }
}

fun handleCommands(input: String) {
    val (cmd, args) = input.split("\\s+".toRegex()).let {
        Command.from(it[0]) to it.drop(1)
    }


    when (cmd) {
        None -> {}

        Echo -> {
            println(input.substringAfter("${cmd.value} "))
        }

        Exit -> {
            if (args.size != 1 || args[0].toIntOrNull() == null) {
                println("invalid arguments")
                return
            }
            val status = args[0].toInt()
            exitProcess(status)
        }

        Type -> {
            if (args.size != 1) {
                println("invalid arguments")
                return
            }
            val builtin = Command.entries.firstOrNull { it.value == args[0] }
            val location = findInPath(args[0])
            val message = when {
                builtin != null -> "${args[0]} is a shell builtin"
                location != null -> "${args[0]} is $location"
                else -> "${args[0]}: not found"
            }
            println(message)
        }

        Pwd -> {
            if (args.isNotEmpty()) {
                println("unexpected arguments.")
                return
            }
            val pwd = File(workingDirectory).canonicalPath
            println(pwd)

        }

        Cd -> {
            if (args.size != 1) {
                println("invalid arguments")
                return
            }
            val path = Path(args[0]).let { if (it.isAbsolute) it else Path("$workingDirectory/${args[0]}") }
            if (path.exists() && path.isDirectory()) {
                workingDirectory = path.normalize().absolutePathString()
            } else {
                println("cd: ${args[0]}: No such file or directory")
            }
        }

        is Unknown -> {
            val location = findInPath(cmd.value)
            if (location != null) {
                val process = ProcessBuilder(listOf(location, *args.toTypedArray())).start()
                process.inputStream.bufferedReader().use { reader ->
                    for (line in reader.lines()) {
                        println(line)
                    }
                }
                process.waitFor()
            } else {
                println("${cmd.value}: command not found")
            }
        }
    }
}

fun findInPath(executable: String): String? {
    val paths = System.getenv("PATH")?.split(":") ?: listOf()
    for (path in paths) {
        val path = File(path)
        val found = path.listFiles()?.find { it.name == executable }
        if (found != null) return found.path
    }
    return null
}

sealed class Command(val value: String) {
    object None : Command("")
    object Echo : Command("echo")
    object Exit : Command("exit")
    object Type : Command("type")
    object Pwd : Command("pwd")
    object Cd : Command("cd")
    class Unknown(value: String) : Command(value)

    companion object {
        val entries: List<Command> =
            Command::class.sealedSubclasses.mapNotNull { it.objectInstance }

        fun from(value: String): Command =
            entries.firstOrNull { it.value == value } ?: Unknown(value)
    }
}
