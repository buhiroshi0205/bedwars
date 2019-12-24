package bedwars

import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import com.dummyc0m.pylon.command.AbstractCommand
import com.dummyc0m.pylon.command.MasterCommand

class OpenCommand: AbstractCommand("open", "bedwars.buy.open", true) {
    override fun execute(sender: CommandSender, args: Array<String>?) {
        if (sender is Player) {
            launchBuyMenu(Main.INSTANCE, sender)
        } else {
            sender.sendMessage("gotta be a player")
        }
    }
}

class HelpCommand: AbstractCommand("help", "bedwars.buy.help", false) {
    override fun execute(sender: CommandSender, args: Array<String>?) {
        sender.sendMessage("try /buy open")
    }

}

fun getMasterCommand(): CommandExecutor {
    val master = MasterCommand("master", "bedwars.buy", false)
    master.addSubCommand(OpenCommand())
    master.addSubCommand(HelpCommand())
    return master
}
