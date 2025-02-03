package com.atcumt.kxq.utils

object ValidationUtil {

    // Check if username is valid (6-16 characters, only letters, numbers, and underscores)
    fun isValidUsername(username: String): Boolean {
        return username.matches("^[a-zA-Z0-9_]{6,16}$".toRegex())
    }

    // Check if password is valid (8-20 characters, no spaces)
    fun isValidPassword(password: String): Boolean {
        return password.length in 8..20 && !password.contains(" ")
    }
}
