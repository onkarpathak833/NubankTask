package com.example.nubank.domain

import com.google.gson.annotations.Expose

import scala.collection.mutable.ListBuffer

case class Account(ActiveCard: Boolean, AvailableLimit: Int) {
  var accountsList: List[Account] = List()

  def validateAndCreateAccount(ActiveCard: Boolean, AvailableLimit: Int): Boolean = {
    if (accountsList.size == 1) {
      true
    }
    else {
      accountsList.+:(Account(ActiveCard, AvailableLimit))
      false
    }
  }

}

case object Account {
  val ActiveCard : Boolean = false
  val AvailableLimit : Int = 0

  def create = {

  }
}


