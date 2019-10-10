package com.example.nubank.authorize

import java.util

import com.example.nubank.domain.{Account, TransactionResponse}
import com.google.gson.{FieldNamingPolicy, Gson, GsonBuilder}
import javafx.scene.input.KeyCombination.Modifier
import org.json.JSONObject

class TransactionProcessor {

  val gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).create()

  def processTransaction(userTransaction: String): String = {
    val transaction: JSONObject = new JSONObject(userTransaction)
    val keysIterator: util.Iterator[_] = transaction.keys()
    val transactionType = keysIterator.next().toString
    transactionType match {
      case "account" => {
        val accountObject: JSONObject = transaction.getJSONObject(transactionType)
        val account: Account = Account(accountObject.getBoolean("active-card"), accountObject.getInt("available-limit"))
        val response = validateAndCreateAccount(account)
        gson.toJson(response)
      }

      case "transaction" => {

        new JSONObject().toString()
      }

    }
  }

  private def validateAndCreateAccount(account: Account): TransactionResponse = {
    if (account.validateAndCreate()) {
      TransactionResponse(account, Array.empty[String])

    }
    else {
      TransactionResponse(account, Array("account-already-initialized"))
    }

  }

  private def authorize(transactionData: JSONObject): String = {


    ""
  }
}
