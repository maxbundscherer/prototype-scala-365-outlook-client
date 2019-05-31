package de.maxbundscherer.prototype.outlookclient.utils

abstract class Logger(name: String) {

  def log(message: String): Unit = println(s"[$name]\t$message")

}