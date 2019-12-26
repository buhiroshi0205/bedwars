package bedwars.util

/**
 * Created by DEDZTBH on 2019-02-13.
 * Project DEMagica
 */


infix fun <T> Boolean.then(block: () -> T): T? = if (this) block() else null