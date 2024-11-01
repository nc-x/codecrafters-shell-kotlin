import kotlin.system.exitProcess

fun main() {
    while (true) {
        print("$ ")
        val cmd = readln()
        handleCommands(cmd)
    }
}

fun handleCommands(cmd: String) {
    val (cmd, args) = cmd.split("\\s+".toRegex()).let {
        it[0] to it.drop(1)
    }
    when (cmd) {
        "" -> {}
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
