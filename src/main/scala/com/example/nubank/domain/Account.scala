package com.example.nubank.domain

case class Account(ActiveCard: Boolean, AvailableLimit: Int) {
  def updateAccountLimit(accountBalance: Int): Account = {
    Account.accountsList = List(Account(ActiveCard, accountBalance))
    Account(ActiveCard, accountBalance)
  }


  def apply(ActiveCard: Boolean, AvailableLimit: Int) = {
    Account.availableLimit = AvailableLimit
  }

  def validateAndCreateAccount(): Boolean = {
    if (Account.accountsList.size == 1) {
      true
    }
    else {
      Account.accountsList = Account.accountsList.+:(Account(ActiveCard, AvailableLimit))
      Account.availableLimit = AvailableLimit
      false
    }
  }

}

object Account {
  var accountsList: List[Account] = List()
  var availableLimit: Int = 0
}


