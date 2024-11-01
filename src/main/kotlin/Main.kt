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
        it[0] to it.drop(1)
    }
    when (cmd) {
        "" -> {}

        "echo" -> {
            println(input.substringAfter("echo "))
        }

        "exit" -> {
            if (args.size != 1 || args[0].toIntOrNull() == null) {
                println("invalid arguments")
                return
            }
            val status = args[0].toInt()
            exitProcess(status)
        }

        else -> {
            println("$cmd: command not found")
        }
    }
}
