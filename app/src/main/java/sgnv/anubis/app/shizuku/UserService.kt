package sgnv.anubis.app.shizuku

import sgnv.anubis.app.IUserService

class UserService : IUserService.Stub() {

    override fun destroy() {
        // Called when the service is being destroyed
    }

    override fun execCommand(command: Array<String>): Int {
        val process = Runtime.getRuntime().exec(command)
        return process.waitFor()
    }

    override fun execCommandWithOutput(command: Array<String>): String {
        val process = Runtime.getRuntime().exec(command)
        val output = process.inputStream.bufferedReader().readText()
        val error = process.errorStream.bufferedReader().readText()
        process.waitFor()
        return if (error.isNotEmpty()) "ERROR:$error" else output
    }
}
