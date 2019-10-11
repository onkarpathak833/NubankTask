package com.example.nubank.authorize.rules

import java.time.{Duration, Instant, Period}
import java.time.temporal.ChronoUnit

import com.example.nubank.constants.TransactionConstants._
import com.example.nubank.domain.{Account, Transaction, TransactionResponse}

case class TransactionRules(transaction: Transaction, account: Option[Account]) {

  def run = {
    List(unInitializedAccountRule, cardInactiveRule,
      transactionExceedsAvailableLimitRule,
      noHighFrequencyTransactionsRule, noDoubledTransactions)
      .filter(result => !result._1)
  }

  private def unInitializedAccountRule: (Boolean, TransactionResponse) = {
    if (Account.accountsList.isEmpty) {
      (false, TransactionResponse(account, List(ACCOUNT_NOT_INITIALIZED)))
    }
    else {
      (true, TransactionResponse(account, List()))
    }
  }

  private def cardInactiveRule = {
    if (!account.exists(_.ActiveCard)) {
      (false, TransactionResponse(account, List(CARD_NOT_ACTIVE)))
    }
    else {
      (true, TransactionResponse(account, List()))
    }
  }

  private def transactionExceedsAvailableLimitRule = {
    if (transaction.amount > Account.availableLimit) {
      (false, TransactionResponse(account, List(INSUFFICIENT_LIMIT)))
    }
    else {
      (true, TransactionResponse(account, List()))
    }
  }

  private def noHighFrequencyTransactionsRule = {
    if (Transaction.transactions.size >= 3) {
      val transactionList = Transaction.transactions.toList
      val currentTransactionTime: Instant = Instant.parse(transaction.time)
      val thirdLastTransaction: Instant = Instant.parse(Transaction.transactions.toList(transactionList.size - 3).time)
      val timeDifferenceInSeconds: Long = Math.abs(Duration.between(currentTransactionTime, thirdLastTransaction).getSeconds)
      if (timeDifferenceInSeconds <= 120) {
        (false, TransactionResponse(account, List(HIGH_FREQUENCY_SMALL_INTERVAL)))
      }
      else {
        (true, TransactionResponse(account, List()))
      }
    }
    else {
      (true, TransactionResponse(account, List()))
    }
  }

  private def noDoubledTransactions = {
    if (Transaction.transactions.size >= 1) {
      val transactionList = Transaction.transactions.toList
      val currentTransactionTime = Instant.parse(transaction.time)

      val doubledTransactions = transactionList.
        filter(thisTransaction => {
          val timeInterval = Math.abs(Duration.between(currentTransactionTime, Instant.parse(thisTransaction.time)).getSeconds)
          timeInterval <= 120 &&
            thisTransaction.merchant.equals(transaction.merchant) &&
            thisTransaction.amount.equals(transaction.amount)
        }

        )

      if (doubledTransactions.isEmpty) {
        (true, TransactionResponse(account, List()))
      }
      else {
        (false, TransactionResponse(account, List(DOUBLED_TRANSACTION)))
      }
    }
    else {
      (true, TransactionResponse(account, List()))
    }
  }

}
