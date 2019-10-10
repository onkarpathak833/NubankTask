package com.example.nubank.domain

import com.google.gson.annotations.Expose
import com.google.gson.{FieldNamingPolicy, GsonBuilder}
import org.json.JSONObject

case class TransactionResponse(account: Account, violations: Array[String])
