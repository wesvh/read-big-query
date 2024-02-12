package expo.modules.readbigquery

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import expo.modules.kotlin.Promise
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response // Importa Response
import java.io.IOException
import android.util.Log

class ReadBigQueryModule : Module() {
    private val client = OkHttpClient() // Define la instancia de OkHttpClient
   
    override fun definition() = ModuleDefinition {
        Name("ReadBigQuery")
        AsyncFunction("PetitionInKotlin") { url: String, promise: Promise ->
            executeWithRetry(url, promise)
        }
        AsyncFunction("readDictionary") { dataSerialized: String,  promise: Promise ->
            readDictionaryFun(dataSerialized, promise)
        }
    }
    private fun executeWithRetry(url: String, promise: Promise, currentRetry: Int = 0, maxRetries: Int = 3, initialBackoff: Long = 1000L) {
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
        override fun onFailure(call: okhttp3.Call, e: IOException) {
            if (currentRetry < maxRetries) {
                retryWithBackoff(url, promise, currentRetry, maxRetries, initialBackoff)
            } else {
                promise.reject("ERROR_HTTP_REQUEST", "Fallo al realizar la petición HTTP después de alcanzar el máximo de $maxRetries intentos", e)
            }
        }

        override fun onResponse(call: okhttp3.Call, response: Response) {
            if (!response.isSuccessful) {
                if (currentRetry < maxRetries) {
                    retryWithBackoff(url, promise, currentRetry, maxRetries, initialBackoff)
                } else {
                promise.reject("ERROR_HTTP_RESPONSE", "Respuesta HTTP no exitosa: ${response.code} después de alcanzar el máximo de $maxRetries intentos", null)
                }
            return
            }
            Log.d("ReadBigQueryModule", "Petición exitosa")
            Log.d("ReadBigQueryModule", "Código de respuesta: ${response}")
            val responseData = response.body?.string() ?: ""
            try {
                    val gson = Gson()
                    
                    // Define el tipo genérico para la respuesta
                    val responseType = object : TypeToken<ApiResponse<List<List<Any>>>>() {}.type
                    
                    // Deserializa el JSON al tipo genérico
                    val responseObj: ApiResponse<List<List<Any>>> = gson.fromJson(responseData, responseType)
                    
                    if (responseObj.ok) {
                        // Procesar la parte `data` como se desee
                        val dataSerialized = gson.toJson(responseObj.data)
                        readDictionaryFun(dataSerialized, promise)
                    } else {
                        promise.reject("ERROR_RESPONSE_NOT_OK", "La respuesta no fue exitosa.", null)
                    }
            } 
            catch (e: Exception) {
                    promise.reject("ERROR_PROCESSING_RESPONSE", "Error al procesar la respuesta.", e)
            }
        
        }}
        )
    }

    private fun retryWithBackoff(url: String, promise: Promise, currentRetry: Int, maxRetries: Int, initialBackoff: Long) {
        val nextRetry = currentRetry + 1
        val nextBackoff = (initialBackoff * Math.pow(1.2, nextRetry.toDouble())).toLong().coerceAtMost(10000) // No exceder 10 segundos
        Thread {
            Thread.sleep(nextBackoff) // Espera antes de reintentar
            executeWithRetry(url, promise, nextRetry, maxRetries, initialBackoff) // Asegúrate de incrementar currentRetry
        }.start()
    }

    private fun readDictionaryFun(dataSerialized : String, promise: Promise) {
            try {
                Log.d("ReadBigQueryModule", "Iniciando la conversión del diccionario")
                
                val gson = Gson()
                val type = object : TypeToken<List<List<Any>>>() {}.type
                val data: List<List<Any>> = gson.fromJson(dataSerialized, type)
                val dictionary = data[0] as List<String>
                val values = data.subList(1, data.size)

                Log.d("ReadBigQueryModule", "Diccionario: $dictionary")

                // ahora la idea es hacer un JSON con el diccionario y los valores, donde el diccionario sea el nombre de las llaves y los valores sean los valores de las llaves

                val result = mutableListOf<Map<String, Any>>()
                for (value in values) {
                    val map = mutableMapOf<String, Any>()
                    for (i in dictionary.indices) {
                        val key = dictionary[i]
                        val keyValue = value[i]

                        // Verifica si la clave actual es 'KitDetails' y si el arreglo tiene elementos
                        if (key == "KitDetails" && keyValue is List<*> && keyValue.isNotEmpty()) {
                            // Proceso de parseo para 'KitDetails'
                            val kitDetailsList = mutableListOf<Map<String, Any>>()
                            val kitDictionary = keyValue[0] as List<String> // Asume que el primer elemento contiene las claves
                            val kitValues = keyValue.subList(1, keyValue.size)
                            for (kitValue in kitValues) {
                                val kitMap = mutableMapOf<String, Any>()
                                if (kitValue is List<*>) { // Asegúrate de que kitValue es una lista antes de acceder a sus elementos.
                                    for (j in kitDictionary.indices) {
                                        kitMap[kitDictionary[j]] = kitValue[j] ?: "" // Usa el operador elvis para manejar nulls.
                                    }
                                }
                                kitDetailsList.add(kitMap)
                            }
                            map[key] = kitDetailsList
                        } else {
                            map[key] = keyValue
                        }
                    }
                    result.add(map)
                }

                // Supongamos que simplemente convertimos `data` de nuevo a un string JSON para el ejemplo.
                val resultString = gson.toJson(result)
                promise.resolve(resultString)
            } catch (e: Exception) {
                promise.reject("ERROR_READ_DICTIONARY", "Failed to read dictionary", e)
            }
    }
}

data class ApiResponse<T>(val ok: Boolean, val data: T)

