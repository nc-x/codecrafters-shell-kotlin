import Command.Echo
import Command.Exit
import Command.None
import Command.Type
import Command.Unknown
import kotlin.system.exitProcess

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
            if (builtin != null) {
                println("${args[0]} is a shell builtin")
            } else {
                println("${args[0]}: not found")
            }
        }

        is Unknown -> {
            println("${cmd.value}: command not found")
        }
    }
}

sealed class Command(val value: String) {
    object None : Command("")
    object Echo : Command("echo")
    object Exit : Command("exit")
    object Type : Command("type")
    class Unknown(value: String) : Command(value)

    companion object {
        val entries: List<Command> =
            Command::class.sealedSubclasses.mapNotNull { it.objectInstance }

        fun from(value: String): Command =
            entries.firstOrNull { it.value == value } ?: Unknown(value)
    }
}
