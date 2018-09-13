package org.orangy.snek

val dictionary = "qwertyuiopasdfghjklzxcvbnm"
fun randomName() = (0..5).joinToString("") { dictionary[random.nextInt(dictionary.length)].toString() }

